<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.busbooking.User" %>
<%@ page import="com.busbooking.Constants" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Booking History</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <style>
        body {
            font-family: 'Roboto', sans-serif;
            background-color: #f4f4f9;
            margin: 0;
            padding: 0;
        }
        .navbar {
            background-color: #e53935;
            padding: 1rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            color: white;
        }
        .navbar h1 {
            margin: 0;
            cursor: pointer;
        }
        .navbar .nav-links {
            display: flex;
            gap: 1rem;
        }
        .navbar .nav-links a {
            color: white;
            text-decoration: none;
            cursor: pointer;
        }
        .container {
            max-width: 1200px;
            margin: 2rem auto;
            padding: 1rem;
            background-color: #ffffff;
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
        .section {
            margin-bottom: 2rem;
        }
        .section h2 {
            color: #333;
            margin-bottom: 1rem;
            font-size: 1.5rem;
        }
        .booking-card {
            position: relative;
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 1rem;
            margin-bottom: 1rem;
            background-color: #fafafa;
            cursor: pointer;
            transition: background-color 0.3s, box-shadow 0.3s;
        }
        .booking-card:hover {
            background-color: #f0f0f0;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }
        .booking-details {
            display: flex;
            justify-content: space-between;
            flex-direction: column;
        }
        .booking-details p {
            margin: 0.5rem 0;
            font-size: 1.1rem;
            font-weight: 500;
            color: #333;
        }
        .passenger-details {
            display: none;
            margin-top: 0.5rem;
            padding-top: 0.5rem;
            border-top: 1px solid #ddd;
        }
        .passenger-details h3 {
            margin-bottom: 0.5rem;
            font-size: 1.2rem;
            color: #333;
        }
        .passenger-details table {
            width: 100%;
            border-collapse: collapse;
            font-size: 0.9rem;
        }
        .passenger-details th, .passenger-details td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
            font-weight: normal;
        }
        .passenger-details th {
            background-color: #f2f2f2;
            font-weight: 700;
        }
        .passenger-details tr:nth-child(even) {
            background-color: #f9f9f9;
        }
    </style>
</head>
<body>
    <div class="navbar">
        <h1 id="home-link">BusBooking</h1>
        <div class="nav-links">
            <a id="home-link-nav">Home</a>
            <a id="signout-link">Sign Out</a>
        </div>
    </div>

    <div class="container">
        <div class="section">
            <h2>Booking History</h2>
            <div id="historyMessage"></div>
            <div id="history-results"></div>
        </div>
    </div>

    <script>
        $(document).ready(function() {
            function redirectToHomePage() {
                const userType = '<%= session.getAttribute("userType") %>';
                const isAdmin = userType === '<%= Constants.ADMIN %>';
                window.location.href = '<%= request.getContextPath() %>/' + (isAdmin ? 'admin/home.jsp' : 'customer/home.jsp');
            }

            $('#home-link, #home-link-nav').click(function() {
                redirectToHomePage();
            });

            $('#signout-link').click(function(event) {
                event.preventDefault();
                $.ajax({
                    type: 'GET',
                    url: '<%= request.getContextPath() %>/signout',
                    success: function(response) {
                        if (response.response_status.status === "success") {
                            window.location.href = '<%= request.getContextPath() %>/publicpages/login.jsp';
                        } else {
                            alert('Sign out failed: ' + response.response_status.message);
                        }
                    },
                    error: function(error) {
                        alert('An error occurred during sign out.');
                        console.error('Error during sign out:', error);
                    }
                });
            });

            $.ajax({
                type: 'GET',
                url: '<%= request.getContextPath() %>/fetchBookingHistory',
                dataType: 'json',
                success: function(response) {
                    if (response.response_status.status === "success") {
                        displayHistory(response.data);
                    } else {
                        $('#historyMessage').css('color', 'red').html(response.response_status.message);
                    }
                },
                error: function(error) {
                    $('#historyMessage').css('color', 'red').html('An error occurred while fetching booking history.');
                    console.error('Error fetching booking history:', error);
                }
            });

            function displayHistory(history) {
                let html = '';
                history.forEach(function(booking) {
                    const isCanceled = booking.statusId == '<%= Constants.CANCEL %>';
                    html += '<div class="booking-card" data-ticket-number="' + booking.ticketNumber + '" style="' + (isCanceled ? 'background-color: #ffe0e0;' : '') + '">';
                    if (isCanceled) {
                        html += '<div style="position: absolute; top: 10px; right: 10px; color: red; font-weight: bold;">Cancelled</div>';
                    }
                    html += '<div class="booking-details">';
                    html += '<p>' + booking.bordingPoint + ' to ' + booking.dropingPoint + ' | Date: ' + booking.date + '</p>';
                    html += '<p>Ticket Number: ' + booking.ticketNumber + ' | RegNo: ' + booking.regNumber + ' | Amount: ' + booking.amount + '</p>';
                    html += '</div>';
                    html += '<div class="passenger-details" id="passenger-' + booking.ticketNumber + '" data-status-id="' + booking.statusId + '"></div>';
                    html += '</div>';
                });
                $('#history-results').html(html);

                $('.booking-card').click(function() {
                    const ticketNumber = $(this).data('ticket-number');
                    const passengerDetailsDiv = $('#passenger-' + ticketNumber);

                    if (passengerDetailsDiv.is(':visible')) {
                        passengerDetailsDiv.hide();
                    } else {
                        $.ajax({
                            type: 'GET',
                            url: '<%= request.getContextPath() %>/fetchPassengerDetails',
                            data: { ticketNumber: ticketNumber },
                            dataType: 'json',
                            success: function(response) {
                                if (response.response_status.status === "success") {
                                    displayPassengerDetails(ticketNumber, response.data);
                                } else {
                                    passengerDetailsDiv.html('<p style="color: red;">' + response.response_status.message + '</p>').show();
                                }
                            },
                            error: function(error) {
                                passengerDetailsDiv.html('<p style="color: red;">An error occurred while fetching passenger details.</p>').show();
                                console.error('Error fetching passenger details:', error);
                            }
                        });
                    }
                });
            }

            function displayPassengerDetails(ticketNumber, passengers) {
                let html = '<h3>Passenger Details</h3>';
                html += '<table>';
                html += '<thead><tr><th>Seat Number</th><th>Name</th><th>Age</th><th>Gender</th></tr></thead>';
                html += '<tbody>';
                passengers.forEach(function(passenger) {
                    html += '<tr>';
                    html += '<td>' + passenger.seatNumber + '</td>';
                    html += '<td>' + passenger.name + '</td>';
                    html += '<td>' + passenger.age + '</td>';
                    html += '<td>' + passenger.gender + '</td>';
                    html += '</tr>';
                });
                html += '</tbody>';
                html += '</table>';
                $('#passenger-' + ticketNumber).html(html).show();
            }
        });
    </script>
</body>
</html>

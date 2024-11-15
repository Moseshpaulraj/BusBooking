<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.busbooking.Constants" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>User Ticket</title>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&family=Open+Sans:wght@400;600&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Open Sans', sans-serif;
            background-color: #f4f4f9;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        .navbar {
            background-color: #e53935;
            padding: 1rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            color: white;
            width: 100%;
            box-sizing: border-box;
            position: absolute;
            top: 0;
        }
        .navbar h1 {
            margin: 0;
            font-family: 'Roboto', sans-serif;
        }
        .navbar a {
            color: white;
            margin-left: 20px;
            text-decoration: none;
            font-weight: 500;
        }
        .container {
            width: 400px;
            background-color: #fff;
            padding: 20px;
            border: 1px dashed #4caf50;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            text-align: center;
        }
        .title {
            font-size: 1em;
            margin-bottom: 10px;
            color: #333;
            font-weight: 600;
            text-transform: uppercase;
        }
        .ticket-details {
            margin-bottom: 20px;
            font-size: 0.9em;
            color: #555;
        }
        .highlight {
            font-weight: bold;
            color: #4caf50;
        }
        #date {
    	color: black; 
		}
        .ticket-info {
            display: flex;
            flex-direction: column;
            align-items: center;
        }
        .info-block {
            margin-bottom: 10px;
        }
        .passenger-details {
            font-size: 0.9em;
            color: #555;
            text-align: left;
            margin-bottom: 10px;
        }
        table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 10px;
        }
        th, td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
        }
        th {
            background-color: #4caf50;
            color: white;
        }
        .options {
            text-align: right;
            margin-top: 10px;
        }
        .options button {
            background-color: #4caf50;
            color: white;
            border: none;
            padding: 10px 20px;
            border-radius: 4px;
            cursor: pointer;
            font-size: 1em;
            transition: background-color 0.3s;
        }
        .options button:hover {
   		 background-color: #45a049; /* Slightly darker shade when hovering */
		}
   </style>
</head>
<body>
    <div class="navbar">
        <h1>BusBooking</h1>
        <div>
            <a href="javascript:redirectToHomePage()">Home</a>
            <a href="javascript:signout()">Signout</a>
        </div>
    </div>
    <div class="container">
        <div class="title">Your Ticket</div>
        <div class="ticket-details">
            <div class="ticket-info">
                <div class="info-block">
                    <p><span id="boardingPoint" class="highlight"></span> to <span id="droppingPoint" class="highlight"></span></p>
                    <p><span id="date" class="highlight"></span></p>
                </div>
                <div class="info-block">
                    <p><strong>Ticket Number:</strong> <span id="ticketNumber"></span> | <strong>No. Of Passengers:</strong> <span id="numPassengers"></span> | <strong>Bus No:</strong> <span id="busId"></span></p>
                </div>
            </div>
        </div>
        <div class="passenger-details">
            <div class="title">Passenger Details</div>
            <table>
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Gender</th>
                        <th>Age</th>
                    </tr>
                </thead>
                <tbody id="passengerDetails">
                    <!-- Passenger details will be added here -->
                </tbody>
            </table>
        </div>
        <div class="ticket-details">
            <p class="highlight"><strong>Total Amount Paid: Rs. <span id="totalAmount"></span></strong></p>
        </div>
        <div class="options">
            <button onclick="downloadTicketAsPDF()">Download as PDF</button>
        </div>
    </div>
    <script>
        document.addEventListener('DOMContentLoaded', (event) => {
            const ticketData = JSON.parse(localStorage.getItem('ticketData'));
            if (ticketData) {
                document.getElementById('ticketNumber').innerText = ticketData.ticketNumber;
                document.getElementById('boardingPoint').innerText = ticketData.boardingPoint;
                document.getElementById('droppingPoint').innerText = ticketData.droppingPoint;
                document.getElementById('date').innerText = ticketData.date;
                document.getElementById('totalAmount').innerText = ticketData.totalAmount.toFixed(2);
                document.getElementById('busId').innerText = ticketData.busId;
                document.getElementById('numPassengers').innerText = ticketData.passengers.length;

                const passengerDetailsTbody = document.getElementById('passengerDetails');
                ticketData.passengers.forEach(function(passenger) {
                    const row = document.createElement('tr');
                    row.innerHTML = 
                        '<td>' + passenger.name + '</td>' +
                        '<td>' + passenger.gender + '</td>' +
                        '<td>' + passenger.age + '</td>';
                    passengerDetailsTbody.appendChild(row);
                });
            } else {
                alert('No ticket data found!');
            }
        });

        function signout() {
            $.ajax({
                type: 'GET',
                url: '<%= request.getContextPath() %>/signout',
                success: function(response) {
                    if (response.response_status.status === "success") {
                        window.location.href = '<%= request.getContextPath() %>/login.jsp';
                    } else {
                        alert('Sign out failed: ' + response.response_status.message);
                    }
                },
                error: function(error) {
                    alert('An error occurred during sign out.');
                    console.error('Error during sign out:', error);
                }
            });
        }

        function redirectToHomePage() {
            const userType = '<%= session.getAttribute("userType") %>';
            const isAdmin = userType === '<%= Constants.ADMIN %>';
            window.location.href = '<%= request.getContextPath() %>/' + (isAdmin ? 'admin/home.jsp' : 'customer/home.jsp');
        }

        function downloadTicketAsPDF() {
            const element = document.querySelector('.container');
            html2pdf()
                .from(element)
                .save('ticket.pdf');
        }
    </script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/html2pdf.js/0.9.2/html2pdf.bundle.min.js"></script>
</body>
</html>
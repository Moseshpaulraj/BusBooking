<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Dashboard</title>
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
        }
        .navbar .menu {
            display: flex;
            align-items: center;
            cursor: pointer;
        }
        .navbar .menu i {
            margin-right: 0.5rem;
        }
        .dropdown {
            position: relative;
            display: inline-block;
        }
        .dropdown-content {
            display: none;
            position: absolute;
            background-color: white;
            min-width: 160px;
            box-shadow: 0 8px 16px rgba(0,0,0,0.2);
            z-index: 1;
            border-radius: 5px;
            overflow: hidden;
        }
        .dropdown-content a {
            color: black;
            padding: 12px 16px;
            text-decoration: none;
            display: block;
            transition: background-color 0.3s;
        }
        .dropdown-content a:hover {
            background-color: #ddd;
        }
        .dropdown:hover .dropdown-content {
            display: block;
        }
        .dropdown:hover .dropbtn {
            background-color: #d32f2f;
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
        }
        .section p {
            color: #666;
        }
        .btn {
            display: inline-block;
            padding: 0.75rem 1.5rem;
            background-color: #e53935;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            transition: background-color 0.3s;
        }
        .btn:hover {
            background-color: #b71c1c;
        }
        #bus-results {
            margin-top: 2rem;
            display: none;
        }
        #bus-results div {
            padding: 1rem;
            border: 1px solid #ddd;
            border-radius: 5px;
            margin-bottom: 1rem;
            background-color: #fafafa;
        }
        #bus-results div:hover {
            background-color: #f0f0f0;
        }
        .view-seat-btn {
            margin-top: 1rem;
            display: inline-block;
            padding: 0.5rem 1rem;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            cursor: pointer;
        }
    </style>
</head>
<body>
    <div class="navbar">
        <h1>BusBooking</h1>
        <div class="dropdown">
            <div class="menu">
                <i class="fas fa-bars"></i>
                <span>Menu</span>
            </div>
            <div class="dropdown-content">
                <a href="<%= request.getContextPath() %>/common/bookinghistory.jsp"><i class="fas fa-history"></i>Previous Bookings</a>
                <a href="<%= request.getContextPath() %>/common/upcomingtrips.jsp"><i class="fas fa-calendar-alt"></i>Upcoming Bookings</a>
                <a href="<%= request.getContextPath() %>/admin/managebuses.jsp"><i class="fas fa-bus"></i>Bus Details</a>
                <a href="<%= request.getContextPath() %>/admin/managecustomers.jsp"><i class="fas fa-user"></i>Customer Details</a>
                <a href="<%= request.getContextPath() %>/admin/managedrivers.jsp"><i class="fas fa-id-card"></i>Driver Details</a>
                <a href="<%= request.getContextPath() %>/admin/manageadmins.jsp"><i class="fas fa-user-shield"></i>Admin Details</a>
                <a href="help.jsp"><i class="fas fa-question-circle"></i>Help</a>
                <a href="#" id="signout-link"><i class="fas fa-sign-out-alt"></i>Sign Out</a>
            </div>
        </div>
    </div>

    <div class="container">
        <div class="section">
            <h2>Book a Ticket</h2>
            <form id="bookingForm">
                <label for="from">From:</label>
                <select id="from" name="from" required>
                    <option value="" disabled selected>Select From Location</option>
                </select>
                <label for="to">To:</label>
                <select id="to" name="to" required>
                    <option value="" disabled selected>Select To Location</option>
                </select>
                <label for="date">Date:</label>
                <input type="date" id="date" name="date" required>
                <button type="submit" class="btn">Search Buses</button>
            </form>
            <div id="searchMessage"></div>
        </div>
    </div>

    <div id="bus-results" class="container"></div>

    <script>
        $(document).ready(function() {

            $.ajax({
                type: 'GET',
                url: '<%= request.getContextPath() %>/fetchLocations',
                dataType: 'json',
                success: function(response) {
                    if (response.response_status.status === "success") {
                        const locations = response.data;
                        locations.forEach(function(location) {
                            $('#from').append(new Option(location, location));
                            $('#to').append(new Option(location, location));
                        });
                    } else {
                        $('#searchMessage').css('color', 'red').html('Failed to fetch locations: ' + response.response_status.message);
                    }
                },
                error: function(error) {
                    $('#searchMessage').css('color', 'red').html('An error occurred while fetching locations.');
                    console.error('Error fetching locations:', error);
                }
            });

            $('#bookingForm').submit(function(event) {
                event.preventDefault();
                $.ajax({
                    type: 'POST',
                    url: '<%= request.getContextPath() %>/searchBuses',
                    data: $(this).serialize(),
                    dataType: 'json',
                    success: function(response) {
                        if (response.response_status.status === "success") {
                            $('#searchMessage').css('color', 'green').html("Available buses loaded successfully.");
                            displayAvailableBuses(response.data);
                        } else {
                            $('#searchMessage').css('color', 'red').html(response.response_status.message);
                            $('#bus-results').hide();
                        }
                    },
                    error: function(error) {
                        $('#searchMessage').css('color', 'red').html('An error occurred while searching buses.');
                        console.error('Error searching buses:', error);
                        $('#bus-results').hide();
                    }
                });
            });

            function displayAvailableBuses(buses) {
                let html = '';
                const date = $('#date').val(); 
                
                if (buses.length === 0) {
                    html = '<p>Oops! No bus is available</p>';
                    $('#bus-results').html(html);
                    $('#bus-results').show();
                } else {
	                buses.forEach(function(bus) {
	                    html += '<div>';
	                    html += '<p>Bus ID: ' + bus.id + '</p>';
	                    html += '<p>AC: ' + (bus.hasAc ? 'Yes' : 'No') + '</p>';
	                    html += '<p>Seat Group: ' + bus.seatGroup + '</p>';
	                    html += '<p>Departure Time: ' + bus.departureTime + '</p>';
	                    html += '<p>Arrival Time: ' + bus.arrivalTime + '</p>';
	                    html += '<button class="view-seat-btn" data-bus-id="' + bus.id + '" data-date="' + date + '">View Seats</button>';
	                    html += '</div>';
	                });
	                $('#bus-results').html(html);
	                $('#bus-results').show();
	
	                $('.view-seat-btn').click(function() {
	                    const busId = $(this).data('bus-id');
	                    const date = $(this).data('date');
	                    const seatGroup = buses.find(bus => bus.id === busId).seatGroup;
	
	                    $.ajax({
	                        url: '<%= request.getContextPath() %>/fetchBookedSeats',
	                        method: 'GET',
	                        data: { busId: busId, date: date },
	                        success: function(response) {
	                            console.log('Response:', response); 
	                            let seatLayoutUrl = getSeatLayoutUrl(seatGroup);
	                            
	                            if (Array.isArray(response.data)) {
	                                console.log('Booked Seats:', response.data); 
	                                if (response.data.length > 0) {
	                                    seatLayoutUrl += '?bookedSeats=' + encodeURIComponent(JSON.stringify(response.data));
	                                }
	                            } else {
	                                console.error('bookedSeats is not an array or is undefined');
	                            }
	                            
	                            window.location.href = seatLayoutUrl;
	                        },
	                        error: function(xhr, status, error) {
	                            console.error('Error fetching booked seats:', error);
	                        }
	                    });
	                });
	
	                function getSeatLayoutUrl(seatGroup) {
	                    let seatLayoutUrl = '';
	                    if (seatGroup === 'sleeper') {
	                        seatLayoutUrl = '<%= request.getContextPath() %>/common/sleeper.jsp';
	                    } else if (seatGroup === 'semi-sleeper') {
	                        seatLayoutUrl = '<%= request.getContextPath() %>/common/semi-sleeper.jsp';
	                    } else if (seatGroup === 'combined') {
	                        seatLayoutUrl = '<%= request.getContextPath() %>/common/combined.jsp';
	                    }
	                    return seatLayoutUrl;
	                }
                }
            }

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
        });
    </script>
</body>
</html>

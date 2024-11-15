<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Bus Details</title>
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
        .navbar .nav-links {
            display: flex;
            gap: 1rem;
        }
        .navbar .nav-links a {
            color: white;
            text-decoration: none;
        }
        .container {
            max-width: 800px;
            margin: 2rem auto;
            padding: 1rem;
            background-color: #ffffff;
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            position: relative;
        }
        h2 {
            color: #333;
            margin-bottom: 1rem;
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1rem;
        }
        .bus-table-container, .search-bus-container {
            display: none;
        }
        .active {
            display: block;
        }
        table {
            width: 100%;
            margin-bottom: 1rem;
            border-collapse: collapse;
        }
        th, td {
            padding: 1rem;
            text-align: left;
        }
        th {
            background-color: #e53935;
            color: white;
            font-weight: 500;
        }
        tr {
            transition: background-color 0.3s;
        }
        tr:hover {
            background-color: #f4f4f9;
        }
        .btn {
            display: inline-block;
            padding: 0.5rem 1rem;
            background-color: #e53935;
            color: white;
            text-decoration: none;
            border-radius: 5px;
            transition: background-color 0.3s;
            cursor: pointer;
        }
        .btn:hover {
            background-color: #b71c1c;
        }
        .form-group {
            margin-bottom: 1rem;
        }
        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
        }
        .form-group input,
        .form-group select {
            width: 100%;
            padding: 0.5rem;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
        .form-group button {
            margin-top: 1rem;
        }
        .form-group .radio-group {
            display: flex;
            gap: 1rem;
        }
        .radio-group label {
            display: flex;
            align-items: center;
        }
        .three-dot-menu-content {
            display: none;
            position: absolute;
            right: 0;
            background-color: #ffffff;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            z-index: 1;
            border-radius: 5px;
            overflow: hidden;
        }
        .three-dot-menu-content a {
            color: #333;
            padding: 0.5rem 1rem;
            text-decoration: none;
            display: block;
        }
        .three-dot-menu-content a:hover {
            background-color: #f4f4f9;
        }
        .row-actions {
            position: relative;
        }
        .three-dot-menu {
            position: relative;
            display: inline-block;
        }
        .three-dot-menu:hover .three-dot-menu-content {
            display: block;
        }
        .toggle-links {
            display: flex;
            gap: 1rem;
            margin-bottom: 1rem;
        }
        .toggle-links a {
            cursor: pointer;
            color: #e53935;
            font-weight: bold;
        }
        .toggle-links a.active {
            text-decoration: underline;
        }
        .details-container {
            margin-top: 1rem;
            display: none;
        }
        .form-inline {
            display: flex;
            gap: 1rem;
            align-items: center;
        }
        .search-results-container {
            margin-top: 1rem;
        }
    </style>
</head>
<body>
    <div class="navbar">
        <h1>Bus Dashboard</h1>
        <div class="nav-links">
            <a href="home.jsp">Home</a>
            <a href="#" id="signout-link">Sign Out</a>
        </div>
    </div>
    <div class="container">
        <div class="header">
            <div class="toggle-links">
                <a id="allBusesLink" class="active">ALL BUSES</a>
                <a id="searchBusesLink">SEARCH BUSES</a>
            </div>
            <i class="fas fa-plus btn" id="addBusBtn"></i>
        </div>
        
        <!-- All Buses List -->
        <div class="bus-table-container active">
            <table id="busTable">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Register Number</th>
                        <th>A/C</th>
                        <th>Current Location</th>
                        <th>Driver Name</th>
                        <th>Seat Group</th>
                    </tr>
                </thead>
                <tbody>
                    <!-- Buses will be populated here by JavaScript -->
                </tbody>
            </table>
        </div>
        
        <!-- Search Buses -->
        <div class="search-bus-container">
            <form id="bookingForm" class="form-inline">
                <select id="fromLocation" name="from">
                    <option value="" disabled selected>Select from location</option>
                    <!-- Locations will be populated here by JavaScript -->
                </select>
                <select id="toLocation" name="to">
                    <option value="" disabled selected>Select to location</option>
                    <!-- Locations will be populated here by JavaScript -->
                </select>
                <input type="date" id="travelDate" name="date" required>
                <button type="submit" class="btn">Search</button>
            </form>
            <div id="bus-results" class="search-results-container">
                <!-- Search results will be populated here by JavaScript -->
            </div>
        </div>
    </div>
    <script>
        $(document).ready(function() {
            $('#signout-link').click(function(event) {
                event.preventDefault();
                $.ajax({
                    type: 'POST',
                    url: '<%= request.getContextPath() %>/signout',
                    success: function() {
                        window.location.href = 'signin.jsp';
                    },
                    error: function(error) {
                        alert('Error signing out');
                        console.error('Sign out error:', error);
                    }
                });
            });

            // Toggle links
            $('#allBusesLink').click(function() {
                $(this).addClass('active');
                $('#searchBusesLink').removeClass('active');
                $('.bus-table-container').addClass('active');
                $('.search-bus-container').removeClass('active');
                allBuses();
            });

            $('#searchBusesLink').click(function() {
                $(this).addClass('active');
                $('#allBusesLink').removeClass('active');
                $('.search-bus-container').addClass('active');
                $('.bus-table-container').removeClass('active');
            });

            $('#addBusBtn').click(function() {
                window.location.href = 'addbus.jsp';
            });

            // Fetch Locations, Drivers, and Routes
            fetchLocations();
            allBuses();

            function fetchLocations() {
                $.ajax({
                    type: 'GET',
                    url: '<%= request.getContextPath() %>/fetchLocations',
                    success: function(response) {
                        if (response.response_status.status === "success") {
                            const locations = response.data;
                            let options = '<option value="" disabled selected>Select from location</option>';
                            locations.forEach(function(location) {
                                options += '<option value="' + location + '">' + location + '</option>';
                            });
                            $('#fromLocation').html(options);

                            options = '<option value="" disabled selected>Select to location</option>';
                            locations.forEach(function(location) {
                                options += '<option value="' + location + '">' + location + '</option>';
                            });
                            $('#toLocation').html(options);
                        } else {
                            console.error('Error fetching locations:', response);
                        }
                    },
                    error: function(error) {
                        console.error('Error fetching locations:', error);
                    }
                });
            }

            function allBuses() {
                $.ajax({
                    type: 'GET',
                    url: '<%= request.getContextPath() %>/fetchBuses',
                    success: function(response) {
                        if (response.response_status.status === "success") {
                            const buses = response.data;
                            let tableRows = '';
                            buses.forEach(function(bus) {
                                tableRows += '<tr>' +
                                    '<td>' + bus.id + '</td>' +
                                    '<td>' + bus.registerNumber + '</td>' +
                                    '<td>' + (bus.isAC ? 'Yes' : 'No') + '</td>' +
                                    '<td>' + bus.currentLocation + '</td>' +
                                    '<td>' + bus.driverName + '</td>' +
                                    '<td>' + bus.seatGroup + '</td>' +
                                '</tr>';
                            });
                            $('#busTable tbody').html(tableRows);
                        } else {
                            console.error('Error fetching buses:', response);
                        }
                    },
                    error: function(error) {
                        console.error('Error fetching buses:', error);
                    }
                });
            }

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
           
        });
    </script>
</body>
</html>

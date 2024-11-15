<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Bus</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/flatpickr/dist/flatpickr.min.css">
<script src="https://cdn.jsdelivr.net/npm/flatpickr"></script>
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
        }
        h2 {
            color: #333;
            margin-bottom: 1rem;
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
        .hidden {
            display: none;
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
        <h2>Add New Bus</h2>
        <form id="addBusForm">
            <div class="form-group">
                <label for="registerNumber">Register Number:</label>
                <input type="text" id="registerNumber" name="registerNumber" required>
            </div>
            <div class="form-group">
                <label>A/C:</label>
                <div class="radio-group">
                    <label><input type="radio" name="ac" value="true" required> Yes</label>
                    <label><input type="radio" name="ac" value="false" required> No</label>
                </div>
            </div>
            <div class="form-group">
                <label>Seat Type:</label>
                <div class="radio-group">
                    <label><input type="radio" name="seatType" value="1" required> Sleeper</label>
                    <label><input type="radio" name="seatType" value="2" required> Semi-Sleeper</label>
                    <label><input type="radio" name="seatType" value="3" required> Combined</label>
                </div>
            </div>
            <div class="form-group">
                <label for="driver">Select Driver:</label>
                <select id="driver" name="driver" required>
                    <option value="" disabled selected>Select Driver</option>
                    <!-- Available drivers will be populated here by JavaScript -->
                </select>
            </div>
            <div class="form-group">
                <label for="departureLocation">Departure Location:</label>
                <select id="departureLocation" name="departureLocation" required>
                    <option value="" disabled selected>Select Departure Location</option>
                    <!-- Locations will be populated here by JavaScript -->
                </select>
            </div>
            <div class="form-group hidden" id="routeGroup">
                <label for="route">Select Route:</label>
                <select id="route" name="route"required>
                    <option value="" disabled selected>Select Route</option>
                    <!-- Routes will be populated here by JavaScript -->
                </select>
            </div>
            <div class="form-group">
		    <label for="departureTime">Departure Time:</label>
		    <input type="text" id="departureTime" name="departureTime" required>
		</div>
		<script>
		    flatpickr("#departureTime", {
		        enableTime: true,
		        noCalendar: true,
		        dateFormat: "H:i:S",
		        time_24hr: true,
		        enableSeconds: true
		    });
		</script>
            <button type="submit" class="btn">Add Bus</button>
        </form>
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

            // Fetch Locations and Drivers
            fetchLocations();
            fetchDrivers();

            function fetchLocations() {
                $.ajax({
                    type: 'GET',
                    url: '<%= request.getContextPath() %>/fetchLocations',
                    success: function(response) {
                        if (response.response_status.status === "success") {
                            const locations = response.data;
                            let options = '<option value="" disabled selected>Select Departure Location</option>';
                            locations.forEach(function(location) {
                                options += '<option value="' + location + '">' + location + '</option>';
                            });
                            $('#departureLocation').html(options);
                        } else {
                            alert('Failed to fetch locations: ' + response.response_status.message);
                        }
                    },
                    error: function(error) {
                        alert('An error occurred while fetching locations.');
                        console.error('Error fetching locations:', error);
                    }
                });
            }

            function fetchDrivers() {
                $.ajax({
                    type: 'GET',
                    url: '<%= request.getContextPath() %>/fetchAvailableDrivers',
                    success: function(response) {
                        if (response.response_status.status === "success") {
                            const drivers = response.data;
                            let options = '<option value="" disabled selected>Select Driver</option>';
                            drivers.forEach(function(driver) {
                                options += '<option value="' + driver.id + '">' + driver.name + '</option>';
                            });
                            $('#driver').html(options);
                        } else {
                            alert('Failed to fetch drivers: ' + response.response_status.message);
                        }
                    },
                    error: function(error) {
                        alert('An error occurred while fetching drivers.');
                        console.error('Error fetching drivers:', error);
                    }
                });
            }

            $('#departureLocation').change(function() {
                const selectedLocation = $(this).val();
                fetchRoutes(selectedLocation);
            });

            function fetchRoutes(location) {
                $.ajax({
                    type: 'GET',
                    url: '<%= request.getContextPath() %>/fetchRoutes',
                    data: { location: location },
                    success: function(response) {
                        if (response.response_status.status === "success") {
                            const routes = response.data;
                            let options = '<option value="" disabled selected>Select Route</option>';
                            routes.forEach(function(route) {
                                options += '<option value="' + route.id + '">' + route.locations + '</option>';
                            });
                            $('#route').html(options);
                            $('#routeGroup').removeClass('hidden');
                        } else {
                            alert('Failed to fetch routes: ' + response.response_status.message);
                        }
                    },
                    error: function(error) {
                        alert('An error occurred while fetching routes.');
                        console.error('Error fetching routes:', error);
                    }
                });
            }

            $('#addBusForm').submit(function(event) {
                event.preventDefault();
                const formData = $(this).serialize();
                $.ajax({
                    type: 'POST',
                    url: '<%= request.getContextPath() %>/addBus',
                    data: formData,
                    success: function(response) {
                        if (response.response_status.status === "success") {
                            alert('Bus added successfully');
                            $('#addBusForm')[0].reset();
                            $('#routeGroup').addClass('hidden');
                        } else {
                            alert('Failed to add bus: ' + response.response_status.message);
                        }
                    },
                    error: function(error) {
                        alert('An error occurred while adding the bus.');
                        console.error('Error adding bus:', error);
                    }
                });
            });
        });
    </script>
</body>
</html>

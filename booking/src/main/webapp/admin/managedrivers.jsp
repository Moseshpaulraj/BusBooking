<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Driver Details</title>
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
        .driver-table-container, .available-driver-container, .add-driver-container {
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
        .form-group input {
            width: 100%;
            padding: 0.5rem;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
        .form-group button {
            margin-top: 1rem;
        }
        .action-btn {
            background: none;
            border: none;
            color: #e53935;
            font-size: 1.5rem;
            cursor: pointer;
        }
        .action-btn:hover {
            color: #b71c1c;
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
    </style>
</head>
<body>
    <div class="navbar">
        <h1>Driver Dashboard</h1>
        <div class="nav-links">
            <a href="home.jsp">Home</a>
            <a href="#" id="signout-link">Sign Out</a>
        </div>
    </div>
    <div class="container">
        <div class="header">
            <div class="toggle-links">
                <a id="allDriversLink" class="active">ALL DRIVERS</a>
                <a id="availableDriversLink">AVAILABLE DRIVERS</a>
            </div>
            <i class="fas fa-plus btn" id="addDriverBtn"></i>
        </div>
        
        <!-- All Drivers List -->
        <div class="driver-table-container active">
            <table id="driverTable">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Phone</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <!-- Drivers will be populated here by JavaScript -->
                </tbody>
            </table>
        </div>
        
        <!-- Available Drivers List -->
        <div class="available-driver-container">
            <table id="availableDriverTable">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Phone</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <!-- Available Drivers will be populated here by JavaScript -->
                </tbody>
            </table>
        </div>

        <!-- Add New Driver -->
        <div class="add-driver-container">
            <h3>Add New Driver</h3>
            <form id="addDriverForm">
                <div class="form-group">
                    <label for="driverName">Name:</label>
                    <input type="text" id="driverName" name="name" required>
                </div>
                <div class="form-group">
                    <label for="driverPhone">Phone:</label>
                    <input type="text" id="driverPhone" name="phoneNumber" required>
                </div>
                <div class="form-group">
                    <label for="driverLicense">License Number:</label>
                    <input type="text" id="driverLicense" name="licenceNumber" required>
                </div>
                <button type="submit" class="btn">Add Driver</button>
            </form>
        </div>

    </div>

    <script>
        $(document).ready(function() {
            // Fetch all drivers
            fetchDrivers();

            // Toggle between all drivers and available drivers
            $('#allDriversLink').click(function() {
                $('.toggle-links a').removeClass('active');
                $(this).addClass('active');
                $('.driver-table-container').addClass('active');
                $('.available-driver-container').removeClass('active');
                $('.add-driver-container').removeClass('active');
                fetchDrivers();
            });

            $('#availableDriversLink').click(function() {
                $('.toggle-links a').removeClass('active');
                $(this).addClass('active');
                $('.driver-table-container').removeClass('active');
                $('.available-driver-container').addClass('active');
                $('.add-driver-container').removeClass('active');
                fetchAvailableDrivers();
            });

            // Show add driver form
            $('#addDriverBtn').click(function() {
                $('.driver-table-container').removeClass('active');
                $('.available-driver-container').removeClass('active');
                $('.add-driver-container').addClass('active');
            });


            $('#addDriverForm').submit(function(event) {
                event.preventDefault();
                $.ajax({
                    type: 'POST',
                    url: '<%= request.getContextPath() %>/addDriver',
                    data: $(this).serialize(),
                    success: function(response) {
                        if (response.response_status.status === "success") {
                            alert('Driver added successfully');
                            fetchDrivers();
                            $('#addDriverForm')[0].reset();
                            $('#allDriversLink').click();
                        } else {
                            alert('Failed to add driver: ' + response.response_status.message);
                        }
                    },
                    error: function(error) {
                        alert('An error occurred while adding driver.');
                        console.error('Error adding driver:', error);
                    }
                });
            });


            function fetchDrivers() {
                $.ajax({
                    type: 'GET',
                    url: '<%= request.getContextPath() %>/fetchDrivers',
                    success: function(response) {
                        if (response.response_status.status === "success") {
                            const drivers = response.data;
                            let html = '';
                            drivers.forEach(function(driver) {
                                html += '<tr>';
                                html += '<td>' + driver.id + '</td>';
                                html += '<td>' + driver.name + '</td>';
                                html += '<td>' + driver.phoneNumber + '</td>';
                                html += '<td class="row-actions"><div class="three-dot-menu"><button class="action-btn"><i class="fas fa-ellipsis-h"></i></button><div class="three-dot-menu-content"><a href="#" class="deleteDriverBtn" data-id="' + driver.id + '">Delete</a></div></div></td>';
                                html += '</tr>';
                            });
                            $('#driverTable tbody').html(html);
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

            function fetchAvailableDrivers() {
                $.ajax({
                    type: 'GET',
                    url: '<%= request.getContextPath() %>/fetchAvailableDrivers',
                    success: function(response) {
                        if (response.response_status.status === "success") {
                            const drivers = response.data;
                            let html = '';  
                            if (drivers.length == 0) {
                                html = '<tr><td colspan="4">No available drivers</td></tr>';
                            } else {
                                drivers.forEach(function(driver) {
                                    html += '<tr>';
                                    html += '<td>' + driver.id + '</td>';
                                    html += '<td>' + driver.name + '</td>';
                                    html += '<td>' + driver.phoneNumber + '</td>';
                                    html += '<td class="row-actions"><div class="three-dot-menu"><button class="action-btn"><i class="fas fa-ellipsis-h"></i></button><div class="three-dot-menu-content"><a href="#" class="deleteDriverBtn" data-id="' + driver.id + '">Delete</a></div></div></td>';
                                    html += '</tr>';
                                });
                            }

                            $('#availableDriverTable tbody').html(html);
                        } else {
                            alert('Failed to fetch available drivers: ' + response.response_status.message);
                        }
                    },
                    error: function(error) {
                        alert('An error occurred while fetching available drivers.');
                        console.error('Error fetching available drivers:', error);
                    }
                });
            }

            // Delete driver
            $(document).on('click', '.deleteDriverBtn', function() {
                const driverId = $(this).data('id');
                $.ajax({
                    type: 'DELETE',
                    url: '<%= request.getContextPath() %>/deleteDriver?id=' + driverId,
                    success: function(response) {
                        if (response.response_status.status === "success") {
                            alert('Driver deleted successfully');
                            fetchDrivers();
                            fetchAvailableDrivers();
                        } else {
                            alert('Failed to delete driver: ' + response.response_status.message);
                        }
                    },
                    error: function(error) {
                        alert('An error occurred while deleting driver.');
                        console.error('Error deleting driver:', error);
                    }
                });
            });

            // Toggle three-dot menu
            $(document).on('click', '.action-btn', function() {
                $(this).next('.three-dot-menu-content').toggle();
            });

            // Close three-dot menu when clicking outside
            $(document).click(function(event) {
                if (!$(event.target).closest('.three-dot-menu').length) {
                    $('.three-dot-menu-content').hide();
                }
            });

            // Sign out functionality
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

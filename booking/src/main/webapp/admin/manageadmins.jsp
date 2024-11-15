<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Details</title>
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
        .admin-table-container, .add-admin-container {
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
    </style>
</head>
<body>
    <div class="navbar">
        <h1>Admin Dashboard</h1>
        <div class="nav-links">
            <a href="home.jsp">Home</a>
            <a href="#" id="signout-link">Sign Out</a>
        </div>
    </div>
    <div class="container">
        <div class="header">
            <h2>All Admins</h2>
            <i class="fas fa-plus btn" id="addAdminBtn"></i>
        </div>
        
        <!-- Admin List -->
        <div class="admin-table-container active">
            <table id="adminTable">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Phone</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <!-- Admins will be populated here by JavaScript -->
                </tbody>
            </table>
        </div>
        
        <!-- Add New Admin -->
        <div class="add-admin-container">
            <h3>Add New Admin</h3>
            <form id="addAdminForm">
                <div class="form-group">
                    <label for="adminName">Name:</label>
                    <input type="text" id="adminName" name="name" required>
                </div>
                <div class="form-group">
                    <label for="adminPhone">Phone:</label>
                    <input type="text" id="adminPhone" name="phoneNumber" required>
                </div>
                <div class="form-group">
                    <label for="adminPassword">Password:</label>
                    <input type="password" id="adminPassword" name="password" required>
                </div>
                <div class="form-group">
                    <label for="adminConfirmPassword">Confirm Password:</label>
                    <input type="password" id="adminConfirmPassword" name="confirmPassword" required>
                </div>
                <button type="submit" class="btn">Add Admin</button>
            </form>
        </div>
    </div>

    <script>
        $(document).ready(function() {
            // Fetch all admins
            fetchAdmins();

            // Toggle between admin table and add admin form
            $('#addAdminBtn').click(function() {
                $('.admin-table-container').toggleClass('active');
                $('.add-admin-container').toggleClass('active');
            });

            // Add new admin
            $('#addAdminForm').submit(function(event) {
                event.preventDefault();
                $.ajax({
                    type: 'POST',
                    url: '<%= request.getContextPath() %>/addAdmin',
                    data: $(this).serialize(),
                    success: function(response) {
                        if (response.response_status.status === "success") {
                            alert('Admin added successfully');
                            fetchAdmins();
                            $('#addAdminForm')[0].reset();
                            $('.admin-table-container').toggleClass('active');
                            $('.add-admin-container').toggleClass('active');
                        } else {
                            alert('Failed to add admin: ' + response.response_status.message);
                        }
                    },
                    error: function(error) {
                        alert('An error occurred while adding admin.');
                        console.error('Error adding admin:', error);
                    }
                });
            });

            // Delete admin
            $(document).on('click', '.deleteAdminBtn', function() {
                const adminId = $(this).data('id');
                $.ajax({
                    type: 'DELETE',
                    url: '<%= request.getContextPath() %>/deleteAdmin?id=' + adminId,
                    success: function(response) {
                        if (response.response_status.status === "success") {
                            alert('Admin deleted successfully');
                            fetchAdmins();
                        } else {
                            alert('Failed to delete admin: ' + response.response_status.message);
                        }
                    },
                    error: function(error) {
                        alert('An error occurred while deleting admin.');
                        console.error('Error deleting admin:', error);
                    }
                });
            });

            // Fetch all admins function
            function fetchAdmins() {
                $.ajax({
                    type: 'GET',
                    url: '<%= request.getContextPath() %>/fetchAdmins',
                    success: function(response) {
                        if (response.response_status.status === "success") {
                            const admins = response.data;
                            let html = '';
                            admins.forEach(function(admin) {
                                html += '<tr>';
                                html += '<td>' + admin.id + '</td>';
                                html += '<td>' + admin.name + '</td>';
                                html += '<td>' + admin.phoneNumber + '</td>';
                                html += '<td class="row-actions"><div class="three-dot-menu"><button class="action-btn"><i class="fas fa-ellipsis-h"></i></button><div class="three-dot-menu-content"><a href="#" class="deleteAdminBtn" data-id="' + admin.id + '">Delete</a></div></div></td>';
                                html += '</tr>';
                            });
                            $('#adminTable tbody').html(html);
                        } else {
                            alert('Failed to fetch admins: ' + response.response_status.message);
                        }
                    },
                    error: function(error) {
                        alert('An error occurred while fetching admins.');
                        console.error('Error fetching admins:', error);
                    }
                });
            }

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
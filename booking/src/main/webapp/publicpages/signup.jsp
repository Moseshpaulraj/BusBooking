<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Sign Up</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <style>
        body {
            font-family: 'Roboto', sans-serif;
            background-color: #f4f4f9;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }
        .signup-container {
            background-color: #ffffff;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            max-width: 400px;
            width: 100%;
        }
        .signup-container h2 {
            text-align: center;
            margin-bottom: 1.5rem;
            color: #333;
        }
        .signup-container input {
            display: block;
            width: 100%;
            padding: 0.75rem;
            margin-bottom: 1rem;
            border: 1px solid #ccc;
            border-radius: 5px;
        }
        .signup-container button {
            display: block;
            width: 100%;
            padding: 0.75rem;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 1rem;
            cursor: pointer;
        }
        .signup-container button:hover {
            background-color: #218838;
        }
        .login-container {
            text-align: center;
            margin-top: 1rem;
            color: #000;
            font-size: 0.9rem;
        }
        .login-link {
            color: #007bff;
            cursor: pointer;
            text-decoration: none;
        }
        .login-link:hover {
            text-decoration: underline;
        }
        #signupMessage {
            text-align: center;
            margin-top: 1rem;
            color: #d9534f;
        }
    </style>
</head>
<body>
    <div class="signup-container">
        <h2>Sign Up</h2>
        <form id="signupForm">
            <input type="text" id="name" name="name" placeholder="Name" required>
            <input type="text" id="phoneNumber" name="phoneNumber" placeholder="Phone Number" required>
            <input type="password" id="password" name="password" placeholder="Password" required>
            <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Confirm Password" required>
            <button type="submit">Sign Up</button>
        </form>
        <div class="login-container">
            Already have an account? <a class="login-link" id="loginButton">Log in</a>
        </div>
        <div id="signupMessage"></div>
    </div>

    <script>
        $(document).ready(function() {
            $('#signupForm').submit(function(event) {
                event.preventDefault();
                $.ajax({
                    type: 'POST',
                    url: '<%= request.getContextPath() %>/signup',
                    data: $(this).serialize(),
                    dataType: 'json',
                    success: function(response) {
                        if (response.response_status.status === "success") {
                            $('#signupMessage').css('color', 'green').html(response.response_status.message);
                            setTimeout(function() {
                                window.location.href = '<%= request.getContextPath() %>/publicpages/login.jsp';
                            }, 2000);
                        } else {
                            $('#signupMessage').css('color', 'red').html(response.response_status.message);
                        }
                    },
                    error: function(error) {
                        $('#signupMessage').css('color', 'red').html('An error occurred. Please try again.');
                    }
                });
            });

            $('#loginButton').click(function() {
                window.location.href = '<%= request.getContextPath() %>/publicpages/login.jsp';
            });
        });
    </script>
</body>
</html>
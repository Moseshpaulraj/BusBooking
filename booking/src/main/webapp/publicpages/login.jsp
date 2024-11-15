<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
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
        .login-container {
            background-color: #ffffff;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            max-width: 400px;
            width: 100%;
        }
        .login-container h2 {
            text-align: center;
            margin-bottom: 1.5rem;
            color: #333;
        }
        .login-container input {
            display: block;
            width: 100%;
            padding: 0.75rem;
            margin-bottom: 1rem;
            border: 1px solid #ccc;
            border-radius: 5px;
        }
        .login-container button {
            display: block;
            width: 100%;
            padding: 0.75rem;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 1rem;
            cursor: pointer;
            margin-top: 1rem;
        }
        .login-container button:hover {
            background-color: #0056b3;
        }
        .signup-container {
            text-align: center;
            margin-top: 1rem;
            color: #000;
        }
        .signup-link {
            color: #007bff;
            cursor: pointer;
        }
        .signup-link:hover {
            text-decoration: underline;
        }
        #loginMessage {
            text-align: center;
            margin-top: 1rem;
            color: #d9534f;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <h2>Login</h2>
        <form id="loginForm">
            <input type="text" id="phoneNumber" name="phoneNumber" placeholder="Phone Number" required>
            <input type="password" id="password" name="password" placeholder="Password" required>
            <button type="submit">Login</button>
        </form>
        <div class="signup-container">
            Don't have an account? <span class="signup-link" id="signupButton">Sign up</span>
        </div>
        <div id="loginMessage"></div>
    </div>

    <script>
    $(document).ready(function() {
        $('#loginForm').submit(function(event) {
            event.preventDefault();
            $.ajax({
                type: 'POST',
                url: '<%= request.getContextPath() %>/login',
                data: $(this).serialize(),
                dataType: 'json',
                success: function(response) {
                    if (response.response_status.status === "success") {
                        $('#loginMessage').css('color', 'green').html(response.response_status.message);
                        console.log("Redirecting to: " + response.data.redirectPage);
                        setTimeout(function() {
                            window.location.href = '<%= request.getContextPath() %>' + response.data.redirectPage;
                        }, 2000);
                    } else {
                        $('#loginMessage').css('color', 'red').html(response.response_status.message);
                    }
                },
                error: function(error) {
                    $('#loginMessage').css('color', 'red').html('An error occurred. Please try again.');
                }
            });
        });

        $('#signupButton').click(function() {
            window.location.href = '<%= request.getContextPath() %>/publicpages/signup.jsp';
        });
    });
    </script>
</body>
</html>
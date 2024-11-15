<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Ticket Booking</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;500;700&family=Open+Sans:wght@400;600&display=swap" rel="stylesheet">
    <style>
        body {
            font-family: 'Open Sans', sans-serif;
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
            font-family: 'Roboto', sans-serif;
        }
        .container {
            display: flex;
            justify-content: space-between;
            margin: 2rem auto;
            max-width: 800px;
            background-color: #fff;
            padding: 20px;
            border: 1px solid #ccc;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        .half-container {
            width: 45%;
        }
        .title {
            font-size: 1.4em;
            margin-bottom: 20px;
            color: #333;
            font-weight: 600;
        }
        .radio-group {
            list-style-type: none;
            padding: 0;
        }
        .radio-group li {
            margin-bottom: 15px;
            font-size: 1.1em;
            color: #555;
        }
        .radio-group input[type="radio"] {
            margin-right: 10px;
            accent-color: #e53935;
            transform: scale(1.2);
        }
        .hidden {
            display: none;
        }
        .result-container {
            width: 100%;
        }
        .passenger-details {
            display: flex;
            flex-direction: column;
            margin-bottom: 20px;
        }
        .passenger-details label {
            margin-bottom: 5px;
            font-weight: 500;
            color: #333;
        }
        .passenger-details input[type="text"], .passenger-details input[type="number"] {
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 1em;
            margin-bottom: 10px;
        }
        .passenger-details input[type="radio"] {
            margin-right: 5px;
        }
        .total-amount {
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-size: 1.2em;
            color: #333;
            font-weight: 600;
            margin-top: 20px;
        }
        .btn-proceed {
            background-color: #e53935;
            color: white;
            padding: 10px 20px;
            border: none;
            border-radius: 4px;
            font-size: 1em;
            cursor: pointer;
        }
        .btn-proceed:hover {
            background-color: #d32f2f;
        }
    </style>
</head>
<body>
    <div class="navbar">
        <h1>BusBooking</h1>
    </div>
    <div class="container" id="selectionContainer">
        <div class="half-container" id="boardingContainer">
            <div class="title">Boarding Points</div>
            <ul class="radio-group" id="boardingPoints"></ul>
        </div>
        <div class="half-container" id="droppingContainer">
            <div class="title">Dropping Points</div>
            <ul class="radio-group" id="droppingPoints"></ul>
        </div>
    </div>
    <div class="container hidden" id="resultContainer">
        <div class="result-container">
            <div class="title">Passenger Details</div>
            <div id="passengerDetailsContainer"></div>
            <div class="total-amount">
                <span id="totalAmount">Total Amount: $0</span>
                <button class="btn-proceed" id="btnProceed">Proceed to Pay</button>
            </div>
        </div>
    </div>
    <script>
    $(document).ready(function() {
        function fetchBoardingPoints() {
            $.ajax({
                url: '<%= request.getContextPath() %>/fetchStops?type=boarding',
                method: 'GET',
                success: function(response) {
                    if (response.response_status.status_code === 200) {
                        let boardingPoints = response.data;
                        $('#boardingPoints').empty();
                        boardingPoints.forEach(function(point) {
                            $('#boardingPoints').append('<li><input type="radio" name="boardingPoint" id="boarding-' + point + '" value="' + point + '"><label for="boarding-' + point + '">' + point + '</label></li>');
                        });
                    } else {
                        console.error('Error fetching boarding points:', response.response_status.message);
                    }
                },
                error: function(error) {
                    console.error('Error fetching boarding points:', error);
                }
            });
        }

        function fetchDroppingPoints() {
            $.ajax({
                url: '<%= request.getContextPath() %>/fetchStops?type=dropping',
                method: 'GET',
                success: function(response) {
                    if (response.response_status.status_code === 200) {
                        let droppingPoints = response.data;
                        $('#droppingPoints').empty();
                        droppingPoints.forEach(function(point) {
                            $('#droppingPoints').append('<li><input type="radio" name="droppingPoint" id="dropping-' + point + '" value="' + point + '"><label for="dropping-' + point + '">' + point + '</label></li>');
                        });
                    } else {
                        console.error('Error fetching dropping points:', response.response_status.message);
                    }
                },
                error: function(error) {
                    console.error('Error fetching dropping points:', error);
                }
            });
        }

        function checkSelections() {
            let boardingSelected = $('input[name="boardingPoint"]:checked').val();
            let droppingSelected = $('input[name="droppingPoint"]:checked').val();
            if (boardingSelected && droppingSelected) {
                $.ajax({
                    url: '<%= request.getContextPath() %>/submitSelectedPoints',
                    method: 'POST',
                    data: {
                        boardingPoint: boardingSelected,
                        droppingPoint: droppingSelected
                    },
                    success: function(response) {
                        if (response.response_status.status_code === 200) {
                            $('#selectionContainer').addClass('hidden');
                            $('#resultContainer').removeClass('hidden');
                            $('#passengerDetailsContainer').empty();
                            
                            response.data.selectedSeats.forEach(function(seat) {
                                var passengerDetails = 
                                    '<div class="passenger-details" data-seat="' + seat + '">' +
                                        '<label>Seat Number: ' + seat + '</label>' +
                                        '<input type="text" name="passengerName" placeholder="Name">' +
                                        '<input type="number" name="passengerAge" placeholder="Age" min="1" max="100">' +
                                        '<div>' +
                                            '<input type="radio" name="passengerGender-' + seat + '" value="male" id="male-' + seat + '">' +
                                            '<label for="male-' + seat + '">Male</label>' +
                                            '<input type="radio" name="passengerGender-' + seat + '" value="female" id="female-' + seat + '">' +
                                            '<label for="female-' + seat + '">Female</label>' +
                                        '</div>' +
                                    '</div>';  
                                $('#passengerDetailsContainer').append(passengerDetails);
                            });
                            $('#totalAmount').text('Total Amount: $' + response.data.totalAmount);
                        } else {
                            console.error('Error submitting points:', response.response_status.message);
                        }
                    },
                    error: function(error){
                        console.error('Error submitting points:', error);
                    }
                });
            }
        }
        
        fetchBoardingPoints();
        fetchDroppingPoints();

        $('#boardingPoints').on('change', 'input[name="boardingPoint"]', checkSelections);
        $('#droppingPoints').on('change', 'input[name="droppingPoint"]', checkSelections);

        $('#btnProceed').click(function() {
            var passengers = [];
            $('#passengerDetailsContainer .passenger-details').each(function() {
                var seatNumber = $(this).data('seat');
                var name = $(this).find('input[name="passengerName"]').val();
                var age = $(this).find('input[name="passengerAge"]').val();
                var gender = $(this).find('input[name^="passengerGender-"]:checked').val();
                
                var passenger = {
                    seatNumber: seatNumber,
                    name: name,
                    age: age,
                    gender: gender
                };
                passengers.push(passenger);
            });

            var totalAmount = $('#totalAmount').text().split('$')[1];
            var requestData = {
                passengers: passengers,
                totalAmount: parseFloat(totalAmount)
            };

            $.ajax({
                url: '<%= request.getContextPath() %>/generateTicket',
                method: 'POST',
                contentType: 'application/json',
                data: JSON.stringify(requestData),
                success: function(response) {
                    if (response.response_status.status_code === 200) {
                        alert('Payment successful!');
                        localStorage.setItem('ticketData', JSON.stringify(response.data));
                        window.location.href = '<%= request.getContextPath() %>/common/ticket.jsp';
                    } else {
                        console.error('Error processing payment:', response.response_status.message);
                    }
                },
                error: function(error) {
                    console.error('Error processing payment:', error);
                }
            });
        });
    });
    </script>
</body>
</html>
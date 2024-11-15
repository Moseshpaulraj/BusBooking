<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.busbooking.User" %>
<%@ page import="com.busbooking.Constants" %>
<%@ page session="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Combined Bus Seat Selection</title>
  <style>
    * {
      box-sizing: border-box;
    }

    body {
      font-family: Arial, sans-serif;
      margin: 0;
      padding: 20px;
      background-color: #f2f2f2;
    }

    .plane {
      max-width: 800px; /* Adjusted width to accommodate different seat sizes */
      margin: 20px auto;
      background-color: #fff;
      padding: 20px;
      border: 1px solid #ccc;
      border-radius: 8px;
    }

    .cabin {
      list-style-type: none;
      padding: 0;
    }

    .row {
      margin-bottom: 20px;
    }

    .seats {
      display: flex;
      flex-wrap: wrap;
      padding: 0;
      margin: 0;
      list-style-type: none;
    }

    .seat {
      flex: 0 0 calc(19% - 10px); /* Adjusted seat size for upper and lower deck */
      margin: 5px;
      text-align: center;
      position: relative;
    }

    .half-size {
      flex: 0 0 calc(10% - 10px); /* Half size for the third row seats */
    }

    .seat-small {
      flex: 0 0 calc(8% - 8px); /* Smaller size for the last row seats */
      margin: 3px;
    }

    .seat input[type="checkbox"] {
      display: none;
    }

    .seat label {
      display: block;
      background-color: #ddd; /* Default seat color */
      padding: 10px;
      cursor: pointer;
      border-radius: 5px;
      user-select: none;
    }

    .seat input[type="checkbox"]:checked + label {
      background-color: #4CAF50; /* User-selected seat color (green) */
    }

    .seat.booked label {
      background-color: #f44336; /* Booked seat color (red) */
      cursor: not-allowed;
    }

    .seat.booked input[type="checkbox"] {
      cursor: not-allowed;
    }

    .submit-btn {
      display: block;
      width: 100%;
      padding: 10px;
      background-color: #4CAF50;
      color: white;
      text-align: center;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      font-size: 16px;
      margin-top: 20px;
    }

    .submit-btn:disabled {
      background-color: #ccc;
      cursor: not-allowed;
    }
  </style>
  <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
  <div class="plane">
    <h1>Combined Bus Seat Selection</h1>
    <form id="seatSelectionForm">
      <!-- Hidden input to store user type -->
      <input type="hidden" id="userType" value="<%= session.getAttribute("userType") %>">
      <div class="cabin">
        <!-- Upper Deck -->
        <div class="row">
          <ol class="seats" type="U">
            <!-- Upper deck seats U1 to U5 -->
            <% for (int i = 1; i <= 5; i++) { %>
              <li class="seat">
                <input type="checkbox" name="selectedSeats" id="U<%= i %>" value="U<%= i %>" />
                <label for="U<%= i %>">U<%= i %></label>
              </li>
            <% } %>
          </ol>
        </div>
        <div class="row">
          <ol class="seats" type="U">
            <!-- Upper deck seats U6 to U10 -->
            <% for (int i = 6; i <= 10; i++) { %>
              <li class="seat">
                <input type="checkbox" name="selectedSeats" id="U<%= i %>" value="U<%= i %>" />
                <label for="U<%= i %>">U<%= i %></label>
              </li>
            <% } %>
          </ol>
        </div>
        <div class="row">
          <ol class="seats" type="U">
            <!-- Upper deck seats U11 to U15 -->
            <% for (int i = 11; i <= 15; i++) { %>
              <li class="seat">
                <input type="checkbox" name="selectedSeats" id="U<%= i %>" value="U<%= i %>" />
                <label for="U<%= i %>">U<%= i %></label>
              </li>
            <% } %>
          </ol>
        </div>

        <!-- Lower Deck -->
        <div class="row">
          <ol class="seats" type="L">
            <!-- Lower deck seats L1 to L5 -->
            <% for (int i = 1; i <= 5; i++) { %>
              <li class="seat">
                <input type="checkbox" name="selectedSeats" id="L<%= i %>" value="L<%= i %>" />
                <label for="L<%= i %>">L<%= i %></label>
              </li>
            <% } %>
          </ol>
        </div>
        <div class="row">
          <ol class="seats" type="L">
            <!-- Lower deck seats L6 to L10 -->
            <% for (int i = 6; i <= 10; i++) { %>
              <li class="seat">
                <input type="checkbox" name="selectedSeats" id="L<%= i %>" value="L<%= i %>" />
                <label for="L<%= i %>">L<%= i %></label>
              </li>
            <% } %>
          </ol>
        </div>
        <div class="row">
          <ol class="seats" type="L">
            <!-- Lower deck seats L11 to L20 with smaller size -->
            <% for (int i = 11; i <= 20; i++) { %>
              <li class="seat seat-small">
                <input type="checkbox" name="selectedSeats" id="L<%= i %>" value="L<%= i %>" />
                <label for="L<%= i %>">L<%= i %></label>
              </li>
            <% } %>
          </ol>
        </div>
      </div>
      <button type="button" class="submit-btn" disabled>Submit Selected Seats</button>
    </form>
  </div>

  <script>
    $(document).ready(function() {
      // Function to parse URL parameters
      function getUrlParameter(name) {
        name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
        var regex = new RegExp('[\\?&]' + name + '=([^&#]*)');
        var results = regex.exec(location.search);
        return results === null ? '' : decodeURIComponent(results[1].replace(/\+/g, ' '));
      }

      // Get the bookedSeats parameter from the URL
      var bookedSeats = getUrlParameter('bookedSeats');
      if (bookedSeats) {
        bookedSeats = JSON.parse(bookedSeats);
      } else {
        bookedSeats = [];
      }

      // Mark the booked seats and make them unclickable
      bookedSeats.forEach(function(seat) {
        var seatCheckbox = $('#' + seat);
        seatCheckbox.prop('checked', false);
        seatCheckbox.prop('disabled', true);
        seatCheckbox.parent().addClass('booked');
      });

      // Get the user type
      var userType = $('#userType').val();
      console.log(userType);

      // Limit the number of selectable seats to 6 if the user is not an admin
      if (userType !== '<%= Constants.ADMIN %>') {
        var selectedSeats = 0;
        $('input[type="checkbox"]').change(function() {
          if (this.checked) {
            selectedSeats++;
            if (selectedSeats === 6) {
              alert('You have selected 6 seats, which is the maximum allowed.');
            }
          } else {
            selectedSeats--;
          }

          if (selectedSeats >= 6) {
            $('input[type="checkbox"]:not(:checked)').prop('disabled', true);
          } else {
            $('input[type="checkbox"]:not(.booked input[type="checkbox"])').prop('disabled', false);
          }

          // Enable/disable the submit button based on selected seats
          if (selectedSeats > 0 && selectedSeats <= 6) {
            $('.submit-btn').prop('disabled', false);
          } else {
            $('.submit-btn').prop('disabled', true);
          }
        });
      } else {
        // If user is an admin, enable the submit button when any seat is selected
        $('input[type="checkbox"]').change(function() {
          var anySelected = $('input[type="checkbox"]:checked').length > 0;
          $('.submit-btn').prop('disabled', !anySelected);
        });
      }

      $('.submit-btn').click(function() {
        var selectedSeats = $('input[name="selectedSeats"]:checked').map(function() {
          return this.value;
        }).get();

        $.ajax({
          url: '<%= request.getContextPath() %>/selectedSeats',
          method: 'POST',
          data: { selectedSeats: selectedSeats },
          traditional: true,
          success: function(response) {
            if (response.response_status.status_code === 200) {
              window.location.href = response.data;
            } else {
              alert('Error: ' + response.response_status.message);
              window.history.back();
            }
          },
          error: function(jqXHR, textStatus, errorThrown) {
            alert('Error: ' + textStatus);
          }
        });
      });
    });
  </script>
</body>
</html>

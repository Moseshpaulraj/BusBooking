package com.busbooking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.customexception.AuthenticationException;
import com.customexception.InvalidInputException;


@WebServlet("/")
public class BusBookingServlet extends HttpServlet {

    private static final String STATUS_SUCCESS = "success", STATUS_FAILED = "failed";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String action = request.getServletPath();

        if ("/signout".equals(action)) {
            signOut(request, response);
        }else if ("/fetchLocations".equals(action)) {
            fetchLocations(response);
        }else if ("/fetchBookedSeats".equals(action)) {
        	fetchBookedSeats(request,response);
        }else if ("/fetchStops".equals(action)) {
        	fetchStops(request,response);
        }else if ("/fetchBookingHistory".equals(action)) {
        	fetchBookingHistory(request,response);
        }else if ("/fetchPassengerDetails".equals(action)) {
        	fetchPassengerDetails(request,response);
        }else if ("/fetchUpcomingTrips".equals(action)) {
        	fetchUpcomingTrips(request,response);
        }else if ("/fetchAdmins".equals(action)) {
        	fetchAdmins(request,response);
        }else if("/fetchDrivers".equals(action)) {
        	fetchDrivers(request,response);
        }else if("/fetchAvailableDrivers".equals(action)) {
        	fetchAvailableDrivers(request,response);
        }else if("/fetchBuses".equals(action)) {
        	fetchBuses(request,response);
        }else if("/fetchRoutes".equals(action)) {
        	fetchRoutes(request,response);
        }
    }

	private void fetchRoutes(HttpServletRequest request, HttpServletResponse response) {
		String location = request.getParameter("location");
		try {
			List<Route> routes = LocationService.getRoutes(location);
			JSONArray routesJson = new JSONArray();
	         for (Route route : routes) {
	             JSONObject routeObj = new JSONObject();
	             routeObj.put("id", route.getId());
	             routeObj.put("locations", route.getLocations());
	             routesJson.put(routeObj);
	         }
	         sendResponse(response, 200, STATUS_SUCCESS, "routes loaded successfully.", routesJson);
		} catch (Exception e) {
            sendResponse(response, 501, STATUS_FAILED, e.getMessage(), null);
		}
	}

	private void fetchBuses(HttpServletRequest request, HttpServletResponse response) {
		try{
			List<Bus> buses =  BusDataAccess.getAllBusDetails();
			JSONArray busesJson = new JSONArray();
	         for (Bus bus : buses) {
	             JSONObject admin = new JSONObject();
	             admin.put("id", bus.getId());
	             admin.put("registerNumber", bus.getRegisterNumber());
	             admin.put("hasAc", bus.getHasAc());
	             admin.put("currentLocation", bus.getCurrentLocation());
	             admin.put("driverName", bus.getDriverName());
	             admin.put("seatGroup", bus.getSeatType());
	             busesJson.put(admin);
	         }
	         sendResponse(response, 200, STATUS_SUCCESS, "Buses loaded successfully.", busesJson);
        }catch (Exception e) {
            sendResponse(response, 501, STATUS_FAILED, e.getMessage(), null);
		}
	}

	private void fetchAvailableDrivers(HttpServletRequest request, HttpServletResponse response) {
		try{
			List<Driver> drivers =  UserDataAccess.getAvailableDriver();
			JSONArray driversJson = new JSONArray();
	         for (Driver driver : drivers) {
	             JSONObject admin = new JSONObject();
	             admin.put("id", driver.getUserId());
	             admin.put("name", driver.getName());
	             admin.put("phoneNumber", driver.getPhoneNumber());
	             admin.put("licenceNumber", driver.getLicenceNumber());
	             driversJson.put(admin);
	         }
	         sendResponse(response, 200, STATUS_SUCCESS, "Drivers loaded successfully.", driversJson);
        }catch (Exception e){
            sendResponse(response, 501, STATUS_FAILED, e.getMessage(), null);
		}
	}

	private void fetchDrivers(HttpServletRequest request, HttpServletResponse response) {
		try {
			List<Driver> drivers = UserDataAccess.getAllDriverDetails();
			JSONArray driversJson = new JSONArray();
	         for (Driver driver : drivers) {
	             JSONObject admin = new JSONObject();
	             admin.put("id", driver.getUserId());
	             admin.put("name", driver.getName());
	             admin.put("phoneNumber", driver.getPhoneNumber());
	             admin.put("licenceNumber", driver.getLicenceNumber());
	             driversJson.put(admin);
	         }
	         sendResponse(response, 200, STATUS_SUCCESS, "Drivers loaded successfully.", driversJson);
        } catch (Exception e) {
            sendResponse(response, 501, STATUS_FAILED, e.getMessage(), null);
		}
	}

	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getServletPath();
        if ("/signup".equals(action)) {
            signUp(request, response);
        } else if ("/login".equals(action)) {
            signIn(request, response);
        } else if ("/searchBuses".equals(action)) {
            searchBuses(request, response);
        } else if ("/selectedSeats".equals(action)) {
        	selectedSeats(request,response);
        } else if ("/submitSelectedPoints".equals(action)) {
        	submitSelectedPoints(request,response);
        } else if ("/generateTicket".equals(action)) {
        	generateTicket(request,response);
        } else if ("/addAdmin".equals(action)) {
        	addAdmin(request,response);
        } else if("/addDriver".equals(action)) {
        	addDriver(request,response);
        } else if("/addBus".equals(action)) {
        	addBus(request,response);
        } else if("/cancelTrip".equals(action)) {
        	cancelTrip(request,response);
        }
    }

	private void cancelTrip(HttpServletRequest request, HttpServletResponse response) {
		String ticketNo =request.getParameter("ticketNumber");
		System.out.print(ticketNo);
		long ticketNumber = Long.parseLong(ticketNo);
		try {
			if(Ticket.cancelTicket(ticketNumber)) {
				sendResponse(response, 201, STATUS_SUCCESS,"Ticket cancelled successfully", null);
			}else {
				sendResponse(response, 402, STATUS_FAILED,"Failed to cancel ticket", null);	
			}
		} catch (Exception e) {
			sendResponse(response, 501, STATUS_FAILED,e.getMessage(), null);	
		}
	}

	private void addBus(HttpServletRequest request, HttpServletResponse response) {
		String registerNumber = request.getParameter("registerNumber");
        String ac = request.getParameter("ac");
        String seatType = request.getParameter("seatType");
        String driver = request.getParameter("driver");
        String currentLocation = request.getParameter("departureLocation");
        String route = request.getParameter("route");
        String departureTime = request.getParameter("departureTime");
		
        boolean hasAc = Boolean.parseBoolean(ac);
        short seatTypeId = Short.parseShort(seatType);
        short driverId =Short.parseShort(driver);
        short locationId;
		try {
			locationId = LocationService.getLocationIdByName(currentLocation);
			short routeId =Short.parseShort(route);
				if(BusDataAccess.addNewBus(registerNumber, hasAc, seatTypeId, locationId, driverId, routeId, departureTime)) {
				sendResponse(response, 201, STATUS_SUCCESS,"Bus Added successfully", null);
				}else {
					sendResponse(response, 402, STATUS_FAILED,"Failed to add bus", null);
				}
		} catch (Exception e) {
			sendResponse(response, 402, STATUS_FAILED,e.getMessage(), null);
		}
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
		String action = request.getServletPath();
        if ("/deleteAdmin".equals(action)) {
        	deleteAdmin(request, response);
        } else if("/deleteDriver".equals(action)) {
        	deleteDriver(request, response);
        }
	}

	private void deleteDriver(HttpServletRequest request, HttpServletResponse response) {		
		String driverID = request.getParameter("id");
		try {
			long driverId = Long.parseLong(driverID);
			boolean isDeleted = UserDataAccess.deleteDriver(driverId);
			if(isDeleted) {
				sendResponse(response, 201, STATUS_SUCCESS,"admin deleted successfully", null);
			}else {
				sendResponse(response, 402, STATUS_FAILED,"Admin deletion failed", null);
			}
		} catch (Exception e) {
			sendResponse(response, 402, STATUS_FAILED,e.getMessage(), null);
		}
	}

	private void signUp(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        String phoneNumber = request.getParameter("phoneNumber");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!password.equals(confirmPassword)) {
            sendResponse(response, 401 , STATUS_FAILED, "Password mismatch.", null);
            return;
        }
        try {
            if (UserAuthentication.signUp(name, phoneNumber, password, Constants.CUSTOMER)) {
                sendResponse(response, 200 , STATUS_SUCCESS, "Sign up successful. Please sign in.", null);
            }
        } catch (InvalidInputException e) {
            sendResponse(response, 401 , STATUS_FAILED, e.getMessage(), null);
        } catch (Exception e) {
            sendResponse(response, 402, STATUS_FAILED, e.getMessage(), null);
        }
    }

    private void signIn(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	
    	String phoneNumber = request.getParameter("phoneNumber");
        String password = request.getParameter("password");
        
        try {
            User user = UserAuthentication.login(phoneNumber, password);
            HttpSession session = request.getSession();
            
            session.setAttribute("user", user);
            session.setAttribute("userType",user.getTypeId());

            String redirectPage;
            if (user.getTypeId() == Constants.ADMIN) {
                redirectPage = "/admin/home.jsp";
            } else if (user.getTypeId() == Constants.CUSTOMER) {
                redirectPage = "/customer/home.jsp";
            } else {
                throw new IllegalStateException("Unexpected user type");
            }

            JSONObject responseData = new JSONObject();
            responseData.put("redirectPage", redirectPage);

            sendResponse(response, 200, STATUS_SUCCESS, "Sign in successful.", responseData);
        } catch (InvalidInputException e) {
            sendResponse(response, 405, STATUS_FAILED, e.getMessage(), null);
        } catch (AuthenticationException e) {
            sendResponse(response, 406, STATUS_FAILED, e.getMessage(), null);
        } catch (Exception e) {
            sendResponse(response, 407, STATUS_FAILED, e.getMessage(), null);
        }
    }

    private void signOut(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        session.invalidate();
        sendResponse(response,201,STATUS_SUCCESS, "Sign out successful.", null);
    }
    
    private void fetchLocations(HttpServletResponse response)  {
        JSONArray jsonLocations = new JSONArray(Constants.LOCATIONS);
        sendResponse(response,200,STATUS_SUCCESS,"Location fetched successfully.",jsonLocations);
    }
    
    private void searchBuses(HttpServletRequest request, HttpServletResponse response) {
        String fromLocation = request.getParameter("from");
        String toLocation = request.getParameter("to");
        String date = request.getParameter("date");
        HttpSession session = request.getSession(false);
        session.setAttribute("fromLocation", fromLocation);
        session.setAttribute("toLocation", toLocation);
        try {
            List<Bus> availableBuses = BusDataAccess.getAvailableBuses(fromLocation, toLocation, date);
            JSONArray jsonBuses = new JSONArray();
            for (Bus bus : availableBuses) {
                JSONObject busJson = new JSONObject();
                busJson.put("id", bus.getId());
                busJson.put("hasAc", bus.getHasAc());
                busJson.put("seatGroup", bus.getSeatGroup());
                busJson.put("departureTime", bus.getDepartureTime().toString());
                busJson.put("arrivalTime", bus.getArrivalTime().toString());
                jsonBuses.put(busJson);
            }
            sendResponse(response, 200, STATUS_SUCCESS, "Available buses fetched successfully.", jsonBuses);
        } catch (Exception e) {
            sendResponse(response, 501, STATUS_FAILED, e.getMessage(), null);
        }
    }
    
    private void fetchBookedSeats(HttpServletRequest request, HttpServletResponse response) {
    	String busId =request.getParameter("busId");
    	 Integer busID = Integer.parseInt(busId);
    	 HttpSession session = request.getSession(false);
     	 session.setAttribute("busId", busId);
         String date = request.getParameter("date");
         session.setAttribute("date", date);

         if (busID == null || date == null) {
        	 sendResponse(response, 501, STATUS_FAILED, "Missing busId or date parameter", null);
             return;
         }
         JSONArray bookedSeats;
		try{
			bookedSeats = new JSONArray(BusDataAccess.getBookedSeats(date, busID));
			session.setAttribute("bookedSeats", bookedSeats);
			sendResponse(response,200,STATUS_SUCCESS,"Seats fetched successfully.",bookedSeats);
		}catch (Exception e) {
			sendResponse(response, 501, STATUS_FAILED, e.getMessage(), null);
		}
    }

    private void selectedSeats(HttpServletRequest request, HttpServletResponse response) {
    	String[] selectedSeats = request.getParameterValues("selectedSeats");
    	HttpSession session = request.getSession(false);
    	JSONArray bookedSeats = (JSONArray) session.getAttribute("bookedSeats");
    	
        if (selectedSeats == null || selectedSeats.length == 0) {
            sendResponse(response, 400, "Error", "No seats selected", null);
            return;
        }
        boolean isBooked = false;
     a: for (String seat : selectedSeats) {
            for (int i = 0; i < bookedSeats.length(); i++) {
                if (bookedSeats.getString(i).equals(seat)) {
                    isBooked = true;
                    break a;
                }
            }
     }
        if (isBooked) {
            sendResponse(response, 400, STATUS_FAILED, "Seats booking failed", null);
        }else {
        	session.setAttribute("selectedSeats", selectedSeats);
            String nextPageUrl = "passengerdetails.jsp";
            sendResponse(response, 200, STATUS_SUCCESS, "Seats booked successfully", nextPageUrl);
        }
    }
    
    private void fetchStops(HttpServletRequest request, HttpServletResponse response) {
    	HttpSession session = request.getSession(false);
    	String type = request.getParameter("type");
        String location = null;
        
        if ("boarding".equals(type)) {
            location = (String) session.getAttribute("fromLocation");
        } else if ("dropping".equals(type)) {
            location = (String) session.getAttribute("toLocation");
        }
        
		try {
			JSONArray jsonStops = new JSONArray(LocationService.getStops(location));
	        sendResponse(response,200,STATUS_SUCCESS,"stops fetched successfully.",jsonStops);
		} catch (Exception e) {
			sendResponse(response, 501, STATUS_FAILED, e.getMessage(), null);
		}
    }

    private void submitSelectedPoints(HttpServletRequest request, HttpServletResponse response) {
        String boardingPoint = request.getParameter("boardingPoint");
        String droppingPoint = request.getParameter("droppingPoint");
        
        try {
            // Assume these methods return the selected seats and total amount.
        	HttpSession session = request.getSession(false);
        	String busID = (String)session.getAttribute("busId");
        	Integer busId = Integer.parseInt(busID);
        	String[] selectedSeats = (String[])session.getAttribute("selectedSeats");
        	short bordingPointId =LocationService.getLocationIdByName(boardingPoint);
			short dropingpointId =LocationService.getLocationIdByName(droppingPoint);
			
			int distance = MapUtil.getDistance(bordingPointId,dropingpointId,busId);

			double amount =BusDataAccess.getTotalAmount(selectedSeats,distance,busId);
			session.setAttribute("boardingPoint", boardingPoint);
			session.setAttribute("droppingPoint", droppingPoint);
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("selectedSeats", selectedSeats);
            jsonResponse.put("totalAmount", amount);

            sendResponse(response, 200, STATUS_SUCCESS, "Points submitted successfully.", jsonResponse);
        } catch (Exception e) {
            sendResponse(response, 501, STATUS_FAILED, e.getMessage(), null);
        }
    }


	private void generateTicket(HttpServletRequest request, HttpServletResponse response) {

        StringBuilder sb = new StringBuilder();
        try {
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        }catch(Exception e){
            sendResponse(response, 501, STATUS_FAILED, e.getMessage(), null);
        }
        String jsonString = sb.toString();

        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray passengersArray = jsonObject.getJSONArray("passengers");
        double totalAmount = jsonObject.getDouble("totalAmount");

        List<Passenger> passengers = new ArrayList<>();
        for (int i = 0; i < passengersArray.length(); i++) {
            JSONObject passengerJSON = passengersArray.getJSONObject(i);
            String seatNumber=passengerJSON.getString("seatNumber");
            String name=passengerJSON.getString("name");
            short age=(short)passengerJSON.getInt("age");
            String gender=passengerJSON.getString("gender");
            Passenger passenger = new Passenger(name,age,gender,seatNumber);
            passengers.add(passenger);
        }
        HttpSession session = request.getSession(false);
        String busID = (String)session.getAttribute("busId");
    	Integer busId = Integer.parseInt(busID);
    	String date =(String) session.getAttribute("date");
        String boardingPoint = (String) session.getAttribute("boardingPoint");
        String droppingPoint = (String) session.getAttribute("droppingPoint");
        long driverId;
		try {
			driverId = BusDataAccess.getDriverId(busId);
			System.out.println(UserContext.getUser());
	        long ticketNumber = Ticket.bookTicket(UserContext.getUser().getUserId(),busId,driverId,boardingPoint,droppingPoint,date,totalAmount,passengers);
	        JSONObject jsonResponse = new JSONObject();
	        jsonResponse.put("ticketNumber", ticketNumber);
	        jsonResponse.put("busId", busId);
	        jsonResponse.put("boardingPoint", boardingPoint);
	        jsonResponse.put("droppingPoint", droppingPoint);
	        jsonResponse.put("date", date);
	        jsonResponse.put("totalAmount", totalAmount);
	        jsonResponse.put("passengers", passengersArray);
	        sendResponse(response, 200, STATUS_SUCCESS, "Points submitted successfully.", jsonResponse);
		} catch (Exception e) {
            sendResponse(response, 501, STATUS_FAILED, e.getMessage(), null);
		}
    }
	
	private void fetchBookingHistory(HttpServletRequest request, HttpServletResponse response) {
		List<Ticket> bookingHistory;
		try {
			bookingHistory = Ticket.fetchPastTripTickets(UserContext.getUser().getUserId());   
			JSONArray historyJson = new JSONArray();
	         for (Ticket ticket : bookingHistory) {
	             JSONObject booking = new JSONObject();
	             booking.put("ticketNumber", ticket.getTicketNumber());
	             booking.put("regNumber", ticket.getBusId());
	             booking.put("bordingPoint", ticket.getBordingPoint());
	             booking.put("dropingPoint", ticket.getDropingPoint());
	             booking.put("date", ticket.getDate());
	             booking.put("amount", ticket.getAmount());
	             booking.put("statusId", ticket.getStatusId());
	             
	             historyJson.put(booking);
	         }
	         sendResponse(response, 200, STATUS_SUCCESS, "bookingHistory loaded successfully.", historyJson);
         } catch (Exception e) {
             sendResponse(response, 501, STATUS_FAILED, e.getMessage(), null);
 		}
	}
	
	private void fetchPassengerDetails(HttpServletRequest request, HttpServletResponse response) {
		String ticketNo =request.getParameter("ticketNumber");
   	 	Long ticketNumber = Long.parseLong(ticketNo);
		List<Passenger> passengerList;
        try {
            passengerList = Passenger.fetchByTicketNumber(ticketNumber);
            JSONArray passengersJson = new JSONArray();
            for (Passenger passenger : passengerList) {
                JSONObject passengerJson = new JSONObject();
                passengerJson.put("seatNumber", passenger.getSeatNumber());
                passengerJson.put("name", passenger.getName());
                passengerJson.put("age", passenger.getAge());
                passengerJson.put("gender", passenger.getGender());
                passengersJson.put(passengerJson);
            }
            sendResponse(response, 200, STATUS_SUCCESS, "Passenger details loaded successfully.", passengersJson);
        } catch (Exception e) {
            sendResponse(response, 501, STATUS_FAILED, e.getMessage(), null);
        }	
	}
	
	private void fetchUpcomingTrips(HttpServletRequest request, HttpServletResponse response) {
		List<Ticket> upcomingTrips;
		try {
			upcomingTrips = Ticket.fetchFutureTripTickets(UserContext.getUser().getUserId());   
			JSONArray upcomingTripsJson = new JSONArray();
	         for (Ticket ticket : upcomingTrips) {
	             JSONObject booking = new JSONObject();
	             booking.put("ticketNumber", ticket.getTicketNumber());
	             booking.put("regNumber", ticket.getBusId());
	             booking.put("bordingPoint", ticket.getBordingPoint());
	             booking.put("dropingPoint", ticket.getDropingPoint());
	             booking.put("date", ticket.getDate());
	             booking.put("amount", ticket.getAmount());
	             booking.put("statusId", ticket.getStatusId());
	             upcomingTripsJson.put(booking);
	         }
	         sendResponse(response, 200, STATUS_SUCCESS, "UpcomingTrips loaded successfully.", upcomingTripsJson);
         } catch (Exception e) {
             sendResponse(response, 501, STATUS_FAILED, e.getMessage(), null);
 		}
	}


	private void fetchAdmins(HttpServletRequest request, HttpServletResponse response) {
		List<User> users;
		try {
			users = UserDataAccess.getUserDetails(Constants.ADMIN);   
			JSONArray adminsJson = new JSONArray();
	         for (User user : users) {
	             JSONObject admin = new JSONObject();
	             admin.put("id", user.getUserId());
	             admin.put("name", user.getName());
	             admin.put("phoneNumber", user.getPhoneNumber());
	             adminsJson.put(admin);
	         }
	         sendResponse(response, 200, STATUS_SUCCESS, "Admins loaded successfully.", adminsJson);
         } catch (Exception e) {
             sendResponse(response, 501, STATUS_FAILED, e.getMessage(), null);
 		}
	}

	private void addAdmin(HttpServletRequest request, HttpServletResponse response) {
		String name = request.getParameter("name");
        String phoneNumber = request.getParameter("phoneNumber");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!password.equals(confirmPassword)) {
            sendResponse(response, 401 , STATUS_FAILED, "Password mismatch.", null);
            return;
        }
        try {
            if (UserAuthentication.signUp(name, phoneNumber, password, Constants.ADMIN)) {
                sendResponse(response, 200 , STATUS_SUCCESS, "Sign up successful. Please sign in.", null);
            }
        } catch (InvalidInputException e) {
            sendResponse(response, 401 , STATUS_FAILED, e.getMessage(), null);
        } catch (Exception e) {
            sendResponse(response, 402, STATUS_FAILED, e.getMessage(), null);
        }	
	}
	
	private void deleteAdmin(HttpServletRequest request, HttpServletResponse response) {
		String adminID = request.getParameter("id");
		try {
			long adminId = Long.parseLong(adminID);
			boolean isAdminDeleted = UserDataAccess.removeAdmin(adminId) ;
			if(isAdminDeleted) {
				sendResponse(response, 201, STATUS_SUCCESS,"admin deleted successfully", null);
			}else {
				sendResponse(response, 402, STATUS_FAILED,"Admin deletion failed", null);
			}
		} catch (Exception e) {
            sendResponse(response, 402, STATUS_FAILED, e.getMessage(), null);
		}
	}
	
	private void addDriver(HttpServletRequest request, HttpServletResponse response) {
		String name = request.getParameter("name");
        String phoneNumber = request.getParameter("phoneNumber");
        String licenceNumber = request.getParameter("licenceNumber");

        try {
            if (UserAuthentication.driverSignUp(name,phoneNumber,licenceNumber,Constants.DRIVER)) {
                sendResponse(response, 200 , STATUS_SUCCESS, "Driver added successfully", null);
            }
        } catch (InvalidInputException e) {
            sendResponse(response, 401 , STATUS_FAILED, e.getMessage(), null);
        } catch (Exception e) {
            sendResponse(response, 402, STATUS_FAILED, e.getMessage(), null);
        }
	}
	
    private void sendResponse(HttpServletResponse response, int statusCode, String status, String message, Object jsonData) {
        JSONObject responseStatus = new JSONObject();
        responseStatus.put("status_code", statusCode);
        responseStatus.put("status", status);
        responseStatus.put("message", message);

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("response_status", responseStatus);
        if (jsonData != null) {
            jsonResponse.put("data", jsonData);
        }

        try (PrintWriter out = response.getWriter()) {
            response.setContentType("application/json");
            out.print(jsonResponse);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

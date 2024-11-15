package com.busbooking;

import java.sql.Connection;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.customexception.MismatchException;

public class BusDataAccess {

	public static List<Bus> getAvailableBuses(String pickupLocation,String dropLocation,String inputDate) throws Exception{

		List<Bus> availablebuses = new ArrayList<Bus>(); ;
		Date date = Validation.validateAndParseDate(inputDate);
		if(date!=null) {
			short pickuplocationId =LocationService.getLocationIdByName(pickupLocation);
			short droplocationId =LocationService.getLocationIdByName(dropLocation);
	
			String str = """
					WITH CurrentLocation AS (
					   SELECT
					       b.id AS bus_id,
					       CASE
					           WHEN (? - b.created_date) % 2 = 0 THEN b.defaultlocation
					           ELSE
					               CASE
					                   WHEN r.from_id = b.defaultlocation THEN r.to_id
					                   WHEN r.to_id = b.defaultlocation THEN r.from_id
					                   ELSE NULL
					               END
					       END AS current_location
					   FROM
					       BusDetails b
					   LEFT JOIN
					       Route r ON (r.from_id = b.defaultlocation OR r.to_id = b.defaultlocation)),
					RouteDistances AS (
					    SELECT
					        rm.route_id,
					        MIN(rm.distance) FILTER (WHERE rm.location_id = ?) AS pickup_distance,
					        MIN(rm.distance) FILTER (WHERE rm.location_id = ?) AS drop_distance
					    FROM
					        RouteMapping rm
					    GROUP BY rm.route_id),
						DispatchAndArrival AS (
						    SELECT
						        brm.route_id,
						        brm.departure_time,
						        brm.departure_time + interval '1 hour' * abs(rd.drop_distance - rd.pickup_distance) / 60 AS estimated_arrival
						    FROM
						        BusRouteMapping brm
						    JOIN
						        RouteDistances rd ON brm.route_id = rd.route_id
						)
						SELECT DISTINCT
						    b.id,b.has_ac,bsgm.group_id,
							brm.departure_time,
						    brm.departure_time + interval '1 hour' * abs(rd.drop_distance - rd.pickup_distance) / 60 AS estimated_arrival
						FROM
						    BusDetails b
						JOIN
						    CurrentLocation cl ON b.id = cl.bus_id
						JOIN
						    BusRouteMapping brm ON b.id = brm.bus_id
						JOIN
							BusSeatGroupMappings bsgm ON b.id = bsgm.bus_id 
						JOIN
						    Route r ON r.id = brm.route_id
						JOIN
						    RouteMapping rm_pickup ON r.id = rm_pickup.route_id
						JOIN
						    RouteMapping rm_drop ON r.id = rm_drop.route_id
						JOIN
						    BusStatus bs ON b.status = bs.id
						JOIN
						    RouteDistances rd ON brm.route_id = rd.route_id
						WHERE
						    ((rm_pickup.location_id = ? AND rm_drop.location_id = ? AND rm_pickup.stop_number < rm_drop.stop_number AND bs.status = 'available' AND cl.current_location = r.from_id)
						    OR
						    (rm_pickup.location_id = ? AND rm_drop.location_id = ? AND rm_pickup.stop_number > rm_drop.stop_number AND bs.status = 'available' AND cl.current_location = r.to_id))
						    AND ((? = CURRENT_DATE AND brm.departure_time > CURRENT_TIME) OR (? > CURRENT_DATE))
						ORDER BY
						    estimated_arrival;
						""";
	
	
			Connection connection = DBConnection.getConnection();
			try(PreparedStatement statement =connection.prepareStatement(str)){
	
				statement.setDate(1,date);
				statement.setShort(2,pickuplocationId);
				statement.setShort(3,droplocationId);
				statement.setShort(4,pickuplocationId);
				statement.setShort(5,droplocationId);
				statement.setShort(6,pickuplocationId);
				statement.setShort(7,droplocationId);
				statement.setDate(8,date);
				statement.setDate(9,date);
	
				try(ResultSet resultSet=statement.executeQuery()){
					while(resultSet.next()){
	
						int id = resultSet.getInt("id");
						boolean hasAc = resultSet.getBoolean("has_ac");
						short seatGroupId = resultSet.getShort("group_id");
						Time departureTime = resultSet.getTime("departure_time");
						Time arrivalTime = resultSet.getTime("estimated_arrival");
	
						Bus bus =new Bus(id,hasAc,seatGroupId,departureTime,arrivalTime);
						availablebuses.add(bus);
					}
				}
			}
			catch(Exception e){
				throw new Exception("Error while getting AvailableBuses !!!  .Please go back and try again. Error details: " + e.getMessage());
				
			}
		}
		return availablebuses ;
	}

	public static int getSelectedBusIndex(List<Bus> availableBuses, Integer busId) {
		int result = -1 ;
		for (int i = 0; i < availableBuses.size(); i++) {
			Bus bus = availableBuses.get(i);
			if (bus.getId() == busId) {
				result = i ;
				break; 
			}
		}
		return result ;
	}

	public static List<String> getTotalSeats(int busId) throws Exception{
		List<String> totalSeats= new ArrayList<String>();
		String str = """
				SELECT
				 	Seats.seat_number 
				 FROM 
				 	BusSeatGroupMappings 
				 JOIN 
				 	Seats ON BusSeatGroupMappings.group_id = Seats.group_id 
				 WHERE 
				 	BusSeatGroupMappings.bus_id = ?;
				""";
		Connection connection = DBConnection.getConnection();
		try(PreparedStatement statement = connection.prepareStatement(str)){
			statement.setInt(1,busId);
			try(ResultSet resultset = statement.executeQuery()){
				while(resultset.next()){
					String seat = resultset.getString("seat_number"); 
					totalSeats.add(seat);
				}
			}
		}catch(Exception e){
			throw new Exception("Unexpected Error occured !!! Please go back and try again. Error details: " + e.getMessage());
		}
		return totalSeats;
	}

	public static List<String> getBookedSeats(String inputDate,int busId ) throws Exception{

		List<String> bookedSeats= new ArrayList<String>();
		Date date = Validation.validateAndParseDate(inputDate);
		if(date!=null) {
			String str = """
					SELECT 
						seat_number 
					FROM 
						TicketDetails td 
					JOIN 
						passangerDetails pd ON td.id = pd.booking_id 
					WHERE
					 	td.booking_date = ? AND td.bus_id = ? And td.status = ?;
					""";
			Connection connection = DBConnection.getConnection();
			try(PreparedStatement statement = connection.prepareStatement(str)){
				statement.setDate(1,date);
				statement.setInt(2,busId);
				statement.setShort(3,Constants.ACTIVE);
				try(ResultSet resultset = statement.executeQuery()){
					while(resultset.next()){
						String seatNumber = resultset.getString("seat_number"); 
						bookedSeats.add(seatNumber);
						System.out.println(seatNumber);
					}
				}
			}catch(Exception e){
				throw new Exception("Error during getting Booked Seats.Please go back and try again. Error details: " + e.getMessage());
			}
		}
		return bookedSeats;
	}

	public static void displaySeats(List<String> totalSeats, List<String> bookedSeats){
		String redColor = "\u001B[31m";
		String resetColor = "\u001B[0m";
		for (String seat : totalSeats) {
			if (bookedSeats.contains(seat)) {
				System.out.print(redColor + seat + resetColor + " ");
			} else {
				System.out.print(seat + " ");
			}
		}
	}

	public static double getTotalAmount(String[] selectedSeats,int distance,int busId) throws Exception{

		double TotalAmount = Constants.DEFAULT_DOUBLE_VALUE ;

		for(int i=0;i<selectedSeats.length;i++){
			String str = """
					SELECT 
						st.seat_type,
						bd.has_ac
					FROM 
						Seats s
					JOIN 
						SeatType st ON s.seat_type = st.id
					JOIN 
						BusSeatGroupMappings bsgm ON s.group_id = bsgm.group_id
					JOIN 
						BusDetails bd ON bsgm.bus_id = bd.id
					WHERE 
						bd.id = ? AND s.seat_number = ?;
						""";
			Connection connection = DBConnection.getConnection();
			try(PreparedStatement statement = connection.prepareStatement(str)){
				statement.setInt(1,busId);
				statement.setString(2,selectedSeats[i]);
				try(ResultSet resultset = statement.executeQuery()){

					if(resultset.next()){
						double amount = Constants.DEFAULT_DOUBLE_VALUE ;
						String seatType = resultset.getString("seat_type");
						boolean hasAc = resultset.getBoolean("has_ac");

						if(hasAc && seatType.equals("sleeper")){

							amount+= (Constants.DEFAULT_FARE + distance + Constants.AC_FARE) * Constants.SLEEPER_MULTIPLIER ;

						}else if(hasAc && seatType.equals("semi-sleeper")){

							amount+= Constants.DEFAULT_FARE + distance + Constants.AC_FARE ;

						}else if(seatType.equals("sleeper")){

							amount+= (Constants.DEFAULT_FARE + distance) * Constants.SLEEPER_MULTIPLIER;

						}else{

							amount+= Constants.DEFAULT_FARE + distance ;

						}
						TotalAmount +=amount ;
					}
				}
			}catch(Exception e){
				throw new Exception("Error during calculating TotalAmount.Please go back and try again. Error details: " + e.getMessage());
			}
		}
		return TotalAmount ;
	}

	public static long getDriverId(int busId) throws Exception {
		String str ="SELECT driver_id FROM busdrivermapping WHERE bus_id = ?;";
		long driverId = -1;

		Connection connection = DBConnection.getConnection();
		try(PreparedStatement statement =connection.prepareStatement(str)){
			statement.setInt(1,busId);
			try(ResultSet resultSet = statement.executeQuery()){
				if(resultSet.next()){
					driverId = resultSet.getLong("driver_id");
				}
			}
		}catch(Exception e){
			throw new Exception("Error while fetching driverId .Please go back and try again. Error details: " + e.getMessage());
		}
		return driverId;
	}

	public static boolean addNewBus(String reg_no,boolean has_ac,short groupId,short currentLocationId,long driverId,short routeId,String departureTime) throws Exception {
		if(Validation.isValidRegisterNumber(reg_no)) {
		LocalTime dep_time = null ;
	//	LocalTime arival_time = null ;

		if(Validation.isValidTimeFormat(departureTime)) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
			dep_time = LocalTime.parse(departureTime, formatter);
		}else {
			throw new MismatchException(" Enter ValidTime ");
		}

		String str = "INSERT INTO busdetails(reg_no,has_ac,status,defaultlocation) Values(?,?,?,?) RETURNING id ;";
		Connection connection = DBConnection.getConnection();
		try(PreparedStatement statement =connection.prepareStatement(str)){
			connection.setAutoCommit(false);
			statement.setString(1,reg_no);
			statement.setBoolean(2,has_ac);
			statement.setShort(3,Constants.AVAILABLE);
			statement.setShort(4,currentLocationId);
			
			try(ResultSet resultSet = statement.executeQuery()){
				if(resultSet.next()){
					int	busId = resultSet.getInt("id");

					str = "INSERT INTO busseatgroupmappings(bus_id,group_id) VALUES (?,?);";
					try(PreparedStatement preparedstatement =connection.prepareStatement(str)){
						preparedstatement.setInt(1,busId);
						preparedstatement.setShort(2,groupId);
						preparedstatement.executeUpdate();
						
						str = "INSERT INTO busdrivermapping(bus_id,driver_id)VALUES(?,?);";
						try(PreparedStatement insertStatement =connection.prepareStatement(str)){
							insertStatement.setInt(1,busId);
							insertStatement.setLong(2,driverId);
							insertStatement.executeUpdate();

							str = "INSERT INTO busroutemapping(bus_id,route_id,departure_time)VALUES (?,?,?);";
							try(PreparedStatement insertionStatement =connection.prepareStatement(str)){
								insertionStatement.setInt(1,busId);
								insertionStatement.setShort(2,routeId);
								insertionStatement.setObject(3,dep_time);
							//	insertionStatement.setObject(4,arival_time);
								insertionStatement.executeUpdate();
								connection.commit();
								return true ;
							}
						}
					}
				}
			}
		}catch(Exception e) {
			try{
				if(connection!=null) {
					connection.rollback();
				}
			}catch(Exception ex) {
				throw new Exception("Unexpected Error occurred !!! Please go back and try again. Error details: " + ex.getMessage());
			}

		}finally {

			if (connection != null) {
				try {
					connection.setAutoCommit(true);
				} catch (Exception exc) {
					throw new Exception("Unexpected Error occurred !!! Please go back and try again. Error details: " + exc.getMessage());
				}
			}
		}
		} else {
			throw new Exception("Enter Valid Register Number");
		}
		return false ;
	}

	public static void deleteBus(Integer busId) throws Exception {
		if(BusDataAccess.isBusIdExits(busId)) {
			String str ="UPDATE busdetails SET status = ? Where id = ?;";	

			Connection connection = DBConnection.getConnection();
			try(PreparedStatement statement =connection.prepareStatement(str)){
				statement.setInt(1,Constants.DELETED);
				statement.setInt(2,busId);
				statement.executeUpdate();
			}catch(Exception e){
				throw new Exception("Error occured while deleting bus !!! Please go back and try again. Error details: " + e.getMessage());
			}
		}
	}

	public static boolean isBusIdExits(Integer busId) throws Exception {

		String str ="SELECT 1 FROM busdetails WHERE id = ? AND status = ? ";
		boolean isExists = false;	
		Connection connection = DBConnection.getConnection();

		try(PreparedStatement statement =connection.prepareStatement(str)){
			statement.setInt(1,busId);
			statement.setShort(2,Constants.AVAILABLE);

			try(ResultSet resultset=statement.executeQuery()){			
				if(resultset.next()){

					isExists = true ;
				}
			}			
		}catch(Exception e){
			throw new Exception("Error occured while finding BusId !!! Please go back and try again. Error details: " + e.getMessage());
		}
		return isExists ;
	}

	public static List<Bus> getAllBusDetails() throws Exception {
		List<Bus> buses = null ;
		String str ="""
				SELECT 
				    bd.id,
				    bd.reg_no,
				    bd.has_ac,
				    l.name AS current_location,
				    u.name AS driver_name,
				    sd.group_name
				FROM 
				    BusDetails bd
				    INNER JOIN BusDriverMapping bdm ON bd.id = bdm.bus_id
				    INNER JOIN Users u ON bdm.driver_id = u.id
				    LEFT JOIN BusSeatGroupMappings bsgm ON bd.id = bsgm.bus_id
				    LEFT JOIN SeatGroup sd ON bsgm.group_id = sd.id
				    LEFT JOIN Locations l ON bd.defaultlocation = l.id
				WHERE 
				    bd.status = ?;
				""";
		Connection connection = DBConnection.getConnection();
		try(PreparedStatement statement =connection.prepareStatement(str)){
			statement.setShort(1,Constants.AVAILABLE);
			buses = new ArrayList<Bus>();
			try(ResultSet resultset=statement.executeQuery()){
			while(resultset.next()){
				int id = resultset.getInt("id");
				String registerNumber = resultset.getString("reg_no");
				boolean hasAc = resultset.getBoolean("has_ac");
				String currentLocation = resultset.getString("current_location");
				String driverName = resultset.getString("driver_name");
				String seatGroup = resultset.getString("group_name");
				Bus bus = new Bus(id,registerNumber,hasAc,currentLocation,driverName,seatGroup);
				buses.add(bus);
				}
			}
		}catch(Exception e){
			throw new Exception("Unexpected Error occured while getting busdetails !!! Please go back and try again. Error details: " + e.getMessage());
		}		
		return buses ;
	}

	public static void DisplayAvailableBuses(List<Bus> availableBuses) {
		for(Bus bus : availableBuses) {
			bus.displayDetails();
		}
	}

	public static String getSeatGroupById(short seatGroupId) throws Exception {
		String str = "Select group_name From seatgroup WHERE id = ? ;" ;
		String result = null ;
		Connection connection = DBConnection.getConnection();
		try(PreparedStatement statement =connection.prepareStatement(str)){
			statement.setShort(1,seatGroupId);
			try(ResultSet resultset=statement.executeQuery()){
				if(resultset.next()) {
					result = resultset.getString("group_name");
				}
			}
		}catch(Exception e) {
			throw new Exception("Error while getting GroupName .Please go back and try again. Error details: " + e.getMessage());
		}
		return result ;
	}
}

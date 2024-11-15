package com.busbooking;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class Ticket{
	
	private long ticketNumber, userId ,driverId;
	private int busId;
	private String bordingPoint,dropingPoint;
	private Date date;
	private double amount;
	private short statusId;
	
	Ticket(long ticketNumber,long userId,Integer busId,String bordingPoint,String dropingPoint,Date date,double amount,short statusId){
		this.ticketNumber=ticketNumber;
		this.userId=userId;
		this.busId=busId;
		this.bordingPoint=bordingPoint;
		this.dropingPoint=dropingPoint;
		this.date=date;
		this.amount=amount;
		this.statusId=statusId;
	}
	
	public long getTicketNumber(){
		return ticketNumber;
	}
	public long getUserId(){
		return userId;
	}
	public long getDriverId(){
		return driverId;
	}
	public int getBusId(){
		return busId;
	}
	public String getDropingPoint(){
		return dropingPoint;
	}
	public String getBordingPoint(){
		return bordingPoint;
	}
	public Date getDate(){
		return date;
	}
	public double getAmount(){
		return amount;
	}
	
	public static long bookTicket(long userId,int busId,long driverId,String bordingPoint,String dropingPoint,String inputDate,double amount,List<Passenger> passengers) throws Exception{
		Long ticketNumber = null ;
		Date date = Validation.validateAndParseDate(inputDate);
		if(date!=null) {
			String str = "INSERT INTO Ticketdetails(user_id, bus_id, driver_id, boarding_point, dropping_point, booking_date, amount) VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";	
			Connection connection = DBConnection.getConnection();
			
			try	(PreparedStatement statement =connection.prepareStatement(str)){
				connection.setAutoCommit(false);
				statement.setLong(1,userId);
				statement.setInt(2,busId);
				statement.setLong(3,driverId);
				statement.setString(4,bordingPoint);
				statement.setString(5,dropingPoint);
				statement.setDate(6,date);
				statement.setDouble(7,amount);
				
				try(ResultSet resultset =statement.executeQuery()){
					if(resultset.next()){
						ticketNumber = resultset.getLong("id");
						str ="INSERT INTO passangerdetails(booking_id,name,age,gender,seat_number) VALUES(?,?,?,?,?);";
						for(Passenger passenger : passengers) {
							try(PreparedStatement preparedStatement =connection.prepareStatement(str)){
							preparedStatement.setLong(1,ticketNumber);
							preparedStatement.setString(2,passenger.getName());
							preparedStatement.setShort(3,passenger.getAge());
							preparedStatement.setString(4,passenger.getGender());
							preparedStatement.setString(5,passenger.getSeatNumber());
							preparedStatement.executeUpdate();
							connection.commit();
							}
						}
					}else {
						connection.rollback();
					}
				}
			}catch(Exception e){
				try{
	                if(connection != null) {
	                	connection.rollback();
	                	e.printStackTrace();                }
	            }catch(SQLException exception) {
	    			throw new Exception(" Unexpected error occurred !!!. Please go back and try again. Error details: " + exception.getMessage());
	            }
			}finally{
				try{
					if(connection != null) {
	                	connection.setAutoCommit(true);
	                }
	            }catch(SQLException ex) {
	            	throw new Exception(" Unexpected error occurred !!!. Please go back and try again. Error details: " + ex.getMessage());
	            }
			}
		}
		return ticketNumber ;
	}
	
	public static List<Ticket> fetchFutureTripTickets(long userId) throws Exception{
		List<Ticket> upcomingTrips = null ;
		String str ="""
				SELECT * FROM TicketDetails
				WHERE 
					user_id = ? AND (booking_date > CURRENT_DATE)
					ORDER BY booking_date;
					""";
		
		Connection connection = DBConnection.getConnection();
		try(PreparedStatement statement =connection.prepareStatement(str)){
			statement.setLong(1,userId);
			
			try(ResultSet resultSet = statement.executeQuery()){
			upcomingTrips = new ArrayList<Ticket>();
			
			while(resultSet.next()){
				long bookingId=resultSet.getLong("id");
				int BusId = resultSet.getInt("bus_id");
				String boardingPoint = resultSet.getString("boarding_point");
				String dropingPoint = resultSet.getString("dropping_point");
				Date date = resultSet.getDate("booking_date");
				double amount  = resultSet.getDouble("amount");
				short statusId =  resultSet.getShort("status");
				
				Ticket ticket = new Ticket(bookingId, userId, BusId, boardingPoint, dropingPoint, date, amount,statusId);
				upcomingTrips.add(ticket);
				}
			}
		}catch(Exception e){
			throw new Exception(" Error occurred while fetching tickets !!!. Please go back and try again. Error details: " + e.getMessage());
		}
		return upcomingTrips ;
	}
	
	public static List<Ticket> fetchPastTripTickets(long userId) throws Exception{
		
		List<Ticket> bookingHistory = null ;
		String str = """
				SELECT * FROM TicketDetails
				WHERE 
					user_id = ? AND (booking_date <= CURRENT_DATE)
					ORDER BY booking_date;
					""";
		Connection connection = DBConnection.getConnection();
		try(PreparedStatement statement =connection.prepareStatement(str)){
			statement.setLong(1,userId);
			try(ResultSet resultSet = statement.executeQuery()){
				bookingHistory = new ArrayList<Ticket>();
				
				while(resultSet.next()){
					long bookingId=resultSet.getLong("id");
					int BusId = resultSet.getInt("bus_id");
					String boardingPoint = resultSet.getString("boarding_point");
					String dropingPoint = resultSet.getString("dropping_point");
					Date date = resultSet.getDate("booking_date");
					double amount  = resultSet.getDouble("amount");
					short statusId =  resultSet.getShort("status");
					
					Ticket ticket = new Ticket(bookingId, userId, BusId, boardingPoint, dropingPoint, date, amount,statusId);
					bookingHistory.add(ticket);
				}
			}
		}catch(Exception e){
			throw new Exception(" Error occurred while fetching tickets !!!. Please go back and try again. Error details: " + e.getMessage());
		}
		return bookingHistory;
	}
	
	public static List<Ticket> getAllBookings(long userId) throws Exception{
		
		UserDataAccess.isUserIdExists(userId,Constants.CUSTOMER);
		List<Ticket> bookings = null ;
		
		String str = """
				SELECT * FROM TicketDetails
				WHERE 
					user_id = ?
				ORDER BY booking_date;
				""";

		Connection connection = DBConnection.getConnection();
		try(PreparedStatement statement =connection.prepareStatement(str)){
			statement.setLong(1,userId);
			try(ResultSet resultSet = statement.executeQuery()){
				bookings = new ArrayList<Ticket>();
				
				while(resultSet.next()){
					long bookingId=resultSet.getLong("id");
					int BusId = resultSet.getInt("bus_id");
					String boardingPoint = resultSet.getString("boarding_point");
					String dropingPoint = resultSet.getString("dropping_point");
					Date date = resultSet.getDate("booking_date");
					double amount  = resultSet.getDouble("amount");
					short statusId =  resultSet.getShort("status");
					
					Ticket booking = new Ticket(bookingId, userId, BusId, boardingPoint, dropingPoint, date, amount,statusId);
					bookings.add(booking);
				}
			}
		}catch(Exception e){
			throw new Exception(" Error occurred while fetching tickets !!!. Please go back and try again. Error details: " + e.getMessage());
		}
		return bookings;
	}
	
	public static boolean cancelTicket(long ticketNo)throws Exception {
		String str ="UPDATE ticketdetails SET status = ? WHERE id = ? ;";
		Connection connection = DBConnection.getConnection();
		try(PreparedStatement statement =connection.prepareStatement(str)){
			statement.setLong(1,Constants.CANCEL);
			statement.setLong(2,ticketNo);
			int rowsUpdated= statement.executeUpdate();
			if (rowsUpdated > 0) {
	            return true; 
	        } else {
	            return false; 
	        }
		}catch(Exception e){
			throw new Exception(" Error occurred while cancelling tickets !!!. Please go back and try again. Error details: " + e.getMessage());
		}
	}

	public short getStatusId() {
		return statusId;
	}
}
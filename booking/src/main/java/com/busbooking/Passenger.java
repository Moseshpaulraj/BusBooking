package com.busbooking;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

class Passenger{
	
	private long id, bookingId;
	private String name ,seatNumber;
	private short age ; 
	private String gender ;
	
	Passenger (String name,short age,String gender,String seatNumber){
		this.name=name;
		this.age=age;
		this.gender=gender;
		this.seatNumber=seatNumber;
	}
	
	Passenger (long id,long bookingId,String name,short age,String gender,String seatNumber){
		this.id = bookingId;
		this.bookingId = bookingId;
		this.name = name;
		this.age = age;
		this.gender = gender;
		this.seatNumber = seatNumber;
	}
	
	public long getId(){
		return id;
	}
	
	public long getBookingId(){
		return bookingId;
	}
	
	public String getName(){
		return name;
	}
	
	public short getAge(){
		return age;
	}
	
	public String getGender(){
		return gender;
	}
	
	public String getSeatNumber(){
		return seatNumber;
	}
	
	public static List<Passenger> fetchByTicketNumber(long ticketNumber) throws Exception{
		List<Passenger> passengerList = null ;
		String str =  "SELECT * FROM passangerdetails WHERE booking_id = ?;";
		
		Connection connection = DBConnection.getConnection();
		try(PreparedStatement statement =connection.prepareStatement(str)){
			statement.setLong(1,ticketNumber);
			try(ResultSet resultSet = statement.executeQuery()){
				passengerList = new ArrayList<Passenger>();
				
				while(resultSet.next()){
					String name = resultSet.getString("name");
					String seatNumber = resultSet.getString("seat_number");
					String gender = resultSet.getString("gender");
					short age = resultSet.getShort("age");
					Passenger passenger = new Passenger(name,age,gender,seatNumber);
					passengerList.add(passenger);
				}
			}
		}catch(Exception e){
			throw new Exception(" Error occurred while fetching passenger Details !!!. Please go back and try again. Error details: " + e.getMessage());
		}
		return passengerList ;
	}
}
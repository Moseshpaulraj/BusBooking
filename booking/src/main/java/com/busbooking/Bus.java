package com.busbooking;

import java.sql.Time;

class Bus {
	
	private int id ;
	private String registerNumber ;
	private boolean hasAc ;
	private short seatGroupId ;
	private Time departureTime ;
	private Time arrivalTime ;
	private String currentLocation ;
	private String driverName ;
	private String seatType ;
	
	Bus(int id,String registerNumber,boolean hasAc,short seatGroupId){
		this.id = id ;
		this.registerNumber = registerNumber ;
		this.hasAc = hasAc ;
		this.seatGroupId = seatGroupId;
	}
	
	Bus(int id,String registerNumber,boolean hasAc,String currentLocation,String driverName,String seatType){
		this.id = id ;
		this.registerNumber = registerNumber ;
		this.hasAc = hasAc ;
		this.currentLocation = currentLocation ;
		this.driverName = driverName ;
		this.seatType = seatType;
		
	}

	Bus(int id,boolean hasAc,short seatGroupId,Time departureTime,Time arrivalTime){
		this.id = id;
		this.hasAc = hasAc;
		this.seatGroupId = seatGroupId;
		this.departureTime = departureTime;
		this.arrivalTime = arrivalTime;
	}
	
	public int getId(){
		return id;
	}
	
	public String getRegisterNumber(){
		return registerNumber;
	}
	
	public boolean getHasAc(){
		return hasAc;
	}
	
	public short getSeatGroupId() {
		return seatGroupId;
	}
	
	public String getSeatGroup() throws Exception {
		 String group = BusDataAccess.getSeatGroupById(seatGroupId);
		 return group ;
	}
	
	public void displayDetails() {
		
		System.out.println("--------------------------------------------");
		System.out.println("id : "+ id +"  RegisterNumber : "+  registerNumber );
		if(hasAc){
			System.out.println("  A\\C Bus");
		}else{
			System.out.println("  Non A\\C Bus");
		}
		System.out.println("--------------------------------------------");
	}

	public Time getArrivalTime() {
		return arrivalTime;
	}

	public Time getDepartureTime() {
		return departureTime;
	}

	public String getCurrentLocation() {
		return currentLocation;
	}

	public String getDriverName() {
		return driverName;
	}

	public String getSeatType() {
		return seatType;
	}
}
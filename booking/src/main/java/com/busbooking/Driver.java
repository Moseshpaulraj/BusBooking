package com.busbooking;

class Driver extends User {
	
	private String licenceNumber ;
	
	public Driver(long userId,String name,String phoneNumber,String licenceNumber) {
		super(userId,name,phoneNumber);
		this.licenceNumber=licenceNumber;
	}
	
	public Driver(long userId,String name,String phoneNumber,short typeId,short statusId,String licenceNumber){
		super(userId,name,phoneNumber,typeId,statusId);
		this.licenceNumber=licenceNumber;
	}
	
	public String getLicenceNumber() {
		return licenceNumber ;
	}
	
	public void displayDetails() {
		System.out.println("--------------------------------------------");
		System.out.println("  Name : "+ getName()+"  PhoneNumber : "+getPhoneNumber());
		System.out.println(" LicenceNumber : "+ licenceNumber+"  UserId : "+getUserId());
		System.out.println("--------------------------------------------");
	}
	
}
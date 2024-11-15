package com.busbooking;

public class User{

	private long userId;
	private String name ,phoneNumber ;
	private short typeId ,statusId;
	
	
	public User(long userId,String name,String phoneNumber){
		this.userId = userId;
		this.name = name;
		this.phoneNumber = phoneNumber;
	}
	
	public User(long userId,String name,String phoneNumber,short typeId,short statusId){
		this.userId = userId;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.typeId = typeId;
		this.statusId = statusId;
	}
	
	public long getUserId(){
		return userId ;
	}
	
	public String getName(){
		return name ;
	}
	
	public String getPhoneNumber(){
		return phoneNumber ;
	}
	
	public short getTypeId(){
		return typeId ;
	}
	
	public short getStatusId(){
		return statusId ;
	}

	public void displayDetails() {
		System.out.println("-----------------------------------------------------");
		System.out.println("UserId : "+ getUserId()+" Name : "+ getName()+"  PhoneNumber : "+getPhoneNumber());
		System.out.println("-----------------------------------------------------");
	}
	
}
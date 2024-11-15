package com.busbooking;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection{
	
	private static Connection connection ;
	
	public static synchronized Connection getConnection() throws Exception {
		try {
	        if (connection == null) {
	            Class.forName("org.postgresql.Driver");
	            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/bus_booking", "moses", "54321");
	        }
		}catch(Exception e){
			throw new Exception("Unexpected Error occured !!! Please go back and try again. Error details: " + e.getMessage());
		}  
        return connection;  
    }
}
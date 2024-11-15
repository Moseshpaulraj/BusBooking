package com.busbooking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants{
	
	public static final byte SIGNUP = 1, LOGIN = 2, EXIT = 3 ;
	public static final byte BOOK_BUS = 1,BOOKING_DETAILS = 2 ,BOOKING_HISTORY = 3 , SIGNOUT = 4 ;
	public static final byte BUS_DETAILS = 1, DRIVER_DETAILS = 2, CUSTOMER_DETAILS = 3, ADMIN_DETAILS = 4, LOGOUT = 5;
	public static final byte ADD_NEW_BUS = 1,DELETE_BUS = 2,VIEW_BUS = 3,BACK = 4;
	public static final byte ADD_NEW_DRIVER = 1, DELETE_DRIVER=2 ,	All_DRIVER_DETAILS =3 ;
	public static final byte VIEW_CUSTOMER = 1, VIEW_BOOKINGS=2 ,	DELETE_CUSTOMER =3 ;
	public static final byte ADD_NEW_ADMIN =1,REMOVE_ADMIN=2,ALL_ADMIN_DETAILS= 3 ;
	
	public static final short CUSTOMER = 1 ;
	public static final short ADMIN = 2 ;
	public static final short DRIVER = 3 ;
	public static final short MINIMUM = 1 ;
	public static final short ACTIVE = 1 ;
	public static final short RESIGNED = 2 ;
	public static final short AVAILABLE = 1 ;
	public static final short UNAVAILABLE = 2 ;
	public static final short DELETED = 3 ;
	public static final short CANCEL = 2 ;
	
	public static final String MAIN_MENU = "\n  ONLINE BUS BOOKING \n\n1 --> SIGNUP \n2 --> LOGIN \n3 --> EXIT " ;
	public static final String USER_MENU = "\n1 --> BOOK BUS \n2 --> BOOKING DETAILS \n3 --> BOOKING HISTORY \n4 --> LOGOUT " ;
	public static final String ADMIN_MENU = "\n1 --> BUS DETAILS \n2 --> DRIVER DETAILS \n3 --> CUSTOMER DETAILS \n4 --> ADMIN DETAILS \n5 --> LOGOUT";
	public static final String BUS_DETAILS_MENU = "\n1 --> ADD NEW BUS \n2 --> DELETE BUS \n3 --> VIEW BUS \n4 --> BACK ";
	public static final String DRIVER_DETAILS_MENU = "\n1 --> ADD NEW DRIVER\n2 --> DELETE DRIVER\n3 --> All DRIVER DETAILS \n4 --> BACK ";
	public static final String CUSTOMER_DETAILS_MENU = "\n1 --> VIEW ALL CUSTOMER \n2 --> VIEW BOOKINGS \n3 --> DELETE CUSTOMER \n4 --> BACK ";
	public static final String ADMIN_DETAILS_MENU = "1 --> ADD NEW ADMIN\n2 --> REMOVE ADMIN \n3 --> VIEW ADMIN DETAILS \n4 --> BACK ";
	
	public static final String ENTER_OPTION = "\nEnter Your Option : " ;
	public static final String INVALID_OPTION = "\nINVALID OPTION !!! " ;
	public static final String INVALID_INPUT = "\nINVALID INPUT !!! " ;
	
	public static final String REDCOLOR = "";
	public static final String RESETCOLOR = "";

	public static final List<String> DEPATURELOCATIONS = new ArrayList<>(Arrays.asList("chennai","salem","coimbatore","madurai","tirunelveli","tenkasi","thoothukudi","kanyakumari"));
	public static final List<String> LOCATIONS = new ArrayList<>(Arrays.asList("chennai","salem","erode","coimbatore","trichy","dindigul","madurai","tirunelveli","tenkasi","thoothukudi","kanyakumari"));
	
	
	public static final short DEFAULT_FARE = 300;
	public static final short AC_FARE = 200;
	public static final double SLEEPER_MULTIPLIER = 1.5; 
	public static final double DEFAULT_DOUBLE_VALUE = 0.0;
	public static final byte DEFAULT_VALUE = 0;
	
	public static final byte MAX_AGE = 99;
	public static final byte MIN_AGE = 1;
	
	public static final String SUCCESS = "success";
	public static final String FAILED = "failed";
	
}

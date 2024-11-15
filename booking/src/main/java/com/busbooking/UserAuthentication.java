package com.busbooking;

import com.customexception.AuthenticationException;
import com.customexception.MismatchException;

public class UserAuthentication {
	
	public static boolean signUp(String name,String phoneNumber,String password,short typeId) throws Exception {
		
		if(Validation.isValidName(name)) {
			if(Validation.isValidPhoneNo(phoneNumber)) {
				if(!UserDataAccess.isPhoneNumberExists(phoneNumber)) {
					if(Validation.isValidPassword(password)) {
						UserDataAccess.addUser(name,phoneNumber,password,typeId);
						return true;
					}else {
						throw new MismatchException(Constants.REDCOLOR +"\nInvalid password !!! "+Constants.RESETCOLOR);
					}
				}else{
					throw new AuthenticationException(Constants.REDCOLOR +" \nThis Phone Number Is Already Linked to an Existing Account.Please LOGIN !!! "+Constants.RESETCOLOR);
				}
			}else {
				throw new MismatchException(Constants.REDCOLOR +"\nInvalid PhoneNumber !!! "+Constants.RESETCOLOR);
			}
		}else {
			throw new MismatchException(Constants.REDCOLOR +"\nInvalid Name !!! "+Constants.RESETCOLOR);
		}
	}

	public static User login(String phoneNumber,String password) throws Exception {
		
		if(Validation.isValidPhoneNo(phoneNumber)) {
			if(UserDataAccess.isPhoneNumberExists(phoneNumber)) {
				if(Validation.isValidPassword(password)) {
					User user = UserDataAccess.getUserdetails(phoneNumber,password);
					if(user!=null) {
						UserContext.setUser(user);
						return user ;
					}else{
						throw new AuthenticationException(Constants.REDCOLOR +"\nINCORRECT PASSWORD !!! "+Constants.RESETCOLOR);
					}
				}else {
					throw new MismatchException(Constants.REDCOLOR +"\nInvalid PASSWORD !!! "+Constants.RESETCOLOR);
				}
			}else {
				throw new AuthenticationException(Constants.REDCOLOR +"\nPlease register your number before logging in !!! "+Constants.RESETCOLOR);
			}
		}else {
			throw new MismatchException(Constants.REDCOLOR +"\nInvalid PhoneNumber !!! "+Constants.RESETCOLOR);
		}
	}

public static boolean driverSignUp(String name,String phoneNumber,String licenceNumber,short typeId) throws Exception {
		
		if(Validation.isValidName(name)) {
			if(Validation.isValidPhoneNo(phoneNumber)) {
				if(!UserDataAccess.isPhoneNumberExists(phoneNumber)) {
					if(Validation.isValidLicenceNo(licenceNumber)) {
						UserDataAccess.addDriver(name,phoneNumber,licenceNumber,typeId);
						return true;
					}else {
						throw new MismatchException(Constants.REDCOLOR +"\nInvalid licenceNumber !!! "+Constants.RESETCOLOR);
					}
				}else{
					throw new AuthenticationException(Constants.REDCOLOR +" \nThis Phone Number Is Already Linked to an Existing Account.Please LOGIN !!! "+Constants.RESETCOLOR);
				}
			}else {
				throw new MismatchException(Constants.REDCOLOR +"\nInvalid PhoneNumber !!! "+Constants.RESETCOLOR);
			}
		}else {
			throw new MismatchException(Constants.REDCOLOR +"\nInvalid Name !!! "+Constants.RESETCOLOR);
		}
	}
}

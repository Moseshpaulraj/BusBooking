package com.busbooking;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Validation{

	public static boolean isValidName(String input){
		Pattern pattern =Pattern.compile("^[A-Za-z][A-Za-z\\s]{3,29}$");
		Matcher matcher = pattern.matcher(input);
		boolean result = matcher.matches();
		return result ;
	}

	public static boolean isValidPhoneNo(String input){
		Pattern pattern =Pattern.compile("^(0|91)?[6-9][0-9]{9}$");
		Matcher matcher = pattern.matcher(input);
		boolean result = matcher.matches();
		return result ;
	}

	public static boolean isValidPassword(String input){
		Pattern pattern =Pattern.compile("^(?!\\s)(?=.*[A-Z])(?=.*[0-9])(?=.*[\\W]).{8,100}(?<!\\s)$");
		Matcher matcher = pattern.matcher(input);
		boolean result = matcher.matches();
		return result ;
	}
	public static boolean isValidDate(String input){
		Pattern pattern =Pattern.compile("^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$");
		Matcher matcher = pattern.matcher(input);
		boolean result = matcher.matches();
		return result ;
	}

	public static boolean isValidRegisterNumber(String input) {
		Pattern pattern =Pattern.compile("^[A-Z]{2}\\d{2}[A-Z]{2}\\d{4}$");
		Matcher matcher = pattern.matcher(input);
		boolean result = matcher.matches();
		return result ;
	}

	public static boolean isValidLicenceNo(String input) {
		Pattern pattern =Pattern.compile("^[A-Z]{2}\\d{13}$");
		Matcher matcher = pattern.matcher(input);
		boolean result = matcher.matches();
		return result ;
	}

	public static boolean isValidTimeFormat(String input) {
		Pattern pattern =Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$");
		Matcher matcher = pattern.matcher(input);
		boolean result = matcher.matches();
		return result ;
	}
	
	public static Date validateAndParseDate(String inputDate) {
		Date sqlDate = null ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		if(Validation.isValidDate(inputDate)) {
			java.util.Date utilDate = null ;
			
			try {
				utilDate = dateFormat.parse(inputDate);
			}catch(ParseException e) {
				e.printStackTrace();
			}			
			 if (utilDate != null) {
		            sqlDate = new Date(utilDate.getTime());
		            return sqlDate;
		        }
			}
		return sqlDate ;
	}
}
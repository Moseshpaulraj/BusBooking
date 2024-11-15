package com.busbooking;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Scanner;

import com.customexception.InvalidInputException;

class InputValidator{
	
	private Scanner scanner = new Scanner(System.in);
	
	public Byte getNextByte() throws InvalidInputException {
		Byte value = null ;
		System.out.println(Constants.ENTER_OPTION);
		if(scanner.hasNextByte()) {
			value = scanner.nextByte();
			return value ;
		}else {
			scanner.nextLine();
			throw new InvalidInputException(" InvalidInput ");	
		}
	}
	
	public Integer getNextInt() throws InvalidInputException {
		Integer value = null ;
		if(scanner.hasNextInt()) {
			value = scanner.nextInt();
			return value ;
		}else {
			scanner.nextLine();
			throw new InvalidInputException(" InvalidInput ");
		}
	
	}

	public Long getNextLong() throws InvalidInputException {
		Long value = null ;
		if(scanner.hasNextLong()) {
			value = scanner.nextLong();
			return value ;
		}else {
			scanner.nextLine();
			throw new InvalidInputException(" InvalidInput ");		}
	}

	public Short getNextShort() throws InvalidInputException {
		Short value = null ;
		if(scanner.hasNextByte()) {
			value = scanner.nextShort();
			return value ;
		}else {
			scanner.nextLine();
			throw new InvalidInputException(" InvalidInput ");
		}
	}

	public Date validateAndParseDate(String inputDate) {
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
	
	public boolean validateSelectedSeats(String[] selectedSeats,List<String> bookedSeats){
		boolean result = true ;
		for (String seat : selectedSeats) {
            if (!bookedSeats.contains(seat)) {
                result = true ;
            } else {
                result = false ;
				return result ;
            }
        }
		return result ;
	}

}
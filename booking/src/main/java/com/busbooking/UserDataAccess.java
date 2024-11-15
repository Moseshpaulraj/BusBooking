package com.busbooking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.customexception.UnsupportedOperation;
public class UserDataAccess {

	public static boolean isPhoneNumberExists(String phoneNumber) throws Exception {

		boolean isExists = false ;
		String str = " SELECT id FROM Users WHERE phone_number = ? ; ";

		Connection connection = DBConnection.getConnection();
		try(PreparedStatement statement =connection.prepareStatement(str)){
			statement.setString(1, phoneNumber);
			try(ResultSet resultSet = statement.executeQuery()){
				isExists = resultSet.next();
			}
		}catch(Exception e){
			throw new Exception(" Error occurred while checking PhoneNumber !!!. Please go back and try again. Error details: " + e.getMessage());
		}
		return isExists ;
	}

	public static void addUser(String name,String phoneNumber,String password,short userTypeId) throws Exception{
		String str ="INSERT INTO users(name,phone_number,type_id,status_id) VALUES(?,?,?,?)RETURNING id;";
		Connection connection = DBConnection.getConnection();

		try(PreparedStatement statement =connection.prepareStatement(str)){
			connection.setAutoCommit(false);
			statement.setString(1,name);
			statement.setString(2,phoneNumber);
			statement.setShort(3,userTypeId);
			statement.setShort(4,Constants.CUSTOMER);

			try(ResultSet resultSet = statement.executeQuery()){
				if(resultSet.next()){

					long userId = resultSet.getLong("id");
					str ="INSERT INTO userpasswordmapping(user_id,password) VALUES(?,?);";
					try(PreparedStatement passwordStatement =connection.prepareStatement(str)){
						passwordStatement.setLong(1,userId);
						passwordStatement.setString(2,password);
						passwordStatement.executeUpdate();
						connection.commit();
					}
				}else {
					connection.rollback();
				}
			}
		}catch(Exception e){
			try{
				if(connection != null) {
					connection.rollback();
					throw new Exception(" Unexpected error occurred !!!. Please go back and try again. Error details: " + e.getMessage());
				}
			}catch(SQLException exception) {
				throw new Exception(" unexpected Error occured  !!!. Please go back and try again. Error details: " + exception.getMessage());
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

	public static User getUserdetails(String phoneNumber,String password) throws Exception {
		User user = null ;
		String str ="""
				SELECT 
					u.id,u.name,u.type_id,u.status_id
				FROM 
					users u 
				JOIN 
					UserPasswordMapping upm ON u.id =upm.user_id 
				WHERE 
					u.phone_number =? 
				AND 
					upm.password =? 
				AND 
					u.status_id =?;
				""";
		Connection connection = DBConnection.getConnection();
		try(PreparedStatement statement = connection.prepareStatement(str)){
			statement.setString(1,phoneNumber);
			statement.setString(2,password);
			statement.setShort(3,Constants.ACTIVE);
			try(ResultSet resultSet = statement.executeQuery()){			
				if(resultSet.next()){
					long userId = resultSet.getLong("id");
					String name = resultSet.getString("name");
					short typeId = resultSet.getShort("type_id");
					short statusId = resultSet.getShort("status_id");

					user = new User(userId,name,phoneNumber,typeId,statusId);
				}
			}
		}catch(Exception e){
			throw new Exception(" Error occurred while fetching UserDetails !!!. Please go back and try again. Error details: " + e.getMessage());
		}
		return user ;
	}

	public static List<User> getUserDetails(short userTypeId) throws Exception{
		List<User> users = null ;
		String query ="SELECT id, name, phone_number FROM Users WHERE status_id = ? AND type_id = ? ;";
		Connection connection = DBConnection.getConnection();

		try(PreparedStatement statement =connection.prepareStatement(query)){
			statement.setShort(1, Constants.ACTIVE);
			statement.setShort(2, userTypeId);

			try(ResultSet resultset=statement.executeQuery()){
				users = new ArrayList<User>();

				while(resultset.next()){
					long id = resultset.getLong("id");
					String name = resultset.getString("name");
					String phone = resultset.getString("phone_number");
					User admin = new User(id,name,phone);
					users.add(admin);
				}
			}
		}catch(Exception e){
			throw new Exception(" Error occurred while fetching UserDetails !!!. Please go back and try again. Error details: " + e.getMessage());
		}
		return users ;
	}

	public static boolean isUserIdExists(Long adminId,short userTypeId) throws Exception {
		String str ="SELECT 1 FROM users WHERE id = ? And type_id = ? AND status_id =? ";
		boolean isExists = false;
		Connection connection = DBConnection.getConnection();

		try(PreparedStatement statement =connection.prepareStatement(str)){
			statement.setLong(1,adminId);
			statement.setShort(2,userTypeId);
			statement.setShort(3, Constants.ACTIVE);

			try(ResultSet resultset=statement.executeQuery()){			
				if(resultset.next()){

					isExists = true ;
				}
			}			
		}catch(Exception exception){
			throw new Exception(" Error occurred while verifying UserDetails !!!. Please go back and try again. Error details: " + exception.getMessage());
		}
		return isExists ;
	}

	public static boolean removeAdmin(long adminId) throws Exception{

		UserDataAccess.isUserIdExists(adminId,Constants.ADMIN);
		boolean isRemoved = false ;
		String str ="UPDATE users SET status_id = ? WHERE type_id = ? AND id = ?;";
		Connection connection = DBConnection.getConnection();
		try(PreparedStatement statement =connection.prepareStatement(str)){
			if(adminId!=170 && adminId != UserContext.getUser().getUserId()){
				statement.setShort(1,Constants.RESIGNED);
				statement.setShort(2,Constants.ADMIN);
				statement.setLong(3,adminId);
				statement.executeUpdate();
				isRemoved = true ;
			}else {
				throw new UnsupportedOperation("Removing the master admin or yourself as the current admin is not permitted");
			}
		}catch(Exception e){
			throw new Exception(" Error occurred while removing admin !!!. Please go back and try again. Error details: " + e.getMessage());
		}
		return isRemoved ;
	}

	public static void removeCoustomer(Long customerId) throws Exception {
		UserDataAccess.isUserIdExists(customerId,Constants.CUSTOMER );
		String str ="UPDATE users SET status_id = ? WHERE type_id = ? AND id = ?;";
		Connection connection = DBConnection.getConnection();
		try(PreparedStatement statement =connection.prepareStatement(str)){
			statement.setShort(1,Constants.ACTIVE);
			statement.setShort(2,Constants.CUSTOMER);
			statement.setLong(3,customerId);
			statement.executeUpdate();	
		}catch(Exception e){
			throw new Exception(" Error occurred while removing customer !!!. Please go back and try again. Error details: " + e.getMessage());
		}
	}

	public static List<Driver> getAvailableDriver() throws Exception{
		List<Driver> availableDrivers = null ;
		String str = """
				SELECT 
					dd.user_id , u.name, u.phone_number, dd.licence_number 
				FROM 
					DriverDetails dd 
				JOIN 
					Users u ON dd.user_id = u.id 
				LEFT JOIN 
					BusDriverMapping bdm ON dd.user_id = bdm.driver_id 
				WHERE 
					bdm.bus_id IS NULL AND u.status_id = ?;
				""";
		Connection connection = DBConnection.getConnection();
		try (PreparedStatement statement = connection.prepareStatement(str)) {
			statement.setShort(1,Constants.ACTIVE);
			try(ResultSet resultSet = statement.executeQuery()){
				availableDrivers = new ArrayList<Driver>();
				while (resultSet.next()) {
					long id = resultSet.getLong("user_id");
					String name = resultSet.getString("name");
					String phoneNumber = resultSet.getString("phone_number");
					String licenceNumber = resultSet.getString("licence_number");
					Driver driver = new Driver(id, name, phoneNumber, licenceNumber);
					availableDrivers.add(driver);
				}
			}
		}catch(Exception e){
			throw new Exception(" Error occurred while fetching Drivers !!!. Please go back and try again. Error details: " + e.getMessage());
		}
		return availableDrivers;
	}

	public static Long getDriverId(int busId) throws Exception {
		String str ="SELECT driver_id FROM busdrivermapping WHERE bus_id = ?;";
		long driverId = -1;

		Connection connection = DBConnection.getConnection();
		try(PreparedStatement statement =connection.prepareStatement(str)){
			statement.setInt(1,busId);
			try(ResultSet resultSet = statement.executeQuery()){
				if(resultSet.next()){
					driverId = resultSet.getLong("driver_id");
				}
			}
		}catch(Exception e){
			throw new Exception(" Error occurred while fetching DriverId !!!. Please go back and try again. Error details: " + e.getMessage());
		}
		return driverId;
	}

	public static boolean deleteDriver(Long driverId) throws Exception {
		if(UserDataAccess.isDriverIdExists(driverId)) {
			String str ="UPDATE users SET status_id = ? WHERE type_id = ? AND id = ?;";
			Connection connection = DBConnection.getConnection();
			try(PreparedStatement statement =connection.prepareStatement(str)){
				statement.setShort(1,Constants.RESIGNED);
				statement.setShort(2,Constants.DRIVER);
				statement.setLong(3,driverId);
				statement.executeUpdate();
				return true ;
			}catch(Exception e){
				throw new Exception(" Error occurred while deleting Driver !!!. Please go back and try again. Error details: " + e.getMessage());
			}
		} 
		return false ;
	}

	public static void addDriver(String name, String phoneNumber , String licenceNumber,short typeId) throws Exception {
		Connection connection = null ;
		String str = "INSERT INTO users(name, phone_number, type_id, status_id) VALUES (?, ?, ?, ?) RETURNING id;";
		connection = DBConnection.getConnection();
		try (PreparedStatement statement = connection.prepareStatement(str)) {
			connection.setAutoCommit(false);
			statement.setString(1, name);
			statement.setString(2, phoneNumber);
			statement.setShort(3, typeId);	    
			statement.setShort(4,Constants.ACTIVE);
			try (ResultSet resultSet = statement.executeQuery()) {			    	
				if (resultSet.next()) {
					long userId = resultSet.getLong("id");	
					str = "INSERT INTO driverdetails(user_id, licence_number) VALUES (?, ?);";
					try(PreparedStatement preparedStatement = connection.prepareStatement(str)){
						preparedStatement.setLong(1, userId);
						preparedStatement.setString(2, licenceNumber);
						preparedStatement.executeUpdate();
					}
				}else {
					connection.rollback();
				}
			}
			connection.commit();
		} catch(Exception e){
			try{
				if(connection != null) {
					connection.rollback();
					throw new Exception(" Unexpected error occurred !!!. Please go back and try again. Error details: " + e.getMessage());
				}
			}catch(SQLException exception) {
				throw new Exception(" unexpected Error occured  !!!. Please go back and try again. Error details: " + exception.getMessage());
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

	public static boolean isDriverIdExists(Long driverId) throws Exception {
		String str ="SELECT 1 FROM users WHERE id = ? And type_id = ? AND status_id =? ";
		boolean isExists = false;	
		Connection connection = DBConnection.getConnection();

		try(PreparedStatement statement =connection.prepareStatement(str)){
			statement.setLong(1,driverId);
			statement.setShort(2,Constants.DRIVER);
			statement.setShort(3,Constants.ACTIVE);

			try(ResultSet resultset=statement.executeQuery()){			
				if(resultset.next()){

					isExists = true ;
				}
			}			
		}catch(Exception exception){
			throw new Exception(" Error occurred while verifying driverId !!!. Please go back and try again. Error details: " + exception.getMessage());
		}
		return isExists ;
	}

	public static List<Driver> getAllDriverDetails() throws Exception {
		List<Driver> drivers = null ;
		String str ="""
					SELECT 
						user_id , name, phone_number, licence_number 
					FROM 
						DriverDetails 
					JOIN 
						Users ON user_id = id 
					WHERE 
						status_id = ? ;
				""";
		Connection connection = DBConnection.getConnection();
		try(PreparedStatement statement =connection.prepareStatement(str)){
			statement.setShort(1,Constants.ACTIVE);
			try(ResultSet resultset=statement.executeQuery()){
				drivers = new ArrayList<Driver>();
				while(resultset.next()){
					long id = resultset.getLong("user_id");
					String name = resultset.getString("name");
					String phoneNumber = resultset.getString("phone_number");
					String licenceNumber = resultset.getString("licence_number");
					Driver driver = new Driver(id,name,phoneNumber,licenceNumber);
					drivers.add(driver);
				}
			}
		}catch(Exception e){
			throw new Exception(" Error occurred while fetching DriverDetails !!!. Please go back and try again. Error details: " + e.getMessage());
		}		
		return drivers ;
	}
}

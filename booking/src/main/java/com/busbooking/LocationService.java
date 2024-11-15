package com.busbooking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


class LocationService{
	
	public static short getLocationIdByName(String name) throws Exception{
		String str = "Select id From locations WHERE name = ? ;" ;
		short id = 0 ;
		Connection connection = DBConnection.getConnection();
		try(PreparedStatement statement =connection.prepareStatement(str)){
			statement.setString(1,name);
			try(ResultSet resultset=statement.executeQuery()){
				if(resultset.next()) {
					id = resultset.getShort("id");
				}
			}
		}catch(Exception e) {
			throw new Exception("Error during getting locationId .Please go back and try again. Error details: " + e.getMessage());
		}
		return id ;
	}
	
	public static String getLocationById(short id) throws Exception{
		String str = "Select name From locations WHERE id = ? ;" ;
		String result = null ;
		Connection connection = DBConnection.getConnection();
		try(PreparedStatement statement =connection.prepareStatement(str)){
			statement.setShort(1,id);
			try(ResultSet resultset=statement.executeQuery()){
				if(resultset.next()) {
					result = resultset.getString("name");
				}
			}
		}catch(Exception e) {
			throw new Exception("Error while getting location .Please go back and try again. Error details: " + e.getMessage());
		}
		return result ;
	}
	
	public static ArrayList<String> getStops(String district) throws Exception{
		String str = """
					SELECT 
						loc_stop.name,loc_stop.id 
					FROM 
						PickupAndDrop pd 
					JOIN 
						Locations loc_district ON pd.district = loc_district.id 
					JOIN 
						Locations loc_stop ON pd.stops = loc_stop.id 
					WHERE 
						loc_district.name = ?;
				""";
		ArrayList<String> stops = new ArrayList<String>();
		Connection connection = DBConnection.getConnection();
		try(PreparedStatement statement =connection.prepareStatement(str)){
			statement.setString(1,district);
			try(ResultSet resultSet = statement.executeQuery()){
				while(resultSet.next()){
					String stop = resultSet.getString("name");
					stops.add(stop);
				}
			}
		}catch(Exception e){
			throw new Exception("Error while getting stops.Please go back and try again. Error details: " + e.getMessage());
		}
		return stops;
	}
	
	public static List<Route> getRoutes(String location) throws Exception {
		short locationId = getLocationIdByName(location);
		String str = """
					SELECT 
						rm.route_id, STRING_AGG(l.name, ', ' ORDER BY rm.stop_number) AS location_names
					FROM 
						RouteMapping rm
					JOIN 
						Route r ON rm.route_id = r.id AND (r.from_id = ? OR r.to_id = ?)
					JOIN 
						Locations l ON rm.location_id = l.id
					GROUP BY rm.route_id
					ORDER BY rm.route_id;
				""";
		List<Route> routes = null ;
		Connection connection = DBConnection.getConnection();
		try(PreparedStatement statement =connection.prepareStatement(str)){
			statement.setShort(1, locationId);
			statement.setShort(2, locationId);
			try(ResultSet resultSet = statement.executeQuery()){
				routes = new ArrayList<Route>();
				while(resultSet.next()){
					short id = resultSet.getShort("route_id");
					String locations = resultSet.getString("location_names");
					Route route = new Route(id,locations);
					routes.add(route);
				}
			}
		}catch(Exception e) {
			throw new Exception("Error while getting routes . Please go back and try again. Error details: " + e.getMessage());
		}
		return routes ;
	}
	
	public static void displayRoutes(Map<Short,String> routes) {
		System.out.println("---------------------------------------------------");
		for (Map.Entry<Short, String> entry : routes.entrySet()) {
            System.out.println(entry.getKey() + ". " + entry.getValue());
        }
		System.out.println("---------------------------------------------------");
	}
	
}

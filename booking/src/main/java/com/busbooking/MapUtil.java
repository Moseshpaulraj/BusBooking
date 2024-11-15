package com.busbooking;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

class MapUtil{
	
/*	public static void displayMap(){
		
			System.out.println("\n                                _____*Chennai");
			System.out.println("                            ___/            |");
			System.out.println("                       _ salem *            |");
			System.out.println("                     _/                     |");
			System.out.println("                  Erode*                   /");
			System.out.println("                 /                        /");
			System.out.println("             ___/                     *Trichy");
			System.out.println("     Coimbatore*                       _/");
			System.out.println("             \\                        |");
			System.out.println("              \\__                     |");
			System.out.println("                 \\                    |");
			System.out.println("                  Dindigul*           |");
			System.out.println("                   \\                  |");
			System.out.println("                    Madurai*         /");
			System.out.println("                     \\             _/");
			System.out.println("                      \\            *Tuticorin");
			System.out.println("                   tenkasi*    *Tirunelveli");
			System.out.println("                        \\         /");
			System.out.println("                   kanyakumari*  /");
			System.out.println("                          \\_ _ _/\n");
	}
	
	
 /*	public static int getDistance(String location1 , String location2){
		
		double[] coordinates1 = getLangLong(location1);
		double[] coordinates2 = getLangLong(location2);
		int distance = findDistance(coordinates1[0],coordinates1[1],coordinates2[0],coordinates2[1]);
		return distance ;
		
	}
	
	public static double[] getLangLong(String address) {
		double[] coordinates = new double[2];
        try
		{
            String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8);
            String apiKey = "426efb6d50msh82dda003509b8a9p1a1899jsnd99b8d3beaa6";
			String apiUrl = "https://trueway-geocoding.p.rapidapi.com/Geocode?address="+encodedAddress+"&language=en&country=in";
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("X-RapidAPI-Key", apiKey)
                .header("X-RapidAPI-Host", "trueway-geocoding.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject jsonObject = new JSONObject(response.body());
            if (jsonObject.has("results") && jsonObject.getJSONArray("results").length() > 0) {
                JSONObject location = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("location");
                double latitude = location.getDouble("lat");
                double longitude = location.getDouble("lng");
				coordinates[0] = latitude ;
				coordinates[1] = longitude ;
            } else {
                System.out.println("No results found for the given address.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return coordinates ;
		}
		
		public static int findDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
		
		double deltaLat = Math.toRadians(latitude2 - latitude1);
		double deltaLon = Math.toRadians(longitude2 - longitude1);
		double haversineFormula = Math.pow(Math.sin(deltaLat / 2), 2) +
									Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) *
									Math.pow(Math.sin(deltaLon / 2), 2);
		double centralAngle = 2 * Math.atan2(Math.sqrt(haversineFormula), Math.sqrt(1 - haversineFormula));
		double earthRadiusKm = 6371;
		int distance = (int)Math.round(earthRadiusKm * centralAngle);
		return distance ;
		
		}*/
	
	public static int getDistance( short location1 , short location2,int busId) throws Exception {
		int distance = 0;
		String str ="""
					WITH BusRoute AS (
				    SELECT route_id
				    FROM BusRouteMapping
				    WHERE bus_id = ?
					),
					PickupPoint AS (
					    SELECT rm.route_id, rm.stop_number AS pickup_stop, rm.distance AS pickup_distance
					    FROM RouteMapping rm
					    JOIN BusRoute br ON rm.route_id = br.route_id
					    WHERE rm.location_id = ?
					),
					DropPoint AS (
					    SELECT rm.route_id, rm.stop_number AS dropoff_stop, rm.distance AS dropoff_distance
					    FROM RouteMapping rm
					    JOIN BusRoute br ON rm.route_id = br.route_id
					    WHERE rm.location_id = ?
					),
					CommonRoute AS (
					    SELECT p.route_id, p.pickup_distance, d.dropoff_distance
					    FROM PickupPoint p
					    JOIN DropPoint d ON p.route_id = d.route_id
					)
					SELECT ABS(pickup_distance - dropoff_distance) AS total_distance
					FROM CommonRoute;
				""";
		
		Connection connection = DBConnection.getConnection();
		try (PreparedStatement statement = connection.prepareStatement(str)) {
			statement.setInt(1,busId);
			statement.setShort(2,location1);
			statement.setShort(3,location2);
			try(ResultSet resultSet = statement.executeQuery()){
				while (resultSet.next()) {
					distance = resultSet.getInt("total_distance");
				}
			}
		}catch(Exception e){
			throw new Exception(" Error occurred while finding distance !!!. Please go back and try again. Error details: " + e.getMessage());
		}
		return distance ;
	}


	public static void displayLocations(List<String> locationList) {
		
		System.out.println("-----------------------------");
		for(byte i=0;i<locationList.size();i++) {
			System.out.println((i+1)+"."+locationList.get(i));
		}
		System.out.println("-----------------------------");
	}

}
package com.busbooking;

public class Route {
	private short id;
    private String locations;

    public Route(short id, String locations) {
        this.id = id;
        this.locations = locations;
    }

    public short getId() {
        return id;
    }

    public String getLocations() {
        return locations;
    }
}

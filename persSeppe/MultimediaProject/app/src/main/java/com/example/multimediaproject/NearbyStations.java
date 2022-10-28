package com.example.multimediaproject;

public class NearbyStations {
    private double longitude;
    private double latitude;
    private String station;
    private double distance;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public double getDistance(){
        return distance;
    }

    public void setDistance(double distance){
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "NearbyStations{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", station='" + station + '\'' +
                ", distance=" + distance +
                '}';
    }
}

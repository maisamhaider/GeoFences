package com.example.geofences.models;

public class HistoryModel {
    int id;
    String title;
    String latitude;
    String longitude;
    String circleSize;
    String state;

    public HistoryModel(int id,
                        String title,
                        String latitude,
                        String longitude,
                        String circleSize,
                        String state) {
        this.id = id;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.circleSize = circleSize;
        this.state = state;
    }

    public HistoryModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCircleSize() {
        return circleSize;
    }

    public void setCircleSize(String circleSize) {
        this.circleSize = circleSize;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}

package profile.manager.location.based.auto.profile.changer.models;

public class HistoryModel {
    int id;
    String title;
    String latitude;
    String longitude;
    String circleSize;
    String state;
    long expirationTime;
    String expirationEnd;
    String geofenceType;
    String dateNow;


    public HistoryModel(int id,
                        String title,
                        String latitude,
                        String longitude,
                        String circleSize,
                        long expirationTime,
                        String state,
                        String expirationEnd,
                        String geofenceType,
                        String dateNow) {
        this.id = id;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.circleSize = circleSize;
        this.state = state;
        this.expirationTime = expirationTime;
        this.expirationEnd = expirationEnd;
        this.geofenceType = geofenceType;
        this.dateNow = dateNow;
    }

    public HistoryModel() {
    }

    public String getExpirationEnd() {
        return expirationEnd;
    }

    public void setExpirationEnd(String expirationEnd) {
        this.expirationEnd = expirationEnd;
    }

    public String getGeofenceType() {
        return geofenceType;
    }

    public void setGeofenceType(String geofenceType) {
        this.geofenceType = geofenceType;
    }

    public String getDateNow() {
        return dateNow;
    }

    public void setDateNow(String dateNow) {
        this.dateNow = dateNow;
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

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }
}

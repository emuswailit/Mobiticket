package ke.co.mobiticket.mobiticket.pojos;

public class FareDetails {
    private String from;
    private String to;
    private String current_station;
    private String current_fare;
    private String lat;
    private String lng;
    private String speed;
    private String created;
    private String updated;
    private String status;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCurrent_station() {
        return current_station;
    }

    public void setCurrent_station(String current_station) {
        this.current_station = current_station;
    }

    public String getCurrent_fare() {
        return current_fare;
    }

    public void setCurrent_fare(String current_fare) {
        this.current_fare = current_fare;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


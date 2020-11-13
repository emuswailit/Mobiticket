package ke.co.mobiticket.mobiticket.pojos;

public class FareDetails {
    private String travel_from;
    private String travel_to;
    private String current_station;
    private String current_fare;
    private String lat;
    private String lng;
    private String speed;
    private String created;
    private String trip_number;
    private String updated;
    private String fare_status;

    public String getTrip_number() {
        return trip_number;
    }

    public void setTrip_number(String trip_number) {
        this.trip_number = trip_number;
    }

    public String getTravel_from() {
        return travel_from;
    }

    public void setTravel_from(String travel_from) {
        this.travel_from = travel_from;
    }

    public String getTravel_to() {
        return travel_to;
    }

    public void setTravel_to(String travel_to) {
        this.travel_to = travel_to;
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

    public String getFare_status() {
        return fare_status;
    }

    public void setFare_status(String fare_status) {
        this.fare_status = fare_status;
    }
}


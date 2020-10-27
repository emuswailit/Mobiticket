package ke.co.mobiticket.mobiticket.retrofit.requests;

public class ServerSearchVehiclesRequest {
    private String access_token;
    private String action;
    private String travel_from;
    private String travel_to;

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setTravel_from(String travel_from) {
        this.travel_from = travel_from;
    }

    public void setTravel_to(String travel_to) {
        this.travel_to = travel_to;
    }
}

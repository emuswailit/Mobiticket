package ke.co.mobiticket.mobiticket.retrofit.responses;

import ke.co.mobiticket.mobiticket.pojos.Route;

public class RouteDetailsResponse {
    private String response_code;
    private String response_message;
    private Route route;

    public String getResponse_code() {
        return response_code;
    }

    public void setResponse_code(String response_code) {
        this.response_code = response_code;
    }

    public String getResponse_message() {
        return response_message;
    }

    public void setResponse_message(String response_message) {
        this.response_message = response_message;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}

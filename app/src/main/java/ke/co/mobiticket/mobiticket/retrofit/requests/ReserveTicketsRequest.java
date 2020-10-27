package ke.co.mobiticket.mobiticket.retrofit.requests;

import java.util.List;

import ke.co.mobiticket.mobiticket.pojos.Passenger;

public class ReserveTicketsRequest {
    private String access_token;
    private String action;
    private List<Passenger> ticket;

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setTicket(List<Passenger> ticket) {
        this.ticket = ticket;
    }
}

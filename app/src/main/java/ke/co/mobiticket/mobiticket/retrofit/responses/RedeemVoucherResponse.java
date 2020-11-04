package ke.co.mobiticket.mobiticket.retrofit.responses;

import java.util.List;

import ke.co.mobiticket.mobiticket.pojos.Ticket;

public class RedeemVoucherResponse {
    private String response_message;
    private String response_code;
    private String [] customer_message;
    private List<Ticket> ticket;



    public String getResponse_message() {
        return response_message;
    }

    public String getResponse_code() {
        return response_code;
    }

    public void setResponse_message(String response_message) {
        this.response_message = response_message;
    }

    public void setResponse_code(String response_code) {
        this.response_code = response_code;
    }

    public String[] getCustomer_message() {
        return customer_message;
    }

    public void setCustomer_message(String[] customer_message) {
        this.customer_message = customer_message;
    }

    public List<Ticket> getTicket() {
        return ticket;
    }

    public void setTicket(List<Ticket> ticket) {
        this.ticket = ticket;
    }
}

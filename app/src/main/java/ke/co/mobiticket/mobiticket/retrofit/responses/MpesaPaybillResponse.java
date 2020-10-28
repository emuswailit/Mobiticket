package ke.co.mobiticket.mobiticket.retrofit.responses;

import java.util.List;

import ke.co.mobiticket.mobiticket.pojos.Ticket;

public class MpesaPaybillResponse {
    private String response_message;
    private String response_code;
    private String amount;
    private String[] customer_message;
    private List<Ticket> ticket;

    public String getResponse_message() {
        return response_message;
    }

    public String getResponse_code() {
        return response_code;
    }

    public String getAmount() {
        return amount;
    }

    public String[] getCustomer_message() {
        return customer_message;
    }

    public List<Ticket> getTicket() {
        return ticket;
    }
}

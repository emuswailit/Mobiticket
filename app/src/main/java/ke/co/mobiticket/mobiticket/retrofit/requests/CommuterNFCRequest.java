package ke.co.mobiticket.mobiticket.retrofit.requests;

public class CommuterNFCRequest {
    private String access_token;
    private String action;
    private String reference_number;
    private String payment_method;
    private String card_data;


    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setReference_number(String reference_number) {
        this.reference_number = reference_number;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public void setCard_data(String card_data) {
        this.card_data = card_data;
    }
}

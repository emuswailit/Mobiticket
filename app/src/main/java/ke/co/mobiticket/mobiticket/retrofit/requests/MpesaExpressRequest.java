package ke.co.mobiticket.mobiticket.retrofit.requests;

public class MpesaExpressRequest {
    private String access_token;
    private String action;
    private String reference_number;
    private String payment_method;
    private String mpesa_phone_number;


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

    public void setMpesa_phone_number(String mpesa_phone_number) {
        this.mpesa_phone_number = mpesa_phone_number;
    }
}

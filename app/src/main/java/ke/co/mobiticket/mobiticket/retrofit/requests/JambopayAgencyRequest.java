package ke.co.mobiticket.mobiticket.retrofit.requests;

public class JambopayAgencyRequest {
    private String access_token;
    private String action;
    private String reference_number;
    private String payment_method;
    private String jambopay_agency_username;
    private String jambopay_agency_password;


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

    public void setJambopay_agency_username(String jambopay_agency_username) {
        this.jambopay_agency_username = jambopay_agency_username;
    }

    public void setJambopay_agency_password(String jambopay_agency_password) {
        this.jambopay_agency_password = jambopay_agency_password;
    }
}

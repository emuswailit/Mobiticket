package ke.co.mobiticket.mobiticket.retrofit.requests;

public class JambopayWalletRequest {
    private String access_token;
    private String action;
    private String reference_number;
    private String payment_method;
    private String jambopay_wallet_username;
    private String jambopay_wallet_password;


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

    public void setJambopay_wallet_username(String jambopay_wallet_username) {
        this.jambopay_wallet_username = jambopay_wallet_username;
    }

    public void setJambopay_wallet_password(String jambopay_wallet_password) {
        this.jambopay_wallet_password = jambopay_wallet_password;
    }
}

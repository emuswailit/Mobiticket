package ke.co.mobiticket.mobiticket.retrofit.requests;

public class InitiatePasswordResetRequest {
    private String action;
    private String email_address;

    public void setAction(String action) {
        this.action = action;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }
}

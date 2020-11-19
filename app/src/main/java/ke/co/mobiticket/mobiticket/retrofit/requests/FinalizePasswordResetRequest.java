package ke.co.mobiticket.mobiticket.retrofit.requests;

public class FinalizePasswordResetRequest {
    private String action;
    private String email_address;
    private String token;
    private String new_pin;

    public void setAction(String action) {
        this.action = action;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setNew_pin(String new_pin) {
        this.new_pin = new_pin;
    }
}

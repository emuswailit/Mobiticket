package ke.co.mobiticket.mobiticket.retrofit.requests;

public class PinlessLoginRequest {
    private String access_token;
    private String action;
    private String phone_number;

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}

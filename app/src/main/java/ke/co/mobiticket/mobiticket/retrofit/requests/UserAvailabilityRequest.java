package ke.co.mobiticket.mobiticket.retrofit.requests;

public class UserAvailabilityRequest {
    private String action;
    private String phone_number;

    public void setAction(String action) {
        this.action = action;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}

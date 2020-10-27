package ke.co.mobiticket.mobiticket.retrofit.requests;

public class ServerValidateOTPRequest {
    private String action;
    private String phone_number;
    private String otp;

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}

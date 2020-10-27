package ke.co.mobiticket.mobiticket.retrofit.responses;

public class ServerValidateOTPResponse {
    private String response_message;
    private String response_code;
    private String otp;
    private String phone_number;

    public String getResponse_message() {
        return response_message;
    }

    public String getResponse_code() {
        return response_code;
    }

    public String getOtp() {
        return otp;
    }

    public String getPhone_number() {
        return phone_number;
    }
}

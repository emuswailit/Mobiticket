package ke.co.mobiticket.mobiticket.retrofit.responses;

public class FinalizePasswordResetResponse {
    private String response_code;
    private String response_message;
    private String phone_number;
    private String email_address;
    private String pin;

    public String getResponse_code() {
        return response_code;
    }

    public String getResponse_message() {
        return response_message;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getEmail_address() {
        return email_address;
    }

    public String getPin() {
        return pin;
    }
}


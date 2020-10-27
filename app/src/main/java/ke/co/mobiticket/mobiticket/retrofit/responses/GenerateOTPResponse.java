package ke.co.mobiticket.mobiticket.retrofit.responses;

public class GenerateOTPResponse {
    private String response_message;
    private String response_code;
    private String generated;
    private String expiry;

    public String getResponse_message() {
        return response_message;
    }

    public String getResponse_code() {
        return response_code;
    }

    public String getGenerated() {
        return generated;
    }

    public String getExpiry() {
        return expiry;
    }
}

package ke.co.mobiticket.mobiticket.retrofit.responses;

public class UserAvailabilityResponse {
    private String response_code;
    private String response_message;
    private String phone_number;
    private Boolean exists;

    public String getResponse_code() {
        return response_code;
    }

    public String getResponse_message() {
        return response_message;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public Boolean getExists() {
        return exists;
    }
}

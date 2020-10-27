package ke.co.mobiticket.mobiticket.retrofit.responses;

public class ServerRegisterResponse {
    String response_code;
    String response_message;

    public String getResponse_code() {
        return response_code;
    }

    public String getResponse_message() {
        return response_message;
    }

    public void setResponse_code(String response_code) {
        this.response_code = response_code;
    }

    public void setResponse_message(String response_message) {
        this.response_message = response_message;
    }
}

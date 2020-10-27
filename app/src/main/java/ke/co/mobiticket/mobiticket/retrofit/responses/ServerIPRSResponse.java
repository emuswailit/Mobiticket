package ke.co.mobiticket.mobiticket.retrofit.responses;

public class ServerIPRSResponse {
    String response_code;
    String response_message;
    String first_name;
    String middle_name;
    String last_name;
    String date_of_birth;
    String gender;
    String id_number;

    public String getMiddle_name() {
        return middle_name;
    }

    public String getResponse_code() {
        return response_code;
    }

    public String getResponse_message() {
        return response_message;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public String getGender() {
        return gender;
    }

    public String getId_number() {
        return id_number;
    }
}

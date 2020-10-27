package ke.co.mobiticket.mobiticket.retrofit.requests;

public class ServerIPRSRequest {
    private String action;
    private String id_number;
    private String year_of_birth;

    public void setAction(String action) {
        this.action = action;
    }

    public void setId_number(String id_number) {
        this.id_number = id_number;
    }

    public void setYear_of_birth(String year_of_birth) {
        this.year_of_birth = year_of_birth;
    }
}

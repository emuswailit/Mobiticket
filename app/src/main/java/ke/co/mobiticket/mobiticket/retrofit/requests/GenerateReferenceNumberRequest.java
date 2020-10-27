package ke.co.mobiticket.mobiticket.retrofit.requests;

public class GenerateReferenceNumberRequest {
    private String access_token;
    private String action;


    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setAction(String action) {
        this.action = action;
    }
}

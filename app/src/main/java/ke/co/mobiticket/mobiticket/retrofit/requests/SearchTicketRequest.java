package ke.co.mobiticket.mobiticket.retrofit.requests;

public class SearchTicketRequest {
    private String access_token;
    private String action;
    private String keywords;


    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}

package ke.co.mobiticket.mobiticket.retrofit.requests;

public class WriteCardRequest {
    private String access_token;
    private String action;
    private String card_type;
    private String wallet_username;
    private String wallet_password;

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public void setWallet_username(String wallet_username) {
        this.wallet_username = wallet_username;
    }

    public void setWallet_password(String wallet_password) {
        this.wallet_password = wallet_password;
    }
}

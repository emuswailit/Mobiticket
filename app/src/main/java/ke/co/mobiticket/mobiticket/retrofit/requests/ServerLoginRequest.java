package ke.co.mobiticket.mobiticket.retrofit.requests;

public class ServerLoginRequest {
    private String action;
    private String username;
    private String password;

    public void setAction(String action) {
        this.action = action;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

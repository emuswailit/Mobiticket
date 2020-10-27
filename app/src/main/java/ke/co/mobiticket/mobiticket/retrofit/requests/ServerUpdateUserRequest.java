package ke.co.mobiticket.mobiticket.retrofit.requests;

public class ServerUpdateUserRequest {
    private String action;
    private String email_address;
    private String password;
    private String street_address="";
    private String city="";
    private String country="Kenya";
    private String id;
    private String access_token;

    public void setId(String id) {
        this.id = id;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setStreet_address(String street_address) {
        this.street_address = street_address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}



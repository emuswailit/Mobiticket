package ke.co.mobiticket.mobiticket.retrofit.responses;

import java.util.List;

import ke.co.mobiticket.mobiticket.pojos.ConductVehicle;
import ke.co.mobiticket.mobiticket.pojos.DriveVehicle;
import ke.co.mobiticket.mobiticket.pojos.OwnedVehicle;

public class ServerLoginResponse {
    private String response_code;
    private String response_message;
    private String id;
    private String access_token;
    private String first_name;
    private String middle_name;
    private String last_name;
    private String wallet_balance;
    private String date_of_birth;
    private String id_number;
    private String phone_number;
    private String email_address;
    private String street_address;
    private String city;
    private String country;
    private String created;
    private String modified;
    private String status;
    private List<OwnedVehicle> owned;
    private List<DriveVehicle> drive;
    private List<ConductVehicle> conduct;

    public String getWallet_balance() {
        return wallet_balance;
    }

    public String getCreated() {
        return created;
    }

    public String getModified() {
        return modified;
    }

    public String getStatus() {
        return status;
    }

    public String getResponse_code() {
        return response_code;
    }

    public String getResponse_message() {
        return response_message;
    }

    public String getId() {
        return id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getDate_of_birth() {
        return date_of_birth;
    }

    public String getId_number() {
        return id_number;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getEmail_address() {
        return email_address;
    }

    public String getStreet_address() {
        return street_address;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public List<OwnedVehicle> getOwned() {
        return owned;
    }

    public List<DriveVehicle> getDrive() {
        return drive;
    }

    public List<ConductVehicle> getConduct() {
        return conduct;
    }
}

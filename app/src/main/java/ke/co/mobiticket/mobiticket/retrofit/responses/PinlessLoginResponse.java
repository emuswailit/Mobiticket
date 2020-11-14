package ke.co.mobiticket.mobiticket.retrofit.responses;

import java.security.acl.Owner;
import java.util.List;

import ke.co.mobiticket.mobiticket.pojos.Charge;
import ke.co.mobiticket.mobiticket.pojos.Conduct;
import ke.co.mobiticket.mobiticket.pojos.Conductor;
import ke.co.mobiticket.mobiticket.pojos.Drive;
import ke.co.mobiticket.mobiticket.pojos.Driver;
import ke.co.mobiticket.mobiticket.pojos.Expense;
import ke.co.mobiticket.mobiticket.pojos.FareDetails;
import ke.co.mobiticket.mobiticket.pojos.HybridVehicle;
import ke.co.mobiticket.mobiticket.pojos.Operator;
import ke.co.mobiticket.mobiticket.pojos.Owned;
import ke.co.mobiticket.mobiticket.pojos.Ticket;

public class PinlessLoginResponse {
    private String response_code;
    private String response_message;
    private String id;
    private String first_name;
    private String last_name;
    private String middle_name;
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

List<HybridVehicle> owned;
List<HybridVehicle> drive;
List<HybridVehicle> conduct;


    public String getResponse_code() {
        return response_code;
    }

    public String getResponse_message() {
        return response_message;
    }

    public String getId() {
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getMiddle_name() {
        return middle_name;
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

    public String getCreated() {
        return created;
    }

    public String getModified() {
        return modified;
    }

    public String getStatus() {
        return status;
    }

    public List<HybridVehicle> getOwned() {
        return owned;
    }

    public List<HybridVehicle> getDrive() {
        return drive;
    }

    public List<HybridVehicle> getConduct() {
        return conduct;
    }
}

package ke.co.mobiticket.mobiticket.retrofit.responses;

import java.util.List;

import ke.co.mobiticket.mobiticket.pojos.Charge;
import ke.co.mobiticket.mobiticket.pojos.Conductor;
import ke.co.mobiticket.mobiticket.pojos.Driver;
import ke.co.mobiticket.mobiticket.pojos.Expense;
import ke.co.mobiticket.mobiticket.pojos.FareDetails;
import ke.co.mobiticket.mobiticket.pojos.Operator;
import ke.co.mobiticket.mobiticket.pojos.Owner;
import ke.co.mobiticket.mobiticket.pojos.Ticket;
import ke.co.mobiticket.mobiticket.pojos.Vehicle;

public class ServerSearchVehiclesResponse {
    private String response_code;
    private String response_message;

    private List<Vehicle> vehicle;

    public String getResponse_code() {
        return response_code;
    }

    public String getResponse_message() {
        return response_message;
    }

    public List<Vehicle> getVehicle() {
        return vehicle;
    }
}

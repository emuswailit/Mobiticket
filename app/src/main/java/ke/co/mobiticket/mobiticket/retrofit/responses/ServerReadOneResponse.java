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

public class ServerReadOneResponse {
    private String response_code;
    private String response_message;
    private String id;
    private String registration_number;
    private String msisdn;
    private String email_address;
    private String routes;
    private String office_city;
    private String type;
    private Operator operator;
    private Owner owner;
    private Driver driver;
    private Conductor conductor;
    private FareDetails fare_details;
    private List<Charge> charge;
    private List<Expense> expense;
    private List<Ticket> ticket;

    public String getResponse_code() {
        return response_code;
    }

    public void setResponse_code(String response_code) {
        this.response_code = response_code;
    }

    public String getResponse_message() {
        return response_message;
    }

    public void setResponse_message(String response_message) {
        this.response_message = response_message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegistration_number() {
        return registration_number;
    }

    public void setRegistration_number(String registration_number) {
        this.registration_number = registration_number;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    public String getRoutes() {
        return routes;
    }

    public void setRoutes(String routes) {
        this.routes = routes;
    }

    public String getOffice_city() {
        return office_city;
    }

    public void setOffice_city(String office_city) {
        this.office_city = office_city;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public Conductor getConductor() {
        return conductor;
    }

    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
    }

    public FareDetails getFare_details() {
        return fare_details;
    }

    public void setFare_details(FareDetails fare_details) {
        this.fare_details = fare_details;
    }

    public List<Charge> getCharge() {
        return charge;
    }

    public void setCharge(List<Charge> charge) {
        this.charge = charge;
    }

    public List<Expense> getExpense() {
        return expense;
    }

    public void setExpense(List<Expense> expense) {
        this.expense = expense;
    }

    public List<Ticket> getTicket() {
        return ticket;
    }

    public void setTicket(List<Ticket> ticket) {
        this.ticket = ticket;
    }
}

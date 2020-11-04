package ke.co.mobiticket.mobiticket.retrofit.responses;

import java.util.List;

import ke.co.mobiticket.mobiticket.pojos.Ticket;

public class WriteCardResponse {
    private String response_message;
    private String response_code;
    private String card_data;
    private String phone_number;
    private String email_address;
    private String card_type;
    private String id_number;
    private String card_number;
    private String requires_password;
    private String password_amount;
    private String card_name;
    private String currency_code;
    private String current_balance;

    public String getResponse_message() {
        return response_message;
    }

    public String getResponse_code() {
        return response_code;
    }

    public String getCard_data() {
        return card_data;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getEmail_address() {
        return email_address;
    }

    public String getCard_type() {
        return card_type;
    }

    public String getId_number() {
        return id_number;
    }

    public String getCard_number() {
        return card_number;
    }

    public String getRequires_password() {
        return requires_password;
    }

    public String getPassword_amount() {
        return password_amount;
    }

    public String getCard_name() {
        return card_name;
    }

    public String getCurrency_code() {
        return currency_code;
    }

    public String getCurrent_balance() {
        return current_balance;
    }
}

package ke.co.mobiticket.mobiticket.retrofit.requests;

public class FinalizePasswordResetRequest {
    private String action;
    private String phone_number;
    private String current_pin;
    private String new_pin;
    private String confirm_new_pin;

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getCurrent_pin() {
        return current_pin;
    }

    public void setCurrent_pin(String current_pin) {
        this.current_pin = current_pin;
    }

    public String getNew_pin() {
        return new_pin;
    }

    public String getConfirm_new_pin() {
        return confirm_new_pin;
    }

    public void setConfirm_new_pin(String confirm_new_pin) {
        this.confirm_new_pin = confirm_new_pin;
    }

    public void setNew_pin(String new_pin) {
        this.new_pin = new_pin;
    }
}

package ke.co.mobiticket.mobiticket.pojos;

import android.widget.EditText;

public class PassengerInput {
    EditText etFirstName;
    EditText etLastName;
    EditText etPhone;
    EditText etEmail;

    public EditText getEtFirstName() {
        return etFirstName;
    }

    public void setEtFirstName(EditText etFirstName) {
        this.etFirstName = etFirstName;
    }

    public EditText getEtLastName() {
        return etLastName;
    }

    public void setEtLastName(EditText etLastName) {
        this.etLastName = etLastName;
    }

    public EditText getEtPhone() {
        return etPhone;
    }

    public void setEtPhone(EditText etPhone) {
        this.etPhone = etPhone;
    }

    public EditText getEtEmail() {
        return etEmail;
    }

    public void setEtEmail(EditText etEmail) {
        this.etEmail = etEmail;
    }
}

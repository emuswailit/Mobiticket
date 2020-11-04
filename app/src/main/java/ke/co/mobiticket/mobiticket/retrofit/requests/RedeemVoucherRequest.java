package ke.co.mobiticket.mobiticket.retrofit.requests;

public class RedeemVoucherRequest {
    private String access_token;
    private String action;
    private String reference_number;
    private String payment_method;
    private String voucher_number;
    private String security_pin;


    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getReference_number() {
        return reference_number;
    }

    public void setReference_number(String reference_number) {
        this.reference_number = reference_number;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(String payment_method) {
        this.payment_method = payment_method;
    }

    public String getVoucher_number() {
        return voucher_number;
    }

    public void setVoucher_number(String voucher_number) {
        this.voucher_number = voucher_number;
    }

    public String getSecurity_pin() {
        return security_pin;
    }

    public void setSecurity_pin(String security_pin) {
        this.security_pin = security_pin;
    }
}

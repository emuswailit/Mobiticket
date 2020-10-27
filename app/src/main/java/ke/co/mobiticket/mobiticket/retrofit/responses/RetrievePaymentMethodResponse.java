package ke.co.mobiticket.mobiticket.retrofit.responses;

import java.util.List;

import ke.co.mobiticket.mobiticket.pojos.PaymentMethod;

public class RetrievePaymentMethodResponse {
    private String response_message;
    private String response_code;
    private List<PaymentMethod> paymentmethod;



    public String getResponse_message() {
        return response_message;
    }

    public String getResponse_code() {
        return response_code;
    }

    public List<PaymentMethod> getPaymentmethod() {
        return paymentmethod;
    }
}

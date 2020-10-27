package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.GenerateReferenceNumberRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.RetrievePaymentMethodsRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.GenerateReferenceNumberResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.RetrievePaymentMethodResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrievePaymentMethodsInterface {
    @POST("paymentmethod")
    Call<RetrievePaymentMethodResponse> retrievePaymentMethods(@Body RetrievePaymentMethodsRequest request);
}

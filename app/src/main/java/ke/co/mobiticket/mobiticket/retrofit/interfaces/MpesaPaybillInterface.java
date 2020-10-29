package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.MpesaExpressRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.MpesaPaybillRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.MpesaExpressResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.MpesaPaybillResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MpesaPaybillInterface {

    @POST("ticket/")
    Call<MpesaPaybillResponse> mpesaPaybillPayment(@Body MpesaPaybillRequest request);
}
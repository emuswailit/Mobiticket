package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.ConfirmReservationRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.MpesaExpressRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.ConfirmReservationResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.MpesaExpressResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MpesaExpressInterface {

    @POST("ticket/")
    Call<MpesaExpressResponse> mpesaExpressPayment(@Body MpesaExpressRequest request);
}
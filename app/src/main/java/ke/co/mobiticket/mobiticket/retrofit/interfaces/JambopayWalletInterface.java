package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.JambopayWalletRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.MpesaExpressRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.JambopayWalletResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.MpesaExpressResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface JambopayWalletInterface {

    @POST("ticket/")
    Call<JambopayWalletResponse> jambopayWalletPayment(@Body JambopayWalletRequest request);
}
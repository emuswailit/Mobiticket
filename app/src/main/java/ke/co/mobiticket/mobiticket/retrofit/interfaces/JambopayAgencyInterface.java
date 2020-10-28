package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.JambopayAgencyRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.JambopayWalletRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.JambopayAgencyResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.JambopayWalletResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface JambopayAgencyInterface {

    @POST("ticket/")
    Call<JambopayAgencyResponse> jambopayAgencyPayment(@Body JambopayAgencyRequest request);
}
package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.CommuterNFCRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.ConfirmReservationRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.CommuterNFCResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.ConfirmReservationResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface CommuterNFCInterface {

    @POST("ticket/")
    Call<CommuterNFCResponse> verifyCommuterCard(@Body CommuterNFCRequest request);
}
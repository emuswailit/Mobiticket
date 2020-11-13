package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.CommuterNFCRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.UserAvailabilityRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.CommuterNFCResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.UserAvailabilityResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserAvailabilityInterface {

    @POST("user/")
    Call<UserAvailabilityResponse> checkUserExists(@Body UserAvailabilityRequest request);
}
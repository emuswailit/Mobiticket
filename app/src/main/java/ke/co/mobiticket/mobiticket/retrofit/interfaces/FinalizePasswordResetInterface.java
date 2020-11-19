package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.FinalizePasswordResetRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.InitiatePasswordResetRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.FinalizePasswordResetResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.InitiatePasswordResetResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FinalizePasswordResetInterface {
    @POST("user")
    Call<FinalizePasswordResetResponse> finalizePasswordReset(@Body FinalizePasswordResetRequest request);
}

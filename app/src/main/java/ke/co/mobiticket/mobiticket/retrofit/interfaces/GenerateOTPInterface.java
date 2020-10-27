package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.ServerGenerateOTPRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.GenerateOTPResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GenerateOTPInterface {
    @POST("user")
    Call<GenerateOTPResponse> generateOTP(@Body ServerGenerateOTPRequest request);
}

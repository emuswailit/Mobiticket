package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.ServerValidateOTPRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerValidateOTPResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ValidateOTPInterface {
    @POST("user" +
            "")
    Call<ServerValidateOTPResponse> validateOTP(@Body ServerValidateOTPRequest request);
}

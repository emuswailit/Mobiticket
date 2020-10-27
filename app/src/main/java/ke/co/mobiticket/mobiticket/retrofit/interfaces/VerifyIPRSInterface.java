package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.pojos.User;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerIPRSRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerIPRSResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface VerifyIPRSInterface {

    @POST("user")
    Call<ServerIPRSResponse> verifyIPRS(@Body ServerIPRSRequest request);
}
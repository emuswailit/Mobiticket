package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.ServerIPRSRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerRegisterRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerIPRSResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerRegisterResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterInterface {

    @POST("user/")
    Call<ServerRegisterResponse> registerUser(@Body ServerRegisterRequest request);
}
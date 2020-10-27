package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.ServerLoginRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerRegisterRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerLoginResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerRegisterResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginInterface {

    @POST("user/")
    Call<ServerLoginResponse> loginUser(@Body ServerLoginRequest request);
}
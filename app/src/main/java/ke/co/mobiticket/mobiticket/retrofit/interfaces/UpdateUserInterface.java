package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.ServerRegisterRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerUpdateUserRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerRegisterResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerUpdateUserResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UpdateUserInterface {

    @POST("user/")
    Call<ServerUpdateUserResponse> updateUser(@Body ServerUpdateUserRequest request);
}
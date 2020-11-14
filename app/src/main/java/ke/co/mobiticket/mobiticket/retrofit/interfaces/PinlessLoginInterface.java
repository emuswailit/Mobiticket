package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.PinlessLoginRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerReadOneRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.PinlessLoginResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerReadOneResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PinlessLoginInterface {

    @POST("user/")
    Call<PinlessLoginResponse> retrieveData(@Body PinlessLoginRequest request);
}
package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.ServerIPRSRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerReadOneRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerIPRSResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerReadOneResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ReadOneInterface {

    @POST("vehicle/")
    Call<ServerReadOneResponse> readOne(@Body ServerReadOneRequest request);
}
package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.JambopayAgencyRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.WriteCardRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.JambopayAgencyResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.WriteCardResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface WriteCardInterface {

@POST("user/")
    Call<WriteCardResponse> writeCard(@Body WriteCardRequest request);
        }
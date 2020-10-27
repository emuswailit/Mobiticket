package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.GenerateReferenceNumberRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerGenerateOTPRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.GenerateOTPResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.GenerateReferenceNumberResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GenerateReferenceNumberInterface {
    @POST("ticket")
    Call<GenerateReferenceNumberResponse> generateReferenceNumber(@Body GenerateReferenceNumberRequest request);
}

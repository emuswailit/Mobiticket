package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.pojos.Passenger;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerSearchVehiclesRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerSearchVehiclesResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ReserveVehiclesInterface {

    @POST("ticket/")
    Call<ServerSearchVehiclesResponse> searchVehicles(@Body Passenger request);
}
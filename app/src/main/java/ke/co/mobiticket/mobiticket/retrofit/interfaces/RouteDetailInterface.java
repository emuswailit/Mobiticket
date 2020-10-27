package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.RouteDetailsRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.SearchRouteRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.RouteDetailsResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RouteDetailInterface {

    @POST("route/")
    Call<RouteDetailsResponse> retrieveRouteDetails(@Body RouteDetailsRequest request);
}
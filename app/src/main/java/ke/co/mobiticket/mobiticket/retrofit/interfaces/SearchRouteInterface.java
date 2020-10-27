package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.SearchRouteRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.SearchRoutesResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SearchRouteInterface {

    @POST("route/")
    Call<SearchRoutesResponse> retrieveRoutes(@Body SearchRouteRequest request);
}
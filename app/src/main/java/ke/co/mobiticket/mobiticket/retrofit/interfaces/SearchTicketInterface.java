package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.ConfirmReservationRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.SearchTicketRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.ConfirmReservationResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.SearchTicketResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SearchTicketInterface {

    @POST("ticket/")
    Call<SearchTicketResponse> searchTicket(@Body SearchTicketRequest request);
}
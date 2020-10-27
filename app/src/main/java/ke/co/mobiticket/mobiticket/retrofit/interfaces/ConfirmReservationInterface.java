package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.ConfirmReservationRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.ReserveTicketsRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.ConfirmReservationResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.ReserveTicketResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ConfirmReservationInterface {

    @POST("ticket/")
    Call<ConfirmReservationResponse> confirmTicketReservation(@Body ConfirmReservationRequest request);
}
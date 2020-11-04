package ke.co.mobiticket.mobiticket.retrofit.interfaces;

import ke.co.mobiticket.mobiticket.retrofit.requests.CommuterNFCRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.RedeemVoucherRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.CommuterNFCResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.RedeemVoucherResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RedeemVoucherInterface {

    @POST("ticket/")
    Call<RedeemVoucherResponse> redeemVoucher(@Body RedeemVoucherRequest request);
}
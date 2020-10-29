package ke.co.mobiticket.mobiticket.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.util.List;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.pojos.Ticket;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.MpesaExpressInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.SearchTicketInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.MpesaExpressRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.SearchTicketRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.MpesaExpressResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.SearchTicketResponse;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends BaseActivity {
    SharedPreferences prefs;
    private String reference_number = "";
//    private TextView tvMpesaExpressOutC
    private ProgressBar progressBar;
    private String customer_message="";
    private String payment_method_id = "";
    private String mpesa_phone_number = "";
    private TextView tvMessage;
    private MaterialCardView cardMpesaXpress, cardMpesaPaybill, cardJambopayWallet, cardJambopayAgency, cardCommuterNFC, cardSuccess;
    private Button btnMpesaXpress, btnMpesaPaybill, btnJambopayWallet, btnJambopayAgency, btnCommuterNFC;
    private EditText etMpesaPhone, etJambopayWalletUsername, etJambopayWalletPassword, etJambopayAgencyUsername, etJambopayAgencyPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        prefs = AppController.getInstance().getMobiPrefs();
        reference_number = prefs.getString(Constants.TICKET_REFERENCE_NUMBER, "");
        payment_method_id = prefs.getString(Constants.TICKET_PAYMENT_METHOD_ID, "");
        Toast.makeText(this, payment_method_id, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, reference_number, Toast.LENGTH_SHORT).show();

        initLayouts();
        initListeners();

    }

    private void initListeners() {


        if (payment_method_id.equals("1")) {
            try {
                cardMpesaXpress.setVisibility(View.VISIBLE);

                btnMpesaXpress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("clicked", "clicked");
                        mpesa_phone_number = etMpesaPhone.getText().toString();
                        Log.e("mpeasa phone", mpesa_phone_number);
//                        progressBar.setVisibility(View.VISIBLE);
                        try {
                            verifyPaymentByMpesaXPress(payment_method_id, reference_number, mpesa_phone_number);
                        }catch (Exception e){
                            Log.e("MpesaXPress Error", e.toString());
                        }

                    }
                });

            }catch (Exception e){
                Log.e("error", e.toString());
            }


        } else if (payment_method_id.equals("2")) {
            cardMpesaPaybill.setVisibility(View.VISIBLE);
        } else if (payment_method_id.equals("3")) {
            cardJambopayWallet.setVisibility(View.VISIBLE);
            final String jambopay_wallet_username = etJambopayWalletUsername.getText().toString();
            final String jambopay_wallet_password = etJambopayWalletPassword.getText().toString();
            btnJambopayWallet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyPaymentByJambopayWallet(jambopay_wallet_username, jambopay_wallet_password);
                }
            });
        } else if (payment_method_id.equals("4")) {
            cardJambopayAgency.setVisibility(View.VISIBLE);
            final String jambopay_agency_username = etJambopayAgencyUsername.getText().toString();
            final String jambopay_agency_password = etJambopayAgencyPassword.getText().toString();
            btnJambopayAgency.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyPaymentByJambopayAgency(jambopay_agency_username, jambopay_agency_password);
                }
            });

        } else if (payment_method_id.equals("5")) {
            cardCommuterNFC.setVisibility(View.VISIBLE);
        }
    }

    private void verifyPaymentByJambopayAgency(String jambopay_agency_username, String jambopay_agency_password) {

    }

    private void verifyPaymentByJambopayWallet(String jambopay_wallet_username, String jambopay_wallet_password) {

    }

    private void initLayouts() {
        progressBar = findViewById(R.id.progressBar);
        tvMessage = findViewById(R.id.tvMessage);

        cardMpesaXpress = findViewById(R.id.cardMpesaXpress);
        cardMpesaPaybill = findViewById(R.id.cardMpesaPaybill);
        cardJambopayWallet = findViewById(R.id.cardJambopayWallet);
        cardJambopayAgency = findViewById(R.id.cardJambopayAgency);
        cardCommuterNFC = findViewById(R.id.cardCommuterNFC);
        cardSuccess = findViewById(R.id.cardSuccess);

        btnMpesaXpress = findViewById(R.id.btnMpesaExpress);
        btnMpesaPaybill = findViewById(R.id.btnMpesaPaybill);
        btnJambopayWallet = findViewById(R.id.btnJambopayWallet);
        btnJambopayAgency = findViewById(R.id.btnJambopayAgency);
        btnCommuterNFC = findViewById(R.id.btnCommuterNFC);

        etMpesaPhone = findViewById(R.id.etMpesaPhone);
        etJambopayWalletUsername = findViewById(R.id.etJambopayWalletUsername);
        etJambopayWalletPassword = findViewById(R.id.etJambopayWalletPassword);
        etJambopayAgencyPassword = findViewById(R.id.etJambopayAgencyPassword);
        etJambopayAgencyUsername = findViewById(R.id.etJambopayAgencyUsername);


    }

    private void verifyPaymentByMpesaXPress(String payment_method_id, final String reference_number, String mpesa_phone_number) {
        MpesaExpressInterface api=AppController.getInstance().getRetrofit().create(MpesaExpressInterface.class);
        MpesaExpressRequest request=new MpesaExpressRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.AUTHORIZE_PAYMENT_ACTION);
        request.setMpesa_phone_number(mpesa_phone_number);
        request.setReference_number(reference_number);
        request.setPayment_method(payment_method_id);

        Gson gson=new Gson();
        Log.e("request", gson.toJson(request));

        Call<MpesaExpressResponse> call=api.mpesaExpressPayment(request);

        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<MpesaExpressResponse>() {
            @Override
            public void onResponse(Call<MpesaExpressResponse> call, Response<MpesaExpressResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body() !=null){
                    try {
                        if (response.body().getResponse_code().equals("0")){
                            cardMpesaXpress.setVisibility(View.GONE);
                            cardSuccess.setVisibility(View.VISIBLE);
                            for (int i=0; i<response.body().getCustomer_message().length; i++){
                                customer_message += response.body().getCustomer_message()[i] +"\n";
                            }

                            tvMessage.setText(customer_message);
                            Toast.makeText(PaymentActivity.this, response.body().getResponse_message(), Toast.LENGTH_SHORT).show();
//                            showCustomDialog("Mpesa Express Payment",customer_message );

                            //Check if ticket is
                            searchTicket(reference_number);

                        }else {
                            showCustomDialog("Mpesa Express Payment", response.body().getResponse_message());
                        }
                    }catch (Exception e){
                        Log.e("Exception", e.toString());
                    }


                }else {
                    showCustomDialog("Mpesa Express Payment","An error occured");
                }
            }

            @Override
            public void onFailure(Call<MpesaExpressResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void searchTicket(String reference_number) {
        SearchTicketInterface api=AppController.getInstance().getRetrofit().create(SearchTicketInterface.class);
        SearchTicketRequest request=new SearchTicketRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.SEARCH_ACTION);
        request.setKeywords(reference_number);

        Call<SearchTicketResponse> call = api.searchTicket(request);
        call.enqueue(new Callback<SearchTicketResponse>() {
            @Override
            public void onResponse(Call<SearchTicketResponse> call, Response<SearchTicketResponse> response) {
               if (response.body()!=null){
                   if (response.body().getResponse_code().equals("0")){
                       showTicketDialog(response.body().getTicket());
                   }

               }else {
                   showCustomDialog("Search Ticket","Error occurred while searching for tickets");
               }
            }

            @Override
            public void onFailure(Call<SearchTicketResponse> call, Throwable t) {

            }
        });


    }

    private void showTicketDialog(List<Ticket> ticket) {

    }


}

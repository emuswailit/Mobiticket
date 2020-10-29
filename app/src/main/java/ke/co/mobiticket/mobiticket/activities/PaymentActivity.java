package ke.co.mobiticket.mobiticket.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.pojos.Ticket;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.JambopayAgencyInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.JambopayWalletInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.MpesaExpressInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.MpesaPaybillInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.SearchTicketInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.JambopayAgencyRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.JambopayWalletRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.MpesaExpressRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.MpesaPaybillRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.SearchTicketRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.JambopayAgencyResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.JambopayWalletResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.MpesaExpressResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.MpesaPaybillResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.SearchTicketResponse;
import ke.co.mobiticket.mobiticket.services.TicketVerificationService;
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
    Timer timer;
    private Boolean ticketNotConfirmed=false;
    private String customer_message="";
    private String payment_method_id = "";
    private String mpesa_phone_number = "";
    private TextView tvMessage, tvPaymentMethodName,tvStatus, tvDate,tvTime,tvTitle,tvTotalAmount,tvMpesaPaybillPrompt;
    private MaterialCardView cardMpesaXpress, cardMpesaPaybill, cardJambopayWallet, cardJambopayAgency, cardCommuterNFC, cardSuccess;
    private Button btnMpesaXpress, btnMpesaPaybill, btnJambopayWallet, btnJambopayAgency, btnCommuterNFC;
    private EditText etMpesaPhone, etJambopayWalletUsername, etJambopayWalletPassword, etJambopayAgencyUsername, etJambopayAgencyPassword;
    private boolean searchIsRunning=false;

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
        try {
            callAsynchronousTask(reference_number);

//            startServiceForSyncing();
        }catch (Exception e){

        }

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

            //Payment via Mpesa Paybill
            cardMpesaPaybill.setVisibility(View.VISIBLE);
            verifyMpesaPaybillPayment(payment_method_id,reference_number);
        } else if (payment_method_id.equals("3")) {

            //Payment via Jambopay wallet
            cardJambopayWallet.setVisibility(View.VISIBLE);

            btnJambopayWallet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String jambopay_wallet_username = etJambopayWalletUsername.getText().toString();
                    final String jambopay_wallet_password = etJambopayWalletPassword.getText().toString();
                    verifyPaymentByJambopayWallet(jambopay_wallet_username, jambopay_wallet_password);
                }
            });
        } else if (payment_method_id.equals("4")) {
            cardJambopayAgency.setVisibility(View.VISIBLE);

            btnJambopayAgency.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String jambopay_agency_username = etJambopayAgencyUsername.getText().toString();
                    final String jambopay_agency_password = etJambopayAgencyPassword.getText().toString();

                    try {
                        if (jambopay_agency_username.isEmpty()||jambopay_agency_username.equals(" ")){
                            Toast.makeText(PaymentActivity.this, "Enter agency  username", Toast.LENGTH_SHORT).show();
                        }else if (jambopay_agency_password.isEmpty()||jambopay_agency_password.equals(" ")){
                            Toast.makeText(PaymentActivity.this, "Enter agency  password", Toast.LENGTH_SHORT).show();
                        }else {
                            if (AppController.getInstance().isNetworkConnected()){
                                verifyPaymentByJambopayAgency(jambopay_agency_username, jambopay_agency_password);
                            }else {
                                startActivity(NoInternetActivity.class);
                            }
                        }

                    }catch (Exception e){
                        Log.e("JP Agency",e.toString());
                    }

                }
            });

        } else if (payment_method_id.equals("5")) {
            cardCommuterNFC.setVisibility(View.VISIBLE);
        }
    }

    private void verifyMpesaPaybillPayment(String payment_method_id, final String reference_number) {
        MpesaPaybillInterface api=AppController.getInstance().getRetrofit().create(MpesaPaybillInterface.class);
        MpesaPaybillRequest request=new MpesaPaybillRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.AUTHORIZE_PAYMENT_ACTION);
        request.setReference_number(reference_number);
        request.setPayment_method(payment_method_id);

        Gson gson=new Gson();
        Log.e("request", gson.toJson(request));

        Call<MpesaPaybillResponse> call=api.mpesaPaybillPayment(request);

        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<MpesaPaybillResponse>() {
            @Override
            public void onResponse(Call<MpesaPaybillResponse> call, Response<MpesaPaybillResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body() !=null){
                    try {
                        if (response.body().getResponse_code().equals("0")){

                          cardMpesaPaybill.setVisibility(View.VISIBLE);
                          progressBar.setVisibility(View.GONE);
                            for (int i=0; i<response.body().getCustomer_message().length; i++){
                                customer_message += response.body().getCustomer_message()[i] +"\n";
                            }

                            tvMpesaPaybillPrompt.setText(customer_message);
                            Toast.makeText(PaymentActivity.this, response.body().getResponse_message(), Toast.LENGTH_SHORT).show();
//                            showCustomDialog("Mpesa Express Payment",customer_message );

                         runCheck(reference_number);


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
            public void onFailure(Call<MpesaPaybillResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });


    }

    private void verifyPaymentByJambopayAgency(String jambopay_agency_username, String jambopay_agency_password) {
        JambopayAgencyInterface api=AppController.getInstance().getRetrofit().create(JambopayAgencyInterface.class);
        JambopayAgencyRequest request=new JambopayAgencyRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.AUTHORIZE_PAYMENT_ACTION);
        request.setReference_number(reference_number);
        request.setPayment_method(payment_method_id);
        request.setJambopay_agency_username(jambopay_agency_username);
        request.setJambopay_agency_password(jambopay_agency_password);

        Gson gson=new Gson();
        Log.e("request", gson.toJson(request));

        Call<JambopayAgencyResponse> call=api.jambopayAgencyPayment(request);

        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<JambopayAgencyResponse>() {
            @Override
            public void onResponse(Call<JambopayAgencyResponse> call, Response<JambopayAgencyResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body() !=null){
                    try {
                        if (response.body().getResponse_code().equals("0")){

                            cardJambopayAgency.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            for (int i=0; i<response.body().getCustomer_message().length; i++){
                                customer_message += response.body().getCustomer_message()[i] +"\n";
                            }

                            tvMessage.setText(customer_message);
                            Toast.makeText(PaymentActivity.this, response.body().getResponse_message(), Toast.LENGTH_SHORT).show();

                            runCheck(reference_number);


                        }else {
                            showCustomDialog("Jambopay Agency Payment", response.body().getResponse_message());
                        }
                    }catch (Exception e){
                        Log.e("Exception", e.toString());
                    }


                }else {
                    showCustomDialog("Jambopay Agency Payment","An error occured");
                }
            }

            @Override
            public void onFailure(Call<JambopayAgencyResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });


    }

    private void verifyPaymentByJambopayWallet(String jambopay_wallet_username, String jambopay_wallet_password) {
        JambopayWalletInterface api=AppController.getInstance().getRetrofit().create(JambopayWalletInterface.class);
        JambopayWalletRequest request=new JambopayWalletRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.AUTHORIZE_PAYMENT_ACTION);
        request.setReference_number(reference_number);
        request.setPayment_method(payment_method_id);
        request.setJambopay_wallet_username(jambopay_wallet_username);
        request.setJambopay_wallet_password(jambopay_wallet_password);

        final Gson gson=new Gson();
        Log.e("request", gson.toJson(request));

        Call<JambopayWalletResponse> call=api.jambopayWalletPayment(request);

        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<JambopayWalletResponse>() {
            @Override
            public void onResponse(Call<JambopayWalletResponse> call, Response<JambopayWalletResponse> response) {
                progressBar.setVisibility(View.GONE);
                Log.e("outcome", gson.toJson(response.body()));
                if (response.body() !=null){
                    try {
                        if (response.body().getResponse_code().equals("0")){

                            cardJambopayWallet.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            for (int i=0; i<response.body().getCustomer_message().length; i++){
                                customer_message += response.body().getCustomer_message()[i] +"\n";
                            }

                            tvMessage.setText(customer_message);
                            Toast.makeText(PaymentActivity.this, response.body().getResponse_message(), Toast.LENGTH_SHORT).show();

                            runCheck(reference_number);



                        }else {
                            showCustomDialog("Jambopay Wallet Payment", response.body().getResponse_message());
                        }
                    }catch (Exception e){
                        Log.e("Exception", e.toString());
                    }


                }else {
                    showCustomDialog("Jambopay Wallet Payment","An error occured");
                }
            }

            @Override
            public void onFailure(Call<JambopayWalletResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("JP Wallet", t.getLocalizedMessage());
            }
        });


    }

    private void initLayouts() {
        progressBar = findViewById(R.id.progressBar);
        tvMessage = findViewById(R.id.tvMessage);
        tvPaymentMethodName = findViewById(R.id.tvPaymentMethodName);
        tvTitle = findViewById(R.id.tvTitle);
        tvDate = findViewById(R.id.tvDate);
        tvTime = findViewById(R.id.tvTime);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);

        cardMpesaXpress = findViewById(R.id.cardMpesaXpress);
        cardMpesaPaybill = findViewById(R.id.cardMpesaPaybill);
        tvMpesaPaybillPrompt = findViewById(R.id.tvMpesaPaybillPrompt);
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

        //Agency input
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

                   runCheck(reference_number);


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

    private void runCheck(String reference_number) {
        //Check if ticket is

            timer =new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Toast.makeText(PaymentActivity.this, "Checking progress...", Toast.LENGTH_SHORT).show();

                }
            }, 0, 10*1000);
        }


    private void searchTicket(final String reference_number) {
        SearchTicketInterface api=AppController.getInstance().getRetrofit().create(SearchTicketInterface.class);
        SearchTicketRequest request=new SearchTicketRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.SEARCH_ACTION);
        request.setKeywords(reference_number);
        progressBar.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Run search again......", Toast.LENGTH_SHORT).show();
        Call<SearchTicketResponse> call = api.searchTicket(request);
        call.enqueue(new Callback<SearchTicketResponse>() {
            @Override
            public void onResponse(Call<SearchTicketResponse> call, Response<SearchTicketResponse> response) {
                searchIsRunning=false;
                progressBar.setVisibility(View.GONE);
               if (response.body()!=null){
                   if (response.body().getResponse_code().equals("0")){

                       if (response.body().getTicket().get(0).getStatus().equals("Confirmed")){


                           showTicketDialog(response.body().getTicket());
                       }else {
                           tvMessage.setText(reference_number +" "+ "Not confirmed!");
                       }


                   }

               }else {
                   showCustomDialog("Search Ticket","Error occurred while searching for tickets");
               }
            }

            @Override
            public void onFailure(Call<SearchTicketResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
searchIsRunning=false;
            }
        });


    }

    private void showTicketDialog(List<Ticket> ticket)  {
        try {


            final Dialog dialog = new Dialog(PaymentActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_accept_payment_method);
            dialog.setCancelable(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            ImageView card_logo = dialog.findViewById(R.id.card_logo);
            if (payment_method_id.equals("8")){
                card_logo.setImageResource(R.drawable.ic_visa_new);
            }else if (payment_method_id.equals("9")){
                card_logo.setImageResource(R.drawable.ic_mastercard_new);
            }else if (payment_method_id.equals("4")){
                card_logo.setImageResource(R.drawable.ic_jambopay_agent);
            }else if (payment_method_id.equals("3")){
                card_logo.setImageResource(R.drawable.ic_jambopay_wallet);
            }else if (payment_method_id.equals("7")){
                card_logo.setImageResource(R.drawable.pesalink);
            }else if (payment_method_id.equals("2")){
                card_logo.setImageResource(R.drawable.ic_mpesa);
            }else if (payment_method_id.equals("1")){
                card_logo.setImageResource(R.drawable.ic_mpesa);
            }else if (payment_method_id.equals("6")){
                card_logo.setImageResource(R.drawable.mticket_green);
            }else if (payment_method_id.equals("5")){
                card_logo.setImageResource(R.drawable.mticket_green);
            }

            TextView tvTitle = dialog.findViewById(R.id.title);
            TextView tvTickets = dialog.findViewById(R.id.tvTickets);
            TextView tvTotalCost = dialog.findViewById(R.id.tvTotalCost);
            TextView tvContent = dialog.findViewById(R.id.content);
//            tvContent.setText(message);
//            tvTitle.setText(title);
//            total_cost=Double.valueOf(prefs.getString(Constants.TICKET_VEHICLE_CURRENT_FARE,""))* passengerList.size();
//            tvTickets.setText("Tickets: "+ passengerList.size());
//            tvTotalCost.setText("KES "+ String.format("%.2f",total_cost));
            ((Button) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PaymentActivity.this, ((AppCompatButton) v).getText().toString() + " Clicked", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            ((Button) dialog.findViewById(R.id.bt_yes)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {





                    dialog.dismiss();
                }
            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            Log.e("Dialog", e.toString());
        }
    }

    private void startServiceForSyncing() {
        Intent i= new Intent(PaymentActivity.this, TicketVerificationService.class);
// potentially add data to the intent
        PaymentActivity.this.startService(i);
    }

    public void callAsynchronousTask(String reference_number) {
        final int x=500;
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {



                            searchTicket(PaymentActivity.this.reference_number);

                        } catch (Exception e) {

                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 30*1000); //execute in every 1000 ms
    }
}

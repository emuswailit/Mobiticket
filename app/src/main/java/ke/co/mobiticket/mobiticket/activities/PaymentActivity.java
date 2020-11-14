package ke.co.mobiticket.mobiticket.activities;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.SecretKey;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.adapters.TicketsAdapter;
import ke.co.mobiticket.mobiticket.pojos.Passenger;
import ke.co.mobiticket.mobiticket.pojos.Ticket;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.CommuterNFCInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.JambopayAgencyInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.JambopayWalletInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.MpesaExpressInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.MpesaPaybillInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.RedeemVoucherInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.SearchTicketInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.CommuterNFCRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.JambopayAgencyRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.JambopayWalletRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.MpesaExpressRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.MpesaPaybillRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.RedeemVoucherRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.SearchTicketRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.CommuterNFCResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.JambopayAgencyResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.JambopayWalletResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.MpesaExpressResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.MpesaPaybillResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.RedeemVoucherResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.SearchTicketResponse;
import ke.co.mobiticket.mobiticket.services.TicketVerificationService;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;
import ke.co.mobiticket.mobiticket.utilities.Cryptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends BaseActivity {
    SharedPreferences prefs;
    private String reference_number = "";
    //    private TextView tvMpesaExpressOutC
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private RecyclerView rvTickets;
    Timer timer;
    Tag myTag;
    private Boolean ticketNotConfirmed = false;
    private String customer_message = "";
    private String payment_method_id = "";
    private String mpesa_phone_number = "";
    private TextView tvMessage, tvPaymentMethodName, tvStatus, tvDate, tvTime, tvTitle, tvTotalAmount, tvMpesaPaybillPrompt, lblPurchasedTickets;
    private MaterialCardView cardMpesaXpress, cardMpesaPaybill, cardJambopayWallet, cardJambopayAgency, cardCommuterNFC, cardSuccess, cardRedeemVoucher;
    private Button btnMpesaXpress, btnMpesaPaybill, btnJambopayWallet, btnJambopayAgency, btnCommuterNFC,btnRedeemVoucher;
    private EditText etMpesaPhone, etJambopayWalletUsername, etJambopayWalletPassword, etJambopayAgencyUsername, etJambopayAgencyPassword, etVoucherNumber,etSecurityPin;
    private boolean searchIsRunning = false;
    List<Ticket> recentTicketsList = new ArrayList<Ticket>();
    List<Passenger> passengerList = null;
    Gson gson = new Gson();
    String recentTicketsString = "";
    private String payload;
    private String encryptedText = null;
    private int search_runs=3;
    SecretKey secretKey=null;
    private ImageView ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);


        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            //No NFC in device or NFC could not be initialized
            Toast.makeText(this, "Device not NFC enabled!", Toast.LENGTH_SHORT).show();
            //Close the activity
//            finish();
        } else {
            Toast.makeText(this, "NFC successfully initialized!", Toast.LENGTH_SHORT).show();
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
            mPendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            //ndef.addDataScheme("http");
            mFilters = new IntentFilter[]{
                    ndef,
            };
            mTechLists = new String[][]{new String[]{Ndef.class.getName()},
                    new String[]{NdefFormatable.class.getName()}};

        }
        prefs = AppController.getInstance().getMobiPrefs();
        reference_number = prefs.getString(Constants.TICKET_REFERENCE_NUMBER, "");
        payment_method_id = prefs.getString(Constants.TICKET_PAYMENT_METHOD_ID, "");
        if (payment_method_id.isEmpty() || payment_method_id.equals("")) {
            finish();
        }
        Toast.makeText(this, payment_method_id, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, reference_number, Toast.LENGTH_SHORT).show();
        String passengerListString = prefs.getString(Constants.PASSENGER_DATA_THIS_BOOKING, "");
        Log.e("retrievePassengers", passengerListString);
        if (passengerListString.isEmpty() || passengerListString.equals("")) {
            finish();
        } else {
            Gson gson = new Gson();
            passengerList = Arrays.asList(new GsonBuilder().create().fromJson(passengerListString, Passenger[].class));
            Log.e("Passengers at pay", String.valueOf(passengerList.size()));

            for (Passenger passenger : passengerList) {
                Toast.makeText(this, passenger.getEmail_address(), Toast.LENGTH_SHORT).show();
            }
        }

        String recentTicketString = prefs.getString(Constants.RECENT_TICKETS, "");
        Log.e("recentTicketsStr", passengerListString);
        if (recentTicketString.isEmpty() || recentTicketString.equals("")) {
            Log.e("No recents", "No recents");
        } else {
            Gson gson = new Gson();
            recentTicketsList = new ArrayList<>(Arrays.asList(new GsonBuilder().create().fromJson(recentTicketString, Ticket[].class)));

            for (Ticket ticket : recentTicketsList) {
                Log.e("from recents", ticket.getStatus());
            }
        }


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

                        //Set mpesa number to user phone number, user can change this

                        mpesa_phone_number = etMpesaPhone.getText().toString();
                        Log.e("mpesa phone", mpesa_phone_number);
//                        progressBar.setVisibility(View.VISIBLE);
                        try {
                            verifyPaymentByMpesaXPress(payment_method_id, reference_number, mpesa_phone_number);
                        } catch (Exception e) {
                            Log.e("MpesaXPress Error", e.toString());
                        }

                    }
                });

            } catch (Exception e) {
                Log.e("error", e.toString());
            }


        } else if (payment_method_id.equals("2")) {

            //Payment via Mpesa Paybill
            cardMpesaPaybill.setVisibility(View.VISIBLE);
            verifyMpesaPaybillPayment(payment_method_id, reference_number);
            btnMpesaPaybill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callAsynchronousTask(reference_number);
                }
            });
        } else if (payment_method_id.equals("3")) {

            //Payment via Jambopay wallet
            cardJambopayWallet.setVisibility(View.VISIBLE);
            etJambopayWalletUsername.setText(prefs.getString(Constants.PHONE_NUMBER,""));

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
            etJambopayAgencyUsername.setText(prefs.getString(Constants.PHONE_NUMBER,""));

            btnJambopayAgency.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String jambopay_agency_username = etJambopayAgencyUsername.getText().toString();
                    final String jambopay_agency_password = etJambopayAgencyPassword.getText().toString();

                    try {
                        if (jambopay_agency_username.isEmpty() || jambopay_agency_username.equals(" ")) {
                            Toast.makeText(PaymentActivity.this, "Enter agency  username", Toast.LENGTH_SHORT).show();
                        } else if (jambopay_agency_password.isEmpty() || jambopay_agency_password.equals(" ")) {
                            Toast.makeText(PaymentActivity.this, "Enter agency  password", Toast.LENGTH_SHORT).show();
                        } else {
                            if (AppController.getInstance().isNetworkConnected()) {
                                verifyPaymentByJambopayAgency(jambopay_agency_username, jambopay_agency_password);
                            } else {
                                startActivity(NoInternetActivity.class);
                            }
                        }

                    } catch (Exception e) {
                        Log.e("JP Agency", e.toString());
                    }

                }
            });

        } else if (payment_method_id.equals("5")) {
            if (mNfcAdapter==null){
                Toast.makeText(this, "NFC not enabled in this device!", Toast.LENGTH_SHORT).show();
            }else {
                cardCommuterNFC.setVisibility(View.VISIBLE);
                //Activity will be initiated by tapping card
            }

        }else if (payment_method_id.equals("10")){
            cardRedeemVoucher.setVisibility(View.VISIBLE);
            btnRedeemVoucher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String voucher_number= etVoucherNumber.getText().toString();
                    String security_pin= etSecurityPin.getText().toString();

                    if (voucher_number.isEmpty()||voucher_number.equals("")){
                        Toast.makeText(PaymentActivity.this, "Enter voucher number", Toast.LENGTH_SHORT).show();
                    }else if (security_pin.isEmpty()||security_pin.equals("")){
                        Toast.makeText(PaymentActivity.this, "Enter security pin", Toast.LENGTH_SHORT).show();
                    }else {
                        verifyRedeemVoucher(voucher_number, security_pin, reference_number);
                    }
                }
            });
        }
    }

    private void verifyRedeemVoucher(String voucher_number, String security_pin, final String reference_number) {

        final Dialog dialog=new Dialog(PaymentActivity.this);
        RedeemVoucherInterface api=AppController.getInstance().getRetrofit().create(RedeemVoucherInterface.class);
        RedeemVoucherRequest request= new RedeemVoucherRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN,""));
        request.setAction(Constants.AUTHORIZE_PAYMENT_ACTION);
        request.setReference_number(this.reference_number);
        request.setPayment_method(payment_method_id);
        request.setVoucher_number(voucher_number);
        request.setSecurity_pin(security_pin);
        showProgressDialog(dialog, "Processing voucher payment"+getResources().getString(R.string.txt_please_wait));
        Call<RedeemVoucherResponse> call=api.redeemVoucher(request);
        call.enqueue(new Callback<RedeemVoucherResponse>() {
            @Override
            public void onResponse(Call<RedeemVoucherResponse> call, Response<RedeemVoucherResponse> response) {
               dialog.dismiss();
                if (response.body() !=null){
                    cardRedeemVoucher.setVisibility(View.GONE);
                    Log.e("redeem voucher", gson.toJson(response.body()));
                    if (response.body().getResponse_code().equals("0")){
                    for ( String message: response.body().getCustomer_message()){
                        customer_message+=message;
                    }
                    tvMessage.setText(customer_message);
                    callAsynchronousTask(reference_number);

                    }else {
                        showPaymentErrorDialog("Reddem Voucher", response.body().getResponse_message());
                    }
                }
            }

            @Override
            public void onFailure(Call<RedeemVoucherResponse> call, Throwable t) {
               dialog.dismiss();
            }
        });
    }

    private void verifyMpesaPaybillPayment(String payment_method_id, final String reference_number) {
        final Dialog dialog=new Dialog(PaymentActivity.this);
        MpesaPaybillInterface api = AppController.getInstance().getRetrofit().create(MpesaPaybillInterface.class);
        MpesaPaybillRequest request = new MpesaPaybillRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.AUTHORIZE_PAYMENT_ACTION);
        request.setReference_number(reference_number);
        request.setPayment_method(payment_method_id);

        Gson gson = new Gson();
        Log.e("request", gson.toJson(request));

        Call<MpesaPaybillResponse> call = api.mpesaPaybillPayment(request);

       showProgressDialog(dialog,"Processing Mpesa Paybill payment"+getResources().getString(R.string.txt_please_wait));
        call.enqueue(new Callback<MpesaPaybillResponse>() {
            @Override
            public void onResponse(Call<MpesaPaybillResponse> call, Response<MpesaPaybillResponse> response) {
                dialog.dismiss();
                if (response.body() != null) {
                    try {
                        if (response.body().getResponse_code().equals("0")) {

                            cardMpesaPaybill.setVisibility(View.VISIBLE);

                            for (int i = 0; i < response.body().getCustomer_message().length; i++) {
                                customer_message += response.body().getCustomer_message()[i] + "\n";
                            }

                            tvMpesaPaybillPrompt.setText(customer_message);


                            runCheck(reference_number);


                        } else {
                            showPaymentErrorDialog("Mpesa Express Payment", response.body().getResponse_message());
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }


                } else {
                    showPaymentErrorDialog("Mpesa Express Payment", "An error occured");
                }
            }

            @Override
            public void onFailure(Call<MpesaPaybillResponse> call, Throwable t) {
                dialog.dismiss();
            }
        });


    }

    private void verifyPaymentByJambopayAgency(String jambopay_agency_username, String jambopay_agency_password) {
        final Dialog dialog=new Dialog(PaymentActivity.this);
        JambopayAgencyInterface api = AppController.getInstance().getRetrofit().create(JambopayAgencyInterface.class);
        JambopayAgencyRequest request = new JambopayAgencyRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.AUTHORIZE_PAYMENT_ACTION);
        request.setReference_number(reference_number);
        request.setPayment_method(payment_method_id);
        request.setJambopay_agency_username(jambopay_agency_username);
        request.setJambopay_agency_password(jambopay_agency_password);

        Gson gson = new Gson();
        Log.e("request", gson.toJson(request));

        Call<JambopayAgencyResponse> call = api.jambopayAgencyPayment(request);

       showProgressDialog(dialog,"Processing Jambopay Agent payment"+getResources().getString(R.string.txt_please_wait));
        call.enqueue(new Callback<JambopayAgencyResponse>() {
            @Override
            public void onResponse(Call<JambopayAgencyResponse> call, Response<JambopayAgencyResponse> response) {
               dialog.dismiss();
                if (response.body() != null) {
                    try {
                        if (response.body().getResponse_code().equals("0")) {

                            cardJambopayAgency.setVisibility(View.GONE);

                            for (int i = 0; i < response.body().getCustomer_message().length; i++) {
                                customer_message += response.body().getCustomer_message()[i] + "\n";
                            }

                            tvMessage.setText(customer_message);
                            Toast.makeText(PaymentActivity.this, response.body().getResponse_message(), Toast.LENGTH_SHORT).show();

                           callAsynchronousTask(reference_number);

                        } else {
                            showPaymentErrorDialog("Jambopay Agency Payment", response.body().getResponse_message());
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }


                } else {
                    showPaymentErrorDialog("Jambopay Agency Payment", "An error occured");
                }
            }

            @Override
            public void onFailure(Call<JambopayAgencyResponse> call, Throwable t) {
                dialog.dismiss();
            }
        });


    }

    private void verifyPaymentByJambopayWallet(String jambopay_wallet_username, String jambopay_wallet_password) {
        final Dialog dialog=new Dialog(PaymentActivity.this);
        JambopayWalletInterface api = AppController.getInstance().getRetrofit().create(JambopayWalletInterface.class);
        JambopayWalletRequest request = new JambopayWalletRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.AUTHORIZE_PAYMENT_ACTION);
        request.setReference_number(reference_number);
        request.setPayment_method(payment_method_id);
        request.setJambopay_wallet_username(jambopay_wallet_username);
        request.setJambopay_wallet_password(jambopay_wallet_password);

        final Gson gson = new Gson();
        Log.e("request", gson.toJson(request));

        Call<JambopayWalletResponse> call = api.jambopayWalletPayment(request);

       showProgressDialog(dialog, "Processing payment"+getResources().getString(R.string.txt_please_wait));
        call.enqueue(new Callback<JambopayWalletResponse>() {
            @Override
            public void onResponse(Call<JambopayWalletResponse> call, Response<JambopayWalletResponse> response) {
                dialog.dismiss();
                Log.e("outcome", gson.toJson(response.body()));
                if (response.body() != null) {
                    try {
                        if (response.body().getResponse_code().equals("0")) {

                            cardJambopayWallet.setVisibility(View.GONE);

                            for (int i = 0; i < response.body().getCustomer_message().length; i++) {
                                customer_message += response.body().getCustomer_message()[i] + "\n";
                            }

                            tvMessage.setText(customer_message);
                            Toast.makeText(PaymentActivity.this, response.body().getResponse_message(), Toast.LENGTH_SHORT).show();

                            callAsynchronousTask(reference_number);


                        } else {
                            try {
                                showPaymentErrorDialog("Jambopay Wallet Payment", response.body().getResponse_message());
                            }catch (Exception e){
                              Log.e("eegege", e.toString());
                            }

                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }


                } else {
                    showPaymentErrorDialog("Jambopay Wallet Payment", "An error occured");
                }
            }

            @Override
            public void onFailure(Call<JambopayWalletResponse> call, Throwable t) {
                dialog.dismiss();
                Log.e("JP Wallet", t.getLocalizedMessage());
            }
        });


    }

    private void initLayouts() {

        ivBack = findViewById(R.id.ivBack);
        tvMessage = findViewById(R.id.tvMessage);
//        tvPaymentMethodName = findViewById(R.id.tvPaymentMethodName);
//        tvTitle = findViewById(R.id.tvTitle);
//        tvDate = findViewById(R.id.tvDate);
//        tvTime = findViewById(R.id.tvTime);
//        tvTotalAmount = findViewById(R.id.tvTotalAmount);

        cardMpesaXpress = findViewById(R.id.cardMpesaXpress);
        cardMpesaPaybill = findViewById(R.id.cardMpesaPaybill);
        tvMpesaPaybillPrompt = findViewById(R.id.tvMpesaPaybillPrompt);
        cardJambopayWallet = findViewById(R.id.cardJambopayWallet);
        cardJambopayAgency = findViewById(R.id.cardJambopayAgency);
        cardCommuterNFC = findViewById(R.id.cardCommuterNFC);
        cardSuccess = findViewById(R.id.cardSuccess);
        cardRedeemVoucher = findViewById(R.id.cardRedeemVoucher);
        etVoucherNumber = findViewById(R.id.etVoucherNumber);
        etSecurityPin = findViewById(R.id.etSecurityPin);
        btnRedeemVoucher = findViewById(R.id.btnRedeemVoucher);

        btnMpesaXpress = findViewById(R.id.btnMpesaExpress);
        btnMpesaPaybill = findViewById(R.id.btnMpesaPaybill);
        btnJambopayWallet = findViewById(R.id.btnJambopayWallet);
        btnJambopayAgency = findViewById(R.id.btnJambopayAgency);
        btnCommuterNFC = findViewById(R.id.btnCommuterNFC);
        lblPurchasedTickets = findViewById(R.id.lblPurchasedTickets);

        etMpesaPhone = findViewById(R.id.etMpesaPhone);
        etMpesaPhone.setText(prefs.getString(Constants.PHONE_NUMBER,""));
        etJambopayWalletUsername = findViewById(R.id.etJambopayWalletUsername);
        etJambopayWalletPassword = findViewById(R.id.etJambopayWalletPassword);

        //Agency input
        etJambopayAgencyPassword = findViewById(R.id.etJambopayAgencyPassword);
        etJambopayAgencyUsername = findViewById(R.id.etJambopayAgencyUsername);

        //Tickets
        rvTickets = findViewById(R.id.rvRecentTickets);
        rvTickets.setLayoutManager(new LinearLayoutManager(this));
        rvTickets.setHasFixedSize(true);
    }

    private void verifyPaymentByMpesaXPress(String payment_method_id, final String reference_number, String mpesa_phone_number) {
        final Dialog dialog=new Dialog(PaymentActivity.this);
        MpesaExpressInterface api = AppController.getInstance().getRetrofit().create(MpesaExpressInterface.class);
        MpesaExpressRequest request = new MpesaExpressRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.AUTHORIZE_PAYMENT_ACTION);
        request.setMpesa_phone_number(mpesa_phone_number);
        request.setReference_number(reference_number);
        request.setPayment_method(payment_method_id);

        Gson gson = new Gson();
        Log.e("request", gson.toJson(request));

        Call<MpesaExpressResponse> call = api.mpesaExpressPayment(request);

       showProgressDialog(dialog,"Proccessing Mpesa payment"+getResources().getString(R.string.txt_please_wait));
        call.enqueue(new Callback<MpesaExpressResponse>() {
            @Override
            public void onResponse(Call<MpesaExpressResponse> call, Response<MpesaExpressResponse> response) {
               dialog.dismiss();
                if (response.body() != null) {
                    try {
                        if (response.body().getResponse_code().equals("0")) {
                            cardMpesaXpress.setVisibility(View.GONE);
                            tvMessage.setVisibility(View.VISIBLE);
                            for (int i = 0; i < response.body().getCustomer_message().length; i++) {
                                customer_message += response.body().getCustomer_message()[i] + "\n";
                            }

                            tvMessage.setText(customer_message);


callAsynchronousTask(reference_number);


                        } else {
                            showPaymentErrorDialog("Mpesa Express Payment", response.body().getResponse_message());
                        }
                    } catch (Exception e) {
                        Log.e("Exception", e.toString());
                    }


                } else {
                    showPaymentErrorDialog("Mpesa Express Payment", "An error occured");
                }
            }

            @Override
            public void onFailure(Call<MpesaExpressResponse> call, Throwable t) {
               dialog.dismiss();
            }
        });

    }

    private void runCheck(String reference_number) {
        //Check if ticket is

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {


            }
        }, 0, 10 * 1000);
    }


    private void searchTicket(final String reference_number, final Timer timer, final String recentTicketsString) {
        final Dialog dialog= new Dialog(PaymentActivity.this);
        SearchTicketInterface api = AppController.getInstance().getRetrofit().create(SearchTicketInterface.class);
        SearchTicketRequest request = new SearchTicketRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.SEARCH_ACTION);
        request.setKeywords(reference_number);
       showProgressDialog(dialog, "Checking payment status"+getResources().getString(R.string.txt_please_wait));

        Call<SearchTicketResponse> call = api.searchTicket(request);
        call.enqueue(new Callback<SearchTicketResponse>() {
            @Override
            public void onResponse(Call<SearchTicketResponse> call, Response<SearchTicketResponse> response) {
                searchIsRunning = false;
               dialog.dismiss();
                if (response.body() != null) {
                    if (response.body().getResponse_code().equals("0")) {
                        try {
                            Gson gson = new Gson();
                            SharedPreferences.Editor editor = prefs.edit();

                            editor.putString(Constants.TICKET_REFERENCE_NUMBER, "");
                            editor.putBoolean(Constants.TICKET_IS_RESERVED, false);
                            editor.putString(Constants.PASSENGER_DATA_THIS_BOOKING, null);
                            editor.putString(Constants.TICKET_PAYMENT_METHOD_ID, "");
                            editor.apply();
                        } catch (Exception e) {
                            Log.e("Write prefs", "Error editing prefs");
                        }

                        Log.e("search Success", gson.toJson(response.body()));
                        lblPurchasedTickets.setVisibility(View.VISIBLE);
                        try {
                            TicketsAdapter adapter = new TicketsAdapter(PaymentActivity.this, response.body().getTicket());
                            rvTickets.setAdapter(adapter);
                            RunLayoutAnimation(rvTickets);
                            List<Ticket> ticketList = response.body().getTicket();

                            List<String> statuses = new ArrayList<>();

                            //Retrieve all statuses and save in a list
                            for (Ticket ticket : ticketList) {
                                statuses.add(ticket.getStatus());
                            }


                            //Iterate through the list to check if any ticket is not confirmed

                            if (statuses.contains("Pending")) {
                                for (String status : statuses) {
                                    Log.e("status", status);
                                }
                                statuses.clear();
                                Log.e("Confirimation", "Not all tickets are confirmed");
                            } else {
//All tickets have been confirmed

                                Log.e("status", "All tickets confirmed");
                                for (String status : statuses) {
                                    Log.e("status", status);
                                }

                                timer.cancel();
                                statuses.clear();
                                showTicketDialog(ticketList, "Payment for tickets done successfully. Would you like to view your tickets?");
//                                try {
//                                    Log.e("str",recentTicketsString);
//                                    recentTicketsList = Arrays.asList(new GsonBuilder().create().fromJson(recentTicketsString, Ticket[].class));
//
//
//                                    Log.e("recentlist", String.valueOf(recentTicketsList.size()));
//                                }catch (Exception e){
//                                    Log.e("concert str to list",e.toString());
//                                }

                                Log.e("ticketList Before", String.valueOf(recentTicketsList.size()));

                                recentTicketsList.addAll(ticketList);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putString(Constants.RECENT_TICKETS, gson.toJson(recentTicketsList));
                                editor.apply();
                                Log.e("ticketList After", String.valueOf(recentTicketsList.size()));

                            }


                        } catch (Exception e) {
                            Log.e("error last", e.toString());
                        }

                    } else {
                       showPaymentErrorDialog("Ticket Payment Status", response.body().getResponse_message());
                    }

                } else {
                    showPaymentErrorDialog("Search Ticket", "Error occurred while searching for tickets");
                }
            }

            @Override
            public void onFailure(Call<SearchTicketResponse> call, Throwable t) {
               dialog.dismiss();
                Log.e("search Failure", gson.toJson(t.getLocalizedMessage()));
                searchIsRunning = false;
            }
        });


    }

    private void showTicketDialog(List<Ticket> ticket, String message) {
        try {


            final Dialog dialog = new Dialog(PaymentActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_success_payment_method);
            dialog.setCancelable(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


            TextView tvTitle = dialog.findViewById(R.id.title);
            TextView tvTickets = dialog.findViewById(R.id.tvTickets);
            TextView tvTotalCost = dialog.findViewById(R.id.tvTotalCost);
            TextView tvContent = dialog.findViewById(R.id.content);
            tvContent.setText(message);
            tvTitle.setText(message);
            Double total_cost = Double.valueOf(prefs.getString(Constants.TICKET_VEHICLE_CURRENT_FARE, "")) * passengerList.size();
            tvTickets.setText("Tickets: " + passengerList.size());
            tvTotalCost.setText("KES " + String.format("%.2f", total_cost));
            ((Button) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(PaymentActivity.this, ((AppCompatButton) v).getText().toString() + " Clicked", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    Intent intent = new Intent(PaymentActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            });
            ((Button) dialog.findViewById(R.id.bt_yes)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    dialog.dismiss();
                }
            });
            ((ImageButton) dialog.findViewById(R.id.bt_exit)).setOnClickListener(new View.OnClickListener() {
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
        Intent i = new Intent(PaymentActivity.this, TicketVerificationService.class);
// potentially add data to the intent
        PaymentActivity.this.startService(i);
    }

    public void callAsynchronousTask(final String reference_number) {
        final int x = 500;
        final Handler handler = new Handler();
        timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {

                            //Do only 3 seacrch runs and exit the application


                            searchTicket(reference_number, timer, recentTicketsString);


                        } catch (Exception e) {

                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5 * 1000); //execute in every 5000 ms
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer!=null){
            timer.cancel();
        }

    }

    @Override
    public void onBackPressed() {
        Log.e("ticket ref", prefs.getString(Constants.TICKET_REFERENCE_NUMBER, ""));
        Log.e("ticket data", prefs.getString(Constants.PASSENGER_DATA_THIS_BOOKING, ""));
        if (prefs.getString(Constants.TICKET_REFERENCE_NUMBER, "").isEmpty() || prefs.getString(Constants.TICKET_REFERENCE_NUMBER, "").equals("")) {
            Intent intent = new Intent(PaymentActivity.this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }else{
            Intent intent = new Intent(PaymentActivity.this, PaymentMethodsActivity.class);
            startActivity(intent);
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        readFromIntent(intent, tag);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            myTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        }
    }

    private void readFromIntent(Intent intent, Tag tag) {
        Toast.makeText(this, "Touched", Toast.LENGTH_SHORT).show();
        String action = intent.getAction();
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }
            buildTagViews(msgs, tag);
        }
    }

    private void buildTagViews(NdefMessage[] messages, Tag tag) {
        if (messages == null || messages.length == 0) return;
try {


        for (int i = 0; i < messages.length; i++) {
            for (int j = 0; j < messages[0].getRecords().length; j++) {
                NdefRecord record = messages[i].getRecords()[j];
                payload = new String(record.getPayload());
                String delimiter = ":";
                String[] temp = payload.split(delimiter);
                Log.e("payload", payload);
                encryptedText = temp[0];
                Log.e("encryptedText", String.valueOf(encryptedText));


                String decryptedText = null;
                Cryptor cryptor = new Cryptor();
                cryptor.initKeyStore();
                String decrypted = cryptor.decryptText(prefs.getString("encryptedKey",""), prefs.getString("keyIv",""));

                Log.e("decrypted",decrypted);
//                Log.e("decrypted", decryptedText);
//                accountNames = temp[1];
//                cardData = temp[2];  14283583939
//                currentBalance = temp[3];
//
                if (payment_method_id.equals("5")) {

                    Log.e("ref num", reference_number);
                    Log.e("payment m", payment_method_id);
                    verifyCommuterNFCCard(payment_method_id, decrypted, reference_number);
                }

            }
            }

        }catch (Exception e){
    Log.e("tap card error", e.toString());
        }
    }

    private void verifyCommuterNFCCard(String payment_method_id, String card_data, final String reference_number) {
        final Dialog dialog=new Dialog(PaymentActivity.this);
        CommuterNFCInterface api = AppController.getInstance().getRetrofit().create(CommuterNFCInterface.class);
        CommuterNFCRequest request = new CommuterNFCRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.AUTHORIZE_PAYMENT_ACTION);
        request.setReference_number(reference_number);
        request.setCard_data(card_data);
        request.setPayment_method(payment_method_id);

        Log.e("card request", gson.toJson(request));
      showProgressDialog(dialog, "Processing Commuter Card payment"+getResources().getString(R.string.txt_please_wait));
        Call<CommuterNFCResponse> call = api.verifyCommuterCard(request);
        call.enqueue(new Callback<CommuterNFCResponse>() {
            @Override
            public void onResponse(Call<CommuterNFCResponse> call, Response<CommuterNFCResponse> response) {
                dialog.dismiss();
                if (response.body() != null) {
                    Log.e("commuter card", gson.toJson(response.body()));
                    if (response.body().getResponse_code().equals("0")) {

                        cardCommuterNFC.setVisibility(View.GONE);
                        tvMessage.setText(response.body().getResponse_message());
                        callAsynchronousTask(reference_number);

                    } else {
                        showPaymentErrorDialog("Commuter Card Payment", response.body().getResponse_message());
                    }

                } else {
                    showPaymentErrorDialog("Commuter Card Payment", "An error occured. Please try again!");
                }
            }

            @Override
            public void onFailure(Call<CommuterNFCResponse> call, Throwable t) {
                dialog.dismiss();
                Log.e("commuter card error", t.getLocalizedMessage());
                showPaymentErrorDialog("Commuter Card Patment", "System error occurred. Please tap card to try again!");
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                    mTechLists);
    }

    @Override
    public void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }
    public void showPaymentErrorDialog(String title, String message) {
        try {


            final Dialog dialog = new Dialog(PaymentActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_warning);
            dialog.setCancelable(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            TextView tvTitle = dialog.findViewById(R.id.title);
            TextView tvContent = dialog.findViewById(R.id.content);
            tvContent.setText(message);
            tvTitle.setText(title);
            ((Button) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });

            ((ImageButton) dialog.findViewById(R.id.bt_exit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    finish();
                    dialog.dismiss();
                }
            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            Log.e("Dialog", e.toString());
        }
    }
}

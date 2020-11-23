package ke.co.mobiticket.mobiticket.activities;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.adapters.AdapterPaymentMethods;
import ke.co.mobiticket.mobiticket.pojos.Passenger;
import ke.co.mobiticket.mobiticket.pojos.PaymentMethod;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.ConfirmReservationInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.GenerateReferenceNumberInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.ReserveTicketsInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.RetrievePaymentMethodsInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.ConfirmReservationRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.GenerateReferenceNumberRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.ReserveTicketsRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.RetrievePaymentMethodsRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.ConfirmReservationResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.GenerateReferenceNumberResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.ReserveTicketResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.RetrievePaymentMethodResponse;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;
import ke.co.mobiticket.mobiticket.widgets.LineItemDecoration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentMethodsActivity extends BaseActivity {
    private RecyclerView rvPaymentMethods;
    private TextView tvPaymentMethods,tvTitle;
    private ImageView ivBack;
    private double total_cost = 0.00;
    ProgressBar progressBar;
    SharedPreferences prefs;
    AdapterPaymentMethods mAdapter;
    List<Passenger> passengerList = new ArrayList<>();
    Gson gson = new Gson();
    Dialog dialog;
    private String reference_number="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);
        initLayouts();



        prefs = AppController.getInstance().getMobiPrefs();
        //Reset selected payment method
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.TICKET_PAYMENT_METHOD_ID, "");
        editor.apply();

        try {
            if (prefs.getString(Constants.PAYMENT_METHODS_REPO,"")==""||prefs.getString(Constants.PAYMENT_METHODS_REPO,"").isEmpty()){
                retrievePaymentMethods();
            }else {
                RetrievePaymentMethodResponse resp = new Gson().fromJson(prefs.getString(Constants.PAYMENT_METHODS_REPO,""), RetrievePaymentMethodResponse.class);
                displayPaymentMethods(resp);

            }

        }catch (Exception e){
            Log.e("get payment methods",e.toString());
        }




        initListeners();

        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.action_vehicles:
                        showCustomYesNoDialog("Ticket purchase in progress", "Your ticket data will be lost. \n\n Do you want to exit anyway?", item);
                        break;
                    case R.id.action_tickets:
                        showCustomYesNoDialog("Ticket purchase in progress", "Your ticket data will be lost. \n\n Do you want to exit anyway?", item);
                        break;
                    case R.id.action_more:
                        showCustomYesNoDialog("Ticket purchase in progress", "Your ticket data will be lost. \n\n Do you want to exit anyway?", item);
                        break;

                }
                return true;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();


        //Check if reference number is set, if not generate new
        if (AppController.getInstance().isNetworkConnected()) {




            if (prefs.getString(Constants.TICKET_REFERENCE_NUMBER,"")==""||prefs.getString(Constants.TICKET_REFERENCE_NUMBER,"").isEmpty()) {
                generateReferenceNumber();
            }else {
                Toast.makeText(this, "Reference number "+ prefs.getString(Constants.TICKET_REFERENCE_NUMBER,"")+" is set. No need to regenerate", Toast.LENGTH_SHORT).show();
            }


        } else {
            startActivity(NoInternetActivity.class);
        }
    }

    private void initListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGoHome(getResources().getString(R.string.text_ticket_purchase_in_progress),getResources().getString(R.string.text_exit));

            }
        });
    }

    private void retrievePaymentMethods() {
        RetrievePaymentMethodsInterface api = AppController.getInstance().getRetrofit().create(RetrievePaymentMethodsInterface.class);
        RetrievePaymentMethodsRequest request = new RetrievePaymentMethodsRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.READ_ACTION);
        dialog = new Dialog(PaymentMethodsActivity.this);
        String message = "Retrieving payment methods\n\nPlease wait.....";
        showProgressDialog(dialog, message);

        Call<RetrievePaymentMethodResponse> call = api.retrievePaymentMethods(request);
        call.enqueue(new Callback<RetrievePaymentMethodResponse>() {
            @Override
            public void onResponse(Call<RetrievePaymentMethodResponse> call, Response<RetrievePaymentMethodResponse> response) {
                dialog.dismiss();
                if (response.body() != null) {
                    RetrievePaymentMethodResponse resp = response.body();
                    if (resp.getResponse_code().equals("0")) {
                        SharedPreferences.Editor editor = prefs.edit();

                        editor.putString(Constants.PAYMENT_METHODS_REPO, new Gson().toJson(resp));
                        editor.apply();
           displayPaymentMethods(resp);

                    } else {
                        showCustomDialog("Payment methods", resp.getResponse_message());
                    }

                } else {
                    showCustomDialog("Retrieve Payment Methods", "An error occurred while retrieving payment methods. Please try again");
                }
            }

            @Override
            public void onFailure(Call<RetrievePaymentMethodResponse> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }

    private void displayPaymentMethods(RetrievePaymentMethodResponse resp) {

        List<PaymentMethod> paymentMethods = resp.getPaymentmethod();



        for (PaymentMethod paymentMethod : paymentMethods) {
            Log.e("method", paymentMethod.getName());
        }
        try {
            //set data and list adapter
            mAdapter = new AdapterPaymentMethods(PaymentMethodsActivity.this, resp.getPaymentmethod());
            rvPaymentMethods.setAdapter(mAdapter);
            RunLayoutAnimation(rvPaymentMethods);

            // on item list clicked
            mAdapter.setOnItemClickListener(new AdapterPaymentMethods.OnItemClickListener() {
                @Override
                public void onItemClick(View view, PaymentMethod obj, int position) {
                    String     payment_method_id = obj.getId();
                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString(Constants.TICKET_PAYMENT_METHOD_ID, payment_method_id);
                    editor.apply();
                    try {
                        showCustomYesNoDialog(obj.getName(), "Use " + obj.getName() + " to pay your fare now?", payment_method_id);
                    } catch (Exception e) {
                        Log.e("dialog p/meth", e.toString());
                    }

                }
            });
        } catch (Exception e) {
            Log.e("Error", e.toString());
        }
    }

    private void initLayouts() {
        ivBack = findViewById(R.id.ivBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("Payment Methods");

        rvPaymentMethods = findViewById(R.id.rvPaymentMethods);
        rvPaymentMethods.setLayoutManager(new LinearLayoutManager(this));
        rvPaymentMethods.addItemDecoration(new LineItemDecoration(this, LinearLayout.VERTICAL));
        rvPaymentMethods.setHasFixedSize(true);

//tvPaymentMethods =findViewById(R.id.tvPaymentMethods);

        progressBar = findViewById(R.id.progressBar);
    }

    public void showCustomYesNoDialog(String title, String message, final String payment_method_id) {
        try {


            final Dialog dialog = new Dialog(PaymentMethodsActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_accept_payment_method);
            dialog.setCancelable(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            CircularImageView card_logo = dialog.findViewById(R.id.image);
            if (payment_method_id.equals("8")) {
                card_logo.setImageResource(R.drawable.ic_visa_new);
            } else if (payment_method_id.equals("9")) {
                card_logo.setImageResource(R.drawable.ic_mastercard_new);
            } else if (payment_method_id.equals("4")) {
                card_logo.setImageResource(R.drawable.ic_jambopay_agent);
            } else if (payment_method_id.equals("3")) {
                card_logo.setImageResource(R.drawable.ic_jambopay_wallet);
            } else if (payment_method_id.equals("7")) {
                card_logo.setImageResource(R.drawable.pesalink);
            } else if (payment_method_id.equals("2")) {
                card_logo.setImageResource(R.drawable.ic_mpesa);
            } else if (payment_method_id.equals("1")) {
                card_logo.setImageResource(R.drawable.ic_mpesa);
            } else if (payment_method_id.equals("6")) {
                card_logo.setImageResource(R.drawable.mticket_green);
            } else if (payment_method_id.equals("5")) {
                card_logo.setImageResource(R.drawable.mticket_green);
            } else if (payment_method_id.equals("10")) {
                card_logo.setImageResource(R.drawable.mticket_green);
            }

            TextView tvTitle = dialog.findViewById(R.id.title);
            TextView tvTickets = dialog.findViewById(R.id.tvTickets);
            TextView tvTotalCost = dialog.findViewById(R.id.tvTotalCost);
            TextView tvContent = dialog.findViewById(R.id.content);
            tvContent.setText(message);
            tvTitle.setText(title);
            total_cost = Double.valueOf(prefs.getString(Constants.TICKET_VEHICLE_CURRENT_FARE, ""));
//            tvTickets.setText("Tickets: "+ passengerList.size());
            tvTotalCost.setText("KES " + String.format("%.2f", total_cost));

            ((ImageButton) dialog.findViewById(R.id.bt_exit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });
            ((Button) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });
            ((Button) dialog.findViewById(R.id.bt_yes)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Passenger passenger = new Passenger();
                    passenger.setVehicle_id(prefs.getString(Constants.TICKET_VEHICLE_ID, ""));
                    passenger.setTotal_fare(prefs.getString(Constants.TICKET_VEHICLE_CURRENT_FARE, ""));
                    passenger.setOperator_id(prefs.getString(Constants.TICKET_VEHICLE_OPERATOR_ID, ""));
                    passenger.setTrip_number(prefs.getString(Constants.TICKET_VEHICLE_TRIP_NUMBER, ""));
                    passenger.setTicketing_agent_id(prefs.getString(Constants.ID, ""));
                    passenger.setSource(Constants.SOURCE);
                    passenger.setTravel_from(prefs.getString(Constants.TICKET_TRAVEL_FROM, ""));
                    passenger.setPickup_point(prefs.getString(Constants.TICKET_PICKUP_POINT, ""));
                    passenger.setTravel_to(prefs.getString(Constants.TICKET_TRAVEL_TO, ""));
                    passenger.setDropoff_point(prefs.getString(Constants.TICKET_DROPOFF_POINT, ""));
                    passenger.setPayment_method_id(payment_method_id);
                    passenger.setSeat("Free Seating"); //TODO : remove this hard coded data
                    passenger.setEmail_address(prefs.getString(Constants.EMAIL_ADDRESS, ""));
                    passenger.setMsisdn(prefs.getString(Constants.PHONE_NUMBER, ""));
                    passenger.setFirst_name(prefs.getString(Constants.FIRST_NAME, ""));
                    passenger.setMiddle_name(prefs.getString(Constants.MIDDLE_NAME, ""));
                    passenger.setLast_name(prefs.getString(Constants.LAST_NAME, ""));

                    passenger.setReference_number(prefs.getString(Constants.TICKET_REFERENCE_NUMBER, ""));
                    passengerList.add(passenger);

                    if (prefs.getBoolean(Constants.TICKET_IS_RESERVED, false)){
                        Toast.makeText(PaymentMethodsActivity.this, "Ticket is already reserved", Toast.LENGTH_SHORT).show();

                        //Ensure payment method is set
                        if (prefs.getString(Constants.TICKET_PAYMENT_METHOD_ID,"").equals("")||prefs.getString(Constants.TICKET_PAYMENT_METHOD_ID,"").isEmpty()){
                            Toast.makeText(PaymentMethodsActivity.this, "Please select payment method", Toast.LENGTH_SHORT).show();
                        }else {
                            passenger.setPayment_method_id(prefs.getString(Constants.TICKET_PAYMENT_METHOD_ID, ""));
                            startActivity(PaymentActivity.class);
                        }

                    }else {
                        if (passengerList.size()>0){
                            reserveTickets(passengerList);
                        }
                        Toast.makeText(PaymentMethodsActivity.this, "Ticket will be reserved", Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                }
            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            Log.e("Dialog", e.toString());
        }
    }

    private void reserveTickets(final List<Passenger> passengerList) {
        ReserveTicketsInterface api = AppController.getInstance().getRetrofit().create(ReserveTicketsInterface.class);
        ReserveTicketsRequest request = new ReserveTicketsRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.CREATE_ACTION);
        request.setTicket(passengerList);
        Log.e("tickets", gson.toJson(passengerList));
        Log.e("request", gson.toJson(request));
        Call<ReserveTicketResponse> call = api.reserveTickets(request);
        dialog = new Dialog(PaymentMethodsActivity.this);
        String message = "Making reservation"+ getResources().getString(R.string.txt_please_wait);
        showProgressDialog(dialog, message);
//        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ReserveTicketResponse>() {
            @Override
            public void onResponse(Call<ReserveTicketResponse> call, Response<ReserveTicketResponse> response) {
                dialog.dismiss();
                if (response.body() != null) {
                    if (response.body().getResponse_code().equals("0")) {
                        Toast.makeText(PaymentMethodsActivity.this, response.body().getResponse_message(), Toast.LENGTH_SHORT).show();
                        String reference_number = response.body().getReference_number();
                        confirmReservation(reference_number, passengerList);


                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean(Constants.TICKET_IS_RESERVED, true);
                        editor.apply();


                        startActivity(PaymentActivity.class);

                    } else {
//                        SharedPreferences.Editor editor = prefs.edit();
//
//                        editor.putString(Constants.TICKET_PAYMENT_METHOD_ID, payment_method_id);
//                        editor.apply();
//                        startActivity(PaymentActivity.class);
                        showCustomDialog("Reserve tickets", response.body().getResponse_message());
                    }

                } else {
                    showCustomDialog("Reserve tickets", "An error occured!");

                }
            }

            @Override
            public void onFailure(Call<ReserveTicketResponse> call, Throwable t) {
                dialog.dismiss();
                Log.e("the error", Objects.requireNonNull(t.getLocalizedMessage()));
            }
        });

    }

    private void confirmReservation(final String reference_number, final List<Passenger> passengerList) {
        final Dialog dialog = new Dialog(PaymentMethodsActivity.this);
        ConfirmReservationInterface api = AppController.getInstance().getRetrofit().create(ConfirmReservationInterface.class);
        ConfirmReservationRequest request = new ConfirmReservationRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.SEARCH_ACTION);
        request.setKeywords(reference_number);

        Call<ConfirmReservationResponse> call = api.confirmTicketReservation(request);
        showProgressDialog(dialog, "Confirming reservation.\n\nPlease wait....");
        call.enqueue(new Callback<ConfirmReservationResponse>() {
            @Override
            public void onResponse(Call<ConfirmReservationResponse> call, Response<ConfirmReservationResponse> response) {
                dialog.dismiss();
                if (response.body() != null) {
                    if (response.body().getResponse_code().equals("0")) {
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(Constants.PASSENGER_DATA_THIS_BOOKING, gson.toJson(passengerList));
                        editor.apply();

                        startActivity(PaymentActivity.class);
                        finish();
                    } else {
                        showCustomDialog("Confirm reservation", response.body().getResponse_message());
                    }
                }
            }

            @Override
            public void onFailure(Call<ConfirmReservationResponse> call, Throwable t) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private void generateReferenceNumber() {
        final Dialog dialog = new Dialog(PaymentMethodsActivity.this);
        //Generate reference number for the tickets
        GenerateReferenceNumberInterface api = AppController.getInstance().getRetrofit().create(GenerateReferenceNumberInterface.class);
        GenerateReferenceNumberRequest request = new GenerateReferenceNumberRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.CREATE_REF_NUMBER_ACTION);
        showProgressDialog(dialog, "Generate reference number\n\nPlease wait.....");
        Call<GenerateReferenceNumberResponse> call = api.generateReferenceNumber(request);
        call.enqueue(new Callback<GenerateReferenceNumberResponse>() {
            @Override
            public void onResponse(Call<GenerateReferenceNumberResponse> call, Response<GenerateReferenceNumberResponse> response) {
                dialog.dismiss();
                if (response.body() != null) {
                    GenerateReferenceNumberResponse referenceNumberResponse = response.body();

                    if (referenceNumberResponse.getResponse_code().equals("0")) {
                         String new_reference_number = referenceNumberResponse.getReference_number();
                                                SharedPreferences.Editor editor = prefs.edit();

                        editor.putString(Constants.TICKET_REFERENCE_NUMBER, new_reference_number);
                        editor.apply();
                        startActivity(PaymentActivity.class);
                    } else {
                        showCustomDialog("Generating reference number", referenceNumberResponse.getResponse_message());
                    }
                }
            }

            @Override
            public void onFailure(Call<GenerateReferenceNumberResponse> call, Throwable t) {
                dialog.dismiss();
            }
        });

    }

    public void showCustomYesNoDialog(String title, String message, final MenuItem item) {
        try {


            final Dialog dialog = new Dialog(PaymentMethodsActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_exit_activity_or_no);
            dialog.setCancelable(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


            TextView tvTitle = dialog.findViewById(R.id.title);
            TextView tvContent = dialog.findViewById(R.id.content);
            tvContent.setText(message);
            tvTitle.setText(title);

            dialog.findViewById(R.id.bt_no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.bt_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (item.getItemId()) {
                        case R.id.action_vehicles:
                            resetTicketPreferences(prefs);
                            startActivity(MyVehiclesActivity.class);
                            finish();
                            break;
                        case R.id.action_tickets:
//                            resetTicketPreferences(prefs);
                            startActivity(TicketsActivity.class);
                            finish();
                            break;
                        case R.id.action_more:
//                            resetTicketPreferences(prefs);
                            startActivity(MoreActivity.class);
                            finish();
                            break;
                    }

                }
            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            Log.e("Dialog", e.toString());
        }
    }

//    private void resetTicketPreferences(SharedPreferences prefs) {
//        SharedPreferences.Editor editor = prefs.edit();
//
//        editor.putString(Constants.TICKET_REFERENCE_NUMBER, "");
//        editor.putBoolean(Constants.TICKET_IS_RESERVED, false);
//        editor.putString(Constants.PASSENGER_DATA_THIS_BOOKING, null);
//        editor.putString(Constants.TICKET_PAYMENT_METHOD_ID, "");
//        editor.apply();
//    }

    public void showGoHome(String title, String message) {
        try {


            final Dialog dialog = new Dialog(PaymentMethodsActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_exit_activity_or_no);
            dialog.setCancelable(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


            TextView tvTitle = dialog.findViewById(R.id.title);
            TextView tvContent = dialog.findViewById(R.id.content);
            tvContent.setText(message);
            tvTitle.setText(title);

            dialog.findViewById(R.id.bt_no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.bt_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetTicketPreferences(prefs);
                    startActivity(DashboardActivity.class);
                    finish();

                }
            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            Log.e("Dialog", e.toString());
        }
    }
    private void resetTicketPreferences(SharedPreferences prefs) {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(Constants.TICKET_REFERENCE_NUMBER, "");
        editor.putBoolean(Constants.TICKET_IS_RESERVED, false);
        editor.putString(Constants.PASSENGER_DATA_THIS_BOOKING, null);
        editor.putString(Constants.TICKET_PAYMENT_METHOD_ID, "");
        editor.apply();
    }
}

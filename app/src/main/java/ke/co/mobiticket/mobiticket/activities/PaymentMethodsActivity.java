package ke.co.mobiticket.mobiticket.activities;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.adapters.AdapterPaymentMethods;
import ke.co.mobiticket.mobiticket.pojos.Passenger;
import ke.co.mobiticket.mobiticket.pojos.PaymentMethod;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.ConfirmReservationInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.ReserveTicketsInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.RetrievePaymentMethodsInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.ConfirmReservationRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.ReserveTicketsRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.RetrievePaymentMethodsRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.ConfirmReservationResponse;
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
    private TextView tvPaymentMethods;
    ProgressBar progressBar;
    SharedPreferences prefs;
    AdapterPaymentMethods mAdapter;
    List<Passenger> passengerList;
    Gson gson = new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);
        initLayouts();
        prefs = AppController.getInstance().getMobiPrefs();
        String passengerListString = prefs.getString(Constants.PASSENGER_DATA_THIS_BOOKING, "");
        Log.e("retrievePassengers", passengerListString);
        if (passengerListString.isEmpty() || passengerListString.equals("")) {
            finish();
        } else {
            Gson gson = new Gson();
            passengerList = Arrays.asList(new GsonBuilder().create().fromJson(passengerListString, Passenger[].class));

            for (Passenger passenger : passengerList) {
                Toast.makeText(this, passenger.getEmail_address(), Toast.LENGTH_SHORT).show();
            }
        }

        retrievePaymentMethods();
    }

    private void retrievePaymentMethods() {
        RetrievePaymentMethodsInterface api = AppController.getInstance().getRetrofit().create(RetrievePaymentMethodsInterface.class);
        RetrievePaymentMethodsRequest request = new RetrievePaymentMethodsRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.READ_ACTION);
        progressBar.setVisibility(View.VISIBLE);

        Call<RetrievePaymentMethodResponse> call = api.retrievePaymentMethods(request);
        call.enqueue(new Callback<RetrievePaymentMethodResponse>() {
            @Override
            public void onResponse(Call<RetrievePaymentMethodResponse> call, Response<RetrievePaymentMethodResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body() != null) {
                    RetrievePaymentMethodResponse resp = response.body();
                    if (resp.getResponse_code().equals("0")) {
                        List<PaymentMethod> paymentMethods = resp.getPaymentmethod();

                        for (PaymentMethod paymentMethod : paymentMethods) {
                            Log.e("method", paymentMethod.getName());
                        }
                        try {
                            //set data and list adapter
                            mAdapter = new AdapterPaymentMethods(PaymentMethodsActivity.this, resp.getPaymentmethod());
                            rvPaymentMethods.setAdapter(mAdapter);

                            // on item list clicked
                            mAdapter.setOnItemClickListener(new AdapterPaymentMethods.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, PaymentMethod obj, int position) {
                                    String payment_method_id = obj.getId();
                                    showCustomYesNoDialog(obj.getName(), "Use this methd to pay now?", payment_method_id);
                                    Toast.makeText(PaymentMethodsActivity.this, obj.getName(), Toast.LENGTH_SHORT).show();


                                }
                            });
                        } catch (Exception e) {
                            Log.e("Error", e.toString());
                        }

                    } else {
                        showCustomDialog("Payment methods", resp.getResponse_message());
                    }

                } else {
                    showCustomDialog("Retrieve Payment Methods", "An error occured while retrieveing payment methods. Please try again");
                }
            }

            @Override
            public void onFailure(Call<RetrievePaymentMethodResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void initLayouts() {
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
            dialog.setContentView(R.layout.dialog_select_yes_or_no);
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
                    Toast.makeText(PaymentMethodsActivity.this, ((AppCompatButton) v).getText().toString() + " Clicked", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });
            ((Button) dialog.findViewById(R.id.bt_yes)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    try {
                        for (Passenger passenger : passengerList) {
                            int seat=1;

                            //Set other vehicle parameters to ticket

                            //Set payment method to tickets
                            passenger.setVehicle_id(prefs.getString(Constants.TICKET_VEHICLE_ID, ""));
                            passenger.setTotal_fare(prefs.getString(Constants.TICKET_TOTAL_FARE, ""));
                            passenger.setOperator_id(prefs.getString(Constants.TICKET_VEHICLE_OPERATOR_ID, ""));
                            passenger.setTrip_number(prefs.getString(Constants.TICKET_VEHICLE_TRIP_NUMBER, ""));
                            passenger.setTicketing_agent_id(prefs.getString(Constants.ID, ""));
                            passenger.setSource(Constants.SOURCE);
                            passenger.setTravel_from(prefs.getString(Constants.TICKET_TRAVEL_FROM, ""));
                            passenger.setPickup_point(prefs.getString(Constants.TICKET_PICKUP_POINT, ""));
                            passenger.setTravel_to(prefs.getString(Constants.TICKET_TRAVEL_TO, ""));
                            passenger.setDropoff_point(prefs.getString(Constants.TICKET_DROPOFF_POINT, ""));
                            passenger.setPayment_method_id(payment_method_id);
                            passenger.setSeat(String.valueOf(seat++)); //TODO : remove this hard coded data

                        }

                        if (AppController.getInstance().isNetworkConnected()) {
                            try {
                                reserveTickets(passengerList, payment_method_id);
                            } catch (Exception e) {

                            }

                        } else {
                            startActivity(NoInternetActivity.class);
                        }


                    } catch (Exception e) {
                        Log.e("error", e.toString());
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

    private void reserveTickets(final List<Passenger> passengerList, final String payment_method_id) {
        ReserveTicketsInterface api = AppController.getInstance().getRetrofit().create(ReserveTicketsInterface.class);
        ReserveTicketsRequest request = new ReserveTicketsRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.CREATE_ACTION);
        request.setTicket(passengerList);
        Log.e("tickets", gson.toJson(passengerList));
        Log.e("request", gson.toJson(request));
        Call<ReserveTicketResponse> call = api.reserveTickets(request);

        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ReserveTicketResponse>() {
            @Override
            public void onResponse(Call<ReserveTicketResponse> call, Response<ReserveTicketResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body() != null) {
                    if (response.body().getResponse_code().equals("0")) {
                        Toast.makeText(PaymentMethodsActivity.this, response.body().getResponse_message(), Toast.LENGTH_SHORT).show();
                        String reference_number = response.body().getReference_number();
                        confirmReservation(reference_number, payment_method_id);


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
                progressBar.setVisibility(View.GONE);
                Log.e("the error", Objects.requireNonNull(t.getLocalizedMessage()));
            }
        });

    }

    private void confirmReservation(final String reference_number, final String payment_method_id) {
        ConfirmReservationInterface api = AppController.getInstance().getRetrofit().create(ConfirmReservationInterface.class);
        ConfirmReservationRequest request = new ConfirmReservationRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.SEARCH_ACTION);
        request.setKeywords(reference_number);

        Call<ConfirmReservationResponse> call = api.confirmTicketReservation(request);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ConfirmReservationResponse>() {
            @Override
            public void onResponse(Call<ConfirmReservationResponse> call, Response<ConfirmReservationResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body() != null) {
                    if (response.body().getResponse_code().equals("0")) {
                        SharedPreferences.Editor editor = prefs.edit();

                        editor.putString(Constants.TICKET_PAYMENT_METHOD_ID, payment_method_id);
                        editor.putString(Constants.TICKET_REFERENCE_NUMBER, reference_number);

                        editor.apply();
                        startActivity(PaymentActivity.class);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<ConfirmReservationResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        //No back press
//    }
}

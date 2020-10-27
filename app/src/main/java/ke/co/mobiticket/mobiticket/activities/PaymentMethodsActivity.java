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

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.adapters.AdapterPaymentMethods;
import ke.co.mobiticket.mobiticket.pojos.Passenger;
import ke.co.mobiticket.mobiticket.pojos.PaymentMethod;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.ReserveTicketsInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.RetrievePaymentMethodsInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.ReserveTicketsRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.RetrievePaymentMethodsRequest;
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
    Gson gson=new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_methods);
        initLayouts();
        prefs= AppController.getInstance().getMobiPrefs();
     String   passengerListString=prefs.getString(Constants.PASSENGER_DATA_THIS_BOOKING,"");
     if (passengerListString.isEmpty()||passengerListString.equals("")){
         finish();
     }else {
         Gson gson=new Gson();
   passengerList= Arrays.asList(new GsonBuilder().create().fromJson(passengerListString,Passenger[].class));

    for (Passenger passenger: passengerList){
        Toast.makeText(this, passenger.getEmail_address(), Toast.LENGTH_SHORT).show();
    }
     }

        retrievePaymentMethods();
    }

    private void retrievePaymentMethods() {
        RetrievePaymentMethodsInterface api= AppController.getInstance().getRetrofit().create(RetrievePaymentMethodsInterface.class);
        RetrievePaymentMethodsRequest request=new RetrievePaymentMethodsRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.READ_ACTION);
        progressBar.setVisibility(View.VISIBLE);

        Call<RetrievePaymentMethodResponse> call=api.retrievePaymentMethods(request);
        call.enqueue(new Callback<RetrievePaymentMethodResponse>() {
            @Override
            public void onResponse(Call<RetrievePaymentMethodResponse> call, Response<RetrievePaymentMethodResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body() !=null){
                    RetrievePaymentMethodResponse resp=response.body();
                    if (resp.getResponse_code().equals("0")){
                        List<PaymentMethod> paymentMethods=resp.getPaymentmethod();

                        for (PaymentMethod paymentMethod:paymentMethods){
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
                                    String payment_method_id=obj.getId();
                                    showCustomYesNoDialog(obj.getName(), "Use this methd to pay now?", payment_method_id);
                                    Toast.makeText(PaymentMethodsActivity.this, obj.getName(), Toast.LENGTH_SHORT).show();


                                }
                            });
                        }catch (Exception e){
                            Log.e("Error", e.toString());
                        }

                    }else {
                        showCustomDialog("Payment methods", resp.getResponse_message());
                    }

                }else {
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
rvPaymentMethods =findViewById(R.id.rvPaymentMethods);
        rvPaymentMethods.setLayoutManager(new LinearLayoutManager(this));
        rvPaymentMethods.addItemDecoration(new LineItemDecoration(this, LinearLayout.VERTICAL));
        rvPaymentMethods.setHasFixedSize(true);

//tvPaymentMethods =findViewById(R.id.tvPaymentMethods);

progressBar=findViewById(R.id.progressBar);
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
                        for (Passenger passenger: passengerList){
                            passenger.setPayment_method_id(payment_method_id);
                        }

                        if (AppController.getInstance().isNetworkConnected()){
                            try {
                                reserveTickets(passengerList);
                            }catch (Exception e){

                            }

                        }else {
                            showCustomDialog(Constants.NO_INTERNET_TITLE, Constants.NO_INTERNET_MESSAGE);
                        }


                    }catch (Exception e){
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

    private void reserveTickets(final List<Passenger> passengerList) {
        ReserveTicketsInterface api=AppController.getInstance().getRetrofit().create(ReserveTicketsInterface.class);
        ReserveTicketsRequest request=new ReserveTicketsRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.CREATE_ACTION);
        request.setAction(gson.toJson(passengerList));
        Log.e("tickets",gson.toJson(passengerList));
        Call<ReserveTicketResponse> call=api.reserveTickets(request);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ReserveTicketResponse>() {
            @Override
            public void onResponse(Call<ReserveTicketResponse> call, Response<ReserveTicketResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body() !=null){
                    if (response.body().getResponse_code().equals("0")){
                        passengerList.clear();

                        SharedPreferences.Editor editor = prefs.edit();

                        editor.putString(Constants.PASSENGER_DATA_THIS_BOOKING,null);

                        editor.apply();
                        Toast.makeText(PaymentMethodsActivity.this, response.body().getResponse_message(), Toast.LENGTH_SHORT).show();

                    }else {
                        showCustomDialog("Reserve tickets", response.body().getResponse_message());
                    }

                }else {
                    showCustomDialog("Reserve tickets", "An error occured!");

                }
            }

            @Override
            public void onFailure(Call<ReserveTicketResponse> call, Throwable t) {
progressBar.setVisibility(View.GONE);
                Log.e("the error", t.getLocalizedMessage());
            }
        });

    }


}

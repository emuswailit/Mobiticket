package ke.co.mobiticket.mobiticket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.adapters.TicketsAdapter;
import ke.co.mobiticket.mobiticket.pojos.Ticket;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.SearchTicketInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.SearchTicketRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.SearchTicketResponse;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingPaybillActivity extends BaseActivity {
SharedPreferences prefs;
Timer timer;
private TextView tvTitle, tvInstructions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_paybill);
        prefs= AppController.getInstance().getMobiPrefs();
        initLayouts();
        String reference_number=prefs.getString(Constants.PENDING_MPESA_PAYBILL_REF_NUMBER,"");
        if (reference_number.isEmpty()||reference_number.equals("")){

        }else {
            callAsynchronousTask(reference_number);
        }

    }

    private void initLayouts() {
        tvInstructions=findViewById(R.id.tvInstructions);
        tvInstructions.setText(prefs.getString(Constants.PENDING_MPESA_PAYBILL_MESSAGE,""));
        tvTitle=findViewById(R.id.tvTitle);
        tvTitle.setText("Confirm Payment");
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


                            searchTicket(reference_number, timer);


                        } catch (Exception e) {

                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5 * 1000); //execute in every 5000 ms
    }

    private void searchTicket(final String reference_number, final Timer timer) {
        final Dialog dialog= new Dialog(PendingPaybillActivity.this);
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

                dialog.dismiss();
                if (response.body() != null) {
                    if (response.body().getResponse_code().equals("0")) {
                        Toast.makeText(PendingPaybillActivity.this, "Done", Toast.LENGTH_SHORT).show();

                    } else {
//                        showPaymentErrorDialog("Ticket Payment Status", response.body().getResponse_message());
                    }

                } else {
//                    showPaymentErrorDialog("Search Ticket", "Error occurred while searching for tickets");
                }
            }

            @Override
            public void onFailure(Call<SearchTicketResponse> call, Throwable t) {
                dialog.dismiss();

            }
        });


    }
}

package ke.co.mobiticket.mobiticket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.adapters.BusAdapter;
import ke.co.mobiticket.mobiticket.pojos.Vehicle;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.SearchVehiclesInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerSearchVehiclesRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerSearchVehiclesResponse;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusListActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView mRvBuses;
    private List<Vehicle> mBusList;
    private ImageView mIvBack, mIvFilter, mIvPrevious, mIvNext;
    private TextView mTvDate, mTvTitle, tvAvailableBus;
    private Calendar mDepartDateCalendar;
    ProgressBar progressBar;
    Gson gson = new Gson();
    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_list);
        prefs = AppController.getInstance().getMobiPrefs();
        initLayouts();
        initializeListeners();

        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.action_vehicles:
                        startActivity(MyVehiclesActivity.class);
                        break;
                    case R.id.action_tickets:
                        startActivity(TicketsActivity.class);

                        break;
                    case R.id.action_more:
                        startActivity(MoreActivity.class);

                }
                return true;
            }
        });
    }

    private void initializeListeners() {
        mIvBack.setOnClickListener(this);
        mIvFilter.setOnClickListener(this);
//        mIvPrevious.setOnClickListener(this);
//        mIvNext.setOnClickListener(this);
        String from = getIntent().getStringExtra(Constants.intentdata.FROM);
        String to = getIntent().getStringExtra(Constants.intentdata.TO);

        if (AppController.getInstance().isNetworkConnected()) {
            searchBuses(from, to);
        } else {
            startActivity(NoInternetActivity.class);
        }


        String mTitle = getIntent().getStringExtra(Constants.intentdata.TRIP_KEY);
        String mSearchTitle = getIntent().getStringExtra(Constants.intentdata.SEARCH_BUS);
        String mPackageTitle = getIntent().getStringExtra(Constants.intentdata.PACKAGE_NAME);

        if (mTitle != null) {
            mTvTitle.setText(mTitle);
        }
        if (mSearchTitle != null) {
            mTvTitle.setText(mSearchTitle);
        }
        if (mPackageTitle != null) {
            mTvTitle.setText(mPackageTitle);
        }

        mBusList = new ArrayList<>();
        mRvBuses.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        mDepartDateCalendar = Calendar.getInstance();
        mTvDate.setText(Constants.DateFormat.DAY_MONTH_YEAR_FORMATTER.format(mDepartDateCalendar.getTime()));

    }

    private void searchBuses(String from, String to) {
        final Dialog dialog = new Dialog(BusListActivity.this);
        try {
            SearchVehiclesInterface api = AppController.getInstance().getRetrofit().create(SearchVehiclesInterface.class);
            ServerSearchVehiclesRequest request = new ServerSearchVehiclesRequest();
            request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
            request.setTravel_from(from);
            request.setTravel_to(to);
            request.setAction(Constants.SEARCH_VEHICLES);
            Call<ServerSearchVehiclesResponse> call = api.searchVehicles(request);
            String message = "Retrieving buses" + getResources().getString(R.string.txt_please_wait);
            showProgressDialog(dialog, message);
            call.enqueue(new Callback<ServerSearchVehiclesResponse>() {

                @Override
                public void onResponse(Call<ServerSearchVehiclesResponse> call, Response<ServerSearchVehiclesResponse> response) {
                    Log.e("body", gson.toJson(response.body()));
                    dialog.dismiss();
                    if (response.body() != null) {


                        ServerSearchVehiclesResponse serverSearchVehiclesResponse = response.body();
                        if (response.body().getResponse_code().equals("0")) {
                            if (serverSearchVehiclesResponse.getVehicle().size() >= 1) {
                                tvAvailableBus.setText(serverSearchVehiclesResponse.getVehicle().size() + " buses available!");
                                mBusList = serverSearchVehiclesResponse.getVehicle();
                                try {
                                    BusAdapter adapter = new BusAdapter(BusListActivity.this, mBusList);
                                    mRvBuses.setAdapter(adapter);
                                    RunLayoutAnimation(mRvBuses);
                                    adapter.setOnItemClickListener(new BusAdapter.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(View view, Vehicle obj, int position) {

                                            Gson gson = new Gson();
                                            Log.e("vehicle", gson.toJson(obj));
                                            SharedPreferences.Editor editor = prefs.edit();


                                            editor.putString(Constants.TICKET_VEHICLE_ID, obj.getVehicle_id());
                                            editor.putString(Constants.TICKET_VEHICLE_CURRENT_FARE, obj.getCurrent_fare());
                                            editor.putString(Constants.TICKET_VEHICLE_OPERATOR_ID, obj.getOperator_id());
                                            editor.putString(Constants.TICKET_VEHICLE_TRIP_NUMBER, obj.getTrip_number());

                                            editor.apply();

                                            //Move straight to payment methods
                                            startActivity(PaymentMethodsActivity.class);

                                            //Deprecating this activity so that phone owner can only pay for self
//                                            startActivity(PassengerDetailActivity.class);
                                        }
                                    });

                                } catch (Exception e) {
                                    Log.e("error", e.toString());
                                }

                            }
                        } else {
                            String title = "Search vehicles";
                            String message = serverSearchVehiclesResponse.getResponse_message();
                            showCustomDialog(title, message);
                        }
                    } else {
                        Log.e("Search vehicles", "null response");
                    }

                }

                @Override
                public void onFailure(Call<ServerSearchVehiclesResponse> call, Throwable t) {
                    dialog.dismiss();
                    Log.e("Search vehicles error", t.toString());
                }
            });


        } catch (Exception e) {
            Log.e("searchVehicles", e.toString());
        }

    }

    private void reserveVehicle(Vehicle obj) {


    }

    private void initLayouts() {
        tvAvailableBus = findViewById(R.id.tvAvailableBus);
        progressBar = findViewById(R.id.progressBar);
        mRvBuses = findViewById(R.id.rvBus);
        mIvBack = findViewById(R.id.ivBack);
        mIvFilter = findViewById(R.id.ivFilter);
        mIvPrevious = findViewById(R.id.ivPrevious);
        mIvNext = findViewById(R.id.ivNext);
        mTvDate = findViewById(R.id.tvDate);
        mTvTitle = findViewById(R.id.tvTitle);
    }

    @Override
    public void onClick(View v) {
        if (v == mIvBack) {
         startActivity(DashboardActivity.class);
         finish();
        } else if (v == mIvFilter) {
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_filter);
            dialog.setCancelable(true);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));


            final TextView tvMax = dialog.findViewById(R.id.endprice);
            final Button mBtnApply = dialog.findViewById(R.id.btnApply);
            ImageView mIvClose = dialog.findViewById(R.id.ivClose);

            mBtnApply.setStateListAnimator(null);
            mBtnApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            AppCompatSeekBar rangeSeekbar1 = dialog.findViewById(R.id.rangeSeekbar1);
            rangeSeekbar1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressChangedValue = 100;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressChangedValue = progress;
                    tvMax.setText(String.valueOf(progressChangedValue));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    tvMax.setText(String.valueOf(progressChangedValue));
                }
            });

            mIvClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();

        }

    }

}

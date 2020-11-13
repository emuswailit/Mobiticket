package ke.co.mobiticket.mobiticket.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.adapters.LazyAdapterBusStops;
import ke.co.mobiticket.mobiticket.adapters.RouteAdapter;
import ke.co.mobiticket.mobiticket.pojos.Route;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.SearchRouteInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.SearchRouteRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.SearchRoutesResponse;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DestinationsActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTitle,labelSearchRoutes;
    private ImageView btnSearchWhereTo;
    SharedPreferences prefs;
    private RecyclerView rvSearchRoutes, rvRecentSearch;
    private ProgressBar progressBar;
    private LinearLayout llRecentRoutes,llSearchedRoutes;
    MaterialCardView cardSearchRoutes;
    MaterialCardView cardSearchDestinations;
    EditText etWhereTo;
    Dialog dialog;
    List<Route> recentSearches = new ArrayList<>();
    List<String> routeIdsList=new ArrayList<>();

    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destinations);
        prefs=AppController.getInstance().getMobiPrefs();
        initLayouts();
        initListeners();
    }

    private void initListeners() {
btnSearchWhereTo.setOnClickListener(this);
    }

    private void initLayouts() {
        tvTitle=findViewById(R.id.tvTitle);

        tvTitle.setText("Buses and Destinations");

        llRecentRoutes = findViewById(R.id.llRecentRoutes);
        llSearchedRoutes = findViewById(R.id.llSearchedRoutes);

        cardSearchDestinations =findViewById(R.id.cardSearchDestination);

        btnSearchWhereTo = findViewById(R.id.btnSearchWhereTo);

        etWhereTo =findViewById(R.id.etWhereTo);

        rvSearchRoutes = findViewById(R.id.rvSearchRoutes);
        rvRecentSearch = findViewById(R.id.rvRecentSearch);
        labelSearchRoutes = findViewById(R.id.labelSearchRoutes);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSearchWhereTo:
               prepareForSearch();
                break;
        }
    }

    private void prepareForSearch() {

        String search = etWhereTo.getText().toString();
        if (!search.isEmpty() || !search.equals("")) {
            if (AppController.getInstance().isNetworkConnected()) {
                try {
                    searchWhereTo(search);
                } catch (Exception e) {
                    Log.e("Error:search", e.toString());
//                            Toast.makeText(getActivity(), "An error occurred!", Toast.LENGTH_SHORT).show();
                }

            } else {
//                        showCustomDialog(Constants.NO_INTERNET_TITLE, Constants.NO_INTERNET_MESSAGE);
                startActivity(new Intent(this, NoInternetActivity.class));
            }

        } else {
//                    Toast.makeText(getActivity(), "Enter your destination!", Toast.LENGTH_SHORT).show();
        }
    }

    private void searchWhereTo(final String search) {
        SearchRouteInterface api = AppController.getInstance().getRetrofit().create(SearchRouteInterface.class);
        SearchRouteRequest request = new SearchRouteRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setKeywords(search);
        request.setAction(Constants.SEARCH_ACTION);
        final Gson gson = new Gson();
        Log.e("clicked", gson.toJson(request));
//        progressBar.setVisibility(View.VISIBLE);
        dialog = new Dialog(DestinationsActivity.this);
        String message = "Retrieving destinations\n\nPlease wait...";
        showProgressDialog(dialog, message);
        Call<SearchRoutesResponse> call = api.retrieveRoutes(request);
        call.enqueue(new Callback<SearchRoutesResponse>() {
            @Override
            public void onResponse(Call<SearchRoutesResponse> call, Response<SearchRoutesResponse> response) {
//                progressBar.setVisibility(View.GONE);
                dialog.dismiss();
                SearchRoutesResponse searchRoutesResponse = response.body();
                if (response.body() != null) {
                    if (searchRoutesResponse.getResponse_code().equals("0")) {
//                        cardSearchRoutes.setVisibility(View.VISIBLE);
                        rvSearchRoutes.setLayoutManager(new LinearLayoutManager(DestinationsActivity.this));
//                    rvSearchRoutes.addItemDecoration(new LineItemDecoration(getActivity(), LinearLayout.VERTICAL));
                        rvSearchRoutes.setHasFixedSize(true);
                        //set data and list adapter

                        if (searchRoutesResponse.getRoute().size() > 0) {
                            llSearchedRoutes.setVisibility(View.VISIBLE);
                            RouteAdapter routeAdapter = new RouteAdapter(DestinationsActivity.this, searchRoutesResponse.getRoute());
                            rvSearchRoutes.setAdapter(routeAdapter);
                            routeAdapter.setOnItemClickListener(new RouteAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, Route obj, int position) {
//                                retrieveRouteDetail(obj.getId());
//                                cardSearchRoutes.setVisibility(View.GONE);
////                                cardSearchDestinations.setVisibility(View.GONE);
                                    Log.e("id", obj.getId());
                                    Log.e("from", obj.getOrigin());
                                    Log.e("to", obj.getDestination());
                                    showDialogPoints(obj);

                                }
                            });
                            labelSearchRoutes.setText(searchRoutesResponse.getRoute().size() + " routes retrieved for \" " + search + "\"");
                        } else {
                            labelSearchRoutes.setText("No routes retrieved for criteria \" " + search + " \" ");
                        }


                    } else {
                        String title = "Search Destinations";
                        String message = searchRoutesResponse.getResponse_message();
                        showCustomDialog(title, message);
                    }

                } else {
                    showCustomDialog("Search destinations", "An error occurred!");
                }
            }

            @Override
            public void onFailure(Call<SearchRoutesResponse> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
                dialog.dismiss();
                Log.e("Error", t.getLocalizedMessage());
            }
        });

    }

    private void showDialogPoints(final Route obj) {
        try {


            final Dialog dialog = new Dialog(DestinationsActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_points);
            dialog.setCancelable(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


            //Spinner for pickup point
            Spinner spPickUpPoint = dialog.findViewById(R.id.spPickUpPoint);
            LazyAdapterBusStops lazyPickupPoint = new LazyAdapterBusStops(DestinationsActivity.this, obj.getStop());
            lazyPickupPoint.notifyDataSetChanged();
            spPickUpPoint.setAdapter(lazyPickupPoint);
            spPickUpPoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    Log.e("pickuppoint", obj.getStop().get(position).getId());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(Constants.TICKET_PICKUP_POINT, obj.getStop().get(position).getName());
                    editor.apply();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            Spinner spDropoffPoint = dialog.findViewById(R.id.spDropoffPoint);
            LazyAdapterBusStops lazyDropOffPoint = new LazyAdapterBusStops(DestinationsActivity.this, obj.getStop());
            lazyDropOffPoint.notifyDataSetChanged();
            spDropoffPoint.setAdapter(lazyPickupPoint);
            spDropoffPoint.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    Log.e("dropoffpoint", obj.getStop().get(position).getId());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(Constants.TICKET_DROPOFF_POINT, obj.getStop().get(position).getName());
                    editor.apply();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            ((Button) dialog.findViewById(R.id.btnSubmit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                                    Only 10 recent searches will be remembered


                    try {
                        if (prefs.getString(Constants.TICKET_DROPOFF_POINT, "").equals(prefs.getString(Constants.TICKET_PICKUP_POINT, ""))) {
                            showCustomDialog("Origin and destination", "The two points need to be different");
                        } else {
                            for (Route route: recentSearches){
                                //Retrieve  IDS of all routes in recent searched list and save in a strings list
                                routeIdsList.add(route.getId());
                            }

                            //Check if route ID is in the list
                            if (routeIdsList.contains(obj.getId())){
                                //Route is already added to recents
                            }else {

                                //List of recent routes will be limited to 10 items long
                                if (recentSearches.size() < 10) {

                                    recentSearches.add(obj);
                                } else {
                                    recentSearches.remove(0);
                                    recentSearches.add(obj);
                                }
                            }


                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString(Constants.TICKET_TRAVEL_FROM, obj.getOrigin());
                            editor.putString(Constants.TICKET_TRAVEL_TO, obj.getDestination());
                            editor.putString(Constants.RECENT_ROUTES, gson.toJson(recentSearches));
                            editor.apply();

                            Intent intent = new Intent(DestinationsActivity.this, BusListActivity.class);
                            intent.putExtra(Constants.intentdata.TRIP_KEY, obj.getOrigin() + " To " + obj.getDestination());

                            startActivity(intent);
                            dialog.dismiss();
                        }
                    }catch (Exception e){
                        Log.e("recents",e.toString());
                    }

                }
            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            Log.e("Dialog", e.toString());
        }
    }
}

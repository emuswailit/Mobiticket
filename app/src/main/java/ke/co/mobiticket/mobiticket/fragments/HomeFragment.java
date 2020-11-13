package ke.co.mobiticket.mobiticket.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.activities.BaseActivity;
import ke.co.mobiticket.mobiticket.activities.BusListActivity;
import ke.co.mobiticket.mobiticket.activities.DashboardActivity;
import ke.co.mobiticket.mobiticket.activities.NoInternetActivity;
import ke.co.mobiticket.mobiticket.activities.PaymentMethodsActivity;
import ke.co.mobiticket.mobiticket.adapters.LazyAdapterBusStops;
import ke.co.mobiticket.mobiticket.adapters.RouteAdapter;
import ke.co.mobiticket.mobiticket.pojos.Route;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.ReadOneInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.RouteDetailInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.SearchRouteInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.RouteDetailsRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.SearchRouteRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerReadOneRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.RouteDetailsResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.SearchRoutesResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerReadOneResponse;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    public static String mTitle = "Mobiticket";
    private AutoCompleteTextView mEdFromCity, mEdToCity;
    private EditText etWhereTo,etSearchVehicle;
    private TextView mEdDepartDate, labelSearchRoutes;
    private Calendar mDepartDateCalendar;
    public static String mFrom, mTo;
    private int mValue = 0;
    private View mView;
    private ImageView mIvSwap, mIvDescrease, mIvIncrease, mSearch, btnSearchWhereTo, btnSearchVehicle;
    private TextView mTvCount;
    private String[] destinations;
    SharedPreferences prefs;
    private RecyclerView rvSearchRoutes, rvRecentSearch;
    private ProgressBar progressBar;
    private LinearLayout llRecentRoutes,llSearchedRoutes;
    MaterialCardView cardSearchRoutes;
    MaterialCardView cardSearchDestinations, cardReadyMatatu,cardRecentRoutes;
    Dialog dialog;
    List<Route> recentSearches = new ArrayList<>();
   List<String> routeIdsList=new ArrayList<>();

    Gson gson = new Gson();


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        prefs = AppController.getInstance().getMobiPrefs();

        initView(view);
        setListener();
        getData();


        return view;
    }

    private void getData() {
     String   recentRoutesListString = prefs.getString(Constants.RECENT_ROUTES, "");
        try {

            if (recentRoutesListString.isEmpty()||recentRoutesListString.equals("")){
//                Toast.makeText(getActivity(), "No recent destinations", Toast.LENGTH_SHORT).show();
            }else {
                              recentSearches = new ArrayList<>(Arrays.asList(new GsonBuilder().create().fromJson(recentRoutesListString, Route[].class)));
                Log.e("stored string", recentRoutesListString);
                Log.e("stored routes", String.valueOf(recentSearches.size()));
                decodeFromStorage(recentSearches);

            }


        } catch (Exception e) {

        }
    }

    private void decodeFromStorage(List<Route> recentSearches) {
        if (recentSearches.size() > 0) {
            llRecentRoutes.setVisibility(View.VISIBLE);

            try {


                //                        cardSearchRoutes.setVisibility(View.VISIBLE);
                rvRecentSearch.setLayoutManager(new LinearLayoutManager(getActivity()));
//                    rvSearchRoutes.addItemDecoration(new LineItemDecoration(getActivity(), LinearLayout.VERTICAL));
                rvRecentSearch.setHasFixedSize(true);
                //set data and list adapter
                RouteAdapter routeAdapter = new RouteAdapter(getActivity(), recentSearches);
                rvRecentSearch.setAdapter(routeAdapter);
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

//                        SharedPreferences.Editor editor = prefs.edit();
//                        editor.putString(Constants.TICKET_TRAVEL_FROM, obj.getOrigin());
//                        editor.putString(Constants.TICKET_TRAVEL_TO, obj.getDestination());
//                        editor.putString(Constants.TICKET_PICKUP_POINT, obj.getOrigin());
//                        editor.putString(Constants.TICKET_DROPOFF_POINT, obj.getDestination());
//                        editor.putString(Constants.RECENT_ROUTES, gson.toJson(recentSearches));
//                        editor.apply();
//
//                        Intent intent = new Intent(getActivity(), BusListActivity.class);
//                        intent.putExtra(Constants.intentdata.TRIP_KEY, obj.getOrigin() + " To " + obj.getDestination());
//                        intent.putExtra(Constants.intentdata.FROM, mFrom);
//                        intent.putExtra(Constants.intentdata.TO, mTo);
//                        startActivity(intent);
                    }
                });

            } catch (Exception e) {
                Log.e("err", e.toString());
            }
        }
    }

    private void setListener() {

        btnSearchWhereTo.setOnClickListener(this);
        btnSearchVehicle.setOnClickListener(this);

    }

    private void initView(View view) {
        llRecentRoutes = view.findViewById(R.id.llRecentRoutes);
        llSearchedRoutes = view.findViewById(R.id.llSearchedRoutes);

        cardSearchDestinations = view.findViewById(R.id.cardSearchDestination);
        cardReadyMatatu = view.findViewById(R.id.cardReadyMatatu);
        cardRecentRoutes = view.findViewById(R.id.cardRecentRoutes);

        btnSearchWhereTo = view.findViewById(R.id.btnSearchWhereTo);
        btnSearchVehicle = view.findViewById(R.id.btnSearchVehicle);

        etWhereTo = view.findViewById(R.id.etWhereTo);
        etSearchVehicle = view.findViewById(R.id.etSearchVehicle);


        rvSearchRoutes = view.findViewById(R.id.rvSearchRoutes);
        rvRecentSearch = view.findViewById(R.id.rvRecentSearch);
        labelSearchRoutes = view.findViewById(R.id.labelSearchRoutes);



//
//        SharedPreferences.Editor editor = prefs.edit();
//
//        editor.putString(Constants.RECENT_ROUTES, "");
//        editor.apply();
//



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnSearchWhereTo:

prepareSearchDestination();
                break;
            case R.id.btnSearchVehicle:
                prepareSearchVehicle();
                break;

        }
    }

    private void prepareSearchDestination() {
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
                startActivity(new Intent(getActivity(), NoInternetActivity.class));
            }

        } else {
//                    Toast.makeText(getActivity(), "Enter your destination!", Toast.LENGTH_SHORT).show();
        }
    }

    private void prepareSearchVehicle() {
        String vehicle_registration= etSearchVehicle.getText().toString();
        if (vehicle_registration.isEmpty()||vehicle_registration.equals("")){
            Toast.makeText(getActivity(), "Enter vehicle registration number", Toast.LENGTH_SHORT).show();
        }else {
            //Hide all other views
            readOne(vehicle_registration);
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
        dialog = new Dialog(getActivity());
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
                        rvSearchRoutes.setLayoutManager(new LinearLayoutManager(getActivity()));
//                    rvSearchRoutes.addItemDecoration(new LineItemDecoration(getActivity(), LinearLayout.VERTICAL));
                        rvSearchRoutes.setHasFixedSize(true);
                        //set data and list adapter

                        if (searchRoutesResponse.getRoute().size() > 0) {
                            llSearchedRoutes.setVisibility(View.VISIBLE);
                            RouteAdapter routeAdapter = new RouteAdapter(getActivity(), searchRoutesResponse.getRoute());
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


            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_points);
            dialog.setCancelable(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


            //Spinner for pickup point
            Spinner spPickUpPoint = dialog.findViewById(R.id.spPickUpPoint);
            LazyAdapterBusStops lazyPickupPoint = new LazyAdapterBusStops(getActivity(), obj.getStop());
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
            LazyAdapterBusStops lazyDropOffPoint = new LazyAdapterBusStops(getActivity(), obj.getStop());
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

                            Intent intent = new Intent(getActivity(), BusListActivity.class);
                            intent.putExtra(Constants.intentdata.TRIP_KEY, obj.getOrigin() + " To " + obj.getDestination());
                            intent.putExtra(Constants.intentdata.FROM, mFrom);
                            intent.putExtra(Constants.intentdata.TO, mTo);
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

    private void retrieveRouteDetail(String id) {
        RouteDetailInterface api = AppController.getInstance().getRetrofit().create(RouteDetailInterface.class);
        RouteDetailsRequest request = new RouteDetailsRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setId(id);
        request.setAction(Constants.READ_ONE_ACTION);
        Call<RouteDetailsResponse> call = api.retrieveRouteDetails(request);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<RouteDetailsResponse>() {
            @Override
            public void onResponse(Call<RouteDetailsResponse> call, Response<RouteDetailsResponse> response) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<RouteDetailsResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void showCustomDialog(String title, String message) {
        try {


            final Dialog dialog = new Dialog(getActivity());
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

            dialog.show();
            dialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            Log.e("Dialog", e.toString());
        }
    }

    public void showProgressDialog(Dialog dialog, String message) {
        try {


            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_progress);
            dialog.setCancelable(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            ProgressBar progressBar = dialog.findViewById(R.id.progress_bar);
            TextView tvMessage = dialog.findViewById(R.id.tvMessage);
            tvMessage.setText(message);


            dialog.show();
            dialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            Log.e("Dialog", e.toString());
        }
    }


    private void readOne(final String vehicle_registration) {


        final Dialog dialog=new Dialog(getActivity());
        ReadOneInterface api = AppController.getInstance().getRetrofit().create(ReadOneInterface.class);
        ServerReadOneRequest request=new ServerReadOneRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN,""));
        request.setId(vehicle_registration);
        request.setAction(Constants.READ_ONE_ACTION);
        Call<ServerReadOneResponse> call = api.readOne(request);
        String message ="Retrieving details"+ getResources().getString(R.string.txt_please_wait);
        showProgressDialog(dialog, message);
        call.enqueue(new Callback<ServerReadOneResponse>() {
            @Override
            public void onResponse(Call<ServerReadOneResponse> call, Response<ServerReadOneResponse> response) {
                Log.e("body", gson.toJson(response.body()));
                ServerReadOneResponse readOneResponse=response.body();
                dialog.dismiss();
                String registration_number=readOneResponse.getRegistration_number();
                if (registration_number.isEmpty()||registration_number.equals("")){
                    return;
                }else {
                    showCustomYesNoDialog(vehicle_registration, "Do you confirm that you are proceeding to pay fare for this vehicle?", readOneResponse);
                }
            }

            @Override
            public void onFailure(Call<ServerReadOneResponse> call, Throwable t) {
                Log.e("Error occured", t.toString());
                dialog.dismiss();
            }
        });
    }

    public void showCustomYesNoDialog(String title, String message, final ServerReadOneResponse readOneResponse) {
        try {


            final Dialog dialog = new Dialog(getActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_view_ticket_details);
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
//                    finish();
                    dialog.dismiss();
                }
            });
            ((Button) dialog.findViewById(R.id.bt_yes)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = prefs.edit();


                    editor.putString(Constants.TICKET_VEHICLE_ID, readOneResponse.getId());
                    editor.putString(Constants.TICKET_VEHICLE_CURRENT_FARE, readOneResponse.getFare_details().getCurrent_fare());
                    editor.putString(Constants.TICKET_VEHICLE_OPERATOR_ID, readOneResponse.getOperator().getId());
                    editor.putString(Constants.TICKET_VEHICLE_TRIP_NUMBER, readOneResponse.getFare_details().getTrip_number());

                    editor.apply();

                    //Move straight to payment methods
                    Intent intent=new Intent(getActivity(), PaymentMethodsActivity.class);
                    startActivity(intent);

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

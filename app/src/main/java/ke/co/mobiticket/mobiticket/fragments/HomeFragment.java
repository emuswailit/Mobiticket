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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.activities.BaseActivity;
import ke.co.mobiticket.mobiticket.activities.BusListActivity;
import ke.co.mobiticket.mobiticket.activities.DashboardActivity;
import ke.co.mobiticket.mobiticket.activities.NoInternetActivity;
import ke.co.mobiticket.mobiticket.adapters.RouteAdapter;
import ke.co.mobiticket.mobiticket.pojos.Route;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.RouteDetailInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.SearchRouteInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.RouteDetailsRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.SearchRouteRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.RouteDetailsResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.SearchRoutesResponse;
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
    private EditText etWhereTo;
    private TextView mEdDepartDate, labelSearchRoutes;
    private Calendar mDepartDateCalendar;
    public static String mFrom, mTo;
    private int mValue = 0;
    private View mView;
    private ImageView mIvSwap, mIvDescrease, mIvIncrease, mSearch, btnSearchWhereTo;
    private TextView mTvCount;
    private String[] destinations;
    SharedPreferences prefs;
    private RecyclerView rvSearchRoutes;
    private ProgressBar progressBar;
    MaterialCardView cardSearchRoutes;
    MaterialCardView cardSearchDestinations;
    Dialog dialog;



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

    }

    private void setListener() {

        btnSearchWhereTo.setOnClickListener(this);



    }

    private void initView(View view) {
        cardSearchDestinations = view.findViewById(R.id.cardSearchDestination);

        btnSearchWhereTo = view.findViewById(R.id.btnSearchWhereTo);

        etWhereTo = view.findViewById(R.id.etWhereTo);
        progressBar = view.findViewById(R.id.progressBar);

        rvSearchRoutes = view.findViewById(R.id.rvSearchRoutes);
        labelSearchRoutes = view.findViewById(R.id.labelSearchRoutes);





    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnSearchWhereTo:

                String search = etWhereTo.getText().toString();
                if (!search.isEmpty()||!search.equals("")) {
                    if (AppController.getInstance().isNetworkConnected()){
                        try {
                            searchWhereTo(search);
                        }catch (Exception e){
                            Log.e("Error:search", e.toString());
                            Toast.makeText(getActivity(), "An error occurred!", Toast.LENGTH_SHORT).show();
                        }

                    }else {
//                        showCustomDialog(Constants.NO_INTERNET_TITLE, Constants.NO_INTERNET_MESSAGE);
                        startActivity(new Intent(getActivity(), NoInternetActivity.class));
                    }

                } else {
                    Toast.makeText(getActivity(), "Enter your destination!", Toast.LENGTH_SHORT).show();
                }
                break;






        }
    }

    private void searchWhereTo(final String search) {
        SearchRouteInterface api = AppController.getInstance().getRetrofit().create(SearchRouteInterface.class);
        SearchRouteRequest request = new SearchRouteRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setKeywords(search);
        request.setAction(Constants.SEARCH_ACTION);
        Gson gson=new Gson();
        Log.e("clicked",gson.toJson(request));
//        progressBar.setVisibility(View.VISIBLE);
        dialog= new Dialog(getActivity());
        String message="Retrieving destinations\n\nPlease wait...";
        showProgressDialog(dialog, message);
        Call<SearchRoutesResponse> call = api.retrieveRoutes(request);
        call.enqueue(new Callback<SearchRoutesResponse>() {
            @Override
            public void onResponse(Call<SearchRoutesResponse> call, Response<SearchRoutesResponse> response) {
//                progressBar.setVisibility(View.GONE);
                dialog.dismiss();
                SearchRoutesResponse searchRoutesResponse=response.body();
                if (response.body() !=null) {
                    if (searchRoutesResponse.getResponse_code().equals("0")) {
//                        cardSearchRoutes.setVisibility(View.VISIBLE);
                        rvSearchRoutes.setLayoutManager(new LinearLayoutManager(getActivity()));
//                    rvSearchRoutes.addItemDecoration(new LineItemDecoration(getActivity(), LinearLayout.VERTICAL));
                        rvSearchRoutes.setHasFixedSize(true);
                        //set data and list adapter

                        if (searchRoutesResponse.getRoute().size() > 0) {
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

                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString(Constants.TICKET_TRAVEL_FROM, obj.getOrigin());
                                    editor.putString(Constants.TICKET_TRAVEL_TO, obj.getDestination());
                                    editor.putString(Constants.TICKET_PICKUP_POINT, obj.getOrigin());
                                    editor.putString(Constants.TICKET_DROPOFF_POINT, obj.getDestination());
                                    editor.apply();

                                    Intent intent = new Intent(getActivity(), BusListActivity.class);
                                    intent.putExtra(Constants.intentdata.TRIP_KEY, obj.getOrigin() + " To " + obj.getDestination());
                                    intent.putExtra(Constants.intentdata.FROM, mFrom);
                                    intent.putExtra(Constants.intentdata.TO, mTo);
                                    startActivity(intent);
                                }
                            });
                            labelSearchRoutes.setText(searchRoutesResponse.getRoute().size() + " routes retrieved for your search \" " + search + " \"");
                        } else {
                            labelSearchRoutes.setText("No routes retrieved for criteria \" " + search + " \" ");
                        }


                    } else {
                        String title = "Search Destinations";
                        String message = searchRoutesResponse.getResponse_message();
                        showCustomDialog(title, message);
                    }

                }else {
                    showCustomDialog("Search destinations","An error occurred!");
                }
            }

            @Override
            public void onFailure(Call<SearchRoutesResponse> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
                dialog.dismiss();
                Log.e("Error",t.getLocalizedMessage());
            }
        });

    }

    private void retrieveRouteDetail(String id) {
        RouteDetailInterface api=AppController.getInstance().getRetrofit().create(RouteDetailInterface.class);
        RouteDetailsRequest request= new RouteDetailsRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setId(id);
        request.setAction(Constants.READ_ONE_ACTION);
        Call<RouteDetailsResponse> call=api.retrieveRouteDetails(request);
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
                    Toast.makeText(getActivity(), ((AppCompatButton) v).getText().toString() + " Clicked", Toast.LENGTH_SHORT).show();
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
}

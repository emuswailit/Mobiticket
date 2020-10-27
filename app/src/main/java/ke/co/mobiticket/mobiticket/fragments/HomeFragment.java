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

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.activities.BaseActivity;
import ke.co.mobiticket.mobiticket.activities.BusListActivity;
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

    private final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mDepartDateCalendar.set(Calendar.YEAR, year);
            mDepartDateCalendar.set(Calendar.MONTH, monthOfYear);
            mDepartDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }


    };

    public HomeFragment() {
        // Required empty public constructor
    }

    /* update label */
    private void updateLabel() {
        mEdDepartDate.setText(Constants.DateFormat.DAY_MONTH_YEAR_FORMATTER.format(mDepartDateCalendar.getTime()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        prefs = AppController.getInstance().getMobiPrefs();
        initView(view);
        setListener();
        getData();

//        setOfferAdapter();

        if (mDepartDateCalendar == null) {
            mDepartDateCalendar = Calendar.getInstance();
        }
        updateLabel();
        return view;
    }

    private void getData() {

    }

    private void setListener() {
        mSearch.setOnClickListener(this);

//        mEdDepartDate.setOnClickListener(this); //Disable date select in intracity
        mIvDescrease.setOnClickListener(this);
        mIvIncrease.setOnClickListener(this);
        mIvSwap.setOnClickListener(this);
        btnSearchWhereTo.setOnClickListener(this);

        mEdFromCity.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mEdFromCity.length() > 0) {
                    mView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    mView.setAlpha(0.2f);
                } else {
                    mView.setBackgroundColor(getResources().getColor(R.color.view_color));
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        mEdToCity.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //  if (validate()) {
                    mFrom = mEdFromCity.getText().toString();
                    mTo = mEdToCity.getText().toString();
                    Intent intent = new Intent(getActivity(), BusListActivity.class);
                    intent.putExtra(Constants.intentdata.TRIP_KEY, mEdFromCity.getText().toString() + " To " + mEdToCity.getText().toString());
                    startActivity(intent);
                    //  }
                    return true;
                }
                return false;
            }

        });
    }

    private void initView(View view) {
        cardSearchDestinations = view.findViewById(R.id.cardSearchDestination);
        labelSearchRoutes = view.findViewById(R.id.labelSearchRoutes);
        cardSearchRoutes = view.findViewById(R.id.cardSearchRoutes);
        btnSearchWhereTo = view.findViewById(R.id.btnSearchWhereTo);

        etWhereTo = view.findViewById(R.id.etWhereTo);
        progressBar = view.findViewById(R.id.progressBar);
        mEdDepartDate = view.findViewById(R.id.edOneWay);
        rvSearchRoutes = view.findViewById(R.id.rvSearchRoutes);

        mEdFromCity = view.findViewById(R.id.edFromCity);
        mEdToCity = view.findViewById(R.id.edToCity);
        mIvDescrease = view.findViewById(R.id.ivDescrease);
        mIvIncrease = view.findViewById(R.id.ivIncrease);
        mTvCount = view.findViewById(R.id.tvCount);
        mView = view.findViewById(R.id.view2);
        mIvSwap = view.findViewById(R.id.ivSwap);
        mSearch = view.findViewById(R.id.btnSearch);

        destinations = new String[]{getString(R.string.lbl_cbd), getString(R.string.lbl_koja), getString(R.string.lbl_ngumba), getString(R.string.lbl_ruiru)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_items, destinations);
        mEdFromCity.setThreshold(1);
        mEdFromCity.setAdapter(adapter);
        mEdToCity.setThreshold(1);
        mEdToCity.setAdapter(adapter);

        mIvDescrease.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnSearchWhereTo:
                Log.e("clicked","clicked");
                String search = etWhereTo.getText().toString();
                if (!search.isEmpty()||!search.equals("")) {
                    if (AppController.getInstance().isNetworkConnected()){
                        searchWhereTo(search);
                    }else {
                        showCustomDialog(Constants.NO_INTERNET_TITLE, Constants.NO_INTERNET_MESSAGE);
                    }

                } else {
                    Toast.makeText(getActivity(), "Enter your destination!", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.edOneWay:

                DatePickerDialog datePickerDialogs = new DatePickerDialog(getActivity(), date, mDepartDateCalendar
                        .get(Calendar.YEAR), mDepartDateCalendar.get(Calendar.MONTH),
                        mDepartDateCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialogs.getDatePicker().setMinDate(new Date().getTime());
                datePickerDialogs.show();
                break;

            case R.id.ivSwap:
                String mFromCity = mEdFromCity.getText().toString();
                String mToCity = mEdToCity.getText().toString();
                Animation startRotateAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_rotate);
                mIvSwap.startAnimation(startRotateAnimation);
                mFromCity = mFromCity + mToCity;
                mToCity = mFromCity.substring(0, mFromCity.length() - mToCity.length());
                mFromCity = mFromCity.substring(mToCity.length());
                mEdFromCity.setText(mFromCity);
                mEdToCity.setText(mToCity);
                mEdFromCity.setSelection(mFromCity.length());
                mEdToCity.setSelection(mToCity.length());
                break;
            case R.id.ivDescrease:
                mValue = mValue - 1;
                mTvCount.setText(String.valueOf(mValue));

                if (mValue <= 1) {

                    ((BaseActivity) Objects.requireNonNull(getActivity())).invisibleView(mIvDescrease);

                } else {

                    ((BaseActivity) getActivity()).showView(mIvDescrease);
                    mTvCount.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    mTvCount.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                break;
            case R.id.ivIncrease:
                mValue = mValue + 1;

                if (mValue < 1) {
                    mValue = 1;

                } else {

                    if (mValue == 1) ((BaseActivity) getActivity()).invisibleView(mIvDescrease);
                    else
                        ((BaseActivity) Objects.requireNonNull(getActivity())).showView(mIvDescrease);
                    mTvCount.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    mTvCount.setText(String.valueOf(mValue));
                    mTvCount.setTextColor(getResources().getColor(R.color.colorPrimary));

                }
                break;
            case R.id.btnSearch:
                //    if (validate()) {

                mFrom = mEdFromCity.getText().toString();
                mTo = mEdToCity.getText().toString();
                if (mFrom.equals("") || mFrom.isEmpty()) {
                    Toast.makeText(getActivity(), "Please select origin", Toast.LENGTH_SHORT).show();
                } else if (mTo.equals("") || mTo.isEmpty()) {
                    Toast.makeText(getActivity(), "Please select destination", Toast.LENGTH_SHORT).show();
                } else {

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt(Constants.SEAT_COUNT, Integer.valueOf(mTvCount.getText().toString()));
                    editor.putString(Constants.TICKET_TRAVEL_FROM, mFrom);
                    editor.putString(Constants.TICKET_PICKUP_POINT, mFrom);
                    editor.putString(Constants.TICKET_TRAVEL_DATE, Constants.DateFormat.YEAR_MONTH_DAY_FORMATTER.format(mDepartDateCalendar.getTime()));
                    editor.putString(Constants.TICKET_TRAVEL_TO, mTo);
                    editor.putString(Constants.TICKET_DROPOFF_POINT, mTo);
                    editor.apply();

                    Intent intent = new Intent(getActivity(), BusListActivity.class);
                    intent.putExtra(Constants.intentdata.TRIP_KEY, mFrom + " To " + mTo);
                    intent.putExtra(Constants.intentdata.FROM, mFrom);
                    intent.putExtra(Constants.intentdata.TO, mTo);
                    startActivity(intent);
                }

                //    }
                break;
        }
    }

    private void searchWhereTo(final String search) {
        SearchRouteInterface api = AppController.getInstance().getRetrofit().create(SearchRouteInterface.class);
        SearchRouteRequest request = new SearchRouteRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setKeywords(search);
        request.setAction(Constants.SEARCH_ACTION);
        progressBar.setVisibility(View.VISIBLE);
        Call<SearchRoutesResponse> call = api.retrieveRoutes(request);
        call.enqueue(new Callback<SearchRoutesResponse>() {
            @Override
            public void onResponse(Call<SearchRoutesResponse> call, Response<SearchRoutesResponse> response) {
                progressBar.setVisibility(View.GONE);
                SearchRoutesResponse searchRoutesResponse=response.body();
                if (searchRoutesResponse.getResponse_code().equals("0")){
                    cardSearchRoutes.setVisibility(View.VISIBLE);
                    rvSearchRoutes.setLayoutManager(new LinearLayoutManager(getActivity()));
//                    rvSearchRoutes.addItemDecoration(new LineItemDecoration(getActivity(), LinearLayout.VERTICAL));
                    rvSearchRoutes.setHasFixedSize(true);
                    //set data and list adapter

                    if (searchRoutesResponse.getRoute().size()>0){
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
                                editor.putString(Constants.TICKET_TRAVEL_DATE, Constants.DateFormat.YEAR_MONTH_DAY_FORMATTER.format(mDepartDateCalendar.getTime()));
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
                        labelSearchRoutes.setText(searchRoutesResponse.getRoute().size()+ " routes retrieved for your search \" "+search+" \"");
                    }else {
                        labelSearchRoutes.setText("No routes retrieved for criteria \" "+ search+ " \" ");
                    }



                }else {
                    String title = "Search Destinations";
                    String message = searchRoutesResponse.getResponse_message();
                        showCustomDialog(title, message);
                }
            }

            @Override
            public void onFailure(Call<SearchRoutesResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
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
}

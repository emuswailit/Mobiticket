package ke.co.mobiticket.mobiticket.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

public class TicketsActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView rvTickets,rvTicketsHistory;
    private ImageView ivLogout,ivBack;
    private TextView tvTitle;
    SharedPreferences prefs;
    private EditText etKeywords;
    private ImageButton btnSearchTicket;
    List<Ticket> ticketArrayList = new ArrayList<>();
    MaterialCardView cardTicketsHistory, cardCurrentTicket;
    TicketsAdapter adapter = null;
    private TextView tvSearchResult;
    Gson gson =new Gson();
    private String ticket_data="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle(R.string.toolbar_title);

        try {

            //Retrieve Shared Preferences
            prefs=AppController.getInstance().getMobiPrefs();

            initLayouts();
            initListeners();
            initData();
            searchTicket(prefs.getString(Constants.PHONE_NUMBER,""));


        } catch (Exception e) {
            Log.e("fdgh", e.toString());
        }






        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.action_vehicles:
                        startActivity(MyVehiclesActivity.class);
                        break;
                    case R.id.action_tickets:
                    break;
                    case R.id.action_more:
                        startActivity(MoreActivity.class);
                        break;

                }
                return true;
            }
        });

    }

    private void loadHistory() {

    }

    private void initData() {
        try {
            Intent intent= getIntent();
            //Check if ticket was foorwarded from intent
            if (intent.getStringExtra("ticket_data").isEmpty()||intent.getStringExtra("ticket_data").equals("")){
                Toast.makeText(this, "No tickets forwarded", Toast.LENGTH_SHORT).show();
            }else {
                cardCurrentTicket.setVisibility(View.VISIBLE);
                ticket_data=intent.getStringExtra("ticket_data");
                Log.e("ticket_data",ticket_data);
                Gson gson = new Gson();
                ticketArrayList = new ArrayList<>(Arrays.asList(new GsonBuilder().create().fromJson(ticket_data, Ticket[].class)));
                if (ticketArrayList.size()>0){
                    Toast.makeText(this, "Tickets forwarded", Toast.LENGTH_SHORT).show();
                    displayCurrentTicket(ticketArrayList);
                }

            }
        }catch (Exception e){
            Log.e("initData",e.toString());
        }

    }

    private void displayCurrentTicket(List<Ticket> ticketArrayList) {
        adapter = new TicketsAdapter(TicketsActivity.this, ticketArrayList);
        rvTickets.setAdapter(adapter);
        RunLayoutAnimation(rvTickets);
    }

    private void initListeners() {

        btnSearchTicket.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivLogout.setOnClickListener(this);


    }

    private void initLayouts() {
        //Tickets

        ivBack = findViewById(R.id.ivBack);
        ivLogout = findViewById(R.id.ivLogout);

        etKeywords = findViewById(R.id.etKeywords);
        ivLogout = findViewById(R.id.ivLogout);
        btnSearchTicket = findViewById(R.id.btnSearchTicket);
        rvTickets = findViewById(R.id.rvTickets);
        rvTickets.setLayoutManager(new LinearLayoutManager(this));
        rvTickets.setHasFixedSize(true);

        cardCurrentTicket = findViewById(R.id.cardCurrentTicket);
        cardTicketsHistory = findViewById(R.id.cardTicketsHistory);



        rvTicketsHistory = findViewById(R.id.rvTicketsHistory);
        rvTicketsHistory.setLayoutManager(new LinearLayoutManager(this));
        rvTicketsHistory.setHasFixedSize(true);

        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("My Tickets");

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.menu, menu);
//
//        MenuItem search = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
//        try {
//            search(searchView);
//        }catch (Exception e){
//            Log.e("hhhc", e.toString());
//        }
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        return super.onOptionsItemSelected(item);
//    }


//    private void search(SearchView searchView) {
//        searchView.setBackgroundColor(getResources().getColor(R.color.green_100));
//
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//
//                try {
//                    if (adapter!=null){
//                        adapter.getFilter().filter(newText);
//                    }
//
//                }catch (Exception e){
//                    Log.e("dvdvd", e.toString());
//                }
//
//                return true;
//            }
//        });
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearchTicket:
                String keyword = etKeywords.getText().toString();

                if (!keyword.equals("")) {
                    Log.e("keyword", keyword);
                    if (AppController.getInstance().isNetworkConnected()){
                        searchTicket(keyword);
                    }else {
                        startActivity(NoInternetActivity.class);
                    }

                }
                break;
            case R.id.ivLogout:
                showLogoutYesNoDialog("Log out", "Do you really want to log out?",TicketsActivity.this);
                break;
            case R.id.ivBack:
                startActivity(DashboardActivity.class);
                finish();
                break;
        }
    }

    private void searchTicket(final String keyword) {
        final Dialog dialog =new Dialog(this);
        SearchTicketInterface api = AppController.getInstance().getRetrofit().create(SearchTicketInterface.class);
        SearchTicketRequest request = new SearchTicketRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.SEARCH_ACTION);
        request.setKeywords(keyword);
        showProgressDialog(dialog, "Loading" + getResources().getString(R.string.txt_please_wait));
        Call<SearchTicketResponse> call = api.searchTicket(request);
        call.enqueue(new Callback<SearchTicketResponse>() {
            @Override
            public void onResponse(Call<SearchTicketResponse> call, Response<SearchTicketResponse> response) {
           dialog.dismiss();
                Log.e("search tickets", gson.toJson(response.body()));
                if (response.body()!=null){
                    if (response.body().getResponse_code().equals("0")){

                        if (response.body().getTicket().size()>0){
                            cardTicketsHistory.setVisibility(View.VISIBLE);
                            displayTickets( response.body().getTicket());
                        }

                    }else {
                        showCustomDialog("Search Tickets",response.body().getResponse_message());
                    }
                }else {
                    showCustomDialog("Search Ticket", "A system error occurred. Please try again!");
                }
            }

            @Override
            public void onFailure(Call<SearchTicketResponse> call, Throwable t) {
Log.e("error", t.getLocalizedMessage());
dialog.dismiss();
                showCustomDialog("Search Ticket", "A system error occurred. Please try again!");
            }
        });
    }

    private void displayTickets(List<Ticket> tickets) {
        adapter = new TicketsAdapter(TicketsActivity.this, tickets);
        rvTicketsHistory.setAdapter(adapter);
        RunLayoutAnimation(rvTicketsHistory);

    }
}
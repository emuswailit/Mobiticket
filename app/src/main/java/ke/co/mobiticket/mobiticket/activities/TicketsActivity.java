package ke.co.mobiticket.mobiticket.activities;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    private RecyclerView rvRecentTickets,rvSearchedTickets;
    private ImageView ivBack;
    SharedPreferences prefs;
    private EditText etKeywords;
    private ImageView btnSearchTicket;
    List<Ticket> recentTicketsList = null;
    MaterialCardView cardSearchedTickets;
    TicketsAdapter adapter = null;
    ProgressBar progressBar;
    private TextView tvSearchResult;
    Gson gson =new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title);


        try {
            prefs = AppController.getInstance().getMobiPrefs();
            String recentTicketString = prefs.getString(Constants.RECENT_TICKETS, "");
            Log.e("recentTicketsStr", recentTicketString);
            if (recentTicketString.isEmpty() || recentTicketString.equals("")) {
                Log.e("No recents", "No recents");
            } else {
                Gson gson = new Gson();
                recentTicketsList = new ArrayList<>(Arrays.asList(new GsonBuilder().create().fromJson(recentTicketString, Ticket[].class)));

                for (Ticket ticket : recentTicketsList) {
                    Log.e("from recents", ticket.getStatus());
                }
            }
        } catch (Exception e) {
            Log.e("bookings", e.toString());
        }
        try {
            initLayouts();
            initListeners();
        } catch (Exception e) {
            Log.e("fdgh", e.toString());
        }
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    private void initListeners() {


//        rvRecentTickets.setLayoutManager(new LinearLayoutManager(this));
        rvRecentTickets.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        rvRecentTickets.setHasFixedSize(true);

        adapter = new TicketsAdapter(TicketsActivity.this, recentTicketsList);
        rvRecentTickets.setAdapter(adapter);
        RunLayoutAnimation(rvRecentTickets);

        btnSearchTicket.setOnClickListener(this);
    }

    private void initLayouts() {
        //Tickets

        etKeywords = findViewById(R.id.etKeywords);
//        tvSearchResult = findViewById(R.id.tvSearchResult);
        progressBar = findViewById(R.id.progressBar);
        btnSearchTicket = findViewById(R.id.btnSearchTicket);
        rvRecentTickets = findViewById(R.id.rvRecentTickets);
        rvSearchedTickets = findViewById(R.id.rvSearchedTickets);
        ivBack = findViewById(R.id.ivBack);
//        cardSearchedTickets = findViewById(R.id.cardSearchedTickets);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }


    private void search(SearchView searchView) {
        searchView.setBackgroundColor(getResources().getColor(R.color.green_100));


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                adapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearchTicket:
                String keyword = etKeywords.getText().toString();

                if (!keyword.equals("")) {
                    Log.e("keyword", keyword);
                    searchTicket(keyword);
                }
                break;
        }
    }

    private void searchTicket(final String keyword) {
        SearchTicketInterface api = AppController.getInstance().getRetrofit().create(SearchTicketInterface.class);
        SearchTicketRequest request = new SearchTicketRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.SEARCH_ACTION);
        request.setKeywords(keyword);
        progressBar.setVisibility(View.VISIBLE);

        Call<SearchTicketResponse> call = api.searchTicket(request);
        call.enqueue(new Callback<SearchTicketResponse>() {
            @Override
            public void onResponse(Call<SearchTicketResponse> call, Response<SearchTicketResponse> response) {
                progressBar.setVisibility(View.GONE);
                Log.e("search tickets", gson.toJson(response.body()));
                if (response.body()!=null){
                    if (response.body().getResponse_code().equals("0")){
                        cardSearchedTickets.setVisibility(View.VISIBLE);
                        TicketsAdapter adapter = new TicketsAdapter(TicketsActivity.this, response.body().getTicket());
                        rvSearchedTickets.setAdapter(adapter);
                        RunLayoutAnimation(rvSearchedTickets);
                        tvSearchResult.setText("Retrieve tickets: "+ String.valueOf(response.body().getTicket().size()));
                    }else {
                        showCustomDialog("Search Tickets",response.body().getResponse_message());
                    }
                }else {
                    Log.e("error","no response");
                }
            }

            @Override
            public void onFailure(Call<SearchTicketResponse> call, Throwable t) {
progressBar.setVisibility(View.GONE);
Log.e("error", t.getLocalizedMessage());
            }
        });
    }
}
package ke.co.mobiticket.mobiticket.activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.adapters.TicketsAdapter;
import ke.co.mobiticket.mobiticket.pojos.Ticket;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;

public class BookingActivity extends BaseActivity {

    private RecyclerView rvTickets;
    private ImageView ivBack;
    SharedPreferences prefs;
    List<Ticket> recentTicketsList=null;
    TicketsAdapter adapter=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);


        try {
            prefs= AppController.getInstance().getMobiPrefs();
            String recentTicketString = prefs.getString(Constants.RECENT_TICKETS, "");
            Log.e("recentTicketsStr", recentTicketString);
            if (recentTicketString.isEmpty() || recentTicketString.equals("")) {
                Log.e("No recents","No recents");
            } else {
                Gson gson = new Gson();
                recentTicketsList = new ArrayList<>(Arrays.asList(new GsonBuilder().create().fromJson(recentTicketString, Ticket[].class)));

                for (Ticket ticket : recentTicketsList) {
                    Log.e("from recents",ticket.getStatus());
                }
            }
        }catch (Exception e){
            Log.e("bookings",e.toString());
        }
        try {
            initLayouts();
            initListeners();
        }catch (Exception e){
            Log.e("fdgh",e.toString());
        }





    }


    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        getMenuInflater().inflate(R.menu.menu, menu);
//
//        MenuItem search = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
//        search(searchView);
//        return true;
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        return super.onOptionsItemSelected(item);
//    }


//    private void search(SearchView searchView) {
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
//                adapter.getFilter().filter(newText);
//                return true;
//            }
//        });
//    }

    private void initListeners() {

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //Tickets
        rvTickets = findViewById(R.id.rvRecentTickets);
        rvTickets.setLayoutManager(new LinearLayoutManager(this));
        rvTickets.setHasFixedSize(true);

        adapter=new TicketsAdapter(BookingActivity.this, recentTicketsList);
        rvTickets.setAdapter(adapter);
        RunLayoutAnimation(rvTickets);
    }

    private void initLayouts() {
        rvTickets=findViewById(R.id.rvRecentTickets);
        ivBack=findViewById(R.id.ivBack);

    }
}
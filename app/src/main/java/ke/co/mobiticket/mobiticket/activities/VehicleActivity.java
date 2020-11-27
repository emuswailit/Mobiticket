package ke.co.mobiticket.mobiticket.activities;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.adapters.ChargesAdapter;
import ke.co.mobiticket.mobiticket.adapters.ExpensesAdapter;
import ke.co.mobiticket.mobiticket.adapters.SectionAdapter;
import ke.co.mobiticket.mobiticket.pojos.Charge;
import ke.co.mobiticket.mobiticket.pojos.Expense;
import ke.co.mobiticket.mobiticket.pojos.Section;
import ke.co.mobiticket.mobiticket.pojos.Ticket;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.ReadOneInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerReadOneRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerReadOneResponse;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBack,ivLogout;
    private TextView tvTitle, tvChargesCount,tvTicketsCount,tvExpensesCount, tvIncome,tvExpense,tvBalance;
    private RecyclerView recyclerViewTickets, recyclerViewCharges,recyclerViewExpenses;
    List<String> dates = new ArrayList<>();
    String vehicle_reg;
    Gson gson=new Gson();
    String  currentDate;
    Dialog dialog;
    SharedPreferences prefs=AppController.getInstance().getMobiPrefs();
    private ArrayList<Ticket> sectionTickets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle);
        Intent intent=getIntent();

         currentDate=AppController.getInstance().getTodaysDate();

        String vehicle_id =intent.getStringExtra("vehicle_id");
         vehicle_reg =intent.getStringExtra("vehicle_reg");
        Log.e("vehicle id", vehicle_id);
        initLayouts();
        initListeners();
        if (vehicle_id.isEmpty()||vehicle_id.equals("")){
            finish();
        }else {
            readOne(vehicle_id);
        }
    }

    private void readOne(String id) {


        final Dialog dialog=new Dialog(VehicleActivity.this);
        ReadOneInterface api = AppController.getInstance().getRetrofit().create(ReadOneInterface.class);
        ServerReadOneRequest request=new ServerReadOneRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN,""));
        request.setId(id);
        request.setAction(Constants.READ_ONE_ACTION);
        Call<ServerReadOneResponse> call = api.readOne(request);
        String message =vehicle_reg+ " details"+ getResources().getString(R.string.txt_please_wait);
        showProgressDialog(dialog, message);
        call.enqueue(new Callback<ServerReadOneResponse>() {
            @Override
            public void onResponse(Call<ServerReadOneResponse> call, Response<ServerReadOneResponse> response) {
                Log.e("body", gson.toJson(response.body()));
dialog.dismiss();
                if (response.body() !=null){
                    if (response.body().getResponse_code().equals("0")) {
                        List<Ticket> tickets = response.body().getTicket();
                        tvBalance.setText(String.format("%.2f",Double.valueOf(response.body().getWallet_balance())));
                        tvIncome.setText(String.format("%.2f",Double.valueOf(response.body().getTotal_income())));
                        tvExpense.setText(String.format("%.2f",Double.valueOf(response.body().getTotal_expenses())));
                        if (tickets.size() > 0) {
                            List<Ticket> todayTickets = new ArrayList<>();
                            for (Ticket ticket : tickets) {
                                if (ticket.getTravel_date().equals(currentDate)) {
                                    todayTickets.add(ticket);
                                }
                            }

                                displayTickets(todayTickets);



                        } else {

                        }

                    }else {
                        showCustomDialog(vehicle_reg + " details", response.body().getResponse_message());

                    }
                    //Displaying charges

                    List<Charge> chargeList = response.body().getCharge();

                    if (chargeList.size()>0){
                        displayCharges(chargeList);
                    }else {
tvChargesCount.setText(R.string.text_no_charges);
                    }



                    List<Expense> expenseList = response.body().getExpense();

                    if (expenseList.size()>0){
                        displayExpenses(expenseList);
                    }else {
                        tvExpensesCount.setText(getResources().getString(R.string.no_expenses));
                    }

                }else {
                    showCustomDialog(vehicle_reg+ " details", response.body().getResponse_message());
                }
            }

            @Override
            public void onFailure(Call<ServerReadOneResponse> call, Throwable t) {
                Log.e("Error occured", t.toString());
                dialog.dismiss();

            }
        });
    }

    private void displayTickets(List<Ticket> todayTickets) {
        if (todayTickets.size()>0) {

            final List<Section> sections = new ArrayList<>();
            final List<String> sectionNames = new ArrayList<>();

            for (Ticket ticket : todayTickets) {
                if (sectionNames.contains(ticket.getTrip_number())) {
//                                Log.e("date already added",ticket.getTravel_date());
                } else {
                    sectionNames.add(ticket.getTrip_number());
//                                Log.e("Added date", ticket.getTravel_date());
                }
            }

            try {

                for (String sectionName : sectionNames) {
                    Log.e("section name", sectionName);
                    sectionTickets = new ArrayList<>();
                    for (Ticket ticket : todayTickets) {
                        if (ticket.getTrip_number().equals(sectionName)) {
                            sectionTickets.add(ticket);
                            Log.e("ticket", sectionName + " : " + ticket.getTravel_date());
                        }
//                                    Log.e("section under", sectionName+" : "+ticket.getReference_number());
//                                    Log.e("section tickets ct", String.valueOf(sectionTickets.size()));
                    }
                    Log.e("count", String.valueOf(sectionTickets.size()));
                    sections.add(new Section(sectionName, sectionTickets));
                }

                final SectionAdapter adapter = new SectionAdapter(VehicleActivity.this, sections);
                recyclerViewTickets.setAdapter(adapter);
            } catch (Exception e) {
                Log.e("problem", e.toString());
            }

        }else {
            tvTicketsCount.setText(getResources().getString(R.string.no_tickets));
        }
    }

    private void displayExpenses(List<Expense> expenseList) {
        List<Expense> filteredExpenseList = new ArrayList<>();

        for (Expense expense: expenseList){
            if (expense.getId()==""|| expense.getId()==null){

            }else {
                filteredExpenseList.add(expense);
            }
        }

        if (filteredExpenseList.size()>0){
            ExpensesAdapter expensesAdapter=new ExpensesAdapter(VehicleActivity.this,expenseList);
            recyclerViewExpenses.setAdapter(expensesAdapter);
        }else {
            tvExpensesCount.setText(getResources().getString(R.string.no_expenses));
        }

    }

    private void displayCharges(List<Charge> chargeList) {
        List<Charge> filteredChargeList=new ArrayList<>();

        for (Charge charge : chargeList){
            if (charge.getId()==""||charge.getId()==null){

            }else {
                filteredChargeList.add(charge);
            }
        }
        if (filteredChargeList.size()>0){
            ChargesAdapter chargesAdapter=new ChargesAdapter(VehicleActivity.this,filteredChargeList);
            recyclerViewCharges.setAdapter(chargesAdapter);

        }else {
            tvChargesCount.setText(getResources().getString(R.string.no_charges));
        }


    }


    private void initListeners() {
    ivBack.setOnClickListener(this);
    ivLogout.setOnClickListener(this);
    }

    private void initLayouts() {
ivLogout=findViewById(R.id.ivLogout);
ivBack=findViewById(R.id.ivBack);
tvIncome=findViewById(R.id.tvIncome);
tvExpense=findViewById(R.id.tvExpense);
tvBalance=findViewById(R.id.tvBalance);
        tvChargesCount=findViewById(R.id.tvChargesCount);
tvTicketsCount=findViewById(R.id.tvTicketsCount);
tvExpensesCount=findViewById(R.id.tvExpensesCount);
tvTitle=findViewById(R.id.tvTitle);
tvTitle.setText(vehicle_reg);

        recyclerViewTickets = (RecyclerView) findViewById(R.id.recyclerViewTickets);
        recyclerViewTickets.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTickets.setHasFixedSize(true);

        //Recycler view for expenses
        recyclerViewExpenses = (RecyclerView) findViewById(R.id.recyclerViewExpenses);
        recyclerViewExpenses.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewExpenses.setHasFixedSize(true);

        //Recycler view for charges
        recyclerViewCharges = (RecyclerView) findViewById(R.id.recyclerViewCharges);
        recyclerViewCharges.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCharges.setHasFixedSize(true);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivBack:
                finish();
                break;

            case R.id.ivLogout:
                showLogoutYesNoDialog("Log out", "Do you really want to log out?", VehicleActivity.this);
                break;
        }
    }

    public void showTicketDialog(Ticket ticket) {
        Toast.makeText(this, ticket.getReference_number(), Toast.LENGTH_SHORT).show();
    }
}

package ke.co.mobiticket.mobiticket.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.adapters.AdapterListExpandDriveVehicle;
import ke.co.mobiticket.mobiticket.pojos.Charge;
import ke.co.mobiticket.mobiticket.pojos.ConductVehicle;
import ke.co.mobiticket.mobiticket.pojos.DriveVehicle;
import ke.co.mobiticket.mobiticket.pojos.Expense;
import ke.co.mobiticket.mobiticket.pojos.Ticket;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.ReadOneInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerReadOneRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerLoginResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerReadOneResponse;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;
import ke.co.mobiticket.mobiticket.widgets.LineItemDecoration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTitle;
    private ImageView iVBack;
    SharedPreferences prefs;
    private RecyclerView recyclerViewDrive;
    private double totalCharges=0.00, totalExpenses=0.00, totalFares=0.00;
    TextView tvHeader, tvExpenses, tvCharges,tvCollection;
    Gson gson=new Gson();
    List<DriveVehicle> driveVehicleList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        initLayouts();

        initListeners();

        prefs = AppController.getInstance().getMobiPrefs();
        Log.e("from up",prefs.getString(Constants.ENTIRE_RESPONSE, "") );
        try {

            //Retrieve vehicles that the user drives, if any
            ServerLoginResponse serverLoginResponse= gson.fromJson(prefs.getString(Constants.ENTIRE_RESPONSE, ""),ServerLoginResponse.class);
            driveVehicleList = serverLoginResponse.getDrive();

            if (driveVehicleList.size()>0){
                displayVehicles(driveVehicleList);
            }else {

            }

        }catch (Exception e){
            Log.e("Error", e.toString());
        }
    }

    private void displayVehicles(List<DriveVehicle> driveVehicleList) {

        //set data and list adapter
        AdapterListExpandDriveVehicle mAdapterDrive = new AdapterListExpandDriveVehicle(this, driveVehicleList);
        recyclerViewDrive.setAdapter(mAdapterDrive);

        // on item list clicked
        mAdapterDrive.setOnItemClickListener(new AdapterListExpandDriveVehicle.OnItemClickListener() {
            @Override
            public void onItemClick(View view, DriveVehicle obj, int position) {
                Snackbar.make(view,obj.getRegistration_number(),Snackbar.LENGTH_LONG).show();
                TextView tvDetails=view.findViewById(R.id.tvDetails);
                showCustomYesNoDialog(obj.getRegistration_number(), "Would you like to veiw a detailed vehicle report?", obj, view);
                if (obj.getStatus().equals("Active")){
                    tvDetails.setTextColor(Color.GREEN);
                }else{
                    tvDetails.setTextColor(Color.RED);
                }

                //Retrieve further details for a vehicle based on supplied ID
//                readOne(obj.getId(), view);
            }
        });
    }

    public void showCustomYesNoDialog(String title, String message, final DriveVehicle obj, final View view) {
        try {


            final Dialog dialog = new Dialog(DriverActivity.this);
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
                    //Retrieve further details for a vehicle based on supplied ID
                    readOne(obj.getId(), view);
                    dialog.dismiss();
                }
            });
            ((Button) dialog.findViewById(R.id.bt_yes)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent =new Intent(DriverActivity.this, VehicleActivity.class);
                    intent.putExtra("vehicle_id",obj.getId());
                    intent.putExtra("vehicle_reg",obj.getRegistration_number());
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

    private void readOne(String id, final View view) {
        totalExpenses = 0.00;
        totalCharges = 0.00;
        totalFares = 0.00;
        final ProgressBar progressBar=view.findViewById(R.id.progressBar);
        ReadOneInterface api = AppController.getInstance().getRetrofit().create(ReadOneInterface.class);
        ServerReadOneRequest request=new ServerReadOneRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN,""));
        request.setId(id);
        request.setAction(Constants.READ_ONE_ACTION);
        Call<ServerReadOneResponse> call = api.readOne(request);
        progressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<ServerReadOneResponse>() {
            @Override
            public void onResponse(Call<ServerReadOneResponse> call, Response<ServerReadOneResponse> response) {
                Log.e("body", gson.toJson(response.body()));
                progressBar.setVisibility(View.GONE);
                if (response.body().getResponse_code().equals("0")) {
                    LinearLayout llFinancials=view.findViewById(R.id.llFinancials);
                    llFinancials.setVisibility(View.VISIBLE);
                    tvExpenses=view.findViewById(R.id.tvExpenses);
                    tvCharges=view.findViewById(R.id.tvCharges);
                    tvCollection=view.findViewById(R.id.tvCollection);


                    ServerReadOneResponse serverReadOneResponse = response.body();
                    LinearLayout lyt_expand=view.findViewById(R.id.lyt_expand);
                    lyt_expand.setVisibility(View.VISIBLE);
                    TextView tvSacco=view.findViewById(R.id.tvSacco);
                    tvSacco.setText(serverReadOneResponse.getOperator().getName());
//                    TextView tvDriverDetails=view.findViewById(R.id.tvDriverDetails);
//                    tvDriverDetails.setText(serverReadOneResponse.getDriver().getFirst_name() +" "+serverReadOneResponse.getDriver().getLast_name()+" "+serverReadOneResponse.getDriver().getPhone_number());

                    //Conductor
                    tvSacco.setText(serverReadOneResponse.getOperator().getName());
//                    TextView tvConductorDetails=view.findViewById(R.id.tvConductorDetails);
//                    tvConductorDetails.setText(serverReadOneResponse.getConductor().getFirst_name() +" "+serverReadOneResponse.getConductor().getLast_name()+" "+serverReadOneResponse.getConductor().getPhone_number());


                    List<Expense>  expenseList= serverReadOneResponse.getExpense();
                    Log.e("Expenses", String.valueOf(expenseList.size()));
                    for (Expense expense : expenseList){
                        if (expense.getId()==null||expense.getId().equals("")){
                            Log.e("Invalid expense", "Invalid expense");

                        }else {
                            Double expenseAmount = Double.valueOf(expense.getAmount());
                            totalExpenses= totalExpenses+expenseAmount;
                            tvExpenses.setText(String.format("%.2f",totalExpenses));

                        }
                    }


                    List<Charge>  chargeList= serverReadOneResponse.getCharge();
                    Log.e("Charges", String.valueOf(expenseList.size()));
                    for (Charge charge : chargeList){
                        if (charge.getId()==null||charge.getId().equals("")){
                            Log.e("Invalid expense", "Invalid expense");

                        }else {
                            Double chargeAmount= Double.valueOf(charge.getAmount());
                            totalCharges= totalCharges+chargeAmount;
                            tvCharges.setText(String.format("%.2f",totalCharges));

                        }
                    }


                    List<Ticket>  ticketList= serverReadOneResponse.getTicket();

                    displayTickets(ticketList);


                }else{
                    Toast.makeText(DriverActivity.this, "Vehicle details not retrieved!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerReadOneResponse> call, Throwable t) {
                Log.e("Error occured", t.toString());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void displayTickets(List<Ticket> ticketList) {
        List<Ticket> todayTickets=new ArrayList<>();
        for (Ticket ticket : ticketList){
            if (ticket.getId()==null||ticket.getId().equals("")){
                Log.e("Invalid ticket", "Invalid ticket");

            }else {
                if (ticket.getTravel_date().equals(AppController.getInstance().getTodaysDate())){
                    todayTickets.add(ticket);
                }

            }


        }
        for (Ticket ticket1:todayTickets){
            Double ticketAmount= Double.valueOf(ticket1.getTotal_fare());
            totalFares= totalFares+ticketAmount;
            tvCollection.setText(String.format("%.2f",totalFares));

        }
    }

    private void initListeners() {
        iVBack.setOnClickListener(this);
    }

    private void initLayouts() {
        recyclerViewDrive=findViewById(R.id.recyclerViewDrive);
        recyclerViewDrive.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewDrive.addItemDecoration(new LineItemDecoration(this, LinearLayout.VERTICAL));
        recyclerViewDrive.setHasFixedSize(true);

        iVBack=findViewById(R.id.ivBack);
        tvTitle=findViewById(R.id.tvTitle);
        tvTitle.setText("Drivers");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivBack:
                finish();
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

package ke.co.mobiticket.mobiticket.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.activities.VehicleActivity;
import ke.co.mobiticket.mobiticket.adapters.AdapterListExpandConductVehicle;
import ke.co.mobiticket.mobiticket.pojos.Charge;
import ke.co.mobiticket.mobiticket.pojos.ConductVehicle;
import ke.co.mobiticket.mobiticket.pojos.Expense;
import ke.co.mobiticket.mobiticket.pojos.Ticket;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.ReadOneInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerReadOneRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerReadOneResponse;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;
import ke.co.mobiticket.mobiticket.widgets.LineItemDecoration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConductVehiclesFragment extends Fragment {

    List<ConductVehicle> conductVehicles = new ArrayList<>();
    private RecyclerView recyclerViewConduct;
    private double totalCharges=0.00, totalExpenses=0.00, totalPayment=0.00;
    TextView tvHeader, tvExpenses, tvCharges,tvCollection;
    SharedPreferences prefs;
    public static final int DIALOG_QUEST_CODE = 300;
    Gson gson=new Gson();

    public ConductVehiclesFragment() {
        // Required empty public constructor
    }

    public static ConductVehiclesFragment newInstance(List<ConductVehicle>  conductVehicles) {
        ConductVehiclesFragment fragment = new ConductVehiclesFragment();
        fragment.conductVehicles = conductVehicles;
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.drive_vehicles_fragment));
        View view = inflater.inflate(R.layout.fragment_drive_vehicles, container, false);
        // Inflate the layout for this fragment
        initViews(view);
        prefs = AppController.getInstance().getMobiPrefs();
        return view;
    }

    private void initViews(View view) {
        tvHeader=view.findViewById(R.id.tvHeader);
        recyclerViewConduct =view.findViewById(R.id.recyclerViewDrive);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        try {

            if (conductVehicles.size()>0) {

                int vehicles = 0;
                for (ConductVehicle conductVehicle : conductVehicles) {
                    if (conductVehicle.getRegistration_number().equals(" ")) {

                    } else {
                        vehicles++;
                    }


                }
                tvHeader.setText("Vehicles you conduct:  " + String.valueOf(vehicles));

                recyclerViewConduct.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerViewConduct.addItemDecoration(new LineItemDecoration(getActivity(), LinearLayout.VERTICAL));
                recyclerViewConduct.setHasFixedSize(true);
                //set data and list adapter
                AdapterListExpandConductVehicle mAdapterDrive = new AdapterListExpandConductVehicle(getActivity(), conductVehicles);
                recyclerViewConduct.setAdapter(mAdapterDrive);

                // on item list clicked
                mAdapterDrive.setOnItemClickListener(new AdapterListExpandConductVehicle.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, ConductVehicle obj, int position) {
                        Snackbar.make(view,obj.getRegistration_number(),Snackbar.LENGTH_LONG).show();
                        TextView tvDetails=view.findViewById(R.id.tvDetails);
                        showCustomYesNoDialog("Vehicle Report", "Would you like to veiw a detailed vehicle report?", obj, view);
                        if (obj.getStatus().equals("Active")){
                            tvDetails.setTextColor(Color.GREEN);
                        }else{
                            tvDetails.setTextColor(Color.RED);
                        }



                    }
                });

            }else {
                tvHeader.setText("No vehicles");
            }

        }catch (Exception e){

        }



    }

    private void readOne(String id, final View view) {
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
if (response.body() !=null){
                if (response.body().getResponse_code().equals("0")) {
                    LinearLayout llFinancials = view.findViewById(R.id.llFinancials);
                    llFinancials.setVisibility(View.VISIBLE);
                    tvExpenses = view.findViewById(R.id.tvExpenses);
                    tvCharges = view.findViewById(R.id.tvCharges);
                    tvCollection = view.findViewById(R.id.tvCollection);

                    Toast.makeText(getActivity(), "Iko", Toast.LENGTH_SHORT).show();
                    ServerReadOneResponse serverReadOneResponse = response.body();
                    LinearLayout lyt_expand = view.findViewById(R.id.lyt_expand);
                    lyt_expand.setVisibility(View.VISIBLE);
                    TextView tvSacco = view.findViewById(R.id.tvSacco);
                    tvSacco.setText(serverReadOneResponse.getOperator().getName());
                    TextView tvDriverDetails = view.findViewById(R.id.tvDriverDetails);
                    tvDriverDetails.setText(serverReadOneResponse.getDriver().getFirst_name() + " " + serverReadOneResponse.getDriver().getLast_name() + " " + serverReadOneResponse.getDriver().getPhone_number());

                    //Conductor
                    tvSacco.setText(serverReadOneResponse.getOperator().getName());
                    TextView tvConductorDetails = view.findViewById(R.id.tvConductorDetails);
                    tvConductorDetails.setText(serverReadOneResponse.getConductor().getFirst_name() + " " + serverReadOneResponse.getConductor().getLast_name() + " " + serverReadOneResponse.getConductor().getPhone_number());

                    List<Expense> expenseList = serverReadOneResponse.getExpense();
                    Log.e("Expenses", String.valueOf(expenseList.size()));
                    for (Expense expense : expenseList) {
                        if (expense.getId() == null || expense.getId().equals("")) {
                            Log.e("Invalid expense", "Invalid expense");

                        } else {
                            totalExpenses = Double.parseDouble(totalExpenses + expense.getAmount());
                            tvExpenses.setText(String.format("%.2f",totalExpenses));
                            Toast.makeText(getActivity(), "Valid expense detected!", Toast.LENGTH_SHORT).show();
                        }
                    }


                    List<Charge> chargeList = serverReadOneResponse.getCharge();
                    Log.e("Charges", String.valueOf(chargeList.size()));
                    for (Charge charge : chargeList) {
                        if (charge.getId() == null || charge.getId().equals("")) {
                            Log.e("Invalid expense", "Invalid expense");

                        } else {
                            totalCharges = Double.parseDouble(totalCharges + charge.getAmount());
                            tvExpenses.setText(String.format("%.2f",totalExpenses));

                        }
                    }

                    List<Ticket> ticketList = serverReadOneResponse.getTicket();
                    Log.e("Tickets", String.valueOf(ticketList.size()));
                    for (Ticket ticket : ticketList) {
                        if (ticket.getId() == null || ticket.getId().equals("")) {
                            Log.e("Invalid ticket", "Invalid ticket");

                        } else {
                            try {

                            }catch (Exception e){
                                totalPayment = totalPayment + Double.parseDouble(ticket.getPayment().get(0).getAmount());
                                tvCollection.setText(String.format("%.2f",totalPayment));

                            }

                        }
                    }

                } else {
                    Toast.makeText(getActivity(), "Vehicle details not retrieved!", Toast.LENGTH_SHORT).show();
                }

            }else {
    Toast.makeText(getActivity(), "Error occured!", Toast.LENGTH_SHORT).show();
            }
            }

            @Override
            public void onFailure(Call<ServerReadOneResponse> call, Throwable t) {
                Log.e("Error occured", t.toString());
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    public void showCustomYesNoDialog(String title, String message, final ConductVehicle obj, final View view) {
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
                    //Retrieve further details for a vehicle based on supplied ID
                    readOne(obj.getId(), view);
                    dialog.dismiss();
                }
            });
            ((Button) dialog.findViewById(R.id.bt_yes)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent =new Intent(getActivity(), VehicleActivity.class);
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

}

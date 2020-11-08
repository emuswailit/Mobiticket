package ke.co.mobiticket.mobiticket.fragments;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ke.co.mobiticket.mobiticket.R;

import ke.co.mobiticket.mobiticket.adapters.AdapterListExpandOwnedVehicle;
import ke.co.mobiticket.mobiticket.pojos.Charge;
import ke.co.mobiticket.mobiticket.pojos.Expense;
import ke.co.mobiticket.mobiticket.pojos.OwnedVehicle;
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
public class OwnedVehiclesFragment extends Fragment {

    List<OwnedVehicle> ownedVehicles = new ArrayList<>();
    private RecyclerView recyclerViewOwned;
    private double totalCharges=0.00, totalExpenses=0.00, totalPayment=0.00;
    TextView tvHeader, tvExpenses, tvCharges,tvCollection;
    SharedPreferences prefs;
    public static final int DIALOG_QUEST_CODE = 300;
    Gson gson=new Gson();

    public OwnedVehiclesFragment() {
        // Required empty public constructor
    }

    public static OwnedVehiclesFragment newInstance(List<OwnedVehicle>  ownedVehicles) {
        OwnedVehiclesFragment fragment = new OwnedVehiclesFragment();
        fragment.ownedVehicles = ownedVehicles;
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
        recyclerViewOwned =view.findViewById(R.id.recyclerViewDrive);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        try {

            if (ownedVehicles.size()>0) {

                int vehicles = 0;
                for (OwnedVehicle ownedVehicle : ownedVehicles) {
                    if (ownedVehicle.getRegistration_number().equals(" ")) {

                    } else {
                        vehicles++;
                    }


                }
                tvHeader.setText("Vehicles you own:  " + String.valueOf(vehicles));

                recyclerViewOwned.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerViewOwned.addItemDecoration(new LineItemDecoration(getActivity(), LinearLayout.VERTICAL));
                recyclerViewOwned.setHasFixedSize(true);
                //set data and list adapter
                AdapterListExpandOwnedVehicle mAdapterDrive = new AdapterListExpandOwnedVehicle(getActivity(), ownedVehicles);
                recyclerViewOwned.setAdapter(mAdapterDrive);

                // on item list clicked
                mAdapterDrive.setOnItemClickListener(new AdapterListExpandOwnedVehicle.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, OwnedVehicle obj, int position) {
                        Snackbar.make(view,obj.getRegistration_number(),Snackbar.LENGTH_LONG).show();
                        TextView tvDetails=view.findViewById(R.id.tvDetails);

                        if (obj.getStatus().equals("Active")){
                            tvDetails.setTextColor(Color.GREEN);
                        }else{
                            tvDetails.setTextColor(Color.RED);
                        }

                        //Retrieve further details for a vehicle based on supplied ID
                        readOne(obj.getId(), view);
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
                if (response.body().getResponse_code().equals("0")) {
                    LinearLayout llFinancials=view.findViewById(R.id.llFinancials);
                    llFinancials.setVisibility(View.VISIBLE);
                    tvExpenses=view.findViewById(R.id.tvExpenses);
                    tvCharges=view.findViewById(R.id.tvCharges);
                    tvCollection=view.findViewById(R.id.tvCollection);

                    Toast.makeText(getActivity(), "Iko", Toast.LENGTH_SHORT).show();
                    ServerReadOneResponse serverReadOneResponse = response.body();
                    LinearLayout lyt_expand=view.findViewById(R.id.lyt_expand);
                    lyt_expand.setVisibility(View.VISIBLE);
                    TextView tvSacco=view.findViewById(R.id.tvSacco);
                    tvSacco.setText(serverReadOneResponse.getOperator().getName());
                    TextView tvDriverDetails=view.findViewById(R.id.tvDriverDetails);
                    tvDriverDetails.setText(serverReadOneResponse.getDriver().getFirst_name() +" "+serverReadOneResponse.getDriver().getLast_name()+" "+serverReadOneResponse.getDriver().getPhone_number());

                    //Conductor
                    tvSacco.setText(serverReadOneResponse.getOperator().getName());
                    TextView tvConductorDetails=view.findViewById(R.id.tvConductorDetails);
                    tvConductorDetails.setText(serverReadOneResponse.getConductor().getFirst_name() +" "+serverReadOneResponse.getConductor().getLast_name()+" "+serverReadOneResponse.getConductor().getPhone_number());

                    List<Expense>  expenseList= serverReadOneResponse.getExpense();
                    Log.e("Expenses", String.valueOf(expenseList.size()));
                    for (Expense expense : expenseList){
                        if (expense.getId()==null||expense.getId().equals("")){
                            Log.e("Invalid expense", "Invalid expense");

                        }else {
                            totalExpenses= Double.parseDouble(totalExpenses+expense.getAmount());
                            tvExpenses.setText(String.valueOf(totalExpenses));
                            Toast.makeText(getActivity(), "Valid expense detected!", Toast.LENGTH_SHORT).show();
                        }
                    }


                    List<Charge>  chargeList= serverReadOneResponse.getCharge();
                    Log.e("Charges", String.valueOf(chargeList.size()));
                    for (Charge charge : chargeList){
                        if (charge.getId()==null||charge.getId().equals("")){
                            Log.e("Invalid expense", "Invalid expense");

                        }else {
                            totalCharges= Double.parseDouble(totalCharges+charge.getAmount());
                            tvExpenses.setText(String.format("%.2f",totalExpenses));
                            Toast.makeText(getActivity(), "Valid expense detected!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    List<Ticket>  ticketList= serverReadOneResponse.getTicket();
                    Log.e("Tickets", String.valueOf(ticketList.size()));
                    for (Ticket ticket : ticketList){
                        if (ticket.getId()==null||ticket.getId().equals("")){
                            Log.e("Invalid ticket", "Invalid ticket");

                        }else {
                            totalPayment= Double.parseDouble(totalPayment+ticket.getPayment().get(0).getAmount());
                            tvCollection.setText(String.format("%.2f",totalPayment));

                        }
                    }

                }else{
                    Toast.makeText(getActivity(), "Vehicle details not retrieved!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerReadOneResponse> call, Throwable t) {
                Log.e("Error occured", t.toString());
                progressBar.setVisibility(View.GONE);
            }
        });
    }


}

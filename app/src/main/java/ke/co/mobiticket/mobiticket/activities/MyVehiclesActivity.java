package ke.co.mobiticket.mobiticket.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.adapters.HybridVehicleAdapter;
import ke.co.mobiticket.mobiticket.pojos.Drive;
import ke.co.mobiticket.mobiticket.pojos.HybridVehicle;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.PinlessLoginInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.PinlessLoginRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.PinlessLoginResponse;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;
import ke.co.mobiticket.mobiticket.widgets.LineItemDecoration;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyVehiclesActivity extends BaseActivity implements View.OnClickListener {
Gson gson=new Gson();
    SharedPreferences prefs;
    List<HybridVehicle> hybridVehicleList=new ArrayList<>();
    List<HybridVehicle> filteredVehicleList=new ArrayList<>();
    List<HybridVehicle> combinedVehicleList=new ArrayList<>();
    List<String> vehicleNumbersList=new ArrayList<>();
    private TextView tvMyVehicles,tvTitle;
    private RecyclerView recyclerView;
    private ImageView ivBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_vehicles);
prefs=AppController.getInstance().getMobiPrefs();
        initLayouts();
        initListeners();

        retrieveData();

    }

    private void retrieveData() {

        final Dialog dialog=new Dialog(MyVehiclesActivity.this);
        PinlessLoginInterface api= AppController.getInstance().getRetrofit().create(PinlessLoginInterface.class);
        PinlessLoginRequest request=new PinlessLoginRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN,""));
        request.setAction(Constants.ACTION_PINLESS_LOGIN);
        request.setPhone_number(prefs.getString(Constants.PHONE_NUMBER,""));

        Call<PinlessLoginResponse> call=api.retrieveData(request);

        showProgressDialog(dialog, "Refreshing data"+getResources().getString(R.string.txt_please_wait));
        call.enqueue(new Callback<PinlessLoginResponse>() {
            @Override
            public void onResponse(Call<PinlessLoginResponse> call, Response<PinlessLoginResponse> response) {
                dialog.dismiss();
                Log.e("fdfd", gson.toJson(response.body()));
                if (response.body().getResponse_code().equals("0")){
                    combinedVehicleList.clear();
                    List<HybridVehicle> driveList=response.body().getDrive();
                    for (HybridVehicle hybridVehicle: driveList){
                        if (combinedVehicleList.add(hybridVehicle));
                    }

                    List<HybridVehicle> conductList=response.body().getConduct();
                    for (HybridVehicle hybridVehicle: conductList){
                        if (combinedVehicleList.add(hybridVehicle));
                    }

                    List<HybridVehicle> ownList=response.body().getOwned();
                    for (HybridVehicle hybridVehicle: ownList){
                        if (combinedVehicleList.add(hybridVehicle));
                    }

                    Log.e("combinedVehicleList", String.valueOf(combinedVehicleList.size()));

                    for (HybridVehicle vehicle: combinedVehicleList){
                        if (vehicleNumbersList.contains(vehicle.getRegistration_number())){
                            Log.e("Already added",vehicle.getRegistration_number());
                        }else {
                            filteredVehicleList.add(vehicle);
                            vehicleNumbersList.add(vehicle.getRegistration_number());
                            Log.e("Added",vehicle.getRegistration_number());
                        }
                    }
                    Log.e("vehicleNumbersList", String.valueOf(vehicleNumbersList.size()));
                    for (HybridVehicle v: filteredVehicleList){
                        Log.e("from v",v.getRegistration_number());
                    }

                    if (filteredVehicleList.size()>0){
                        tvMyVehicles.setText("Your vehicles: "+filteredVehicleList.size());
                        try {
                            HybridVehicleAdapter hybridVehicleAdapter=new HybridVehicleAdapter( filteredVehicleList, MyVehiclesActivity.this);
                            recyclerView.setAdapter(hybridVehicleAdapter);
                        }catch (Exception e){
                            Log.e("fdfd", e.toString());
                        }


                    }else {
                        tvMyVehicles.setText("No vehicles to display");
                    }



                    Log.e("filteredVehicleList", String.valueOf(filteredVehicleList.size()));
                }else {
                    showCustomDialog("Vehicle details", response.body().getResponse_message());
                }
            }

            @Override
            public void onFailure(Call<PinlessLoginResponse> call, Throwable t) {
dialog.dismiss();
showCustomDialog("Vehicle details", "System errror occurred. Please try again");
                Log.e("error", t.getLocalizedMessage());
            }
        });
    }

    private void initListeners() {
ivBack.setOnClickListener(this);
    }

    private void initLayouts() {
ivBack=findViewById(R.id.ivBack);
tvTitle=findViewById(R.id.tvTitle);
tvTitle.setText("My Vehicles");
recyclerView=findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new LineItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setHasFixedSize(true);
tvMyVehicles=findViewById(R.id.tvMyVehicles);

    }

    public void customMethod(HybridVehicle myList) {
        Toast.makeText(this, "Hello "+ myList.getRegistration_number(), Toast.LENGTH_SHORT).show();
        Intent intent= new Intent(MyVehiclesActivity.this, VehicleActivity.class);
        intent.putExtra("vehicle_id",myList.getId());
        intent.putExtra("vehicle_reg",myList.getRegistration_number());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.ivBack:
                finish();
                break;
        }
    }
}

package ke.co.mobiticket.mobiticket.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.fragments.ConductVehiclesFragment;
import ke.co.mobiticket.mobiticket.fragments.DriveVehiclesFragment;
import ke.co.mobiticket.mobiticket.fragments.OwnedVehiclesFragment;
import ke.co.mobiticket.mobiticket.pojos.ConductVehicle;
import ke.co.mobiticket.mobiticket.pojos.DriveVehicle;
import ke.co.mobiticket.mobiticket.pojos.OwnedVehicle;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerLoginResponse;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;

public class OperatorsActivity extends BaseActivity implements View.OnClickListener {
    Button btnVehiclesDrive,btnVehiclesConduct,btnVehiclesOwn;
    SharedPreferences prefs;
    ImageView ivBack;
    List<DriveVehicle>  driveVehicleList = new ArrayList<>();
    List<ConductVehicle>  conductVehicleList = new ArrayList<>();
    List<OwnedVehicle> ownVehicleList = new ArrayList<>();
    Gson gson=new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operators);
        btnVehiclesDrive=findViewById(R.id.btnVehiclesDrive);
        btnVehiclesConduct=findViewById(R.id.btnVehiclesConduct);
        btnVehiclesOwn=findViewById(R.id.btnVehiclesOwn);
        ivBack=findViewById(R.id.ivBack);

        btnVehiclesDrive.setOnClickListener(this);
        btnVehiclesConduct.setOnClickListener(this);
        btnVehiclesOwn.setOnClickListener(this);
        ivBack.setOnClickListener(this);

        prefs = AppController.getInstance().getMobiPrefs();
        Log.e("from up",prefs.getString(Constants.ENTIRE_RESPONSE, "") );
        try {

            //Retrieve vehicles that the user drives, if any
            ServerLoginResponse serverLoginResponse= gson.fromJson(prefs.getString(Constants.ENTIRE_RESPONSE, ""),ServerLoginResponse.class);
            driveVehicleList = serverLoginResponse.getDrive();
            conductVehicleList = serverLoginResponse.getConduct();
            ownVehicleList = serverLoginResponse.getOwned();

            Log.e("serverLoginResponse", gson.toJson(serverLoginResponse));

        }catch (Exception e){
            Log.e("Error", e.toString());
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnVehiclesDrive:

                if (driveVehicleList.size()>0){
                    btnVehiclesDrive.setVisibility(View.VISIBLE);
                    DriveVehiclesFragment driveVehiclesFragment =   DriveVehiclesFragment.newInstance(driveVehicleList);
                    loadFragment(driveVehiclesFragment);
                }else {
                    Toast.makeText(this, "No vehicles retrieved!", Toast.LENGTH_SHORT).show();
                }

                return;
            case R.id.btnVehiclesConduct:
                if (conductVehicleList.size()>0){
                    btnVehiclesConduct.setVisibility(View.VISIBLE);
                    ConductVehiclesFragment conductVehiclesFragment =   ConductVehiclesFragment.newInstance(conductVehicleList);
                    loadFragment(conductVehiclesFragment);

                }else {
                    Toast.makeText(this, "No vehicles retrieved!", Toast.LENGTH_SHORT).show();
                }

                return;
            case R.id.btnVehiclesOwn:

                if (ownVehicleList.size()>0){
                    btnVehiclesOwn.setVisibility(View.VISIBLE);
                    OwnedVehiclesFragment ownVehiclesFragment =   OwnedVehiclesFragment.newInstance(ownVehicleList);
                    loadFragment(ownVehiclesFragment);
                }else {
                    Toast.makeText(this, "No vehicles retrieved!", Toast.LENGTH_SHORT).show();
                }

                return;

            case R.id.ivBack:
                startActivity(DashboardActivity.class);
        }



    }
}

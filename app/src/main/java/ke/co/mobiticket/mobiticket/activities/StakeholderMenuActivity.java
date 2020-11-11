package ke.co.mobiticket.mobiticket.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.pojos.ConductVehicle;
import ke.co.mobiticket.mobiticket.pojos.DriveVehicle;
import ke.co.mobiticket.mobiticket.pojos.OwnedVehicle;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerLoginResponse;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;

public class StakeholderMenuActivity extends BaseActivity implements View.OnClickListener {

    SharedPreferences prefs;
    List<DriveVehicle> driveVehicleList = new ArrayList<>();
    List<ConductVehicle>  conductVehicleList = new ArrayList<>();
    List<OwnedVehicle> ownVehicleList = new ArrayList<>();
    Gson gson=new Gson();
    private LinearLayout cardOwners, cardDrivers,cardConductors, cardMarketPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stakeholder_menu);

        initLayouts();
        initListeners();

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

    private void initListeners() {
        cardOwners.setOnClickListener(this);
        cardDrivers.setOnClickListener(this);
        cardConductors.setOnClickListener(this);
        cardMarketPlace.setOnClickListener(this);
    }

    private void initLayouts() {
        cardOwners=findViewById(R.id.cardOwners);
        cardDrivers=findViewById(R.id.cardDrivers);
        cardConductors=findViewById(R.id.cardConductors);
        cardMarketPlace=findViewById(R.id.cardMarketPlace);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cardOwners:
                Log.e("clicked", "owner");
                if (ownVehicleList.size()>0){
                   startActivity(OwnerActivity.class);
                    Toast.makeText(this, "Welcome Mr Owner", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Not allowed", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.cardDrivers:

                if (driveVehicleList.size()>0){
                    startActivity(DriverActivity.class);
                    Toast.makeText(this, "Welcome Mr Driver", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Not allowed", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.cardConductors:
                if (driveVehicleList.size()>0){
                    startActivity(ConductorActivity.class);
                    Toast.makeText(this, "Welcome Mr Conductor", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Not allowed", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.cardMarketPlace:
                startActivity(MarketPlaceActivity.class);
                Toast.makeText(this, "Welcome to the marketplace", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

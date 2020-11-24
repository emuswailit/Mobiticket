package ke.co.mobiticket.mobiticket.activities;

import androidx.annotation.NonNull;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.utilities.AppController;

public class MoreActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTitle, tvProfileSettings,tvWallet, tvTapCard,tvMyVehicles,tvHelp,tvSetting,tvLogout;
    private ImageView ivLogout,ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        initLayouts();
        initListeners();

        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.action_vehicles:
                        startActivity(MyVehiclesActivity.class);
                        break;
                    case R.id.action_tickets:
                        startActivity(TicketsActivity.class);
                        break;
                    case R.id.action_more:
                        break;

                }
                return true;
            }
        });
    }

    private void initListeners() {

        ivLogout.setOnClickListener(this);
        tvProfileSettings.setOnClickListener(this);
        tvWallet.setOnClickListener(this);
        tvMyVehicles.setOnClickListener(this);
        tvWallet.setOnClickListener(this);
        tvMyVehicles.setOnClickListener(this);
        tvTapCard.setOnClickListener(this);
        tvHelp.setOnClickListener(this);
        tvSetting.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    private void initLayouts() {
        ivLogout=findViewById(R.id.ivLogout);
        ivBack=findViewById(R.id.ivBack);
        tvTitle=findViewById(R.id.tvTitle);
        tvTitle.setText("More..");
        tvProfileSettings=findViewById(R.id.tvProfileSettings);
        tvWallet=findViewById(R.id.tvWallet);
        tvMyVehicles=findViewById(R.id.tvMyVehicles);
        tvTapCard=findViewById(R.id.tvTapCard);
        tvHelp=findViewById(R.id.tvHelp);
        tvSetting=findViewById(R.id.tvSetting);
        tvLogout=findViewById(R.id.tvLogout);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvProfileSettings:
                startActivity(ProfileSettingsActivity.class);
                finish();
                break;
            case R.id.tvWallet:
                startActivity(WalletActivity.class);
                finish();
                break;
            case R.id.tvMyVehicles:
                startActivity(MyVehiclesActivity.class);
                finish();
                break;
            case R.id.tvTapCard:
                startActivity(NFCActivity.class);
                finish();
                break;
            case R.id.tvHelp:
                startActivity(HelpActivity.class);
                finish();
                break;
            case R.id.tvSetting:
                break;
            case R.id.tvLogout:

                showLogoutYesNoDialog("Log out", "Do you really want to log out?",MoreActivity.this);
                break;
            case R.id.ivLogout:

                showLogoutYesNoDialog("Log out", "Do you really want to log out?",MoreActivity.this);
                break;
            case R.id.ivBack:
               startActivity(DashboardActivity.class);
               finish();
                break;
            default:
                break;
        }
    }


}

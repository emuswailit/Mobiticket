package ke.co.mobiticket.mobiticket.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.fragments.MoreFragment;
import ke.co.mobiticket.mobiticket.utilities.AppController;

public class MoreActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTitle, tvProfileSettings,tvWallet, tvTapCard,tvMyVehicles,tvHelp,tvSetting,tvLogout;


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
                    case R.id.action_home:
                    startActivity(DashboardActivity.class);
                        break;
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

        tvProfileSettings.setOnClickListener(this);
        tvWallet.setOnClickListener(this);
        tvMyVehicles.setOnClickListener(this);
        tvWallet.setOnClickListener(this);
        tvMyVehicles.setOnClickListener(this);
        tvTapCard.setOnClickListener(this);
        tvHelp.setOnClickListener(this);
        tvSetting.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
    }

    private void initLayouts() {
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
                AppController.getInstance().logOutUser();
                finish();
                break;
            default:
                break;
        }
    }
}

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

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.utilities.AppController;

public class MoreActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTitle, tvProfileSettings,tvWallet, tvTapCard,tvMyVehicles,tvHelp,tvSetting,tvLogout;
    private ImageView ivLogout;


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
    }

    private void initLayouts() {
        ivLogout=findViewById(R.id.ivLogout);
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
                showLogoutYesNoDialog("Log out", "Do you really want to log out?");
                break;
            case R.id.ivLogout:
                showLogoutYesNoDialog("Log out", "Do you really want to log out?");
                break;
            default:
                break;
        }
    }

    public void showLogoutYesNoDialog(String title, String message) {
        try {


            final Dialog dialog = new Dialog(MoreActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_exit_activity_or_no);
            dialog.setCancelable(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


            TextView tvTitle = dialog.findViewById(R.id.title);
            TextView tvContent = dialog.findViewById(R.id.content);
            tvContent.setText(message);
            tvTitle.setText(title);

            dialog.findViewById(R.id.bt_no).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });
            dialog.findViewById(R.id.bt_yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppController.getInstance().logOutUser();
                    startActivity(SelectionActivity.class);
                    finish();
                }
            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            Log.e("Dialog", e.toString());
        }
    }
}

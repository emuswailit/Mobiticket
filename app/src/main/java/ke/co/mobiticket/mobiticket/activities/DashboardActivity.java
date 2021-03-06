package ke.co.mobiticket.mobiticket.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.fragments.BookingFragment;
import ke.co.mobiticket.mobiticket.fragments.Home1Fragment;
import ke.co.mobiticket.mobiticket.fragments.MoreFragment;
import ke.co.mobiticket.mobiticket.fragments.OffersFragment;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;


public class DashboardActivity extends BaseActivity implements View.OnClickListener {
    /*variable declaration*/
    private TextView mTvTitle;
    private ImageView mIvNotification, mIvHome, mIvPackages, mIvBooking, mIvOther,ivLogout;
    private Home1Fragment home1Fragment = new Home1Fragment();
    private OffersFragment mFragmentOffers = new OffersFragment();
    private BookingFragment mMyBookingFragment = new BookingFragment();
    private MoreFragment mMoreFragment = new MoreFragment();
    private LinearLayout mLlHome, mLllPackages, mLlBooking, mLlMore;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initLayouts();
        initializeListeners();

//        setSelected(mIvHome);


        loadFragment(home1Fragment);


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

                        startActivity(MoreActivity.class);

                }
                return true;
            }
        });
    }



    /* init layout */
    @SuppressLint("ClickableViewAccessibility")
    public void initLayouts() {

        ivLogout = findViewById(R.id.ivLogout);
        mTvTitle = findViewById(R.id.tvTitle);mTvTitle.setText("Buses & Destinations");
//        mIvNotification = findViewById(R.id.ivNotification);
//        mLlHome = findViewById(R.id.llHome);
//        mLllPackages = findViewById(R.id.llPackage);
//        mLlBooking = findViewById(R.id.llBooking);
//        mLlMore = findViewById(R.id.llMore);
//        mIvHome = findViewById(R.id.ivHome);
//        mIvPackages = findViewById(R.id.ivPackages);
//        mIvBooking = findViewById(R.id.ivBooking);
//        mIvOther = findViewById(R.id.ivMore);
//        mTvTitle.setText(HomeFragment.mTitle);

    }

    /* initialize listener */
    public void initializeListeners() {
ivLogout.setOnClickListener(this);
    }





    /* onBack press */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /* onClick listener */
    @Override
    public void onClick(View v) {
switch (v.getId()){
    case R.id.ivLogout:
        showLogoutYesNoDialog("Log out", "Do you really want to log out?", DashboardActivity.this);
        break;
}

    }

    /* get Activity result */

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 101: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (home1Fragment.isVisible()) {
//                        homeFragment.ChangeDestination(result.get(0));
                    } else {
                        loadFragment(home1Fragment);
                    }
                }
                break;
            }

        }
    }

}

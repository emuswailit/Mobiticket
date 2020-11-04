package ke.co.mobiticket.mobiticket.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.fragments.BookingFragment;
import ke.co.mobiticket.mobiticket.fragments.HomeFragment;
import ke.co.mobiticket.mobiticket.fragments.MoreFragment;
import ke.co.mobiticket.mobiticket.fragments.OffersFragment;


public class DashboardActivity extends BaseActivity implements View.OnClickListener {
    /*variable declaration*/
    private TextView mTvTitle;
    private ImageView mIvNotification, mIvHome, mIvPackages, mIvBooking, mIvOther;
    private HomeFragment homeFragment = new HomeFragment();
    private OffersFragment mFragmentOffers = new OffersFragment();
    private BookingFragment mMyBookingFragment = new BookingFragment();
    private MoreFragment mMoreFragment = new MoreFragment();
    private LinearLayout mLlHome, mLllPackages, mLlBooking, mLlMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        initLayouts();
        initializeListeners();

        setSelected(mIvHome);
        loadFragment(homeFragment);

    }

    /* init layout */
    @SuppressLint("ClickableViewAccessibility")
    public void initLayouts() {

        mTvTitle = findViewById(R.id.tvTitle);
        mIvNotification = findViewById(R.id.ivNotification);
        mLlHome = findViewById(R.id.llHome);
        mLllPackages = findViewById(R.id.llPackage);
        mLlBooking = findViewById(R.id.llBooking);
        mLlMore = findViewById(R.id.llMore);
        mIvHome = findViewById(R.id.ivHome);
        mIvPackages = findViewById(R.id.ivPackages);
        mIvBooking = findViewById(R.id.ivBooking);
        mIvOther = findViewById(R.id.ivMore);
        mTvTitle.setText(HomeFragment.mTitle);

    }

    /* initialize listener */
    public void initializeListeners() {
        mIvNotification.setOnClickListener(this);
        mLlHome.setOnClickListener(this);
        mLllPackages.setOnClickListener(this);
        mLlBooking.setOnClickListener(this);
        mLlMore.setOnClickListener(this);
        SetNotificationImage(mIvNotification);

    }

    /* set selected item in bottom navigation */
    private void setSelected(ImageView mBarImg) {
        mBarImg.setBackground(getResources().getDrawable(R.drawable.bg_tint_icon));

    }

    /* Update UI */
    private void updateUi() {
        mIvHome.setImageResource(R.drawable.ic_home);
        mIvHome.setBackground(null);
        mIvPackages.setImageResource(R.drawable.ic_package);
        mIvPackages.setBackground(null);
        mIvBooking.setImageResource(R.drawable.ic_booking);
        mIvBooking.setBackground(null);
        mIvOther.setImageResource(R.drawable.ic_fill);
        mIvOther.setBackground(null);

    }

    /* onBack press */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /* onClick listener */
    @Override
    public void onClick(View v) {

        if (v == mIvNotification) {
            startActivity(NotificationActivity.class);
            return;
        }
        updateUi();

        switch (v.getId()) {

            case R.id.llHome:
                if (!homeFragment.isVisible()) {
                    mTvTitle.setText(HomeFragment.mTitle);
                    loadFragment(homeFragment);
                }
                setSelected(mIvHome);
                mIvHome.setImageResource(R.drawable.ic_home_fill);
                break;
            case R.id.llPackage:
                if (!mFragmentOffers.isVisible()) {
                    mTvTitle.setText(OffersFragment.mTitle);
                    loadFragment(mFragmentOffers);
                }
                setSelected(mIvPackages);
                mIvPackages.setImageResource(R.drawable.ic_package_fill);
                break;
            case R.id.llBooking:
                startActivity(TicketsActivity.class);
//                if (!mMyBookingFragment.isVisible()) {
//                    mTvTitle.setText(BookingFragment.mTitle);
//                    loadFragment(mMyBookingFragment);
//                }
//                setSelected(mIvBooking);
//                mIvBooking.setImageResource(R.drawable.ic_booking_fill);
                break;
            case R.id.llMore:
                if (!mMoreFragment.isVisible()) {
                    mTvTitle.setText(MoreFragment.mTitle);
                    loadFragment(mMoreFragment);
                }
                setSelected(mIvOther);
                mIvOther.setImageResource(R.drawable.ic_more_fill2);
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
                    if (homeFragment.isVisible()) {
//                        homeFragment.ChangeDestination(result.get(0));
                    } else {
                        loadFragment(homeFragment);
                    }
                }
                break;
            }

        }
    }

}

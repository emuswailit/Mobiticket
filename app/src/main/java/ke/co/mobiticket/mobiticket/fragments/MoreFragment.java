package ke.co.mobiticket.mobiticket.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.activities.BaseActivity;
import ke.co.mobiticket.mobiticket.activities.CardsActivity;
import ke.co.mobiticket.mobiticket.activities.HelpActivity;
import ke.co.mobiticket.mobiticket.activities.MyVehiclesActivity;
import ke.co.mobiticket.mobiticket.activities.NFCActivity;
import ke.co.mobiticket.mobiticket.activities.ProfileSettingsActivity;
import ke.co.mobiticket.mobiticket.activities.SelectionActivity;

import ke.co.mobiticket.mobiticket.activities.SettingsActivity;
import ke.co.mobiticket.mobiticket.activities.WalletActivity;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreFragment extends Fragment implements View.OnClickListener {

    /*variable declaration*/
    public static final String mTitle = "More";
    private TextView mTvProfileSettings, mTvWallet, mTvCards, mTvOperators, mTvHelp, mTvLogout, mTvSetting,tvTapCard;
    private String mFlag = "1";
    SharedPreferences prefs;


    /* create view */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_more, null);
        initLayouts(view);
        initializeListeners();
        prefs=AppController.getInstance().getMobiPrefs();
        return view;
    }

    /* initialize listener */
    private void initializeListeners() {
        mTvProfileSettings.setOnClickListener(this);
        mTvWallet.setOnClickListener(this);
//        mTvCards.setOnClickListener(this);
        mTvOperators.setOnClickListener(this);
        mTvHelp.setOnClickListener(this);
        mTvLogout.setOnClickListener(this);
        mTvSetting.setOnClickListener(this);
        tvTapCard.setOnClickListener(this);
    }

    /* init layout */
    private void initLayouts(View view) {
        mTvProfileSettings = view.findViewById(R.id.tvProfileSettings);
        mTvWallet = view.findViewById(R.id.tvWallet);
//        mTvCards = view.findViewById(R.id.tvCards);
        mTvOperators = view.findViewById(R.id.tvMyVehicles);
        mTvHelp = view.findViewById(R.id.tvHelp);
        mTvLogout = view.findViewById(R.id.tvLogout);
        mTvSetting = view.findViewById(R.id.tvSetting);
        tvTapCard = view.findViewById(R.id.tvTapCard);
    }

    /* onClick listener */
    @Override
    public void onClick(View v) {
        if (v == mTvProfileSettings)
            ((BaseActivity) requireActivity()).startActivity(ProfileSettingsActivity.class);
        else if (v == mTvWallet)
            ((BaseActivity) requireActivity()).startActivity(WalletActivity.class);
        else if (v == mTvOperators)
            ((BaseActivity) requireActivity()).startActivity(MyVehiclesActivity.class);
        else if (v == mTvHelp)
            ((BaseActivity) requireActivity()).startActivity(HelpActivity.class);
        else if (v == mTvSetting)
            ((BaseActivity) requireActivity()).startActivity(SettingsActivity.class);
        else if (v == mTvLogout) {
           logoutUser();
        }else if (v==tvTapCard){
            startActivity(new Intent(getActivity(), NFCActivity.class));
        }
    }

    private void logoutUser() {
        AppController.getInstance().logOutUser();
        getActivity().finish();
        Toast.makeText(getActivity(), "Looged out", Toast.LENGTH_SHORT).show();

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(Constants.RECENT_ROUTES, "");
        editor.apply();


        startActivity(new Intent(getActivity(), SelectionActivity.class));
    }
}

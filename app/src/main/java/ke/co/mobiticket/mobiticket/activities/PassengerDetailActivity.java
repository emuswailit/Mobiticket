package ke.co.mobiticket.mobiticket.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.pojos.Passenger;
import ke.co.mobiticket.mobiticket.pojos.PassengerInput;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.GenerateReferenceNumberInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.GenerateReferenceNumberRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.GenerateReferenceNumberResponse;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PassengerDetailActivity extends BaseActivity implements View.OnClickListener {
    /*variable declaration*/
    private Button mBtnBook;
    private LinearLayout mLlDynamicContent;
    private int mCount;
    private ProgressBar progressBar;
    private int seatCount=1;
    private String[] mSplited;
    private EditText etAccountOwnerPhone, etAccountOwnerNames, etOtherPassengerFirstName, etOtherPassengerLastName, etOtherPassengerPhone,etOtherPassengerEmail;
    private ImageView mIVBack;
    private Boolean selfTravelling = true;
    private LinearLayout llAccountOwnerDetails;
    private String accountOwnerFirstName, accountOwnerMiddleName, accountOwnerLastName, accountOwnerPhone;
    List<Passenger> passengerList = new ArrayList<>();

    SharedPreferences prefs;
    List<PassengerInput> passengerInputList = new ArrayList<>();
    private Boolean accountOwnerTravelling = true;

    public Boolean getAccountOwnerTravelling() {
        return accountOwnerTravelling;
    }

    public void setAccountOwnerTravelling(Boolean accountOwnerTravelling) {
        this.accountOwnerTravelling = accountOwnerTravelling;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_detail);
        prefs = AppController.getInstance().getMobiPrefs();
        initLayouts();
        String title = "Number of seats required";
        String message = "Include ticket for " + AppController.getInstance().camelCase(prefs.getString(Constants.FIRST_NAME, "")) + " " + AppController.getInstance().camelCase(prefs.getString(Constants.LAST_NAME, "")) + " ? ";
        showYesNoDialog(title, message);
        try {
            initializeListeners();
        } catch (Exception e) {
            Log.e("error", e.toString());
        }


    }

    /* init layout */
    private void initLayouts() {
        mBtnBook = findViewById(R.id.btnBook);
        progressBar = findViewById(R.id.progressBar);
        mLlDynamicContent = findViewById(R.id.llDynamicContent);
        etAccountOwnerNames = findViewById(R.id.etAccountOwnerName);
        etAccountOwnerPhone = findViewById(R.id.etAccountOwnerPhone);
        llAccountOwnerDetails = findViewById(R.id.llAccountOwnerDetails);
        mIVBack = findViewById(R.id.ivBack);


    }

    /* initialize listener */
    private void initializeListeners() {
        mBtnBook.setOnClickListener(this);
        mIVBack.setOnClickListener(this);
        mBtnBook.setStateListAnimator(null);
        mCount = AppController.getInstance().getMobiPrefs().getInt(Constants.SEAT_COUNT, 0);
        accountOwnerFirstName = prefs.getString(Constants.FIRST_NAME, "");
        accountOwnerMiddleName = prefs.getString(Constants.MIDDLE_NAME, "");
        accountOwnerLastName = prefs.getString(Constants.LAST_NAME, "");
        accountOwnerPhone = prefs.getString(Constants.PHONE_NUMBER, "");
    }

    /* onClick listener */
    @Override
    public void onClick(View v) {
        if (v == mBtnBook) {
            try {
                if (accountOwnerTravelling) {
                    Passenger passenger = new Passenger();

                    passenger.setMsisdn(prefs.getString(Constants.PHONE_NUMBER, ""));
                    passenger.setEmail_address(prefs.getString(Constants.EMAIL_ADDRESS, ""));
                    passenger.setFirst_name(prefs.getString(Constants.FIRST_NAME, ""));
                    passenger.setLast_name(prefs.getString(Constants.LAST_NAME, ""));
                    passenger.setMiddle_name(prefs.getString(Constants.MIDDLE_NAME, ""));
                    passengerList.add(passenger);
                }

                for (PassengerInput pi : passengerInputList) {
                    String first_name = pi.getEtFirstName().getText().toString();
                    String last_name = pi.getEtLastName().getText().toString();
                    String email = pi.getEtEmail().getText().toString();
                    String phone = pi.getEtPhone().getText().toString();
                    Log.e("full names", first_name + " " + last_name);          Passenger passenger = new Passenger();
                    passenger.setSource(Constants.SOURCE);
                    passenger.setMsisdn(phone);
                    passenger.setEmail_address(email);
                    passenger.setFirst_name(first_name);
                    passenger.setLast_name(last_name);
                    passenger.setMiddle_name(" ");

                    passengerList.add(passenger);
                }

                if (passengerList.size()>0){
                    if (AppController.getInstance().isNetworkConnected()){
                        try{
                            Gson gson=new Gson();
                            Log.e("array", gson.toJson(passengerList));
                            generateReferenceNumber(passengerList);
                        }catch(Exception e){
                            Log.e("gerenerateRefNum:", e.toString());
                        }


                    }else {
                        startActivity(NoInternetActivity.class);
                    }




                }
            }catch (Exception e){
                Log.e("error", e.toString());
            }


            // if (validate()) {
//            startActivity(PaymentActivity.class);
            //  }
        } else if (v == mIVBack) {
            onBackPressed();
        }
    }

    private void generateReferenceNumber(final List<Passenger> passengerList) {
final Dialog dialog=new Dialog(PassengerDetailActivity.this);
        //Generate reference number for the tickets
        GenerateReferenceNumberInterface api= AppController.getInstance().getRetrofit().create(GenerateReferenceNumberInterface.class);
        GenerateReferenceNumberRequest request= new GenerateReferenceNumberRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN, ""));
        request.setAction(Constants.CREATE_REF_NUMBER_ACTION);
       showProgressDialog(dialog, "Saving details\n\nPlease wait.....");
        Call<GenerateReferenceNumberResponse> call= api.generateReferenceNumber(request);
        call.enqueue(new Callback<GenerateReferenceNumberResponse>() {
            @Override
            public void onResponse(Call<GenerateReferenceNumberResponse> call, Response<GenerateReferenceNumberResponse> response) {
                dialog.dismiss();
                if (response.body() !=null ){
                    GenerateReferenceNumberResponse referenceNumberResponse=response.body();

                    if (referenceNumberResponse.getResponse_code().equals("0")){
                        if (!referenceNumberResponse.getReference_number().isEmpty()||!referenceNumberResponse.getReference_number().equals("") ){
                            if (passengerList.size()>0){
                                String reference_number=referenceNumberResponse.getReference_number();
                                finalizePassengerData(passengerList,reference_number );
                                Log.e("reference", reference_number);
                            }
                        }
                    }else {
                        showCustomDialog("Generating reference number", referenceNumberResponse.getResponse_message());
                    }
                }
            }

            @Override
            public void onFailure(Call<GenerateReferenceNumberResponse> call, Throwable t) {
                dialog.dismiss();
            }
        });

    }

    private void finalizePassengerData(List<Passenger> passengerList, String reference_number) {

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(Constants.TICKET_REFERENCE_NUMBER, reference_number);
        editor.apply();
        for (Passenger passenger : passengerList) {
            passenger.setReference_number(reference_number);
        }



        try {


            //Represent finalized ticket as json and save it as shared preferences for use in future activities
            Gson gson=new Gson();
            SharedPreferences.Editor editor1 = prefs.edit();
            editor1.putString(Constants.PASSENGER_DATA_THIS_BOOKING, gson.toJson(passengerList));
            editor1.apply();

            Log.e("passengers", gson.toJson(passengerList));

        Intent intent =new Intent(PassengerDetailActivity.this, PaymentMethodsActivity.class);
        startActivity(intent);


        }catch (Exception e){
            Log.e("error", e.toString());
        }


    }

    private void reserveSeats(List<Passenger> passengerList) {

    }

    /* validations */
    private boolean validate() {
        boolean flag = true;
        if (TextUtils.isEmpty(etAccountOwnerPhone.getText())) {
            flag = false;
            showToast(getString(R.string.msg_mobile_number));
        } else if (TextUtils.isEmpty(etOtherPassengerLastName.getText())) {
            flag = false;
            showToast(getString(R.string.msg_last_name));
        } else if (TextUtils.isEmpty(etOtherPassengerFirstName.getText())) {
            flag = false;
            showToast(getString(R.string.msg_first_name));
        }
        return flag;
    }

    private void showYesNoDialog(String title, String message) {
        try {


            final Dialog dialog = new Dialog(PassengerDetailActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_yes_no);
            dialog.setCancelable(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            final TextView tvSeatCount = dialog.findViewById(R.id.tvSeatCount);
            ImageView btnSubtract = dialog.findViewById(R.id.btnSubtract);
            btnSubtract.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (seatCount>1){
                            seatCount=seatCount-1;
                            tvSeatCount.setText(String.valueOf(seatCount));
                        }
                    }catch (Exception e){
                        Log.e("error",e.toString());
                    }


                }
            });
            ImageView btnAdd = dialog.findViewById(R.id.btnAdd);
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        seatCount=seatCount +1;
                        tvSeatCount.setText(String.valueOf(seatCount));
                    }catch (Exception e){
                        Log.e("error", e.toString());
                    }

                }
            });

            TextView tvTitle = dialog.findViewById(R.id.title);
            TextView tvContent = dialog.findViewById(R.id.content);
            tvContent.setText(message);
            tvTitle.setText(title);
            ((Button) dialog.findViewById(R.id.bt_no)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Account owner is not travelling
                    processExcludingOwner(seatCount);
                    setAccountOwnerTravelling(false);
                    Toast.makeText(getApplicationContext(), ((AppCompatButton) v).getText().toString() + " Clicked", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            ((Button) dialog.findViewById(R.id.bt_yes)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    processIncludingOwner(seatCount);
                    Toast.makeText(getApplicationContext(), ((AppCompatButton) v).getText().toString() + " Clicked", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            Log.e("Dialog", e.toString());
        }
    }

    private void processExcludingOwner(int seatCount) {

        //Owner is not travelling
        int otherTickets = prefs.getInt(Constants.SEAT_COUNT, 0);
        int i = 0;

        while (i < seatCount) {
            PassengerInput pi = new PassengerInput();
            i++;
            View view1 = getLayoutInflater().inflate(R.layout.item_passenger1, mLlDynamicContent, false);
            TextView mTvSeatNo = view1.findViewById(R.id.tvSeatNo);
            final RelativeLayout mRlHeading = view1.findViewById(R.id.rlHeading);
            final RelativeLayout mRlSubHeading = view1.findViewById(R.id.rlSubHeading);
            final ImageView mIvIcon = view1.findViewById(R.id.ivIcon);
            etOtherPassengerFirstName = view1.findViewById(R.id.etOtherPassengerFirstName);
            pi.setEtFirstName(etOtherPassengerFirstName);
            etOtherPassengerLastName = view1.findViewById(R.id.etOtherPassengerLastName);
            pi.setEtLastName(etOtherPassengerLastName);
            etOtherPassengerPhone = view1.findViewById(R.id.etOtherPassengerPhone);
            pi.setEtPhone(etOtherPassengerPhone);
            etOtherPassengerEmail = view1.findViewById(R.id.etOtherPassengerEmail);
            pi.setEtEmail(etOtherPassengerEmail);

            passengerInputList.add(pi);
            mRlHeading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRlSubHeading.getVisibility() == View.VISIBLE) {

                        mIvIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black));

                        hideView(mRlSubHeading);
                    } else {
                        mIvIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black));
                        showView(mRlSubHeading);

                    }
                }
            });
            mTvSeatNo.setText("Passenger " + String.valueOf(i));
            mLlDynamicContent.addView(view1);
        }
        mBtnBook.setVisibility(View.VISIBLE);
    }

    private void processIncludingOwner(int seatCount) {
        llAccountOwnerDetails.setVisibility(View.VISIBLE);
        mBtnBook.setVisibility(View.VISIBLE);
        if (seatCount == 1) {
            //Owner is travelling alone
            etAccountOwnerNames.setText(AppController.getInstance().camelCase(accountOwnerFirstName) + " " + AppController.getInstance().camelCase(accountOwnerMiddleName) + " " + AppController.getInstance().camelCase(accountOwnerLastName));
            etAccountOwnerPhone.setText(accountOwnerPhone);
        } else if (seatCount > 1) {
            etAccountOwnerNames.setText(AppController.getInstance().camelCase(accountOwnerFirstName) + " " + AppController.getInstance().camelCase(accountOwnerMiddleName) + " " + AppController.getInstance().camelCase(accountOwnerLastName));
            etAccountOwnerPhone.setText(accountOwnerPhone);
            int seatsMinusAccountOwner = seatCount-1;
            int i = 0;
            while (i < seatsMinusAccountOwner) {
                PassengerInput pi = new PassengerInput();
                i++;
                View view1 = getLayoutInflater().inflate(R.layout.item_passenger1, mLlDynamicContent, false);
                TextView mTvSeatNo = view1.findViewById(R.id.tvSeatNo);
                final RelativeLayout mRlHeading = view1.findViewById(R.id.rlHeading);
                final RelativeLayout mRlSubHeading = view1.findViewById(R.id.rlSubHeading);
                final ImageView mIvIcon = view1.findViewById(R.id.ivIcon);
                etOtherPassengerFirstName = view1.findViewById(R.id.etOtherPassengerFirstName);
                pi.setEtFirstName(etOtherPassengerFirstName);
                etOtherPassengerLastName = view1.findViewById(R.id.etOtherPassengerLastName);
                pi.setEtLastName(etOtherPassengerLastName);
                etOtherPassengerPhone = view1.findViewById(R.id.etOtherPassengerPhone);
                pi.setEtPhone(etOtherPassengerPhone);
                etOtherPassengerEmail = view1.findViewById(R.id.etOtherPassengerEmail);
                pi.setEtEmail(etOtherPassengerEmail);


                passengerInputList.add(pi);
                mRlHeading.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mRlSubHeading.getVisibility() == View.VISIBLE) {

                            mIvIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_down_black));

                            hideView(mRlSubHeading);
                        } else {
                            mIvIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black));
                            showView(mRlSubHeading);

                        }
                    }
                });
                mTvSeatNo.setText("Passenger " + String.valueOf(i + 1));
                mLlDynamicContent.addView(view1);
            }
        }
    }


}

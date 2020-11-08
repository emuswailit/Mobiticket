package ke.co.mobiticket.mobiticket.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.GenerateOTPInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.ValidateOTPInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerGenerateOTPRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerValidateOTPRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.GenerateOTPResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerValidateOTPResponse;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VerificationActivity extends BaseActivity implements View.OnClickListener {

    /*variable declaration*/
    private EditText mEdDigit1, mEdDigit2, mEdDigit3, mEdDigit4;
    private LinearLayout mLlVerify;
    private TextView mTvResend, mTvTimer;
    private ImageView mIvBack;
    private EditText[] mEds;
    Gson gson=new Gson();
    String phone_number;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        initLayouts();
        initializeListeners();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            phone_number = extras.getString("phone_number");
            //The key argument here must match that used in the other activity
            Log.e("retrieved phone", phone_number);

            generateOTP(phone_number);
        }
    }

    /* init layout */
    private void initLayouts() {
        progressBar = findViewById(R.id.progressBar);
        mEdDigit1 = findViewById(R.id.edDigit1);
        mEdDigit2 = findViewById(R.id.edDigit2);
        mEdDigit3 = findViewById(R.id.edDigit3);
        mEdDigit4 = findViewById(R.id.edDigit4);
        mLlVerify = findViewById(R.id.llVerify);
        mTvResend = findViewById(R.id.tvResend);
        mTvTimer = findViewById(R.id.tvTimer);
        mEds = new EditText[]{mEdDigit1, mEdDigit2, mEdDigit3, mEdDigit4};

        mIvBack = findViewById(R.id.ivBack);
    }

    /* initialize listener */
    private void initializeListeners() {
        mIvBack.setOnClickListener(this);


        mEdDigit1.setOnKeyListener(new PinOnKeyListener(0));
        mEdDigit2.setOnKeyListener(new PinOnKeyListener(1));
        mEdDigit3.setOnKeyListener(new PinOnKeyListener(2));
        mEdDigit4.setOnKeyListener(new PinOnKeyListener(3));
        mEdDigit1.addTextChangedListener(new CodeTextWatcher(0));
        mEdDigit2.addTextChangedListener(new CodeTextWatcher(1));
        mEdDigit3.addTextChangedListener(new CodeTextWatcher(2));
        mEdDigit4.addTextChangedListener(new CodeTextWatcher(3));
        mLlVerify.setOnClickListener(this);

        new CountDownTimer(60000, 1000) { // adjust the milli seconds here

            @SuppressLint("DefaultLocale")
            public void onTick(long millisUntilFinished) {
                mTvTimer.setText(String.format("%d seconds left",
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                hideView(mTvTimer);
                showView(mTvResend);
            }
        }.start();

        mEdDigit4.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (validate()) {
                        //        if(validate()) {

                        //     }
                    }
                    return true;
                }
                return false;
            }

        });

    }

    /* onClick listener */
    @Override
    public void onClick(View v) {
        if (v == mIvBack) {
            onBackPressed();
        }
        if (v == mLlVerify) {
            //  if(validate()) {
            String digit1 =mEdDigit1.getText().toString();
            String digit2 =mEdDigit2.getText().toString();
            String digit3 =mEdDigit3.getText().toString();
            String digit4 =mEdDigit4.getText().toString();

            if (digit1.isEmpty()||digit1.equals("")||digit2.isEmpty()||digit2.equals("")||digit3.isEmpty()||digit3.equals("")||digit4.isEmpty()||digit4.equals("")){
                Toast.makeText(this, "Enter OTP correctly", Toast.LENGTH_SHORT).show();
            }else {
                String code=digit1+digit2+digit3+digit4;
                processVerifyCode(code);
            }


            // }
        }

    }

    /* Validation */
    private boolean validate() {
        boolean flag = true;
        if (TextUtils.isEmpty(mEdDigit1.getText())) {
            flag = false;
            showToast(getString(R.string.msg_code));
        } else if (TextUtils.isEmpty(mEdDigit2.getText())) {
            flag = false;
            showToast(getString(R.string.msg_code));
        } else if (TextUtils.isEmpty(mEdDigit3.getText())) {
            flag = false;
            showToast(getString(R.string.msg_code));
        } else if (TextUtils.isEmpty(mEdDigit4.getText())) {
            flag = false;
            showToast(getString(R.string.msg_code));
        }

        return flag;
    }

    /* back space key handler*/
    public class PinOnKeyListener implements View.OnKeyListener {

        private int mCurrentIndex;

        PinOnKeyListener(int currentIndex) {
            this.mCurrentIndex = currentIndex;
        }

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (mEds[mCurrentIndex].getText().toString().isEmpty() && mCurrentIndex != 0) {
                    mEds[mCurrentIndex - 1].requestFocus();

                }

            }
            return false;
        }

    }

    /* implement TextWatcher class*/
    public class CodeTextWatcher implements TextWatcher {

        private int mCurrentIndex;
        private boolean mIsFirst = false, mIsLast = false;
        private String mNewString = "";

        CodeTextWatcher(int currentIndex) {
            this.mCurrentIndex = currentIndex;

            if (currentIndex == 0)
                this.mIsFirst = true;
            else if (currentIndex == mEds.length - 1)
                this.mIsLast = true;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            mNewString = s.subSequence(start, start + count).toString().trim();
        }

        @Override
        public void afterTextChanged(Editable s) {

            String text = mNewString;

            if (text.length() > 1)
                text = String.valueOf(text.charAt(0));

            mEds[mCurrentIndex].removeTextChangedListener(this);
            mEds[mCurrentIndex].setText(text);
            mEds[mCurrentIndex].setSelection(text.length());
            mEds[mCurrentIndex].addTextChangedListener(this);

            if (text.length() == 1)
                moveToNext();
            else if (text.length() == 0)
                moveToPrevious();
        }

        private void moveToNext() {
            if (!mIsLast)
                mEds[mCurrentIndex + 1].requestFocus();

            if (isAllEditTextsFilled() && mIsLast) {
                mEds[mCurrentIndex].clearFocus();
                hideKeyboard();
            }
        }

        private void moveToPrevious() {
            if (!mIsFirst)
                mEds[mCurrentIndex - 1].requestFocus();
        }

        private boolean isAllEditTextsFilled() {
            for (EditText editText : mEds)
                if (editText.getText().toString().trim().length() == 0)
                    return false;
            return true;
        }

        private void hideKeyboard() {
            if (getCurrentFocus() != null) {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

        }
    }

    private void generateOTP(final String phone_number) {
        ServerGenerateOTPRequest request=new ServerGenerateOTPRequest();
        request.setAction(Constants.GENERATE_OTP_ACTION);
        request.setPhone_number(phone_number);
        progressBar.setVisibility(View.VISIBLE);
        Log.e("OTP request", gson.toJson(request));
        GenerateOTPInterface api = AppController.getInstance().getRetrofit().create(GenerateOTPInterface.class);
        Log.e("OTP api", gson.toJson(api));
        Call<GenerateOTPResponse> call=api.generateOTP(request);
        call.enqueue(new Callback<GenerateOTPResponse>() {
            @Override
            public void onResponse(Call<GenerateOTPResponse> call, Response<GenerateOTPResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body()==null){


                }else{
                    GenerateOTPResponse serverGenerateOTPResponse = response.body();
                    if (serverGenerateOTPResponse.getResponse_code().equals("0")){


                    }else {


                    }
                }
            }

            @Override
            public void onFailure(Call<GenerateOTPResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
//                String title = "One Time Pin Generation";
//                String message="An error occured while generating pin. System will retry again!";
//                showCustomDialog(title, message);
                Log.e("error", "error");
            }
        });

    }

    private void processVerifyCode(String message) {

        ServerValidateOTPRequest request=new ServerValidateOTPRequest();
        request.setAction(Constants.VALIDATE_OTP_ACTION);
        request.setPhone_number(phone_number);
        request.setOtp(message);

        progressBar.setVisibility(View.VISIBLE);
        ValidateOTPInterface api= AppController.getInstance().getRetrofit().create(ValidateOTPInterface.class);
        Call<ServerValidateOTPResponse> call=api.validateOTP(request);
        call.enqueue(new Callback<ServerValidateOTPResponse>() {
            @Override
            public void onResponse(Call<ServerValidateOTPResponse> call, Response<ServerValidateOTPResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body()==null){
                    Toast.makeText(VerificationActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
                }else {
                    ServerValidateOTPResponse serverValidateOTPResponse =response.body();
                    if (serverValidateOTPResponse.getResponse_code().equals("0")){
                        startActivity(new Intent(VerificationActivity.this, LoginActivity.class));

                    }else {
//                        Intent intent= new Intent();
//                        intent.putExtra("outcome","failure");
//                        intent.putExtra("message",serverValidateOTPResponse.getResponse_message());
//                        setResult(200, intent);
//                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerValidateOTPResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}

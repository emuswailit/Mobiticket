package ke.co.mobiticket.mobiticket.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.widget.AppCompatButton;

import com.google.gson.Gson;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.FinalizePasswordResetInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.LoginInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.InitiatePasswordResetInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.FinalizePasswordResetRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.InitiatePasswordResetRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerLoginRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.FinalizePasswordResetResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.InitiatePasswordResetResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerLoginResponse;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private ImageView ivBack;
    private TextView tvTitle, tvForgottenPassword;
    private Button btnLogin;
    private EditText etUsername, etPassword;
    private ProgressBar progressBar;
    private String phone_number = "";
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            phone_number = extras.getString("phone_number");
            //The key argument here must match that used in the other activity
            Log.e("retrieved phone", phone_number);


        }


        initLayouts();
        initializeListeners();
    }

    private void initializeListeners() {
        tvTitle.setOnClickListener(this);
        tvForgottenPassword.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    private void initLayouts() {
        tvForgottenPassword = findViewById(R.id.tvForgottenPassword);
        ivBack = findViewById(R.id.ivBack);
        tvTitle = findViewById(R.id.tvTitle);
        btnLogin = findViewById(R.id.btnLogin);
        etUsername = findViewById(R.id.etPhoneNumber);
        etUsername.setText(phone_number);
        etUsername.setFocusable(false);


        etPassword = findViewById(R.id.etPassword);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {
        if (v == tvTitle || v == ivBack) {
            startActivity(new Intent(this, SelectionActivity.class));
        }

        if (v == tvForgottenPassword) {
            if (phone_number.isEmpty() || phone_number.equals("")) {
                finish();
            } else {
                initiatePasswordReset1(phone_number);
            }

        }

        if (v == btnLogin) {
            if (etUsername.getText().toString().isEmpty() || etUsername.getText().toString().equals("")) {
                etUsername.setError("Enter email address or phone");
            } else if (etPassword.getText().toString().isEmpty() || etPassword.getText().toString().equals("")) {
                etPassword.setError("Enter your password");
            } else {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                if (AppController.getInstance().isNetworkConnected()) {
                    processLogin(username, password);
                } else {
                    startActivity(NoInternetActivity.class);
                }

            }
        }
    }

    private void initiatePasswordReset1(final String phone_number) {
        final Dialog dialog = new Dialog(LoginActivity.this);
        //Check if user has a jambopay suffixed default created email
        InitiatePasswordResetInterface api = AppController.getInstance().getRetrofit().create(InitiatePasswordResetInterface.class);
        InitiatePasswordResetRequest request = new InitiatePasswordResetRequest();
        request.setAction(Constants.ACTION_PASSWORD_RESET);
        request.setPhone_number(phone_number);

        showProgressDialog(dialog, "Retrieving details" + getResources().getString(R.string.txt_please_wait));
        Call<InitiatePasswordResetResponse> call = api.initiatePasswordReset(request);
        call.enqueue(new Callback<InitiatePasswordResetResponse>() {
            @Override
            public void onResponse(Call<InitiatePasswordResetResponse> call, Response<InitiatePasswordResetResponse> response) {
                dialog.dismiss();
                if (response.body() != null) {
                    if (response.body().getResponse_code().equals("0")) {


                        String phone = response.body().getPhone_number();

                        showDialogResetPassword("Password Reset", "Enter token sent to your phone and a new pin", phone);

                    } else {
//                        showCustomDialog("Pin Reset", response.body().getResponse_message());

                        Toast.makeText(LoginActivity.this, "Jambopay email does not exist", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    showCustomDialog("Password Reset", "A system error occurred. Please Try again");
                }
            }

            @Override
            public void onFailure(Call<InitiatePasswordResetResponse> call, Throwable t) {
                dialog.dismiss();
                showCustomDialog("Password Reset", "System error occurred. Please try again");

            }
        });

    }


    private void processLogin(String username, String password) {
        final Dialog dialog = new Dialog(LoginActivity.this);
        ServerLoginRequest request = new ServerLoginRequest();
        request.setAction(Constants.LOGIN_ACTION);
        request.setUsername(username);
        request.setPassword(password);
        Log.e("log in u", username);
        Log.e("log in p", password);
        showProgressDialog(dialog, "Logging in" + getResources().getString(R.string.txt_please_wait));

        try {
            LoginInterface api = AppController.getInstance().getRetrofit().create(LoginInterface.class);
            Call<ServerLoginResponse> call = api.loginUser(request);
            call.enqueue(new Callback<ServerLoginResponse>() {

                @Override
                public void onResponse(Call<ServerLoginResponse> call, Response<ServerLoginResponse> response) {
                    dialog.dismiss();

                    if (response.body() == null) {
                        Log.e("login ", gson.toJson(response.body()));
                        showCustomDialog("Log in", "A system error occurred. Please try again!");
                    } else {
                        ServerLoginResponse serverLoginResponse = response.body();

                        if (response.body().getResponse_code().equals("0")) {
                            //Login successful, redirect to Dashboard
                            Gson gson = new Gson();
                            String json_string = new Gson().toJson(response.body());
                            Log.e("whole json", json_string);
                            gson.fromJson(json_string, ServerLoginResponse.class);
                            AppController.getInstance().logInUser(serverLoginResponse, json_string);
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        } else if (response.body().getResponse_code().equals("20")) {
                            //User has not validated OTP, redirect to relevant activity, forwarding his phone number
                            String title = "Login Failed!";
                            String phone_number = serverLoginResponse.getPhone_number();
                            String message = response.body().getResponse_message();
                            showDialogValidateOTP(title, message, phone_number);
                        } else {
                            String title = "Log In!";
                            String message = response.body().getResponse_message();
                            showCustomDialog(title, message);
                        }

                    }
                }

                @Override
                public void onFailure(Call<ServerLoginResponse> call, Throwable t) {

                    showCustomDialog("Log In", "System error occurred!. Please try again!");
                    Log.e("KorrosError", t.getLocalizedMessage());
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            Log.e("LoginActivity", e.toString());
        }


    }

    private void showDialogValidateOTP(String title, String message, final String phone_number) {
        try {


            final Dialog dialog = new Dialog(LoginActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_validate_otp);
            dialog.setCancelable(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            TextView tvTitle = dialog.findViewById(R.id.title);
            TextView tvContent = dialog.findViewById(R.id.content);
            tvContent.setText(message);
            tvTitle.setText(title);
            ((Button) dialog.findViewById(R.id.bt_validate)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Redirect to OTP generation and verification
                    Intent intent = new Intent(LoginActivity.this, VerificationActivity.class);
                    intent.putExtra("phone_number", phone_number);
                    startActivity(intent);

                    //Kill the registration activity
                    finish();
                    dialog.dismiss();
                }
            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            Log.e("Dialog", e.toString());
        }
    }

    public void showCustomDialog(String title, String message, PaymentActivity paymentActivity) {
        try {


            final Dialog dialog = new Dialog(LoginActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_warning);
            dialog.setCancelable(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            TextView tvTitle = dialog.findViewById(R.id.title);
            TextView tvContent = dialog.findViewById(R.id.content);
            tvContent.setText(message);
            tvTitle.setText(title);
            ((Button) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            Log.e("Dialog", e.toString());
        }
    }

    public void showDialogResetPassword(String title, String message, final String phone) {
        try {


            final Dialog dialog = new Dialog(LoginActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_reset_password);
            dialog.setCancelable(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            TextView tvTitle = dialog.findViewById(R.id.title);
            TextView tvContent = dialog.findViewById(R.id.content);
            final EditText etCurrentPin = dialog.findViewById(R.id.etCurrentPin);
            final EditText etNewPin = dialog.findViewById(R.id.etNewPin);
            final EditText etConfirmNewPin = dialog.findViewById(R.id.etConfirmNewPin);
            final EditText etPassword = dialog.findViewById(R.id.etPassword);
            tvContent.setText(message);
            tvTitle.setText(title);
            ((ImageButton) dialog.findViewById(R.id.bt_exit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    finish();
                    dialog.dismiss();
                }
            });
            ((AppCompatButton) dialog.findViewById(R.id.bt_submit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String current_pin = etCurrentPin.getText().toString();
                    String new_pin = etNewPin.getText().toString();
                    String confirm_new_pin = etConfirmNewPin.getText().toString();

                    if (current_pin.isEmpty() || current_pin.equals("")) {

                        etCurrentPin.setError("Enter pin sent to your phone");

                    } else if (new_pin.isEmpty() || new_pin.equals("")) {
                        etNewPin.setError("Enter your new pin");

                    } else if (confirm_new_pin.isEmpty() || confirm_new_pin.equals("")) {
                        etConfirmNewPin.setError("Re-enter new pin to confirm");

                    } else if (!new_pin.equals(confirm_new_pin)) {
                        etNewPin.setError("Password confrimation not matching");
                        etConfirmNewPin.setError("Password confrimation not matching");
                        etNewPin.setText("");
                        etConfirmNewPin.setText("");

                    } else {
                        finalizePasswordReset(current_pin, new_pin, confirm_new_pin, phone);
                        dialog.dismiss();

                    }


                }
            });
            dialog.show();
            dialog.getWindow().setAttributes(lp);
        } catch (Exception e) {
            Log.e("Dialog", e.toString());
        }
    }

    private void finalizePasswordReset(String current_pin, String new_pin, String confirm_new_pin, String phone) {
        final Dialog dialog = new Dialog(LoginActivity.this);

        FinalizePasswordResetInterface api = AppController.getInstance().getRetrofit().create(FinalizePasswordResetInterface.class);
        FinalizePasswordResetRequest request = new FinalizePasswordResetRequest();
        request.setAction(Constants.ACTION_FINALIZE_PASSWORD_RESET);
        request.setPhone_number(phone_number);
        request.setCurrent_pin(current_pin);
        request.setNew_pin(new_pin);
        request.setConfirm_new_pin(confirm_new_pin);

        showProgressDialog(dialog, "Updating your password" + getResources().getString(R.string.txt_please_wait));
        Call<FinalizePasswordResetResponse> call = api.finalizePasswordReset(request);
        call.enqueue(new Callback<FinalizePasswordResetResponse>() {
            @Override
            public void onResponse(Call<FinalizePasswordResetResponse> call, Response<FinalizePasswordResetResponse> response) {
                dialog.dismiss();
                if (response.body() != null) {
                    if (response.body().getResponse_code().equals("0")) {
                        showCustomDialog("Password Reset", response.body().getResponse_message());
                    } else {
                        Toast.makeText(LoginActivity.this, response.body().getResponse_message(), Toast.LENGTH_SHORT).show();
                    }
//                    Log.e("success", gson.toJson(response.body()));
//
                } else {
                    showCustomDialog("Reset Password", "System error occurred. Please try again");
                }
            }

            @Override
            public void onFailure(Call<FinalizePasswordResetResponse> call, Throwable t) {
                showCustomDialog("Reset Password", "System error occurred. Please try again");
                dialog.dismiss();
            }
        });
    }


}

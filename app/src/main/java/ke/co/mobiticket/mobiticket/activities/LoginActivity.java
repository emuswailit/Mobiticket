package ke.co.mobiticket.mobiticket.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.LoginInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerLoginRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerLoginResponse;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView ivBack;
    private TextView tvTitle;
    private Button btnLogin;
    private EditText etUsername, etPassword;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initLayouts();
        initializeListeners();
    }

    private void initializeListeners() {
        tvTitle.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    private void initLayouts() {
        ivBack = findViewById(R.id.ivBack);
        tvTitle = findViewById(R.id.tvTitle);
        btnLogin = findViewById(R.id.btnLogin);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public void onClick(View v) {
        if (v == tvTitle || v == ivBack) {
            startActivity(new Intent(this, SelectionActivity.class));
        }

        if (v == btnLogin) {
            if (etUsername.getText().toString().isEmpty() || etUsername.getText().toString().equals("")) {
                etUsername.setError("Enter email address or phone");
            } else if (etPassword.getText().toString().isEmpty() || etPassword.getText().toString().equals("")) {
                etPassword.setError("Enter your password");
            } else {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                if (AppController.getInstance().isNetworkConnected()){
                    processLogin(username, password);
                }else{
                    String title="No internet connection!";
                    String message="Internet connection is required for this function!";
                    showCustomDialog(title,message);
                }

            }
        }
    }

    private void processLogin(String username, String password) {
        ServerLoginRequest request = new ServerLoginRequest();
        request.setAction(Constants.LOGIN_ACTION);
        request.setUsername(username);
        request.setPassword(password);
        progressBar.setVisibility(View.VISIBLE);

        try {
            LoginInterface api = AppController.getInstance().getRetrofit().create(LoginInterface.class);
            Call<ServerLoginResponse> call = api.loginUser(request);
            call.enqueue(new Callback<ServerLoginResponse>() {

                @Override
                public void onResponse(Call<ServerLoginResponse> call, Response<ServerLoginResponse> response) {
                    progressBar.setVisibility(View.GONE);

                    if (response.body() == null) {
                        Toast.makeText(LoginActivity.this, "Error occured while loging in!", Toast.LENGTH_SHORT).show();
                    } else {
                        ServerLoginResponse serverLoginResponse = response.body();
                        Toast.makeText(LoginActivity.this, response.body().getResponse_message(), Toast.LENGTH_SHORT).show();

                        if (response.body().getResponse_code().equals("0")) {
                            //Login successful, redirect to Dashboard
                            Gson gson=new Gson();
                            String json_string = new Gson().toJson(response.body());
                            Log.e("whole json", json_string);
                            gson.fromJson(json_string,ServerLoginResponse.class);
                            AppController.getInstance().logInUser(serverLoginResponse, json_string);
                            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                        }else if (response.body().getResponse_code().equals("20")){
                            //User has not validated OTP, redirect to relevant activity, forwarding his phone number
                            String title = "Login Failed!";
                            String phone_number=serverLoginResponse.getPhone_number();
                            String message = response.body().getResponse_message();
                            showDialogValidateOTP(title,message, phone_number);
                        }
                            else
                         {
                            String title = "Login Failed!";
                            String message = response.body().getResponse_message();
                            showCustomDialog(title, message);
                        }

                    }
                }

                @Override
                public void onFailure(Call<ServerLoginResponse> call, Throwable t) {
                    progressBar.setVisibility(View.VISIBLE);
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

    private void showCustomDialog(String title, String message) {
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
}

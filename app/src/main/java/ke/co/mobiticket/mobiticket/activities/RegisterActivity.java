package ke.co.mobiticket.mobiticket.activities;

import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.RegisterInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.VerifyIPRSInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerIPRSRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerRegisterRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerIPRSResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerRegisterResponse;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private EditText etYearOfBirth, etIdNumber;
    private ImageView ivBack;
    private Button btnSubmit1, btnSubmit2;
    private LinearLayout llFirstname, llMiddlename, llLastname, llGender, llPhoneNumber, llEmail, llPassword, llConfirmPassword;
    private EditText etFirstname, etMiddlename, etLastname, etGender, etPhoneNumber, etEmail, etPassword, etConfirmPassword;
    private ProgressBar progressBar;
    String year_of_birth = null;
    String id_number = null;
    String phone_number = null;
    String formatted_phone_number = null;
    String email_address = null;
    String password = null;
    String confirm_password = null;
    ServerIPRSResponse serverIPRSResponse = null;
    final Gson gson = new Gson();
    private Boolean retry=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        initLayouts();
        initializeListeners();
    }

    private void initializeListeners() {
        ivBack.setOnClickListener(this);
        btnSubmit1.setOnClickListener(this);
        btnSubmit2.setOnClickListener(this);
    }

    private void initLayouts() {
        etYearOfBirth = findViewById(R.id.etYearOfBirth);
        etIdNumber = findViewById(R.id.etIdNumber);
        ivBack = findViewById(R.id.ivBack);
        btnSubmit1 = findViewById(R.id.btnSubmit1);
        btnSubmit2 = findViewById(R.id.btnSubmit2);

        llFirstname = findViewById(R.id.llFirstname);
        llMiddlename = findViewById(R.id.llMiddlename);
        llLastname = findViewById(R.id.llLastname);
        llGender = findViewById(R.id.llGender);
        llPhoneNumber = findViewById(R.id.llPhoneNumber);
        llEmail = findViewById(R.id.llEmail);
        llPassword = findViewById(R.id.llPassword);
        llConfirmPassword = findViewById(R.id.llConfirmPassword);

        etMiddlename = findViewById(R.id.etMiddleName);
        etFirstname = findViewById(R.id.etFirstname);
        etLastname = findViewById(R.id.etLastname);
        etGender = findViewById(R.id.etGender);


        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);


        progressBar = findViewById(R.id.progressbar
        );
    }

    @Override
    public void onClick(View v) {
        if (v == ivBack) {
            //Go back one step
            startActivity(new Intent(this, SelectionActivity.class));
            finish();
        }

        if (v == btnSubmit1) {
            //Submit IPRS check data


            if (etIdNumber.getText().toString().isEmpty() || etIdNumber.getText().toString().equals("")) {
                etIdNumber.setError("National ID is required");
            } else if (etYearOfBirth.getText().toString().isEmpty() || etYearOfBirth.getText().toString().equals("")) {
                etYearOfBirth.setError("Year of birth is required");
            } else {
                year_of_birth = etYearOfBirth.getText().toString();
                id_number = etIdNumber.getText().toString();
//                startActivity(new Intent(RegisterActivity.this, VerificationActivity.class));

if (AppController.getInstance().isNetworkConnected()){
    processIPRSRequest(id_number, year_of_birth);
}else {

    showCustomDialog(Constants.NO_INTERNET_TITLE,Constants.NO_INTERNET_MESSAGE);
}
            }
        }

        if (v == btnSubmit2) {

            try {
                if (etPhoneNumber.getText().toString().isEmpty() || etPhoneNumber.getText().toString().equals("")) {
                    etPhoneNumber.setError("Enter phone number");
                } else if (etEmail.getText().toString().isEmpty() || etEmail.getText().toString().equals("")) {
                    etEmail.setError("Enter email address");
                } else if (etPassword.getText().toString().isEmpty() || etPassword.getText().toString().equals("")) {
                    etPassword.setError("Enter email address");
                } else if (etConfirmPassword.getText().toString().isEmpty() || etConfirmPassword.getText().toString().equals("")) {
                    etConfirmPassword.setError("Confirm your password");
                } else {

                    phone_number = etPhoneNumber.getText().toString();

                    formatted_phone_number = phone_number.replaceFirst("^0+(?!$)", "254");

                    Log.e("phone_number", phone_number);
                    Log.e("formatted_phone_number", formatted_phone_number);

                    email_address = etEmail.getText().toString();
                    Log.e("email_address", email_address);

                    password = etPassword.getText().toString();
                    Log.e("password", password);

                    confirm_password = etConfirmPassword.getText().toString();
                    Log.e("confirm_password", confirm_password);

                    if (password.equals(confirm_password)) {

                        ServerRegisterRequest request = new ServerRegisterRequest();
                        request.setAction(Constants.CREATE_ACTION);
                        request.setFirst_name(serverIPRSResponse.getFirst_name());
                        request.setMiddle_name(serverIPRSResponse.getMiddle_name());
                        request.setLast_name(serverIPRSResponse.getLast_name());
                        request.setDate_of_birth(serverIPRSResponse.getDate_of_birth());
                        request.setId_number(serverIPRSResponse.getId_number());
                        request.setPhone_number(phone_number);
                        request.setEmail_address(email_address);
                        request.setPassword(password);


                        if (AppController.getInstance().isNetworkConnected()){
                            processRegister(request, phone_number);
                        }else {
                            String title="No internet connection!";
                            String message="Internet connection is required for this function!";
                            showCustomDialog(title,message);
                        }



                    } else {
                        etPassword.setText("");
                        etConfirmPassword.setText("");
                        etPassword.setError("Password not matching");
                        etConfirmPassword.setError("Password not matching");
                    }
                }


            } catch (Exception e) {
                Log.e("Errors", e.toString());
            }
        }
    }



    private void processRegister(ServerRegisterRequest request, final String phone_number) {
final Dialog dialog=new Dialog(RegisterActivity.this);
        try {



            Log.e("request", gson.toJson(request));
            showProgressDialog(dialog,"Registering\n\nPlease wait.....");
            RegisterInterface api = AppController.getInstance().getRetrofit().create(RegisterInterface.class);
            Call<ServerRegisterResponse> call = api.registerUser(request);
            call.enqueue(new Callback<ServerRegisterResponse>() {
                @Override
                public void onResponse(Call<ServerRegisterResponse> call, Response<ServerRegisterResponse> response) {
                  dialog.dismiss();
                    if (response.body() == null) {
                        Toast.makeText(RegisterActivity.this, "Response is null", Toast.LENGTH_SHORT).show();
                    } else {
                        final Gson gson = new Gson();
                        Log.e("request response", gson.toJson(response.body()));


                        ServerRegisterResponse resp = response.body();
                        if (resp.getResponse_code().equals("0")) {
                            //Registration was succesfull so generate OTP

                            //Redirect to OTP generation and verification
                            Intent intent = new Intent(RegisterActivity.this, VerificationActivity.class);
                            intent.putExtra("phone_number", phone_number);
                            startActivity(intent);

                            //Kill the registration activity
                            finish();
                        } else {

                           //Registration failure, display dialog showing reason for failure
                            String title = "Registration Failure!";
                            String message = resp.getResponse_message();
                            showCustomDialog(title, message);
                        }
                    }

                }

                @Override
                public void onFailure(Call<ServerRegisterResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Log.e("register error", t.toString());
                }
            });

        } catch (Exception e) {
            Log.e("register err", e.toString());
            dialog.dismiss();
            showCustomDialog("Registration", "System error occurred.");
        }
    }



    private void processIPRSRequest(String id_number, String year_of_birth) {
final Dialog dialog=new Dialog(RegisterActivity.this);
        ServerIPRSRequest request = new ServerIPRSRequest();
        request.setAction(Constants.IPRS_ACTION);
        request.setYear_of_birth(year_of_birth);
        request.setId_number(id_number);

        try {
            VerifyIPRSInterface api = AppController.getInstance().getRetrofit().create(VerifyIPRSInterface.class);
            Call<ServerIPRSResponse> call = api.verifyIPRS(request);
            showProgressDialog(dialog, "Processing\n\nPlease wait....");
            call.enqueue(new Callback<ServerIPRSResponse>() {
                @Override
                public void onResponse(Call<ServerIPRSResponse> call, Response<ServerIPRSResponse> response) {
                   dialog.dismiss();
                    if (response.body() == null) {
                        Toast.makeText(RegisterActivity.this, "Null response", Toast.LENGTH_SHORT).show();
                    } else {
                        serverIPRSResponse = response.body();
                        if (serverIPRSResponse.getResponse_code().equals("0")) {
//                        Toast.makeText(RegisterActivity.this, "All is well, " + serverIPRSResponse.getFirst_name() + "! That's how government knows you!", Toast.LENGTH_SHORT).show();
                            llFirstname.setVisibility(View.VISIBLE);
                            llLastname.setVisibility(View.VISIBLE);
                            llGender.setVisibility(View.VISIBLE);
                            llPhoneNumber.setVisibility(View.VISIBLE);
                            llEmail.setVisibility(View.VISIBLE);
                            llPassword.setVisibility(View.VISIBLE);
                            llConfirmPassword.setVisibility(View.VISIBLE);


                            btnSubmit2.setVisibility(View.VISIBLE);
                            btnSubmit1.setVisibility(View.GONE);
                            etFirstname.setText(serverIPRSResponse.getFirst_name());
                            etLastname.setText(serverIPRSResponse.getLast_name());
                            etGender.setText(serverIPRSResponse.getGender());

                            //Make phone and email edit texts outstanding
                            etPhoneNumber.setError("Enter phone number");
                            etEmail.setError("Enter your email address");
                            etPassword.setError("Enter your password");
                            etConfirmPassword.setError("Confirm your password");


                            //Prevent editing ID Number and Year of Birth Fields
                            etYearOfBirth.setFocusable(false);
                            etIdNumber.setFocusable(false);

                            if (!serverIPRSResponse.getMiddle_name().equals("") || !serverIPRSResponse.getMiddle_name().isEmpty()) {
                                llMiddlename.setVisibility(View.VISIBLE);
                                etMiddlename.setText(serverIPRSResponse.getMiddle_name());
                            }
                        } else {
                            String message = serverIPRSResponse.getResponse_message();
                            String title = "Verification!";
                            showCustomDialog(title, message);
                            Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                        }

                    }

                }


                @Override
                public void onFailure(Call<ServerIPRSResponse> call, Throwable t) {
                    Log.e("Error response", t.toString());
                   dialog.dismiss();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public String replace(String str, int index, char replace){
        if(str==null){
            return str;
        }else if(index<0 || index>=str.length()){
            return str;
        }
        char[] chars = str.toCharArray();
        chars[index] = replace;
        return String.valueOf(chars);
    }

}

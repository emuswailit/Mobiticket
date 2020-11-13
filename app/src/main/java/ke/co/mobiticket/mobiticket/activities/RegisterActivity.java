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

import com.google.android.material.card.MaterialCardView;
import com.google.gson.Gson;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.RegisterInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.UserAvailabilityInterface;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.VerifyIPRSInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerIPRSRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerRegisterRequest;
import ke.co.mobiticket.mobiticket.retrofit.requests.UserAvailabilityRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerIPRSResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerRegisterResponse;
import ke.co.mobiticket.mobiticket.retrofit.responses.UserAvailabilityResponse;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    private EditText etYearOfBirth, etIdNumber;
    private ImageView ivBack;
    private Button btnSubmit1, btnSubmit2,btnRegister;
    private LinearLayout llFirstname, llMiddlename, llLastname, llGender, llPhoneNumber, llEmail, llPassword, llConfirmPassword;
    private EditText etFirstname, etMiddlename, etLastname, etGender, etPhoneNumber, etEmail, etPassword, etConfirmPassword, etUserPhone;
    private EditText etUserPhoneNumber,etUserMiddleName,etUserLastName,etUserNationalID,etUserEmail,etUserFirstname,etUserPassword,etUserConfirmPassword;
    private ProgressBar progressBar;
    private MaterialCardView  cardRegistrationDetails;
    String year_of_birth = null;
    String id_number = null;
String phone_number="";
    String email_address = null;
    String password = null;
    String confirm_password = null;
    ServerIPRSResponse serverIPRSResponse = null;
    final Gson gson = new Gson();
    private Boolean retry = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);
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
        ivBack.setOnClickListener(this);
        btnSubmit1.setOnClickListener(this);
        btnSubmit2.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
    }

    private void initLayouts() {

        cardRegistrationDetails = findViewById(R.id.cardRegistrationDetails);
        etYearOfBirth = findViewById(R.id.etYearOfBirth);
        etUserPhone = findViewById(R.id.etUserPhone);
        etIdNumber = findViewById(R.id.etIdNumber);
        ivBack = findViewById(R.id.ivBack);
        btnSubmit1 = findViewById(R.id.btnSubmit1);
        btnSubmit2 = findViewById(R.id.btnSubmit2);
        btnRegister = findViewById(R.id.btnRegister);

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
        etUserPhoneNumber = findViewById(R.id.etUserPhoneNumber);
etUserPhoneNumber.setText(phone_number);
etUserPhoneNumber.setFocusable(false);
        etUserEmail = findViewById(R.id.etUserEmail);
        etUserFirstname = findViewById(R.id.etUserFirstname);
        etUserMiddleName = findViewById(R.id.etUserMiddleName);
        etUserLastName = findViewById(R.id.etUserLastName);
        etUserNationalID = findViewById(R.id.etUserNationalID);
        etUserPassword = findViewById(R.id.etUserPassword);
        etUserConfirmPassword = findViewById(R.id.etUserConfirmPassword);


        progressBar = findViewById(R.id.progressbar
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnRegister:
                Log.e("dgfv","fbwDVXC");
etUserPhoneNumber.setText(phone_number);
etUserPhoneNumber.setFocusable(false);
      registerUser(phone_number);
                break;
        }
    }

    private void registerUser(final String phone_number) {
        String phone=      etUserPhoneNumber.getText().toString();
        String email=      etUserEmail.getText().toString();
        String first_name=      etUserFirstname.getText().toString();
        String middle_name=      etUserMiddleName.getText().toString();
        String last_name=      etUserLastName.getText().toString();
        String national_id=      etUserNationalID.getText().toString();
        String password=      etUserPassword.getText().toString();
        String confirm_password=      etUserConfirmPassword.getText().toString();

        if (phone.isEmpty()||phone.equals("")){
            etUserPhoneNumber.setError("Phone number is required");
            return;
        } else if (email.isEmpty()||email.equals("")){
            etUserEmail.setError("Email address is required");
            return;
        } else if (national_id.isEmpty()||national_id.equals("")){
            etUserNationalID.setError("National ID is required");
            return;
        }else if (first_name.isEmpty()||first_name.equals("")){
            etUserFirstname.setError("First name is required");
            return;
        }else if (last_name.isEmpty()||last_name.equals("")){
            etUserLastName.setError("Last name is required");
            return;
        }else if (password.isEmpty()||password.equals("")){
            etUserPassword.setError("Password is required");
            return;
        }else if (confirm_password.isEmpty()||confirm_password.equals("")){

            etUserConfirmPassword.setError("Please confirm password");
            return;
        }else{

            if (!password.equals(confirm_password)){
                etPassword.setError("Passwords do not match");
                etUserConfirmPassword.setError("Passwords do not match");
                return;
            }

            RegisterInterface api =AppController.getInstance().getRetrofit().create(RegisterInterface.class);
            final Dialog dialog= new Dialog(RegisterActivity.this);
            ServerRegisterRequest request=new ServerRegisterRequest();
            request.setPassword(password);
            request.setLast_name(last_name);
            request.setMiddle_name(middle_name);
            request.setFirst_name(first_name);
            request.setEmail_address(email);

            //Sort out 2547... vs 07..... issues
//            formatted_phone_number=phone.replaceFirst("^0+(?!$)", "254");
            request.setPhone_number(phone_number);
            request.setId_number(national_id);
            request.setAction(Constants.CREATE_ACTION);

         showProgressDialog(dialog, "Attempting registration"+ getResources().getString(R.string.txt_please_wait));
            Call<ServerRegisterResponse> call=api.registerUser(request);
            call.enqueue(new Callback<ServerRegisterResponse>() {
                @Override
                public void onResponse(Call<ServerRegisterResponse> call, Response<ServerRegisterResponse> response) {
                    dialog.dismiss();
                    if (response.body()!=null){
                        Log.e("response reg", gson.toJson(response.body()));
                        if (response.body().getResponse_code().equals("0")){
                            Log.e("success reg", response.body().getResponse_message());

                            Intent intent =new Intent(RegisterActivity.this, VerificationActivity.class);
                            intent.putExtra("phone_number", phone_number);
                            startActivity(intent);
                        }else {

                        showCustomDialog("Registration", response.body().getResponse_message());
                        }

                    }else {
                        showCustomDialog("Registration", "System error occurred. Please try again.");
                    }
                }

                @Override
                public void onFailure(Call<ServerRegisterResponse> call, Throwable t) {
dialog.dismiss();
                }
            });
        }
    }





    private void processRegister(ServerRegisterRequest request, final String phone_number) {
        final Dialog dialog = new Dialog(RegisterActivity.this);
        try {


            Log.e("request", gson.toJson(request));

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
//                            Intent intent = new Intent(RegisterActivity.this, VerificationActivity.class);
//                            intent.putExtra("phone_number", phone_number);
//                            startActivity(intent);

                            //Kill the registration activity
                            finish();
                        } else {

                            //Registration failure, display dialog showing reason for failure
                            String title = "Registration Failure!";
                            String message = resp.getResponse_message();
                            showCustomDialog(title, message);
                            Intent intent = new Intent(RegisterActivity.this, VerificationActivity.class);
                            intent.putExtra("phone_number", phone_number);
                            startActivity(intent);
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
        final Dialog dialog = new Dialog(RegisterActivity.this);
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


    public String replace(String str, int index, char replace) {
        if (str == null) {
            return str;
        } else if (index < 0 || index >= str.length()) {
            return str;
        }
        char[] chars = str.toCharArray();
        chars[index] = replace;
        return String.valueOf(chars);
    }


    public void showCustomYesNoDialog(String title, String message) {
        try {


            final Dialog dialog = new Dialog(RegisterActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_view_ticket_details);
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
                    finish();
                    dialog.dismiss();
                }
            });
            ((Button) dialog.findViewById(R.id.bt_yes)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(LoginActivity.class);
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

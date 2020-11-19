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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
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
    private Button btnRegister;
    private TextView tvHeader;

    private EditText etUserPhoneNumber,etUserMiddleName,etUserLastName,etUserNationalID,etUserEmail,etUserFirstname,etUserPassword,etUserConfirmPassword;

String phone_number="";
    final Gson gson = new Gson();
    private Boolean retry = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register1);
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
        btnRegister.setOnClickListener(this);
    }

    private void initLayouts() {

        tvHeader = findViewById(R.id.tvHeader);
        tvHeader.setText(phone_number+"\n\nNo details were retrieved for this  username. You can proceed to register by entering details below");
        ivBack = findViewById(R.id.ivBack);
        btnRegister = findViewById(R.id.btnRegister);


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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                finish();
                startActivity(SelectionActivity.class);
                finish();
                break;

            case R.id.btnRegister:
                Log.e("dgfv","fbwDVXC");
etUserPhoneNumber.setText(phone_number);

try {
    registerUser(phone_number);
}catch (Exception e){
    Log.e("register",e.toString());
}

                break;
        }
    }

    private void registerUser(final String phone_number) {

        String email=      etUserEmail.getText().toString();
        Log.e("email",email);
        String first_name=      etUserFirstname.getText().toString();
        Log.e("first_name",first_name);
        String middle_name=      etUserMiddleName.getText().toString();
        Log.e("middle_name",middle_name);
        String last_name=      etUserLastName.getText().toString();
        Log.e("last_name",last_name);
        String national_id=      etUserNationalID.getText().toString();
        Log.e("national_id",national_id);
        String password=      etUserPassword.getText().toString();
        Log.e("password",password);
        String confirm_password=      etUserConfirmPassword.getText().toString();







        Log.e("confirm_password",confirm_password);

        if (phone_number.isEmpty()||phone_number.equals("")){
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
                etUserPassword.setError("Passwords do not match");
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

                    Log.e("register error", t.toString());
                }
            });

        } catch (Exception e) {
            Log.e("register err", e.toString());
            dialog.dismiss();
            showCustomDialog("Registration", "System error occurred.");
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(SelectionActivity.class);
        finish();
    }
}

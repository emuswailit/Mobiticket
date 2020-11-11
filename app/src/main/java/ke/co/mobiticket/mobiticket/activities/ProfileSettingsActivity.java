package ke.co.mobiticket.mobiticket.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.UpdateUserInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.ServerUpdateUserRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.ServerUpdateUserResponse;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileSettingsActivity extends BaseActivity implements View.OnClickListener {

    /*variable declaration*/
    private ImageView mIvBack, mIvNotification,mIvAddProfile;
    private Button mBtnSave;
    private EditText etFirstName, etLastName, etEmail, etPhoneNumber, etMiddleName, etStreetAddress, etCity, etCountry, etPassword, etConfirmPassword;
    SharedPreferences prefs;

    private String email, street_address, city, country;
    private String password=null;
    Gson gson=new Gson();
    Dialog dialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);

        prefs= AppController.getInstance().getMobiPrefs();
        initLayouts();
        initializeListeners();

    }

    /* initialize listener */
    private void initializeListeners() {
        mIvBack.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);
        mIvNotification.setOnClickListener(this);
        mIvAddProfile.setOnClickListener(this);
        mBtnSave.setStateListAnimator(null);
    }

    /* init layout */
    private void initLayouts() {

        mIvBack = findViewById(R.id.ivBack);
        mBtnSave = findViewById(R.id.btnSave);
        etFirstName = findViewById(R.id.edFirstName);
        etFirstName.setText(AppController.getInstance().camelCase(prefs.getString(Constants.FIRST_NAME,"")));

        etMiddleName = findViewById(R.id.etMiddleName);
        etMiddleName.setText(AppController.getInstance().camelCase(prefs.getString(Constants.MIDDLE_NAME,"")));



        etLastName = findViewById(R.id.etLastName);
        etLastName.setText(AppController.getInstance().camelCase(prefs.getString(Constants.LAST_NAME,"")));


        etEmail = findViewById(R.id.etEmail);
        etEmail.setText(prefs.getString(Constants.EMAIL_ADDRESS,""));


        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etPhoneNumber.setText(prefs.getString(Constants.PHONE_NUMBER,""));


        mIvAddProfile = findViewById(R.id.ivAddProfile);
        mIvNotification = findViewById(R.id.ivNotification);


        //Residence
        etStreetAddress = findViewById(R.id.etStreetAddress);
        etStreetAddress.setText(prefs.getString(Constants.STREET_ADDRESS,""));


        etCity = findViewById(R.id.etCity);
        etCity.setText(prefs.getString(Constants.CITY,""));


        etCountry = findViewById(R.id.etCountry);
        etCountry.setText(prefs.getString(Constants.COUNTRY,""));

        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        if (etStreetAddress.getText().toString().isEmpty() || etStreetAddress.getText().toString().equals("")) {
            etStreetAddress.setError("Please update your residence street");
        } else if (etCity.getText().toString().isEmpty() || etCity.getText().toString().equals("")) {
            etCity.setError("Enter your town or city name");
        } else if (etCountry.getText().toString().isEmpty() || etCountry.getText().toString().equals("")) {
            etCountry.setError("name of your country");
        }



        SetNotificationImage(mIvNotification);
    }

    /* onClick listener */
    @Override
    public void onClick(View v) {
        if (v == mIvBack) onBackPressed();
        else if (v == mIvNotification) startActivity(NotificationActivity.class);
        else if (v == mBtnSave) {
            //  if (validate()) {
//            showToast(getString(R.string.msg_saved));

            ServerUpdateUserRequest request=new ServerUpdateUserRequest();
            request.setAction(Constants.UPDATE_USER_ACTION);
            request.setId(prefs.getString(Constants.ID,""));
            request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN,""));

            if (!etEmail.getText().equals("")   || etEmail.getText().toString().equals(prefs.getString(Constants.EMAIL_ADDRESS, ""))){
                email="";
            }else{
                email =etEmail.getText().toString();
                request.setEmail_address(email);
            }

            if (etStreetAddress.getText().equals("")   || etStreetAddress.getText().toString().equals(prefs.getString(Constants.STREET_ADDRESS, ""))){
                street_address="";
            }else{
                street_address =etStreetAddress.getText().toString();
                request.setStreet_address(street_address);
            }

            if (!etCountry.getText().equals("")   || !etCountry.getText().toString().equals(prefs.getString(Constants.COUNTRY, ""))){
                country="";
            }else {
                country =etCountry.getText().toString();
                request.setCountry(country);
            }

            if (etCity.getText().equals("")   || etCity.getText().toString().equals(prefs.getString(Constants.CITY, ""))){
                city="";
            }else{
                city =etCity.getText().toString();
                request.setCity(city);
            }

            if (etPassword.getText().toString().equals("")||etPassword.getText().toString().isEmpty()){
                //Do nothing about password
            }else {
                password=etPassword.getText().toString().trim();

                if (etConfirmPassword.getText().toString().isEmpty()||etConfirmPassword.getText().toString().equals("")){
                    etConfirmPassword.setError("Passwords must match!");
                    return;
                } else if (!etConfirmPassword.getText().toString().equals(etConfirmPassword.getText().toString())){
                    etConfirmPassword.setError("Passwords must match!");
                    return;
                }else {
                    password=etPassword.getText().toString();
                    request.setPassword(password);
                }


            }


go(request);
            Toast.makeText(this, "Readu for update", Toast.LENGTH_SHORT).show();

//            if (!etPassword.getText().equals("") && etPassword.getText().toString().trim().equals(etConfirmPassword.getText().toString().trim()) ){
//                password =etPassword.getText().toString();
//                request.setPassword(password);
//            }else {
//                password="";
//                etConfirmPassword.setError("Passwords must match!");
//                etPassword.setError("Passwords must match!");
//                etPassword.setText("");
//                etConfirmPassword.setText("");
//            }
//            if (password.equals("")&&city.equals("")&&country.equals("") && email.equals("")) {
//                Toast.makeText(this, "Nothing to update", Toast.LENGTH_SHORT).show();
//                Log.e("password A",password);
//                Log.e("city A",city);
//                Log.e("country A",country);
//                Log.e("email A",email);
//
//            }else{
////                try {
////                    go(request);
////                }catch (Exception e){
////                    Log.e("go() Error", e.toString());
////                }
//
//
//                Toast.makeText(this, "Something to update", Toast.LENGTH_SHORT).show();
//                Log.e("password B",password);
//                Log.e("city B",city);
//                Log.e("country B",country);
//                Log.e("email B",email);
//
//            }




            //  }
        }
    }

    private void go(ServerUpdateUserRequest request) {
        try {
            dialog = new Dialog(ProfileSettingsActivity.this);
            String message = "Updating profile\n\nPlease wait.....\n\n";
            showProgressDialog(dialog, message);
            UpdateUserInterface api = AppController.getInstance().getRetrofit().create(UpdateUserInterface.class);
            Call<ServerUpdateUserResponse> call = api.updateUser(request);
            call.enqueue(new Callback<ServerUpdateUserResponse>() {
                @Override
                public void onResponse(Call<ServerUpdateUserResponse> call, Response<ServerUpdateUserResponse> response) {

                    Log.e("error", gson.toJson(response.body()));
                    dialog.dismiss();
                    if (response.body() == null) {
                    showCustomDialog("Update profile", "A system error occurred. Please try again!");
                    } else {

                        if (response.body().getResponse_code().equals("0")) {
                            showToast("Please log in again!");
                            startActivity(LoginActivity.class);
                        } else {
                            String title = "Update profile";
                            String message = response.body().getResponse_message();
//                            showCustomDialog(title, message);
                        }
                        showCustomDialog("Profile Update", response.body().getResponse_message());

                    }

                }

                @Override
                public void onFailure(Call<ServerUpdateUserResponse> call, Throwable t) {
dialog.dismiss();
                    Log.e("error", t.getLocalizedMessage());
                }
            });
        } catch (Exception e) {

        }
    }

    /* validations */
    private boolean validate() {
        boolean flag = true;
        if (TextUtils.isEmpty(etFirstName.getText())) {
            flag = false;
            showToast(getString(R.string.msg_first_name));
        } else if (TextUtils.isEmpty(etLastName.getText())) {
            flag = false;
            showToast(getString(R.string.msg_last_name));
        } else if (TextUtils.isEmpty(etEmail.getText())) {
            flag = false;
            showToast(getString(R.string.msg_email_required));
        } else if (TextUtils.isEmpty(etPhoneNumber.getText())) {
            flag = false;
            showToast(getString(R.string.msg_mobile_number));
        }

        return flag;
    }


}


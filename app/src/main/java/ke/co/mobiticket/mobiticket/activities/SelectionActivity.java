package ke.co.mobiticket.mobiticket.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.adapters.LazyAdapterCountryCodes;
import ke.co.mobiticket.mobiticket.pojos.Country;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.UserAvailabilityInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.UserAvailabilityRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.UserAvailabilityResponse;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectionActivity extends BaseActivity implements View.OnClickListener {
    private Button btnGo;
    Spinner spCountryCode;
    EditText etUserPhone;
    ImageView btnNext;
    SharedPreferences prefs;
    List<Country> countryList=new ArrayList<>();
    private String code="";
    private String retrieved_phone_number="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        Country kenya=new Country("Kenya", "254");
        countryList.add(kenya);
        Country uganda=new Country("Uganda", "256");
        countryList.add(uganda);

        prefs= AppController.getInstance().getMobiPrefs();

        if (prefs.getString(Constants.PHONE_NUMBER,"").isEmpty() && prefs.getString(Constants.EMAIL_ADDRESS,"").isEmpty() ){

        }else {

            startActivity(DashboardActivity.class);
        }
        initLayouts();
        initializeListeners();
    }

    private void initializeListeners() {

        LazyAdapterCountryCodes adapterCountryCodes=new LazyAdapterCountryCodes(this, countryList);
        adapterCountryCodes.notifyDataSetChanged();
        spCountryCode.setAdapter(adapterCountryCodes);
        spCountryCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                code = countryList.get(position).getCode();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        btnNext.setOnClickListener(this);
//        btnLogin.setStateListAnimator(null);
//        btnRegister.setOnClickListener(this);
//        btnRegister.setStateListAnimator(null);
    }

    private void initLayouts() {
        spCountryCode = findViewById(R.id.spCountryCode);
        etUserPhone = findViewById(R.id.etUserPhone);
        btnNext = findViewById(R.id.btnNext);
    }


    @Override
    public void onClick(View v) {
switch (v.getId()){
    case R.id.btnNext:
        String phone_number =etUserPhone.getText().toString();

        if (phone_number.equals("")||phone_number.isEmpty()){
            Toast.makeText(this, "Please enter phone number to continue", Toast.LENGTH_SHORT).show();
            return;

        }else {
            phone_number=phone_number.replaceFirst ("^0*", "");
        }

        if (!code.isEmpty()||!code.equals("")){
            phone_number=code+phone_number;
        }

        //Number formatting for Kenya numbers
        if (code.equals("254")&& phone_number.length()!=12){

            if (phone_number.length()>12){
                Toast.makeText(this, "Number you entered is more than 12 digits", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Number you entered is less than 12 digits", Toast.LENGTH_SHORT).show();
            }

            return;
        }
        Toast.makeText(this, phone_number, Toast.LENGTH_SHORT).show();
        if (AppController.getInstance().isNetworkConnected()) {
            checkIfUserExists(phone_number);
        }else {
            startActivity(NoInternetActivity.class);
        }
        break;
}
    }

    private void checkIfUserExists(final String entered_phone_number) {
        final Dialog dialog = new Dialog(SelectionActivity.this);
        showProgressDialog(dialog, "Checking if you are already registered" + getResources().getString(R.string.txt_please_wait));
        UserAvailabilityInterface api = AppController.getInstance().getRetrofit().create(UserAvailabilityInterface.class);
        UserAvailabilityRequest request = new UserAvailabilityRequest();
        request.setAction(Constants.CHECK_USER_AVAILABILITY);
        request.setPhone_number(entered_phone_number);
        Call<UserAvailabilityResponse> call = api.checkUserExists(request);
        showProgressDialog(dialog, "Attempting registration" + getResources().getString(R.string.txt_please_wait));
        call.enqueue(new Callback<UserAvailabilityResponse>() {
            @Override
            public void onResponse(Call<UserAvailabilityResponse> call, Response<UserAvailabilityResponse> response) {
                dialog.dismiss();
                if (response.body() != null) {
                    if (response.body().getResponse_code().equals("0")) {

                        if (response.body().getExists()) {
                            retrieved_phone_number=response.body().getPhone_number();
                            if (!retrieved_phone_number.isEmpty()|| !retrieved_phone_number.equals("")){
                                Intent intent=new Intent(SelectionActivity.this, LoginActivity.class);
                                intent.putExtra("phone_number", retrieved_phone_number);
                                startActivity(intent);
                            }else {

                            }

//                            showCustomYesNoDialog("Registration", "You are already registered with Jambopay. Would you like to opt into our Mobiticket Cashless Fare Solution?");
                        } else {
                            //Use does not exist, go to register
                            Intent intent=new Intent(SelectionActivity.this, RegisterActivity.class);
                            intent.putExtra("phone_number", entered_phone_number);
                            startActivity(intent);
                            finish();
                        }

                    } else {
                        //Use does not exist, go to register
                        Intent intent=new Intent(SelectionActivity.this, RegisterActivity.class);
                        intent.putExtra("phone_number", entered_phone_number);
                        startActivity(intent);
                        finish();
                    }

                } else {
                    showCustomDialog("Checking User Availability", "An error occurred!");
                }
            }

            @Override
            public void onFailure(Call<UserAvailabilityResponse> call, Throwable t) {
                dialog.dismiss();
            }
        });


    }
}

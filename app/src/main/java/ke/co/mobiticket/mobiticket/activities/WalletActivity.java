package ke.co.mobiticket.mobiticket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class WalletActivity extends BaseActivity implements View.OnClickListener {
private ImageView ivBack;
private TextView tvBalance, tvTitle;
private SharedPreferences prefs;
Gson gson=new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        initLayouts();
        initListeners();
        prefs= AppController.getInstance().getMobiPrefs();
    }

    private void initListeners() {
        ivBack.setOnClickListener(this);
        tvBalance.setOnClickListener(this);
    }

    private void initLayouts() {
        tvTitle=findViewById(R.id.tvTitle);
        tvTitle.setText("My Wallet");
        ivBack=findViewById(R.id.ivBack);

        tvBalance=findViewById(R.id.tvBalance);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivBack:
                startActivity(MoreActivity.class);
                finish();
                break;

            case R.id.tvBalance:
                String phone_number=prefs.getString(Constants.PHONE_NUMBER,"");
                if (!phone_number.isEmpty()||phone_number.equals("")){
                    showDialogLogin("Show Balance", "Enter your password to continue", phone_number);
                }
                break;

            default:
                break;
        }
    }

    private void showDialogLogin(String title, String message, final String phone_number) {
        Toast.makeText(this, "Enter your email..", Toast.LENGTH_SHORT).show();

        try {


            final Dialog dialog = new Dialog(WalletActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_enter_password);
            dialog.setCancelable(false);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            TextView tvTitle = dialog.findViewById(R.id.title);
            final EditText etPassword = dialog.findViewById(R.id.etPassword);

            tvTitle.setText(title);
            ((Button) dialog.findViewById(R.id.bt_submit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String password=etPassword.getText().toString();
                    if (password.isEmpty()||password.equals("")){
                        Toast.makeText(WalletActivity.this, "Enter email address", Toast.LENGTH_SHORT).show();
                    }else {
processLogin(phone_number,password);
                    }
                    dialog.dismiss();
                }
            });

            ((ImageButton) dialog.findViewById(R.id.bt_exit)).setOnClickListener(new View.OnClickListener() {
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

    private void processLogin(String username, String password) {
        final Dialog dialog = new Dialog(WalletActivity.this);
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

                            tvBalance.setText("Jambopay Wallet Balance: \nKES "+ serverLoginResponse.getWallet_balance());
                            ServerLoginResponse resp=response.body();

                            //Update shared preferences
                            AppController.getInstance().logInUser(serverLoginResponse,gson.toJson(resp));

                        }  else {
                            String title = "Log In!";
                            String message = response.body().getResponse_message();
                            showCustomDialog(title, message);
                        }

                    }
                }

                @Override
                public void onFailure(Call<ServerLoginResponse> call, Throwable t) {
                    dialog.dismiss();
                }
            });
        } catch (Exception e) {
            Log.e("LoginActivity", e.toString());
        }


    }
}

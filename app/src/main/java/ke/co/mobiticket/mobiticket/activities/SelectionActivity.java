package ke.co.mobiticket.mobiticket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ke.co.mobiticket.mobiticket.R;

public class SelectionActivity extends AppCompatActivity implements View.OnClickListener {
    private Button btnLogin, btnRegister;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        initLayouts();
        initializeListeners();
    }

    private void initializeListeners() {
        btnLogin.setOnClickListener(this);
        btnLogin.setStateListAnimator(null);
        btnRegister.setOnClickListener(this);
        btnRegister.setStateListAnimator(null);
    }

    private void initLayouts() {
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
    }


    @Override
    public void onClick(View v) {
        if (v==btnLogin){
            startActivity(new Intent(SelectionActivity.this, LoginActivity.class));
        }

        if (v==btnRegister){
            startActivity(new Intent(SelectionActivity.this, RegisterActivity.class));
        }
    }
}

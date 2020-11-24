package ke.co.mobiticket.mobiticket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.utilities.AppController;

public class SplashActivity extends AppCompatActivity {
    private ImageView mIVLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        initLayouts();
        initializeListeners();

    }

    /* initialize listener */
    private void initializeListeners() {
        Glide.with(this).load(R.raw.ic_logo).into(mIVLogo);

        new Handler().postDelayed(new Runnable() {
                                      @Override
                                      public void run() {
                                          startActivity(new Intent(SplashActivity.this, SelectionActivity.class));
                                          onBackPressed();
                                      }
                                  },
                3000);
    }

    /* init layout */
    /* init layout */
    private void initLayouts() {

        mIVLogo = findViewById(R.id.ivLogo);

    }
}
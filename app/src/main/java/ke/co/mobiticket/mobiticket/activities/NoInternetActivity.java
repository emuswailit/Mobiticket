package ke.co.mobiticket.mobiticket.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Tools;

public class NoInternetActivity extends AppCompatActivity {
    private ProgressBar progress_bar;
    private LinearLayout lyt_no_connection;
    private AppCompatButton bt_retry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);

        initComponent();
        Tools.setSystemBarColor(this, android.R.color.white);
        Tools.setSystemBarLight(this);
    }


    private void initComponent() {
        progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
        lyt_no_connection = (LinearLayout) findViewById(R.id.lyt_no_connection);
        bt_retry = (AppCompatButton) findViewById(R.id.bt_retry);

        progress_bar.setVisibility(View.GONE);
        lyt_no_connection.setVisibility(View.VISIBLE);

        bt_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                progress_bar.setVisibility(View.VISIBLE);
                lyt_no_connection.setVisibility(View.GONE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progress_bar.setVisibility(View.GONE);
                        lyt_no_connection.setVisibility(View.VISIBLE);
                        if (AppController.getInstance().isNetworkConnected()){
                            finish();
                        }
                    }
                }, 1000);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (AppController.getInstance().isNetworkConnected()){
            finish();
        }else {

        }
    }
}

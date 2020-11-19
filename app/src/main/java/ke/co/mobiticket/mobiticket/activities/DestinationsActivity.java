package ke.co.mobiticket.mobiticket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ke.co.mobiticket.mobiticket.R;

public class DestinationsActivity extends BaseActivity implements View.OnClickListener {
    private TextView tvTitle;
    private ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destinations);
        initLayouts();
        initListeners();
    }

    private void initListeners() {
        ivBack.setOnClickListener(this);
    }

    private void initLayouts() {
        ivBack=findViewById(R.id.ivBack);
        tvTitle=findViewById(R.id.tvTitle);
        tvTitle.setText("Buses and Stops");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivBack:
                finish();
                break;
        }
    }
}

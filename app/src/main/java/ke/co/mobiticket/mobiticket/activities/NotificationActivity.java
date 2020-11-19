package ke.co.mobiticket.mobiticket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ke.co.mobiticket.mobiticket.R;

public class NotificationActivity extends BaseActivity implements View.OnClickListener {
private TextView tvTitle;
private ImageView ivBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        initLayouts();
        setListeners();

        //Forward
        String url = "https://api.whatsapp.com/send?phone="+"254709338000"+"&text="+"Jambo";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void setListeners() {
        ivBack.setOnClickListener(this);
    }

    private void initLayouts() {
        ivBack=findViewById(R.id.ivBack);
        tvTitle=findViewById(R.id.tvTitle);
        tvTitle.setText("Notifications");
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

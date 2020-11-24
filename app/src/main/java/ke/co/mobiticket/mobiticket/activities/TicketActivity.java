package ke.co.mobiticket.mobiticket.activities;

        import androidx.appcompat.app.AppCompatActivity;

        import android.content.Intent;
        import android.os.Bundle;
        import android.util.Log;

        import ke.co.mobiticket.mobiticket.R;

public class TicketActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        Intent intent= getIntent();
        String ticket_data=intent.getStringExtra("ticket_data");
        Log.e("ticket_data",ticket_data);
    }
}

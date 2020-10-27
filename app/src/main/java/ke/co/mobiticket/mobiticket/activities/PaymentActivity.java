package ke.co.mobiticket.mobiticket.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;

public class PaymentActivity extends AppCompatActivity {
    SharedPreferences prefs;
    private String reference_number="";
    private String payment_method_id="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        prefs= AppController.getInstance().getMobiPrefs();

         reference_number=prefs.getString(Constants.TICKET_REFERENCE_NUMBER,"");
         payment_method_id=prefs.getString(Constants.TICKET_PAYMENT_METHOD_ID,"");
        Toast.makeText(this, payment_method_id, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, reference_number, Toast.LENGTH_SHORT).show();
    }
}

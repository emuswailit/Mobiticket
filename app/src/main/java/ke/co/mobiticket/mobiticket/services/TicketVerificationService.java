package ke.co.mobiticket.mobiticket.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.annotation.Nullable;

import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class TicketVerificationService extends IntentService {

    private SharedPreferences prefs;
    public TicketVerificationService() {
        super("TicketVerificationService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId){
         Toast.makeText(this, "Sync service initiated", Toast.LENGTH_SHORT).show();
        prefs = AppController.getInstance().getMobiPrefs();
        Toast.makeText(this, prefs.getString(Constants.TICKET_REFERENCE_NUMBER,""), Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flag, startId);
    }

    @Override
    public void onDestroy(){
        // Toast.makeText(this, "Sync service concluded", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}



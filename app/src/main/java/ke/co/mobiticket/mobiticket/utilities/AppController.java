package ke.co.mobiticket.mobiticket.utilities;



import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;


import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ke.co.mobiticket.mobiticket.retrofit.responses.ServerLoginResponse;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppController extends Application {

    public static AppController mInstance;
    public boolean isLoggedIn;

    private SharedPreferences prefs;
    String current_date_time;
    public static synchronized AppController getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        prefs = getSharedPreferences(Constants.SHARED_PREFRENCES, Context.MODE_PRIVATE);
        mInstance = this;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH);
        final Date date1 = new Date();
        current_date_time = dateFormat.format(date1);
    }


    //Centralize SharedPreferences declaration
    public SharedPreferences getMobiPrefs() {
        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFRENCES, Context.MODE_PRIVATE);
        return prefs;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public boolean isLoggedIn() {

        if (prefs.getBoolean(Constants.IS_LOGGED_IN, false)) {

            return true;
        } else {
            Toast.makeText(getApplicationContext(), "Please log in again", Toast.LENGTH_LONG).show();
            return false;
        }


    }


    public void logOutUser() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.IS_LOGGED_IN,true);
        editor.putString(Constants.ACCESS_TOKEN,null);
        editor.putString(Constants.EMAIL_ADDRESS,null);
        editor.putString(Constants.PHONE_NUMBER,null);
        editor.putString(Constants.FIRST_NAME,null);
        editor.putString(Constants.MIDDLE_NAME,null);
        editor.putString(Constants.LAST_NAME,null);
        editor.putString(Constants.ID,null);
        editor.putString(Constants.ID_NUMBER,null);
        editor.putString(Constants.DATE_OF_BIRTH,null);
        editor.putString(Constants.STREET_ADDRESS,null);
        editor.putString(Constants.CITY,null);
        editor.putString(Constants.COUNTRY,null);
        editor.putString(Constants.STATUS,null);
        editor.putString(Constants.DRIVE,null);
        editor.putString(Constants.CONDUCT,null);
        editor.putString(Constants.ENTIRE_RESPONSE,null);

        editor.apply();

    }

    public void logInUser(ServerLoginResponse serverLoginResponse, String json_string) {
              Gson gson=new Gson();
//        ServerLoginResponse serverLoginResponse= gson.fromJson(json_response,ServerLoginResponse.class);

//        Log.e("request", gson.toJson(request));
Log.e("json_string",json_string);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(Constants.IS_LOGGED_IN,true);
        editor.putString(Constants.ACCESS_TOKEN,serverLoginResponse.getAccess_token());
        editor.putString(Constants.EMAIL_ADDRESS,serverLoginResponse.getEmail_address());
        editor.putString(Constants.PHONE_NUMBER,serverLoginResponse.getPhone_number());
        editor.putString(Constants.FIRST_NAME,serverLoginResponse.getFirst_name());
        editor.putString(Constants.MIDDLE_NAME,serverLoginResponse.getMiddle_name());
        editor.putString(Constants.LAST_NAME,serverLoginResponse.getLast_name());
        editor.putString(Constants.ID,serverLoginResponse.getId());
        editor.putString(Constants.ID_NUMBER,serverLoginResponse.getId_number());
        editor.putString(Constants.DATE_OF_BIRTH,serverLoginResponse.getDate_of_birth());
        editor.putString(Constants.STREET_ADDRESS,serverLoginResponse.getStreet_address());
        editor.putString(Constants.CITY,serverLoginResponse.getCity());
        editor.putString(Constants.COUNTRY,serverLoginResponse.getCountry());
        editor.putString(Constants.STATUS,serverLoginResponse.getStatus());
        editor.putString(Constants.DRIVE,gson.toJson(serverLoginResponse.getDrive()));
        editor.putString(Constants.OWNED,gson.toJson(serverLoginResponse.getOwned()));
        editor.putString(Constants.CONDUCT,gson.toJson(serverLoginResponse.getConduct()));
        editor.putString(Constants.ENTIRE_RESPONSE,json_string);
        editor.apply();
    }

    public Retrofit getRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return  retrofit;
    }


    public static String camelCase(String in) {
        if (in == null || in.length() < 1) {
            return "";
        } //validate in
        String out = "";
        for (String part : in.toLowerCase().split("_")) {
            if (part.length() < 1) { //validate length
                continue;
            }
            out += part.substring(0, 1).toUpperCase();
            if (part.length() > 1) { //validate length
                out += part.substring(1);
            }
        }
        return out;
    }



}

package ke.co.mobiticket.mobiticket.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

import ke.co.mobiticket.mobiticket.R;
import ke.co.mobiticket.mobiticket.retrofit.interfaces.WriteCardInterface;
import ke.co.mobiticket.mobiticket.retrofit.requests.WriteCardRequest;
import ke.co.mobiticket.mobiticket.retrofit.responses.WriteCardResponse;
import ke.co.mobiticket.mobiticket.utilities.AppController;
import ke.co.mobiticket.mobiticket.utilities.Constants;
import ke.co.mobiticket.mobiticket.utilities.Cryptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NFCActivity extends BaseActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private EditText etJambopayWalletUsername,etJambopayWalletPassword;
    private Spinner spCardType;
    String[] card_types = { "regularcommuter", "crew", };
    private Button btnSubmit;
    private ImageView ivBack;
    String card_type="";
    SharedPreferences prefs;
    private NfcAdapter mNfcAdapter=null;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    ProgressBar progressBar;
    private String card_data="";
    private static String publicKey = "";
    private static String privateKey = "";
    private String  wallet_username;
    private String  wallet_password;
    Gson gson=new Gson();
    private static final Charset UTF_8 = StandardCharsets.UTF_8;
    private static final int AES_KEY_BIT = 256;
    private static final int IV_LENGTH_BYTE = 12;
    private static final String ENCRYPT_ALGO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    String OUTPUT_FORMAT = "%-30s:%s";
    byte[] encryptedText =null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_n_f_c);
prefs=AppController.getInstance().getMobiPrefs();
        initLayouts();
        initListeners();

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter==null){
            //No NFC in device or NFC could not be initialized
            Toast.makeText(this, "Device not NFC enabled!", Toast.LENGTH_SHORT).show();
            //Close the activity
            finish();
        }else {
            Toast.makeText(this, "NFC successfully initialized!", Toast.LENGTH_SHORT).show();
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
            mPendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
            IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            //ndef.addDataScheme("http");
            mFilters = new IntentFilter[] {
                    ndef,
            };
            mTechLists = new String[][] { new String[] { Ndef.class.getName() },
                    new String[] { NdefFormatable.class.getName() }};


        }
    }



    private void initLayouts() {
         spCardType =  findViewById(R.id.spCardType);
         ivBack =  findViewById(R.id.ivBack);
         etJambopayWalletUsername =  findViewById(R.id.etJambopayWalletUsername);
         etJambopayWalletPassword =  findViewById(R.id.etJambopayWalletPassword);
         btnSubmit =  findViewById(R.id.btnSubmit);
         progressBar =  findViewById(R.id.progressBar);

    }

    private void initListeners() {
        spCardType.setOnItemSelectedListener(this);
//Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,card_types);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spCardType.setAdapter(aa);

        btnSubmit.setOnClickListener(this);
        ivBack.setOnClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        card_type=card_types[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivBack:
                finish();
                break;
            case R.id.btnSubmit:
                if (AppController.getInstance().isNetworkConnected()){
                    writeNFCCard();
                }
                break;
        }
    }

    private void writeNFCCard() {

        wallet_username= etJambopayWalletUsername.getText().toString();
        wallet_password= etJambopayWalletPassword.getText().toString();
        if (wallet_username.isEmpty()||wallet_username.equals("")){
            Toast.makeText(this, "Enter your username", Toast.LENGTH_SHORT).show();
        }else if (wallet_password.isEmpty()||wallet_password.equals("")){
            Toast.makeText(this, "Enter your password", Toast.LENGTH_SHORT).show();
        }else if (card_type.isEmpty()||card_type.equals("")){
            Toast.makeText(this, "Please select card type", Toast.LENGTH_SHORT).show();
        }else {
            writecard(wallet_username,wallet_password,card_type);
        }

    }

    private void writecard(String jambopay_wallet_username, String jambopay_wallet_password, String card_type) {
        WriteCardInterface api=AppController.getInstance().getRetrofit().create(WriteCardInterface.class);
        WriteCardRequest request=new WriteCardRequest();
        request.setAccess_token(prefs.getString(Constants.ACCESS_TOKEN,""));
        request.setAction(Constants.WRITE_CARD_ACTION);
        request.setCard_type(card_type);
        request.setWallet_password(jambopay_wallet_password);
        request.setWallet_username(jambopay_wallet_username);

        progressBar.setVisibility(View.VISIBLE);
        Call<WriteCardResponse> call=api.writeCard(request);
        call.enqueue(new Callback<WriteCardResponse>() {
            @Override
            public void onResponse(Call<WriteCardResponse> call, Response<WriteCardResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.body()!=null){
                    Gson gson=new Gson();
                    Toast.makeText(NFCActivity.this, response.body().getResponse_message(), Toast.LENGTH_SHORT).show();
                    Log.e("write card", gson.toJson(response.body()));
                    if (response.body().getResponse_code().equals("0")){
                    card_data=response.body().getCard_data();
                    }else {
                        showCustomDialog("Write NFC card!",response.body().getResponse_message());
                    }
                    showCustomDialog("Please tap your card to write!", response.body().getResponse_message());

                }else {
                    Toast.makeText(NFCActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WriteCardResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNfcAdapter != null) mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                mTechLists);
    }

    @Override
    public void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Toast.makeText(this, "Read....", Toast.LENGTH_SHORT).show();
        Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);


        if (card_data.equals("")) {
            Toast.makeText(getApplicationContext(), "No processed card data available for writing", Toast.LENGTH_LONG).show();
            return;
        } else {


            try {
                Cryptor cryptor=new Cryptor();
                cryptor.setIv();

                String encryptedText=cryptor.encryptText(card_data);
                prefs.edit().putString("encryptedKey",encryptedText ).apply();
                prefs.edit().putString("keyIv", cryptor.getIv_string()).apply();
Log.e("encryptedText",encryptedText);



            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }



            Toast.makeText(getApplicationContext(), "Data ready for writing", Toast.LENGTH_LONG).show();
            String externalType = "nfclab.com:mobima3";
             String payload = encryptedText+":"+":"+card_type;
//            String payload = account_phone_number + ":" + card_name + ":" + card_data + ":" + current_balance;
//            Log.e("payload", payload);
            // String payload = card_data;
            //String payload = card_data;
            NdefRecord extRecord1 = new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE, externalType.getBytes(), new byte[0], payload.getBytes());
            NdefMessage newMessage = new NdefMessage(new NdefRecord[]{extRecord1});
            writeNdefMessageToTag(newMessage, tag);
        }

    }
    public static String decrypt(byte[] cText, SecretKey secret, byte[] iv) throws Exception {

        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        cipher.init(Cipher.DECRYPT_MODE, secret, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        byte[] plainText = cipher.doFinal(cText);
        return new String(plainText, UTF_8);

    }
    public static String decryptWithPrefixIV(byte[] cText, SecretKey secret) throws Exception {

        ByteBuffer bb = ByteBuffer.wrap(cText);

        byte[] iv = new byte[IV_LENGTH_BYTE];
        bb.get(iv);
        //bb.get(iv, 0, iv.length);

        byte[] cipherText = new byte[bb.remaining()];
        bb.get(cipherText);

        String plainText = decrypt(cipherText, secret, iv);
        return plainText;

    }

    // AES-GCM needs GCMParameterSpec
    public static byte[] encrypt(byte[] pText, SecretKey secret, byte[] iv) throws Exception {

        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGO);
        cipher.init(Cipher.ENCRYPT_MODE, secret, new GCMParameterSpec(TAG_LENGTH_BIT, iv));
        byte[] encryptedText = cipher.doFinal(pText);
        return encryptedText;

    }
    // prefix IV length + IV bytes to cipher text
    public static byte[] encryptWithPrefixIV(byte[] pText, SecretKey secret, byte[] iv) throws Exception {

        byte[] cipherText = encrypt(pText, secret, iv);

        byte[] cipherTextWithIv = ByteBuffer.allocate(iv.length + cipherText.length)
                .put(iv)
                .put(cipherText)
                .array();
        return cipherTextWithIv;

    }

    boolean writeNdefMessageToTag(NdefMessage message, Tag detectedTag) {
        int size = message.toByteArray().length;
        try {
            Ndef ndef = Ndef.get(detectedTag);
            if (ndef != null) {
                ndef.connect();

                if (!ndef.isWritable()) {
                    Toast.makeText(this, "Tag is read-only.", Toast.LENGTH_SHORT).show();
                    String msg="Tag is read-only.";
                    // showMessageWriteUnsuccessfull(msg);
                    return false;
                }
                if (ndef.getMaxSize() < size) {
                    Toast.makeText(this, "The data cannot written to card, card capacity is " + ndef.getMaxSize() + " bytes, message is " + size + " bytes.", Toast.LENGTH_SHORT).show();
                    String msg="The data cannot written to card, card capacity is " + ndef.getMaxSize() + " bytes, message is " + size + " bytes.";
                    // showMessageWriteUnsuccessfull(msg);
                    return false;
                }

                ndef.writeNdefMessage(message);
                ndef.close();
                showCustomDialog("Write card!", "card has been written succesfully!");
                return true;
            } else {
                Log.e("No ndef", "No ndef");
                NdefFormatable ndefFormat = NdefFormatable.get(detectedTag);
                if (ndefFormat != null) {
                    Log.e("Ndef format","Ndef is formatable!");
                    try {

                        ndefFormat.connect();
                        ndefFormat.format(message);
                        ndefFormat.close();
                        Log.e("ndef",gson.toJson(ndefFormat));
                        //showDialogCardWrittenSuccessfully();
                        return true;
                    } catch (IOException e) {
                        Toast.makeText(this, "Failed to format tag", Toast.LENGTH_SHORT).show();
                        String msg="Failed to format tag";
                        Log.e("Format error", e.toString());
                        showCustomDialog("Write card!", msg);
                        Log.e("ndef fail",gson.toJson(e));
                        return false;
                    }


                } else {
                    Toast.makeText(this, "NDEF is not supported", Toast.LENGTH_SHORT).show();
                    String msg="NDEF is not supported";
                    showCustomDialog("Write card!", msg);
                    return false;
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Write operation is failed", Toast.LENGTH_SHORT).show();
            String msg="Write operation is failed";
            showCustomDialog("Write card!", msg);
        }
        return false;
    }


}

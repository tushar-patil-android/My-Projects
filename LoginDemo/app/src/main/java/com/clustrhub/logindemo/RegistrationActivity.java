package com.clustrhub.logindemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RegistrationActivity extends AppCompatActivity {

    private EditText fnameEdtTxt, lnameEdtTxt, emailEdtTxt, mobNoEdtTxt, pswdEdtTxt, cnfrnEdtTxt;
    private Button registerUserBtn;
    private String fnameEdtTxtValue, lnameEdtTxtValue, emailEdtTxtValue, mobNoEdtTxtValue, pswdEdtTxtValue, cnfrnEdtTxtValue;
    private String deviceId, deviceIMEI;
    private CheckBox acceptTermsCb;
    public static String AUTH_TOKEN = "auth_token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        TelephonyManager tManager = (TelephonyManager) getBaseContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        deviceIMEI = tManager.getDeviceId();


        fnameEdtTxt = (EditText) findViewById(R.id.first_name_edttxt);
        lnameEdtTxt = (EditText) findViewById(R.id.last_name_edttxt);
        emailEdtTxt = (EditText) findViewById(R.id.email_edttxt);
        mobNoEdtTxt = (EditText) findViewById(R.id.mob_no_edttxt);
        pswdEdtTxt = (EditText) findViewById(R.id.pswd_edttxt);
        cnfrnEdtTxt = (EditText) findViewById(R.id.cnfrm_pswd_edttxt);
        registerUserBtn = (Button) findViewById(R.id.register_button);
        acceptTermsCb = (CheckBox) findViewById(R.id.accept_terms_cb);

        registerUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fnameEdtTxtValue = fnameEdtTxt.getText().toString();
                lnameEdtTxtValue = lnameEdtTxt.getText().toString();
                emailEdtTxtValue = emailEdtTxt.getText().toString();
                mobNoEdtTxtValue = mobNoEdtTxt.getText().toString();
                pswdEdtTxtValue = pswdEdtTxt.getText().toString();
                cnfrnEdtTxtValue = cnfrnEdtTxt.getText().toString();

                if (!validate()) {
                    if (!pswdEdtTxtValue.trim().equals(cnfrnEdtTxtValue.trim())) {
                        pswdEdtTxt.setError("Password does not match");
                        cnfrnEdtTxt.setError("Password does not match");
                    }

                    if (mobNoEdtTxtValue.trim().length() >= 10 && mobNoEdtTxtValue.trim().length() >= 10) {
                        // Correct mobile number
                        new PostData().execute();
                    } else {
                        mobNoEdtTxt.setError("Enter correct mobile number");
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Enter All Parameters", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public boolean validate() {
        if (fnameEdtTxtValue.equals("")) {
            fnameEdtTxt.setError("Enter First Name");
        } else if (lnameEdtTxtValue.equals("")) {
            lnameEdtTxt.setError("Enter Last Name");
        } else if (emailEdtTxtValue.equals("")) {
            emailEdtTxt.setError("Enter Email");
        } else if (mobNoEdtTxtValue.equals("")) {
            mobNoEdtTxt.setError("Enter Mobile");
        }else if (!isEmailValid(emailEdtTxtValue.trim())){
            emailEdtTxt.setError("Enter Correct Email");
        }else if (pswdEdtTxtValue.equals("")) {
            pswdEdtTxt.setError("Enter Password");
        } else if (cnfrnEdtTxtValue.equals("")) {
            cnfrnEdtTxt.setError("Enter Confirm Password");
        } else if (!acceptTermsCb.isChecked()) {
            Toast.makeText(getApplicationContext(), "Please accept Terms", Toast.LENGTH_SHORT).show();
        } else {
            return false;
        }
        return true;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private class PostData extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... params) {

            InputStream inputStream = null;
            String result = "";

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://52.25.25.139//api/userauth/user");

            try {
               /* Example Json
                { "device" : { "id" : "ios6:dd7a2f5bdd1f8ad697a5c3b5c1ff4bc25e512c"
                },
                    "user" : {
                    "first_name" : "Aaron",
                            "last_name" : "Smith",
                            "email" : "aaron@gmail.com",
                            "countrycode" : "91",
                            "mobile_number" : "1254566822",
                            "password" :"Astar1@qB",
                            "agreeterms" : "1"
                }
                }*/

                JSONObject mainJsonObject = new JSONObject();
                JSONObject deviceJsonObject = new JSONObject();
                deviceJsonObject.put("id", deviceId);

                JSONObject userJsonObject = new JSONObject();
                userJsonObject.put("first_name", fnameEdtTxtValue);
                userJsonObject.put("last_name", lnameEdtTxtValue);
                userJsonObject.put("email", emailEdtTxtValue);
                userJsonObject.put("countrycode", "91");
                userJsonObject.put("mobilenumber", mobNoEdtTxtValue);
                userJsonObject.put("password", pswdEdtTxtValue);
                userJsonObject.put("agreeterms", "1");

                mainJsonObject.put("device", deviceJsonObject);
                mainJsonObject.put("user", userJsonObject);
                String json = mainJsonObject.toString();

                StringEntity se = new StringEntity(json);
                httpPost.setEntity(se);
                httpPost.setHeader("Content-type", "application/json");
                HttpResponse httpResponse = httpclient.execute(httpPost);
                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null) {
                    result = convertInputStreamToString(inputStream);

                    final JSONObject jsnObj = new JSONObject(result);
                    final String res = jsnObj.get("message").toString();
                    final String status = jsnObj.get("status").toString();
                    if (status.trim().equals("true")) {
                        SharedPreferences.Editor editor = getSharedPreferences("Prefs", MODE_PRIVATE).edit();
                        try {
                            String dataStr = jsnObj.getString("data");
                            JSONObject jsn = new JSONObject(dataStr);
                            editor.putString("auth_token", jsn.getString("auth_token").toString());
                            editor.putString("device_id", jsn.getString("device_id").toString());

                            runOnUiThread(new Runnable(){

                                @Override
                                public void run(){
                                   Toast.makeText(RegistrationActivity.this, res, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.commit();
                    }
                else {
                        Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
                    }

                } else {
                    result = "Did not work!";
                    Log.d("Response", result.toString());
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.d("Response", e.toString());
            }
            return null;
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

}

package com.clustrhub.logindemo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mMobNoEdtTxt;
    private EditText mPasswordEdtTxt;
    private View mProgressView;
    private View mLoginFormView;
    private Button loginBtn, registerBtn;
    private String deviceId, authToken;
    private String enteredMobNo, enteredPswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        mMobNoEdtTxt = (EditText) findViewById(R.id.mob_no);
        mPasswordEdtTxt = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.login_button);
        registerBtn = (Button) findViewById(R.id.register_button);

        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 8;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                enteredMobNo = mMobNoEdtTxt.getText().toString();
                enteredPswd = mPasswordEdtTxt.getText().toString();
                if (enteredMobNo.trim().equals("")) {
                    mMobNoEdtTxt.setError("Enter Mobile Number");
                } else if (enteredPswd.trim().equals("")) {
                    mMobNoEdtTxt.setError("Enter Password");
                } else {
                    new PostData().execute();
                }
                break;
            case R.id.register_button:
                Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
                startActivity(intent);
                break;
        }
    }

    private class PostData extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... params) {

            InputStream inputStream = null;
            String result = "";

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://52.25.25.139/api/token/auth");

            try {
                /* Example JSON
                { "device" : { "id" : "android:dd7a2f5bdd1f8ad697a5c3b5c1ff4bc25e512c",
                        "auth_token" : "5080f57e3854ee89a8eb8a1ff63b78c8d93228f82f61a4d96723eb4a7aa6fc81"
                },
                    "user" : { "mobile_number" : "1111111111",
                        "password" : "Astar1@qB"
                }
                }
                * */


                SharedPreferences prefs = getSharedPreferences("Prefs", MODE_PRIVATE);
                String authToken = prefs.getString("auth_token", null);
                deviceId = prefs.getString("device_id", null);

                JSONObject mainJsonObject = new JSONObject();
                JSONObject deviceJsonObject = new JSONObject();
                deviceJsonObject.put("id", deviceId);
                deviceJsonObject.put("auth_token", authToken);

                JSONObject userJsonObject = new JSONObject();
                userJsonObject.put("mobilenumber", enteredMobNo);
                userJsonObject.put("password", enteredPswd);

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

                    JSONObject jsnObj = new JSONObject(result);
                    final String res = jsnObj.get("message").toString();
                    final String status = jsnObj.get("status").toString();

                        runOnUiThread(new Runnable(){

                            @Override
                            public void run(){
                                if (status.trim().equals("true")) {
                                Toast.makeText(LoginActivity.this, res, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), res, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                } else {
                    result = "Did not work!";
                    Log.d("Response", result.toString());
                    Toast.makeText(getApplicationContext(), "Invalid Login", Toast.LENGTH_SHORT).show();
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

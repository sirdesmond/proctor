package com.team2.android.proctor.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.team2.android.proctor.R;
import com.team2.android.proctor.model.constants.Constants;
import com.team2.android.proctor.model.input.LoginInput;
import com.team2.android.proctor.model.input.User;
import com.team2.android.proctor.util.JSONParser;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener{

    Proctor proctor;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = (Button) findViewById(R.id.btn_login);
        proctor = (Proctor) getApplicationContext();

        login.setOnClickListener(this);
    }

    private LoginInput testInput;
    Intent intent;
    int REQUEST_CODE=1;

    // this is to check the user input
    public void loginVerification(View v) throws ExecutionException, InterruptedException, JSONException {

        // get the userid
        EditText ed_username = (EditText) findViewById(R.id.login_username);
        String username = ed_username.getText().toString();
        // get the password
        EditText ed_password = (EditText) findViewById(R.id.login_password);
        String password = ed_password.getText().toString();
        //validate input
        boolean isValid = isValidInput(username, password);

        if (isValid) {

            testInput = new LoginInput(username, password);
            GetData getData = new GetData();
            getData.execute((new LoginInput(username, password)));
            JSONObject loginOutput = getData.get();

            int statuscode = com.team2.android.proctor.util.JSONParser.StatusCode;
            if (HttpStatus.SC_OK == statuscode) {
                //Here we Need to save details in SharedPreference

                proctor.getSession().createLoginSession(loginOutput.getString("isStudent"),
                        username, loginOutput.getLong("userId"));
                Intent i = new Intent();
                setResult(Activity.RESULT_CANCELED,i);
                finish();
            } else {
                Toast.makeText(this, "wrong credentials", Toast.LENGTH_SHORT).show();
            }

        }

    }

    public boolean isValidInput(String username, String password) {

        return (username.isEmpty() || password.isEmpty()) ? false : true;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_login){
            try {
                loginVerification(v);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class GetData extends AsyncTask<LoginInput, Integer, JSONObject> {

        @Override
        protected JSONObject doInBackground(LoginInput... params) {
            JSONObject returnJsonVal = null;
            for (LoginInput para : params) {
                List<NameValuePair> params1 = new ArrayList<NameValuePair>();

                params1.add(new BasicNameValuePair("username", para.getUsername()));
                params1.add(new BasicNameValuePair("password", para.getPassword()));

                returnJsonVal = JSONParser.makeHttpRequest(Constants.LOGIN_ENDPOINT_URL, "POST", params1);
            }
            return returnJsonVal;
        }

//        @Override
//        protected void onPostExecute() {
//            super.onPostExecute();
//            //intent to join student or professor activity
//        }
    }


}

package com.team2.android.proctor.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.team2.android.proctor.R;
import com.team2.android.proctor.model.constants.Constants;
import com.team2.android.proctor.model.output.User;
import com.team2.android.proctor.util.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewAttendanceActivity extends AppCompatActivity {

    ArrayList<String> users;
    Intent i;
    int courseId;
    private static final String TAG = ViewAttendanceActivity.class.getSimpleName();
    ArrayAdapter<String> userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_attendance);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        i = getIntent();
        courseId = i.getIntExtra("courseId",0);
        users = new ArrayList<>();

        //execute task to populate users
        new ViewAttendanceTask().execute();
        ListView presentList = (ListView) findViewById(R.id.presentList);
        userAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,users);

        presentList.setAdapter(userAdapter);

    }

    class ViewAttendanceTask extends AsyncTask<Void,Void,JSONArray>{

        @Override
        protected JSONArray doInBackground(Void... params) {
            JSONArray response;

            List<NameValuePair> params1 = new ArrayList<>();

            params1.add(new BasicNameValuePair("courseId",String.valueOf(courseId)));
            response = JSONParser.makeHttpRequestArray(Constants.VIEW_ATTENDANCE_URL, "POST", params1);
            return response;
        }

        @Override
        protected void onPostExecute(JSONArray usersArray) {
            super.onPostExecute(usersArray);

            if(usersArray != null){

                for(int i =0;i<usersArray.length();i++){
                    try {
                        JSONObject obj = usersArray.getJSONObject(i);
                        User user = new User();
                        Log.d(TAG, obj.toString());
                        user.setName(obj.getString("firstname") + " " + obj.getString("lastname"));
                        users.add(user.getName());
                        userAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else{
                Log.e(TAG,"Nothing in array");
            }
        }
    }

}

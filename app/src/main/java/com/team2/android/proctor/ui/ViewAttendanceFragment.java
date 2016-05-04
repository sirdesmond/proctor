package com.team2.android.proctor.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class ViewAttendanceFragment extends BackHandledFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    ArrayList<String> users;
    Intent i;
    int courseId;
    public static final String TAG = ViewAttendanceFragment.class.getSimpleName();
    ArrayAdapter<String> userAdapter;
    View fragmentView;
    Bundle bundle;

    public ViewAttendanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewAttendanceFragment newInstance(String param1, String param2) {
        ViewAttendanceFragment fragment = new ViewAttendanceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getTagText() {
        return TAG;
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        bundle = getArguments();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView =  inflater.inflate(R.layout.fragment_view, container, false);



        courseId = bundle.getInt("courseId");
        Log.d("ViewAttendance","course Id:"+courseId);
        users = new ArrayList<>();

        //execute task to populate users
        new ViewAttendanceTask().execute();
        ListView presentList = (ListView) fragmentView.findViewById(R.id.presentList);
        userAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1,users);

        presentList.setAdapter(userAdapter);

        return fragmentView;
    }





    class ViewAttendanceTask extends AsyncTask<Void,Void,JSONArray> {

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

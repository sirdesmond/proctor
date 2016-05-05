package com.team2.android.proctor.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AppIdentifier;
import com.google.android.gms.nearby.connection.AppMetadata;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.team2.android.proctor.R;
import com.team2.android.proctor.model.constants.Constants;
import com.team2.android.proctor.model.input.Attendance;
import com.team2.android.proctor.model.input.User;
import com.team2.android.proctor.model.output.Course;
import com.team2.android.proctor.util.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;


public class AttendanceFragment extends BackHandledFragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        Connections.ConnectionRequestListener,
        Connections.MessageListener,
        Connections.EndpointDiscoveryListener {

    public static final String TAG = "attend";
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Long pref_UserId;
    Course course;
    Proctor proctor;
    View fragmentView;
    OnViewCoursesListener mCallback;
    Bundle bundle;
    TextView already_chk;


    public interface OnViewCoursesListener{
        public void onViewCourses(int courseId);
    }

    /**
     * Timeouts (in millis) for startAdvertising and startDiscovery.
     */
    private static final long TIMEOUT_ADVERTISE = 0;
    private static final long TIMEOUT_DISCOVER = 1000L * 30L;

    /**
     * Possible states for this application:
     *      IDLE - GoogleApiClient not yet connected, can't do anything.
     *      READY - GoogleApiClient connected, ready to use Nearby Connections API.
     *      ADVERTISING - advertising for peers to connect.
     *      DISCOVERING - looking for a peer that is advertising.
     *      CONNECTED - found a peer.
     */
    @Retention(RetentionPolicy.CLASS)
    @IntDef({STATE_IDLE, STATE_READY, STATE_ADVERTISING, STATE_DISCOVERING, STATE_CONNECTED})
    public @interface NearbyConnectionState {}
    private static final int STATE_IDLE = 1023;
    private static final int STATE_READY = 1024;
    private static final int STATE_ADVERTISING = 1025;
    private static final int STATE_DISCOVERING = 1026;
    private static final int STATE_CONNECTED = 1027;

    private GoogleApiClient mGoogleApiClient;


    /** The current state of the application **/
    @NearbyConnectionState
    private int mState = STATE_IDLE;

    /** Views and dialogs**/
    TextView course_tv;
    TextView duration_tv;
    Button take_attendance_btn;
    Button view_attendance_btn;
    Button give_attendance_btn;
    boolean checked_in = false;
    /** The endpoint ID of the connected peer, used for messaging **/
    private String mOtherEndpointId;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public AttendanceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AttendanceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AttendanceFragment newInstance(String param1, String param2) {
        AttendanceFragment fragment = new AttendanceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onAttach(Activity context) {
        super.onAttach(context);

        try{
            mCallback = (OnViewCoursesListener) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString()
                    + "must implement OnCourseSelectedListener");
        }
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
        User user = (User) bundle.getSerializable("user");
        int userType = user.getCode();

        sharedPref = getActivity().getSharedPreferences(getString
                (R.string.SHARED_PREF_KEY), Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        checked_in = sharedPref.getBoolean("checkedIn",false);
        if(userType == 0 ) {
            fragmentView = inflater.inflate(R.layout.fragment_prof_attendance, container, false);
        }

        if(userType == 1 ) {
            fragmentView = inflater.inflate(R.layout.fragment_student_attendance, container, false);
        }

        pref_UserId=sharedPref.getLong(getString(R.string.USERID), 0);

        Log.d(TAG,"the user id :"+ pref_UserId);

        //TODO fetch course
        course = (Course) bundle.getSerializable("course");
        Log.d("Attendance","Course is "+ course.getCourseName()+":"+course.getCourse_id());
        //set view based on user type 0-prof, 1-student

        course_tv = (TextView) fragmentView.findViewById(R.id.course_tv);
        duration_tv = (TextView) fragmentView.findViewById(R.id.duration_tv);
        take_attendance_btn = (Button) fragmentView.findViewById(R.id.takeatt_btn);
        view_attendance_btn = (Button) fragmentView.findViewById(R.id.viewatt_btn);
        give_attendance_btn = (Button) fragmentView.findViewById(R.id.checkin_btn);
        already_chk = (TextView) fragmentView.findViewById(R.id.already_chk);

        /*course_tv.setText(course.getCourseName());
        duration_tv.setText(course.getStartDuration().toString()+course.getEndDuration().toString());*/

        course_tv.setText(course.getCourseName());
        duration_tv.setText(course.getCourseStartTime()+" to "+ course.getCourseEndTime());

        // Initialize Google API Client for Nearby Connections.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Nearby.CONNECTIONS_API)
                .build();

        if (take_attendance_btn != null) take_attendance_btn.setOnClickListener(this);
        if (view_attendance_btn != null) view_attendance_btn.setOnClickListener(this);

        if (give_attendance_btn != null) give_attendance_btn.setOnClickListener(this);
        return fragmentView;
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

        // Disconnect the Google API client and stop any ongoing discovery or advertising. When the
        // GoogleAPIClient is disconnected, any connected peers will get an onDisconnected callback.
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Check if the device is connected (or connecting) to a WiFi network.
     * @return true if connected or connecting, false otherwise.
     */
    private boolean isConnectedToNetwork() {
        ConnectivityManager connManager = (ConnectivityManager)
                getActivity().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo info = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return (info != null && info.isConnectedOrConnecting());
    }

    /**
     * Begin advertising for Nearby Connections, if possible.
     */
    private void startAdvertising() {
        Log.d(TAG,"start advertising");
        if (!isConnectedToNetwork()) {
            Log.d(TAG,"startAdvertising: not connected to WiFi network.");
            //send a signal to service
            return;
        }

        List<AppIdentifier> appIdentifierList = new ArrayList<>();
        appIdentifierList.add(new AppIdentifier(getActivity().getPackageName()));
        AppMetadata appMetadata = new AppMetadata(appIdentifierList);

        // Advertise for Nearby Connections.
        String name = null;
        Nearby.Connections.startAdvertising(mGoogleApiClient, name, appMetadata, TIMEOUT_ADVERTISE,
                this).setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {

            @Override
            public void onResult(Connections.StartAdvertisingResult result) {
                Log.d(TAG, "startAdvertising:onResult:" + result);

                if (result.getStatus().isSuccess()) {
                    Log.d(TAG, "startAdvertising:onResult: SUCCESS");
                    updateToast(STATE_ADVERTISING);
                } else {
                    Log.d(TAG, "startAdvertising:onResult: FAILURE");
                    Toast.makeText(getActivity(), "Professor is not ready yet!", Toast.LENGTH_SHORT).show();
                    int statusCode = result.getStatus().getStatusCode();
                    if (statusCode == ConnectionsStatusCodes.STATUS_ALREADY_ADVERTISING) {
                        Log.d(TAG, "STATUS_ALREADY_ADVERTISING");
                    } else {
                        Log.d(TAG, "STATE_READY");
                        updateToast(STATE_READY);
                    }
                }
            }
        });
    }

    private void startDiscovery() {
        Log.d(TAG, "startDiscovery");
        if (!isConnectedToNetwork()) {
            Log.d(TAG, "startDiscovery: not connected to WiFi network.");

            //TODO: backup for WIFI
            //if prof sent signal to service
            //then send attendance to service directly
            return;
        }

        // Discover nearby apps that are advertising with the required service ID.
        String serviceId = getString(R.string.service_id);
        Nearby.Connections.startDiscovery(mGoogleApiClient, serviceId, TIMEOUT_DISCOVER, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.d(TAG, "startDiscovery:onResult: SUCCESS");

                            updateToast(STATE_DISCOVERING);
                        } else {
                            Log.d(TAG, "startDiscovery:onResult: FAILURE");

                            // If the user hits 'Discover' multiple times in the timeout window,
                            // the error will be STATUS_ALREADY_DISCOVERING
                            int statusCode = status.getStatusCode();
                            if (statusCode == ConnectionsStatusCodes.STATUS_ALREADY_DISCOVERING) {
                                Log.d(TAG, "STATUS_ALREADY_DISCOVERING");
                            } else {
                                updateToast(STATE_READY);
                            }
                        }
                    }
                });
    }

    private void sendMessage() {
        // Sends a reliable message, which is guaranteed to be delivered eventually and to respect
        // message ordering from sender to receiver.

        //send id:present
        String msg = ""+pref_UserId+":"+ course.getCourse_id();
        Log.d(TAG, "sending message...");
        if(!checked_in){

            Nearby.Connections.sendReliableMessage(mGoogleApiClient, mOtherEndpointId, msg.getBytes());
            editor.putBoolean("checkedIn",true);
            editor.commit();
            give_attendance_btn.setVisibility(View.INVISIBLE);
            already_chk.setVisibility(View.VISIBLE);
            already_chk.setText("You just checked in to this class");
        }else{
            give_attendance_btn.setVisibility(View.INVISIBLE);
            already_chk.setVisibility(View.VISIBLE);
            already_chk.setText("You've checked into this class already");
        }
    }

    /**
     * Change the application state and update the visibility on on-screen views '
     * based on the new state of the application.
     * @param newState the state to move to (should be NearbyConnectionState)
     */
    private void updateToast(@NearbyConnectionState int newState) {
        mState = newState;
        switch (mState) {
            case STATE_IDLE:
                // The GoogleAPIClient is not connected, we can't yet start advertising
                Toast.makeText(getActivity(), "Can't start advertising",
                        Toast.LENGTH_SHORT).show();
                break;
            case STATE_READY:
                // The GoogleAPIClient is connected, we can begin advertising or discovery.
                Toast.makeText(getActivity(), "Can start advertising or discovery",
                        Toast.LENGTH_SHORT).show();
                break;
            case STATE_ADVERTISING:
                break;
            case STATE_DISCOVERING:
                break;
            case STATE_CONNECTED:
                // We are connected to another device via the Connections API
                Toast.makeText(getActivity(), "Connected to another device",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }



    /**
     * Send a connection request to a given endpoint.
     * @param endpointId the endpointId to which you want to connect.
     * @param endpointName the name of the endpoint to which you want to connect. Not required to
     *                     make the connection, but used to display after success or failure.
     */
    private void connectTo(String endpointId, final String endpointName) {
        Log.d(TAG,"connectTo:" + endpointId + ":" + endpointName);

        // Send a connection request to a remote endpoint.
        String myName = null;
        byte[] myPayload = null;
        Nearby.Connections.sendConnectionRequest(mGoogleApiClient, myName, endpointId, myPayload,
                new Connections.ConnectionResponseCallback() {
                    @Override
                    public void onConnectionResponse(String endpointId, Status status,
                                                     byte[] bytes) {
                        Log.d(TAG, "onConnectionResponse:" + endpointId + ":" + status);
                        if (status.isSuccess()) {
                            Log.d(TAG, "onConnectionResponse: " + endpointName + " SUCCESS");
                            Toast.makeText(getActivity(), "Connected to " + endpointName,
                                    Toast.LENGTH_SHORT).show();

                            mOtherEndpointId = endpointId;
                            updateToast(STATE_CONNECTED);
                            //send message now
                            sendMessage();
                        } else {
                            Log.d(TAG, "onConnectionResponse: " + endpointName + " FAILURE");
                        }
                    }
                }, this);
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG,"onConnected");
        updateToast(STATE_READY);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"onConnectionSuspended: " + i);
        updateToast(STATE_IDLE);

        // Try to re-connect
        mGoogleApiClient.reconnect();
    }

    @Override
    public void onConnectionRequest(final String endpointId, String deviceId, String endpointName,
                                    byte[] payload) {
        Log.d(TAG,"onConnectionRequest:" + endpointId + ":" + endpointName);


        Nearby.Connections.acceptConnectionRequest(mGoogleApiClient, endpointId,
                payload, AttendanceFragment.this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.d(TAG, "acceptConnectionRequest: SUCCESS");

                            mOtherEndpointId = endpointId;
                            updateToast(STATE_CONNECTED);
                        } else {
                            Log.d(TAG, "acceptConnectionRequest: FAILURE");
                        }
                    }
                });

    }

    @Override
    public void onEndpointFound(final String endpointId, String deviceId, String serviceId,
                                final String endpointName) {

        Log.d(TAG, "onEndpointFound:" + endpointId + ":" + endpointName);

        AttendanceFragment.this.connectTo(endpointId, endpointName);

    }

    @Override
    public void onEndpointLost(String endpointId) {
        Log.d(TAG,"onEndpointLost:" + endpointId);
    }

    @Override
    public void onMessageReceived(String endpointId, byte[] payload, boolean isReliable) {
        //send payload in async task
        Log.d(TAG,"onMessageReceived:" +  new String(payload));
        String message = new String(payload);
        Attendance attendance = new Attendance();
        attendance.setUserId(Long.parseLong(message.split(":")[0]));
        attendance.setCourseId(Integer.parseInt(message.split(":")[1]));

        new TakeAttendanceTask().execute(attendance);
    }

    @Override
    public void onDisconnected(String endpointId) {
        Log.d(TAG, "onDisconnected:" + endpointId);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.viewatt_btn:
                //call to view Attendance fragment
                mCallback.onViewCourses(course.getCourse_id());

                break;

            case R.id.takeatt_btn:
                startAdvertising();
                Toast.makeText(getActivity(), "Taking Attendance in background", Toast.LENGTH_LONG).show();
                break;
            case R.id.checkin_btn:
                startDiscovery();
                Toast.makeText(getActivity(), "giving attendance in background", Toast.LENGTH_LONG).show();

                break;

        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG,"onConnectionFailed: " + connectionResult);
        updateToast(STATE_IDLE);
    }

    class TakeAttendanceTask extends AsyncTask<Attendance, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(Attendance... params) {
            JSONObject returnJsonVal = null;
            for (Attendance param : params) {
                List<NameValuePair> params1 = new ArrayList<>();

                params1.add(new BasicNameValuePair("courseId",
                        String.valueOf(param.getCourseId())));
                params1.add(new BasicNameValuePair("userId",
                        String.valueOf(param.getUserId())));

                returnJsonVal = JSONParser.makeHttpRequest(Constants.TAKE_ATTENDANCE_URL, "POST", params1);
            }
            return returnJsonVal;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            try {
                String status = jsonObject.getString("response");
                Log.d(TAG, "response: " + status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}

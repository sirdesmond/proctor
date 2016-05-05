package com.team2.android.proctor.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.team2.android.proctor.model.input.User;
import com.team2.android.proctor.ui.LoginActivity;

/**
 * Created by kofikyei on 4/16/16.
 */
public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    User user;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "PROCTOR_PREF_KEY";

    // All Shared Preferences Keys
    private static final String IS_STUDENT = "IsStudent";

    // User name (make variable public to access from outside)
    public static final String KEY_USERNAME = "userName";

    // Email address (make variable public to access from outside)
    public static final String KEY_USERID = "userId";

    // Constructor
    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String isStudent,String userName, long userId){
        // Storing login value as TRUE
        editor.putString(IS_STUDENT, isStudent);

        // Storing name in pref
        editor.putString(KEY_USERNAME, userName);

        // Storing userId in pref
        editor.putLong(KEY_USERID, userId);

        // commit changes
        editor.commit();
    }


    public User checkUserType(){
        // Check login status
        if(pref.getString(IS_STUDENT,"").equals("0")){
            user = new User(0,pref.getLong("userId",9999),pref.getString(KEY_USERNAME,"nothing"));

        }else if(pref.getString(IS_STUDENT,"").equals("1")){
            user = new User(1,pref.getLong("userId",9999),pref.getString(KEY_USERNAME,"nothing"));
        }else{
            user = new User(99,99L,"nothing");
        }

        return user;

    }


    /**
     * Clear session details
     * */
    public void logoutUser(){
        // Clearing all data from Shared Preferences

        createLoginSession("1","9999999",999999L);
        editor.clear();
        editor.commit();

        // After logout redirect user to Login Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }


}

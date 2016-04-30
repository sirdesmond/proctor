package com.team2.android.proctor.ui;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.team2.android.proctor.R;
import com.team2.android.proctor.model.input.User;
import com.team2.android.proctor.model.output.Course;

public class MainActivity  extends ActionBarActivity
        implements NavigationDrawerCallbacks,
        ProfessorFragment.OnCourseSelectedListener,
        StudentFragment.OnCourseSelectedListener,
        AttendanceFragment.OnViewCoursesListener,
        BackHandledFragment.BackHandlerInterface{

    User user;
    Fragment fragment;


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    Bundle bundle;
    private BackHandledFragment selectedFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = ((Proctor)getApplicationContext()).getSession().checkUserType();
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
        // populate the navigation drawer
        //TODO set userData
        mNavigationDrawerFragment.setUserData("Desmond", "dappiahk@hawk.iit.edu", BitmapFactory.decodeResource(getResources(), R.drawable.avatar));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        switch(position){
            case 0:
                if(user.getCode() == 0){
                    //start professor fragment
                    Log.d("Main","in main here");
                    fragment = getFragmentManager().findFragmentByTag(ProfessorFragment.TAG);
                    bundle = new Bundle();
                    bundle.putSerializable("user", user);
                    if (fragment == null) {
                        fragment = new ProfessorFragment();
                        fragment.setArguments(bundle);
                    }
                    getFragmentManager().beginTransaction().replace(R.id.container, fragment, ProfessorFragment.TAG).commit();
                }
                else if(user.getCode() == 1){
                    //start student fragment
                    bundle = new Bundle();
                    bundle.putSerializable("user",user);
                    fragment = getFragmentManager().findFragmentByTag(StudentFragment.TAG);
                    if (fragment == null) {
                        fragment = new StudentFragment();
                        fragment.setArguments(bundle);
                    }
                    getFragmentManager().beginTransaction().replace(R.id.container, fragment, StudentFragment.TAG).commit();
                }else{
                    //login activity
                    Log.d("Main","in here...");
                    Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivityForResult(i, 1);
                }
                break;
            case 1:
                fragment = getFragmentManager().findFragmentByTag(HelpFragment.TAG);
                if (fragment == null) {
                    fragment = new HelpFragment();
                }
                getFragmentManager().beginTransaction().replace(R.id.container, fragment, HelpFragment.TAG).commit();
                break;
            case 2:
                ((Proctor)getApplicationContext()).getSession().logoutUser();
                break;
            case 3:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        user = ((Proctor)getApplicationContext()).getSession().checkUserType();
        if (requestCode == 1){
            Log.d("Main", "came back");
            if(user.getCode() == 0){
                //start professor fragment
                fragment = getFragmentManager().findFragmentByTag(ProfessorFragment.TAG);
                bundle = new Bundle();
                bundle.putSerializable("user", user);
                if (fragment == null) {
                    fragment = new ProfessorFragment();
                    fragment.setArguments(bundle);
                }
                getFragmentManager().beginTransaction().replace(R.id.container, fragment, ProfessorFragment.TAG).commit();
            }
            else{
                //start student fragment
                fragment = getFragmentManager().findFragmentByTag(StudentFragment.TAG);
                bundle = new Bundle();
                bundle.putSerializable("user", user);
                if (fragment == null) {
                    fragment = new StudentFragment();
                    fragment.setArguments(bundle);
                }
                getFragmentManager().beginTransaction().replace(R.id.container, fragment, StudentFragment.TAG).commit();
            }
        }
    }

    @Override
        public void onBackPressed() {
            if (mNavigationDrawerFragment.isDrawerOpen())
                mNavigationDrawerFragment.closeDrawer();
            else
                super.onBackPressed();
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCourseSelected(User user,Course course) {
        AttendanceFragment attendanceFrag = (AttendanceFragment)
                getFragmentManager().findFragmentByTag(AttendanceFragment.TAG);

       if(attendanceFrag==null) {
           Log.d("Main", "course selected: " + course.getCourseName());
           attendanceFrag = new AttendanceFragment();
           Bundle args = new Bundle();
           args.putSerializable("user", user);
           args.putSerializable("course", course);
           attendanceFrag.setArguments(args);
           getFragmentManager().beginTransaction()
                   .replace(R.id.container, attendanceFrag, AttendanceFragment.TAG)
                   .commit();
       }

    }


    @Override
    public void onViewCourses(int courseId) {
        ViewAttendanceFragment attendanceFrag = (ViewAttendanceFragment)
                getFragmentManager().findFragmentByTag(ViewAttendanceFragment.TAG);

        if(attendanceFrag==null) {
            Log.d("Main", "course selected: " + courseId);
            attendanceFrag = new ViewAttendanceFragment();
            Bundle args = new Bundle();
            args.putSerializable("courseId", courseId);
            attendanceFrag.setArguments(args);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, attendanceFrag, ViewAttendanceFragment.TAG)
                    .commit();
        }
    }

    @Override
    public void setSelectedFragment(BackHandledFragment backHandledFragment) {
        this.selectedFragment = backHandledFragment;
    }
}

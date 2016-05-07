package com.team2.android.proctor.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.app.FragmentTransaction;
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
        AttendanceFragment.OnViewCoursesListener{

    User user;
    boolean ignore = false;



    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;
    Bundle bundle;


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
        mNavigationDrawerFragment.setUserData(user.getUserName(), user.getUserName() + "@hawk.iit.edu",
                BitmapFactory.decodeResource(getResources(), R.drawable.avatar));
    }

    @Override
    protected void onResume() {
        Fragment fragment;
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        super.onResume();

        user = ((Proctor)getApplicationContext()).getSession().checkUserType();

        //TODO set userData
        mNavigationDrawerFragment.setUserData(user.getUserName(), user.getUserName() + "@hawk.iit.edu",
                BitmapFactory.decodeResource(getResources(), R.drawable.avatar));




        Log.d("Main", user.getCode() + ":" + user.getUserName() + ":" + user.getUserId());

        if(user.getCode() == 0){
            //start professor fragment
            bundle = new Bundle();
            bundle.putSerializable("user", user);


            clearBackStack();
            fragment = new ProfessorFragment();
            fragment.setArguments(bundle);
            ft.replace(R.id.container, fragment, ProfessorFragment.TAG)
                    .addToBackStack(null).commit();


        }
        else if(user.getCode() == 1){
            //start student fragment
            bundle = new Bundle();
            bundle.putSerializable("user", user);

            clearBackStack();
            fragment = new StudentFragment();
            fragment.setArguments(bundle);
            ft.replace(R.id.container, fragment, StudentFragment.TAG)
                    .addToBackStack(null).commit();

        }

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment fragment;

        switch(position){
            case 0:
                if(ignore==true){
                    clearBackStack();
                    ignore = false;
                }
                if(user.getCode() == 0){
                    //start professor fragment
                    fragment = getFragmentManager().findFragmentByTag(ProfessorFragment.TAG);
                    bundle = new Bundle();
                    bundle.putSerializable("user", user);
                    if (fragment == null) {
                        fragment = new ProfessorFragment();
                        fragment.setArguments(bundle);
                        ft.replace(R.id.container, fragment, ProfessorFragment.TAG)
                                .addToBackStack(ProfessorFragment.TAG);
                    }else{
                        ft.replace(R.id.container, fragment, ProfessorFragment.TAG);
                    }

                   ft.commit();

                }
                else if(user.getCode() == 1){
                    //start student fragment
                    bundle = new Bundle();
                    bundle.putSerializable("user", user);
                    fragment = getFragmentManager().findFragmentByTag(StudentFragment.TAG);

                    if (fragment == null) {
                        fragment = new StudentFragment();
                        fragment.setArguments(bundle);
                        ft.replace(R.id.container, fragment, StudentFragment.TAG)
                                .addToBackStack(StudentFragment.TAG);
                    }else{
                        ft.replace(R.id.container, fragment, StudentFragment.TAG);
                    }
                    ft.commit();

                }else{
                    //login activity
                    Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivityForResult(i, 1);
                }
                break;
            case 1:
                if(ignore==true){
                    clearBackStack();
                    ignore = false;
                }
                fragment = getFragmentManager().findFragmentByTag(HelpFragment.TAG);

                if (fragment == null) {
                    fragment = new HelpFragment();
                    ft.replace(R.id.container, fragment, HelpFragment.TAG)
                            .addToBackStack(HelpFragment.TAG);
                }else{
                    ft.replace(R.id.container, fragment, HelpFragment.TAG);
                }
                ft.commit();

                if(ignore==true){
                    clearBackStack();
                    ignore = false;
                }
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
        mNavigationDrawerFragment.setUserData(user.getUserName(), user.getUserName() + "@hawk.iit.edu",
                BitmapFactory.decodeResource(getResources(), R.drawable.avatar));

        recreate();
        if (requestCode == 1) {
            ignore = true;
            Log.d("Main", "calling onResume");
            onResume();
        }


    }

    private void clearBackStack() {
        FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
        public void onBackPressed() {

            if (getFragmentManager().getBackStackEntryCount() > 1) {
                getFragmentManager().popBackStack();
            } else {
                if (mNavigationDrawerFragment.isDrawerOpen())
                    mNavigationDrawerFragment.closeDrawer();
                else mNavigationDrawerFragment.openDrawer();
            }


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
                   .addToBackStack(AttendanceFragment.TAG).commit();
       }

    }


    @Override
    public void onViewCourses(int courseId) {
        ViewAttendanceFragment attendanceFrag = (ViewAttendanceFragment)
                getFragmentManager().findFragmentByTag(ViewAttendanceFragment.TAG);

        if(attendanceFrag == null) {
            Log.d("Main", "course selected: " + courseId);
            attendanceFrag = new ViewAttendanceFragment();
            Bundle args = new Bundle();
            args.putSerializable("courseId", courseId);
            attendanceFrag.setArguments(args);

            getFragmentManager().beginTransaction()
                    .replace(R.id.container, attendanceFrag, ViewAttendanceFragment.TAG)
                    .addToBackStack(ViewAttendanceFragment.TAG).commit();
        }

    }


}

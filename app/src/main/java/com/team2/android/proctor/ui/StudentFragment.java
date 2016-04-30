package com.team2.android.proctor.ui;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.team2.android.proctor.R;
import com.team2.android.proctor.model.constants.Constants;
import com.team2.android.proctor.model.input.User;
import com.team2.android.proctor.model.output.Course;
import com.team2.android.proctor.util.CourseAdapter;
import com.team2.android.proctor.util.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class StudentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String TAG = "prof";


    public ArrayList<Course> courses  = new ArrayList<Course>();
    Proctor proctor;


    View fragmentView;
    Bundle bundle;
    OnCourseSelectedListener mCallback;
    User user;
    ListView courselist;
    CourseAdapter courseAdapter;

    public interface OnCourseSelectedListener{
        public void onCourseSelected(User user,Course course);
    }

    public StudentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfessorFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfessorFragment newInstance(String param1, String param2) {
        ProfessorFragment fragment = new ProfessorFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try{
            mCallback = (OnCourseSelectedListener) activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString()
                    + "must implement OnCourseSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        proctor = (Proctor) getActivity().getApplicationContext();
        bundle = getArguments();


        LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        fragmentView = mInflater.inflate(R.layout.fragment_student, null, false);
        courselist = (ListView) fragmentView.findViewById(R.id.courselist);

        user = (User) bundle.getSerializable("user");
        Log.d("Student", "user id: " + user.getUserId());
        long studentIDint = user.getUserId();
        String studentID = String.valueOf(studentIDint);
        GetStudentCourseData getStudentCourseData =  new GetStudentCourseData(studentID);
        getStudentCourseData.execute((Void) null);

        JSONArray jsonCourses = null;
        try {
            jsonCourses = getStudentCourseData.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        int size;

        size = jsonCourses.length();


        String[] courseNames = new String[size];
        Time[] stimestamps = new Time[size];
        Time[] etimestamps = new Time[size];
        String[] days = new String[size];
        int[] courseIds = new int[size];

        try {
            for(int i=0;i<size;i++)
            {
                JSONObject object = jsonCourses.getJSONObject(i);
                courseNames[i]=object.getString("courseName");
                stimestamps[i]= Time.valueOf(object.getString("courseStartTime"));
                etimestamps[i]= Time.valueOf(object.getString("courseEndTime"));
                days[i]=object.getString("days");
                courseIds[i] = object.getInt("course_id");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Course coursearray[] = new Course[size];
        for(int i=0;i<size;i++)
        {
            coursearray[i] = new Course(courseIds[i],courseNames[i],
                    stimestamps[i],etimestamps[i],days[i]);
            courses.add(coursearray[i]);
        }




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_student, container, false);
         courselist = (ListView) fragmentView.findViewById(R.id.courselist);


        courseAdapter = new CourseAdapter(getActivity(),android.R.layout.simple_list_item_1,courses);
        courselist.setAdapter(courseAdapter);

        courselist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //start attendance activity with selected course details and user type
                Course course = courses.get(position);

                mCallback.onCourseSelected(user,course);
            }
        });
        return fragmentView;
    }



    class GetStudentCourseData extends AsyncTask<Void, Void, JSONArray> {
        private final String id;

        GetStudentCourseData(String id)
        {
            this.id=id;
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            JSONArray returnJsonVal = null;

            List<NameValuePair> params1 = new ArrayList<NameValuePair>();

            params1.add(new BasicNameValuePair("id",id));

            returnJsonVal = JSONParser.makeHttpRequestArray(Constants.STUDENT_COURSES_URL, "POST", params1);

            return returnJsonVal;

        }

    }}

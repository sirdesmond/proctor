package com.team2.android.proctor.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.team2.android.proctor.R;
import com.team2.android.proctor.model.output.Course;

import java.util.ArrayList;

//import com.team2.android.proctor.model.Course;

/**
 * Created by Abhishek on 3/12/2016.
 */
public class CourseAdapter extends ArrayAdapter<Course> {

    public ArrayList<Course> courseList = new ArrayList<Course>();
    private static LayoutInflater mInflater;


    public CourseAdapter(Context context, int resource, ArrayList<Course> courselist) {
        super(context, resource);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //getSystemService(Context.LAYOUT_INFLATER_SERVICE)
        this.courseList = courselist;
    }

    public void addItem(final Course course) {
        courseList.add(course);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return courseList.size();
    }

    @Override
    public Course getItem(int position) {
        return courseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //System.out.println("my view"+position + " "+ convertView);
        View view= convertView;

        if(convertView ==null)
        {
            view = mInflater.inflate(R.layout.course_view,null);
        }

        TextView name = (TextView) view.findViewById(R.id.name);
        TextView timing = (TextView) view.findViewById(R.id.timing);
        TextView days = (TextView) view.findViewById(R.id.days);


        name.setText(courseList.get(position).getCourseName());
        timing.setText("Start:"+courseList.get(position).getformatedStartTime()+"\nEnd:"+courseList.get(position).getformatedEndTime());
        days.setText(courseList.get(position).getDays());

        return view;
    }
}

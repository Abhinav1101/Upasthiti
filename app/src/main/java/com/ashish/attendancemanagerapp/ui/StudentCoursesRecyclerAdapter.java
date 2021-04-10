package com.ashish.attendancemanagerapp.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashish.attendancemanagerapp.R;
import com.ashish.attendancemanagerapp.model.CourseInfo;

import java.util.List;

public class StudentCoursesRecyclerAdapter extends RecyclerView.Adapter<StudentCoursesRecyclerAdapter.ViewHolder> {

    private static final String TAG = "StudentCoursesRecyclerAdapter";
    private Context context;
    private List<CourseInfo> courseList;

    public StudentCoursesRecyclerAdapter(Context context, List<CourseInfo> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public StudentCoursesRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.row_student_course, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentCoursesRecyclerAdapter.ViewHolder viewHolder, int position) {
        CourseInfo courseInfo = courseList.get(position);
        String[] tokens = courseInfo.getCourseId().split(",");
        int percentage = Integer.parseInt(tokens[1]);
        if(percentage==111){
            percentage=100;
        }
        viewHolder.courseIdTextView.setText(tokens[0]);
        viewHolder.courseNameTextView.setText(courseInfo.getCourseName());
        viewHolder.courseDepartmentTextView.setText(courseInfo.getCourseDepartment());
        viewHolder.progressBar.setProgress(percentage);
        viewHolder.progressBarText.setText(percentage+"%");

    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView courseIdTextView, courseNameTextView, courseDepartmentTextView;
        TextView progressBarText;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);

            context = ctx;

            courseIdTextView = itemView.findViewById(R.id.rowStudentCourseIdTextView);
            if(courseIdTextView == null){
                Log.d(TAG, "NULL");
            }
            courseNameTextView = itemView.findViewById(R.id.rowStudentCourseNameTextView);
            courseDepartmentTextView = itemView.findViewById(R.id.rowStudentCourseDepartmentTextView);
            progressBar = itemView.findViewById(R.id.rowStudentCourseProgressBar);
            progressBarText = itemView.findViewById(R.id.rowStudentCourse_text_view_progress);
        }
    }
    public CourseInfo getAdapterPositionCourseInfo(int position) {
        if (position >= 0 && position<courseList.size()){
            return courseList.get(position);
        }
        return null;
    }
}

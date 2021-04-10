package com.ashish.attendancemanagerapp.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashish.attendancemanagerapp.R;
import com.ashish.attendancemanagerapp.model.ClassInfo;

import java.util.List;

public class TeacherRecycleAdapter extends RecyclerView.Adapter<TeacherRecycleAdapter.ViewHolder>{
    private Context context;
    private List<ClassInfo> classInfoList;


    public TeacherRecycleAdapter(Context context, List<ClassInfo> classInfoList) {
        this.context = context;
        this.classInfoList = classInfoList;
    }

    @NonNull
    @Override
    public TeacherRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.row_class_info, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherRecycleAdapter.ViewHolder viewHolder, int position) {
        ClassInfo classInfo = classInfoList.get(position);

        viewHolder.courseIdTextView.setText(classInfo.getCourseId() + " - " + classInfo.getCourseYear());
        viewHolder.courseNameTextView.setText(classInfo.getCourseName());
        viewHolder.courseDeptTextView.setText(classInfo.getCourseDepartment());
        viewHolder.courseTimingTextView.setText(classInfo.getCourseTiming());

    }

    @Override
    public int getItemCount() {
        return classInfoList.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView courseIdTextView, courseNameTextView, courseDeptTextView, courseTimingTextView;


        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);

            context = ctx;

            courseIdTextView = itemView.findViewById(R.id.row_classInfoCourseIdTextView);
            courseNameTextView = itemView.findViewById(R.id.row_classInfoNameTextView);
            courseDeptTextView = itemView.findViewById(R.id.row_classInfoCourseDeptTextView);
            courseTimingTextView = itemView.findViewById(R.id.row_classInfoTimingTextView);
        }
    }

    public ClassInfo getAdapterPositionClassInfo(int position) {
        if (position >= 0 && position<classInfoList.size()){
            return classInfoList.get(position);
        }
        return null;
    }
}

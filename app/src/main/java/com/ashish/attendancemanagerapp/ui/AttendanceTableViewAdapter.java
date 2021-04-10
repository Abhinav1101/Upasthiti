package com.ashish.attendancemanagerapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ashish.attendancemanagerapp.R;
import com.ashish.attendancemanagerapp.model.DateAttendanceInfo;

import java.util.List;

public class AttendanceTableViewAdapter extends RecyclerView.Adapter{


    List<DateAttendanceInfo> attendanceList;

    public AttendanceTableViewAdapter(List<DateAttendanceInfo> attendanceList) {
        this.attendanceList = attendanceList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.studentattendancelistitem ,parent, false);

        return new RowViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RowViewHolder rowViewHolder = (RowViewHolder) holder;
        int rowPos = rowViewHolder.getAdapterPosition();

        if (rowPos == 0) {
            // Header Cells. Main Headings appear here
            rowViewHolder.serial_numberTextView.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.dateTextView.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.timeDurationTextView.setBackgroundResource(R.drawable.table_header_cell_bg);
            rowViewHolder.presentTextView.setBackgroundResource(R.drawable.table_header_cell_bg);

            rowViewHolder.serial_numberTextView.setText(" S.No. ");
            rowViewHolder.dateTextView.setText(" Date ");
            rowViewHolder.timeDurationTextView.setText(" Duration ");
            rowViewHolder.presentTextView.setText("Present");

        } else {

            rowViewHolder.serial_numberTextView.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.dateTextView.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.timeDurationTextView.setBackgroundResource(R.drawable.table_content_cell_bg);
            rowViewHolder.presentTextView.setBackgroundResource(R.drawable.table_content_cell_bg);

            DateAttendanceInfo dateAttendanceInfo = attendanceList.get(rowPos-1);
            //Log.d("onBindViewHolder:", dateAttendanceInfo.getDate());
            //Log.d("onBindViewHolder:", dateAttendanceInfo.getTimeInterval());
            // Content Cells. Content appear here
            rowViewHolder.serial_numberTextView.setText(String.valueOf(rowPos));
            rowViewHolder.dateTextView.setText(dateAttendanceInfo.getDate());
            rowViewHolder.timeDurationTextView.setText(dateAttendanceInfo.getTimeInterval().charAt(0)+"");
            if(dateAttendanceInfo.getTimeInterval().charAt(1)=='0'){
                rowViewHolder.presentTextView.setText(" -- ");
            } else {
                rowViewHolder.presentTextView.setText(" P ");
            }

        }
    }

    @Override
    public int getItemCount() {
        return attendanceList.size()+1;
    }

    public class RowViewHolder extends RecyclerView.ViewHolder{
        protected TextView serial_numberTextView;
        protected TextView dateTextView;
        protected TextView timeDurationTextView, presentTextView;

        public RowViewHolder(View itemView) {
            super(itemView);

            serial_numberTextView = itemView.findViewById(R.id.serial_numberTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            timeDurationTextView = itemView.findViewById(R.id.timeDurationTextView);
            presentTextView = itemView.findViewById(R.id.row_studentAttendance_PresentTextView);

        }
    }

}

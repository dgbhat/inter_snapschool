package Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snapschool.mlps.R;

import java.util.List;

import DataModels.AttendanceInfo;

/**
 * Created by snapschool on 12/29/15.
 * attendance list adapter
 */
public class AttendanceListAdapter extends BaseAdapter{
    private Context context;
    private List<AttendanceInfo> attendanceList;
    public AttendanceListAdapter(Context context, List<AttendanceInfo> attendanceList) {
        this.context = context;
        this.attendanceList = attendanceList;
    }

    @Override
    public int getCount() {
        return attendanceList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return attendanceList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.attendance_list_item, parent, false);
        }
        if (position == 0)
            convertView.setBackgroundColor(Color.rgb(231,231,239));
        else
            convertView.setBackgroundColor(Color.TRANSPARENT);
        TextView rollText = (TextView)convertView.findViewById(R.id.roll_textView);
        TextView studentNameText = (TextView)convertView.findViewById(R.id.studentName_textView);
        TextView statusText = (TextView)convertView.findViewById(R.id.status_textView);
        rollText.setText(attendanceList.get(position).getStudentRoll());
        studentNameText.setText(attendanceList.get(position).getStudentName());
        String status = attendanceList.get(position).getStatus();
        switch (status) {
            case "1":
                statusText.setTextColor(Color.GREEN);
                String presentText = "Present";
                statusText.setText(presentText);
                break;
            case "2":
                statusText.setTextColor(Color.RED);
                String absentText = "Absent";
                statusText.setText(absentText);
                break;
            case "0":
                status = "";
                statusText.setText(status);
                break;
            default:
                break;

        }
        return convertView;
    }
}

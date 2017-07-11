package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snapschool.mlps.R;

import java.util.List;

import DataModels.ClassRoutineInfo;

/**
 * Created by snapschool on 12/28/15.
 * class routine list adapter
 */
public class ClassRoutineListAdapter extends BaseAdapter{
    private Context context;
    private List<ClassRoutineInfo> classesOfDay;
    public ClassRoutineListAdapter(Context context, List<ClassRoutineInfo> classesOfDay) {
        this.context = context;
        this.classesOfDay = classesOfDay;
    }

    @Override
    public int getCount() {
        return classesOfDay.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return classesOfDay.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.class_routine_list_item, parent, false);
        }
        TextView startTimeText = (TextView)convertView.findViewById(R.id.startTime_textView);
        TextView endTimeText = (TextView)convertView.findViewById(R.id.endTime_textView);
        TextView subjectNameText = (TextView)convertView.findViewById(R.id.subjectName_textView);

        String PM = "";
        String startTime;
        Integer startHour = Integer.parseInt(classesOfDay.get(position).getStartHour());
        if (startHour > 12) {
            startHour -= 12;
            PM = "PM";
        }
        startTime = String.valueOf(startHour) + ":" + classesOfDay.get(position).getStartMinute() + PM;
        startTimeText.setText(startTime);

        PM = "";
        String endTime;
        Integer endHour = Integer.parseInt(classesOfDay.get(position).getEndHour());
        if (endHour > 12) {
            endHour -= 12;
            PM = "PM";
        }
        endTime = String.valueOf(endHour) + ":" + classesOfDay.get(position).getEndMinute() + PM;
        endTimeText.setText(endTime);

        subjectNameText.setText(classesOfDay.get(position).getSubjectName());

        return convertView;
    }
}

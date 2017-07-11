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

import DataModels.MarkInfo;

/**
 * Created by snapschool on 1/5/16.
 * exam mark list adapter
 */
public class ExamMarksListAdapter extends BaseAdapter{
    private Context context;
    private List<MarkInfo> markList;
    public ExamMarksListAdapter(Context context, List<MarkInfo> markList) {
        this.context = context;
        this.markList = markList;
    }

    @Override
    public int getCount() {
        return markList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return markList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.marks_list_item, parent, false);
        }
        TextView rollText = (TextView)convertView.findViewById(R.id.roll_textView);
        TextView nameText = (TextView)convertView.findViewById(R.id.student_name_textView);
        TextView markText = (TextView)convertView.findViewById(R.id.marks_textView);
        if (position == 0) {
            convertView.setBackgroundColor(Color.rgb(231, 231, 239));
        }
        else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }
        rollText.setText(markList.get(position).getStudentRoll());
        nameText.setText(markList.get(position).getStudentName());
        markText.setText(markList.get(position).getMark());
        return convertView;
    }
}

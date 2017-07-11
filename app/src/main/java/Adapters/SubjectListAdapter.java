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

import DataModels.SubjectInfo;

/**
 * Created by snapschool on 12/28/15.
 * subject list adapter class
 */
public class SubjectListAdapter extends BaseAdapter {
    private List<SubjectInfo> subjectList;
    private Context context;
    public SubjectListAdapter(Context context, List<SubjectInfo> subjectList) {
        this.context = context;
        this.subjectList = subjectList;
    }

    @Override
    public int getCount() {
        return subjectList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return subjectList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.subject_list_item, parent, false);
        }
        TextView subjectNameText = (TextView)convertView.findViewById(R.id.subjectName_textView);
        TextView teacherNameText = (TextView)convertView.findViewById(R.id.teacherName_textView);
        if (position == 0)
            convertView.setBackgroundColor(Color.rgb(231, 231, 239));
        else
        convertView.setBackgroundColor(Color.TRANSPARENT);
        subjectNameText.setText(subjectList.get(position).getSubjectName());
        teacherNameText.setText(subjectList.get(position).getTeacherName());
        return convertView;
    }
}

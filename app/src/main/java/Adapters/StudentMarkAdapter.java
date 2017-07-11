package Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.snapschool.mlps.R;

import java.util.HashMap;
import java.util.List;

import DataModels.StudentMarkInfo;

/**
 * Created by snapschool on 12/25/15.
 * student marks adapter
 */
public class StudentMarkAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> examNames;
    private HashMap<String, List<StudentMarkInfo>> marks;
    public StudentMarkAdapter(Context context, List<String> examNames, HashMap<String, List<StudentMarkInfo>> marks) {
        this.context = context;
        this.examNames = examNames;
        this.marks = marks;
    }

    @Override
    public int getGroupCount() {
        return examNames.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return examNames.get(groupPosition);
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        TextView examNameText = (TextView)convertView.findViewById(android.R.id.text1);
        examNameText.setTextColor(Color.BLACK);
        examNameText.setText(examNames.get(groupPosition));
        convertView.setBackgroundColor(Color.rgb(231, 231, 239));
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return marks.get(examNames.get(groupPosition)).size();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return marks.get(examNames.get(groupPosition)).get(childPosition);
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.student_mark_item, parent, false);
        }
        TextView subjectNameText = (TextView)convertView.findViewById(R.id.subject_textView);
        subjectNameText.setText(marks.get(examNames.get(groupPosition)).get(childPosition).getSubjectName());
        TextView markText = (TextView)convertView.findViewById(R.id.marks_textView);
        markText.setText(marks.get(examNames.get(groupPosition)).get(childPosition).getMark());
        TextView gradeText = (TextView)convertView.findViewById(R.id.grade_textView);
        gradeText.setText(marks.get(examNames.get(groupPosition)).get(childPosition).getGrade());
        if (childPosition == 0)
            convertView.setBackgroundColor(Color.rgb(231, 231, 239));
        else
            convertView.setBackgroundColor(Color.rgb(250, 250, 250));
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}

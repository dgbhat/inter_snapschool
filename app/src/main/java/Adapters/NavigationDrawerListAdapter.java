package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapschool.mlps.R;
import java.util.HashMap;
import java.util.List;


/**
 * Created by snapschool on 12/13/15.
 * navigation menu list adapter
 */
public class NavigationDrawerListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> headerTitles;
    private HashMap<String , List<String>> childTitles;

    public NavigationDrawerListAdapter(Context context, List<String> headerTitles, HashMap<String , List<String>> childTitles) {
        this.context = context;
        this.headerTitles = headerTitles;
        this.childTitles = childTitles;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.childTitles.get(this.headerTitles.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String childTitle = (String )getChild(groupPosition, childPosition);
        childTitle = "    " + childTitle;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.navigation_drawer_item, parent, false);
        }
        TextView childTitleText = (TextView)convertView.findViewById(R.id.title);
        childTitleText.setText(childTitle);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.childTitles.get(this.headerTitles.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.headerTitles.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.headerTitles.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String)getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.navigation_drawer_item, parent, false);
        }
        TextView headerTitleText = (TextView)convertView.findViewById(R.id.title);
        headerTitleText.setText(headerTitle);
        ImageView iconView = (ImageView)convertView.findViewById(R.id.icon);
        switch (headerTitle) {
            case "Home":
                iconView.setImageResource(R.drawable.home);
                break;
            case "Student":
                iconView.setImageResource(R.drawable.student);
                break;
            case "Teacher":
                iconView.setImageResource(R.drawable.teacher);
                break;
            case "Parent":
                iconView.setImageResource(R.drawable.parent);
                break;
            case "Subject":
                iconView.setImageResource(R.drawable.subject);
                break;
            case "Class Routine":
                iconView.setImageResource(R.drawable.class_routine);
                break;
            case "Daily Attendance":
                iconView.setImageResource(R.drawable.attendance);
                break;
            case "Exam Marks":
                iconView.setImageResource(R.drawable.exam);
                break;
            case "Noticeboard":
                iconView.setImageResource(R.drawable.noticeboard);
                break;
            case "Accounting":
                iconView.setImageResource(R.drawable.accounting);
                break;
            case "Profile":
                iconView.setImageResource(R.drawable.profile);
                break;
            case "Log out":
                iconView.setImageResource(R.drawable.logout);
                break;
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

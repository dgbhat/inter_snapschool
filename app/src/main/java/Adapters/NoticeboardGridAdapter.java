package Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.snapschool.mlps.R;

import java.util.List;

/**
 * Created by snapschool on 1/8/16.
 * noticeboard grid view adapter
 */
public class NoticeboardGridAdapter extends BaseAdapter {
    private Context context;
    private List<String> dates;
    private List<Boolean> hasEvent;
    public NoticeboardGridAdapter(Context context, List<String> dates, List<Boolean> hasEvent) {
        this.context = context;
        this.dates = dates;
        this.hasEvent = hasEvent;
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.noticeboard_grid_item, parent, false);
        }
        Button dateButton = (Button)convertView.findViewById(R.id.date_button);
        dateButton.setText(dates.get(position));
        if (hasEvent.get(position))
            dateButton.setBackgroundColor(Color.rgb(208,210,212));
        else
            dateButton.setBackgroundColor(Color.TRANSPARENT);
        dateButton.setTag(position);
        return convertView;
    }
}

package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snapschool.mlps.R;

import java.util.List;

/**
 * Created by snapschool on 1/28/16.
 * selector list adapter
 */
public class SelectionListAdapter extends BaseAdapter {
    private Context context;
    private List<String> listItemTitles;
    public SelectionListAdapter(Context context, List<String> listItemTitles) {
        this.context = context;
        this.listItemTitles = listItemTitles;
    }

    @Override
    public int getCount() {
        return listItemTitles.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return listItemTitles.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.selector_row_item, parent, false);
        }
        TextView listItemTitleView = (TextView)convertView.findViewById(R.id.selector_item_texiView);
        listItemTitleView.setText(listItemTitles.get(position).toUpperCase());
        return convertView;
    }
}

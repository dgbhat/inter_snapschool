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

/**
 * Created by snapschool on 12/25/15.
 * student profile information adapter
 */
public class StudentProfileAdapter extends BaseAdapter {
    private Context context;
    private List<String> captions;
    private List<String> details;
    public StudentProfileAdapter(Context context, List<String> captions, List<String> details) {
        this.context = context;
        this.captions = captions;
        this.details = details;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return captions.get(position);
    }

    @Override
    public int getCount() {
        return captions.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.user_profile_item, parent, false);
        }
        TextView captionText = (TextView)convertView.findViewById(R.id.caption_textView);
        captionText.setText(captions.get(position));
        TextView detailsText = (TextView)convertView.findViewById(R.id.details_textView);
        detailsText.setTag(captions.get(position));
        if (captions.get(position).equals("Email") || captions.get(position).equals("Phone")) {
            detailsText.setTextColor(Color.BLUE);
        }
        detailsText.setText(details.get(position));
        return convertView;
    }
}

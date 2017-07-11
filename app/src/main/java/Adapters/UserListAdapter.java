package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snapschool.mlps.R;
import java.util.List;

import DataModels.UserInfo;

/**
 * Created by snapschool on 12/23/15.
 * User list adapter
 */
public class UserListAdapter extends BaseAdapter {
    private Context context;
    private List<UserInfo> userList;
    public UserListAdapter(Context context, List<UserInfo> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return userList.get(position);
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.user_list_item, parent, false);
        }
        TextView userNameText = (TextView)convertView.findViewById(R.id.username_textView);
        userNameText.setText(userList.get(position).getUserName());
        if (userList.get(position).getImage() != null) {
            ImageView imageView = (ImageView)convertView.findViewById(R.id.user_imageView);
            imageView.setImageBitmap(userList.get(position).getImage());
        }
        return convertView;
    }
}

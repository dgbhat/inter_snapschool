package Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.snapschool.mlps.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import DataModels.AccountingInfo;

/**
 * Created by snapschool on 1/25/16.
 * student accounting info list adapter
 */
public class StudentAccountingListAdapter extends BaseAdapter {
    private List<AccountingInfo> studentAccountingInfoList;
    private Context context;
    public StudentAccountingListAdapter(Context context, List<AccountingInfo> studentAccountingInfoList) {
        this.context = context;
        this.studentAccountingInfoList = studentAccountingInfoList;
    }

    @Override
    public int getCount() {
        return studentAccountingInfoList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return studentAccountingInfoList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.student_accounting_list_item, parent, false);
        }
        if (position == 0)
            convertView.setBackgroundColor(Color.rgb(231, 231, 239));
        else
            convertView.setBackgroundColor(Color.TRANSPARENT);
        TextView titleText = (TextView)convertView.findViewById(R.id.title_textView);
        titleText.setText(studentAccountingInfoList.get(position).getTitle());
        TextView amountText = (TextView)convertView.findViewById(R.id.amount_textView);
        amountText.setText(studentAccountingInfoList.get(position).getAmount());
        TextView dueAmountText = (TextView)convertView.findViewById(R.id.due_textView);
        dueAmountText.setText(studentAccountingInfoList.get(position).getDueAmount());
        TextView dateText = (TextView)convertView.findViewById(R.id.date_textView);
        if (position == 0)
            dateText.setText(studentAccountingInfoList.get(position).getTimestamp());
        else {
            long timeStamp = Long.parseLong(studentAccountingInfoList.get(position).getTimestamp()) * 1000;
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Date eventDate = new Date(timeStamp);
            String date = dateFormat.format(eventDate);
            dateText.setText(date);
        }
        return convertView;
    }
}

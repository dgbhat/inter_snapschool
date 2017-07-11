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
 * Created by snapschool on 1/7/16.
 * accounting list adapter
 */
public class AccountingListAdapter extends BaseAdapter {
    private Context context;
    private List<AccountingInfo> accountingInfoList;
    public AccountingListAdapter(Context context, List<AccountingInfo> accountingInfoList) {
        this.context = context;
        this.accountingInfoList = accountingInfoList;
    }

    @Override
    public int getCount() {
        return accountingInfoList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return accountingInfoList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.accounting_list_item, parent, false);
        }
        if (position == 0)
            convertView.setBackgroundColor(Color.rgb(231, 231, 239));
        else
            convertView.setBackgroundColor(Color.TRANSPARENT);
        TextView titleText = (TextView)convertView.findViewById(R.id.title_textView);
        titleText.setText(accountingInfoList.get(position).getTitle());
        TextView amountText = (TextView)convertView.findViewById(R.id.amount_textView);
        amountText.setText(accountingInfoList.get(position).getAmount());
        TextView dateText = (TextView)convertView.findViewById(R.id.date_textView);
        if (position == 0)
            dateText.setText(accountingInfoList.get(position).getTimestamp());
        else {
            long timeStamp = Long.parseLong(accountingInfoList.get(position).getTimestamp()) * 1000;
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
            Date eventDate = new Date(timeStamp);
            String date = dateFormat.format(eventDate);
            dateText.setText(date);
        }
        return convertView;
    }
}

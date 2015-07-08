package cesi.com.tchatapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cesi.com.tchatapp.R;
import cesi.com.tchatapp.helper.DateHelper;
import cesi.com.tchatapp.model.Message;
import cesi.com.tchatapp.utils.Constants;

/**
 * Created by sca on 02/06/15.
 */
public class MessagesAdapter extends BaseAdapter {

    private final Context context;

    public MessagesAdapter(Context ctx){
        this.context = ctx;
    }

    List<Message> messages = new LinkedList<>();
    public void addMessage(List<Message> messages){
        this.messages = messages;
        this.notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        if(messages == null){
            return 0;
        }
        return messages.size();
    }

    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if(convertView==null){
            // inflate the layout
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(R.layout.item_message, parent, false);
            vh = new ViewHolder();
            vh.username = (TextView) convertView.findViewById(R.id.msg_user);
            vh.message = (TextView) convertView.findViewById(R.id.msg_message);
            vh.date = (TextView) convertView.findViewById(R.id.msg_date);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        vh.username.setText(getItem(position).getUsername());
        vh.message.setText(getItem(position).getMsg());
        try {
            vh.date.setText(DateHelper.getFormattedDate(getItem(position).getDate()));
        } catch (ParseException e) {
            Log.e(Constants.TAG, "error parsing date",e);
        }

        return convertView;
    }
    private class ViewHolder{
        TextView username;
        TextView message;
        TextView date;
    }
}

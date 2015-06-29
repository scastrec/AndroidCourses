package cesi.com.tchatapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cesi.com.tchatapp.R;
import cesi.com.tchatapp.model.User;

/**
 * Created by sca on 07/06/15.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    List<User> users;
    Context context;

    public UserAdapter(Context ctx){
        this.context = ctx;
    }

    public void setUser(List<User> users){
        this.users = users;
        notifyDataSetChanged();
    }


    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View convertView = inflater.inflate(R.layout.item_user, parent, false);
        ViewHolder vh = new ViewHolder(convertView);
        return vh;
    }

    @Override
    public void onBindViewHolder(UserAdapter.ViewHolder vh, int position) {
        vh.username.setText(users.get(position).getUsername());
    }

    @Override
    public int getItemCount() {
        if(users == null){
            return 0;
        }
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView username;

        public ViewHolder(final View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.user_name);
        }
    }
}

package cesi.com.notes.adapter;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.util.List;

import cesi.com.notes.R;
import cesi.com.notes.helper.DateHelper;
import cesi.com.notes.model.User;

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
        if(users.get(position).getUrlPhoto() != null) {
            Picasso.with(context).load(users.get(position).getUrlPhoto()).resize(50, 50)
                    .centerCrop()
                    .error(R.drawable.ic_account_circle_black_48dp)
                    .into(vh.img);
        } else {
            vh.img.setImageResource(R.drawable.ic_account_circle_black_48dp);
        }

        try {
            vh.date.setText(DateHelper.getFormattedDate(users.get(position).getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
        TextView date;
        ImageView img;

        public ViewHolder(final View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.user_name);
            date = (TextView) itemView.findViewById(R.id.user_date);
            img = (ImageView) itemView.findViewById(R.id.user_img);
        }
    }
}

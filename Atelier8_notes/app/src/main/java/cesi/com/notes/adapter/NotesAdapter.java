package cesi.com.notes.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import cesi.com.notes.R;
import cesi.com.notes.helper.DateHelper;
import cesi.com.notes.model.Note;

/**
 * Created by sca on 02/06/15.
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

  private final Context context;
    private final CompoundButton.OnCheckedChangeListener listener;

  public NotesAdapter(Context ctx, CompoundButton.OnCheckedChangeListener listener) {
    this.context = ctx;
      this.listener = listener;
  }

  List<Note> notes = new LinkedList<Note>();

  public void addNotes(List<Note> notes) {
    this.notes = notes;
    this.notifyDataSetChanged();
  }

  @Override
  public NotesAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, final int i) {
    LayoutInflater inflater = ((Activity) context).getLayoutInflater();
    View convertView = inflater.inflate(R.layout.item_note, parent, false);
    ViewHolder vh = new ViewHolder(convertView);
    return vh;
  }

  @Override
  public void onBindViewHolder(final NotesAdapter.ViewHolder vh, final int position) {
    vh.username.setText(notes.get(position).getUsername());
    vh.message.setText(notes.get(position).getMsg());
    vh.done.setChecked(notes.get(position).isDone());
      vh.done.setOnCheckedChangeListener(listener);
      vh.done.setTag(position);
    try {
      vh.date.setText(DateHelper.getFormattedDate(notes.get(position).getDate()));
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public int getItemCount() {
    if (notes == null) {
      return 0;
    }
    return notes.size();
  }

    public Note getItem(Integer position) {
        return notes.get(position);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
    TextView username;
    TextView message;
    TextView date;
      CheckBox done;

    public ViewHolder(final View itemView) {
      super(itemView);
      username = (TextView) itemView.findViewById(R.id.note_user);
      message = (TextView) itemView.findViewById(R.id.note_message);
      date = (TextView) itemView.findViewById(R.id.note_date);
      done = (CheckBox) itemView.findViewById(R.id.not_check);
    }
  }
}

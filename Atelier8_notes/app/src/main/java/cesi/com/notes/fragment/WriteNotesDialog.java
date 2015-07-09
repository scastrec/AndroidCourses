package cesi.com.notes.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import cesi.com.notes.R;
import cesi.com.notes.utils.Constants;

/**
 * Created by sca on 04/06/15.
 */
public class WriteNotesDialog extends DialogFragment {

    private EditText note;

    public static WriteNotesDialog getInstance(final String token) {
        WriteNotesDialog f = new WriteNotesDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putString("token", token);
        f.setArguments(args);

        return f;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        final LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View view = inflater.inflate(R.layout.dialog_note, null);
        note = (EditText) view.findViewById(R.id.note_msg);

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.validate, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        closeKeyboard();
                        if (!note.getText().toString().isEmpty()) {
                            //post note
                            new SendNoteAsyncTask(view.getContext()).execute(note.getText().toString());
                        } else {
                            note.setError(WriteNotesDialog.this.getActivity()
                                    .getString(R.string.error_missing_msg));
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        closeKeyboard();
                        WriteNotesDialog.this.dismiss();
                    }
                }

        );
        return builder.create();
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(note.getWindowToken(), 0);
    }

    /**
     * AsyncTask for sign-in
     */
    protected class SendNoteAsyncTask extends AsyncTask<String, Void, Integer> {

        Context context;

        public SendNoteAsyncTask(final Context context) {
            this.context = context;
        }

        @Override
        protected Integer doInBackground(String... params) {
            try {
                //then create an httpClient.
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(URI.create(context.getString(R.string.url_notes)));
                request.setHeader("token", getArguments().getString("token"));

                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                pairs.add(new BasicNameValuePair("note", params[0]));
                //set entity
                request.setEntity(new UrlEncodedFormEntity(pairs));

                // do request.
                HttpResponse httpResponse = client.execute(request);

                Log.d(Constants.TAG, "received for url: " + request.getURI() + " return code: " + httpResponse
                        .getStatusLine()
                        .getStatusCode());

                return httpResponse
                        .getStatusLine()
                        .getStatusCode();
            } catch (Exception e) {
                Log.d(Constants.TAG, "Error occured in your AsyncTask : ", e);
                return 500;
            }
        }

        @Override
        public void onPostExecute(Integer status) {
            if (status != 200) {
                Toast.makeText(context, context.getString(R.string.error_send_msg), Toast.LENGTH_SHORT).show();
            }else {
                WriteNotesDialog.this.dismiss();
            }
        }
    }
}

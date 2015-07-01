package cesi.com.tchatapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cesi.com.tchatapp.adapter.MessagesAdapter;
import cesi.com.tchatapp.helper.JsonParser;
import cesi.com.tchatapp.helper.NetworkHelper;
import cesi.com.tchatapp.model.Message;
import cesi.com.tchatapp.utils.Constants;

/**
 * Created by sca on 02/06/15.
 */
public class TchatActivity extends ActionBarActivity {

    private static final long TIME_POLLING = 2000;
    ListView listView;
    EditText msgToSend;
    MessagesAdapter adapter;

    String token;

    List<Message> messages;

    Timer timer;
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            try {
                //then create an httpClient.
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(URI.create(TchatActivity.this.getString(R.string.url_msg)));
                request.setHeader("token", token);
                // do request.
                HttpResponse httpResponse = client.execute(request);
                String response = null;

                //Store response
                if (httpResponse.getEntity() != null) {
                    response = EntityUtils.toString(httpResponse.getEntity());
                }
                Log.d(Constants.TAG, "received for url: " + request.getURI() + " return code: " + httpResponse
                        .getStatusLine()
                        .getStatusCode());
                if (httpResponse.getStatusLine().getStatusCode() != 200) {
                    //error happened
                }
                messages = JsonParser.getMessages(response);
                mHandler.obtainMessage(1).sendToTarget();
            } catch (Exception e) {
                Log.d(Constants.TAG, "Error occured in your AsyncTask : ", e);
            }

        }
    };

    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                adapter.addMessage(messages);
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_tchat);
        token = this.getIntent().getExtras().getString(Constants.INTENT_TOKEN);
        if (token == null) {
            Toast.makeText(this, this.getString(R.string.error_no_token), Toast.LENGTH_SHORT).show();
            finish();
        }
        msgToSend = (EditText) findViewById(R.id.tchat_msg);
        findViewById(R.id.tchat_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (msgToSend.getText().toString().isEmpty()) {
                    msgToSend.setError(v.getContext().getString(R.string.error_missing_msg));
                    return;
                }
                new SendMessageAsyncTask(v.getContext()).execute(msgToSend.getText().toString());
                msgToSend.setText("");
            }
        });

        listView = (ListView) findViewById(R.id.tchat_list);
        adapter = new MessagesAdapter(this);
        listView.setAdapter(adapter);

        //start polling
        timer = new Timer();
        // first start in 500 ms, then update every TIME_POLLING
        timer.schedule(task, 500, TIME_POLLING);
    }

    /**
     * AsyncTask for sign-in
     */
    protected class SendMessageAsyncTask extends AsyncTask<String, Void, Integer> {

        Context context;

        public SendMessageAsyncTask(final Context context) {
            this.context = context;
        }

        @Override
        protected Integer doInBackground(String... params) {
            if (!NetworkHelper.isInternetAvailable(context)) {
                return null;
            }

            try {
                //then create an httpClient.
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(URI.create(context.getString(R.string.url_msg)));
                request.setHeader("token", token);

                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                pairs.add(new BasicNameValuePair("message", params[0]));
                //set entity
                request.setEntity(new UrlEncodedFormEntity(pairs));

                // do request.
                HttpResponse httpResponse = client.execute(request);
                String response = null;

                //Store response
                if (httpResponse.getEntity() != null) {
                    response = EntityUtils.toString(httpResponse.getEntity());
                }
                Log.d(Constants.TAG, "received for url: " + request.getURI() + " return code: " + httpResponse
                        .getStatusLine()
                        .getStatusCode());

                return httpResponse
                        .getStatusLine()
                        .getStatusCode();
            } catch (Exception e) {
                Log.d(Constants.TAG, "Error occured in your AsyncTask : ", e);
                return null;
            }
        }

        @Override
        public void onPostExecute(Integer status) {
            if (status != 200) {
                Toast.makeText(context, context.getString(R.string.error_send_msg), Toast.LENGTH_SHORT).show();
            }
        }
    }
}

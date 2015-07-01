package cesi.com.tchatapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cesi.com.tchatapp.adapter.MessagesAdapter;
import cesi.com.tchatapp.WriteMsgDialog;
import cesi.com.tchatapp.helper.JsonParser;
import cesi.com.tchatapp.model.Message;
import cesi.com.tchatapp.utils.Constants;

/**
 * Created by sca on 02/06/15.
 */
public class TchatActivity extends ActionBarActivity {

    private static final long TIME_POLLING = 2000;
    RecyclerView listView;
    EditText msgToSend;
    FloatingActionButton fab;
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

    private LinearLayoutManager mLayoutManager;
    private Toolbar mToolbar;

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_tchat);
        token = this.getIntent().getExtras().getString(Constants.INTENT_TOKEN);
        if (token == null) {
            Toast.makeText(this, this.getString(R.string.error_no_token), Toast.LENGTH_SHORT).show();
            finish();
        }
        listView = (RecyclerView) findViewById(R.id.tchat_list);
        listView.setHasFixedSize(true);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        listView.setLayoutManager(mLayoutManager);

        mToolbar = (Toolbar) findViewById(R.id.tchat_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WriteMsgDialog.getInstance(token).show(TchatActivity.this.getFragmentManager(), "write");

            }
        });

        msgToSend = (EditText) findViewById(R.id.tchat_msg);

        adapter = new MessagesAdapter(this);
        listView.setAdapter(adapter);

        //start polling
        timer = new Timer();
        // first start in 500 ms, then update every TIME_POLLING
        timer.schedule(task, 500, TIME_POLLING);
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tchat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.tchat_refresh) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

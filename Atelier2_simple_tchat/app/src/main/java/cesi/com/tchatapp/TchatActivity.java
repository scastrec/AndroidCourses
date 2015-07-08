package cesi.com.tchatapp;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

import cesi.com.tchatapp.adapter.MessagesAdapter;
import cesi.com.tchatapp.fragment.WriteMsgFragment;
import cesi.com.tchatapp.helper.JsonParser;
import cesi.com.tchatapp.helper.NetworkHelper;
import cesi.com.tchatapp.model.Message;
import cesi.com.tchatapp.utils.Constants;

/**
 * Created by sca on 02/06/15.
 */
public class TchatActivity extends ActionBarActivity {

    ListView listView;
    MessagesAdapter adapter;

    String token;


    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_tchat);
        token = this.getIntent().getExtras().getString(Constants.INTENT_TOKEN);
        if(token == null){
            Toast.makeText(this, this.getString(R.string.error_no_token), Toast.LENGTH_SHORT).show();
            finish();
        }
        listView = (ListView) findViewById(R.id.tchat_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"horacio.gonzales@gmail.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Android");
                intent.putExtra(Intent.EXTRA_TEXT, adapter.getItem(position).getMsg());
                intent.setType("text/plain");

                //Intent create chooser, creates a popin that displays available apps.
                // Second param is this popin title
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });

        findViewById(R.id.tchat_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WriteMsgFragment.getInstance(token).show(TchatActivity.this.getSupportFragmentManager(),
                        WriteMsgFragment.class.toString());
            }
        });
        adapter = new MessagesAdapter(this);
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.tchat_empty));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tchat, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.tchat_refresh:
                new GetMessagesAsyncTask(this).execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }





    /**
     * AsyncTask for sign-in
     */
    protected class GetMessagesAsyncTask extends AsyncTask<String, Void, List<Message>> {

        Context context;

        public GetMessagesAsyncTask(final Context context) {
            this.context = context;
        }

        @Override
        protected List<Message> doInBackground(String... params) {
            if(!NetworkHelper.isInternetAvailable(context)){
                return null;
            }

            try {
                //then create an httpClient.
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(URI.create(context.getString(R.string.url_msg)));
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
                if(httpResponse.getStatusLine().getStatusCode() != 200){
                    //error happened
                    return null;
                }
                return JsonParser.getMessages(response);
            } catch (Exception e){
                Log.d(Constants.TAG, "Error occured in your AsyncTask : ", e);
                return null;
            }
        }

        @Override
        public void onPostExecute(final List<Message> msgs){
            int nb = 0;
            if(msgs != null){
                nb = msgs.size();
            }
            Toast.makeText(TchatActivity.this, "loaded nb messages: "+nb, Toast.LENGTH_LONG).show();
            adapter.addMessage(msgs);
        }
    }
}

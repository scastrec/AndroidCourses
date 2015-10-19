package cesi.com.tchatapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
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
import cesi.com.tchatapp.helper.NetworkHelper;
import cesi.com.tchatapp.model.Message;
import cesi.com.tchatapp.utils.Constants;

/**
 * Created by sca on 02/06/15.
 */
public class TchatActivity extends ActionBarActivity {

    RecyclerView listView;
    FloatingActionButton fab;
    MessagesAdapter adapter;

    String token;

    List<Message> messages;

    private LinearLayoutManager mLayoutManager;
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;

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
                WriteMsgDialog d = WriteMsgDialog.getInstance(token);
                d.show(TchatActivity.this.getFragmentManager(), "write");

            }
        });

        adapter = new MessagesAdapter(this);
        listView.setAdapter(adapter);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupNavigationView(navigationView);
        }

        new GetMessagesAsyncTask().execute();
    }

    /**
     * setup drawer.
     * @param navigationView
     */
    private void setupNavigationView(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        if(menuItem.getItemId() == R.id.tchat_disconnect){
                            //TODO  Your turn
                        } if(menuItem.getItemId() == R.id.tchat_users){

                        } else if(menuItem.getItemId() == R.id.tchat_tchat) {
                        } else {
                            menuItem.setChecked(true);
                            mDrawerLayout.closeDrawers();
                        }
                        return true;
                    }
                });
    }

    /**
     * AsyncTask for sign-in
     */
    protected class GetMessagesAsyncTask extends AsyncTask<String, Void, List<Message>> {


        @Override
        protected List<Message> doInBackground(String... params) {
            if(!NetworkHelper.isInternetAvailable(TchatActivity.this)){
                return null;
            }

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
            if(msgs != null) {
                adapter.addMessage(msgs);
            }
            //swipeLayout.setRefreshing(false);
        }
    }


}

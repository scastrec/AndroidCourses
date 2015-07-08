package cesi.com.tchatapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import cesi.com.tchatapp.helper.JsonParser;
import cesi.com.tchatapp.helper.NetworkHelper;
import cesi.com.tchatapp.utils.Constants;

/**
 * Created by sca on 08/07/15.
 */
public class UsersActivity extends Activity {

    private ListView list;
    private ArrayAdapter<String> adapter;
    String token = null;
    private SwipeRefreshLayout swipeLayout;

    @Override
    public void onCreate(Bundle savedInstace){
        super.onCreate(savedInstace);
        setContentView(R.layout.activity_users);
        token = getIntent().getExtras().getString(Constants.INTENT_TOKEN);
        list = (ListView) findViewById(R.id.list);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, new LinkedList<String>());
        list.setAdapter(adapter);
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.users_swiperefresh);
        setupRefreshLayout();
    }


    @Override
    public void onResume(){
        super.onResume();
        loading();
    }
    private void loading() {
        swipeLayout.setRefreshing(true);
        new GetUsersAsyncTask().execute();
    }

    /**
     * Setup refresh layout
     */
    private void setupRefreshLayout() {
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loading();
            }
        });
        swipeLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimaryDark, R.color.colorPrimary);
        /**
         * this is to avoid error on double scroll on listview/swipeRefreshLayout
         */
        list.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if (list != null && list.getChildCount() > 0) {
                    // check if the first item of the list is visible
                    boolean firstItemVisible = list.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = list.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeLayout.setEnabled(enable);
            }
        });
    }

    /**
     * AsyncTask for sign-in
     */
    protected class GetUsersAsyncTask extends AsyncTask<String, Void, List<String>> {

        @Override
        protected List<String> doInBackground(String... params) {
            if(!NetworkHelper.isInternetAvailable(UsersActivity.this)){
                return null;
            }

            try {
                //then create an httpClient.
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(URI.create(UsersActivity.this.getString(R.string.url_users)));
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
                return JsonParser.getUsers(response);
            } catch (Exception e){
                Log.d(Constants.TAG, "Error occured in your AsyncTask : ", e);
                return null;
            }
        }

        @Override
        public void onPostExecute(final List<String> users){
            if (users != null){
                adapter.clear();
                adapter.addAll(users);
            }

            adapter.notifyDataSetChanged();
            swipeLayout.setRefreshing(false);
        }
    }
}

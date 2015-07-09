package cesi.com.notes.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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

import cesi.com.notes.R;
import cesi.com.notes.adapter.NotesAdapter;
import cesi.com.notes.helper.JsonParser;
import cesi.com.notes.helper.NetworkHelper;
import cesi.com.notes.model.Note;
import cesi.com.notes.session.Session;
import cesi.com.notes.utils.Constants;

/**
 * Created by sca on 06/06/15.
 */
public class NotesFragment extends Fragment {

    //UI
    SwipeRefreshLayout swipeLayout;
    RecyclerView recyclerView;
    NotesAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(
                R.layout.fragment_notes, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.messages_list);
        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.messages_swiperefresh);
        setupRefreshLayout();
        setupRecyclerView();
        return v;
    }

    @Override
    public void onResume(){
        super.onResume();
        loading();
    }

    /**
     * Load messages
     */
    private void loading() {
        swipeLayout.setRefreshing(true);
        new GetNoteAsyncTask(NotesFragment.this.getActivity()).execute();
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
    }

    /**
     * Setup recycler view.
     */
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        adapter = new NotesAdapter(this.getActivity(), new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Integer position = (Integer) buttonView.getTag();
                new CheckAsyncTask(adapter.getItem(position).getId()).execute(isChecked);
            }
        });
        recyclerView.setAdapter(adapter);
        
        // Add this. 
        // Two scroller could have problem. 
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView view, int scrollState) {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                swipeLayout.setEnabled(topRowVerticalPosition >= 0);
            }
        });
    }

    /**
     * AsyncTask for sign-in
     */
    protected class CheckAsyncTask extends AsyncTask<Boolean, Void, Integer> {

        String id;
        public CheckAsyncTask(String id) {
            this.id = id;
        }

        @Override
        protected Integer doInBackground(Boolean... params) {
            if (!NetworkHelper.isInternetAvailable(NotesFragment.this.getActivity())) {
                return null;
            }
            try {
                //then create an httpClient.
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(URI.create(NotesFragment.this.getString(R.string.url_notes_update, id)));
                request.setHeader("token", Session.token);

                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                pairs.add(new BasicNameValuePair("done", params[0].toString()));
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
                Toast.makeText(NotesFragment.this.getActivity(),
                        NotesFragment.this.getActivity().getString(R.string.error_send_msg),
                        Toast.LENGTH_SHORT).show();
            }else {
                Snackbar.make(swipeLayout, "Checked done", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    /**
     * AsyncTask for sign-in
     */
    protected class GetNoteAsyncTask extends AsyncTask<String, Void, List<Note>> {

        Context context;

        public GetNoteAsyncTask(final Context context) {
            this.context = context;
        }

        @Override
        protected List<Note> doInBackground(String... params) {
            if(!NetworkHelper.isInternetAvailable(context)){
                return null;
            }

            try {
                //then create an httpClient.
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(URI.create(context.getString(R.string.url_notes)));
                request.setHeader("token", Session.token);
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
                return JsonParser.getNotes(response);
            } catch (Exception e){
                Log.d(Constants.TAG, "Error occured in your AsyncTask : ", e);
                return null;
            }
        }

        @Override
        public void onPostExecute(final List<Note> msgs){
            if(msgs != null) {
                adapter.addNotes(msgs);
            }
            swipeLayout.setRefreshing(false);
        }
    }
}

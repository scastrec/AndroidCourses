package cesi.com.notes.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import java.net.URI;
import java.util.List;

import cesi.com.notes.R;
import cesi.com.notes.adapter.UserAdapter;
import cesi.com.notes.helper.JsonParser;
import cesi.com.notes.helper.NetworkHelper;
import cesi.com.notes.model.HttpResult;
import cesi.com.notes.model.User;
import cesi.com.notes.session.Session;
import cesi.com.notes.utils.Constants;

/**
 * Created by sca on 06/06/15.
 */
public class UsersFragment extends Fragment {

    //UI
    SwipeRefreshLayout swipeLayout;
    RecyclerView recyclerView;
    UserAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(
                R.layout.fragment_users, container, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.users_list);
        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.users_swiperefresh);
        setupRefreshLayout();
        setupRecyclerView();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        loading();
    }

    /**
     * Load messages
     */
    private void loading() {
        swipeLayout.setRefreshing(true);
        new GetUsersAsyncTask(UsersFragment.this.getActivity()).execute();
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
        adapter = new UserAdapter(this.getActivity());
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
    protected class GetUsersAsyncTask extends AsyncTask<String, Void, List<User>> {

        private final Context context;

        public GetUsersAsyncTask(Context ctx){
            this.context = ctx;
        }

        @Override
        protected List<User> doInBackground(String... params) {
            if (!NetworkHelper.isInternetAvailable(context)) {
                return null;
            }
            try {
                HttpResult result = NetworkHelper.doGet(context.getString(R.string.url_users), null, Session.token);

                if(result.code != 200){
                    //error happened
                    return null;
                }
                return JsonParser.getUsers(result.json);
            } catch (Exception e) {
                Log.d(Constants.TAG, "Error occured in your AsyncTask : ", e);
                return null;
            }
        }

        @Override
        public void onPostExecute(final List<User> users) {
            if (users != null) {
                adapter.setUser(users);
            }
            swipeLayout.setRefreshing(false);
        }
    }
}

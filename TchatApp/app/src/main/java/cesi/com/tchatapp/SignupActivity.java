package cesi.com.tchatapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import cesi.com.tchatapp.helper.NetworkHelper;
import cesi.com.tchatapp.utils.Constants;

/**
 * Created by sca on 02/06/15.
 */
public class SignupActivity extends Activity {

    EditText username;
    EditText pwd;
    ProgressBar pg;
    Button btn;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_signup);
        username = (EditText) findViewById(R.id.signup_username);
        pwd = (EditText) findViewById(R.id.signup_pwd);
        pg = (ProgressBar) findViewById(R.id.signup_pg);

        btn = (Button) findViewById(R.id.signup_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading(true);
                new SignupAsyncTask(v.getContext()).execute();
            }
        });
    }

    private void loading(boolean loading) {
        if(loading){
            pg.setVisibility(View.VISIBLE);
            btn.setVisibility(View.INVISIBLE);
        } else {
            pg.setVisibility(View.INVISIBLE);
            btn.setVisibility(View.VISIBLE);
        }
    }

    /**
     * AsyncTask for sign-in
     */
    protected class SignupAsyncTask extends AsyncTask<Void, Void, String> {

        Context context;

        public SignupAsyncTask(final Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            if(!NetworkHelper.isInternetAvailable(context)){
                return "Internet not available";
            }

            try {
                //then create an httpClient.
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(URI.create(context.getString(R.string.url_signup)));

                List<NameValuePair> pairs = new ArrayList<NameValuePair>();
                pairs.add(new BasicNameValuePair("username", username.getText().toString()));
                pairs.add(new BasicNameValuePair("pwd", pwd.getText().toString()));
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
                if(httpResponse.getStatusLine().getStatusCode() != 200){
                    //error happened
                    return null;
                }
                return response;
            } catch (Exception e){
                Log.d(Constants.TAG, "Error occured in your AsyncTask : ", e);
                return "an error occured";
            }
        }

        @Override
        public void onPostExecute(final String token){
            loading(false);
            if(token != null){
                SignupActivity.this.finish();
            } else {
                Toast.makeText(context, context.getString(R.string.error_signup), Toast.LENGTH_LONG).show();
            }
        }
    }
}

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

import cesi.com.tchatapp.helper.JsonParser;
import cesi.com.tchatapp.helper.NetworkHelper;
import cesi.com.tchatapp.session.Session;
import cesi.com.tchatapp.utils.Constants;
import cesi.com.tchatapp.utils.PreferenceHelper;

/**
 * Created by sca on 02/06/15.
 */
public class SigninActivity extends Activity {

    EditText username;
    EditText pwd;
    ProgressBar pg;
    Button btn;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_signin);
        username = (EditText) findViewById(R.id.signin_username);
        username.setText(PreferenceHelper.getValue(this, PreferenceHelper.LOGIN));
        pwd = (EditText) findViewById(R.id.signin_pwd);
        pg = (ProgressBar) findViewById(R.id.signin_pg);
        btn = (Button) findViewById(R.id.signin_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().toString().isEmpty()){
                    username.setError(SigninActivity.this.getString(R.string.error_missing_login));
                    return;
                }
                if(pwd.getText().toString().isEmpty()){
                    pwd.setError(SigninActivity.this.getString(R.string.error_missing_pwd));
                    return;
                }
                loading(true);
                new SigninAsyncTask(v.getContext()).execute();
            }
        });
        findViewById(R.id.signin_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), SignupActivity.class);
                startActivity(i);
            }
        });

    }

    private void loading(boolean loading) {
        if(loading){
            pg.setVisibility(View.VISIBLE);
            btn.setVisibility(View.INVISIBLE);
            username.setEnabled(false);
            pwd.setEnabled(false);
        } else {
            pg.setVisibility(View.INVISIBLE);
            btn.setVisibility(View.VISIBLE);
            username.setEnabled(true);
            pwd.setEnabled(true);
        }
    }

    /**
     * AsyncTask for sign-in
     */
    protected class SigninAsyncTask extends AsyncTask<Void, Void, String>{

        Context context;

        public SigninAsyncTask(final Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {
            if(!NetworkHelper.isInternetAvailable(context)){
                return null;
            }

            try {
                //then create an httpClient.
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(URI.create(context.getString(R.string.url_signin)));

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
                if(httpResponse
                        .getStatusLine()
                        .getStatusCode() == 200) {
                    return JsonParser.getToken(response);
                }
                return null;
            } catch (Exception e){
                Log.d(Constants.TAG, "Error occured in your AsyncTask : ", e);
                return null;
            }
        }

        @Override
        public void onPostExecute(final String token){
            loading(false);
            if(token != null){
                //save login
                PreferenceHelper.setValue(SigninActivity.this, PreferenceHelper.LOGIN,
                        username.getText().toString());
                Session.token = token;
                Intent i = new Intent(context, DrawerActivity.class);
                i.putExtra(Constants.INTENT_TOKEN, token);
                startActivity(i);
            } else {
                Toast.makeText(context, context.getString(R.string.error_login), Toast.LENGTH_LONG).show();
            }
        }
    }
}

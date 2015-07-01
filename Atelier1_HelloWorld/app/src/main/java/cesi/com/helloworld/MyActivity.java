package cesi.com.helloworld;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.net.URI;

import cesi.com.helloworld.helper.NetworkHelper;

/**
 * Created by sca on 29/05/15.
 */
public class MyActivity extends Activity{

    TextView textView;
    EditText editText;
    Button button;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);

        setContentView(R.layout.activity_hello);
        textView = (TextView)findViewById(R.id.texview);
        editText = (EditText)findViewById(R.id.edittext);
        ((Button)findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do my request
                displayProgressDialog();
                new HelloAsyncTask(v.getContext()).execute(editText.getText().toString());
            }
        });


    }

    /**
     * display a Progress Dialog.
     */
    private void displayProgressDialog() {
        progressDialog =  new ProgressDialog(MyActivity.this);

        progressDialog.setTitle("Loading ...");
        progressDialog.setMessage("hello in progress ...");
        progressDialog.show();
    }

    /**
     * Method to close progress dialog.
     */
    private void hideProgressDialog() {
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        } else {
            Log.w("HelloWorld", "trying to close Progress dialog that is not exist or opened");
        }
    }

    public class HelloAsyncTask extends AsyncTask<String, Void, String>{

        Context context;

        public HelloAsyncTask(final Context context){
            this.context = context;

        }


        @Override
        protected String doInBackground(String... params) {
            if(!NetworkHelper.isInternetAvailable(context)){
                return "Internet not available";
            }

            try {
                //then create an httpClient.
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(URI.create(context.getString(R.string.url_hello)+"?name="+params[0]));

                // do request.
                HttpResponse httpResponse = client.execute(request);
                String response = null;

                //Store response
                if (httpResponse.getEntity() != null) {
                    response = EntityUtils.toString(httpResponse.getEntity());
                }

                Log.d("HelloWorld", "received for url: " + request.getURI() + " return code: " + httpResponse
                        .getStatusLine()
                        .getStatusCode());
                return response;
            } catch (Exception e){
                Log.d("HelloWorld", "Error occured in your AsyncTask : ", e);
                return "an error occured";
            }
        }

        @Override
        protected void onPostExecute(final String s) {
            hideProgressDialog();
            textView.setText(s);
        }
    }
}

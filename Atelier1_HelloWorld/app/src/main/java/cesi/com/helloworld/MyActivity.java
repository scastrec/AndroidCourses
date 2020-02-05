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
import android.widget.Toast;

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
                new HelloAsyncTask().execute(editText.getText().toString());
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

        @Override
        protected String doInBackground(String... params) {
            if(!NetworkHelper.isInternetAvailable(MyActivity.this)){
                return "Internet not available";
            }
            return NetworkHelper.connect(params[0]);
        }

        @Override
        protected void onPostExecute(final String s) {
            hideProgressDialog();
            Toast.makeText(MyActivity.this, "Response : " + s, Toast.LENGTH_LONG).show();
            textView.setText(s);
        }
    }
}

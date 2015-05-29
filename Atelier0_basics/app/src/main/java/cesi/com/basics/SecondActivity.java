package cesi.com.basics;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by sca on 27/05/15.
 */
public class SecondActivity extends Activity {


    @Override
    public void onCreate(final Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.secondactivity);

        //get intent data
        String text = this.getIntent().getStringExtra("VALUE");
        ((TextView)findViewById(R.id.textview)).setText(text);
    }
}

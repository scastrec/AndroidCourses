package hello.cesi.com.projectwebview;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


public class MainActivity2Activity extends ActionBarActivity {

    WebView wv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wv = (WebView) findViewById(R.id.webview);
        wv.setWebViewClient(new WebViewClient());
        //enable chrome://inspect for remote debug
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        wv.getSettings().setJavaScriptEnabled(true);
        wv.setWebChromeClient(new WebChromeClient());
        wv.loadUrl("file:///android_asset/www/page.html", null);

        //enable javascript to call Java
        wv.addJavascriptInterface(new MyJSInterface(this), "Android");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_previous) {
            String js = "javascript:hello('Stephane')";
            wv.loadUrl(js, null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class MyJSInterface {

        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        MyJSInterface(final Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void helloJava() {
            runOnUiThread(new Runnable() {

                public void run() {
                    Toast.makeText(mContext, "I've been called from JS", Toast.LENGTH_LONG).show();
                }
            });
        }

    }
}

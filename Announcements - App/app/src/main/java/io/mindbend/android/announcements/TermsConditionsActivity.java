package io.mindbend.android.announcements;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

public class TermsConditionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.activity_terms_conditions, null);
        setContentView(view);

        String termsAndConditionsUrl = getString(R.string.terms_and_conditions_URL);
        WebView webView = (WebView)findViewById(R.id.webView);
        //webView.getSettings().setJavaScriptEnabled(true);
        if (!App.hasNetworkConnection(this)){
            Snackbar.make(view, getString(R.string.no_network_connection), Snackbar.LENGTH_SHORT).show();
        } else {
            webView.loadUrl(termsAndConditionsUrl);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_terms_conditions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

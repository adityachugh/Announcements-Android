package io.mindbend.android.announcements;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.ParseUser;

import io.mindbend.android.announcements.onboardingAndSignupin.OnboardingActivity;

public class MoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        LinearLayout reportBug = (LinearLayout)findViewById(R.id.report_bug);
        reportBug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: email intent to hello@mindbend.io, until support is working
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, getResources().getStringArray(R.array.report_bug_emails));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.report_bug_email_subject));
                emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.report_bug_email_text));

                try {
                    startActivity(Intent.createChooser(emailIntent, "Email bug report..."));
                    finish();
                    Log.i("Finished sending email", "");
                }
                catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MoreActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        LinearLayout reviewApp = (LinearLayout)findViewById(R.id.review_app);
        reviewApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String appPackageName= getPackageName();
                Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
                marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET|Intent.FLAG_ACTIVITY_MULTIPLE_TASK| Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(marketIntent);
            }
        });


        LinearLayout signOut = (LinearLayout)findViewById(R.id.more_sign_out);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: sign out user
//                ParseUser.logOut();
                //sign out of parse, then...

                Intent i = new Intent(MoreActivity.this, OnboardingActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more, menu);
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

package io.mindbend.android.announcements;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.rebound.BaseSpringSystem;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.facebook.rebound.SpringUtil;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import io.mindbend.android.announcements.onboardingAndSignupin.OnboardingActivity;

public class MoreActivity extends AppCompatActivity {

    private static final String TAG = "MoreActivity";

    private final BaseSpringSystem mSpringSystem = SpringSystem.create();
    private final ExampleSpringListener mSpringListener = new ExampleSpringListener();
    private FrameLayout mRootView;
    private Spring mScaleSpring;
    private View mLogo;
    ProgressBar mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        //progress bar grabbing
        mLoading = (ProgressBar)findViewById(R.id.more_progressbar);

        LinearLayout reportBug = (LinearLayout)findViewById(R.id.report_bug);
        reportBug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                mLoading.setVisibility(View.VISIBLE);
                ParseUser.logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        //sign out of parse, then...
                        mLoading.setVisibility(View.GONE);
                        Intent i = new Intent(MoreActivity.this, OnboardingActivity.class);
                        startActivity(i);
                    }
                });
            }
        });

        //***REBOUND ANIMATION***
        mRootView = (FrameLayout) findViewById(R.id.rebound_framelayout);
        mLogo = (ImageView) findViewById(R.id.more_app_logo);

        // Create the animation spring.
        mScaleSpring = mSpringSystem.createSpring();

        // Add an OnTouchListener to the root view.
        mRootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // When pressed start solving the spring to 1.
                        mScaleSpring.setEndValue(1);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // When released start solving the spring to 0.
                        mScaleSpring.setEndValue(0);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        // Add a listener to the spring when the Activity resumes.
        mScaleSpring.addListener(mSpringListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Remove the listener to the spring when the Activity pauses.
        mScaleSpring.removeListener(mSpringListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more, menu);
        return true;
    }

    private class ExampleSpringListener extends SimpleSpringListener {
        @Override
        public void onSpringUpdate(Spring spring) {
            // On each update of the spring value, we adjust the scale of the image view to match the
            // springs new value. We use the SpringUtil linear interpolation function mapValueFromRangeToRange
            // to translate the spring's 0 to 1 scale to a 100% to 50% scale range and apply that to the View
            // with setScaleX/Y. Note that rendering is an implementation detail of the application and not
            // Rebound itself. If you need Gingerbread compatibility consider using NineOldAndroids to update
            // your view properties in a backwards compatible manner.
            float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(spring.getCurrentValue(), 0, 1, 1, 0.5);
            mLogo.setScaleX(mappedValue);
            mLogo.setScaleY(mappedValue);
        }
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

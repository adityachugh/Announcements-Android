package io.mindbend.android.announcements.onboardingAndSignupin;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import io.mindbend.android.announcements.App;
import io.mindbend.android.announcements.R;


public class OnboardingActivity extends ActionBarActivity {

    //Tells SignInUp activity which button has been clicked
    public static final String EXTRA = "WhichButton";

    public static final String SIGNINTAG = "Sign in";
    public static final String SIGNUPTAG = "Sign up";
    private static final String EXTRA_FOR_TRANSITION = "TRANSITION_OF_APP_LOGO_AND_TITLE";
    private LinearLayout mAppLogoAndTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        //get sign in and up buttons
        Button signInButton = (Button) findViewById(R.id.sign_in_button_onboarding);
        Button signUpButton = (Button) findViewById(R.id.sign_up_button_onboarding);

        //button colour backgrounds if under API 21 (default tint will not work)
        //NOTE: TINT DOES NOT WORK ON API 21! Background coloured instead.
        if (!App.isLollipopOrHigher) {
            signInButton.setBackgroundColor(getResources().getColor(android.R.color.white));
            signUpButton.setBackgroundColor(getResources().getColor(R.color.accent));
        }

        //grab the linear layout with the app logo and app title in order to run the transition
        mAppLogoAndTitle = (LinearLayout)findViewById(R.id.onboarding_app_logo_and_title);

        //sign in and up buttons go to "SignInUpActivity"
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OnboardingActivity.this, SignInUpActivity.class);
                //Tells SignInUp which button has been clicked
                i.putExtra(EXTRA, SIGNINTAG);
                startSignInUpActivity(i); //custom method used to include transition if API 21 or higher
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OnboardingActivity.this, SignInUpActivity.class);
                i.putExtra(EXTRA, SIGNUPTAG);
                startSignInUpActivity(i); //custom method used to include transition if API 21 or higher
            }
        });


    }

    private void startSignInUpActivity(Intent i) {
        if (App.isKitkatOrHigher) {
            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(OnboardingActivity.this, mAppLogoAndTitle, getString(R.string.onboaring_to_signup_animation));
            startActivity(i, options.toBundle());
        }
        else {
            startActivity(i);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_onboarding, menu);
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

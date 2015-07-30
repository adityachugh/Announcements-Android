package io.mindbend.android.announcements;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class SignInUpActivity extends ActionBarActivity {
    //Fragments used in this Activity
    private SignInFragment mSignInFragment;
    private SignUpFragment mSignUpFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_up);

        //lets us know which button has been pressed.
        Intent intent = getIntent();
        String whichButton = intent.getStringExtra(OnboardingActivity.EXTRA);

        //put SignInFragment in frag container if "SIGN IN" pressed
        if (whichButton.equals(OnboardingActivity.SIGNINTAG)){
            mSignInFragment = (SignInFragment)getFragmentManager().findFragmentById(R.id.fragment_container);
            //create if null
            if (mSignInFragment == null){
                mSignInFragment = new SignInFragment();

                getFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, mSignInFragment)
                        .commit();
            }
        }

        //put SignUpFragment in frag container if "SIGN UP" pressed
        if (whichButton.equals(OnboardingActivity.SIGNUPTAG)){
            mSignUpFragment = (SignUpFragment)getFragmentManager().findFragmentById(R.id.fragment_container);
            //create if null
            if (mSignUpFragment == null){
                mSignUpFragment = new SignUpFragment();

                getFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, mSignUpFragment)
                        .commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_in_up, menu);
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

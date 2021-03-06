package io.mindbend.android.announcements.onboardingAndSignupin;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.io.Serializable;

import io.mindbend.android.announcements.R;


public class SignInUpActivity extends ActionBarActivity implements Serializable{
    //Fragments used in this Activity
    private SignInFragment mSignInFragment;
    private SignUpFragment mSignUpFragment;
    private String mWhichButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_up);

        //lets us know which button has been pressed.
        Intent intent = getIntent();
        mWhichButton = intent.getStringExtra(OnboardingActivity.EXTRA);

        //put SignInFragment in frag container if "SIGN IN" pressed
        if (mWhichButton.equals(OnboardingActivity.SIGNINTAG)){
            mSignInFragment = (SignInFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            //create if null
            if (mSignInFragment == null){
                mSignInFragment = new SignInFragment();
                //inflate frag
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if (ft.isEmpty())
                    ft.add(R.id.fragment_container, mSignInFragment).commitAllowingStateLoss();
            }
        }

        //put SignUpFragment in frag container if "SIGN UP" pressed
        if (mWhichButton.equals(OnboardingActivity.SIGNUPTAG)){
            mSignUpFragment = (SignUpFragment)getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            //create if null
            if (mSignUpFragment == null){
                mSignUpFragment = new SignUpFragment();
                //inflate frag
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if (ft.isEmpty())
                    ft.add(R.id.fragment_container, mSignUpFragment).commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mWhichButton.equals(OnboardingActivity.SIGNINTAG) && (!mSignInFragment.isVisible())){
            mSignInFragment.getFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
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
        switch (id){
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                supportFinishAfterTransition();
        }

        return super.onOptionsItemSelected(item);
    }
}

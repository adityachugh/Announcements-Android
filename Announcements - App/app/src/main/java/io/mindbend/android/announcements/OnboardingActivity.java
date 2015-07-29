package io.mindbend.android.announcements;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


public class OnboardingActivity extends ActionBarActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        //get sign in and up buttons; colour backgrounds if under API 21 (default tint will not work)
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            Button signInButton = (Button)findViewById(R.id.signin_button);
            Button signUpButton = (Button)findViewById(R.id.signup_button);

            signInButton.setBackgroundColor(getResources().getColor(R.color.text_tertiary));
            signUpButton.setBackgroundColor(getResources().getColor(R.color.accent));
        }
//test button to go to tabbed activity
//        Button testButton = (Button)findViewById(R.id.test_button);
//        testButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(OnboardingActivity.this, TabbedActivity.class);
//                startActivity(i);
//            }
//        });

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

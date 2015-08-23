package io.mindbend.android.announcements.onboardingAndSignupin;


import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.io.Serializable;

import io.mindbend.android.announcements.App;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.TabbedActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment
        implements Serializable{

    private static final String TAG = "SignInFragment";

    public static final String SIGN_IN_FRAG = "sign_in_frag";

    private ForgotPasswordFragment mForgotPasswordFragment;

    private ProgressBar mLoading;


    public SignInFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        //Stop keyboard from automatically popping up
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_in, container, false);

        mLoading = (ProgressBar)v.findViewById(R.id.signin_progressbar);

        //Fetch Button "Sign In"
        Button signInButton = (Button) v.findViewById(R.id.sign_in_button);
        final TextView forgotPassword = (TextView)v.findViewById(R.id.sign_in_forgot_password);


        //button colour background if under API 21 (default tint will not work)
        //NOTE: TINT DOES NOT WORK ON API 21! Background coloured instead.
        if (!App.isAPI22OrHigher) {
            signInButton.setBackgroundColor(getResources().getColor(R.color.accent));
        }

        final EditText username = (EditText)v.findViewById(R.id.sign_in_username);
        final EditText password = (EditText)v.findViewById(R.id.sign_in_password);

        //Move to tabbed activity once user logs in
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().equals("") || password.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "Please enter your username and password", Toast.LENGTH_SHORT).show();
                } else {
                    mLoading.setVisibility(View.VISIBLE);
                    ParseUser.logInInBackground(username.getText().toString(), password.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser parseUser, ParseException e) {
                            mLoading.setVisibility(View.GONE);
                            if (e == null){
                                //login successful!
                                Intent i = new Intent(getActivity(), TabbedActivity.class);
                                startActivity(i);
                            } else {
                                //incorrect credentials!
                                AlertDialog.Builder failedLogin = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
                                failedLogin.setTitle(getResources().getString(R.string.incorrect_login_credentials))
                                        .setPositiveButton("OK", null);
                                failedLogin.show();
                            }
                        }
                    });
                }
            }
        });

        //Move to ForgotPasswordFragment
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "forgot pass clicked");

                //Create fragment if null
                if (mForgotPasswordFragment == null){
                    mForgotPasswordFragment = new ForgotPasswordFragment();
                }

                getFragmentManager().beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.fragment_container, mForgotPasswordFragment)
                        .addToBackStack(SIGN_IN_FRAG)
                        .commitAllowingStateLoss();
            }
        });

        return v;

    }


}

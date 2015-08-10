package io.mindbend.android.announcements.onboardingAndSignupin;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.parse.Parse;

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

        //Fetch Button "Sign In"
        Button signInButton = (Button) v.findViewById(R.id.sign_in_button);

        final TextView forgotPassword = (TextView)v.findViewById(R.id.sign_in_forgot_password);

        //button colour background if under API 21 (default tint will not work)
        //NOTE: TINT DOES NOT WORK ON API 21! Background coloured instead.
        if (!App.isAPI22OrHigher) {
            signInButton.setBackgroundColor(getResources().getColor(R.color.accent));
        }

        //Move to tabbed activity once user logs in
        //TODO: sign in authentication
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), TabbedActivity.class);
                startActivity(i);
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
                        .replace(R.id.fragment_container, mForgotPasswordFragment)
                        .addToBackStack(SIGN_IN_FRAG)
                        .commit();
            }
        });

        return v;

    }


}

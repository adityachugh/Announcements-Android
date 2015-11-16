package io.mindbend.android.announcements.onboardingAndSignupin;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseCloud;

import java.io.Serializable;

import io.mindbend.android.announcements.App;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.TermsConditionsActivity;
import io.mindbend.android.announcements.cloudCode.VerificationDataSource;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements Serializable {
    private EditText mFirstName, mLastName, mUsername, mEmail, mPassword;
    ProgressBar mLoader;

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setRetainInstance(true);

        //Stop keyboard from automatically popping up
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        View v =  inflater.inflate(R.layout.fragment_sign_up, container, false);

        TextView termsAndConditions = (TextView)v.findViewById(R.id.terms_and_conditions_signup);
        termsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to terms and conditions website activity
                Intent i = new Intent(getActivity(), TermsConditionsActivity.class);
                startActivity(i);
            }
        });

        mLoader = (ProgressBar)v.findViewById(R.id.sign_up_progressbar);

        //Fetch Button "Sign Up"
        Button signUpButton = (Button) v.findViewById(R.id.sign_up_button);

        //button colour background if under API 21 (default tint will not work)
        //NOTE: TINT DOES NOT WORK ON API 21! Background coloured instead.
        if (!App.isAPI22OrHigher) {
            signUpButton.setBackgroundColor(getResources().getColor(R.color.accent));
        }

        //setting up all the edittexts
        mFirstName = (EditText)v.findViewById(R.id.sign_up_first_name);
        mLastName = (EditText)v.findViewById(R.id.sign_up_last_name);
        mUsername = (EditText)v.findViewById(R.id.sign_up_username);
        mEmail = (EditText)v.findViewById(R.id.sign_up_email);
        mPassword = (EditText)v.findViewById(R.id.sign_up_password);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = mFirstName.getText().toString();
                String lastName = mLastName.getText().toString();
                String username = mUsername.getText().toString();
                username = username.replace(" ", "");
                username = username.toLowerCase();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if(firstName.equals("") || lastName.equals("") || username.equals("") || email.equals("") || password.equals(""))
                    Snackbar.make(v, "Please fill in all fields.", Snackbar.LENGTH_SHORT).show();
                else {
                    //check if fields in use
                    VerificationDataSource.signupUser(v, getActivity(), firstName, lastName, password, username, email);
                }
            }
        });

        return v;
    }


}

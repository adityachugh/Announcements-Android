package io.mindbend.android.announcements.onboardingAndSignupin;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.TabbedActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {


    private ForgotPasswordFragment mForgotPasswordFragment;


    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_in, container, false);

        //Fetch Button "Sign In"
        Button signInButton = (Button) v.findViewById(R.id.sign_in_button);

        TextView forgotPassword = (TextView)v.findViewById(R.id.sign_in_forgot_password);

        //button colour background if under API 21 (default tint will not work)
        //NOTE: TINT DOES NOT WORK ON API 21! Background coloured instead.
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) {
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
                //Create fragment if null
                if (mForgotPasswordFragment == null){
                    mForgotPasswordFragment = new ForgotPasswordFragment();
                }
            }
        });

        return v;

    }


}

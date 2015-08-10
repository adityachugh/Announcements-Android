package io.mindbend.android.announcements.onboardingAndSignupin;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import java.io.Serializable;

import io.mindbend.android.announcements.App;
import io.mindbend.android.announcements.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements Serializable {


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

        //Fetch Button "Sign Up"
        Button signUpButton = (Button) v.findViewById(R.id.sign_up_button);

        //button colour background if under API 21 (default tint will not work)
        //NOTE: TINT DOES NOT WORK ON API 21! Background coloured instead.
        if (!App.isAPI22OrHigher) {
            signUpButton.setBackgroundColor(getResources().getColor(R.color.accent));
        }

        return v;
    }


}

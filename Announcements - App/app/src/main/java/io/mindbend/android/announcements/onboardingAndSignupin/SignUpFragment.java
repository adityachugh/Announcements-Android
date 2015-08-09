package io.mindbend.android.announcements.onboardingAndSignupin;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;

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
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }


}

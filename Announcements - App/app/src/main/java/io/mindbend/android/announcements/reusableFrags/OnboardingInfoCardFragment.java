package io.mindbend.android.announcements.reusableFrags;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.Serializable;

import io.mindbend.android.announcements.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OnboardingInfoCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OnboardingInfoCardFragment extends Fragment implements Serializable{

    private static final String ARG_TITLE = "title";
    private static final String ARG_TEXT = "text";

    private String mTitle;
    private String mText;

    public static OnboardingInfoCardFragment newInstance(String cardTitle, String cardText) {
        OnboardingInfoCardFragment fragment = new OnboardingInfoCardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, cardTitle);
        args.putString(ARG_TEXT, cardText);
        fragment.setArguments(args);
        return fragment;
    }

    public OnboardingInfoCardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTitle = getArguments().getString(ARG_TITLE);
            mText = getArguments().getString(ARG_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_onboarding_info_card, container, false);

        TextView titleText = (TextView)v.findViewById(R.id.info_card_title);
        TextView infoText = (TextView)v.findViewById(R.id.info_card_text);

        titleText.setText(mTitle);
        infoText.setText(mText);

        return v;
    }


}

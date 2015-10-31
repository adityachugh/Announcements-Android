package io.mindbend.android.announcements.reusableFrags;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private static final String ARG_IMAGE_PATH = "image";

    private String mTitle;
    private String mText;
    private int mImagePath;

    public static OnboardingInfoCardFragment newInstance(String cardTitle, String cardText, int imagePath) {
        OnboardingInfoCardFragment fragment = new OnboardingInfoCardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, cardTitle);
        args.putInt(ARG_IMAGE_PATH, imagePath);
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
            mImagePath = getArguments().getInt(ARG_IMAGE_PATH);
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

        ImageView image = (ImageView)v.findViewById(R.id.info_card_image);
        image.setImageResource(mImagePath);

        return v;
    }


}

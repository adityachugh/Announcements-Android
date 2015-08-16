package io.mindbend.android.announcements.adminClasses;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;

public class NewAnnouncementFragment extends Fragment implements DatePickerDialog.OnDateSetListener {
    private static final String ARG_ORGANIZATION = "org";
    public static final int ADD_PHOTO = 2;
    private Organization mOrg;

    //various elements of the newA frag
    private byte[] mImageBytes;
    private EditText mTitle;
    private EditText mBody;

    private LinearLayout mStartDateSelector;
    private TextView mStartDateTV;
    private Date mStartDate;

    private boolean selectingStartDate = false;

    private LinearLayout mEndDateSelector;
    private TextView mEndDateTV;
    private Date mEndDate;

    private Switch mAllowComments;
    private Switch mOrgToNotify;

    public static NewAnnouncementFragment newInstance(Organization posterOrg) {
        NewAnnouncementFragment fragment = new NewAnnouncementFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ORGANIZATION, posterOrg);
        fragment.setArguments(args);
        return fragment;
    }

    public NewAnnouncementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mOrg = (Organization)getArguments().getSerializable(ARG_ORGANIZATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_new_announcement, container, false);

        setupView(v);

        ImageButton uploadFab = (ImageButton)v.findViewById(R.id.upload_announcement_fab);
        uploadFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: check all fields
            }
        });

        return v;
    }

    private void setupView(View v) {
        mTitle = (EditText)v.findViewById(R.id.newA_title_ET);
        mBody = (EditText)v.findViewById(R.id.newA_body_ET);

        mStartDateTV = (TextView)v.findViewById(R.id.newA_start_date_TV);

        mStartDateTV.setText(dateToString(new Date()));
        mStartDateSelector = (LinearLayout)v.findViewById(R.id.newA_start_date_field);
        mStartDateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectingStartDate = true;
                // dialogue box to change today date
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date()); //new Date gets the current date and time

                //instantiate the date picker dialog and implement the onDateSet method (it is implemented by the today frag)
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme, NewAnnouncementFragment.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        mEndDateTV = (TextView)v.findViewById(R.id.newA_end_date_TV);
        mEndDateTV.setText(dateToString(new Date()));
        mEndDateSelector = (LinearLayout)v.findViewById(R.id.newA_end_date_field);
        mEndDateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectingStartDate = false;
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date()); //new Date gets the current date and time

                //instantiate the date picker dialog and implement the onDateSet method (it is implemented by the today frag)
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme, NewAnnouncementFragment.this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        LinearLayout imageSelector = (LinearLayout)v.findViewById(R.id.newA_image_field);
        imageSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("Image", "image selection begun");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                getActivity().startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), ADD_PHOTO);
            }
        });

        mAllowComments = (Switch)v.findViewById(R.id.newA_allow_comments_SWITCH);
        mOrgToNotify = (Switch)v.findViewById(R.id.newA_post_in_parent_SWITCH);
        mOrgToNotify.setTextOff(mOrg.getTitle()); //TODO: GET PARENT TYPE
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private String dateToString (Date date){
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
        return df.format(date);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Date date = new Date(year, monthOfYear, dayOfMonth);
        if (selectingStartDate){
            mStartDate = date;
            mStartDateTV.setText(dateToString(date));
        }
        else {
            mEndDate = date;
            mEndDateTV.setText(dateToString(date));
        }
    }

    public void setmImageBytes(byte[] mImageBytes) {
        this.mImageBytes = mImageBytes;
    }
}

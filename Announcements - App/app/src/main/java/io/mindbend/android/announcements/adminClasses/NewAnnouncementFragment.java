package io.mindbend.android.announcements.adminClasses;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.parse.FunctionCallback;
import com.parse.ParseException;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.mindbend.android.announcements.Organization;
import io.mindbend.android.announcements.R;
import io.mindbend.android.announcements.cloudCode.PostsDataSource;

public class NewAnnouncementFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
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

    private Switch mNotifyParent;
    private View mView;
    private RadioGroup mRadioGroup;

    private ProgressBar mLoading;

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
        mView = inflater.inflate(R.layout.fragment_new_announcement, container, false);

        setupView(mView);

        ImageButton uploadFab = (ImageButton) mView.findViewById(R.id.upload_announcement_fab);
        uploadFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check text
                if (mTitle.getText().toString().equals("") || mBody.getText().toString().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
                    builder.setMessage("Cannot leave title or body of announcement empty!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //nothing
                                }
                            });
                    AlertDialog alertDialog = builder.show();
                }
                //checking dates
                else if (!areDatesAppropriate()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
                    builder.setMessage("The end date for an announcement must be after the start date")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //nothing
                                }
                            });
                    AlertDialog alertDialog = builder.show();
                }

                //everything's good
                else {
                    String title = mTitle.getText().toString();
                    String body = mBody.getText().toString();
                    mRadioGroup = (RadioGroup) mView.findViewById(R.id.newA_priority_group);
                    PostsDataSource.uploadPostForOrganization(mView, mLoading, mOrg.getmObjectId(), title, body, mImageBytes, mStartDate, mEndDate, getPrioritySelected(), mNotifyParent.isChecked(), new FunctionCallback<Boolean>() {
                        @Override
                        public void done(Boolean success, ParseException e) {
                            if (e == null && success)
                                getActivity().onBackPressed();
                        }
                    });
                }
            }
        });

        return mView;
    }

    private int getPrioritySelected() {
        switch (mRadioGroup.getCheckedRadioButtonId()){
            case R.id.newA_priority_low:
                return PostsDataSource.LOW_PRIORITY;
            case R.id.newA_priority_medium:
                return PostsDataSource.MEDIUM_PRIORITY;
            case R.id.newA_priority_high:
                return PostsDataSource.HIGH_PRIORITY;

        }
        return 0;
    }

    private void setupView(View v) {
        mLoading = (ProgressBar)v.findViewById(R.id.newA_submition_progressbar);

        mStartDate = new Date();
        mEndDate = new Date();

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
                openDatePickerDialog();
            }
        });

        mEndDateTV = (TextView)v.findViewById(R.id.newA_end_date_TV);
        mEndDateTV.setText(dateToString(new Date()));
        mEndDateSelector = (LinearLayout)v.findViewById(R.id.newA_end_date_field);
        mEndDateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectingStartDate = false;
                openDatePickerDialog();
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

        mNotifyParent = (Switch)v.findViewById(R.id.newA_notify_followers_SWITCH);
        TextView notifyParentTV = (TextView)v.findViewById(R.id.newO_notify_parent_TV);
        if (mOrg.getmParentLevel() == null){
            LinearLayout notifyParentField = (LinearLayout)mView.findViewById(R.id.newO_notify_parent_field);
            notifyParentField.setVisibility(View.GONE);
        } else {
            notifyParentTV.setText(getString(R.string.format_new_announce_Notify, mOrg.getmParentLevel().getmLevelTitle()));
        }
    }

    private void openDatePickerDialog() {
        int year = selectingStartDate ? mStartDate.getYear() : mEndDate.getYear();
        year += 1900;
        int month = selectingStartDate ? mStartDate.getMonth() : mEndDate.getMonth();
        int day = selectingStartDate ? mStartDate.getDate() : mEndDate.getDate();

        //instantiate the date picker dialog and implement the onDateSet method (it is implemented by the today frag)
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), R.style.DialogTheme, NewAnnouncementFragment.this, year, month, day);
        datePickerDialog.show();
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
        date.setMinutes(roundMinuteToIntervals(date.getMinutes()));
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy  hh:mm a");
        return df.format(date);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        int hours = selectingStartDate ? mStartDate.getHours() : mEndDate.getHours();
        int minutes = selectingStartDate ? mStartDate.getMinutes() : mEndDate.getMinutes();
        Date date = new Date(year-1900, monthOfYear, dayOfMonth, hours, minutes);
        if (selectingStartDate){
            mStartDate = date;
            mStartDateTV.setText(dateToString(date));
        }
        else {
            mEndDate = date;
            mEndDateTV.setText(dateToString(date));
        }

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), R.style.DialogTheme, NewAnnouncementFragment.this, hours, minutes, false);
        timePickerDialog.show();
    }

    public void setmImageBytes(byte[] mImageBytes) {
        this.mImageBytes = mImageBytes;
    }

    private boolean areDatesAppropriate(){
        DateTime start = new DateTime(mStartDate);
        DateTime end = new DateTime(mEndDate);

        if (end.isBefore(start))
            return false;
        else {
//            int daysBetween = Days.daysBetween(start, end).getDays();
//            return (daysBetween < 5);
            return true;
        }
    }



    private static final int TIME_PICKER_INTERVAL=15;
    private boolean mIgnoreEvent=false;
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        if (mIgnoreEvent)
            return;
        if (minute%TIME_PICKER_INTERVAL!=0){
            minute = roundMinuteToIntervals(minute);
            mIgnoreEvent=true;
            view.setCurrentMinute(minute);
            mIgnoreEvent=false;
        }

        if (selectingStartDate){
            mStartDate.setHours(hourOfDay);
            mStartDate.setMinutes(minute);
            mStartDateTV.setText(dateToString(mStartDate));
        } else {
            mEndDate.setHours(hourOfDay);
            mEndDate.setMinutes(minute);
            mEndDateTV.setText(dateToString(mEndDate));
        }
    }

    public int roundMinuteToIntervals(int minute) {
        //to round to 15 minute intervals
        if(minute%TIME_PICKER_INTERVAL!=0){
            int minuteFloor=minute-(minute%TIME_PICKER_INTERVAL);
            minute=minuteFloor + (minute==minuteFloor+1 ? TIME_PICKER_INTERVAL : 0);
            if (minute==60)
                minute=0;
        }
        return minute;
    }
}

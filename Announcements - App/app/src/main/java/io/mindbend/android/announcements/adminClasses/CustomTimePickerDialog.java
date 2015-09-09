package io.mindbend.android.announcements.adminClasses;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;

/**
 * Created by Akshay Pall on 09/09/2015.
 */
public class CustomTimePickerDialog extends TimePickerDialog {

    public static final int TIME_PICKER_INTERVAL=15;
    private boolean mIgnoreEvent=false;

    public CustomTimePickerDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute,
                                  boolean is24HourView) {
        super(context, callBack, hourOfDay, minute, is24HourView);
    }
    /*
     * (non-Javadoc)
     * @see android.app.TimePickerDialog#onTimeChanged(android.widget.TimePicker, int, int)
     * Implements Time Change Interval
     */
    @Override
    public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
        super.onTimeChanged(timePicker, hourOfDay, minute);
        if (!mIgnoreEvent){
            minute = getRoundedMinute(minute);
            mIgnoreEvent=true;
            timePicker.setCurrentMinute(minute);
            mIgnoreEvent=false;
        }
    }

    public static int getRoundedMinute(int minute){
        if(minute % TIME_PICKER_INTERVAL != 0){
            int minuteFloor = minute - (minute % TIME_PICKER_INTERVAL);
            minute = minuteFloor + (minute == minuteFloor + 1 ? TIME_PICKER_INTERVAL : 0);
            if (minute == 60)  minute=0;
        }

        return minute;
    }
}
package com.avikhasija.materialannouncements;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * Created by Avik Hasija on 7/24/2015.
 */
public class RatioLayout extends FrameLayout {
    private float mRatio;

    public RatioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.RatioLayout,0,0);
        mRatio = a.getFloat(R.styleable.RatioLayout_ratio, 1);
        a.recycle();
    }

    public void setRatio(float ratio) {
        mRatio = ratio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);


        int desiredHeight = (int) (widthSize * mRatio);
        int selectedHeight;

        if (heightMode == MeasureSpec.EXACTLY){
            selectedHeight = heightSize;

        } else if (heightMode == MeasureSpec.AT_MOST){
            selectedHeight = Math.min(heightSize, desiredHeight);

        } else{
            selectedHeight = desiredHeight;

        }
        super.onMeasure(MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(selectedHeight, MeasureSpec.EXACTLY));
    }
}

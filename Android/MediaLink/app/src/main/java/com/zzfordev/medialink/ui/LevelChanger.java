package com.zzfordev.medialink.ui;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class LevelChanger extends LinearLayout
{
    public interface OnValueChangedListener
    {
        void onValueChanged(float value);
    }


    ////
    private OnValueChangedListener mOnValueChangedListener;
    private SeekBar mSeekBar;
    private TextView mValue;
    private float mMin;
    private float mMax;
    private float mStepLength;
    private float mDefaultValue;


    //

    public LevelChanger(Context context)
    {
        super(context);
    }


    public LevelChanger(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void setOnValueChangedListener(OnValueChangedListener l)
    {
        mOnValueChangedListener = l;
    }

    /*
    e.g. min = -0.1, max = 0.1, stepLength = 0.02
    then possible values are:
        -0.1, -0.08, -0.06, -0.04, -0.02, 0.00, 0.02, 0.04, 0.06, 0.08, 0.1
    there are 11 possible values, so steps = 11
     */
    public void setParams(float min, float max, float defaultValue, float stepLength)
    {
        mMax = max;
        mMin = min;
        mStepLength = stepLength;
        mDefaultValue = defaultValue;

        initUI();
    }

    public void setToDefault()
    {
        mValue.setText(String.format("%.2f", mDefaultValue));

        int defaultStep = valueToStep(mDefaultValue);
        mSeekBar.setProgress(defaultStep);
    }

    private void initUI()
    {
        mValue = new TextView(getContext());
        mSeekBar = new SeekBar(getContext());

        mValue.setWidth(200);
        mValue.setText(String.format("%.2f", mDefaultValue));

        int step = (int)((mMax - mMin) / mStepLength );
        int defaultStep = valueToStep(mDefaultValue);
        mSeekBar.setMax(step);
        mSeekBar.setProgress(defaultStep);
        mSeekBar.setOnSeekBarChangeListener(new SeekBarChangedListener());

        //
        setOrientation(LinearLayout.HORIZONTAL);

        //
        removeAllViews();

        addView(mValue);
        addView(mSeekBar, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    private float stepToValue(int step)
    {
        return mMin + step * mStepLength;
    }

    private int valueToStep(float value)
    {
        return (int)((value - mMin) / mStepLength);
    }

    private class SeekBarChangedListener implements SeekBar.OnSeekBarChangeListener
    {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b)
        {
            float value = stepToValue(i);

            mValue.setText(String.format("%.2f", value));

            if (mOnValueChangedListener != null)
            {
                mOnValueChangedListener.onValueChanged(value);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar)
        {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar)
        {

        }
    }
}

package com.mobile.otrcapitalllc.CustomViews;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.mobile.otrcapitalllc.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomSwitch extends LinearLayout {

    @BindView(R.id.switch_custom)
    Switch mSwitch;
    @BindView(R.id.textView_switch)
    TextView mTextView;

    private Context mContext;

    public CustomSwitch(Context context) {
        super(context);
        mContext = context;
        inflateLayout();
    }

    public CustomSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        inflateLayout();
    }

    public CustomSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        inflateLayout();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomSwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        inflateLayout();
    }

    private void inflateLayout() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_custom_switch, this, true);
        if (!isInEditMode()) ButterKnife.bind(this, view);
    }

    public boolean isChecked() {
        return mSwitch.isChecked();
    }

    public void setChecked(boolean checked) {
        mSwitch.setChecked(checked);
    }

    public String getText() {
        return mTextView.getText().toString();
    }

    public void setText(String text) {
        mTextView.setText(text);
    }
}

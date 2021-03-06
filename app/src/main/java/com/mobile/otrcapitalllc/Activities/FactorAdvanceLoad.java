package com.mobile.otrcapitalllc.Activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mobile.otrcapitalllc.CustomViews.CustomSwitch;
import com.mobile.otrcapitalllc.Helpers.ActivityTags;
import com.mobile.otrcapitalllc.Helpers.BrokerDatabase;
import com.mobile.otrcapitalllc.Helpers.FilterWithSpaceAdapter;
import com.mobile.otrcapitalllc.Models.Broker;
import com.mobile.otrcapitalllc.Models.CustomError;
import com.mobile.otrcapitalllc.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FactorAdvanceLoad extends Activity {
    @Bind(R.id.brokerNameET)
    AutoCompleteTextView brokerNameET;
    @Bind(R.id.loadNumberET)
    EditText loadNumberET;
    @Bind(R.id.totalPayET)
    EditText totalPayET;
    @Bind(R.id.totalDeductionET)
    EditText totalDeductionET;
    @Bind(R.id.totalPayInfo)
    TextView totalPayInfo;
    @Bind(R.id.totalPayTV)
    TextView totalPayTV;
    @Bind(R.id.totalDeductionTV)
    TextView totalDeductionTV;
    @Bind(R.id.totalDeductionsInfo)
    TextView totalDeductionsInfo;
    @Bind(R.id.textClearImgBtn)
    ImageButton textClearImgBtn;
    @Bind({ R.id.custom_switch1, R.id.custom_switch2, R.id.custom_switch3, R.id.custom_switch4 })
    List<CustomSwitch> switchViews;

    List<Broker> brokers;
    private boolean getInput = true;
    private String activityType;
    private String cellNumber;
    private ActivityTags.TAKE_PHOTO_TYPE photoType;

    @OnClick(R.id.scanButton)
    public void scanButton(View view) {
        photoType = ActivityTags.TAKE_PHOTO_TYPE.CAMERA;
        CustomError error = checkFields();
        if (error.isError()) {
            showAlertDialog(error.getDescription());
        } else {
            if (isNeedPhone()) {
                showPhoneInputDialog();
            } else {
                openNextScreen();
            }
        }
    }

    @OnClick(R.id.galleryButton)
    public void galleryButton(View view) {
        photoType = ActivityTags.TAKE_PHOTO_TYPE.GALLERY;
        CustomError error = checkFields();
        if (error.isError()) {
            showAlertDialog(error.getDescription());
        } else {
            if (isNeedPhone()) {
                showPhoneInputDialog();
            } else {
                openNextScreen();
            }
        }
    }

    @OnClick(R.id.textClearImgBtn)
    public void clearText(View view) {
        brokerNameET.setText("");
        textClearImgBtn.setVisibility(View.INVISIBLE);
    }

    @OnClick({ R.id.custom_switch1, R.id.custom_switch2, R.id.custom_switch3, R.id.custom_switch4 })
    public void switchClick(CustomSwitch aSwitch) {
        ButterKnife.apply(switchViews, UNCHECKED, aSwitch.getId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factor_advance_load);
        ButterKnife.bind(this);

        activityType = getIntent().getStringExtra(ActivityTags.TAG_ACTIVITY_TYPE);
        this.setTitle(activityType);

        String brokerName = getIntent().getStringExtra(ActivityTags.TAG_BROKER_NAME);
        if (!TextUtils.isEmpty(brokerName)) {
            brokerNameET.setText(brokerName);
        }

        if (activityType.equals(ActivityTags.TAG_FACTOR_ADVANCE)) {
            totalPayTV.setText("Total Load Amount:");
            totalDeductionTV.setText("Advance Request Amount:");
        }
        Point pointSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(pointSize);
        brokerNameET.setDropDownWidth(pointSize.x);

        totalPayInfo.setVisibility(View.GONE);
        totalDeductionsInfo.setVisibility(View.GONE);

        setListeners();

        List<String> brokerNames = new ArrayList();
        BrokerDatabase db = new BrokerDatabase(FactorAdvanceLoad.this);
        brokers = db.GetBrokerList();

        for (Broker b : brokers) {
            brokerNames.add(b.get_brokerName());
        }

        FilterWithSpaceAdapter<String> adapter = new FilterWithSpaceAdapter<String>(this, android.R.layout
            .simple_dropdown_item_1line, brokerNames);
        brokerNameET.setAdapter(adapter);

        String[] types;
        if (activityType.equals(ActivityTags.TAG_FACTOR_ADVANCE)) {
            types = getResources().getStringArray(R.array.advance_load);
        } else {
            types = getResources().getStringArray(R.array.factor_load);
        }
        for (int i = 0; i < switchViews.size(); i++) {
            switchViews.get(i).setText(types[i]);
        }
    }

    private CustomError checkFields() {
        boolean isError = false;
        StringBuilder sb = new StringBuilder();
        sb.append("A required field is empty: ");

        String brokerName = brokerNameET.getText().toString();
        if (TextUtils.isEmpty(brokerName)) {
            isError = true;
            sb.append("\n\tBroker Name");
        } else if (brokerDetails(brokerName) == null) {
            isError = true;
            sb.append("\n\tInvalid Broker Name");
        }

        if (TextUtils.isEmpty(loadNumberET.getText().toString())) {
            isError = true;
            sb.append("\n\tLoad Number");
        }

        if (TextUtils.isEmpty(totalPayET.getText().toString())) {
            isError = true;
            if (activityType.equals(ActivityTags.TAG_FACTOR_ADVANCE)) {
                sb.append("\n\tTotal Load Amount");
            } else {
                sb.append("\n\tTotal Pay");
            }
        }

        if (TextUtils.isEmpty(totalDeductionET.getText().toString())) {
            if (activityType.equals(ActivityTags.TAG_FACTOR_ADVANCE)) {
                isError = true;
                sb.append("\n\tAdvance Request Amount");
            } else {
                totalDeductionET.setText("0.0");
            }
        }

        if (!isError && Float.parseFloat(totalPayET.getText().toString()) - Float.parseFloat(totalDeductionET.getText()
            .toString()) <= 0.0) {
            isError = true;
            if (activityType.equals(ActivityTags.TAG_FACTOR_ADVANCE)) {
                sb.append("\n\tAdvance Request cannot be larger than Total Load");
            } else {
                sb.append("\n\tTotal Deduction cannot be larger than Total Pay");
            }
        }

        boolean isChecked = false;
        for (CustomSwitch cs : switchViews) {
            if (cs.isChecked())
                isChecked = true;
        }
        if (!isChecked) {
            isError = true;
            sb.append("\n\tNeeds to select an option");
        }

        return new CustomError(isError, sb.toString());
    }

    private boolean isNeedPhone() {
        String pickedSwitch = "";
        for (CustomSwitch cs : switchViews) {
            if (cs.isChecked())
                pickedSwitch = cs.getText();
        }
        return activityType.equals(ActivityTags.TAG_FACTOR_ADVANCE) && pickedSwitch.equals("Text Comchek");
    }

    private void setListeners() {
        brokerNameET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                totalPayInfo.setVisibility(View.GONE);
                totalDeductionsInfo.setVisibility(View.GONE);
                return false;
            }
        });

        brokerNameET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(brokerNameET.getWindowToken(), 0);
                    loadNumberET.requestFocus();
                    imm.showSoftInput(loadNumberET, InputMethodManager.SHOW_IMPLICIT);
                    return true;
                }
                return false;
            }
        });

        brokerNameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (brokerNameET.getText().toString().length() > 0)
                    textClearImgBtn.setVisibility(View.VISIBLE);
                else
                    textClearImgBtn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        loadNumberET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                totalPayInfo.setVisibility(View.GONE);
                totalDeductionsInfo.setVisibility(View.GONE);
                return false;
            }
        });

        totalPayET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                totalPayInfo.setVisibility(View.VISIBLE);
                totalDeductionsInfo.setVisibility(View.GONE);
                return false;
            }
        });

        totalDeductionET.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                totalPayInfo.setVisibility(View.GONE);
                totalDeductionsInfo.setVisibility(View.VISIBLE);
                return false;
            }
        });

    }

    @Nullable
    private Broker brokerDetails(String brokerName) {
        for (Broker b : brokers) {
            if (b.get_brokerName().equals(brokerName)) {
                return b;
            }
        }
        return null;
    }

    private void openNextScreen() {
        String brokerName = brokerNameET.getText().toString();
        float invoiceAmount = Float.parseFloat(totalPayET.getText().toString()) - Float.parseFloat(totalDeductionET.getText()
            .toString());
        Bundle extras = new Bundle();
        extras.putString(ActivityTags.TAG_BROKER_NAME, brokerName);
        extras.putString(ActivityTags.TAG_LOAD_NUMBER, loadNumberET.getText().toString());
        extras.putString(ActivityTags.TAG_PKEY, brokerDetails(brokerName).get_pKey());
        extras.putString(ActivityTags.TAG_MC_NUMBER, brokerDetails(brokerName).get_mcnumber());
        extras.putFloat(ActivityTags.TAG_INVOICE_AMOUNT, invoiceAmount);
        extras.putString(ActivityTags.TAG_ACTIVITY_TYPE, activityType);
        extras.putString(ActivityTags.TAG_PHOTO_TYPE, photoType.name());
        extras.putString(ActivityTags.TAG_PAYMENT_OPTION, getPaymentOption());
        extras.putString(ActivityTags.TAG_CELL_NUMBER, cellNumber);
        extras.putFloat(ActivityTags.TAG_ADV_REQ_AMOUNT, Float.parseFloat(totalDeductionET.getText().toString()));
        Intent intent = new Intent(FactorAdvanceLoad.this, LoadDetails.class);
        intent.putExtra("data_extra", extras);
        startActivity(intent);
    }

    private void showAlertDialog(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener
            () {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void showPhoneInputDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setLines(1);
        editText.setFilters(new InputFilter[]{ new InputFilter.LengthFilter(14) });
        editText.addTextChangedListener(new PhoneNumberFormattingTextWatcher() {
            private boolean backspacingFlag = false;
            private boolean editedFlag = false;
            private int cursorComplement;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                cursorComplement = s.length() - editText.getSelectionStart();
                if (count > after) {
                    backspacingFlag = true;
                } else {
                    backspacingFlag = false;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String string = s.toString();
                String phone = string.replaceAll("[^\\d]", "");
                if (!editedFlag) {
                    if (phone.length() >= 6 && !backspacingFlag) {
                        editedFlag = true;
                        String ans = "(" + phone.substring(0, 3) + ") " + phone.substring(3, 6) + "-" + phone.substring(6);
                        editText.setText(ans);
                        editText.setSelection(editText.getText().length() - cursorComplement);
                    } else if (phone.length() >= 3 && !backspacingFlag) {
                        editedFlag = true;
                        String ans = "(" + phone.substring(0, 3) + ") " + phone.substring(3);
                        editText.setText(ans);
                        editText.setSelection(editText.getText().length() - cursorComplement);
                    }
                } else {
                    editedFlag = false;
                }
            }
        });
        alert.setTitle("Enter cell#");

        alert.setView(editText);

        alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                cellNumber = editText.getText().toString();
                if (cellNumber.isEmpty() || cellNumber.length() < 14) {
                    showAlertDialog("Cell# must be non empty");
                } else {
                    openNextScreen();
                }
            }
        });

        alert.show();
    }

    @Nullable
    private String getPaymentOption() {
        for (CustomSwitch cs : switchViews) {
            if (cs.isChecked())
                return cs.getText();
        }
        return null;
    }

    static final ButterKnife.Setter<CustomSwitch, Integer> UNCHECKED = new ButterKnife.Setter<CustomSwitch, Integer>() {
        @Override
        public void set(CustomSwitch view, Integer id, int index) {
            if (view.getId() == id) {
                view.setChecked(!view.isChecked());
            } else {
                if (view.isChecked())
                    view.setChecked(false);
            }

        }
    };
}

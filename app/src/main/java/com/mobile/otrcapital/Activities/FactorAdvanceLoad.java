package com.mobile.otrcapital.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mobile.otrcapital.Helpers.ActivityTags;
import com.mobile.otrcapital.Helpers.Broker;
import com.mobile.otrcapital.Helpers.BrokerDatabase;
import com.mobile.otrcapital.Helpers.FilterWithSpaceAdapter;
import com.mobile.otrcapital.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FactorAdvanceLoad extends Activity
{
    @Bind(R.id.brokerNameET) AutoCompleteTextView brokerNameET;
    @Bind(R.id.loadNumberET) EditText loadNumberET;
    @Bind(R.id.totalPayET) EditText totalPayET;
    @Bind(R.id.totalDeductionET) EditText totalDeductionET;
    @Bind(R.id.totalPayInfo) TextView totalPayInfo;
    @Bind(R.id.totalPayTV) TextView totalPayTV;
    @Bind(R.id.totalDeductionTV) TextView totalDeductionTV;
    @Bind(R.id.totalDeductionsInfo) TextView totalDeductionsInfo;
    @Bind(R.id.textClearImgBtn)ImageButton textClearImgBtn;
    List<Broker> brokers;
    private boolean getInput = true;
    private String activityType;

    @OnClick (R.id.scanButton) public void scanButton (View view)
    {
        String errorMessage = "A required field is empty: ";
        boolean isError = false;
        String brokerName = brokerNameET.getText().toString();
        if (brokerName.isEmpty())
        {
            isError = true;
            errorMessage += "\n\tBroker Name";
        }
        else if (brokerDetails(brokerName) == null)
        {
            isError = true;
            errorMessage += "\n\tInvalid Broker Name";
        }

        if (loadNumberET.getText().toString().isEmpty())
        {
            isError = true;
            errorMessage += "\n\tLoad Number";
        }

        if (totalPayET.getText().toString().isEmpty())
        {
            isError = true;
            if (activityType.equals(ActivityTags.TAG_FACTOR_ADVANCE))
                errorMessage += "\n\tTotal Load Amount";
            else
                errorMessage += "\n\tTotal Pay";
        }

        if (totalDeductionET.getText().toString().isEmpty())
        {
            totalDeductionET.setText("0.0");
            if (activityType.equals(ActivityTags.TAG_FACTOR_ADVANCE))
            {
                isError = true;
                errorMessage += "\n\tAdvance Request Amount";
            }
        }

        if (isError)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setMessage(errorMessage)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {

                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        else
        {
            float invoiceAmount = Float.parseFloat(totalPayET.getText().toString())
                    -Float.parseFloat(totalDeductionET.getText().toString());
            Bundle extras = new Bundle();
            extras.putString(ActivityTags.TAG_BROKER_NAME, brokerName);
            extras.putString(ActivityTags.TAG_LOAD_NUMBER, loadNumberET.getText().toString());
            extras.putString(ActivityTags.TAG_PKEY, brokerDetails(brokerName).get_pKey());
            extras.putString(ActivityTags.TAG_MC_NUMBER, brokerDetails(brokerName).get_mcnumber());
            extras.putFloat(ActivityTags.TAG_INVOICE_AMOUNT, invoiceAmount);
            extras.putString(ActivityTags.TAG_ACTIVITY_TYPE,activityType);
            Intent intent = new Intent(FactorAdvanceLoad.this, LoadDetails.class);
            intent.putExtra("data_extra", extras);
            startActivity(intent);
        }
    }

    @OnClick(R.id.textClearImgBtn) public void clearText (View view)
    {
        brokerNameET.setText("");
        textClearImgBtn.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Bundle extras = getIntent().getExtras();
        Bundle bundle =  extras.getBundle("data_extra");
        try
        {
            activityType = bundle.getString(ActivityTags.TAG_ACTIVITY_TYPE);
        }catch(NullPointerException e)
        {
            activityType = "";
        }
        this.setTitle(activityType);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factor_advance_load);
        ButterKnife.bind(this);
        String brokerName = bundle.getString(ActivityTags.TAG_BROKER_NAME);

        if (brokerName != null && !brokerName.isEmpty())
        {
            brokerNameET.setText(bundle.getString(ActivityTags.TAG_BROKER_NAME));
        }

        if (activityType.equals(ActivityTags.TAG_FACTOR_ADVANCE))
        {
            totalPayTV.setText("Total Load Amount:");
            totalDeductionTV.setText("Advance Request Amount:");
        }
        Point pointSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(pointSize);
        brokerNameET.setDropDownWidth(pointSize.x);

        totalPayInfo.setVisibility(View.GONE);
        totalDeductionsInfo.setVisibility(View.GONE);

        SetListeners();

        AssetManager assetManager = getAssets();
        List <String> brokerNames = new ArrayList();
        BrokerDatabase db = new BrokerDatabase(FactorAdvanceLoad.this);
        brokers = db.GetBrokerList();

        for (Broker b:brokers)
        {
            brokerNames.add(b.get_brokerName());
        }

        FilterWithSpaceAdapter <String> adapter = new FilterWithSpaceAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, brokerNames);
        brokerNameET.setAdapter(adapter);

    }

    private void SetListeners()
    {
        brokerNameET.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                totalPayInfo.setVisibility(View.GONE);
                totalDeductionsInfo.setVisibility(View.GONE);
                return false;
            }
        });

        brokerNameET.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(brokerNameET.getWindowToken(), 0);
                    loadNumberET.requestFocus();
                    imm.showSoftInput(loadNumberET, InputMethodManager.SHOW_IMPLICIT);
                    return true;
                }
                return false;
            }
        });

        brokerNameET.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (brokerNameET.getText().toString().length() > 0)
                    textClearImgBtn.setVisibility(View.VISIBLE);
                else
                    textClearImgBtn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });



        loadNumberET.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                totalPayInfo.setVisibility(View.GONE);
                totalDeductionsInfo.setVisibility(View.GONE);
                return false;
            }
        });


        totalPayET.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                totalPayInfo.setVisibility(View.VISIBLE);
                totalDeductionsInfo.setVisibility(View.GONE);
                return false;
            }
        });



        totalDeductionET.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                totalPayInfo.setVisibility(View.GONE);
                totalDeductionsInfo.setVisibility(View.VISIBLE);
                return false;
            }
        });

    }

    private Broker brokerDetails(String brokerName)
    {
        for (Broker b: brokers)
        {
            if (b.get_brokerName().equals(brokerName))
            {
                return b;
            }
        }
        return null;
    }


}

package com.mobile.otrcapital.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;

import com.mobile.otrcapital.Helpers.ActivityTags;
import com.mobile.otrcapital.Models.Broker;
import com.mobile.otrcapital.Helpers.BrokerDatabase;
import com.mobile.otrcapital.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BrokerCheck extends Activity
{
    @Bind(R.id.brokerNameET) AutoCompleteTextView brokerNameET;
    @Bind(R.id.mcNumberET) EditText mcNumberET;
    @Bind(R.id.textClearImgBtn)ImageButton textClearImgBtn;
    private Broker brokerToCheck;
    private List<Broker> brokers;
    private boolean brokerFound;

    @OnClick(R.id.verifyButton) public void verifyButton (View view)
    {
        if (brokerFound)
        {
            launchBrokerDetails();
        }
        else
        {
            if (brokerNameET.getText().toString().isEmpty() && mcNumberET.getText().toString().isEmpty())
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder
                        .setTitle("UNABLE TO PROCEED")
                        .setMessage("Both Broker name and MC number cannot be left blank." +
                                "\nPlease enter either a valid Broker Name or MC number to continue.")
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
                String mcnumberString,brokerNameString;

                if (mcNumberET.getText().toString().isEmpty())
                    mcnumberString = "xxx";
                else
                    mcnumberString=mcNumberET.getText().toString();

                if(brokerNameET.getText().toString().isEmpty())
                    brokerNameString = "xxx";
                else
                    brokerNameString = brokerNameET.getText().toString();

                if (checkBrokerInDB(brokerNameString,mcnumberString))
                {
                    launchBrokerDetails();
                }
                else
                {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder
                            .setTitle("BROKER NOT FOUND")
                            .setMessage("The Broker does not exist in the database," +
                                    "\nPlease enter either a valid Broker Name or MC number to continue.")
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
            }
        }
    }
    @OnClick(R.id.textClearImgBtn) public void clearText (View view)
    {
        brokerNameET.setText("");
        textClearImgBtn.setVisibility(View.INVISIBLE);
    }


    private void launchBrokerDetails()
    {
        Bundle extras = new Bundle();
        extras.putString(ActivityTags.TAG_BROKER_NAME, brokerToCheck.get_brokerName());
        extras.putString(ActivityTags.TAG_MC_NUMBER, brokerToCheck.get_mcnumber());
        extras.putString(ActivityTags.TAG_PKEY, brokerToCheck.get_pKey());
        Intent intent = new Intent(BrokerCheck.this, BrokerDetails.class);
        intent.putExtra("data_extra", extras);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broker_check);
        ButterKnife.bind(this);

        final List <String> brokerNames = new ArrayList();
        BrokerDatabase db = new BrokerDatabase(BrokerCheck.this);
        brokers = db.GetBrokerList();

        for (Broker b:brokers)
        {
            brokerNames.add(b.get_brokerName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, brokerNames);
        brokerNameET.setAdapter(adapter);
        brokerNameET.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                final String currentBrokerName = brokerNameET.getText().toString();
                if (checkBrokerInDB(currentBrokerName, "xxx"))
                    mcNumberET.setText(brokerToCheck.get_mcnumber());
            }
        });
        Point pointSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(pointSize);
        brokerNameET.setDropDownWidth(pointSize.x);

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
                    mcNumberET.requestFocus();
                    imm.showSoftInput(mcNumberET, InputMethodManager.SHOW_IMPLICIT);
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


        mcNumberET.setOnKeyListener(new View.OnKeyListener()
        {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mcNumberET.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
    }

    private boolean checkBrokerInDB(String currentBrokerName,String mcNumber)
    {
        for (Broker b : brokers)
        {
            if (currentBrokerName.equals(b.get_brokerName()) || mcNumber.equals(b.get_mcnumber()))
            {
                brokerFound = true;
                brokerToCheck = b;
                return true;
            }
        }
        return false;
    }


}

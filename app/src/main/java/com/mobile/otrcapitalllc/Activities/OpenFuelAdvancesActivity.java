package com.mobile.otrcapitalllc.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mobile.otrcapitalllc.Adapters.OpenFuelAdvancesAdapter;
import com.mobile.otrcapitalllc.Helpers.ActivityTags;
import com.mobile.otrcapitalllc.Helpers.PreferenceManager;
import com.mobile.otrcapitalllc.Models.HistoryInvoiceModel;
import com.mobile.otrcapitalllc.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class OpenFuelAdvancesActivity extends BaseActivity {

    @Bind(R.id.listView)
    ListView listView;

    OpenFuelAdvancesAdapter adapter;

    public static void start(Context context) {
        Intent starter = new Intent(context, OpenFuelAdvancesActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_fuel_advances);
        ButterKnife.bind(this);

        List<HistoryInvoiceModel> list = PreferenceManager.with(this).getAdvanceLoadList();
        adapter = new OpenFuelAdvancesAdapter(this, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                HistoryInvoiceModel model = adapter.getItem(i);
                Intent intent = new Intent(OpenFuelAdvancesActivity.this, FactorAdvanceLoad.class);

                if (model != null) {
                    Bundle extras = new Bundle();
                    extras.putString(ActivityTags.TAG_BROKER_NAME, model.getBrokerName());
                    extras.putString(ActivityTags.TAG_LOAD_NUMBER, model.getPoNumber());
                    extras.putString(ActivityTags.TAG_PKEY, Integer.toString(model.getCustomerPKey()));
                    extras.putString(ActivityTags.TAG_MC_NUMBER, model.getCustomerMCNumber());
                    extras.putString(ActivityTags.TAG_ACTIVITY_TYPE, ActivityTags.TAG_FACTOR_LOAD);
                    extras.putFloat(ActivityTags.TAG_INVOICE_AMOUNT, model.getInvoiceAmount());
                    extras.putFloat(ActivityTags.TAG_ADV_REQ_AMOUNT, model.getAdvanceRequestAmount());

                    intent.putExtra("data_extra", extras);
                }
                intent.putExtra(ActivityTags.TAG_ACTIVITY_TYPE, ActivityTags.TAG_FACTOR_LOAD);
                startActivity(intent);
            }
        });
    }

    @OnClick(R.id.btnLoadFactor)
    public void factorAdvanceLoad() {
        Intent intent = new Intent(this, FactorAdvanceLoad.class);
        intent.putExtra(ActivityTags.TAG_ACTIVITY_TYPE, ActivityTags.TAG_FACTOR_LOAD);
        startActivity(intent);
    }
}

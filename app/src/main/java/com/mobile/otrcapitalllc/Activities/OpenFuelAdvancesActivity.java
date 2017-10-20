package com.mobile.otrcapitalllc.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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


public class OpenFuelAdvancesActivity extends Activity {

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
    }

    @OnClick(R.id.btnLoadFactor)
    public void factorAdvanceLoad() {
        Intent intent = new Intent(this, FactorAdvanceLoad.class);
        intent.putExtra(ActivityTags.TAG_ACTIVITY_TYPE, ActivityTags.TAG_FACTOR_LOAD);
        startActivity(intent);
    }
}

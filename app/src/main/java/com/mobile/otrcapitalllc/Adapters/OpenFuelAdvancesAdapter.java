package com.mobile.otrcapitalllc.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobile.otrcapitalllc.Helpers.PreferenceManager;
import com.mobile.otrcapitalllc.Models.HistoryInvoiceModel;
import com.mobile.otrcapitalllc.R;

import java.util.ArrayList;
import java.util.List;


public class OpenFuelAdvancesAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final List<HistoryInvoiceModel> list;

    public OpenFuelAdvancesAdapter(Context context, ArrayList<String> fileNames) {
        super(context, R.layout.open_fuel_advance_view, fileNames);
        this.mContext = context;
        this.list = PreferenceManager.with(mContext).getAdvanceLoadList();
    }

    static class ViewHolder {
        TextView tvBrokerName;
        TextView tvLoadNumber;
        TextView tvInvAmount;
        TextView tvFuelAmount;
        TextView tvDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OpenFuelAdvancesAdapter.ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.open_fuel_advance_view, parent, false);
            viewHolder = new OpenFuelAdvancesAdapter.ViewHolder();
            viewHolder.tvBrokerName = (TextView) convertView.findViewById(R.id.tvBrokerName);
            viewHolder.tvLoadNumber = (TextView) convertView.findViewById(R.id.tvLoadNumber);
            viewHolder.tvInvAmount = (TextView) convertView.findViewById(R.id.tvInvAmount);
            viewHolder.tvFuelAmount = (TextView) convertView.findViewById(R.id.tvFuelAmount);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (OpenFuelAdvancesAdapter.ViewHolder) convertView.getTag();
        }


        if (position < list.size()) {
            viewHolder.tvBrokerName.setText("unknown");
            viewHolder.tvLoadNumber.setText("unknown");
            viewHolder.tvInvAmount.setText("NA");
            viewHolder.tvFuelAmount.setText("NA");
            viewHolder.tvDate.setText("NA");
        } else {
            HistoryInvoiceModel model = list.get(position);

            viewHolder.tvBrokerName.setText(model.getCustomerPKey());
            viewHolder.tvLoadNumber.setText(model.getCustomerMCNumber());
            viewHolder.tvInvAmount.setText(String.format("%.02f", model.getInvoiceAmount()));
            viewHolder.tvFuelAmount.setText(String.format("%.02f", model.getAdvanceRequestAmount()));
            viewHolder.tvDate.setText(model.getTimestamp());
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
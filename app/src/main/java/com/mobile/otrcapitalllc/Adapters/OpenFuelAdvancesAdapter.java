package com.mobile.otrcapitalllc.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobile.otrcapitalllc.Models.HistoryInvoiceModel;
import com.mobile.otrcapitalllc.R;

import java.util.List;


public class OpenFuelAdvancesAdapter extends ArrayAdapter<HistoryInvoiceModel> {
    private final Context mContext;

    public OpenFuelAdvancesAdapter(Context context, List<HistoryInvoiceModel> list) {
        super(context, R.layout.open_fuel_advance_view, list);
        this.mContext = context;
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

        HistoryInvoiceModel model = getItem(position);
        if(model != null) {
            viewHolder.tvBrokerName.setText(model.getBrokerName());
            viewHolder.tvLoadNumber.setText(model.getPoNumber());
            viewHolder.tvInvAmount.setText(String.format("%.02f", model.getInvoiceAmount()));
            viewHolder.tvFuelAmount.setText(String.format("%.02f", model.getAdvanceRequestAmount()));
            viewHolder.tvDate.setText(model.getTimestamp());
        }

        return convertView;
    }
}
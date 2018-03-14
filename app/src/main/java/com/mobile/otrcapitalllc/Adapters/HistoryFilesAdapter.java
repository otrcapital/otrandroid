package com.mobile.otrcapitalllc.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobile.otrcapitalllc.Helpers.LogHelper;
import com.mobile.otrcapitalllc.Helpers.PreferenceManager;
import com.mobile.otrcapitalllc.Models.HistoryInvoiceComparable;
import com.mobile.otrcapitalllc.Models.HistoryInvoiceModel;
import com.mobile.otrcapitalllc.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by jawad on 8/3/2015.
 */
public class HistoryFilesAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final ArrayList<String> fileNames;
    private final ArrayList<HistoryInvoiceComparable> historyInvoiceComparables = new ArrayList<>();

    public HistoryFilesAdapter(Context context, ArrayList<String> fileNames) {
        super(context, R.layout.history_item_view, fileNames);
        this.mContext = context;
        this.fileNames = fileNames;
        for (String fileName :
                fileNames) {
            String jsonInvoice = PreferenceManager.with(mContext).getStringWithKey(fileName);
            if (jsonInvoice == null)
                historyInvoiceComparables.add(new HistoryInvoiceComparable(fileName, null));
            else
                historyInvoiceComparables.add(new HistoryInvoiceComparable(fileName, new Gson().fromJson(jsonInvoice, HistoryInvoiceModel.class)));
        }
        Collections.sort(historyInvoiceComparables);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.history_item_view, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.documentNameTV = convertView.findViewById(R.id.documentNameTV);
            viewHolder.statusTV = convertView.findViewById(R.id.statusTV);
            viewHolder.timestampTV = convertView.findViewById(R.id.timeStampTV);
            viewHolder.rateTV = convertView.findViewById(R.id.rateTV);
            viewHolder.loadNoTV = convertView.findViewById(R.id.loadNoTV);
            viewHolder.fileInfoGroup = convertView.findViewById(R.id.fileInfoGroup);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.documentNameTV.setTag(historyInvoiceComparables.get(position).getFilename());
        viewHolder.fileInfoGroup.setTag(historyInvoiceComparables.get(position).getFilename());
        HistoryInvoiceModel historyInvoiceModel = historyInvoiceComparables.get(position).getHistoryInvoiceModel();
        viewHolder.documentNameTV.setText((historyInvoiceModel.getBrokerName() != null) ? historyInvoiceModel.getBrokerName() : "Document");
        viewHolder.statusTV.setText(historyInvoiceModel.getStatus());
        viewHolder.timestampTV.setText(historyInvoiceModel.getTimestamp());
        viewHolder.rateTV.setText("Rate: " + String.format("%.02f", historyInvoiceModel.getInvoiceAmount()));
        viewHolder.loadNoTV.setText("Load#: " + historyInvoiceModel.getPoNumber());
        return convertView;
    }

    public void RemoveFileFromList(int position) {
        try {
            historyInvoiceComparables.remove(position);
        } catch (IndexOutOfBoundsException e) {
            LogHelper.logError("Index out of bounds exception -> HistoryFilesAdapter\n" + e.toString());
        }
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView documentNameTV;
        TextView statusTV;
        TextView timestampTV;
        TextView rateTV;
        TextView loadNoTV;
        RelativeLayout fileInfoGroup;
    }

}

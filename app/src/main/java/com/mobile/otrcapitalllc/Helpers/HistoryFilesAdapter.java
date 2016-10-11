package com.mobile.otrcapitalllc.Helpers;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mobile.otrcapitalllc.Models.HistoryInvoiceModel;
import com.mobile.otrcapitalllc.R;

/**
 * Created by jawad on 8/3/2015.
 */
public class HistoryFilesAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final ArrayList<String> fileNames;

    public HistoryFilesAdapter(Context context, ArrayList<String> fileNames) {
        super(context, R.layout.history_item_view, fileNames);
        this.mContext = context;
        this.fileNames = fileNames;
    }

    static class ViewHolder {
        TextView documentNameTV;
        TextView statusTV;
        TextView timestampTV;
        TextView rateTV;
        TextView loadNoTV;
        RelativeLayout fileInfoGroup;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.history_item_view, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.documentNameTV = (TextView) convertView.findViewById(R.id.documentNameTV);
            viewHolder.statusTV = (TextView) convertView.findViewById(R.id.statusTV);
            viewHolder.timestampTV = (TextView) convertView.findViewById(R.id.timeStampTV);
            viewHolder.rateTV = (TextView) convertView.findViewById(R.id.rateTV);
            viewHolder.loadNoTV = (TextView) convertView.findViewById(R.id.loadNoTV);
            viewHolder.fileInfoGroup = (RelativeLayout) convertView.findViewById(R.id.fileInfoGroup);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.documentNameTV.setText(fileNames.get(position));

        String jsonInvoice = PreferenceManager.with(mContext).getStringWithKey(fileNames.get(position));
        if (jsonInvoice == null) {
            return convertView;
        }
        final HistoryInvoiceModel model = new Gson().fromJson(jsonInvoice, HistoryInvoiceModel.class);

        try {
            viewHolder.documentNameTV.setTag(fileNames.get(position));
            viewHolder.fileInfoGroup.setTag(fileNames.get(position));
            viewHolder.statusTV.setText(model.getStatus());
            viewHolder.timestampTV.setText(model.getTimestamp());
            viewHolder.rateTV.setText("Rate: " + String.format("%.02f", model.getInvoiceAmount()));
            viewHolder.loadNoTV.setText("Load#: " + model.getPoNumber());
        } catch (IndexOutOfBoundsException e) {
            viewHolder.statusTV.setText("unknown");
            viewHolder.timestampTV.setText("unknown");
            viewHolder.rateTV.setText("Rate: NA");
            viewHolder.loadNoTV.setText("Load# NA");
            Log.e(ActivityTags.TAG_LOG, "IndexOutOfBounds-> HistoryFilesAdapter, File: " + fileNames.get(position));
        }

        return convertView;
    }

    public void RemoveFileFromList(int position) {
        try {
            fileNames.remove(position);
        } catch (IndexOutOfBoundsException e) {
            Log.e(ActivityTags.TAG_LOG, "Index out of bounds exception -> HistoryFilesAdapter\n" + e.toString());
        }
        notifyDataSetChanged();
    }

}

package com.mobile.otrcapitalllc.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.ArrayList;

/**
 * Created by jawad on 8/3/2015.
 */
public class HistoryFilesAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final ArrayList<String> fileNames;
    private SharedPreferences prefs;

    public HistoryFilesAdapter(Context context, ArrayList<String> fileNames) {
        super(context, R.layout.history_item_view, fileNames);
        this.context = context;
        this.fileNames = fileNames;
        prefs = context.getSharedPreferences(ActivityTags.SHARED_PREFS_TAG, 0);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View historyFilesView = inflater.inflate(R.layout.history_item_view, parent, false);
        TextView documentNameTV = (TextView) historyFilesView.findViewById(R.id.documentNameTV);
        TextView statusTV = (TextView) historyFilesView.findViewById(R.id.statusTV);
        TextView timestampTV = (TextView) historyFilesView.findViewById(R.id.timeStampTV);
        TextView rateTV = (TextView) historyFilesView.findViewById(R.id.rateTV);
        TextView loadNoTV = (TextView) historyFilesView.findViewById(R.id.loadNoTV);
        RelativeLayout fileInfoGroup = (RelativeLayout) historyFilesView.findViewById(R.id.fileInfoGroup);
        documentNameTV.setText(fileNames.get(position));

        String jsonInvoice = prefs.getString(fileNames.get(position), null);
        if (jsonInvoice == null) {
            return null;
        }
        final HistoryInvoiceModel model = new Gson().fromJson(jsonInvoice, HistoryInvoiceModel.class);

        try {
            documentNameTV.setTag(fileNames.get(position));
            fileInfoGroup.setTag(fileNames.get(position));
            statusTV.setText(model.getStatus());
            timestampTV.setText(model.getTimestamp());
            rateTV.setText("Rate: " + String.format("%.02f", model.getInvoiceAmount()));
            loadNoTV.setText("Load#: " + model.getPoNumber());
        } catch (IndexOutOfBoundsException e) {
            statusTV.setText("unknown");
            timestampTV.setText("unknown");
            rateTV.setText("Rate: NA");
            loadNoTV.setText("Load# NA");
            Log.e(ActivityTags.TAG_LOG, "IndexOutOfBounds-> HistoryFilesAdapter, File: " + fileNames.get(position));
        }
        return historyFilesView;
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

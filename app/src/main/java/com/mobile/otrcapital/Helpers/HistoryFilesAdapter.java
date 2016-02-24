package com.mobile.otrcapital.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobile.otrcapital.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;

/**
 * Created by jawad on 8/3/2015.
 */
public class HistoryFilesAdapter extends ArrayAdapter<String>
{
    private final Context context;
    private final ArrayList<String> fileNames;
    private ArrayList<String> fileAttribues;
    private SharedPreferences prefs;

    public HistoryFilesAdapter(Context context, ArrayList<String> fileNames)
    {
        super(context, R.layout.history_item_view, fileNames);
        this.context = context;
        this.fileNames = fileNames;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View historyFilesView = inflater.inflate(R.layout.history_item_view, parent, false);
        TextView documentNameTV = (TextView) historyFilesView.findViewById(R.id.documentNameTV);
        TextView statusTV = (TextView) historyFilesView.findViewById(R.id.statusTV);
        TextView timestampTV = (TextView) historyFilesView.findViewById(R.id.timeStampTV);
        TextView rateTV = (TextView) historyFilesView.findViewById(R.id.rateTV);
        TextView loadNoTV = (TextView) historyFilesView.findViewById(R.id.loadNoTV);
        RelativeLayout fileInfoGroup = (RelativeLayout) historyFilesView.findViewById(R.id.fileInfoGroup);
        //ImageButton resendImgBtn = (ImageButton) historyFilesView.findViewById(R.id.resendImgBtn);
        //ImageButton emailImgBtn = (ImageButton) historyFilesView.findViewById(R.id.emailImgBtn);
        documentNameTV.setText(fileNames.get(position));
        prefs = context.getSharedPreferences(ActivityTags.SHARED_PREFS_TAG, 0);

        fileAttribues = new ArrayList<String>(prefs.getStringSet(fileNames.get(position), new LinkedHashSet<String>()));
        Collections.sort(fileAttribues);
        final apiInvoiceDataJson invoiceData = new apiInvoiceDataJson();
        String[] documentTypeArray;
        try
        {
            invoiceData.ClientLogin = prefs.getString(ActivityTags.PREFS_USER_EMAIL, "");
            invoiceData.ClientPassword = prefs.getString(ActivityTags.PREFS_USER_PASSWORD, "");
            invoiceData.CustomerPKey = Integer.parseInt(fileAttribues.get(ActivityTags.FILE_PKEY).substring(2));
            invoiceData.CustomerMCNumber = fileAttribues.get(ActivityTags.FILE_MC_NUMBER).substring(2);
            invoiceData.InvoiceAmount = Float.parseFloat(fileAttribues.get(ActivityTags.FILE_INVOICE_AMOUNT).substring(2));
            invoiceData.PoNumber = fileAttribues.get(ActivityTags.FILE_LOAD_NUMBER).substring(2);
            documentTypeArray = fileAttribues.get(ActivityTags.FILE_DOCUMENT_TYPES).substring(2).split("/");
        }catch (IndexOutOfBoundsException e)
        {
            documentTypeArray = new String[0];
        }
        final ArrayList<String> documentTypeList = new ArrayList<String>();
        for (String s: documentTypeArray)
        {
            documentTypeList.add(s);
        }

        try
        {
            //resendImgBtn.setTag(fileNames.get(position));
            //emailImgBtn.setTag(fileNames.get(position));
            documentNameTV.setTag(fileNames.get(position));
            fileInfoGroup.setTag(fileNames.get(position));
            statusTV.setText(fileAttribues.get(ActivityTags.FILE_STATUS).substring(2));
            timestampTV.setText(fileAttribues.get(ActivityTags.FILE_TIMESTAMP).substring(2));
            rateTV.setText("Rate: " +String.format("%.02f", invoiceData.InvoiceAmount));
            loadNoTV.setText("Load#: "+invoiceData.PoNumber);

            if (statusTV.getText().toString().equals("success"))
            {
                //resendImgBtn.setVisibility(View.INVISIBLE);
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            //resendImgBtn.setVisibility(View.INVISIBLE);
            //emailImgBtn.setVisibility(View.INVISIBLE);
            statusTV.setText("unknown");
            timestampTV.setText("unknown");
            rateTV.setText("Rate: NA");
            loadNoTV.setText("Load# NA");
            Log.e(ActivityTags.TAG_LOG, "IndexOutOfBounds-> HistoryFilesAdapter, File: " + fileNames.get(position));
        }



        /*resendImgBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String fileName = (String) v.getTag();
                ((History) context).UploadDoc(fileName,invoiceData,documentTypeList,prefs);
            }
        });

        emailImgBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final String fileName = (String) v.getTag();
                ((History) context).EmailFile(fileName);
            }
        });

        documentNameTV.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String fileName = (String) v.getTag();
                ((History) context).OpenFile(fileName);
            }
        });

        fileInfoGroup.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String fileName = (String) v.getTag();
                ((History) context).OpenFile(fileName);
            }
        });

        */return historyFilesView;
    }

    public void RemoveFileFromList(int position)
    {
        try
        {
            fileNames.remove(position);
        }catch (IndexOutOfBoundsException e)
        {
            Log.e(ActivityTags.TAG_LOG,"Index out of bounds exception -> HistoryFilesAdapter\n" +e.toString());
        }
        notifyDataSetChanged();
    }

}

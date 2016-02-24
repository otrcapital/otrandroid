package com.mobile.otrcapital.Activities;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mobile.otrcapital.Helpers.ActivityTags;
import com.mobile.otrcapital.Helpers.HistoryFilesAdapter;
import com.mobile.otrcapital.Helpers.apiInvoiceDataJson;
import com.mobile.otrcapital.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;

import butterknife.Bind;
import butterknife.ButterKnife;


public class History extends ListActivity
{
    private final Activity activity = this;
    @Bind(R.id.verifyUserGroup) LinearLayout verifyUserGroup;
    @Bind(R.id.verifyUserTV) TextView verifyUserTV;
    HistoryFilesAdapter adapter;
    private ArrayList<String> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        fileList = getFileList();
        adapter = new HistoryFilesAdapter(this,fileList);
        setListAdapter(adapter);
        registerForContextMenu(getListView());

        verifyUserGroup.setVisibility(View.INVISIBLE);
    }

    private ArrayList<String> getFileList()
    {
        File myDir = new File(ActivityTags.EXT_STORAGE_DIR);
        myDir.mkdirs();
        ArrayList<String> files = new ArrayList<String>();

        for (File f : myDir.listFiles())
        {
            if (f.isFile() && f != null)
            {
                files.add(f.getName().toString());
            }

        }

        return files;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        OpenFile(fileList.get(position));
    }

    public void OpenFile(final String fileName)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(ActivityTags.EXT_STORAGE_DIR + fileName)), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    public void EmailFile(final String fileName)
    {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent .setType("*/*");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(ActivityTags.EXT_STORAGE_DIR + fileName )));
        startActivity(Intent.createChooser(emailIntent, "Choose an email provider:"));
    }

    public void DeleteFile(int position)
    {
        File file = new File(ActivityTags.EXT_STORAGE_DIR + fileList.get(position));
        if (file.exists()){
            file.delete();
        }

        adapter.RemoveFileFromList(position);
        adapter.notifyDataSetChanged();

    }

    public void ResendFile(final String fileName)
    {
        SharedPreferences prefs = this.getSharedPreferences(ActivityTags.SHARED_PREFS_TAG, 0);

        ArrayList<String> fileAttribues = new ArrayList<String>(prefs.getStringSet(fileName, new LinkedHashSet<String>()));
        Collections.sort(fileAttribues);
        final apiInvoiceDataJson invoiceData = new apiInvoiceDataJson();
        String[] documentTypeArray;
        String factoryType = "";
        try
        {
            invoiceData.ClientLogin = prefs.getString(ActivityTags.PREFS_USER_EMAIL, "");
            invoiceData.ClientPassword = prefs.getString(ActivityTags.PREFS_USER_PASSWORD, "");
            invoiceData.CustomerPKey = Integer.parseInt(fileAttribues.get(ActivityTags.FILE_PKEY).substring(2));
            invoiceData.CustomerMCNumber = fileAttribues.get(ActivityTags.FILE_MC_NUMBER).substring(2);
            invoiceData.InvoiceAmount = Float.parseFloat(fileAttribues.get(ActivityTags.FILE_INVOICE_AMOUNT).substring(2));
            invoiceData.PoNumber = fileAttribues.get(ActivityTags.FILE_LOAD_NUMBER).substring(2);
            factoryType = fileAttribues.get(ActivityTags.FILE_FACTOR_TYPE).substring(2);
            if (factoryType.equals("ADV"))
                invoiceData.AdvanceRequestAmount = Float.parseFloat(fileAttribues.get(ActivityTags.FILE_ADV_REQ_AMOUNT));
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

        LoadDetails.UploadDocument(fileName,this,activity,verifyUserGroup,invoiceData,documentTypeList,factoryType,prefs);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        menu.add(0, 0, 0, "Delete");//groupId, itemId, order, title
        menu.add(0, 1, 1, "Open");
        menu.add(0, 2, 2, "Email");
        menu.add(0, 3, 3, "Resend");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        String listItemName = fileList.get(info.position);

        if(item.getTitle().equals("Delete"))
        {
            DeleteFile(info.position);
        }
        else if(item.getTitle().equals("Open"))
        {
            OpenFile(fileList.get(info.position));
        }
        else if (item.getTitle().equals("Email"))
        {
            EmailFile(fileList.get(info.position));
        }
        else if (item.getTitle().equals("Resend"))
        {
            ResendFile(fileList.get(info.position));
        }
        else
        {
            return false;
        }
        return true;
    }

}

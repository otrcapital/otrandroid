package com.mobile.otrcapitalllc.Activities;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.mobile.otrcapitalllc.Helpers.ActivityTags;
import com.mobile.otrcapitalllc.Helpers.HistoryFilesAdapter;
import com.mobile.otrcapitalllc.Helpers.PreferenceManager;
import com.mobile.otrcapitalllc.Models.HistoryInvoiceModel;
import com.mobile.otrcapitalllc.R;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class History extends ListActivity {
    private final Activity activity = this;
    @Bind(R.id.verifyUserGroup)
    LinearLayout verifyUserGroup;
    @Bind(R.id.verifyUserTV)
    TextView verifyUserTV;
    HistoryFilesAdapter adapter;
    private ArrayList<String> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        fileList = getFileList();
        adapter = new HistoryFilesAdapter(this, fileList);
        setListAdapter(adapter);
        registerForContextMenu(getListView());

        verifyUserGroup.setVisibility(View.INVISIBLE);
    }

    private ArrayList<String> getFileList() {
        File myDir = new File(ActivityTags.EXT_STORAGE_DIR);
        myDir.mkdirs();
        ArrayList<String> files = new ArrayList<>();

        for (File f : myDir.listFiles()) {
            if (f.isFile() && f != null) {
                files.add(f.getName().toString());
            }

        }

        return files;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        OpenFile(fileList.get(position));
    }

    public void OpenFile(final String fileName) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(ActivityTags.EXT_STORAGE_DIR + fileName)), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, "No app found for view pdf", Toast.LENGTH_LONG).show();
        }
    }

    public void EmailFile(final String fileName) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType("*/*");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(ActivityTags.EXT_STORAGE_DIR + fileName)));
        startActivity(Intent.createChooser(emailIntent, "Choose an email provider:"));
    }

    public void DeleteFile(int position) {
        File file = new File(ActivityTags.EXT_STORAGE_DIR + fileList.get(position));
        if (file.exists()) {
            file.delete();
        }

        adapter.RemoveFileFromList(position);
        adapter.notifyDataSetChanged();
    }

    public void ResendFile(final String fileName) {
        String jsonInvoice = PreferenceManager.with(this).getStringWithKey(fileName);
        if (jsonInvoice == null) {
            Toast.makeText(this, "Error with resend file", Toast.LENGTH_LONG).show();
            return;
        }
        final HistoryInvoiceModel model = new Gson().fromJson(jsonInvoice, HistoryInvoiceModel.class);

        String[] documentTypeArray;
        try {
            documentTypeArray = model.getDocumentTypesString().split("/");
        } catch (IndexOutOfBoundsException e) {
            documentTypeArray = new String[0];
        }
        final ArrayList<String> documentTypeList = new ArrayList<>();
        for (String s : documentTypeArray) {
            documentTypeList.add(s);
        }

        if (model.getStatus().equals("success")) {
            Toast.makeText(this, "Document has been sent already", Toast.LENGTH_LONG).show();
            return;
        }

        LoadDetails.UploadDocument(fileName, this, activity, verifyUserGroup, model.getApiInvoiceFromModel(), documentTypeList, model.getFactorType());

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.add(0, 0, 0, "Delete");//groupId, itemId, order, title
        menu.add(0, 1, 1, "Open");
        menu.add(0, 2, 2, "Email");
        menu.add(0, 3, 3, "Resend");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String listItemName = fileList.get(info.position);

        if (item.getTitle().equals("Delete")) {
            DeleteFile(info.position);
        } else if (item.getTitle().equals("Open")) {
            OpenFile(fileList.get(info.position));
        } else if (item.getTitle().equals("Email")) {
            EmailFile(fileList.get(info.position));
        } else if (item.getTitle().equals("Resend")) {
            ResendFile(fileList.get(info.position));
        } else {
            return false;
        }
        return true;
    }

}

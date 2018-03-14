package com.mobile.otrcapitalllc.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mobile.otrcapitalllc.Adapters.HistoryFilesAdapter;
import com.mobile.otrcapitalllc.Helpers.PreferenceManager;
import com.mobile.otrcapitalllc.Models.HistoryInvoiceModel;
import com.mobile.otrcapitalllc.R;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class History extends BaseActivity {

    @BindView(R.id.verifyUserGroup)
    LinearLayout verifyUserGroup;

    @BindView(R.id.verifyUserTV)
    TextView verifyUserTV;

    @BindView(R.id.list)
    ListView list;

    HistoryFilesAdapter adapter;
    private ArrayList<String> fileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);

        fileList = getFileList();

        adapter = new HistoryFilesAdapter(this, fileList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                OpenFile(fileList.get(i));
            }
        });
        registerForContextMenu(list);

        verifyUserGroup.setVisibility(View.INVISIBLE);
    }

    private ArrayList<String> getFileList() {
        File myDir = new File(Environment.getExternalStorageDirectory().toString());
        myDir.mkdirs();
        ArrayList<String> files = new ArrayList<>();

        if (myDir.listFiles() != null) {
            for (File f : myDir.listFiles()) {
                if (f.isFile() && PreferenceManager.with(this).getStringWithKey(f.getName()) != null) {
                    files.add(f.getName());
                }
            }
        }


        return files;
    }

    public void OpenFile(final String fileName) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".Helpers.GenericFileProvider", new File(Environment.getExternalStorageDirectory() + "/" + fileName)), "application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(this, getString(R.string.no_app_found_to_view_pdf), Toast.LENGTH_LONG).show();
        }
    }

    public void EmailFile(final String fileName) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType("application/pdf");
        emailIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".Helpers.GenericFileProvider", new File(Environment.getExternalStorageDirectory() + "/" + fileName)));
        startActivity(Intent.createChooser(emailIntent, getString(R.string.choose_email_provider)));
    }

    public void DeleteFile(int position) {
        File file = new File(Environment.getExternalStorageDirectory() + "/" + fileList.get(position));
        if (file.exists()) {
            file.delete();
        }

        adapter.RemoveFileFromList(position);
        adapter.notifyDataSetChanged();
    }

    public void ResendFile(final String fileName, final String brokerName) {
        String jsonInvoice = PreferenceManager.with(this).getStringWithKey(fileName);
        if (jsonInvoice == null) {
            Toast.makeText(this, getString(R.string.error_with_resend_file), Toast.LENGTH_LONG).show();
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
            Toast.makeText(this, getString(R.string.document_has_been_sent_already), Toast.LENGTH_LONG).show();
            return;
        }

        LoadDetails.UploadDocument(brokerName, fileName, this, this, verifyUserGroup, model.getApiInvoiceFromModel(), documentTypeList, model.getFactorType());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.add(0, 0, 0, getString(R.string.delete));//groupId, itemId, order, title
        menu.add(0, 1, 1, getString(R.string.open));
        menu.add(0, 2, 2, getString(R.string.email));
        menu.add(0, 3, 3, getString(R.string.resend));
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        String listItemName = fileList.get(info.position);
        String brokerName = "unknown";
        String jsonInvoice = PreferenceManager.with(this).getStringWithKey(listItemName);
        HistoryInvoiceModel model = new Gson().fromJson(jsonInvoice, HistoryInvoiceModel.class);

        if (model != null) {
            brokerName = model.getBrokerName();
        }

        if (item.getTitle().equals(getString(R.string.delete))) {
            DeleteFile(info.position);
        } else if (item.getTitle().equals(getString(R.string.open))) {
            OpenFile(fileList.get(info.position));
        } else if (item.getTitle().equals(getString(R.string.email))) {
            EmailFile(fileList.get(info.position));
        } else if (item.getTitle().equals(getString(R.string.resend))) {
            ResendFile(brokerName, fileList.get(info.position));
        } else {
            return false;
        }
        return true;
    }

}

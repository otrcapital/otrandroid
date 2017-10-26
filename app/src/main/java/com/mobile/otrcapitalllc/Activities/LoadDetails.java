package com.mobile.otrcapitalllc.Activities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.mobile.otrcapitalllc.Helpers.ActivityTags;
import com.mobile.otrcapitalllc.Helpers.CrashlyticsHelper;
import com.mobile.otrcapitalllc.Helpers.LogHelper;
import com.mobile.otrcapitalllc.Helpers.PermissionHelper;
import com.mobile.otrcapitalllc.Helpers.PreferenceManager;
import com.mobile.otrcapitalllc.Helpers.RealPathUtil;
import com.mobile.otrcapitalllc.Helpers.RestClient;
import com.mobile.otrcapitalllc.Models.ApiInvoiceDataJson;
import com.mobile.otrcapitalllc.Models.HistoryInvoiceModel;
import com.mobile.otrcapitalllc.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class LoadDetails extends Activity {
    private static final int TAKE_PICTURE = 1;
    private static final int REQUEST_CROP_PICTURE = 2;
    private static final int EDIT_IMAGE = 3;
    private static final int PICK_PICTURE = 4;
    private final Activity activity = this;
    @Bind(R.id.brokerNameTV)
    TextView brokerNameTV;
    @Bind(R.id.loadNumberET)
    TextView loadNumberET;
    @Bind(R.id.proofOfDeliveryCB)
    CheckBox proofOfDeliveryCB;
    @Bind(R.id.billOfLadingCB)
    CheckBox billOfLadingCB;
    @Bind(R.id.othersCB)
    CheckBox othersCB;
    @Bind(R.id.rateConfirmationCB)
    CheckBox rateConfirmationCB;
    @Bind(R.id.proofOfDeliveryGroup)
    RelativeLayout proofOdDeliveryGroup;
    @Bind(R.id.othersGroup)
    RelativeLayout othersGRoup;
    @Bind(R.id.billOfLadingGroup)
    RelativeLayout billOfLadingGroup;
    @Bind(R.id.rateConfirmationGroup)
    RelativeLayout rateConfirmationGroup;
    @Bind(R.id.verifyUserGroup)
    LinearLayout verifyUserGroup;
    @Bind(R.id.verifyUserTV)
    TextView verifyUserTV;
    private ArrayList<File> imageFiles = new ArrayList<File>();
    private ArrayList<String> stringArray = new ArrayList<String>();
    private File pdfFile;
    private String activityType;
    private String mcNumber, pKey, paymentOption, cellNumber;
    private float invoiceAmount, advReqAmount;
    private ArrayList<String> GalleryList = new ArrayList<String>();

    public static void UploadDocument(final String brokerName, final String FileName, final Context ContextActivity, final Activity activity, final View
            VerifyUserGroup, final ApiInvoiceDataJson invoiceData, final ArrayList<String> DocumentType, final String factorType) {

        TypedFile typedFile = new TypedFile("application/pdf", new File(ActivityTags.EXT_STORAGE_DIR + FileName));
        VerifyUserGroup.setVisibility(View.VISIBLE);
        String documentTypesString = "";
        for (String s : DocumentType) {
            documentTypesString += s + "/";
        }

        final HistoryInvoiceModel historyInvoiceModel = new HistoryInvoiceModel(invoiceData);
        historyInvoiceModel.setDocumentTypesString(documentTypesString);
        historyInvoiceModel.setFactorType(factorType);
        historyInvoiceModel.setBrokerName(brokerName);

        final String userCredentials = PreferenceManager.with(ContextActivity).getUserCredentials();

        RestClient restClient = new RestClient(ContextActivity, userCredentials);
        restClient.getApiService().Upload(invoiceData, DocumentType, typedFile, factorType, "android", new Callback<String>() {

            @Override
            public void success(String s, Response response) {
                Toast.makeText(ContextActivity, "Document upload success", Toast.LENGTH_LONG).show();
                VerifyUserGroup.setVisibility(View.INVISIBLE);
                String timeStamp = new SimpleDateFormat("dd/MM/yyy HH:mm:ss").format(new Date());
                historyInvoiceModel.setTimestamp(timeStamp);
                historyInvoiceModel.setStatus("success");

                String modelInfp = new Gson().toJson(historyInvoiceModel);
                PreferenceManager.with(ContextActivity).saveStringWithKey(FileName, modelInfp);
                if (factorType.equals("ADV")) {
                    PreferenceManager.with(ContextActivity).saveOpenAdvanceLoad(modelInfp);
                }

                Intent intent = new Intent(ContextActivity, History.class);
                activity.finish();
                ContextActivity.startActivity(intent);
            }

            @Override
            public void failure(RetrofitError error) {
                CrashlyticsHelper.logException(error);
                String toastText = "";
                try {
                    if (error.getResponse().getStatus() == 400) {
                        toastText = error.getResponse().getReason();
                    }
                    if (error.isNetworkError()) {
                        toastText = "Network error, server not responding";
                    }
                } catch (NullPointerException e) {
                    toastText = "Network error, please try again later";
                }

                LogHelper.logError(error.toString());

                Toast.makeText(ContextActivity, toastText, Toast.LENGTH_LONG).show();
                VerifyUserGroup.setVisibility(View.INVISIBLE);
                String timeStamp = new SimpleDateFormat("dd/MM/yyy HH:mm:ss").format(new Date());
                historyInvoiceModel.setTimestamp(timeStamp);
                historyInvoiceModel.setStatus("failure");

                String modelInfp = new Gson().toJson(historyInvoiceModel);
                PreferenceManager.with(ContextActivity).saveStringWithKey(FileName, modelInfp);
                if (factorType.equals("ADV")) {
                    PreferenceManager.with(ContextActivity).saveOpenAdvanceLoad(modelInfp);
                }

                Intent intent = new Intent(ContextActivity, History.class);
                activity.finish();
                ContextActivity.startActivity(intent);
            }
        });
    }

    @OnClick(R.id.uploadDocButton)
    public void uploadDocButton(View view) {
        if (GetDocumentTypes().isEmpty()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Please select atleast one Document Type").setCancelable(false).setPositiveButton
                    ("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        } else {
            createPDFfile();

            final String userEmail = PreferenceManager.with(this).getUserEmail();
            final String userPassword = PreferenceManager.with(this).getUserPassword();

            ApiInvoiceDataJson invoiceData = new ApiInvoiceDataJson();
            invoiceData.CustomerPKey = Integer.parseInt(pKey);
            invoiceData.CustomerMCNumber = mcNumber;
            invoiceData.PoNumber = loadNumberET.getText().toString();
            invoiceData.InvoiceAmount = invoiceAmount;
            invoiceData.ClientLogin = userEmail;
            invoiceData.ClientPassword = userPassword;
            invoiceData.AdvanceRequestType = paymentOption;
            if (activityType.equals(ActivityTags.TAG_FACTOR_ADVANCE)) {
                invoiceData.AdvanceRequestAmount = advReqAmount;
                invoiceData.Phone = cellNumber;
            }

            String factorType = "";
            if (activityType.equals(ActivityTags.TAG_FACTOR_ADVANCE))
                factorType = "ADV";
            else
                factorType = "FAC";

            UploadDocument(brokerNameTV.getText().toString(), pdfFile.getName(), this, activity, verifyUserGroup, invoiceData, GetDocumentTypes(), factorType);
        }

    }

    @OnClick(R.id.addPageButton)
    public void addPageButton(View view) {
        takePicture();
    }

    @OnClick(R.id.addPageButtonGallery)
    public void addPageButtonGallery(View view) {
        takePictureFromGallery();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_details);
        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        Bundle bundle = extras.getBundle("data_extra");
        try {
            activityType = bundle.getString(ActivityTags.TAG_ACTIVITY_TYPE);
        } catch (NullPointerException e) {
            activityType = "";
        }

        this.setTitle(activityType + " Details");

        setLayout(activityType);

        brokerNameTV.setText(bundle.getString(ActivityTags.TAG_BROKER_NAME));
        loadNumberET.setText(bundle.getString(ActivityTags.TAG_LOAD_NUMBER));
        mcNumber = bundle.getString(ActivityTags.TAG_MC_NUMBER);
        pKey = bundle.getString(ActivityTags.TAG_PKEY);
        paymentOption = bundle.getString(ActivityTags.TAG_PAYMENT_OPTION);
        invoiceAmount = bundle.getFloat(ActivityTags.TAG_INVOICE_AMOUNT);
        cellNumber = bundle.getString(ActivityTags.TAG_CELL_NUMBER);
        if (activityType.equals(ActivityTags.TAG_FACTOR_ADVANCE))
            advReqAmount = bundle.getFloat(ActivityTags.TAG_ADV_REQ_AMOUNT);
        verifyUserGroup.setVisibility(View.INVISIBLE);

        loadNumberET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(loadNumberET.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        String type = bundle.getString(ActivityTags.TAG_PHOTO_TYPE);
        if (ActivityTags.TAKE_PHOTO_TYPE.CAMERA.name().equals(type)) {
            takePicture();
        } else if (ActivityTags.TAKE_PHOTO_TYPE.GALLERY.name().equals(type)) {
            takePictureFromGallery();
        }
    }

    private void setLayout(String loadFactorAdvance) {
        switch (loadFactorAdvance) {
            case ActivityTags.TAG_FACTOR_LOAD:
                billOfLadingGroup.setVisibility(View.GONE);
                break;
            case ActivityTags.TAG_FACTOR_ADVANCE:
                proofOdDeliveryGroup.setVisibility(View.GONE);
                othersGRoup.setVisibility(View.GONE);
                break;
        }
    }

    private void takePicture() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        createImageFile();
        FillPhotoList();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFiles.get(imageFiles.size() - 1)));
        startActivityForResult(intent, TAKE_PICTURE);

    }

    private void takePictureFromGallery() {
        createImageFile();
        FillPhotoList();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PICTURE);
    }

    private void createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
        String imageFileName = "img_" + timeStamp + ".jpg";
        File myDir = new File(ActivityTags.TEMP_STORAGE_DIR);
        myDir.mkdirs();
        imageFiles.add(new File(myDir, imageFileName));
    }

    private void copyImageFile(String path) {
        if (!PermissionHelper.hasPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return;
        }

        try {
            FileChannel source;
            FileChannel destination;
            source = new FileInputStream(new File(path)).getChannel();
            destination = new FileOutputStream(imageFiles.get(imageFiles.size() - 1)).getChannel();
            if (destination != null && source != null) {
                destination.transferFrom(source, 0, source.size());
            }
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void createPDFfile() {
        LogHelper.logDebug("Creating PDF file");

        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
        String fileName[] = brokerNameTV.getText().toString().split("/");
        fileName[0] += "_" + timeStamp + ".pdf";
        pdfFile = new File(ActivityTags.EXT_STORAGE_DIR, fileName[0]);
        {
            Document document = new Document();

            try {
                PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
                document.setMargins(0, 0, 0, 0);
                document.open();

                for (int i = 0; i < imageFiles.size(); i++) {
                    Bitmap bitmap = BitmapFactory.decodeFile(imageFiles.get(i).getAbsolutePath());

                    Image image = Image.getInstance(imageFiles.get(i).getAbsolutePath());
                    image.scaleToFit(document.getPageSize());
                    document.add(image);

                    if (i < (imageFiles.size() - 1)) {
                        document.newPage();
                    }

                }

                document.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        File images = new File(ActivityTags.TEMP_STORAGE_DIR);
        deleteRecursive(images);
        LogHelper.logDebug("PDF file success");
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((requestCode == TAKE_PICTURE) && (resultCode == RESULT_OK)) {

            Uri selectedImage;
            try {
                selectedImage = Uri.fromFile(imageFiles.get(imageFiles.size() - 1));
            } catch (ArrayIndexOutOfBoundsException e) {
                selectedImage = Uri.fromFile(imageFiles.get(0));
            }

            getContentResolver().notifyChange(selectedImage, null);
            DeleteGalleryDuplicates();

            Intent intent = new Intent(this, CropImage.class);
            intent.putExtra("image_path", imageFiles.get(imageFiles.size() - 1).getAbsolutePath());
            startActivityForResult(intent, REQUEST_CROP_PICTURE);
        } else if ((requestCode == PICK_PICTURE) && (resultCode == RESULT_OK)) {
            String realPath;
            if (Build.VERSION.SDK_INT < 11) {
                realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());
            } else if (Build.VERSION.SDK_INT < 19) {
                realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());
            } else {
                try {
                    realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());
                }
            }

            copyImageFile(realPath);

            getContentResolver().notifyChange(data.getData(), null);
            DeleteGalleryDuplicates();

            Intent intent = new Intent(this, CropImage.class);
            intent.putExtra("image_path", imageFiles.get(imageFiles.size() - 1).getAbsolutePath());
            startActivityForResult(intent, REQUEST_CROP_PICTURE);
        } else if (requestCode == REQUEST_CROP_PICTURE) {
            // When we are done cropping, display it in the ImageView.

            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, RefineCapture.class);
                intent.putExtra("image_path", imageFiles.get(imageFiles.size() - 1).getAbsolutePath());
                startActivityForResult(intent, EDIT_IMAGE);
            }

        } else if (resultCode == RESULT_CANCELED) {
            finish();
        }

    }

    private ArrayList<String> GetDocumentTypes() {
        ArrayList<String> documentTypes = new ArrayList<String>();

        if (proofOfDeliveryCB.isChecked())
            documentTypes.add("pod");

        if (othersCB.isChecked())
            documentTypes.add("other");

        if (rateConfirmationCB.isChecked())
            documentTypes.add("rc");

        if (billOfLadingCB.isChecked())
            documentTypes.add("bol");

        return documentTypes;

    }

    private void FillPhotoList() {
        // initialize the list!
        GalleryList.clear();
        String[] projection = {MediaStore.Images.ImageColumns.DISPLAY_NAME};
        // intialize the Uri and the Cursor, and the current expected size.
        Cursor c = null;
        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        //
        // Query the Uri to get the data path.  Only if the Uri is valid.
        if (u != null) {
            c = managedQuery(u, projection, null, null, null);
        }

        // If we found the cursor and found a record in it (we also have the id).
        if ((c != null) && (c.moveToFirst())) {
            do {
                // Loop each and add to the list.
                GalleryList.add(c.getString(0));
            } while (c.moveToNext());
        }
    }

    private void DeleteGalleryDuplicates() {
        if (!PermissionHelper.hasPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            return;
        }
        File CurrentFile = imageFiles.get(imageFiles.size() - 1);
        // based on the result we either set the preview or show a quick toast splash.
        // Some versions of Android save to the MediaStore as well.  Not sure why!  We don't know what
        // name Android will give either, so we get to search for this manually and remove it.
        String[] projection = {MediaStore.Images.ImageColumns.SIZE, MediaStore.Images.ImageColumns.DISPLAY_NAME, MediaStore
                .Images.ImageColumns.DATA, BaseColumns._ID,};
        //
        // intialize the Uri and the Cursor, and the current expected size.
        Cursor c = null;
        Uri u = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        //
        if (CurrentFile != null) {
            // Query the Uri to get the data path.  Only if the Uri is valid,
            // and we had a valid size to be searching for.
            if ((u != null) && (CurrentFile.length() > 0)) {
                c = managedQuery(u, projection, null, null, null);
            }
            //
            // If we found the cursor and found a record in it (we also have the size).
            if ((c != null) && (c.moveToFirst())) {
                do {
                    // Check each area in the gallery we built before.
                    boolean bFound = false;
                    for (String sGallery : GalleryList) {
                        if (sGallery.equalsIgnoreCase(c.getString(1))) {
                            bFound = true;
                            break;
                        }
                    }
                    //
                    // To here we looped the full gallery.
                    if (!bFound) {
                        // This is the NEW image.  If the size is bigger, copy it.
                        // Then delete it!
                        File f = new File(c.getString(2));

                        // Ensure it's there, check size, and delete!
                        if ((f.exists()) && (CurrentFile.length() < c.getLong(0)) && (CurrentFile.delete())) {
                            // Finally we can stop the copy.
                            try {
                                CurrentFile.createNewFile();
                                FileChannel source = null;
                                FileChannel destination = null;
                                try {
                                    source = new FileInputStream(f).getChannel();
                                    destination = new FileOutputStream(CurrentFile).getChannel();
                                    destination.transferFrom(source, 0, source.size());
                                } finally {
                                    if (source != null) {
                                        source.close();
                                    }
                                    if (destination != null) {
                                        destination.close();
                                    }
                                }
                            } catch (IOException e) {
                                // Could not copy the file over.

                            }
                        }
                        //
                        ContentResolver cr = getContentResolver();
                        cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=" + c.getString(3), null);
                        break;
                    }
                } while (c.moveToNext());
            }
        }
    }

}

package com.mobile.otrcapitalllc.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.mobile.otrcapitalllc.Helpers.ActivityTags;
import com.mobile.otrcapitalllc.Helpers.LogHelper;
import com.mobile.otrcapitalllc.Helpers.PermissionHelper;
import com.mobile.otrcapitalllc.Helpers.PreferenceManager;
import com.mobile.otrcapitalllc.Helpers.RealPathUtil;
import com.mobile.otrcapitalllc.Models.HistoryInvoiceModel;
import com.mobile.otrcapitalllc.R;
import com.mobile.otrcapitalllc.Services.GetBrokers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainDashboard extends BaseActivity {

    private static final int TAKE_PICTURE = 11;
    private static final int REQUEST_CROP_PICTURE = 12;
    private static final int EDIT_IMAGE = 13;
    private static final int SHARE_IMAGE = 14;
    private static final int PICK_PICTURE = 15;
    @BindView(R.id.verifyUserGroup)
    LinearLayout verifyUserGroup;
    @BindView(R.id.verifyUserTV)
    TextView verifyUserTV;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private ArrayList<File> imageFiles = new ArrayList<>();
    private String activityType;

    @OnClick(R.id.factorLoadImgBtn)
    public void factorLoadImgBtn(View view) {
        List<HistoryInvoiceModel> list = PreferenceManager.with(this).getAdvanceLoadList();
//        if (list.size() > 0) {
//            OpenFuelAdvancesActivity.start(this);
//        }else {
//            Intent intent = new Intent(this, FactorAdvanceLoad.class);
//            intent.putExtra(ActivityTags.TAG_ACTIVITY_TYPE, ActivityTags.TAG_FACTOR_LOAD);
//            startActivity(intent);
//        }

        Intent intent = new Intent(this, FactorAdvanceLoad.class);
        intent.putExtra(ActivityTags.TAG_ACTIVITY_TYPE, ActivityTags.TAG_FACTOR_LOAD);
        startActivity(intent);
    }

    @OnClick(R.id.advanceLoadImgBtn)
    public void advanceLoadImgBtn(View view) {
        factorAdvanceLoad(ActivityTags.TAG_FACTOR_ADVANCE);
    }

    @OnClick(R.id.brokerCheckImgBtn)
    public void brokerCheckImgBtn(View view) {
        Intent intent = new Intent(MainDashboard.this, BrokerCheck.class);
        startActivity(intent);

    }

    @OnClick(R.id.historyImgBtn)
    public void historyImgBtn(View view) {
        if (PermissionHelper.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            openHistoryIntent();
        } else {
            getPermissionsHelper().checkStoragePermissions(new PermissionHelper.RequestResultListener() {

                @Override
                public void onShouldShowPermissionRationale() {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), R.string.permission_storage_rationale, Snackbar.LENGTH_INDEFINITE)
                            .setAction(android.R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getPermissionsHelper().requestStoragePermissions(MainDashboard.this);
                                }
                            }).show();
                }

                @Override
                public void onRequestResult(boolean granted) {
                    if (granted) {
                        openHistoryIntent();
                    } else {
                        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), R.string.permissions_not_granted, Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }
            }, this);
        }
    }

    private void openHistoryIntent() {
        Intent intent = new Intent(MainDashboard.this, History.class);
        startActivity(intent);
    }

    @OnClick(R.id.signOutImgBtn)
    public void signOutImgBtn(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.alert_logout));

        String positiveText = getString(R.string.btn_confirm);
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferenceManager.with(MainDashboard.this).saveTokenValid(false);

                        Intent intent = new Intent(MainDashboard.this, LoginScreen.class);
                        finish();
                        startActivity(intent);
                    }
                });

        String negativeText = getString(android.R.string.cancel);
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @OnClick(R.id.contactUsImgBtn)
    public void contactUsImgBtn(View view) {
        Intent intent = new Intent(MainDashboard.this, ContactUs.class);
        startActivity(intent);
    }

    @OnClick(R.id.scanImgBtn)
    public void scanBtnClick(View view) {
        if (PermissionHelper.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            showScanPromt();
        } else {
            getPermissionsHelper().checkStoragePermissions(new PermissionHelper.RequestResultListener() {

                @Override
                public void onShouldShowPermissionRationale() {
                    Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), R.string.permission_storage_rationale, Snackbar.LENGTH_INDEFINITE)
                            .setAction(android.R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getPermissionsHelper().requestStoragePermissions(MainDashboard.this);
                                }
                            }).show();
                }

                @Override
                public void onRequestResult(boolean granted) {
                    if (granted) {
                        showScanPromt();
                    } else {
                        Snackbar.make(getWindow().getDecorView().findViewById(android.R.id.content), R.string.permissions_not_granted, Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }
            }, this);
        }
    }

    private void showScanPromt() {
        CharSequence variants[] = new CharSequence[]{getString(R.string.scan_option_camera), getString(R.string.scan_option_gallery)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.scan_title));
        builder.setItems(variants, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    scanFromCamera();
                } else {
                    scanFromGallery();
                }
            }
        });
        builder.show();
    }

    private void scanFromCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        createImageFile();
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFiles.get(imageFiles.size() - 1)));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".Helpers.GenericFileProvider", imageFiles.get(imageFiles.size() - 1)));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    private void scanFromGallery() {
        createImageFile();

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PICTURE);
    }

    private void createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
        String imageFileName = "image_" + timeStamp + ".jpg";
        File myDir = new File(ActivityTags.TEMP_STORAGE_DIR);
        myDir.mkdirs();
        imageFiles.add(new File(myDir, imageFileName));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dashboard);
        ButterKnife.bind(this);

        LogHelper.logDebug("Getting customer list from server");

        if (PreferenceManager.with(this).getDbUpdateTimestamp() == 0) {
            Toast.makeText(this, "Setting up database, check notification bar for progress", Toast.LENGTH_LONG).show();
        }

        Intent intent = new Intent(MainDashboard.this, GetBrokers.class);
        startService(intent);
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

            //getContentResolver().notifyChange(selectedImage, null);

            ArrayList<Uri> imageUris = new ArrayList<>();
            imageUris.add(selectedImage);

            Intent intent = new Intent(this, CropImage.class);
            intent.putExtra("image_path", imageFiles.get(imageFiles.size() - 1).getAbsolutePath());
            startActivityForResult(intent, REQUEST_CROP_PICTURE);
        } else if ((requestCode == PICK_PICTURE) && (resultCode == RESULT_OK)) {
            String realPath;
            if (Build.VERSION.SDK_INT < 19) {
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

            Intent intent = new Intent(this, CropImage.class);
            intent.putExtra("image_path", imageFiles.get(imageFiles.size() - 1).getAbsolutePath());
            startActivityForResult(intent, REQUEST_CROP_PICTURE);
        } else if (requestCode == REQUEST_CROP_PICTURE) {
            if (resultCode == RESULT_OK) {

                Intent intent = new Intent(this, RefineCapture.class);
                intent.putExtra("image_path", imageFiles.get(imageFiles.size() - 1).getAbsolutePath());
                startActivityForResult(intent, EDIT_IMAGE);
            }
        } else if (requestCode == EDIT_IMAGE) {
            if (resultCode == RESULT_OK) {

                Uri selectedImage;
                try {
                    selectedImage = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".Helpers.GenericFileProvider", imageFiles.get(imageFiles.size() - 1));
                } catch (ArrayIndexOutOfBoundsException e) {
                    selectedImage = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".Helpers.GenericFileProvider", imageFiles.get(0));
                }

//                ArrayList<Uri> imageUris = new ArrayList<>();
//                imageUris.add(selectedImage);


                Document document = new Document();
                String dirpath = android.os.Environment.getExternalStorageDirectory().toString();
                try {
                    PdfWriter.getInstance(document, new FileOutputStream(dirpath + "/example.pdf")); //  Change pdf's name.
                    document.open();
                    Image img = Image.getInstance(getBytes(getContentResolver().openInputStream(selectedImage)));
                    float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                            - document.rightMargin() - 0) / img.getWidth()) * 100; // 0 means you have no indentation. If you have any, change it.
                    img.scalePercent(scaler);
                    img.setAlignment(Image.ALIGN_CENTER | Image.ALIGN_TOP);
                    document.add(img);
                    document.close();
                    String filename = "example.pdf";
                    File filelocation = new File(dirpath, filename);
                    Uri path = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".Helpers.GenericFileProvider", filelocation);
                    Intent emailIntent = new Intent(Intent.ACTION_SEND);
                    //emailIntent.setType("vnd.android.cursor.dir/email");
                    emailIntent.setType("application/pdf");
                    emailIntent.putExtra(Intent.EXTRA_STREAM, path);
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "DOCUMENT SENT USING OTR APP");
                    startActivityForResult(Intent.createChooser(emailIntent, "Share scan"), SHARE_IMAGE);

                } catch (BadElementException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                imageFiles.clear();

//                Intent shareIntent = new Intent();
//                shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
//                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
//                shareIntent.setType("image/*");
//                startActivityForResult(Intent.createChooser(shareIntent, "Share image:"), SHARE_IMAGE);
            }
        } else if (requestCode == SHARE_IMAGE) {
            File images = new File(ActivityTags.TEMP_STORAGE_DIR);
            deleteRecursive(images);
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();

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
            if (source != null) {
                destination.transferFrom(source, 0, source.size());
            }
            if (source != null) {
                source.close();
            }
            destination.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void factorAdvanceLoad(String activityType) {
        Intent intent = new Intent(MainDashboard.this, FactorAdvanceLoad.class);
        intent.putExtra(ActivityTags.TAG_ACTIVITY_TYPE, activityType);
        startActivity(intent);
    }
}

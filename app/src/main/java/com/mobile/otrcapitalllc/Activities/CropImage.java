package com.mobile.otrcapitalllc.Activities;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mobile.otrcapitalllc.Helpers.ActivityTags;
import com.mobile.otrcapitalllc.Helpers.DrawView;
import com.mobile.otrcapitalllc.Helpers.LogHelper;
import com.mobile.otrcapitalllc.Helpers.ScalingUtilities;
import com.mobile.otrcapitalllc.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CropImage extends Activity {
    @Bind(R.id.editImageView)
    ImageView editImageView;
    @Bind(R.id.executeBtn)
    ImageButton executeBtn;
    @Bind(R.id.undoBtn)
    ImageButton undoBtn;
    @Bind(R.id.EdgeCornersDV)
    DrawView EdgeCorners;
    Bitmap origBitmap, warpedBitmap;
    private String imagePath;
    private String cropDone = "crop";
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {

        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    LogHelper.logDebug("OpenCV loaded successfully");
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @OnClick(R.id.executeBtn)
    public void execute(View view) {
        if (cropDone.equals("crop")) {
            Bitmap bitmap = ((BitmapDrawable) editImageView.getDrawable()).getBitmap();
            AsyncTaskWarp warp = new AsyncTaskWarp();
            warp.execute(bitmap);
        } else if (cropDone.equals("done")) {
            saveOutput();
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }

    }

    @OnClick(R.id.undoBtn)
    public void undo(View view) {
        undoBtn.setEnabled(false);
        editImageView.setImageBitmap(origBitmap);
        undoBtn.setAlpha(0.5f);
        cropDone = "crop";
        executeBtn.setImageResource(R.drawable.icon_done);
        EdgeCorners.setVisibility(View.VISIBLE);

    }

    @OnClick(R.id.brightnessContrastBtn)
    public void launchBrightnessContrast(View view) {
        saveOutput();
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @OnClick(R.id.rotateBtn)
    public void rotateBtn(View view) {
        rotateBitmap(180);
        editImageView.setImageBitmap(origBitmap);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        ButterKnife.bind(this);

        imagePath = getIntent().getStringExtra("image_path");
        origBitmap = BitmapFactory.decodeFile(imagePath);
        if (origBitmap == null) {
            showErrorDialog();
        }
        if (origBitmap.getWidth() > origBitmap.getHeight()) {
            rotateBitmap(90);
        }

        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        }
        editImageView.setImageBitmap(origBitmap);
    }

    private void showErrorDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Error");
        adb.setMessage("Cannot open file from path");
        adb.setIcon(android.R.drawable.ic_dialog_alert);
        adb.setPositiveButton("OK", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent returnIntent = new Intent();
                setResult(RESULT_CANCELED, returnIntent);
                finish();
            }
        });
        adb.create().show();
    }

    private void rotateBitmap(int rotation) {
        int width = origBitmap.getWidth();
        int height = origBitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.preRotate(rotation);

        LogHelper.logDebug("Image width: " + width + " Height: " + height);

        Bitmap adjustedBitmap = Bitmap.createBitmap(origBitmap, 0, 0, width, height, matrix, true);
        origBitmap = adjustedBitmap;
    }

    private Bitmap warp(Bitmap image, Point topLeft, Point topRight, Point bottomLeft, Point bottomRight) {
        int resultWidth = (int) (topRight.x - topLeft.x);
        int bottomWidth = (int) (bottomRight.x - bottomLeft.x);
        if (bottomWidth > resultWidth) {
            resultWidth = bottomWidth;
        }

        int resultHeight = (int) (bottomLeft.y - topLeft.y);
        int bottomHeight = (int) (bottomRight.y - topRight.y);
        if (bottomHeight > resultHeight) {
            resultHeight = bottomHeight;
        }

        resultHeight = (int) Math.round(resultWidth * 1.41);

        Mat inputMat = new Mat(image.getHeight(), image.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(image, inputMat);
        Mat outputMat = new Mat(resultWidth, resultHeight, CvType.CV_8UC1);

        List<Point> source = new ArrayList<>();
        source.add(topLeft);
        source.add(topRight);
        source.add(bottomLeft);
        source.add(bottomRight);
        Mat startM = Converters.vector_Point2f_to_Mat(source);

        Point ocvPOut1 = new Point(0, 0);
        Point ocvPOut2 = new Point(resultWidth, 0);
        Point ocvPOut3 = new Point(0, resultHeight);
        Point ocvPOut4 = new Point(resultWidth, resultHeight);
        List<Point> dest = new ArrayList<>();
        dest.add(ocvPOut1);
        dest.add(ocvPOut2);
        dest.add(ocvPOut3);
        dest.add(ocvPOut4);
        Mat endM = Converters.vector_Point2f_to_Mat(dest);

        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(startM, endM);

        Imgproc.warpPerspective(inputMat, outputMat, perspectiveTransform, new Size(resultWidth, resultHeight));

        Bitmap output = Bitmap.createBitmap(resultWidth, resultHeight, Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(outputMat, output);
        return output;
    }

    private Point[] ScreenPoint2ImagePixels(android.graphics.Point[] ScreenPoints) {
        Drawable drawable = editImageView.getDrawable();
        Rect imageBounds = drawable.getBounds();

        //original height and width of the bitmap
        int intrinsicHeight = drawable.getIntrinsicHeight();
        int intrinsicWidth = drawable.getIntrinsicWidth();

        //height and width of the visible (scaled) image
        int scaledWidth = editImageView.getMeasuredWidth();
        int scaledHeight = editImageView.getMeasuredHeight();

        //Find the ratio of the original image to the scaled image
        double heightRatio = (double) intrinsicHeight / (double) scaledHeight;
        double widthRatio = (double) intrinsicWidth / (double) scaledWidth;
        Point[] ImagePixels = new Point[4];

        for (int i = 0; i < ImagePixels.length; i++) {
            //get the distance from the left and top of the image bounds
            int scaledImageOffsetX = ScreenPoints[i].x - imageBounds.left;
            int scaledImageOffsetY = ScreenPoints[i].y - imageBounds.top;

            //scale these distances according to the ratio of your scaling
            double originalImageOffsetX = scaledImageOffsetX * widthRatio;
            double originalImageOffsetY = scaledImageOffsetY * heightRatio;

            ImagePixels[i] = new Point(originalImageOffsetX, originalImageOffsetY);
        }

        return ImagePixels;
    }

    private void saveOutput() {
        String[] path = imagePath.split("/");
        String fname = path[path.length - 1];
        File file = new File(ActivityTags.TEMP_STORAGE_DIR, fname);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (warpedBitmap == null) {
                warpedBitmap = origBitmap;
            }
            warpedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class AsyncTaskWarp extends AsyncTask<Bitmap, Void, Bitmap> {
        Point[] points = new Point[4];
        LinearLayout progressIndicatorGroup = (LinearLayout) findViewById(R.id.progressIndicatorGroup);

        @Override
        protected Bitmap doInBackground(Bitmap... origImage) {
            try {
                LogHelper.logDebug(
                        String.format("Trying to warp bitmap (width: %s, height: %s)", origImage[0].getWidth(), origImage[0].getHeight()));
                Bitmap newBitmap = warp(origImage[0], points[0], points[1], points[3], points[2]);
                LogHelper.logDebug("Do in background done");
                return newBitmap;
            } catch (OutOfMemoryError oom) {
                LogHelper.logDebug("Crop image OutOfMemoryError in background task");
                LogHelper.logDebug("Trying to scale...");
                Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(origImage[0],
                        (int) (origImage[0].getWidth() / 1.5),
                        (int) (origImage[0].getHeight() / 1.5),
                        ScalingUtilities.ScalingLogic.FIT);
                LogHelper.logDebug(String.format("new width: %s, height: %s", scaledBitmap.getWidth(), scaledBitmap.getHeight()));
                Bitmap newBitmap = warp(scaledBitmap, points[0], points[1], points[3], points[2]);
                LogHelper.logDebug("Do in background done");
                return newBitmap;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressIndicatorGroup.setVisibility(View.VISIBLE);
            points = ScreenPoint2ImagePixels(EdgeCorners.getPoints());
            LogHelper.logDebug("Pre execute done");
        }

        @Override
        protected void onPostExecute(Bitmap warpedImage) {
            LogHelper.logDebug("Post execute started");

            super.onPostExecute(warpedImage);
            warpedBitmap = warpedImage;
            progressIndicatorGroup.setVisibility(View.INVISIBLE);
            editImageView.setImageBitmap(warpedImage);
            EdgeCorners.setVisibility(View.INVISIBLE);
            undoBtn.setEnabled(true);
            undoBtn.setAlpha(1.0f);
            executeBtn.setImageResource(R.drawable.icon_done_double);
            cropDone = "done";

            LogHelper.logDebug("Post execute done");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}

package com.mobile.otrcapitalllc.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.mobile.otrcapitalllc.Helpers.ActivityTags;
import com.mobile.otrcapitalllc.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RefineCapture extends Activity
{
    @Bind(R.id.editImageView)ImageView editImageView;
    @Bind(R.id.undoBtn)ImageButton undoBtn;
    private float contrast = 1.0f, brightness = 0.0f;
    private Bitmap imageBMP;
    private String imagePath;
    private List<PreValues> preValues = new ArrayList<PreValues>();

    @OnClick(R.id.brightnessIncreaseBtn)
    public void brightnessIncrease (View view)
    {
        preValues.add(new PreValues(brightness,contrast));

        if (brightness < 255)
        {
            brightness +=10;
        }

        if (brightness > 255)
        {
            brightness = 255;
        }

        undoBtn.setEnabled(true);
        undoBtn.setAlpha(1.0f);
        Bitmap new_bm = changeBitmapContrastBrightness(imageBMP,contrast,brightness);
        editImageView.setImageBitmap(new_bm);

    }

    @OnClick(R.id.brightnessDecreaseBtn)
    public void brightnessDecrease (View view)
    {
        preValues.add(new PreValues(brightness,contrast));

        if (brightness > -255)
        {
            brightness -=10;
        }

        if (brightness < -255)
        {
            brightness = -255;
        }

        undoBtn.setEnabled(true);
        undoBtn.setAlpha(1.0f);
        Bitmap new_bm = changeBitmapContrastBrightness(imageBMP,contrast,brightness);
        editImageView.setImageBitmap(new_bm);
    }

    @OnClick(R.id.contrastIncreaseBtn)
    public void contrastIncrease (View view)
    {
        preValues.add(new PreValues(brightness,contrast));

        if (contrast < 10)
        {
            contrast +=0.2f;
        }
        if (contrast > 10)
        {
            contrast = 10;
        }

        undoBtn.setEnabled(true);
        undoBtn.setAlpha(1.0f);
        Bitmap new_bm = changeBitmapContrastBrightness(imageBMP,contrast,brightness);
        editImageView.setImageBitmap(new_bm);

    }

    @OnClick(R.id.contrastDecreaseBtn)
    public void contrastDecrease (View view)
    {
        preValues.add(new PreValues(brightness,contrast));

        if (contrast > 0)
        {
            contrast -= 0.2f;
        }
        if (contrast < 0)
        {
            contrast = 0.1f;
        }

        undoBtn.setEnabled(true);
        undoBtn.setAlpha(1.0f);
        Bitmap new_bm = changeBitmapContrastBrightness(imageBMP,contrast,brightness);
        editImageView.setImageBitmap(new_bm);

    }

    @OnClick(R.id.undoBtn)
    public void undo (View view)
    {
        if (preValues.size() >= 1)
        {
            brightness = preValues.get(preValues.size()-1).Brightness;
            contrast = preValues.get(preValues.size()-1).Contrast;
            preValues.remove(preValues.size()-1);
            Bitmap new_bm = changeBitmapContrastBrightness(imageBMP,contrast,brightness);
            editImageView.setImageBitmap(new_bm);
        }

        else
        {
            undoBtn.setEnabled(false);
            undoBtn.setAlpha(0.5f);
        }

    }

    @OnClick(R.id.doneBtn)
    public void doneBtn (View view)
    {
        Bitmap new_bm = changeBitmapContrastBrightness(imageBMP,contrast,brightness);String[] path = imagePath.split("/");
        String fname = path[path.length-1];
        File file = new File (ActivityTags.TEMP_STORAGE_DIR, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            new_bm.compress(Bitmap.CompressFormat.JPEG, 60, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent returnIntent = new Intent();
        setResult(RESULT_OK,returnIntent);
        finish();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        imagePath = extras.getString("image_path");
        imageBMP = BitmapFactory.decodeFile(imagePath);
        setContentView(R.layout.activity_refine_capture);
        ButterKnife.bind(this);

        editImageView.setImageBitmap(imageBMP);
        undoBtn.setEnabled(false);

    }

    /**
     *
     * bmp input bitmap
     * contrast 0..10 1 is default
     * brightness -255..255 0 is default
     */
    private Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness)
    {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);

        return ret;
    }

    private class PreValues {
        public float Contrast, Brightness;

        public PreValues(float brightness, float contrast) {
            Contrast = contrast;
            Brightness = brightness;
        }
    }


}

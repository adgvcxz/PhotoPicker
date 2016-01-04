package com.adgvcxz.photopicker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.adgvcxz.photopicker.util.Util;
import com.adgvcxz.photopicker.views.ImageCropView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * zhaowei
 * Created by zhaowei on 15/12/30.
 */
public class CropActivity extends AppCompatActivity {

    private static final int ORIGIN_SIZE = 0;

    public static final String CROP_FILE_PATH = "CROP_FILE_PATH";
    public static final String CROP_MAX_WIDTH = "MAX_WIDTH";
    public static final String CROP_MAX_HEIGHT = "MAX_HEIGHT";
    public static final String CROP_QUALITY = "CROP_QUALITY";

    private ImageCropView mImageCropView;
    private File mCropFile;
    private int mMaxWidth;
    private int mMaxHeight;
    private int mQuality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_crop);
        mImageCropView = (ImageCropView) findViewById(R.id.ac_crop_image);
        Intent intent = getIntent();
        String path = intent.getStringExtra(CROP_FILE_PATH);
        Uri uri = intent.getData();
        mMaxHeight = intent.getIntExtra(CROP_MAX_HEIGHT, ORIGIN_SIZE);
        mMaxWidth = intent.getIntExtra(CROP_MAX_WIDTH, ORIGIN_SIZE);
        mQuality = intent.getIntExtra(CROP_QUALITY, 100);
        if (TextUtils.isEmpty(path) || mQuality <= 0 || mQuality > 100) {
            finish();
        }
        mCropFile = new File(path);
        mImageCropView.setImageFilePath(uri.getPath());
        if (mMaxHeight == ORIGIN_SIZE && mMaxWidth == ORIGIN_SIZE) {
            int[] size = Util.getImageSize(uri.getPath());
            mImageCropView.setAspectRatio(size[0], size[1]);
        } else {
            mImageCropView.setAspectRatio(mMaxWidth, mMaxHeight);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.crop_image) {
            Bitmap bitmap;
            if (mMaxWidth > ORIGIN_SIZE && mMaxHeight > ORIGIN_SIZE) {
                bitmap = mImageCropView.getCroppedImageMaxSize(mMaxWidth, mMaxHeight);
            } else {
                bitmap = mImageCropView.getCroppedImage();
            }
            new CropAsyn().execute(bitmap);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.crop, menu);
        return super.onCreateOptionsMenu(menu);
    }

    class CropAsyn extends AsyncTask<Bitmap, Void, Boolean> {

        @Override
        protected void onPostExecute(Boolean value) {
            if (value) {
                Intent intent = new Intent();
                intent.setData(Uri.fromFile(mCropFile));
                setResult(RESULT_OK, intent);
            }
            finish();
        }

        @Override
        protected Boolean doInBackground(Bitmap... params) {
            try {
                Bitmap bitmap = params[0];
                FileOutputStream fileOutputStream = new FileOutputStream(mCropFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                bitmap.recycle();
                System.gc();
                Runtime.getRuntime().gc();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}

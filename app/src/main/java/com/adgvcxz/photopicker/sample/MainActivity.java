package com.adgvcxz.photopicker.sample;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.adgvcxz.photopicker.OnPickPhotoListener;
import com.adgvcxz.photopicker.PhotoPicker;

import java.io.File;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnPickPhotoListener {

    private PhotoPicker mPhotoPicker;

    private File mDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        mDir = new File(Environment.getExternalStorageDirectory(), "PhotoPicker");
        if (!mDir.exists() || !mDir.isDirectory()) {
            mDir.mkdir();
        }
        findViewById(R.id.ac_main_camera).setOnClickListener(this);
        findViewById(R.id.ac_main_gallery).setOnClickListener(this);
        mPhotoPicker = new PhotoPicker.Builder(this)
                .setCameraCode(1)
                .setCameraFile(new File(mDir.getAbsolutePath() + "/abcd.jpg"))
                .setOnPickPhotoListener(this)
                .create();
        query();

        final String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};

        Cursor thumbnailsCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection, // Which columns to return
                null,       // Return all rows
                null,
                null);

//        int thumbnailColumnIndex = thumbnailsCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);

        while (thumbnailsCursor.moveToNext()) {
//            int thumbnailImageID = thumbnailsCursor.getInt(thumbnailColumnIndex);
            String path = thumbnailsCursor.getString(thumbnailsCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
            Log.e("zhaow", path);
            int imageId = thumbnailsCursor.getInt(thumbnailsCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            Log.e("zhaow", imageId + "aaaa");
            Cursor imagesCursor = getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI
                    , new String[]{MediaStore.Images.Thumbnails.DATA}, MediaStore.Images.Thumbnails._ID + "=?", new String[]{imageId + ""}, null);
            if (imagesCursor != null && imagesCursor.moveToFirst()) {
                int thumbnailColumnIndex = imagesCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
                int thumbnailImageID = imagesCursor.getInt(thumbnailColumnIndex);
                Log.e("zhaowwwww", imagesCursor.getString(thumbnailImageID));
            }
            imagesCursor.close();
//            Log.e("zhaowacd", uriToFullImage(thumbnailsCursor).getPath());
        }
        thumbnailsCursor.close();
    }

    private void query() {
        final String[] projection = {MediaStore.Images.Thumbnails.DATA,MediaStore.Images.Thumbnails.IMAGE_ID};

        Cursor thumbnailsCursor = getContentResolver().query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                projection, // Which columns to return
                null,       // Return all rows
                null,
                null);

        // Extract the proper column thumbnails
        int thumbnailColumnIndex = thumbnailsCursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);

        if (thumbnailsCursor.moveToFirst()) {
            do {
                // Generate a tiny thumbnail version.
                int thumbnailImageID = thumbnailsCursor.getInt(thumbnailColumnIndex);
                String imageId = thumbnailsCursor.getString(thumbnailsCursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
                String thumbnailPath = thumbnailsCursor.getString(thumbnailImageID);
                Log.e("query", imageId + "   " + thumbnailPath);
            } while (thumbnailsCursor.moveToNext());
        }
        thumbnailsCursor.close();
    }

//    private Uri uriToFullImage(Cursor thumbnailsCursor){
//        String imageId = thumbnailsCursor.getString(thumbnailsCursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
//
//        // Request image related to this thumbnail
//        String[] filePathColumn = { MediaStore.Images.Media.DATA };
//        Cursor imagesCursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, filePathColumn, MediaStore.Images.Media._ID + "=?", new String[]{imageId}, null);
//
//        if (imagesCursor != null && imagesCursor.moveToFirst()) {
//            int columnIndex = imagesCursor.getColumnIndex(filePathColumn[0]);
//            String filePath = imagesCursor.getString(columnIndex);
//            imagesCursor.close();
//            return Uri.parse(filePath);
//        } else {
//            imagesCursor.close();
//            return Uri.parse("");
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPhotoPicker.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ac_main_gallery:
                mPhotoPicker.goToGallery(this);
                break;
            case R.id.ac_main_camera:
                mPhotoPicker.goToCamera(this);
                break;
        }
    }

    @Override
    public void onPickPhoto(File file) {
        Snackbar.make(findViewById(R.id.ac_main_layout), file.getAbsolutePath(), Snackbar.LENGTH_LONG)
                .setAction("ok", this).show();
    }
}

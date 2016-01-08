package com.adgvcxz.photopicker.sample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.adgvcxz.photopicker.OnPickMultiPhotoListener;
import com.adgvcxz.photopicker.OnPickPhotoListener;
import com.adgvcxz.photopicker.PhotoPicker;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnPickPhotoListener, OnPickMultiPhotoListener {


    private static final int CAMERA_REQUEST = 1;

    private PhotoPicker mPhotoPicker;

    private File mDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true).build();
        Fresco.initialize(this, config);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        mDir = new File(Environment.getExternalStorageDirectory(), "PhotoPicker");
        if (!mDir.exists() || !mDir.isDirectory()) {
            mDir.mkdir();
        }
        findViewById(R.id.ac_main_camera).setOnClickListener(this);
        findViewById(R.id.ac_main_camera_crop).setOnClickListener(this);
        findViewById(R.id.ac_main_gallery).setOnClickListener(this);
        findViewById(R.id.ac_main_gallery_crop).setOnClickListener(this);
        findViewById(R.id.ic_main_wechat_gallery).setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPhotoPicker.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ac_main_gallery:
                mPhotoPicker = new PhotoPicker.Builder()
                        .galleryCode(1)
                        .goToCamera(this, this);
                break;
            case R.id.ac_main_camera:
                mPhotoPicker = new PhotoPicker.Builder()
                        .cameraCode(CAMERA_REQUEST)
                        .cameraFile(new File(mDir.getAbsolutePath() + "/abcd.jpg"))
                        .goToGallery(this, this);
                break;
            case R.id.ac_main_camera_crop:
                mPhotoPicker = new PhotoPicker.Builder()
                        .galleryCode(CAMERA_REQUEST)
                        .crop()
                        .cropMaxSize(512, 512)
                        .cameraFile(new File(mDir.getAbsolutePath() + "/abcd.jpg"))
                        .cropFile(new File(mDir.getAbsolutePath() + "/abcde.jpg"))
                        .goToCamera(this, this);
                break;
            case R.id.ac_main_gallery_crop:
                mPhotoPicker = new PhotoPicker.Builder()
                        .galleryCode(CAMERA_REQUEST)
                        .crop()
                        .cropFile(new File(mDir.getAbsolutePath() + "/abcde.jpg"))
                        .goToGallery(this, this);
                break;
            case R.id.ic_main_wechat_gallery:
                mPhotoPicker = new PhotoPicker.Builder()
                        .multiPhoto(5)
                        .goToMultiPhotoGallery(this, this);
                break;
        }
    }

    @Override
    public void onPickPhoto(File file) {
        Snackbar.make(findViewById(R.id.ac_main_layout), file.getAbsolutePath(), Snackbar.LENGTH_LONG)
                .setAction("ok", this).show();
    }

    @Override
    public void onPickPhotos(ArrayList<File> files) {
    }
}

package com.adgvcxz.photopicker.sample;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
                .setCameraFile(new File(mDir.getAbsolutePath() + "/abcd.jpeg"))
                .setOnPickPhotoListener(this)
                .create();
    }

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

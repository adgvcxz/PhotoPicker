package com.adgvcxz.photopicker;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.adgvcxz.photopicker.views.PhotoPickerAdapter;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.util.ArrayList;

import static android.provider.MediaStore.Images.Media;

/**
 * zhaowei
 * Created by zhaowei on 16/1/6.
 */
public class PhotoPickerActivity extends AppCompatActivity {

    private ArrayList<String> mPaths;
    private RecyclerView mPhotoRecyclerView;
    private PhotoPickerAdapter mPhotoPickerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_photo_picker);
        setSupportActionBar((Toolbar) findViewById(R.id.ac_photo_picker_toolbar));
        mPhotoRecyclerView = (RecyclerView) findViewById(R.id.ac_photo_picker_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mPaths = new ArrayList<>();
        mPhotoPickerAdapter = new PhotoPickerAdapter(this);
        loadPhotos();
        mPhotoRecyclerView.setAdapter(mPhotoPickerAdapter);
        mPhotoPickerAdapter.setPaths(mPaths);

    }

    private void loadPhotos() {
        final String[] projection = {Media.DATA, Media.BUCKET_ID, Media.BUCKET_DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection, null, null
                , Media.DATE_ADDED + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                mPaths.add(cursor.getString(0));
            }
            cursor.close();
        }
    }
}

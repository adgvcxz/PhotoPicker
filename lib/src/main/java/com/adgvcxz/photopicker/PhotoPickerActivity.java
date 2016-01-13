package com.adgvcxz.photopicker;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.adgvcxz.photopicker.util.PhotoDir;
import com.adgvcxz.photopicker.views.PhotoPickerAdapter;

import java.io.File;
import java.util.ArrayList;

import static android.provider.MediaStore.Images.Media;

/**
 * zhaowei
 * Created by zhaowei on 16/1/6.
 */
public class PhotoPickerActivity extends AppCompatActivity implements PhotoPickerAdapter.OnSelectPhotoListener {

    public static final String MAX = "MAX";
    public static final String PATHS = "PATHS";
    public static final String CAMERA = "CAMERA";

    private ArrayList<PhotoDir> mPhotoDirs;
    private RecyclerView mPhotoRecyclerView;
    private PhotoPickerAdapter mPhotoPickerAdapter;
    private MenuItem mMenuItem;
    private File mPhotoFile;
    private TextView mPhotoDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_ac_photo_picker);
        setSupportActionBar((Toolbar) findViewById(R.id.picker_ac_photo_picker_toolbar));
        mPhotoRecyclerView = (RecyclerView) findViewById(R.id.picker_ac_photo_picker_recycler_view);
        mPhotoDir = (TextView) findViewById(R.id.picker_ac_photo_picker_photo_dir);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mPhotoDirs = new ArrayList<>();
        Intent intent = getIntent();
        String path = intent.getStringExtra(CAMERA);
        if (!TextUtils.isEmpty(path)) {
            mPhotoFile = new File(path);
        }
        mPhotoPickerAdapter = new PhotoPickerAdapter(this, intent.getIntExtra(MAX, 0), !TextUtils.isEmpty(path));
        loadPhotos();
        mPhotoRecyclerView.setAdapter(mPhotoPickerAdapter);
//        mPhotoPickerAdapter.setPaths(mPaths);
        mPhotoPickerAdapter.setOnSelectPhotoListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.picker_done) {
            Intent intent = new Intent();
            intent.putStringArrayListExtra(PATHS, mPhotoPickerAdapter.getSelected());
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.picker_done, menu);
        mMenuItem = menu.findItem(R.id.picker_done);
        return super.onCreateOptionsMenu(menu);
    }

    private void loadPhotos() {
        final String[] projection = {Media.DATA, Media.BUCKET_ID, Media.BUCKET_DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection, null, null
                , Media.DATE_ADDED + " DESC");
        if (cursor != null) {
            ArrayList<String> paths = new ArrayList<>();
            while (cursor.moveToNext()) {
                //TODO
                String path = cursor.getString(0);
                String id = cursor.getString(1);
                String name = cursor.getString(2);
                paths.add(cursor.getString(0));
            }
            cursor.close();
        }
    }

    @Override
    public void onPickNumber(int number, int max) {
        if (max > 0) {
            mMenuItem.setTitle(String.format(getString(R.string.picker_menu_done_max), number, max));
        } else {
            mMenuItem.setTitle(String.format(getString(R.string.picker_menu_done), number));
        }

    }

    @Override
    public void onClickCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        startActivityForResult(intent, PhotoPicker.DEFAULT_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PhotoPicker.DEFAULT_CAMERA && resultCode == RESULT_OK) {
            ArrayList<String> paths = new ArrayList<>();
            paths.add(mPhotoFile.getAbsolutePath());
            Intent intent = new Intent();
            intent.putStringArrayListExtra(PATHS, paths);
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}

package com.adgvcxz.photopicker;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.adgvcxz.photopicker.views.PhotoPickerAdapter;

import java.util.ArrayList;

import static android.provider.MediaStore.Images.Media;

/**
 * zhaowei
 * Created by zhaowei on 16/1/6.
 */
public class PhotoPickerActivity extends AppCompatActivity implements PhotoPickerAdapter.OnSelectPhotoListener {

    public static final String MAX = "MAX";
    public static final String PATHS = "PATHS";

    private ArrayList<String> mPaths;
    private RecyclerView mPhotoRecyclerView;
    private PhotoPickerAdapter mPhotoPickerAdapter;
    private MenuItem mMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_ac_photo_picker);
        setSupportActionBar((Toolbar) findViewById(R.id.ac_photo_picker_toolbar));
        mPhotoRecyclerView = (RecyclerView) findViewById(R.id.ac_photo_picker_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mPaths = new ArrayList<>();
        mPhotoPickerAdapter = new PhotoPickerAdapter(this, getIntent().getIntExtra(MAX, 0));
        loadPhotos();
        mPhotoRecyclerView.setAdapter(mPhotoPickerAdapter);
        mPhotoPickerAdapter.setPaths(mPaths);
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
            while (cursor.moveToNext()) {
                mPaths.add(cursor.getString(0));
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
}

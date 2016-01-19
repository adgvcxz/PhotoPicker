package com.adgvcxz.photopicker;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.adgvcxz.photopicker.util.PhotoDir;
import com.adgvcxz.photopicker.views.PhotoPickerAdapter;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.util.ArrayList;

import static android.provider.MediaStore.Images.Media;

/**
 * zhaowei
 * Created by zhaowei on 16/1/6.
 */
public class PhotoPickerActivity extends AppCompatActivity implements
        PhotoPickerAdapter.OnSelectPhotoListener, View.OnClickListener, Animation.AnimationListener, View.OnTouchListener {

    public static final String MAX = "MAX";
    public static final String PATHS = "PATHS";
    public static final String CAMERA = "CAMERA";

    private ArrayList<PhotoDir> mPhotoDirs;
    private RecyclerView mPhotoRecyclerView;
    private RecyclerView mDirRecyclerView;
    private View mBg;
    private PhotoPickerAdapter mPhotoPickerAdapter;
    private MenuItem mMenuItem;
    private File mPhotoFile;
    private TextView mPhotoDir;
    private int mIndex;
    private Animation mShowRecyclerAnim;
    private Animation mHideRecyclerAnim;
    private Animation mShowBgAnim;
    private Animation mHideBgAnim;
    private boolean mIsAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.picker_ac_photo_picker);
        setSupportActionBar((Toolbar) findViewById(R.id.picker_ac_photo_picker_toolbar));
        mPhotoRecyclerView = (RecyclerView) findViewById(R.id.picker_ac_photo_picker_recycler_view);
        mPhotoDir = (TextView) findViewById(R.id.picker_ac_photo_picker_photo_dir);
        mBg = findViewById(R.id.picker_ac_photo_dir_bg);
        mPhotoDir.setOnClickListener(this);
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
        mIndex = 0;
        mPhotoPickerAdapter.setPaths(mPhotoDirs.get(mIndex).getPhotos());
        mPhotoPickerAdapter.setOnSelectPhotoListener(this);
        mPhotoDir.setText(mPhotoDirs.get(mIndex).getName());
        mDirRecyclerView = (RecyclerView) findViewById(R.id.picker_ac_photo_dirs);
        mDirRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDirRecyclerView.setAdapter(new PhotoDirAdapter());
        mShowRecyclerAnim = AnimationUtils.loadAnimation(this, R.anim.picker_show_directory_recycler_anim);
        mHideRecyclerAnim = AnimationUtils.loadAnimation(this, R.anim.picker_hide_directory_recycler_anim);
        mShowBgAnim = AnimationUtils.loadAnimation(this, R.anim.picker_show_directory_bg_anim);
        mHideBgAnim = AnimationUtils.loadAnimation(this, R.anim.picker_hide_directory_bg_anim);
        mHideBgAnim.setAnimationListener(this);
        mShowBgAnim.setAnimationListener(this);
        mIsAnim = false;
        mBg.setOnTouchListener(this);
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
                String path = cursor.getString(0);
                String id = cursor.getString(1);
                String name = cursor.getString(2);
                paths.add(cursor.getString(0));
                PhotoDir photoDir = new PhotoDir(id, name);
                int index = mPhotoDirs.indexOf(photoDir);
                if (index >= 0) {
                    mPhotoDirs.get(index).addPhoto(path);
                } else {
                    photoDir.addPhoto(path);
                    photoDir.setCover(path);
                    mPhotoDirs.add(photoDir);
                }
            }
            PhotoDir allImages = new PhotoDir("", "All Images");
            allImages.setCover(paths.get(0));
            allImages.addAll(paths);
            mPhotoDirs.add(0, allImages);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.picker_ac_photo_picker_photo_dir) {
            if (!mIsAnim) {
                if (mBg.isShown()) {
                    hideDirectory();
                } else {
                    showDirectory();
                }
            }
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
        mIsAnim = true;
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation.equals(mHideBgAnim)) {
            mBg.setVisibility(View.GONE);
            mDirRecyclerView.setVisibility(View.GONE);
        }
        mIsAnim = false;
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!mIsAnim) {
                hideDirectory();
            }
        }
        return true;
    }

    class PhotoDirAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DirHolder(View.inflate(PhotoPickerActivity.this, R.layout.picker_item_photo_dir, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            DirHolder dirHolder = (DirHolder) holder;
            dirHolder.setPhotoDir(position);
        }

        @Override
        public int getItemCount() {
            return mPhotoDirs.size();
        }
    }

    class DirHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        SimpleDraweeView cover;
        TextView name;
        AppCompatCheckBox checkBox;
        PhotoDir photoDir;

        public DirHolder(View itemView) {
            super(itemView);
            cover = (SimpleDraweeView) itemView.findViewById(R.id.picker_item_photo_dir_cover);
            name = (TextView) itemView.findViewById(R.id.picker_item_photo_dir_name);
            checkBox = (AppCompatCheckBox) itemView.findViewById(R.id.picker_item_photo_dir_check);
            itemView.setOnClickListener(this);
        }

        public void setPhotoDir(int position) {
            this.photoDir = mPhotoDirs.get(position);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.fromFile(new File(photoDir.getCover())))
                    .setResizeOptions(new ResizeOptions(192, 192))
                    .build();
            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setOldController(cover.getController())
                    .setImageRequest(request)
                    .build();
            cover.setController(controller);
            name.setText(photoDir.getName());
            checkBox.setChecked(mIndex == position);
        }

        @Override
        public void onClick(View v) {
        }
    }

    private void showDirectory() {
        mDirRecyclerView.setVisibility(View.VISIBLE);
        mBg.setVisibility(View.VISIBLE);
        mDirRecyclerView.startAnimation(mShowRecyclerAnim);
        mBg.startAnimation(mShowBgAnim);
    }

    private void hideDirectory() {
        mDirRecyclerView.startAnimation(mHideRecyclerAnim);
        mBg.startAnimation(mHideBgAnim);
    }
}

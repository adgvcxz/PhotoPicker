package com.adgvcxz.photopicker.sample;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adgvcxz.photopicker.OnPickMultiPhotoListener;
import com.adgvcxz.photopicker.OnPickPhotoListener;
import com.adgvcxz.photopicker.PhotoPicker;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements View.OnClickListener
        , OnPickPhotoListener, OnPickMultiPhotoListener {


    private static final int CAMERA_REQUEST = 1;

    private PhotoPicker mPhotoPicker;

    private File mDir;

    private RecyclerView mRecyclerView;

    private ArrayList<File> mFiles;

    private PhotoAdapter mPhotoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true).build();
        Fresco.initialize(this, config);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.ac_main_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mPhotoAdapter = new PhotoAdapter(this);
        mRecyclerView.setAdapter(mPhotoAdapter);
        mFiles = new ArrayList<>();
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
        findViewById(R.id.ac_main_wechat_gallery).setOnClickListener(this);
        findViewById(R.id.ac_main_wechat_gallery_camera).setOnClickListener(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPhotoPicker.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ac_main_camera:
                mPhotoPicker = new PhotoPicker.Builder()
                        .cameraCode(CAMERA_REQUEST)
                        .cameraFile(new File(mDir.getAbsolutePath() + "/abcd.jpg"))
                        .goToCamera(this, this);
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
            case R.id.ac_main_gallery:
                mPhotoPicker = new PhotoPicker.Builder()
                        .galleryCode(1)
                        .goToGallery(this, this);
                break;
            case R.id.ac_main_gallery_crop:
                mPhotoPicker = new PhotoPicker.Builder()
                        .galleryCode(CAMERA_REQUEST)
                        .crop()
                        .cropFile(new File(mDir.getAbsolutePath() + "/abcde.jpg"))
                        .goToGallery(this, this);
                break;
            case R.id.ac_main_wechat_gallery:
                mPhotoPicker = new PhotoPicker.Builder()
                        .multiPhoto(5)
                        .goToMultiPhotoGallery(this, this);
                break;
            case R.id.ac_main_wechat_gallery_camera:
                mPhotoPicker = new PhotoPicker.Builder()
                        .multiPhoto(5)
                        .cameraFile(new File(mDir.getAbsolutePath() + "/abcd.jpg"))
                        .goToMultiPhotoGallery(this, this);
                break;
        }
    }

    @Override
    public void onPickPhoto(File file) {
        mFiles.clear();
        mFiles.add(file);
        mPhotoAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPickPhotos(ArrayList<File> files) {
        mFiles = files;
        mPhotoAdapter.notifyDataSetChanged();
    }

    class PhotoAdapter extends RecyclerView.Adapter {

        private LayoutInflater inflater;

        public PhotoAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(inflater.inflate(R.layout.item_photo, null)) {};
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SimpleDraweeView simpleDraweeView = (SimpleDraweeView) holder.itemView.findViewById(R.id.item_photo);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.fromFile(mFiles.get(position)))
                    .setResizeOptions(new ResizeOptions(256, 256))
                    .build();
            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setOldController(simpleDraweeView.getController())
                    .setImageRequest(request)
                    .build();
            simpleDraweeView.setController(controller);
        }

        @Override
        public int getItemCount() {
            return mFiles.size();
        }
    }
}

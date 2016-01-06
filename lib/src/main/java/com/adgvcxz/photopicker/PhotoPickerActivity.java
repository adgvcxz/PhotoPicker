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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setDownsampleEnabled(true).build();
        Fresco.initialize(this, config);
        setContentView(R.layout.ac_photo_picker);
        setSupportActionBar((Toolbar) findViewById(R.id.ac_photo_picker_toolbar));
        mPhotoRecyclerView = (RecyclerView) findViewById(R.id.ac_photo_picker_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mPaths = new ArrayList<>();
        loadPhotos();
        mPhotoRecyclerView.setAdapter(new PhotoAdapter());
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

    class PhotoAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(PhotoPickerActivity.this).inflate(R.layout.item_photo, null)) {
            };
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            SimpleDraweeView view = (SimpleDraweeView) holder.itemView.findViewById(R.id.item_photo_image);
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.fromFile(new File(mPaths.get(position))))
                    .setResizeOptions(new ResizeOptions(200, 200))
                    .build();
            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setOldController(view.getController())
                    .setImageRequest(request)
                    .build();
            view.setController(controller);
        }

        @Override
        public int getItemCount() {
            return mPaths.size();
        }
    }
}

package com.adgvcxz.photopicker.views;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adgvcxz.photopicker.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.File;
import java.util.ArrayList;

/**
 * zhaowei
 * Created by zhaowei on 16/1/7.
 */
public class PhotoPickerAdapter extends RecyclerView.Adapter {

    private static final int SHOW_WIDTH = 192;
    private static final int SHOW_HEIGHT = 192;
    private int mMax;
    private LayoutInflater mInflater;
    private ArrayList<String> mPaths;
    private ArrayList<String> mSelected;
    private OnSelectPhotoListener mOnSelectPhotoListener;

    public PhotoPickerAdapter(Context context, int max) {
        mInflater = LayoutInflater.from(context);
        mPaths = new ArrayList<>();
        mSelected = new ArrayList<>();
        mMax = max;
    }

    public void setPaths(ArrayList<String> paths) {
        mPaths = paths;
        notifyDataSetChanged();
    }

    public void setOnSelectPhotoListener(OnSelectPhotoListener listener) {
        mOnSelectPhotoListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoHolder(mInflater.inflate(R.layout.picker_item_photo, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PhotoHolder photoHolder = (PhotoHolder) holder;
        photoHolder.setPhoto(position, mPaths.get(position));
    }

    @Override
    public int getItemCount() {
        return mPaths.size();
    }

    public ArrayList<String> getSelected() {
        return mSelected;
    }

    public class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        SimpleDraweeView photo;
        View layout;
        AppCompatCheckBox checkBox;
        String path;
        int position;

        public PhotoHolder(View itemView) {
            super(itemView);
            photo = (SimpleDraweeView) itemView.findViewById(R.id.item_photo_image);
            layout = itemView.findViewById(R.id.item_photo_check_layout);
            checkBox = (AppCompatCheckBox) itemView.findViewById(R.id.item_photo_check);
            layout.setOnClickListener(this);
        }

        public void setPhoto(int position, String path) {
            this.path = path;
            this.position = position;
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.fromFile(new File(path)))
                    .setResizeOptions(new ResizeOptions(SHOW_WIDTH, SHOW_HEIGHT))
                    .build();
            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setOldController(photo.getController())
                    .setImageRequest(request)
                    .build();
            photo.setController(controller);
            checkBox.setChecked(mSelected.contains(path));
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.item_photo_check_layout) {
                boolean checked = checkBox.isChecked();
                if (mMax <= 0 || mSelected.size() < mMax || checked) {
                    checkBox.setChecked(!checked);
                    if (!checked) {
                        mSelected.add(path);
                    } else {
                        mSelected.remove(path);
                    }
                }
                if (mOnSelectPhotoListener != null) {
                    mOnSelectPhotoListener.onPickNumber(mSelected.size(), mMax);
                }
            }
        }
    }

    public interface OnSelectPhotoListener {
        void onPickNumber(int number, int max);
    }
}

package com.adgvcxz.photopicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.adgvcxz.photopicker.util.Util;

import java.io.File;

/**
 * zhaowei
 * Created by zhaowei on 15/12/29.
 */
public class PhotoPicker {

    public static final int DEFAULT_CAMERA = 10001;
    public static final int DEFAULT_GALLERY = 10002;
    public static final int DEFAULT_CROP = 10003;

    private int mCameraRequestCode;
    private int mGalleryRequestCode;
    private int mCropRequestCode;
    private File mPhotoFile;
    private OnPickPhotoListener mOnPickPhotoListener;
    private boolean mCrop;
    private int mMaxWidth;
    private int mMaxHeight;
    private File mCropFile;
    private Activity mActivity;

    public PhotoPicker() {
        mCameraRequestCode = DEFAULT_CAMERA;
        mGalleryRequestCode = DEFAULT_GALLERY;
        mCropRequestCode = DEFAULT_CROP;
    }

    public boolean goToCamera(Activity activity) {
        mActivity = activity;
        if (mPhotoFile != null || (mCrop && !TextUtils.isEmpty(mCropFile.getAbsolutePath()))) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
            activity.startActivityForResult(intent, mCameraRequestCode);
            return true;
        }
        return false;
    }

    public void goToGallery(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        activity.startActivityForResult(intent, mGalleryRequestCode);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mOnPickPhotoListener != null && resultCode == Activity.RESULT_OK) {
            if (requestCode == mCameraRequestCode) {
                if (mCrop) {
                    Intent intent = new Intent(mActivity, CropActivity.class);
                    intent.setData(Uri.fromFile(mPhotoFile));
                    intent.putExtra(CropActivity.CROP_MAX_HEIGHT, mMaxHeight);
                    intent.putExtra(CropActivity.CROP_MAX_WIDTH, mMaxWidth);
                    intent.putExtra(CropActivity.CROP_FILE_PATH, mCropFile.getAbsolutePath());
                    intent.putExtra(CropActivity.CROP_QUALITY, 100);
                    mActivity.startActivityForResult(intent, DEFAULT_CROP);
                } else {
                    mOnPickPhotoListener.onPickPhoto(mPhotoFile);
                }
            } else if (requestCode == mGalleryRequestCode) {
                if (data != null) {
                    Uri uri = data.getData();
                    String path = Util.getPath(mActivity, uri);
                    if (mCrop) {
                    } else {
                        mOnPickPhotoListener.onPickPhoto(new File(path));
                    }
                }
            } else if (requestCode == mCropRequestCode) {

            }
        }
    }

    public static class Builder {

        private PhotoPicker photoPicker;

        public static PhotoPicker Builder() {
            return new PhotoPicker();
        }

        public Builder setCameraCode(int code) {
            photoPicker.mCameraRequestCode = code;
            return this;
        }

        public Builder setGalleryCode(int code) {
            photoPicker.mGalleryRequestCode = code;
            return this;
        }

        public Builder setCameraFile(File file) {
            photoPicker.mPhotoFile = file;
            return this;
        }

        public Builder setCrop(boolean crop) {
            photoPicker.mCrop = crop;
            return this;
        }

        public Builder setCropMaxSize(int width, int height) {
            photoPicker.mMaxHeight = height;
            photoPicker.mMaxWidth = width;
            return this;
        }

        public Builder setCropFile(File cropFile) {
            photoPicker.mCropFile = cropFile;
            return this;
        }

        public Builder setCropRequestCode(int code) {
            photoPicker.mCropRequestCode = code;
            return this;
        }

        public Builder setOnPickPhotoListener(OnPickPhotoListener listener) {
            photoPicker.mOnPickPhotoListener = listener;
            return this;
        }

        public PhotoPicker create() {
            return photoPicker;
        }
    }
}

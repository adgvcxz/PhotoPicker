package com.adgvcxz.photopicker;

import android.app.Activity;
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
                mOnPickPhotoListener.onPickPhoto(mCropFile);
            }
        }
    }

    public static class Builder {

        private PhotoPicker photoPicker;

        public Builder() {
            photoPicker = new PhotoPicker();
        }

        public Builder cameraCode(int code) {
            photoPicker.mCameraRequestCode = code;
            return this;
        }

        public Builder galleryCode(int code) {
            photoPicker.mGalleryRequestCode = code;
            return this;
        }

        public Builder cameraFile(File file) {
            photoPicker.mPhotoFile = file;
            return this;
        }

        public Builder crop() {
            photoPicker.mCrop = true;
            return this;
        }

        public Builder cropMaxSize(int width, int height) {
            photoPicker.mMaxHeight = height < 0 ? 0 : height;
            photoPicker.mMaxWidth = width < 0 ? 0 : width;
            return this;
        }

        public Builder cropFile(File cropFile) {
            photoPicker.mCropFile = cropFile;
            return this;
        }

        public Builder cropRequestCode(int code) {
            photoPicker.mCropRequestCode = code;
            return this;
        }

        public Builder setOnPickPhotoListener(OnPickPhotoListener listener) {
            photoPicker.mOnPickPhotoListener = listener;
            return this;
        }

        public PhotoPicker goToCamera(Activity activity) {
            if (photoPicker.mPhotoFile != null || (photoPicker.mCrop && photoPicker.mCropFile != null
                    && !TextUtils.isEmpty(photoPicker.mCropFile.getAbsolutePath()))) {
                photoPicker.mActivity = activity;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoPicker.mPhotoFile));
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                activity.startActivityForResult(intent, photoPicker.mCameraRequestCode);
                return photoPicker;
            }
            return null;
        }

        public PhotoPicker goToGallery(Activity activity) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            activity.startActivityForResult(intent, photoPicker.mGalleryRequestCode);
            return photoPicker;
        }
    }
}

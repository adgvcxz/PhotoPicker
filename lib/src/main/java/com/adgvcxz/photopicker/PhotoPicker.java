package com.adgvcxz.photopicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.adgvcxz.photopicker.util.Util;

import java.io.File;

/**
 * zhaowei
 * Created by zhaowei on 15/12/29.
 */
public class PhotoPicker {

    public static final int DEFAULT_CAMERA = 10001;
    public static final int DEFAULT_GALLERY = 10002;

    private Context mContext;
    private int mCameraRequestCode;
    private int mGalleryRequestCode;
    private File mPhotoFile;
    private OnPickPhotoListener mOnPickPhotoListener;

    public PhotoPicker(Context context) {
        mContext = context;
        mCameraRequestCode = DEFAULT_CAMERA;
        mGalleryRequestCode = DEFAULT_GALLERY;
    }

    public boolean goToCamera(Activity activity) {
        if (mPhotoFile != null) {
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
        if (mOnPickPhotoListener != null) {
            if (resultCode == Activity.RESULT_OK && requestCode == mCameraRequestCode) {
                mOnPickPhotoListener.onPickPhoto(mPhotoFile);
            } else if (resultCode == Activity.RESULT_OK && requestCode == mGalleryRequestCode) {
                if (data != null) {
                    Uri uri = data.getData();
                    String path = Util.getPath(mContext, uri);
                    mOnPickPhotoListener.onPickPhoto(new File(path));
                }
            }
        }
    }

    public static class Builder {

        private PhotoPicker photoPicker;

        public Builder(Context context) {
            photoPicker = new PhotoPicker(context);
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

        public Builder setOnPickPhotoListener(OnPickPhotoListener listener) {
            photoPicker.mOnPickPhotoListener = listener;
            return this;
        }

        public PhotoPicker create() {
            return photoPicker;
        }
    }
}

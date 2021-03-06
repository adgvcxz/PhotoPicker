package com.adgvcxz.photopicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.adgvcxz.photopicker.util.Util;

import java.io.File;
import java.util.ArrayList;

/**
 * zhaowei
 * Created by zhaowei on 15/12/29.
 */
public class PhotoPicker {

    public static final int DEFAULT_CAMERA = 10001;
    public static final int DEFAULT_GALLERY = 10002;
    public static final int DEFAULT_CROP = 10003;
    public static final int DEFAULT_MULTI = 10004;

    private int mCameraRequestCode;
    private int mGalleryRequestCode;
    private int mCropRequestCode;
    private int mMultiRequestCode;
    private File mPhotoFile;
    private OnPickPhotoListener mOnPickPhotoListener;
    private OnPickMultiPhotoListener mOnPickMultiPhotoListener;
    private boolean mCrop;
    private int mMaxWidth;
    private int mMaxHeight;
    private File mCropFile;
    private Activity mActivity;
    private boolean mSystemCrop;
    private int mMaxPhoto;

    public PhotoPicker() {
        mCameraRequestCode = DEFAULT_CAMERA;
        mGalleryRequestCode = DEFAULT_GALLERY;
        mCropRequestCode = DEFAULT_CROP;
        mMultiRequestCode = DEFAULT_MULTI;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == mCameraRequestCode) {
                if (mCrop) {
                    goToCrop();
                } else {
                    if (mOnPickPhotoListener != null) {
                        mOnPickPhotoListener.onPickPhoto(mPhotoFile);
                    }
                }
            } else if (requestCode == mGalleryRequestCode) {
                if (data != null) {
                    Uri uri = data.getData();
                    String path = Util.getPath(mActivity, uri);
                    if (mCrop) {
                        mPhotoFile = new File(path);
                        goToCrop();
                    } else {
                        if (mOnPickPhotoListener != null) {
                            mOnPickPhotoListener.onPickPhoto(new File(path));
                        }
                    }
                }
            } else if (requestCode == mCropRequestCode) {
                if (mOnPickPhotoListener != null) {
                    mOnPickPhotoListener.onPickPhoto(mCropFile);
                }
            } else if (requestCode == mMultiRequestCode) {
                if (mOnPickMultiPhotoListener != null) {
                    ArrayList<File> files = new ArrayList<>();
                    ArrayList<String> paths = data.getStringArrayListExtra(PhotoPickerActivity.PATHS);
                    for (String s : paths) {
                        files.add(new File(s));
                    }
                    mOnPickMultiPhotoListener.onPickPhotos(files);
                }
            }
        }
    }

    private void goToCrop() {
        if (!mSystemCrop) {
            Intent intent = new Intent(mActivity, CropActivity.class);
            intent.setData(Uri.fromFile(mPhotoFile));
            intent.putExtra(CropActivity.CROP_MAX_HEIGHT, mMaxHeight);
            intent.putExtra(CropActivity.CROP_MAX_WIDTH, mMaxWidth);
            intent.putExtra(CropActivity.CROP_FILE_PATH, mCropFile.getAbsolutePath());
            intent.putExtra(CropActivity.CROP_QUALITY, 100);
            mActivity.startActivityForResult(intent, mCropRequestCode);
        } else {
            Intent intent = new Intent();
            intent.setAction("com.android.camera.action.CROP");
            intent.setDataAndType(Uri.fromFile(mPhotoFile), "image/*");
            intent.putExtra("crop", "true");
            if (mMaxWidth > 0 && mMaxHeight > 0) {
                intent.putExtra("aspectX", mMaxWidth);
                intent.putExtra("aspectY", mMaxHeight);
                intent.putExtra("outputX", mMaxWidth);
                intent.putExtra("outputY", mMaxHeight);
            } else {
                int[] size = Util.getImageSize(mPhotoFile.getAbsolutePath());
                intent.putExtra("aspectX", size[0]);
                intent.putExtra("aspectY", size[1]);
            }
            intent.putExtra("scale", true);
            intent.putExtra("noFaceDetection", true);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCropFile));
            mActivity.startActivityForResult(intent, mCropRequestCode);
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

        public Builder systemCrop() {
            photoPicker.mSystemCrop = true;
            return this;
        }

        public Builder multiPhoto(int max) {
            photoPicker.mMaxPhoto = max;
            return this;
        }

        public Builder multiCameraFile(File file) {
            photoPicker.mPhotoFile = file;
            return this;
        }

        public PhotoPicker goToCamera(Activity activity, OnPickPhotoListener listener) {
            if (photoPicker.mPhotoFile != null || (photoPicker.mCrop && photoPicker.mCropFile != null
                    && !TextUtils.isEmpty(photoPicker.mCropFile.getAbsolutePath()))) {
                photoPicker.mOnPickPhotoListener = listener;
                photoPicker.mActivity = activity;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoPicker.mPhotoFile));
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                activity.startActivityForResult(intent, photoPicker.mCameraRequestCode);
                return photoPicker;
            }
            return null;
        }

        public PhotoPicker goToGallery(Activity activity, OnPickPhotoListener listener) {
            photoPicker.mActivity = activity;
            photoPicker.mOnPickPhotoListener = listener;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            activity.startActivityForResult(intent, photoPicker.mGalleryRequestCode);
            return photoPicker;
        }

        public PhotoPicker goToMultiPhotoGallery(Activity activity, OnPickMultiPhotoListener listener) {
            photoPicker.mActivity = activity;
            photoPicker.mOnPickMultiPhotoListener = listener;
            Intent intent = new Intent(activity, PhotoPickerActivity.class);
            intent.putExtra(PhotoPickerActivity.MAX, photoPicker.mMaxPhoto);
            intent.putExtra(PhotoPickerActivity.CAMERA, photoPicker.mPhotoFile == null ? "" : photoPicker.mPhotoFile.getAbsolutePath());
            activity.startActivityForResult(intent, photoPicker.mMultiRequestCode);
            return photoPicker;
        }
    }
}

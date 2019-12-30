package edu.newhaven.krikorherlopian.android.vproperty;


import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.mikelau.croperino.AlertInterface;
import com.mikelau.croperino.CameraDialog;
import com.mikelau.croperino.CropImage;
import com.mikelau.croperino.CroperinoConfig;
import com.mikelau.croperino.CroperinoFileUtil;
import com.mikelau.croperino.InternalStorageContentProvider;

import java.io.File;
import java.io.IOException;


public class Croperino {

    static public Uri mImageCaptureUri;
    static public File mFileTemp;
    public static void runCropImage(File file, Activity ctx, boolean isScalable, int aspectX, int aspectY, int color, int bgColor) {
        Intent intent = new Intent(ctx, CropImage.class);
        intent.putExtra(CropImage.IMAGE_PATH, mFileTemp.getPath());
        intent.putExtra(CropImage.SCALE, isScalable);
        intent.putExtra(CropImage.ASPECT_X, aspectX);
        intent.putExtra(CropImage.ASPECT_Y, aspectY);
        intent.putExtra("color", color);
        intent.putExtra("bgColor", bgColor);
        ctx.startActivityForResult(intent, CroperinoConfig.REQUEST_CROP_PHOTO);
    }

    public static void prepareChooser(final Activity ctx, String message, int color) {
        CameraDialog.getConfirmDialog(ctx, ctx.getResources().getString(R.string.app_name),
                message,
                "CAMERA",
                "GALLERY",
                "CLOSE",
                color,
                true,
                new AlertInterface.WithNeutral() {
                    @Override
                    public void PositiveMethod(final DialogInterface dialog, final int id) {
                        if (CroperinoFileUtil.verifyCameraPermissions(ctx)) {
                            prepareCamera(ctx);
                        }
                    }

                    @Override
                    public void NeutralMethod(final DialogInterface dialog, final int id) {
                        if (CroperinoFileUtil.verifyStoragePermissions(ctx)) {
                            prepareGallery(ctx);
                        }
                    }

                    @Override
                    public void NegativeMethod(final DialogInterface dialog, final int id) {

                    }
                });
    }

    public static void prepareCamera(Activity ctx) {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Camera");
                if (!storageDir.exists()) {
                    storageDir.mkdir();
                }

                mFileTemp = File.createTempFile(CroperinoConfig.getsImageName(), ".jpg", storageDir);
                if (Uri.fromFile(CroperinoFileUtil.newCameraFile()) != null) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        mImageCaptureUri = FileProvider.getUriForFile(ctx,
                                ctx.getApplicationContext().getPackageName() + ".provider",
                                CroperinoFileUtil.newCameraFile());
                    } else {
                        mImageCaptureUri = Uri.fromFile(CroperinoFileUtil.newCameraFile());
                    }
                } else {
                    mImageCaptureUri = FileProvider.getUriForFile(ctx,
                            "edu.newhaven.krikorherlopian.android.vproperty.fileprovider",
                            mFileTemp);
                }
            } else {
                mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
            }

            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
            intent.putExtra("return-data", true);
            ctx.startActivityForResult(intent, CroperinoConfig.REQUEST_TAKE_PHOTO);
        } catch (Exception e) {
            if (e instanceof ActivityNotFoundException) {
                Log.d("Croperino", "Activity not found");
            } else if (e instanceof IOException) {
                Log.d("Croperino", "IO Exception");
            } else {
            }
        }
    }

    public static void prepareGallery(Activity ctx) {
        Intent i = new Intent(Intent.ACTION_PICK);
        i.setType("image/*");
        ctx.startActivityForResult(i, CroperinoConfig.REQUEST_PICK_FILE);
    }
}
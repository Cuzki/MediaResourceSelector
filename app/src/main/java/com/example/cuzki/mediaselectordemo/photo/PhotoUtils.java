/**
 * Created on 2016/8/11
 */
package com.example.cuzki.mediaselectordemo.photo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import java.io.File;
import java.io.IOException;

/**
 * <p>
 *
 * @author Cuzki
 */
public class PhotoUtils {
    public static final String LOCAL_FILE_PREFIX = "file://";
    public static final int TAKE_PICTURE = 0xFF1;
    public static final int PICK_ALBUM = 0xFF2;
    public static final int CROP_ALBUM = 0xFF3;
    public static final int OPEN_ALBUM = 0xFF4;
    public static final int SD_NOT_EXISTS = 0x0;
    public static final int CAPTURE_SUCCESS = 0x1;
    public static final int CANNOT_FIND_CAMERA = 0x2;
    public static final int CANNOT_CREATE_DIR = 0x3;
    public static final int MAKE_VIDEO = 0xFF5;

    public static final String VIDEO_THUMBNAIL_UPDATE_SUCCESSFUL = "album_loading_successful";

    private static final String DEFAULT_PIC_PATH = getDefaulstCacheDirInSdCard() + File.separator;

    public static String takePhoto(Context context) {
        String path = null;
        takePhoto(context, path = getFilePath());
        return path;
    }

    public static String getDefaulstCacheDirInSdCard() throws IllegalStateException {
        String sdCardPath = null;
        sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        return sdCardPath + File.separator + "media";
    }

    public static void takePhoto(Context context, String filePath) {
        if (!checkCameraHardware(context)) {
            return;
        }
        File mPhotoFile = new File(filePath);
        if (!mPhotoFile.exists()) {
            try {
                mPhotoFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
        intent.putExtra("xx", filePath);
        ((Activity) context).startActivityForResult(intent, TAKE_PICTURE);
    }

    public void pickAlbum(Context context) {
        final String IMAGE_TYPE = "image/*";
        Intent pickAlbum = new Intent(Intent.ACTION_PICK, null);
        pickAlbum.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGE_TYPE);
        ((Activity) context).startActivityForResult(pickAlbum, PICK_ALBUM);
    }

    /**
     * 部分机型没有支持 com.android.camera.action.CROP 这个action
     */
    @Deprecated
    public void cropImage(Context context, Uri uri, int outputX, int outputY) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, CROP_ALBUM);
        } else {
            //TODO throw exception
        }
    }

    public static String getFilePath() {
        long curTime = System.currentTimeMillis();
        File file = new File(DEFAULT_PIC_PATH);
        if (!file.isDirectory()) {
            file.mkdir();
        }
        return DEFAULT_PIC_PATH + curTime + "_" + "PIC.jpg";
    }

    public static String getFilePath(boolean isImage) {
        long curTime = System.currentTimeMillis();
        File file = new File(DEFAULT_PIC_PATH);
        if (!file.isDirectory()) {
            file.mkdir();
        }
        return DEFAULT_PIC_PATH + curTime + "_" + (isImage ? "PIC.jpg" : "VIDEO.mp4");
    }

    public static Uri getFileUriFromString(String filePath) {
        Uri uri = Uri.fromFile(new File(filePath));
        return uri;
    }


    public boolean pickAlbum(Fragment fragment) {
        final String IMAGE_TYPE = "image/*";
        Intent pickAlbum = new Intent(Intent.ACTION_PICK, null);
        pickAlbum.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                IMAGE_TYPE);
        Activity act = fragment.getActivity();
        if (act != null && pickAlbum.resolveActivity(act.getPackageManager()) != null) {
            fragment.startActivityForResult(pickAlbum, PICK_ALBUM);
            return true;
        }
        return false;
    }

    /**
     * 部分机型没有支持 com.android.camera.action.CROP 这个action
     */
    @Deprecated
    public boolean cropImage(Fragment fragment, Uri inputFileUri, int outputX, int outputY, String outputFilePath) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(inputFileUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        File photoFile = new File(outputFilePath);
        if (!photoFile.exists()) {
            try {
                photoFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        Activity act = fragment.getActivity();
        if (act != null && intent.resolveActivity(act.getPackageManager()) != null) {
            fragment.startActivityForResult(intent, CROP_ALBUM);
            return true;
        }
        return false;
    }


    /**
     * 拍摄视频（配置180s,0低品质）
     *
     * @param context   上下文
     * @param videoPath 视频保存路径
     */
    public static void makeVideo(Context context, String videoPath) {
        makeVideo(context, videoPath, 180, 0);
    }

    /**
     * 拍摄视频 请求码（PhotoUtil.MAKE_VIDEO）
     *
     * @param context   上下文
     * @param videoPath 视频保存路径
     * @param duration  录制时长（s）
     * @param quality   品质（0/1两个档位）
     */
    public static void makeVideo(Context context, String videoPath, int duration, int quality) {
        if (context == null || !checkCameraHardware(context)) {
            return;
        }
        File mPhotoFile = new File(videoPath);
        if (!mPhotoFile.exists()) {
            try {
                mPhotoFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, duration);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, quality);
        ((Activity) context).startActivityForResult(intent, MAKE_VIDEO);
    }

    public static Bitmap getVideoThumbnail(String videoPath, int kind) {
        Bitmap bitmap = null;
        // 获取视频的缩略图
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
//        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
//                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 检测设备是否有camera
     *
     * @param context
     * @return
     */
    private static boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 获取指定目录已使用空间(目录大小)
     * @param dir 目录
     * @return 已使用空间
     */
    public static String getUsedSizeStr(File dir){
        return formatSize2Str(getUsedSize(dir));
    }
    /**
     * 获取指定目录已使用空间(目录大小)
     * @param dir 目录
     * @return 已使用空间
     */
    public static long getUsedSize(File dir){
        if(!dir.isDirectory()) return 0;
        long totalSize = 0;
        for (File _file:dir.listFiles()){
            if(_file.isDirectory()){
                totalSize += getUsedSize(_file);
            }else{
                totalSize += _file.length();
            }
        }
        return totalSize;
    }
    /**
     * 将字节大小格式化为"*KB,*MB,*GB"显示
     *
     * @param size 需要格式化的字节数据
     * @return 格式化后的空格大小显示
     */
    public static String formatSize2Str(double size) {
        int iKB = 1024;
        long iMB = 1048576;
        long iGB = 1073741824;

        if (size < iKB)
            return String.format("%.2f B", size);

        if (size >= iKB && size < iMB)
            return String.format("%.2f KB", size / iKB);

        if (size >= iMB && size < iGB)
            return String.format("%.2f MB", size / iMB);

        if (size >= iGB)
            return String.format("%.2f GB", size / iGB);
        return String.format("%.2f bytes", size);
    }
}

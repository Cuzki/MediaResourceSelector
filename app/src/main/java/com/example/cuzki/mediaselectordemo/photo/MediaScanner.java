/**
 * Created on 2016/8/11
 */
package com.example.cuzki.mediaselectordemo.photo;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;

public class MediaScanner {

    public interface MediaCallBack {
        void onScannerStart();

        void onScannerEnd();
    }

    private MediaScannerConnection mediaScanConn = null;

    private MediaSannerClient client = null;

    private String filePath = null;

    private String fileType = null;

    private String[] filePaths = null;

    private MediaCallBack mediaCallBack;

    /**
     * 然后调用MediaScanner.scanFile("/sdcard/2.mp3");
     */
    public MediaScanner(Context context) {
        init(context);
    }

    public MediaScanner(Context context, MediaCallBack callBack) {
        init(context);
        mediaCallBack = callBack;
    }

    private void init(Context context) {
        if (context == null) {
            return;
        }
        //创建MusicSannerClient
        if (client == null) {
            client = new MediaSannerClient();
        }

        if (mediaScanConn == null) {
            mediaScanConn = new MediaScannerConnection(context, client);
        }
    }

    class MediaSannerClient implements
            MediaScannerConnection.MediaScannerConnectionClient {

        public void onMediaScannerConnected() {
            if (mediaCallBack != null) {
                mediaCallBack.onScannerStart();
            }
            if (filePath != null) {
                mediaScanConn.scanFile(filePath, fileType);
            }
            if (filePaths != null) {
                for (String file : filePaths) {
                    mediaScanConn.scanFile(file, fileType);
                }
            }
            filePath = null;
            fileType = null;
            filePaths = null;
        }

        public void onScanCompleted(String path, Uri uri) {
            // TODO Auto-generated method stub
            mediaScanConn.disconnect();
            mediaCallBack.onScannerEnd();
        }

    }

    /**
     * 扫描文件标签信息
     *
     * @param filepath 文件路径 eg:/sdcard/MediaPlayer/dahai.mp3
     * @param fileType 文件类型 eg: audio/mp3  media/*  application/ogg
     */
    public void scanFile(String filepath, String fileType) {

        this.filePath = filepath;

        this.fileType = fileType;
        //连接之后调用MusicSannerClient的onMediaScannerConnected()方法
        mediaScanConn.connect();
    }

    /**
     * @param filePaths 文件路径
     * @param fileType  文件类型
     */
    public void scanFile(String[] filePaths, String fileType) {

        this.filePaths = filePaths;

        this.fileType = fileType;

        mediaScanConn.connect();

    }

    public String getFilePath() {

        return filePath;
    }

    public void setFilePath(String filePath) {

        this.filePath = filePath;
    }

    public String getFileType() {

        return fileType;
    }

    public void setFileType(String fileType) {

        this.fileType = fileType;
    }


}
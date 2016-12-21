/**
 * Created on 2016/8/11
 */
package com.example.cuzki.mediaselectordemo.photo;

import java.io.Serializable;

/**
 * <p/>
 *
 * @author Cuzki
 */
public class MediaResourseBean implements Serializable {

    /**
     * 父文件夹名
     */
    private String fatherPath;

    /**
     * 真是路径
     */
    private String realPath;

    /**
     * 缩略图
     */
    private String thumbnails;

    public MediaResourseBean(String fatherPath, String thumbnails, String realPath) {
        this.fatherPath = fatherPath;
        this.thumbnails = thumbnails;
        this.realPath = realPath;
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }

    public String getFatherPath() {
        return fatherPath;
    }

    public void setFatherPath(String fatherPath) {
        this.fatherPath = fatherPath;
    }

    public String getThumbnails() {
        return thumbnails;
    }

    public void setThumbnails(String thumbnails) {
        this.thumbnails = thumbnails;
    }
}

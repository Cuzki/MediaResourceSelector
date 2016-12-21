/**
 * Created on 2016/5/11
 */
package com.example.cuzki.mediaselectordemo.photo;

/**
 * <p>
 *
 * @author Cuzki
 */
public class PhotoDetail {
    String path;
    String fatherName;
    boolean isChecked=false;
    public PhotoDetail(String path, String fatherName){
        this.path=path;
        this.fatherName=fatherName;
    }
}

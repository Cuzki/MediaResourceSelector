/**
 * Created on 2016/5/10
 */
package com.example.cuzki.mediaselectordemo.photo;

/**
 * <p/>
 *
 * @author Cuzki
 */
public interface ItemCallBack {

    AlbumSelectedResult doOnToggle(int position);

    void showPhoto(int positon, MediaResourseBean bean);
}

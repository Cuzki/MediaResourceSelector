/**
 * Created on 2016/8/11
 */
package com.example.cuzki.mediaselectordemo;

import android.content.Context;

/**
 * <p>
 *
 * @author Cuzki
 */
public class AppContextUtils  {

    private static Context scontext;

    public static  void init(Context context){
        scontext  = context;
    }

    public static Context getContext(){
        return scontext;
    }
}

package com.houxj.latticefont;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by Administrator on 2018/6/13.
 */

public class CommonUtil {

    //TODO 屏幕密度（像素比例）
    public static float getDisplayDensity(Context context){
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getResources().getDisplayMetrics();
        return dm.density;
    }
}

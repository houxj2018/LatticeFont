package com.houxj.latticefont.detection;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2018/6/13.
 */

public interface IDetectionResult {
    public void onResult(int code, Bitmap bmp);
}

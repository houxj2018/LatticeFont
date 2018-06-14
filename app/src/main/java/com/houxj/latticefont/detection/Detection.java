package com.houxj.latticefont.detection;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2018/6/13.
 */

public abstract class Detection {
    protected IDetectionResult mIDetectionResult;
    protected double mDarkValue;//深色闸值
    protected double mGrayValue;//灰色闸值
    protected double mLightGrayValue;//浅灰色闸值

    Detection(){
        mIDetectionResult = null;
        mDarkValue =0;
        mGrayValue = 0;
        mLightGrayValue = 0;
    }
    //TODO 进行边缘检测
    public abstract void detection(Bitmap originalBitmap);

    //TODO 设置出来结果回调
    public void setDetectionCallBack(IDetectionResult callBack){
        mIDetectionResult = callBack;
    }

    //TODO 设置闸值
    public void setValue(double dark, double gray, double lightgray){
        mDarkValue = dark;
        mGrayValue = gray;
        mLightGrayValue = lightgray;
    }
}

package com.houxj.latticefont.detection;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.houxj.latticefont.BitmapUtil;

/**
 * Created by Administrator on 2018/6/13.
 */

public class SobelDetection extends Detection {
    private final static String TAG = SobelDetection.class.getName();
    private Bitmap mOriginalBmp;
    private Bitmap temp;
    private double max;
    private int[] mmap;
    private double[] tmap;

    @Override
    public void detection(Bitmap originalBitmap) {
        mOriginalBmp = originalBitmap;
        new Thread(new Runnable() {
            @Override
            public void run() {
                handlerDetection();
            }
        }).start();
    }

    private void handlerDetection(){
        Log.i(TAG, "handlerDetection()");
        realDetection();
        Bitmap bitmap = tranDelectionBitmap();
        Log.i(TAG, "tranDelectionBitmap() bitmap=" + bitmap);
        if(null != mIDetectionResult){
            mIDetectionResult.onResult(0, bitmap);
        }
    }

    private void realDetection(){
        Log.i(TAG, "realDetection()");
        //将原图灰度化
        this.temp = BitmapUtil.toGrayscale(this.mOriginalBmp);
        //图片的宽高
        int w = temp.getWidth();
        int h = temp.getHeight();

        //存放灰度图个像素点的数值
        mmap = new int[w * h];
        //存放计算后各对应点的数值
        tmap = new double[w * h];

        //获取灰度图各像素点的数值，并赋给mmap数组
        temp.getPixels(mmap, 0, temp.getWidth(), 0, 0, temp.getWidth(),
                temp.getHeight());

        //保存数值最大的数
        max = -999;
        Log.i(TAG, "realDetection() w=" + w + " h=" + h);
        //进行计算
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                //计算横向数值
                double gx = GX(i, j, temp);
                //计算纵向数值
                double gy = GY(i, j, temp);
                //进行开方处理
                tmap[j * w + i] = Math.sqrt(gx * gx + gy * gy);
                //保存最大值
                if (max < tmap[j * w + i]) {
                    max = tmap[j * w + i];
                }
            }
        }
    }

    private Bitmap tranDelectionBitmap(){
        Log.i(TAG, "tranDelectionBitmap()" + mDarkValue
                + " mGrayValue=" + mGrayValue + " mLightGrayValue=" + mLightGrayValue);
        if(mDarkValue == 0 && mGrayValue == 0 && mLightGrayValue == 0){
            return temp;
        }

        int w = temp.getWidth();
        int h = temp.getHeight();
        //存放处理后的图象各像素点的数组
        int[] cmap = new int[w * h];
        Log.i(TAG, "tranDelectionBitmap()" +w + " h=" + h);

        //筛选计算
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (tmap[j * w + i] > max * mDarkValue) {
                    //如果大于阙值max*a，则保存灰度图该点的像素
                    cmap[j * w + i] = mmap[j * w + i];
                } else if (tmap[j * w + i] > max * mGrayValue) {
                    //否则如果大于阙值max*b，则保存灰度图该点的像素+50,(变淡)
                    cmap[j * w + i] = getColor(mmap[j * w + i], 50);
                } else if (tmap[j * w + i] > max * mLightGrayValue) {
                    //否则如果大于阙值max*c，则保存灰度图该点的像素+80,(变得更淡)
                    cmap[j * w + i] = getColor(mmap[j * w + i], 80);
                } else {
                    //否则该点为白色
                    cmap[j * w + i] = Color.WHITE;
                }
//                Log.i(TAG, String.format("x=%d y=%d value =0x%X",i,j,cmap[j * w + i]));
            }
        }
        //将筛选出来的结果生成bitmap
        return Bitmap.createBitmap(cmap, temp.getWidth(), temp.getHeight(), Bitmap.Config.ARGB_8888);
    }

    //处理颜色
    private int getColor(int color, int value) {
        int cr, cg, cb;
        cr = (color & 0x00ff0000) >> 16;
        cg = (color & 0x0000ff00) >> 8;
        cb = color & 0x000000ff;

        cr += value;
        cg += value;
        cb += value;

        if(cr > 255){
            cr = 255;
        }
        if(cg > 255){
            cg = 255;
        }
        if(cb > 255){
            cb = 255;
        }

        if(cr < 0){
            cr = 0;
        }
        if(cg < 0){
            cg = 0;
        }
        if(cb < 0){
            cb = 0;
        }
        return Color.argb(255, cr, cg, cb);
    }

    //获取横向的
    private static double GX(int x, int y, Bitmap bitmap) {
        double res = (-1) * getPixel(x - 1, y - 1, bitmap)
                + 1 * getPixel(x + 1, y - 1, bitmap)
                + (-Math.sqrt(2)) * getPixel(x - 1, y, bitmap)
                + Math.sqrt(2) * getPixel(x + 1, y, bitmap)
                + (-1) * getPixel(x - 1, y + 1, bitmap)
                + 1 * getPixel(x + 1, y + 1, bitmap);

        return res;
    }

    //获取纵向的
    private static double GY(int x, int y, Bitmap bitmap) {
        double res = 1 * getPixel(x - 1, y - 1, bitmap)
                + Math.sqrt(2) * getPixel(x, y - 1, bitmap)
                + 1 * getPixel(x + 1, y - 1, bitmap)
                + (-1) * getPixel(x - 1, y + 1, bitmap)
                + (-Math.sqrt(2)) * getPixel(x, y + 1, bitmap)
                + (-1) * getPixel(x + 1, y + 1, bitmap);

        return res;
    }

    //获取第x行第y列的色度
    private static double getPixel(int x, int y, Bitmap bitmap) {
        if (x < 0 || x >= bitmap.getWidth() || y < 0 || y >= bitmap.getHeight()) {
            return 0;
        }
        return bitmap.getPixel(x, y);
    }


    //END
}

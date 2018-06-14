package com.houxj.latticefont;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;


import java.util.Arrays;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.img_led)
    ImageView mimgLED;
    @BindView(R.id.edit_input)
    EditText meditInput;
    @BindView(R.id.img_info)
    ImageView mimgInfo;
    @BindView(R.id.img_info_zoom)
    ImageView mimgZoom;

    private final static int LED_W = 36;
    private final static int LED_H = 12;
    private final static int LIGHT_PIX = 8;
    private static int LIGHT_SIZE = 0;

    private byte[] mLEDData = new byte[LED_W*LED_H];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        LIGHT_SIZE = LIGHT_PIX*(int) CommonUtil.getDisplayDensity(this);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initLED();
    }

    private void handLed(final Bitmap bitmap){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mimgZoom.setImageBitmap(bitmap);
            }
        });

        byte[] data = getByteByBitmap(bitmap);
        StringBuilder builder = new StringBuilder();
        for (int x=0;x< LED_H; x++){
            builder.delete(0, builder.length());
            for (int y=0; y< LED_H; y++){
                if(data[x*LED_H + y] == 1) {
                    builder.append("*");
                }else{
                    builder.append(" ");
                }
            }
            Log.i("hou", builder.toString());
        }
    }

    public void onClickShow(View view){
        String temp = meditInput.getText().toString();
        if(temp.length() > 0) {
            Typeface mFace = null;//Typeface.createFromAsset(getAssets(),"ygyxsziti2.0.ttf");
            Log.i("hou", "onClickShow() " + CommonUtil.getDisplayDensity(this));
            Bitmap bitmap = drawText(temp.substring(0,1), LED_H * LIGHT_SIZE, mFace);
            mimgInfo.setImageBitmap(bitmap);
            byte[] data = getCharDetection(bitmap);
            if(null != data){
                fillDataInLEDData(0, data);
                initLED();
            }
        }
    }

    public void onClickDetection(View view){
        startActivity(new Intent(this,TestDetectionActivity.class));
    }

    CountDownTimer countDownTimer = null;
    public void onClickRanLED(View view){
        if(null == countDownTimer) {
            countDownTimer = new CountDownTimer(20000, 200) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Random random = new Random();
                    int index = random.nextInt(LED_W * LED_H - 1);
                    Log.i("hou", "onTick " +millisUntilFinished +" ind=" + index);
                    Arrays.fill(mLEDData, (byte)0);
                    mLEDData[index] = 1;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initLED();
                        }
                    });
                }

                @Override
                public void onFinish() {
                    Log.i("hou", "onFinish ");
                }
            };
        }
        countDownTimer.start();
        Log.i("hou", "onClickRanLED ");
    }

    private void initLED(){
        Log.i("hou", "initLED()");
        Bitmap bitmap = Bitmap.createBitmap(LED_W*LIGHT_SIZE,LED_H * LIGHT_SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.CYAN);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);

        canvas.drawColor(Color.BLACK);
        int cir_r = LIGHT_SIZE / 2;
        int index = 0;
        for (int y = 0; y< LED_H; y++){
            for (int x = 0; x < LED_W; x++){
                index = y * LED_W + x;
                paint.setStyle(Paint.Style.STROKE);
                if (index < mLEDData.length  && mLEDData[index] == 1){
                    paint.setStyle(Paint.Style.FILL);
                }
//                final RectF rectF = new RectF(x * LIGHT_SIZE + 2, y * LIGHT_SIZE + 2,
//                        x * LIGHT_SIZE + LIGHT_SIZE - 4, y * LIGHT_SIZE + LIGHT_SIZE - 4);
//                canvas.drawRoundRect(rectF,3, 3, paint);
                canvas.drawCircle((x * LIGHT_SIZE) + cir_r, (y * LIGHT_SIZE) + cir_r,cir_r - 2,paint);
            }
        }
        mimgLED.setImageBitmap(bitmap);
    }

    private byte[] getLattice(String chart){


        return null;
    }

    public static Bitmap drawText(String str, int size, Typeface typeface){
        boolean bdrawgz = true;
        Log.i("hou", "drawText() " + str + " size=" + size);
        Bitmap bitmap = Bitmap.createBitmap(size,size , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        //背景
        canvas.drawColor(Color.WHITE);
        //画一个格子看看
        if(bdrawgz) {
            Paint paint = new Paint();
            paint.reset();
            paint.setColor(Color.CYAN);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            int l = size / LED_H;
            for (int x = 1; x < LED_H; x++) {
                canvas.drawLine(x * l, 0, x * l, size, paint);
            }
            for (int y = 1; y < LED_H; y++) {
                canvas.drawLine(0, y * l, size, y * l, paint);
            }
        }
        //打印文字
        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(size* 1.0f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.BLACK);
        if(null != typeface) {
        textPaint.setTypeface(typeface);
        }
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
        float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom
        Rect rect = new Rect(0,0,size,size);
        int baseLineY = (int) (rect.centerY() - top/2 - bottom/2);//基线中间点的y轴计算公式
        Log.i("hou", "baseLineY=" + baseLineY);
        canvas.drawText(str, rect.centerX(), baseLineY,textPaint);// 设置bitmap上面的文字位置
        return bitmap;
    }

    private byte[] getByteByBitmap(Bitmap bitmap){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        byte[] data = new byte[w*h];
        for (int y=0; y< h; y++){
            for (int x=0; x< w; x++){
                data[y*w + x] = 0;
                Log.i("hou", String.format("x=%d y=%d value =0x%X bright=%.4f",x,y,bitmap.getPixel(x,y),getColorBrightness(bitmap.getPixel(x,y))));
                if(bitmap.getPixel(x,y) != Color.WHITE){
                    data[y*w + x] = 1;
                }
            }
        }
        return data;
    }

    public float getColorBrightness(int color){
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int V = Math.max(b, Math.max(r, g));
        return (V / 255.f);
    }

    private byte[] getCharDetection(Bitmap bitmap){
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        byte[] byRet = null;
        if(w == h) {
            int g = w / LED_H;
            int l = g / 4;
            int cx = 0;
            int cy = 0;
            int val =0;
            byRet = new byte[LED_H * LED_H];
            for (int y =0; y< LED_H;y++){
                for (int x =0; x< LED_H; x++){
                    cx = x * g;
                    cy = y * g;
                    byRet[y*LED_H + x] = 0;
                    val = 0;
                    val = bitmap.getPixel(cx + l,cy + l) != Color.WHITE? val +1:val;
                    val = bitmap.getPixel(cx+g - l,cy + l) != Color.WHITE? val +1:val;
                    val = bitmap.getPixel(cx + l, cy + g - l) != Color.WHITE? val +1:val;
                    val = bitmap.getPixel(cx +g - l, cy + g - l) != Color.WHITE? val +1:val;

                    if(val >= 1){
                        byRet[y*LED_H + x] = 1;
                    }
                }
            }
        }
        return byRet;
    }

    private void fillDataInLEDData(int index, byte[] data){
        int led_ind = 0;
        int cx = 0, cy = 0;
        int x =0, y= 0;
        for (int i = 0 ; i< data.length; i++){
            y = i / LED_H;
            x = i % LED_H;
            cx = index * LED_H + x;
            cy = y;
            led_ind = cy * LED_W + cx;
            mLEDData[led_ind] = data[i];
//            Log.i("hou", "fillDataInLEDData x=" + x + " y=" + y
//                    + " cx=" + cx + " cy=" + cy + "led_ind=" +led_ind + " val=" + mLEDData[led_ind]);
        }
    }

}

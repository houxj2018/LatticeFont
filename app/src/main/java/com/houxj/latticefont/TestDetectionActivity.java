package com.houxj.latticefont;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.houxj.latticefont.detection.IDetectionResult;
import com.houxj.latticefont.detection.SobelDetection;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/6/13.
 */

public class TestDetectionActivity extends Activity {

    @BindView(R.id.image_src)
    ImageView mImageViewSrc;
    @BindView(R.id.image_des)
    ImageView mImageViewDes;
    @BindView(R.id.text_info)
    TextView mTextInfo;

    SobelDetection sebel = new SobelDetection();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_detection);
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        sebel.setDetectionCallBack(new IDetectionResult() {
            @Override
            public void onResult(int code, Bitmap bmp) {
                setBitmap(bmp);
            }
        });
    }

    public void onClickTran(View view){
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.timg);
//        Bitmap bitmap = MainActivity.drawText("国",24, null);
        mImageViewSrc.setImageBitmap(bitmap);
        mTextInfo.setText("开始转换中");
        sebel.setValue(0.25,0.08,0.06);
        sebel.detection(bitmap);
    }

    private void setBitmap(final Bitmap bitmap){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mImageViewDes.setImageBitmap(bitmap);
                mTextInfo.setText("转换完成");
            }
        });
    }

}

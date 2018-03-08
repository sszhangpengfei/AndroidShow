package com.zpf.androidshow;

import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zpf.androidshow.screen.Constant;
import com.zpf.androidshow.screen.ScreenRecord;

/**
 * Created by zpf on 2018/3/6
 *
 * MediaProjectionManager流程
 * 1.调用Context.getSystemService()方法即可获取MediaProjectionManager实例
 * 2.调用MediaProjectionManager对象的createScreenCaptureIntent()方法创建一个屏幕捕捉的Intent
 * 3.调用startActivityForResult()方法启动第2步得到的Intent，这样即可启动屏幕捕捉的Intent
 * 4.重写onActivityResult()方法，在该方法中通过MediaProjectionManager对象来获取MediaProjection对象，在该对象中即可获取被捕获的屏幕
 * **/
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final int REQUEST_CODE_A = 10001;

    private Button start_record,stop_record;

    private MediaProjectionManager mMediaProjectionManager;

    private ScreenRecord mScreenRecord;

    private boolean isRecording = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitView();
        InitMPManager();
    }

    /**
     * 初始化View
     * **/
    private void InitView(){
        start_record = findViewById(R.id.start_record);
        start_record.setOnClickListener(this);
        stop_record = findViewById(R.id.stop_record);
        stop_record.setOnClickListener(this);
    }

    /**
     * 初始化MediaProjectionManager
     * **/
    private void InitMPManager(){
        mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
    }


    /**
     * 开始截屏
     * **/
    private void StartScreenCapture(){
        isRecording = true;
        Intent captureIntent = mMediaProjectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, REQUEST_CODE_A);
    }


    /**
     * 停止截屏
     * **/
    private void StopScreenCapture(){
        isRecording = false;
        mScreenRecord.release();
    }


    /**
     *
     * **/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        try {
            MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            if(mediaProjection == null){
                Toast.makeText(this,"程序发生错误:MediaProjection@1",Toast.LENGTH_SHORT).show();
                return;
            }
            mScreenRecord = new ScreenRecord(this,mediaProjection);
            mScreenRecord.start();


        }
        catch (Exception e){

        }

    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.start_record:
                StartScreenCapture();
                break;
            case R.id.stop_record:
                StopScreenCapture();
                break;
        }
    }
}

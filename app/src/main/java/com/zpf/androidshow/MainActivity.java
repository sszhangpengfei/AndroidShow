package com.zpf.androidshow;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.zpf.androidshow.media.h264data;
import com.zpf.androidshow.rtsp.RtspServer;
import com.zpf.androidshow.screen.Constant;
import com.zpf.androidshow.screen.ScreenRecord;

import java.util.Locale;
import java.util.concurrent.ArrayBlockingQueue;

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

    private TextView line2;

    private MediaProjectionManager mMediaProjectionManager;

    private ScreenRecord mScreenRecord;

    private boolean isRecording = false;

    private static int yuvqueuesize = 10;
    public static ArrayBlockingQueue<h264data> h264Queue = new ArrayBlockingQueue<>(yuvqueuesize);
    private RtspServer mRtspServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InitView();
        InitMPManager();
        displayIpAddress();
    }

    private ServiceConnection mRtspServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mRtspServer = ((RtspServer.LocalBinder)service).getService();
            mRtspServer.addCallbackListener(mRtspCallbackListener);
            mRtspServer.start();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {}

    };

    @Override
    protected void onResume(){
        super.onResume();


    }

    private RtspServer.CallbackListener mRtspCallbackListener = new RtspServer.CallbackListener() {

        @Override
        public void onError(RtspServer server, Exception e, int error) {
            // We alert the user that the port is already used by another app.
            if (error == RtspServer.ERROR_BIND_FAILED) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Port already in use !")
                        .setMessage("You need to choose another port for the RTSP server !")
                        .show();
            }
        }

        @Override
        public void onMessage(RtspServer server, int message) {
            if (message==RtspServer.MESSAGE_STREAMING_STARTED) {
//                if (mAdapter != null && mAdapter.getHandsetFragment() != null)
//                    mAdapter.getHandsetFragment().update();

            } else if (message==RtspServer.MESSAGE_STREAMING_STOPPED) {
//                if (mAdapter != null && mAdapter.getHandsetFragment() != null)
//                    mAdapter.getHandsetFragment().update();
            }
        }

    };


    public static void putData(byte[] buffer, int type,long ts) {
        if (h264Queue.size() >= 10) {
            h264Queue.poll();
        }
        h264data data = new h264data();
        data.data = buffer;
        data.type = type;
        data.ts = ts;
        h264Queue.add(data);
    }

    /**
     * 初始化View
     * **/
    private void InitView(){
        start_record = findViewById(R.id.start_record);
        start_record.setOnClickListener(this);
        stop_record = findViewById(R.id.stop_record);
        stop_record.setOnClickListener(this);
        line2 = (TextView)findViewById(R.id.line2);
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
        bindService(new Intent(this,RtspServer.class), mRtspServiceConnection, Context.BIND_AUTO_CREATE);
    }


    /**
     * 停止截屏
     * **/
    private void StopScreenCapture(){
        isRecording = false;
        mScreenRecord.release();
        if (mRtspServer != null) mRtspServer.removeCallbackListener(mRtspCallbackListener);
        unbindService(mRtspServiceConnection);
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


    private void displayIpAddress() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String ipaddress = null;
        if (info!=null && info.getNetworkId()>-1) {
            int i = info.getIpAddress();
            String ip = String.format(Locale.ENGLISH,"%d.%d.%d.%d", i & 0xff, i >> 8 & 0xff,i >> 16 & 0xff,i >> 24 & 0xff);
            line2.setText("rtsp://");
            line2.append(ip);
            line2.append(":8086");
        }

    }
}

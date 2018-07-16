package com.ywl5320.mymusic;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ywl5320.myplayer.WlTimeInfoBean;
import com.ywl5320.myplayer.listener.WlOnCompleteListener;
import com.ywl5320.myplayer.listener.WlOnErrorListener;
import com.ywl5320.myplayer.listener.WlOnLoadListener;
import com.ywl5320.myplayer.listener.WlOnParparedListener;
import com.ywl5320.myplayer.listener.WlOnPauseResumeListener;
import com.ywl5320.myplayer.listener.WlOnTimeInfoListener;
import com.ywl5320.myplayer.log.MyLog;
import com.ywl5320.myplayer.opengl.WlGLSurfaceView;
import com.ywl5320.myplayer.player.MuteEnum;
import com.ywl5320.myplayer.player.WlPlayer;
import com.ywl5320.myplayer.util.WlTimeUtil;


public class MainActivity extends AppCompatActivity {
    private WlPlayer wlPlayer;
    private TextView tvTime;
    private WlGLSurfaceView wlGLSurfaceView;
    private SeekBar seekBar;
    private int position;
    private boolean seek = false;

    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }

        

        tvTime = findViewById(R.id.tv_time);
        wlGLSurfaceView = findViewById(R.id.wlglsurfaceview);
        seekBar = findViewById(R.id.seekbar);
        wlPlayer = new WlPlayer();
        wlPlayer.setWlGLSurfaceView(wlGLSurfaceView);
        wlPlayer.setWlOnParparedListener(new WlOnParparedListener() {
            @Override
            public void onParpared() {
                MyLog.d("准备好了，可以开始播放声音了");
                wlPlayer.start();
            }
        });
        wlPlayer.setWlOnLoadListener(new WlOnLoadListener() {
            @Override
            public void onLoad(boolean load) {
                if (load) {
                    MyLog.d("加载中...");
                } else {
                    MyLog.d("播放中...");
                }
            }
        });

        wlPlayer.setWlOnPauseResumeListener(new WlOnPauseResumeListener() {
            @Override
            public void onPause(boolean pause) {
                if (pause) {
                    MyLog.d("暂停中...");
                } else {
                    MyLog.d("播放中...");
                }
            }
        });

        wlPlayer.setWlOnTimeInfoListener(new WlOnTimeInfoListener() {
            @Override
            public void onTimeInfo(WlTimeInfoBean timeInfoBean) {
//                MyLog.d(timeInfoBean.toString());
                Message message = Message.obtain();
                message.what = 1;
                message.obj = timeInfoBean;
                handler.sendMessage(message);

            }
        });

        wlPlayer.setWlOnErrorListener(new WlOnErrorListener() {
            @Override
            public void onError(int code, String msg) {
                MyLog.d("code:" + code + ", msg:" + msg);
            }
        });

        wlPlayer.setWlOnCompleteListener(new WlOnCompleteListener() {
            @Override
            public void onComplete() {
                MyLog.d("播放完成了");
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                position = progress * wlPlayer.getDuration() / 100;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seek = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                wlPlayer.seek(position);
                seek = false;
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }

    public void begin(View view) {
        wlPlayer.setSource("/sdcard/qme-test/test.mp4");
        wlPlayer.parpared();
    }

    public void pause(View view) {

        wlPlayer.pause();

    }

    public void resume(View view) {
        wlPlayer.resume();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                WlTimeInfoBean wlTimeInfoBean = (WlTimeInfoBean) msg.obj;
                tvTime.setText(WlTimeUtil.secdsToDateFormat(wlTimeInfoBean.getTotalTime(), wlTimeInfoBean.getTotalTime())
                        + "/" + WlTimeUtil.secdsToDateFormat(wlTimeInfoBean.getCurrentTime(), wlTimeInfoBean.getTotalTime()));


                if (!seek && wlTimeInfoBean.getTotalTime() > 0) {
                    seekBar.setProgress(wlTimeInfoBean.getCurrentTime() * 100 / wlTimeInfoBean.getTotalTime());
                }
            }
        }
    };

    public void stop(View view) {
        wlPlayer.stop();
    }


    public void next(View view) {
        wlPlayer.playNext("/sdcard/qme-test/test.mp4");
    }

    //audio
    public void onAudioLeft(View view){
        wlPlayer.setMute(MuteEnum.MUTE_LEFT);
    }

    public void onAudioRight(View view){
        wlPlayer.setMute(MuteEnum.MUTE_RIGHT);
    }

    public void onAudioCenter(View view){
        wlPlayer.setMute(MuteEnum.MUTE_CENTER);
    }

    public void speed(View view) {
        wlPlayer.setSpeed(1.5f);
        wlPlayer.setPitch(1.0f);
    }

    public void pitch(View view) {
        wlPlayer.setPitch(1.5f);
        wlPlayer.setSpeed(1.0f);
    }

    public void speedpitch(View view) {
        wlPlayer.setSpeed(1.5f);
        wlPlayer.setPitch(1.7f);
    }

    public void normalspeedpitch(View view) {
        wlPlayer.setSpeed(1.0f);
        wlPlayer.setPitch(1.0f);
    }
}

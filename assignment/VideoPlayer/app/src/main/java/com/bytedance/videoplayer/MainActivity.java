package com.bytedance.videoplayer;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //VideoView videoView;
    SeekBar seekBar;
    TextView textViewTime;
    TextView textViewCurrentTime;
    public static final String path = Environment.getExternalStorageDirectory().getPath() + File.separator;
    private SurfaceView surfaceView;
    private MediaPlayer player;
    private SurfaceHolder holder;

    Uri mSelectedVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        setContentView(R.layout.activity_main);

        textViewTime=findViewById(R.id.textViewTime);
        textViewCurrentTime=findViewById(R.id.textViewCurrentTime);
        seekBar = findViewById(R.id.seekBar);
        surfaceView = findViewById(R.id.surfaceView);
        player = new MediaPlayer();
        try {
            player.setDataSource(getResources().openRawResourceFd(R.raw.yuminhong));
            holder = surfaceView.getHolder();
            holder.addCallback(new PlayerCallBack());
            player.prepare();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // 自动播放
                    player.start();
                    player.setLooping(true);
                    textViewTime.setText(time(player.getDuration()));

                    handler.postDelayed(runnable,0);
                }
            });
            player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                    System.out.println(percent);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        findViewById(R.id.buttonPlay).setOnClickListener(this);
        findViewById(R.id.buttonPause).setOnClickListener(this);
        findViewById(R.id.buttonChange).setOnClickListener(this);
        //findViewById(R.id.buttonOpen).setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress()*player.getDuration()/100;
                if (player.isPlaying()) {
                    // 设置当前播放的位置
                    //System.out.println();
                    player.seekTo(progress);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonPlay:
                player.start();
                break;
            case R.id.buttonPause:
                player.pause();
                break;
            case R.id.buttonChange:
               // System.out.println("111");
                //System.out.println(MainActivity.this.getRequestedOrientation());
                if(MainActivity.this.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                else{
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }

                break;
            /*case R.id.buttonOpen:
                //OpenFileUtil.getInstance().openFile(this,new File("videos/"));
                //chooseVideo();

                break;*/
            default:
                break;
        }
    }

    public void chooseVideo() {
        // TODO-C2 (5) Start Activity to select a video
        System.out.println("caonima");
        //Intent intent = new Intent();

        //intent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(Intent.createChooser(intent, "Select Video"),PICK_VIDEO);
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        //intent.addCategory(Intent.CATEGORY_DEFAULT);
        //intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.setDataAndType(mSelectedVideo, getMIMEType(file));
        startActivityForResult(Intent.createChooser(intent,"caonima"),2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("WTF! ", "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        if (resultCode == RESULT_OK && null != data) {
            mSelectedVideo = data.getData();
            Log.d("WTF2222! ", "mSelectedVideo = " + mSelectedVideo);
            //OpenFileUtil.getInstance().openFile(this,new File(String.valueOf(mSelectedVideo)));

            try {
                mSelectedVideo=FileProvider.getUriForFile(this,"com.bytedance.videoplayer.fileProvider",new File(new URI(String.valueOf(data.getData()))));
                player.setDataSource(this,mSelectedVideo);
                System.out.println("NMSL");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            if(resultCode == RESULT_OK)
                System.out.println("WTF3333");
            if(data == null)
                System.out.println("WTF4444");
        }
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            if (player.isPlaying()) {
                int current = player.getCurrentPosition()*100/player.getDuration();
                System.out.println(player.getCurrentPosition()+"+"+player.getDuration()+"current "+current);
                seekBar.setProgress(current);
                textViewCurrentTime.setText(time(player.getCurrentPosition()));
            }
            handler.postDelayed(runnable, 500);
        }
    };
    @Override
    protected void onStop() {
        super.onStop();
        if (player.isPlaying()) {
            player.stop();
        }
    }

    protected String time(long millionSeconds) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millionSeconds);
        return simpleDateFormat.format(c.getTime());
    }

    private class PlayerCallBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            player.setDisplay(holder);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Toast.makeText(this, "+++++++++++++++", Toast.LENGTH_LONG).show();
    }
}

package com.bytedance.videoplayer;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bytedance.videoplayer.Player.VideoPlayerIJK;
import com.bytedance.videoplayer.Player.VideoPlayerListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    VideoPlayerIJK videoPlayerIJK;
    MediaPlayer mediaPlayer;
    SurfaceHolder holder;
    SeekBar seekBar;
    TextView textViewTime;
    TextView textViewCurrentPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*ImageView imageView = findViewById(R.id.imageView);
        String url = "https://s3.pstatp.com/toutiao/static/img/logo.271e845.png";
        Glide.with(this).load(url).into(imageView);*/
        videoPlayerIJK = findViewById(R.id.ijkPlayer);
        seekBar = findViewById(R.id.seekBar);
        textViewCurrentPosition=findViewById(R.id.textViewCurrentPosition);
        textViewTime=findViewById(R.id.textViewTime);


        seekBar.setOnSeekBarChangeListener(onSeekBarChangeListener);

        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }
        videoPlayerIJK.setListener(new VideoPlayerListener());
        videoPlayerIJK.setVideoResource(R.raw.yuminhong);
        videoPlayerIJK.start();

        handler.postDelayed(runnable, 0);
        System.out.println("????????"+videoPlayerIJK.getDuration());
        if(videoPlayerIJK.)
        textViewTime.setText(time(videoPlayerIJK.getDuration()));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
                break;
        }
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        // 当进度条停止修改的时候触发
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 取得当前进度条的刻度
            int progress = seekBar.getProgress();
            if (videoPlayerIJK.isPlaying()) {
                // 设置当前播放的位置
                videoPlayerIJK.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        }
    };


    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        public void run() {
            if (videoPlayerIJK.isPlaying()) {
                int current = (int)(((float)videoPlayerIJK.getCurrentPosition())/videoPlayerIJK.getDuration());
              //  System.out.println(videoPlayerIJK.getCurrentPosition()+"+"+videoPlayerIJK.getDuration()+"current "+current);
                seekBar.setProgress(current);
                textViewCurrentPosition.setText(time(videoPlayerIJK.getCurrentPosition()));
            }
            handler.postDelayed(runnable, 1000);
        }
    };
    @Override
    protected void onStop() {
        super.onStop();
        if (videoPlayerIJK.isPlaying()) {
            videoPlayerIJK.stop();
        }

        IjkMediaPlayer.native_profileEnd();
    }

    protected String time(long millionSeconds) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millionSeconds);
        return simpleDateFormat.format(c.getTime());
    }
}

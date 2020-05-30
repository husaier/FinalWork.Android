package com.example.finalwork;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String NICKNAME = "nickName";
    private static final String VIDEOURL = "videoUrl";
    private static final String AVATARS = "avatars";
    private static final String DESCRIPTION = "description";
    private static final String LIKECOUNT = "likecount";

    private static final String TAG = "VideoFragment";
    private static final int UPDATE_VIDEO_PROGRESS = 0;

    private String nickName;//用户名
    private String videoUrl;//视频地址
    private String avatars;//视频封面
    private String description;//视频描述
    private int likecount;//视频描述
    private boolean flagLike;//是否喜欢此视频

    private TextView tv_likecount;
    private LottieAnimationView lav_like;
    private VideoView mVideoView;
    private LottieAnimationView lav_loading;
    private SeekBar sk_video;
    private TextView tv_curTime;
    private TextView tv_totalTime;
    private static Handler mHandler;
    private updateThread videoUpdater;

    {
        flagLike = false;
    }

    public VideoFragment() {
        // Required empty public constructor
    }

    public static VideoFragment newInstance(String nickName, String videoUrl, String avatars,
                                            String description, Integer likecount) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putString(NICKNAME, nickName);
        args.putString(VIDEOURL, videoUrl);
        args.putString(AVATARS, avatars);
        args.putString(DESCRIPTION, description);
        args.putInt(LIKECOUNT, likecount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nickName = "@" + getArguments().getString(NICKNAME);
            videoUrl = getArguments().getString(VIDEOURL);
            avatars = getArguments().getString(AVATARS);
            description = getArguments().getString(DESCRIPTION);
            likecount = getArguments().getInt(LIKECOUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //设置loading动画
        lav_loading = view.findViewById(R.id.lav_loading);
        //设置用户名
        TextView tv_nickName = view.findViewById(R.id.tv_nickname);
        tv_nickName.setText(nickName);
        //设置like动画
        lav_like = view.findViewById(R.id.lav_like);
        lav_like.setAlpha(0f);
        lav_like.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                lav_like.setProgress(0f);
                lav_like.setAlpha(1f);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                lav_like.setAlpha(0f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                lav_like.pauseAnimation();
                lav_like.setAlpha(0f);
            }

            @Override
            public void onAnimationRepeat(Animator animation) { }
        });

        //设置封面图
        CircleImageView circle_iv = (CircleImageView) view.findViewById(R.id.cover);
        Uri imageUri = Uri.parse(avatars);
        Glide.with(this)
                .load(imageUri)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(new ViewTarget(circle_iv) {
            @Override
            public void onResourceReady(@NonNull Object resource, @Nullable Transition transition) {
                this.view.setBackground((Drawable) resource);
            }
        });
        //设置头像旋转
        ObjectAnimator animator = ObjectAnimator.ofFloat(circle_iv , "rotation" , 0 , 360);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.RESTART);
        animator.setDuration(18000);
        animator.start();
        //设置灰心图片
        ImageView iv_heart = view.findViewById(R.id.im_heart);
        if (flagLike)
            iv_heart.setImageResource(R.drawable.hear_red);
        else
            iv_heart.setImageResource(R.drawable.heart_gray);
        iv_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flagLike) {
                    iv_heart.setImageResource(R.drawable.heart_gray);
                    likecount--;
                    tv_likecount.setText(String.valueOf(likecount));
                    lav_like.cancelAnimation();
                    flagLike = false;
                }
                else {
                    iv_heart.setImageResource(R.drawable.hear_red);
                    likecount++;
                    tv_likecount.setText(String.valueOf(likecount));
                    lav_like.playAnimation();
                    //Log.d(TAG, "播放动画");
                    flagLike = true;
                }
            }
        });
        //设置likecount
        tv_likecount = view.findViewById(R.id.tv_likecount);
        tv_likecount.setText(String.valueOf(likecount));
        //设置分享图片
        ImageView iv_share = view.findViewById(R.id.im_share);
        iv_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
        //设置description
        TextView tv_description = view.findViewById(R.id.tv_description);
        tv_description.setText(description);
        //设置视频播放控制
        mVideoView = view.findViewById(R.id.vv_video);
        sk_video = view.findViewById(R.id.sk_video);
        tv_curTime = view.findViewById((R.id.tv_cur_time));
        tv_totalTime = view.findViewById(R.id.tv_total_time);
        setVideo();
        //设置单机双击
        view.setOnTouchListener(new MyTouchListener(getContext()) {
            @Override
            void multiClick(View v, int count) {
                //连续点击超过2次，被视作一次双击事件
                if (count == 1) {
                    if (mVideoView.isPlaying())
                        mVideoView.pause();
                    else
                        mVideoView.start();
                }
                else {
                    if (flagLike) {
                        iv_heart.setImageResource(R.drawable.heart_gray);
                        likecount--;
                        tv_likecount.setText(String.valueOf(likecount));
                        lav_like.cancelAnimation();
                        flagLike = false;
                    }
                    else {
                        iv_heart.setImageResource(R.drawable.hear_red);
                        likecount++;
                        tv_likecount.setText(String.valueOf(likecount));
                        lav_like.playAnimation();
                        //Log.d(TAG, "播放动画");
                        flagLike = true;
                    }
                }
            }

            @Override
            void leftSlide() { }

            @Override
            void rightSlide() {

            }
        });
    }

    private void share() {
        Intent share_intent = new Intent();
        share_intent.setAction(Intent.ACTION_SEND);//设置分享行为
        share_intent.setType("text/plain");//设置分享内容的类型
        String title = nickName + "\n" + description + "\n";
        share_intent.putExtra(Intent.EXTRA_SUBJECT, title);//添加分享内容标题
        share_intent.putExtra(Intent.EXTRA_TEXT, title + videoUrl);//添加分享内容
        // 创建分享的Dialog
        share_intent = Intent.createChooser(share_intent, "分享到");
        startActivity(share_intent);
    }

    private void setVideo() {

        Uri uri = Uri.parse(videoUrl);
        mVideoView.setVideoURI(uri);
        Log.d(TAG, "uri已设置");
        //播放控制
        sk_video.setProgress(0);
        sk_video.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private boolean shouldPlaying;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser) {
                    float proportion = (float)progress / 100;
                    float length = (float)mVideoView.getDuration();
                    int cur = (int)(length * proportion);
                    setTime(cur);
                    mVideoView.seekTo(cur);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(mVideoView.isPlaying()) {
                    shouldPlaying = true;
                    mVideoView.pause();
                }
                else
                    shouldPlaying = false;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(shouldPlaying)
                    mVideoView.start();
            }
        });
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case UPDATE_VIDEO_PROGRESS:
                        int mCur = mVideoView.getCurrentPosition();
                        setTime(mCur);
                }
            }
        };
        videoUpdater = new updateThread();
        videoUpdater.start();
//        MediaController mediaController = new MediaController(getContext());
//        mVideoView.setMediaController(mediaController);
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "VideoView onCompletion");
                mp.start();
                mp.setLooping(true);
            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if(what==MediaPlayer.MEDIA_ERROR_UNKNOWN){
                    if(extra==MediaPlayer.MEDIA_ERROR_IO){
                        //文件不存在或错误，或网络不可访问错误
                        Toast.makeText(getContext(), "网络文件错误", Toast.LENGTH_LONG).show();
                    } else if(extra==MediaPlayer.MEDIA_ERROR_TIMED_OUT){
                        //超时
                        Toast.makeText(getContext(), "网络超时", Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideoView.setBackground(null);
                if (!mp.isPlaying())
                    mp.start();
                else
                    setTime(0);
                if (lav_loading.isAnimating()) {
                    lav_loading.setAlpha(0f);
                    lav_loading.pauseAnimation();
                }
            }
        });
    }

    private void setTime(int cur) {
        int length = mVideoView.getDuration();
        if(length < 0)
            return;
        float tmp = (float)cur / (float)length * (float) 100;
        sk_video.setProgress((int)tmp);
        Log.d(TAG, "video duration:" + length);
        Log.d(TAG, "current position:" + cur + " seek bar progress:" +  tmp);

        if(cur % 1000 >= 500)
            cur = cur / 1000 + 1;
        else
            cur = cur / 1000;

        if(length % 1000 >= 500)
            length = length / 1000 + 1;
        else
            length = length / 1000;
        int length_min = length / 60;
        int length_sec = length % 60;
        int cur_min = cur / 60;
        int cur_sec = cur % 60;
        String time = String.valueOf(cur_min) + ':' + cur_sec;
        tv_curTime.setText(time);
        time = String.valueOf(length_min) + ':' + length_sec;
        tv_totalTime.setText(time);
    }

    private class updateThread extends Thread {
        @Override
        public void run() {
            super.run();
            while(!isInterrupted()) {
                try{
                    Thread.sleep(500);
                    if(mVideoView.isPlaying()) {
                        mHandler.sendEmptyMessage(UPDATE_VIDEO_PROGRESS);
                    }
                }
                catch (InterruptedException e) {
                    Log.d(TAG, "updateThread is destroyed!");
                }
            }
        }
    }
}

abstract class MyTouchListener implements View.OnTouchListener {
    //private static String TAG = "MyTouchListener";
    private static String TAG = "VideoFragment";
    private static final float FLIP_DISTANCE = 300; //每秒钟滑动多少个像素

    private View view;
    private long THRESHOLD; //阈值，单位：毫秒
    private int count; // 记录连续点击的次数
    private Runnable mRunnable;
    private Handler mHandler;
    private GestureDetector mDetector;

    public MyTouchListener(Context context) {
        THRESHOLD = 300;
        count = 0;
        mRunnable = () -> {
            //如果Runnable执行，表示在UP事件发生后TRASHOLD ms过去，没有再产生UP事件
            Log.d(TAG, "Delay Runnable触发");
            Log.d(TAG, "count = " + String.valueOf(count));
            //UP事件后，THRASHOLD ms内又发生UP事件，被视为连续点击
            multiClick(view, count);
            count = 0;
        };
        mHandler = new Handler();
        mDetector = new GestureDetector(context , new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                Log.d(TAG, "OnTouch ACTION_DOWN触发");
                //如果有的话，取消上一个Runnable任务
                if (mRunnable != null)
                    mHandler.removeCallbacks(mRunnable);
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d(TAG, "OnTouch ACTION_UP触发");
                //每次抬起都部署一个runnable
                count++;
                mHandler.postDelayed(mRunnable, THRESHOLD);
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1.getX() - e2.getX() > FLIP_DISTANCE) {
                    leftSlide();
                    Log.d(TAG, "左滑");
                    return true;
                }
                if (e2.getX() - e1.getX() > FLIP_DISTANCE) {
                    rightSlide();
                    Log.d(TAG, "右滑");
                    return true;
                }
                return false;
            }
        });
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int eventName = event.getAction();
        view = v;
        if (eventName != MotionEvent.ACTION_DOWN && eventName != MotionEvent.ACTION_UP) {
            count = 0;
            //Log.d(TAG, "其它事件" + String.valueOf(eventName));
        }
        mDetector.onTouchEvent(event);
        return true;
    }

    abstract void multiClick(View v, int count);

    abstract void leftSlide();

    abstract void rightSlide();
};

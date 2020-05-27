package com.example.finalwork;

import android.animation.Animator;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;


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

    private String nickName;//用户名
    private String videoUrl;//视频地址
    private String avatars;//视频封面
    private String description;//视频描述
    private int likecount;//视频描述
    private boolean flagLike;//是否喜欢此视频

    private TextView tv_likecount;
    private LottieAnimationView lav_like;

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
        //设置用户名
        TextView tv_nickName = view.findViewById(R.id.tv_nickname);
        tv_nickName.setText(nickName);
        //设置like动画
        lav_like = view.findViewById(R.id.lav_like);
        lav_like.useHardwareAcceleration();
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
        //设置灰心图片
        ImageView iv_heart = view.findViewById(R.id.im_heart);
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
        //设置description
        TextView tv_description = view.findViewById(R.id.tv_description);
        tv_description.setText(description);
        //设置视频播放控制
        VideoView mVideoView = view.findViewById(R.id.vv_video);
        Uri uri = Uri.parse(videoUrl);
        mVideoView.setVideoURI(uri);
        Log.d(TAG, "uri已设置");
        //播放控制器
        //mVideoView.setMediaController(new MediaController(getContext()));
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
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
                mp.start();
            }
        });
        //设置单机双击
        view.setOnTouchListener(new MyMultiClickListener() {
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
        });
        Uri imageUri = Uri.parse(avatars);
        //Glide.with(this).load(imageUri).into(mVideoView.back);
        //显示图片
        Glide.with(this).load(imageUri).into(new ViewTarget(mVideoView) {
            @Override
            public void onResourceReady(@NonNull Object resource, @Nullable Transition transition) {
                this.view.setBackground((Drawable) resource);
            }
        });
    }

    private class MyClickListener implements View.OnTouchListener {
        private long THRESHOLD; //阈值，单位：毫秒
        private boolean flag; //是否喜欢该视频
        private long lastTime; //上次抬起的时刻
        private long now;
        private int count; // 记录连续点击的次数
        private Runnable mRunnable;
        private Handler mHandler;
        {
            THRESHOLD = 2000;
            lastTime = 0;
            count = 0;
            mRunnable = () -> {
                //如果Runnable执行，表示在UP事件发生后TRASHOLD ms过去，没有再产生DOWM事件
                Log.d(TAG, "Delay Runnable触发");
                Log.d(TAG, "count = " + String.valueOf(count));
                //UP事件后，THRASHOLD ms内又发生DOWN事件，被视为连续点击
                //连续点击超过2次，都会被视作一次双击事件
                if (count == 1) {
                    //单击
                    Log.d(TAG, "单击事件");
                }
                else {
                    //双击
                    Log.d(TAG, "双击事件");
                }
                count = 0;
            };
            mHandler = new Handler();
        }
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //Log.d(TAG, "OnTouch触发");
            now = System.currentTimeMillis();
            int eventName = event.getAction();
            if (eventName == MotionEvent.ACTION_DOWN) {
                Log.d(TAG, "OnTouch ACTION_DOWN触发");
                //如果有的话，取消上一个Runnable任务
                if (mRunnable != null)
                    mHandler.removeCallbacks(mRunnable);
                count++;
                return true;
            }
            else if (eventName == MotionEvent.ACTION_UP) {
                Log.d(TAG, "OnTouch ACTION_UP触发");
                lastTime = now;
                //每次抬起都部署一个runnable
                mHandler.postDelayed(mRunnable, THRESHOLD);
            }
            else { //其它事件
                Log.d(TAG, "事件" + String.valueOf(eventName));
            }
            //这是另一种处理，它有弊端
            //它会把双击事件的前一次点击，也视为一次单击事件

//                if (eventName == MotionEvent.ACTION_DOWN) {
//                    Log.d(TAG, "OnTouch DOWN触发" + String.valueOf(now - lastTime));
//                    if (now - lastTime < THRESHOLD) {
//                        //双击
//                        Log.d(TAG, "双击触发");
//                        if (flag) {
//                            iv_heart.setImageResource(R.drawable.heart_gray);
//                            likecount--;
//                            tv_likecount.setText(String.valueOf(likecount));
//                            lav_like.cancelAnimation();
//                            flag = false;
//                        }
//                        else {
//                            iv_heart.setImageResource(R.drawable.hear_red);
//                            likecount++;
//                            tv_likecount.setText(String.valueOf(likecount));
//                            lav_like.playAnimation();
//                            //Log.d(TAG, "播放动画");
//                            flag = true;
//                        }
//                    }
//                    else {
//                        //单击
//                        Log.d(TAG, "单击触发" + String.valueOf(now - lastTime));
//                        if (mVideoView.isPlaying())
//                            mVideoView.pause();
//                        else
//                            mVideoView.start();
//                    }
//                    return true;
//                }
//                else if(eventName == MotionEvent.ACTION_UP){
//                    Log.d(TAG, "OnTouch UP触发");
//                    lastTime = now;
//                }
//                else {
//                    Log.d(TAG, "事件" + String.valueOf(eventName));
//                }
            return false;
        }
    }
}

abstract class MyMultiClickListener implements View.OnTouchListener {
    private static String TAG = "MyMultiClickListener";

    private View view;
    private long THRESHOLD; //阈值，单位：毫秒
    private int count; // 记录连续点击的次数
    private Runnable mRunnable;
    private Handler mHandler;
    {
        THRESHOLD = 300;
        count = 0;
        mRunnable = () -> {
            //如果Runnable执行，表示在UP事件发生后TRASHOLD ms过去，没有再产生DOWM事件
            Log.d(TAG, "Delay Runnable触发");
            Log.d(TAG, "count = " + String.valueOf(count));
            //UP事件后，THRASHOLD ms内又发生DOWN事件，被视为连续点击
            multiClick(view, count);
            count = 0;
        };
        mHandler = new Handler();
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int eventName = event.getAction();
        if (eventName == MotionEvent.ACTION_DOWN) {
            Log.d(TAG, "OnTouch ACTION_DOWN触发");
            //如果有的话，取消上一个Runnable任务
            if (mRunnable != null)
                mHandler.removeCallbacks(mRunnable);
            count++;
            return true;
        }
        else if (eventName == MotionEvent.ACTION_UP) {
            Log.d(TAG, "OnTouch ACTION_UP触发");
            //每次抬起都部署一个runnable
            view = v;
            mHandler.postDelayed(mRunnable, THRESHOLD);
        }
        else { //其它事件
            count = 0;
            Log.d(TAG, "其它事件" + String.valueOf(eventName));
        }
        return false;
    }

    abstract void multiClick(View v, int count);
};

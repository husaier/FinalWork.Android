package com.example.finalwork;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
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
    private GestureDetector mDetector;

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
        LottieAnimationView lav_loading = view.findViewById(R.id.lav_loading);
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
        VideoView mVideoView = view.findViewById(R.id.vv_video);
        Uri uri = Uri.parse(videoUrl);
        mVideoView.setVideoURI(uri);
        Log.d(TAG, "uri已设置");
        //播放控制器
        MediaController mediaController = new MediaController(getContext());
        mVideoView.setMediaController(mediaController);
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
                if (lav_loading.isAnimating()) {
                    lav_loading.setAlpha(0f);
                    lav_loading.pauseAnimation();
                }
            }
        });
        //设置单机双击、长按3秒重头播放
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
        Uri imageUri = Uri.parse(avatars);
        //Glide.with(this).load(imageUri).into(mVideoView.back);
        //显示图片
//        Glide.with(this).load(imageUri).into(new ViewTarget(mVideoView) {
//            @Override
//            public void onResourceReady(@NonNull Object resource, @Nullable Transition transition) {
//                this.view.setBackground((Drawable) resource);
//            }
//        });
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

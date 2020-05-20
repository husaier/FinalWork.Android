package com.example.finalwork;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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

    private static final String TAG = "VideoFragment";

    private String nickName;
    private String videoUrl;
    private String avatars;

    public VideoFragment() {
        // Required empty public constructor
    }

    public static VideoFragment newInstance(String nickName, String videoUrl, String avatars) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putString(NICKNAME, nickName);
        args.putString(VIDEOURL, videoUrl);
        args.putString(AVATARS, avatars);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nickName = getArguments().getString(NICKNAME);
            videoUrl = getArguments().getString(VIDEOURL);
            avatars = getArguments().getString(AVATARS);
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
        TextView tv_nickName = view.findViewById(R.id.tv_nickname);
        tv_nickName.setText(nickName);
        VideoView mVideoView = view.findViewById(R.id.vv_video);
        Uri uri = Uri.parse(videoUrl);
        mVideoView.setVideoURI(uri);

        // 加载图片Uri对象
        Uri imageUri = Uri.parse(avatars);
        //Glide.with(this).load(imageUri).into(mVideoView.back);
        //显示图片
        Glide.with(this).load(imageUri).into(new ViewTarget(mVideoView) {
            @Override
            public void onResourceReady(@NonNull Object resource, @Nullable Transition transition) {
                this.view.setBackground((Drawable) resource);
            }
        });


        Log.d(TAG, "uri已设置");
        //播放控制器
        mVideoView.setMediaController(new MediaController(getContext()));
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
    }
}

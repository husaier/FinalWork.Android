package com.example.finalwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// API共有10条视频
// android官方文档：https://developer.android.google.cn/docs
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private List<String> ids = new ArrayList<>();//视频ID
    private List<String> feedurls = new ArrayList<>();//视频链接
    private List<String> nickNames = new ArrayList<>();//视频名称
    private List<String> descriptions = new ArrayList<>();//视频描述
    private List<Integer> likecounts = new ArrayList<>();//视频点赞数
    private List<String> avatars = new ArrayList<>();//视频封面链接

    private ViewPagerFragmentStateAdapter mAdapter;
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager2 = findViewById(R.id.view_pager2);

        //从API获取所有视频的信息
        getData();
    }

    private void getData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://beiyou.bytedance.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.d("mainactivity", "2");
        ApiService apiService = retrofit.create(ApiService.class);
        // 做网络请求
        apiService.getVideoMessages().enqueue(new Callback<VideoMessage[]>() {
            // 获取视频信息成功
            @Override
            public void onResponse(Call<VideoMessage[]> call, Response<VideoMessage[]> response) {
                if (response.body() != null) {
                    VideoMessage[] videos = response.body();// 存入数组
                    if (videos.length != 0) {                // 判断数组是否为空
                        for (VideoMessage v : videos) {        //遍历数组，将信息存入各个list中
                            ids.add(v._id);
                            feedurls.add(v.feedurl);
                            nickNames.add(v.nickname);
                            descriptions.add(v.description);
                            likecounts.add(v.likecount);
                            avatars.add(v.avatar);
                        }
                    }
                    // 获取信息完成
                    mAdapter = new ViewPagerFragmentStateAdapter(getSupportFragmentManager(), getLifecycle());
                    viewPager2.setAdapter(mAdapter);
                }
            }

            // 获取视频信息失败，打印错误信息
            @Override
            public void onFailure(Call<VideoMessage[]> call, Throwable t) {
                Log.d(TAG, Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    class ViewPagerFragmentStateAdapter extends FragmentStateAdapter {

        ViewPagerFragmentStateAdapter(@NonNull FragmentManager fragmentManager, Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @Override
        public int getItemCount() {
            return nickNames.size();
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return VideoFragment.newInstance(nickNames.get(position), feedurls.get(position), avatars.get(position),
                    descriptions.get(position), likecounts.get(position));
        }
    }
}

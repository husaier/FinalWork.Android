package com.example.finalwork;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
                    //设置转换动画
                    viewPager2.setPageTransformer(new MyTransformer());
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

    public static class MyTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        @Override
        public void transformPage(@NonNull View page, float position) {
            int pageWidth = page.getWidth();
            int pageHeight = page.getHeight();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                page.setAlpha(0);

            } else if (position <= 1) { // [-1,1]
                // Modify the default slide transition to shrink the page as well
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    page.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    page.setTranslationX(-horzMargin + vertMargin / 2);
                }

                // Scale the page down (between MIN_SCALE and 1)
                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);

                // Fade the page relative to its size.
                page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                page.setAlpha(0);
            }
        }
    }
}

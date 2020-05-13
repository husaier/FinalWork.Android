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

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// API共有10条视频
// android官方文档：https://developer.android.google.cn/docs
public class MainActivity extends AppCompatActivity {
    private List<String> ids = new ArrayList<>();
    private List<String> feedurls = new ArrayList<>();
    private List<String> nickNames = new ArrayList<>();
    private List<String> descriptions = new ArrayList<>();
    private List<Integer> likecounts = new ArrayList<>();
    private List<String> avatars = new ArrayList<>();


    private ViewPagerFragmentStateAdapter mAdapter;
    private ViewPager2 viewPager2;

//    {
//        nickNames.add("王火火");
//        nickNames.add("LILILI");
//        nickNames.add("新闻启示录");
//        nickNames.add("综艺大咖秀");
//        nickNames.add("南翔不爱吃饭");
//        nickNames.add("王者主播那些事儿");
//        nickNames.add("十秒学做菜");
//        nickNames.add("九零后老母亲");
//        nickNames.add("FPX电子竞技俱乐部");
//        nickNames.add("抖音官方广告报名！");
//    }
//
//    {
//        feedurls.add("http://jzvd.nathen.cn/video/1137e480-170bac9c523-0007-1823-c86-de200.mp4");
//        feedurls.add("http://jzvd.nathen.cn/video/e0bd348-170bac9c3b8-0007-1823-c86-de200.mp4");
//        feedurls.add("http://jzvd.nathen.cn/video/2f03c005-170bac9abac-0007-1823-c86-de200.mp4");
//        feedurls.add("http://jzvd.nathen.cn/video/7bf938c-170bac9c18a-0007-1823-c86-de200.mp4");
//        feedurls.add("http://jzvd.nathen.cn/video/47788f38-170bac9ab8a-0007-1823-c86-de200.mp4");
//        feedurls.add("http://jzvd.nathen.cn/video/2d6ffe8f-170bac9ab87-0007-1823-c86-de200.mp4");
//        feedurls.add("http://jzvd.nathen.cn/video/633e0ce-170bac9ab65-0007-1823-c86-de200.mp4");
//        feedurls.add("http://jzvd.nathen.cn/video/2d6ffe8f-170bac9ab87-0007-1823-c86-de200.mp4");
//        feedurls.add("http://jzvd.nathen.cn/video/51f7552c-170bac98718-0007-1823-c86-de200.mp4");
//        feedurls.add("http://jzvd.nathen.cn/video/2a101070-170bad88892-0007-1823-c86-de200.mp4");
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager2 = findViewById(R.id.view_pager2);
        Log.d("mainactivity", "1");
        getData();

        Log.d("mainactivity", "4");
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
            return VideoFragment.newInstance(nickNames.get(position), feedurls.get(position));
        }
    }

    private void getData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://beiyou.bytedance.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.d("mainactivity", "2");
        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getVideoMessages().enqueue(new Callback<VideoMessage[]>() {
            @Override
            public void onResponse(Call<VideoMessage[]> call, Response<VideoMessage[]> response) {
                Log.d("mainactivity", "3");
                if (response.body() != null) {
                    VideoMessage[] videos = response.body();
                    if(videos.length != 0) {
                        for(VideoMessage v:videos) {
                            ids.add(v._id);
                            feedurls.add(v.feedurl);
                            nickNames.add(v.nickname);
                            descriptions.add(v.description);
                            likecounts.add(v.likecount);
                            avatars.add(v.avatar);
//                        Log.d("mainactivity", v.toString());
                        }
                    }
                    mAdapter = new ViewPagerFragmentStateAdapter(getSupportFragmentManager(), getLifecycle());
                    viewPager2.setAdapter(mAdapter);
                }
            }
            @Override
            public void onFailure(Call<VideoMessage[]> call, Throwable t) {
                Log.d("mainactivity", t.getMessage());
            }
        });
    }
}

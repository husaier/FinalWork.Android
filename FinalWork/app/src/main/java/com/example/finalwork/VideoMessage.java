package com.example.finalwork;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoMessage {
    //      "_id":"5e9830b0ce330a0248e89d86",
    //      "feedurl":"http://jzvd.nathen.cn/video/1137e480-170bac9c523-0007-1823-c86-de200.mp4",
    //      "nickname":"王火火",
    //      "description":"这是第一条Feed数据",
    //      "likecount":10000,
    //      "avatar":"http://jzvd.nathen.cn/snapshot/f402a0e012b14d41ad07939746844c5e00005.jpg"
    @SerializedName("_id")
    public String _id;//ID
    @SerializedName("feedurl")
    public String feedurl;//视频链接
    @SerializedName("nickname")
    public String nickname;//视频名
    @SerializedName("description")
    public String description;//视频描述
    @SerializedName("likecount")
    public int likecount;//喜欢数
    @SerializedName("avatar")
    public String avatar;//封面图片链接

    @Override
    public String toString() {
        return "videomessages{" +
                "_id=" + _id +
                ", feedurl='" + feedurl + '\'' +
                ", nickname='" + nickname + '\'' +
                ", description='" + description + '\'' +
                ", likecount='" + likecount + '\'' +
                ", avatar=" + avatar +
                '}';
    }
}

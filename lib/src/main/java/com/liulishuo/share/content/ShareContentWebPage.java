package com.liulishuo.share.content;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.liulishuo.share.type.ShareContentType;

/**
 * Created by echo on 5/18/15.
 * 分享网页模式
 */
public class ShareContentWebPage extends ShareContentPic {

    private String title, summary, url; 

    /**
     * @param title   标题
     * @param summary 描述
     * @param url     点击分享的内容后跳转的链接
     * @param thumb   图片的bitmap。保证在32kb以内,如果要分享图片，那么必传
     * @param large   大图的bitmap。10m以内，如果要分享图片，那么必传
     */
    public ShareContentWebPage(@NonNull String title, @NonNull String summary, String url,
            @Nullable Bitmap thumb, @Nullable Bitmap large) {
        super(thumb, large);
        this.title = title;
        this.summary = summary;
        this.url = url;
    }

    public ShareContentWebPage(@NonNull String title, @NonNull String summary, String actionUrl, @Nullable String thumbUrl) {
		super(thumbUrl);
		this.title = title;
		this.summary = summary;
		this.url = actionUrl;
	}

    @Override
    public String getSummary() {
        return summary;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getURL() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int getType() {
        return ShareContentType.WEBPAGE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.title);
        dest.writeString(this.summary);
        dest.writeString(this.url);
    }

    private ShareContentWebPage(Parcel in) {
        super(in);
        this.title = in.readString();
        this.summary = in.readString();
        this.url = in.readString();
    }

    public static final Creator<ShareContentWebPage> CREATOR = new Creator<ShareContentWebPage>() {
        @Override
        public ShareContentWebPage createFromParcel(Parcel source) {
            return new ShareContentWebPage(source);
        }

        @Override
        public ShareContentWebPage[] newArray(int size) {
            return new ShareContentWebPage[size];
        }
    };
}

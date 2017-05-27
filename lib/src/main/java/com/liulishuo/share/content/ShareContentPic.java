package com.liulishuo.share.content;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.support.annotation.Nullable;

import com.liulishuo.share.type.ShareContentType;

/**
 * Created by echo on 5/18/15.
 * 分享图片模式
 */
public class ShareContentPic implements ShareContent {

    private Bitmap thumbBmp, largeBmp;

    private byte[] thumbBmpBytes;

    private String largeBmpPath;

    private String thumbUrl;

    /**
     * @param thumbBmp 如果需要分享图片，则必传
     */
    public ShareContentPic(@Nullable Bitmap thumbBmp, @Nullable Bitmap largeBmp) {
        this.thumbBmp = thumbBmp;
        this.largeBmp = largeBmp;
    }

    public ShareContentPic(@Nullable String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    @Override
    public String getSummary() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public String getURL() {
        return null;
    }

    @Override
    public byte[] getThumbBmpBytes() {
        return thumbBmpBytes;
    }

    @Override
    public String getLargeBmpPath() {
        return largeBmpPath;
    }

    @Override
    public String getMusicUrl() {
        return null;
    }

    @ShareContentType
    @Override
    public int getType() {
        return ShareContentType.PIC;
    }

    public Bitmap getThumbBmp() {
        return thumbBmp;
    }

    public Bitmap getLargeBmp() {
        return largeBmp;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbBmpBytes(byte[] thumbBmpBytes) {
        this.thumbBmpBytes = thumbBmpBytes;
    }

    public void setLargeBmpPath(String largeBmpPath) {
        this.largeBmpPath = largeBmpPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByteArray(this.thumbBmpBytes);
        dest.writeString(this.largeBmpPath);
        dest.writeString(this.thumbUrl);
    }

    ShareContentPic(Parcel in) {
        this.thumbBmpBytes = in.createByteArray();
        this.largeBmpPath = in.readString();
        this.thumbUrl = in.readString();
    }

    public static final Creator<ShareContentPic> CREATOR = new Creator<ShareContentPic>() {
        @Override
        public ShareContentPic createFromParcel(Parcel source) {
            return new ShareContentPic(source);
        }

        @Override
        public ShareContentPic[] newArray(int size) {
            return new ShareContentPic[size];
        }
    };
}

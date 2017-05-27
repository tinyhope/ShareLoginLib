package com.liulishuo.share;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Kale
 * @date 2017/3/21
 */
class SlUtils {

    @Nullable
    static byte[] getImageThumbByteArr(@Nullable String src) {
        return getImageThumbByteArr(getBitmapFromUrl(src));
    }

    @Nullable
    private static Bitmap getBitmapFromUrl(@Nullable String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            return BitmapFactory.decodeStream(conn.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Note:外部传入的bitmap可能会被用于其他的地方，所以这里不能做recycle()
     */
    @Nullable
    static byte[] getImageThumbByteArr(@Nullable Bitmap src) {
        if (src == null) {
            return null;
        }

        final Bitmap bitmap;
        if (src.getWidth() > 250 || src.getHeight() > 250) {
            bitmap = ThumbnailUtils.extractThumbnail(src, 250, 250);
        } else {
            bitmap = src;
        }

        byte[] thumbData = null;
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream);
            thumbData = outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            /*if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }*/
        }
        return thumbData;
    }

    /**
     * 此方法是耗时操作，如果对于特别大的图，那么需要做异步
     *
     * Note:外部传入的bitmap可能会被用于其他的地方，所以这里不能做recycle()
     */
    static String saveLargeBitmap(final Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }

        String path = SlConfig.pathTemp;
        if (!TextUtils.isEmpty(path)) {
            String imagePath = path + "sl_large_pic";
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(imagePath);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                /*if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }*/
            }
            return imagePath;
        } else {
            return null;
        }
    }
}

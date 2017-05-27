package com.liulishuo.share;

import android.app.Activity;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.liulishuo.share.activity.SL_WeiXinHandlerActivity;
import com.liulishuo.share.pay.alipay.Alipay;
import com.liulishuo.share.type.SsoPayType;

import static com.liulishuo.share.type.SsoPayType.ALIPAY;
import static com.liulishuo.share.type.SsoPayType.WEIXIN;

public class SsoPayManager {

    public static OnPayListener onPayListener;

    public static void pay(@NonNull Activity context, @SsoPayType String type,
            String payData, @Nullable OnPayListener listener) {
        SsoPayManager.onPayListener = listener;
        switch (type) {
            case WEIXIN:
                if (ShareLoginSDK.isWeiXinInstalled(context)) {
                    SL_WeiXinHandlerActivity.pay(context, payData);
                } else {
                    if (SsoPayManager.onPayListener != null) {
                        SsoPayManager.onPayListener.onError("未安装微信");
                    }
                }
                break;
            case ALIPAY:
                Alipay.pay(context, payData);
                break;
            default:
                throw new IllegalArgumentException("not supported platform: " + type);
        }
    }

    public static void recycle() {
        onPayListener = null;
    }

    public static class OnPayListener {

        @CallSuper
        public void onSuccess() {
            onComplete();
        }

        @CallSuper
        public void onPending() {
            onComplete();
        }

        @CallSuper
        public void onCancel() {
            onComplete();
        }

        @CallSuper
        public void onError(String errMsg) {
            onComplete();
        }

        private void onComplete() {
            SsoPayManager.recycle();
        }
    }
}

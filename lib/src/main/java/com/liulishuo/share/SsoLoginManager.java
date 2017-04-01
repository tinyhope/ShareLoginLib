package com.liulishuo.share;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.liulishuo.share.activity.SL_QQHandlerActivity;
import com.liulishuo.share.activity.SL_WeiBoHandlerActivity;
import com.liulishuo.share.activity.SL_WeiXinHandlerActivity;
import com.liulishuo.share.type.SsoLoginType;

import static com.liulishuo.share.type.SsoLoginType.QQ;
import static com.liulishuo.share.type.SsoLoginType.WEIBO;
import static com.liulishuo.share.type.SsoLoginType.WEIXIN;

/**
 * @author Kale
 * @date 2016/3/30
 */
public class SsoLoginManager {

    @Nullable
    public static LoginListener listener;

    public static void login(@NonNull Activity activity, @SsoLoginType String type, @Nullable LoginListener listener) {
        login(activity, type, listener, null);
    }

    /**
     * @param weixinCodeRespListener 得到微信code的listener。如果不为空，loginListener将不会被自动调用，必须要手动调用。
     */
    public static void login(@NonNull Activity activity, @SsoLoginType String type,
            @Nullable LoginListener listener, @Nullable WXLoginRespListener weixinCodeRespListener) {
        SsoLoginManager.listener = listener;
        switch (type) {
            case QQ:
                if (ShareLoginSDK.isQQInstalled(activity)) {
                    Intent intent = new Intent(activity, SL_QQHandlerActivity.class).putExtra(ShareLoginSDK.KEY_IS_LOGIN_TYPE, true);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    if (listener != null) {
                        listener.onError("未安装QQ");
                    }
                }
                break;
            case WEIBO:
                Intent intent = new Intent(activity, SL_WeiBoHandlerActivity.class).putExtra(ShareLoginSDK.KEY_IS_LOGIN_TYPE, true);
                activity.startActivity(intent);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                break;
            case WEIXIN:
                if (ShareLoginSDK.isWeiXinInstalled(activity)) {
                    SL_WeiXinHandlerActivity.wxRespListener = weixinCodeRespListener;
                    SL_WeiXinHandlerActivity.login(activity.getApplicationContext());
                    activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    if (listener != null) {
                        listener.onError("未安装微信");
                    }
                }
                break;
        }
    }

    public static void recycle() {
        listener = null;
        SL_WeiXinHandlerActivity.wxRespListener = null;
    }

    public static class LoginListener {

        /**
         * @param accessToken 第三方给的一次性token，几分钟内会失效
         * @param uId         用户的id
         * @param expiresIn   过期时间
         * @param wholeData   第三方本身返回的全部json数据
         */
        @CallSuper
        public void onSuccess(String accessToken, String uId, long expiresIn, @Nullable String wholeData) {
            onComplete();
        }

        @CallSuper
        public void onError(String errorMsg) {
            onComplete();
        }

        @CallSuper
        public void onCancel() {
            onComplete();
        }

        @CallSuper
        protected void onComplete() {
            SsoLoginManager.recycle();
        }
    }

    public interface WXLoginRespListener {

        void onLoginResp(String respCode, SsoLoginManager.LoginListener listener);
    }

}

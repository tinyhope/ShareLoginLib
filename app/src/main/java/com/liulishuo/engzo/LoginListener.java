package com.liulishuo.engzo;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.liulishuo.share.OAuthUserInfo;
import com.liulishuo.share.SsoLoginManager;
import com.liulishuo.share.SsoUserInfoManager;
import com.liulishuo.share.type.SsoLoginType;

/**
 * @author Kale
 * @date 2016/4/5
 */
class LoginListener extends SsoLoginManager.LoginListener {

    private static final String TAG = "LoginListener";

    private MainActivity activity;

    private
    @SsoLoginType
    String type;

    LoginListener(MainActivity activity, @SsoLoginType String type) {
        this.activity = activity;
        this.type = type;
    }

    @Override
    public void onSuccess(String accessToken, String userId, long expiresIn, String data) {
        super.onSuccess(accessToken, userId, expiresIn, data);
        Log.d(TAG, "accessToken = " + accessToken + "\nuid = " + userId + "\nexpires_in = " + expiresIn);
        loadUserInfo(accessToken, userId);

        String result = "登录成功";
        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
        activity.handResult(result);
    }

    @Override
    public void onError(String msg) {
        super.onError(msg);
        String result = "登录失败,失败信息：" + msg;
        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
        activity.handResult(result);
    }

    @Override
    public void onCancel() {
        super.onCancel();
        String result = "取消登录";
        Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
        activity.handResult(result);
    }

    /**
     * 加载用户的个人信息
     */
    private void loadUserInfo(String accessToken, String userId) {
        SsoUserInfoManager.getUserInfo(activity, type, accessToken, userId, new SsoUserInfoManager.UserInfoListener() {
            @Override
            public void onSuccess(@NonNull final OAuthUserInfo userInfo) {
                final String info = " nickname = " + userInfo.nickName + "\n"
                        + " sex = " + userInfo.sex + "\n"
                        + " id = " + userInfo.userId;

                activity.onGotUserInfo(info, userInfo.headImgUrl);
            }

            @Override
            public void onError(String msg) {
                activity.onGotUserInfo(" 出错了！\n" + msg, null);
            }
        });
    }
}

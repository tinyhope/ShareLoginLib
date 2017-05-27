package com.liulishuo.share.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.liulishuo.share.ShareLoginSDK;
import com.liulishuo.share.SsoLoginManager;
import com.liulishuo.share.SsoShareManager;
import com.liulishuo.share.content.ShareContent;
import com.liulishuo.share.type.ShareContentType;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.api.BaseMediaObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.sina.weibo.sdk.share.WbShareHandler;
import com.sina.weibo.sdk.utils.Utility;
import org.json.JSONException;
import org.json.JSONObject;

import static com.liulishuo.share.SlConfig.weiBoAppId;
import static com.liulishuo.share.SlConfig.weiBoRedirectUrl;
import static com.liulishuo.share.SlConfig.weiBoScope;

/**
 * @author Jack Tony
 *         2015/10/26
 *
 * https://github.com/sinaweibosdk/weibo_android_sdk
 */
public class SL_WeiBoHandlerActivity extends Activity {

    private SsoHandler ssoHandler;
    private WbShareHandler shareHandler;
    private boolean isFirstIn = true;
    private boolean isLogin = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLogin = getIntent().getBooleanExtra(ShareLoginSDK.KEY_IS_LOGIN_TYPE, true);

        String appId = weiBoAppId;
        if (TextUtils.isEmpty(appId)) {
            throw new NullPointerException("请通过shareBlock初始化weiBoAppId");
        }

        AuthInfo authInfo = new AuthInfo(getApplicationContext(), appId, weiBoRedirectUrl, weiBoScope);
        WbSdk.install(getApplicationContext(), authInfo);

        if (isLogin) {
            ssoHandler = new SsoHandler(this);

            if (savedInstanceState == null) {
                // 防止不保留活动情况下activity被重置后直接进行操作的情况
                doLogin(SsoLoginManager.listener);
            }
        } else {
            shareHandler = new WbShareHandler(this);
            shareHandler.registerApp();
            if (savedInstanceState == null) {
                // 防止不保留活动情况下activity被重置后直接进行操作的情况
                doShare();
            } else {
                shareHandler.doResultIntent(getIntent(), shareCallback);
            }
        }

        if (savedInstanceState != null) {
            isFirstIn = false;
        }
    }

    /**
     * 因为微博客户端在用户取消分享后，用户点击保存到草稿箱后就不能接收到回调。
     * 因此，在这里必须进行强制关闭，不能依赖回调来关闭。
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (isFirstIn) {
            isFirstIn = false;
        } else {
            if (isLogin) {
                // 这里处理通过网页登录无回调的问题
                finish();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!isLogin) {
            shareHandler.doResultIntent(intent, shareCallback);
            finish();
        }
    }

    /**
     * 解析用户【登录】的结果
     * SSO 授权回调
     * 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (isLogin) {
            if (ssoHandler != null) {
                ssoHandler.authorizeCallBack(requestCode, resultCode, data);
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // login
    ///////////////////////////////////////////////////////////////////////////

    private void doLogin(final SsoLoginManager.LoginListener listener) {
        WbAuthListener authListener = new WbAuthListener() {
            @Override
            public void onSuccess(Oauth2AccessToken accessToken) {
                if (listener != null && accessToken != null) {
                    if (accessToken.isSessionValid()) {
                        listener.onSuccess(accessToken.getToken(), accessToken.getUid(),
                                accessToken.getExpiresTime() / 1000000, oAuthData2Json(accessToken));
                    }
                }
                finish();
            }

            @Override
            public void cancel() {
                if (listener != null) {
                    listener.onCancel();
                }
                finish();
            }

            @Override
            public void onFailure(WbConnectErrorMessage wbConnectErrorMessage) {
                if (listener != null) {
                    listener.onError(wbConnectErrorMessage.getErrorMessage());
                }
                finish();
            }
        };

        /*
         * 此种授权方式会根据手机是否安装微博客户端来决定使用sso授权还是网页授权
         * 1. SSO 授权时，需要在 onActivityResult 中调用 {@link SsoHandler#authorizeCallBack} 后，该回调才会被执行。
         * 2. 非SSO 授权时，当授权结束后，该回调就会被执行
         */
        ssoHandler.authorize(authListener);
    }

    @Nullable
    private String oAuthData2Json(@NonNull Oauth2AccessToken data) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uid", data.getUid());
            jsonObject.put("refresh_token", data.getRefreshToken());
            jsonObject.put("access_token", data.getToken());
            jsonObject.put("expires_in", String.valueOf(data.getExpiresTime() / 1000000));
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    ///////////////////////////////////////////////////////////////////////////
    // share
    ///////////////////////////////////////////////////////////////////////////

    private void doShare() {
        ShareContent content = getIntent().getParcelableExtra(SsoShareManager.KEY_CONTENT);
        if (content == null) {
            throw new NullPointerException("ShareContent is null，intent = " + getIntent());
        }
        WeiboMultiMessage wbMessage = createShareObject(content);
        shareHandler.shareMessage(wbMessage, true);
    }

    private WeiboMultiMessage createShareObject(@NonNull ShareContent shareContent) {
        WeiboMultiMessage weiboMultiMessage = new WeiboMultiMessage();
        switch (shareContent.getType()) {
            case ShareContentType.TEXT:
                // 纯文字
                weiboMultiMessage.textObject = getTextObj(shareContent);
                break;
            case ShareContentType.WEBPAGE:
                // 网页
                weiboMultiMessage.textObject = getTextObj(shareContent);
                weiboMultiMessage.mediaObject = getWebPageObj(shareContent);
                break;
            case ShareContentType.PIC:
                // 纯图片
                // weiboMultiMessage.imageObject = getImageObj(shareContent.getLargeBmpPath());
                // break;
            case ShareContentType.MUSIC:
            default:
                throw new IllegalArgumentException("不支持该分享");
        }
        if (!weiboMultiMessage.checkArgs()) {
            throw new IllegalArgumentException("分享信息的参数类型不正确");
        }
        return weiboMultiMessage;
    }

    /**
     * 创建文本消息对象
     */
    private TextObject getTextObj(ShareContent shareContent) {
        TextObject textObject = new TextObject();
        textObject.text = shareContent.getTitle();
        return textObject;
    }

    /**
     * 创建多媒体（网页）消息对象
     */
    private WebpageObject getWebPageObj(ShareContent shareContent) {
        WebpageObject mediaObject = new WebpageObject();
        buildMediaObj(mediaObject, shareContent);
        mediaObject.defaultText = shareContent.getTitle();
        mediaObject.actionUrl = shareContent.getURL();
        return mediaObject;
    }

    private void buildMediaObj(BaseMediaObject mediaObject, ShareContent shareContent) {
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = shareContent.getTitle();
        mediaObject.description = shareContent.getSummary();
        mediaObject.thumbData = shareContent.getThumbBmpBytes();
    }

    private static WbShareCallback shareCallback = new WbShareCallback() {

        private SsoShareManager.ShareStateListener listener = SsoShareManager.listener;

        @Override
        public void onWbShareSuccess() {
            if (listener != null) {
                listener.onSuccess();
            }
        }

        @Override
        public void onWbShareCancel() {
            if (listener != null) {
                listener.onCancel();
            }
        }

        @Override
        public void onWbShareFail() {
            if (listener != null) {
                listener.onError("");
            }
        }
    };
}

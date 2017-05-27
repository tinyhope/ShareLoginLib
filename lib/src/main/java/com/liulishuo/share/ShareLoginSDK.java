package com.liulishuo.share;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.sina.weibo.sdk.WeiboAppManager;
import com.sina.weibo.sdk.auth.WbAppInfo;
import com.sina.weibo.sdk.utils.LogUtil;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import java.io.File;
import java.util.List;
import java.util.Locale;

public class ShareLoginSDK {

    public static final String TAG = "ShareLoginSDK";

    public static final String KEY_IS_LOGIN_TYPE = "action_type";

    private ShareLoginSDK() {
    }

    public static void init(Application application, @NonNull SlConfig cfg) {
        if (SlConfig.isDebug) {
            LogUtil.enableLog();
        } else {
            LogUtil.disableLog();
        }

        if (TextUtils.isEmpty(SlConfig.pathTemp)) {
            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                try {
                    SlConfig.pathTemp = application.getExternalCacheDir() + File.separator;
                    File dir = new File(SlConfig.pathTemp);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                } catch (Exception e) {
                    if (SlConfig.isDebug) {
                        throw e;
                    }
                    SlConfig.pathTemp = null;
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 判断第三方客户端是否安装
    ///////////////////////////////////////////////////////////////////////////

    public static boolean isQQInstalled(@NonNull Context context) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        if (pm == null) {
            return false;
        }
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        for (PackageInfo info : packages) {
            String name = info.packageName.toLowerCase(Locale.ENGLISH);
            if ("com.tencent.mobileqq".equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isWeiBoInstalled(@NonNull Context context) {
        WbAppInfo wbAppInfo = WeiboAppManager.getInstance(context.getApplicationContext()).getWbAppInfo();
        return wbAppInfo != null && wbAppInfo.isLegal();
    }

    public static boolean isWeiXinInstalled(Context context) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, SlConfig.weiXinAppId, true);
        return api.isWXAppInstalled();
    }
}

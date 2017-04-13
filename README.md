# ShareLoginLib   
[![](https://jitpack.io/v/MankIndX/ShareLoginLib.svg)](https://jitpack.io/#MankIndX/ShareLoginLib)  

ShareLoginLib likes simple sharesdk or umeng in China . It is a tool to help developers to share their content (image , text or webpage ) to WeChat,Weibo and QQ.  

![](./screenshot/logo.png)

## 示例
![](./screenshot/login.png) ![](./screenshot/share.png) ![](./screenshot/wechat.png)

## 添加依赖

1.在项目外层的build.gradle中添加JitPack仓库

```
repositories {
  // ...
	maven {
		url "https://jitpack.io"
	}
}
```

2.在用到的项目中添加依赖  
>	compile 'com.github.MankIndX:ShareLoginLib:[Latest release](https://github.com/MankIndX/ShareLoginLib/releases) (< click it)'  

## 使用

### 登录、分享、支付  
```JAVA  
// 登录
SsoLoginManager.login(this, SsoLoginType.XXX, new SsoLoginManager.LoginListener(){
                    @Override
                    public void onSuccess(String accessToken, String uId, long expiresIn, @Nullable String wholeData) {
                        super.onSuccess(accessToken, uId, expiresIn, wholeData); // must call super
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel(); // must call super
                    }

                    @Override
                    public void onError(String errorMsg) {
                        super.onError(errorMsg); // must call super
                    }
                });


// 分享
SsoShareManager.share(MainActivity.this, SsoShareType.XXX
        new ShareContentWebpage("title", "summary", "http://www.kale.com", mBitmap),
        new SsoShareManager.ShareStateListener(){
                    @Override
                    public void onSuccess() {
                        super.onSuccess(); // must call super
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel(); // must call super
                    }

                    @Override
                    public void onError(String msg) {
                        super.onError(msg); // must call super
                    }
                });

// 支付
SsoPayManager.pay(MainActivity.this, SsoPayType.XXX, payData,
        new SsoPayManager.OnPayListener(){
                    @Override
                    public void onSuccess() {
                        super.onSuccess(); // must call super
                    }

                    @Override
                    public void onPending() {
                        super.onPending(); // must call super
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel(); // must call super
                    }

                    @Override
                    public void onError(String msg) {
                        super.onError(msg); // must call super
                    }
                });

```   

### 判断是否已安装第三方客户端  
```JAVA
ShareLoginSDK.isWeiXinInstalled(this);
ShareLoginSDK.isWeiBoInstalled(this);
ShareLoginSDK.isQQInstalled(this);
```

部分手机上需要读取手机app列表的权限。


### 通过token和id得到用户信息
```JAVA
SsoUserInfoManager.getUserInfo(context, SsoLoginType.XXX, accessToken, userId,
    new UserInfoListener() {

        public void onSuccess(@NonNull OAuthUserInfo userInfo) {
            // 可以得到：昵称、性别、头像、用户id
        }

        public void onError(String errorMsg) {
        }
    });
```  

更多详细的操作请参考项目的demo。

## 配置工作

### 1. 在build.gradle中配置QQ的key  

```java
defaultConfig {
	// ...
    applicationId "xxx.xxx.xxx" // 你的app包名
    manifestPlaceholders = ["tencentAuthId": "tencent123456"]   // tencent+你的AppId
}
```

### 2. 在使用功能前配置常量

```java  
SlConfig config = new SlConfig.Builder()
            .debug(false)
            .appName("Your App Name")
            .picTempFile(null) // 指定缓存缩略图的目录名字，如无特殊要求可以是null
            .qq(QQ_APPID, QQ_SCOPE)
            .weiXin(WEIXIN_APPID, WEIXIN_SECRET)
            .weiBo(WEIBO_APPID, WEIBO_REDIRECT_URL, WEIBO_SCOPE).build();

ShareLoginSDK.init(this, config);
```

## 重要说明

- 需要强制获取外部存储卡的权限，否则会拿不到分享的图片
- 签名后的app才可以进行测试
- 使用者要在第三方平台进行注册后才可测试
- 库作者不会提供任何和签名、密码、AppId等有关信息
- 测试app需要有和第三方sdk约定好的正确签名

## 推荐的测试环境  

- 开启不保留活动
- 未安装第三方应用  
- 安装第三方应用，但第三方应用未登录  
- 未开启不保留活动，并且第三方应用已经登录

## 配置运行本demo的环境

如果你要运行该项目给出的demo，那么可以修改本地的`gradle.properties`文件，将下列信息修改成你自己的值。   

```
STORE_FILE_PATH	       xxxxx
STORE_PASSWORD	       xxxxx
KEY_ALIAS		       xxxxx
KEY_PASSWORD	       xxxxx
PACKAGE_NAME_SUFFIX    xxxxx
TENCENT_AUTHID tencent xxxxx
```

## LICENCE

  The MIT License (MIT)

  Copyright (c) 2015-2017 kale Inc.

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.

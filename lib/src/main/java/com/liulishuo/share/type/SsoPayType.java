package com.liulishuo.share.type;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
@StringDef({SsoPayType.ALIPAY, SsoPayType.WEIXIN})
public @interface SsoPayType {

	String ALIPAY = "ALIPAY", WEIXIN = "WEIXIN";

}

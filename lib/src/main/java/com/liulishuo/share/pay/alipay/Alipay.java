package com.liulishuo.share.pay.alipay;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.alipay.sdk.app.PayTask;
import com.liulishuo.share.SsoPayManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author MankIndX
 *         2017/4/12
 */
public class Alipay {

	public static void pay(@NonNull Activity activity, @Nullable String payData) {
		Data data = parseData(payData);
		if (data != null) {
			new Thread(() -> {
				PayTask payTask = new PayTask(activity);
				Result result = new Result(payTask.payV2(data.toStringData(), true));
				activity.runOnUiThread(() -> handlePayResult(result, SsoPayManager.onPayListener));
			}).start();
		} else {
			if (SsoPayManager.onPayListener != null) {
				SsoPayManager.onPayListener.onError("支付请求错误");
			}
		}
	}

	private static void handlePayResult(Result result, SsoPayManager.OnPayListener listener) {
		if (listener != null) {
			if (result.isSuccess()) {
				listener.onSuccess();
			} else if (result.isPending()) {
				listener.onPending();
			} else if (result.isCancelled()) {
				listener.onCancel();
			} else {
				listener.onError("支付失败");
			}
		}
	}

	@Nullable
	private static Data parseData(String payData) {
		try {
			Data data = new Data();
			JSONObject jo = new JSONObject(payData);
			data.partner = jo.getString("partner");
			data.seller_id = jo.getString("seller_id");
			data.out_trade_no = jo.getString("out_trade_no");
			data.subject = jo.getString("subject");
			data.body = jo.getString("body");
			data.total_fee = jo.getString("total_fee");
			data.notify_url = jo.getString("notify_url");
			data.payment_type = jo.getString("payment_type");
			data.it_b_pay = jo.getString("it_b_pay");
			data.sign = jo.getString("sign");
			return data;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static class Data {
		public String sign;
		public String body;
		public String it_b_pay;
		public String notify_url;
		public String out_trade_no;
		public String partner;
		public String payment_type;
		public String seller_id;
		public String subject;
		public String total_fee;

		public String toStringData() {
			StringBuilder builder = new StringBuilder();
			builder.append("partner=\"").append(partner).append("\"");
			builder.append("&seller_id=\"").append(seller_id).append("\"");
			builder.append("&out_trade_no=\"").append(out_trade_no).append("\"");
			builder.append("&subject=\"").append(subject).append("\"");
			builder.append("&body=\"").append(body).append("\"");
			builder.append("&total_fee=\"").append(total_fee).append("\"");
			builder.append("&notify_url=\"").append(notify_url).append("\"");
			builder.append("&service=\"mobile.securitypay.pay\"");
			builder.append("&payment_type=\"").append(payment_type).append("\"");
			builder.append("&_input_charset=\"utf-8\"");
			builder.append("&it_b_pay=\"").append(it_b_pay).append("\"");
			builder.append("&sign=\"").append(sign).append("\"");
			builder.append("&sign_type=\"RSA\"");
			return builder.toString();
		}
	}
}

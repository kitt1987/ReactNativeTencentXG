package com.kh.tencentxg;

import java.lang.Exception;

import android.content.Context;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGLocalMessage;

public class TencentXGModule extends ReactContextBaseJavaModule {

    private Context context;
    private static final String TAG = "TencentXG";

    public TencentXGModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext.getApplicationContext();
    }

    @Override
    public String getName() {
        return "TencentXG";
    }

    @ReactMethod
    public void registerPush() {
        XGPushManager.registerPush(this.context);
    }

    @ReactMethod
    public void registerPushThen(final Promise done) {
        XGPushManager.registerPush(this.context, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                done.resolve(data);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                done.reject(new Exception(msg));
            }
        });
    }

    @ReactMethod
    public void registerPushAndBindAccount(String account) {
        XGPushManager.registerPush(this.context, account);
    }

    @ReactMethod
    public void registerPushAndBindAccountThen(String account, final Promise done) {
        XGPushManager.registerPush(this.context, account, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                done.resolve(data);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                done.reject(new Exception(msg));
            }
        });
    }

    @ReactMethod
    public void registerPushWithTicketThen(String account, String ticket, int ticketType, String qua, final Promise done) {
      XGPushManager.registerPush(this.context, account, ticket, ticketType, qua, new XGIOperateCallback() {
        @Override
        public void onSuccess(Object data, int flag) {
            done.resolve(data);
        }

        @Override
        public void onFail(Object data, int errCode, String msg) {
            done.reject(new Exception(msg));
        }
      });
    }

    @ReactMethod
    public void unregisterPush() {
      XGPushManager.unregisterPush(this.context);
    }

    @ReactMethod
    public void setTag(String tag) {
      XGPushManager.setTag(this.context, tag);
    }

    @ReactMethod
    public void deleteTag(String tag) {
      XGPushManager.deleteTag(this.context, tag);
    }

    @ReactMethod
    public void addLocalNotification(String title, String content, String date, String hour, String minute) {
        XGLocalMessage local_msg = new XGLocalMessage();
        local_msg.setType(1);
    		local_msg.setTitle(title);
    		local_msg.setContent(content);
    		local_msg.setDate(date);
    		local_msg.setHour(hour);
    		local_msg.setMin(minute);
    		XGPushManager.addLocalNotification(this.context, local_msg);
    }

    // XGPushConfig

    @ReactMethod
    public void enableDebug(Boolean enable) {
        XGPushConfig.enableDebug(this.context, enable);
    }

    @ReactMethod
    public void setCredential(Integer accessId, String accessKey) {
        XGPushConfig.setAccessId(this.context, accessId);
        XGPushConfig.setAccessKey(this.context, accessKey);
    }

    @ReactMethod
    public String getDeviceToken() {
        return XGPushConfig.getToken(this.context);
    }
}

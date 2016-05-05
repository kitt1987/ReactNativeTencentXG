package com.kh.tencentxg;

import java.lang.Exception;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;
import android.content.IntentFilter;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
//import com.facebook.react.bridge.Promise;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGLocalMessage;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushTextMessage;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;

public class TencentXGModule extends ReactContextBaseJavaModule {

    private Context context;
    private ReactApplicationContext reactContext;
    private static final String TAG = "TencentXG";
    private static final String RCTLocalNotificationEvent = "localNotification";
    private static final String RCTRemoteNotificationEvent = "notification";
    private static final String RCTRegisteredEvent = "register";
    private static final String RCTFailureEvent = "error";

    public TencentXGModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.context = reactContext.getApplicationContext();
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put("LocalNotificationEvent", RCTLocalNotificationEvent);
        constants.put("RemoteNotificationEvent", RCTRemoteNotificationEvent);
        constants.put("RegisteredEvent", RCTRegisteredEvent);
        constants.put("FailureEvent", RCTFailureEvent);
        return constants;
    }

    @Override
    public String getName() {
        return "TencentXG";
    }

    @ReactMethod
    public void registerPush() {
        XGPushManager.registerPush(this.context, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                sendEvent(RCTRegisteredEvent, data);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                sendEvent(RCTFailureEvent, msg);
            }
        });
    }

    @ReactMethod
    public void registerPushAndBindAccount(String account) {
        XGPushManager.registerPush(this.context, account, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                sendEvent(RCTRegisteredEvent, data);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                sendEvent(RCTFailureEvent, msg);
            }
        });
    }

    @ReactMethod
    public void registerPushWithTicket(String account, String ticket, int ticketType, String qua) {
      XGPushManager.registerPush(this.context, account, ticket, ticketType, qua, new XGIOperateCallback() {
          @Override
          public void onSuccess(Object data, int flag) {
              sendEvent(RCTRegisteredEvent, data);
          }

          @Override
          public void onFail(Object data, int errCode, String msg) {
              sendEvent(RCTFailureEvent, msg);
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

    private void sendEvent(String eventName,
                           @Nullable Object params) {
        this.reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
}

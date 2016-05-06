package com.kh.tencentxg;

import java.util.HashMap;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.content.IntentFilter;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.tencent.android.tpush.XGPushManager;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGLocalMessage;
import com.tencent.android.tpush.XGPushBaseReceiver;

public class TencentXGModule extends ReactContextBaseJavaModule implements LifecycleEventListener {

    private Context context;
    private ReactApplicationContext reactContext;
    private BroadcastReceiver innerReceiver;
    private static final String LogTag = "TencentXG";
    private static final String RCTLocalNotificationEvent = "localNotification";
    private static final String RCTRemoteNotificationEvent = "notification";
    private static final String RCTRegisteredEvent = "register";
    private static final String RCTFailureEvent = "error";

    public TencentXGModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        this.context = reactContext.getApplicationContext();
        innerReceiver = new InnerMessageReceiver(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(XGMessageReceiver.MActionNotification);
        filter.addAction(XGMessageReceiver.MActionCustomNotification);
        filter.addAction(XGMessageReceiver.MActionUnregister);
        filter.addAction(XGMessageReceiver.MActionRegistration);
        filter.addAction(XGMessageReceiver.MActionTagSetting);
        filter.addAction(XGMessageReceiver.MActionTagDeleting);
        filter.addAction(XGMessageReceiver.MActionClickNotification);
        LocalBroadcastManager.getInstance(this.context).registerReceiver(this.innerReceiver, filter);
        reactContext.addLifecycleEventListener(this);
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

    private void sendEvent(String eventName, @Nullable Object params) {
        this.reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @Override
    public void onHostResume() {

    }

    @Override
    public void onHostPause() {
        XGPushManager.onActivityStoped(this.getCurrentActivity());
        LocalBroadcastManager.getInstance(this.context).unregisterReceiver(this.innerReceiver);
    }

    @Override
    public void onHostDestroy() {

    }

    public void sendEvent(Intent intent) {
        Bundle payload = intent.getExtras().getBundle("data");
        switch (intent.getAction()) {
            case XGMessageReceiver.MActionNotification:
                WritableMap params = Arguments.createMap();
                params.putString("Content", payload.getString("Content"));
                params.putString("Title", payload.getString("Title"));
                params.putInt("MsgId", (int)payload.getLong("MsgId"));
                params.putInt("NotificationId", (int)payload.getLong("NotificationId"));
                params.putInt("NActionType", (int)payload.getLong("NActionType"));

                Log.d(LogTag, "Got notification " + payload.toString());
                sendEvent(RCTRemoteNotificationEvent, params);
                break;
            case XGMessageReceiver.MActionCustomNotification:
                Log.d(LogTag, "Got custom notification " + payload.toString());
                sendEvent(RCTRemoteNotificationEvent, payload);
                break;
            case XGMessageReceiver.MActionUnregister: {
                int errorCode = payload.getInt("errorCode");
                Log.d(LogTag, "Got unregister result " + payload.toString());
                if (errorCode != XGPushBaseReceiver.SUCCESS) {
                    sendEvent(RCTFailureEvent, "Fail to set unregister caused by " + errorCode);
                }
                break;
            }
            case XGMessageReceiver.MActionRegistration: {
                int errorCode = payload.getInt("errorCode");
                Log.d(LogTag, "Got register result " + payload.toString());
//                if (errorCode != XGPushBaseReceiver.SUCCESS) {
//                    sendEvent(RCTFailureEvent, "Fail to set register caused by " + errorCode);
//                } else {
//                    sendEvent(RCTRegisteredEvent, payload.getString("Token"));
//                }

                break;
            }

            case XGMessageReceiver.MActionTagSetting: {
                Log.d(LogTag, "Got tag setting result " + payload.toString());
                int errorCode = payload.getInt("errorCode");
                if (errorCode != XGPushBaseReceiver.SUCCESS) {
                    sendEvent(RCTFailureEvent, "Fail to set tag " + payload.getString("tagName") +
                            " caused by " + errorCode);
                }
                break;
            }

            case XGMessageReceiver.MActionTagDeleting: {
                Log.d(LogTag, "Got tag deleting result " + payload.toString());
                int errorCode = payload.getInt("errorCode");
                if (errorCode != XGPushBaseReceiver.SUCCESS) {
                    sendEvent(RCTFailureEvent, "Fail to delete tag " + payload.getString("tagName") +
                            " caused by " + errorCode);
                }
                break;
            }

            case XGMessageReceiver.MActionClickNotification:
                Log.d(LogTag, "Got notification clicking result " + payload.toString());
//                sendEvent(RCTRegisteredEvent, payload);
                break;
        }
    }
}

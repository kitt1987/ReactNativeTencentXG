package com.kh.tencentxg;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

public class XGMessageReceiver extends XGPushBaseReceiver {

    public static final String MActionNotification = "com.kh.tencentxg.XG-Notification";
    public static final String MActionCustomNotification = "com.kh.tencentxg.XG-CustomNotification";
    public static final String MActionUnregister = "com.kh.tencentxg.XG-Unregister";
    public static final String MActionRegistration = "com.kh.tencentxg.XG-Registration";
    public static final String MActionTagSetting = "com.kh.tencentxg.XG-TagSetting";
    public static final String MActionTagDeleting = "com.kh.tencentxg.XG-TagDeleting";
    public static final String MActionClickNotification = "com.kh.tencentxg.XG-ClickNotification";

    private static final String LogTag = "[TXG]XG Receiver";

	// 通知展示
	@Override
	public void onNotifactionShowedResult(Context context,
			XGPushShowedResult notifiShowedRlt) {
		if (context == null || notifiShowedRlt == null) return;

        Log.d(LogTag, "Got notification " + notifiShowedRlt.toString());

        Bundle payload = new Bundle();
        payload.putString("Content", notifiShowedRlt.getContent());
        payload.putString("Title", notifiShowedRlt.getTitle());
        payload.putLong("MsgId", notifiShowedRlt.getMsgId());
        payload.putLong("NotificationId", notifiShowedRlt.getNotifactionId());
        payload.putLong("NActionType", notifiShowedRlt.getNotificationActionType());
        payload.putString("CustomContent", notifiShowedRlt.getCustomContent());

        Intent intent = new Intent(MActionNotification);
        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.putExtra("data", payload);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

    @Override
    public void onTextMessage(Context context, XGPushTextMessage message) {
        if (context == null || message == null) return;
        Log.d(LogTag, "Got text notification " + message.toString());

        Bundle payload = new Bundle();
        payload.putString("Title", message.getTitle());
        payload.putString("Content", message.getContent());
        payload.putString("CustomContent", message.getCustomContent());

        Intent intent = new Intent(MActionCustomNotification);
        intent.putExtra("data", payload);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    @Override
    public void onRegisterResult(Context context, int errorCode,
                                 XGPushRegisterResult message) {
        if (context == null || message == null) return;
        Log.d(LogTag, "Got register result " + message.toString());

        Bundle payload = new Bundle();
        payload.putInt("errorCode", errorCode);
        payload.putLong("AccessId", message.getAccessId());
        payload.putString("Account", message.getAccount());
        payload.putString("DeviceId", message.getDeviceId());
        payload.putString("Ticket", message.getTicket());
        payload.putShort("TicketType", message.getTicketType());
        payload.putString("Token", message.getToken());

        Intent intent = new Intent(MActionRegistration);
        intent.putExtra("data", payload);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

	@Override
	public void onUnregisterResult(Context context, int errorCode) {
        if (context == null) return;
        Log.d(LogTag, "Got unregister result " + errorCode);

        Bundle payload = new Bundle();
        payload.putInt("errorCode", errorCode);

        Intent intent = new Intent(MActionUnregister);
        intent.putExtra("data", payload);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

	@Override
	public void onSetTagResult(Context context, int errorCode, String tagName) {
        if (context == null) return;
        Log.d(LogTag, "Got setting tag result " + errorCode);

        Bundle payload = new Bundle();
        payload.putInt("errorCode", errorCode);
        payload.putString("tagName", tagName);

        Intent intent = new Intent(MActionTagSetting);
        intent.putExtra("data", payload);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

	@Override
	public void onDeleteTagResult(Context context, int errorCode, String tagName) {
        if (context == null) return;
        Log.d(LogTag, "Got deleting tag result " + errorCode);

        Bundle payload = new Bundle();
        payload.putInt("errorCode", errorCode);
        payload.putString("tagName", tagName);

        Intent intent = new Intent(MActionTagDeleting);
        intent.putExtra("data", payload);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

	@Override
	public void onNotifactionClickedResult(Context context,
			XGPushClickedResult message) {
        if (context == null || message == null) return;
        Log.d(LogTag, "Got message click " + message.toString());

        Bundle payload = new Bundle();
        payload.putString("Content", message.getContent());
        payload.putString("Title", message.getTitle());
        payload.putLong("MsgId", message.getMsgId());
        payload.putString("CustomContent", message.getCustomContent());
        payload.putLong("NActionType", message.getNotificationActionType());
        payload.putLong("ActionType", message.getActionType());
        payload.putString("ActivityName", message.getActivityName());

        Intent intent = new Intent(MActionClickNotification);
        intent.putExtra("data", payload);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

}

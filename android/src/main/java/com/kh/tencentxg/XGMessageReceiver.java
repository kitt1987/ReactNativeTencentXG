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

    public static final String MActionNotification = "XG-Notification";
    public static final String MActionCustomNotification = "XG-CustomNotification";
    public static final String MActionUnregister = "XG-Unregister";
    public static final String MActionRegistration = "XG-Registration";
    public static final String MActionTagSetting = "XG-TagSetting";
    public static final String MActionTagDeleting = "XG-TagDeleting";
    public static final String MActionClickNotification = "XG-ClickNotification";

    private static final String LogTag = "[TXG]XG Receiver";

	// 通知展示
	@Override
	public void onNotifactionShowedResult(Context context,
			XGPushShowedResult notifiShowedRlt) {
		if (context == null || notifiShowedRlt == null) return;

        Bundle payload = new Bundle();
        payload.putString("Content", notifiShowedRlt.getContent());
        payload.putString("Title", notifiShowedRlt.getTitle());
        payload.putLong("MsgId", notifiShowedRlt.getMsgId());
        payload.putLong("NotificationId", notifiShowedRlt.getNotifactionId());
        payload.putLong("NActionType", notifiShowedRlt.getNotificationActionType());

        Intent intent = new Intent(MActionNotification);
        intent.putExtra("data", payload);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

    @Override
    public void onTextMessage(Context context, XGPushTextMessage message) {
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
        Bundle payload = new Bundle();
        payload.putInt("errorCode", errorCode);

        Intent intent = new Intent(MActionUnregister);
        intent.putExtra("data", payload);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

	@Override
	public void onSetTagResult(Context context, int errorCode, String tagName) {
        Bundle payload = new Bundle();
        payload.putInt("errorCode", errorCode);
        payload.putString("tagName", tagName);

        Intent intent = new Intent(MActionTagSetting);
        intent.putExtra("data", payload);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

	@Override
	public void onDeleteTagResult(Context context, int errorCode, String tagName) {
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
        Bundle payload = new Bundle();
        payload.putString("Content", message.getContent());
        payload.putString("Title", message.getTitle());
        payload.putLong("MsgId", message.getMsgId());
        payload.putString("CustomContent", message.getCustomContent());
        payload.putLong("NActionType", message.getNotificationActionType());
        payload.putLong("ActionType", message.getActionType());
        payload.putString("ActionType", message.getActivityName());

        Intent intent = new Intent(MActionUnregister);
        intent.putExtra("data", payload);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

}

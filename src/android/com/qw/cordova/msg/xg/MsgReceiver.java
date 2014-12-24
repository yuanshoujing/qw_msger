package com.qw.cordova.msg.xg;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.qw.cordova.msg.Messenger;
import com.qw.cordova.msg.MsgHandlerActivity;
import com.tencent.android.tpush.XGPushBaseReceiver;
import com.tencent.android.tpush.XGPushClickedResult;
import com.tencent.android.tpush.XGPushRegisterResult;
import com.tencent.android.tpush.XGPushShowedResult;
import com.tencent.android.tpush.XGPushTextMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 信鸽消息接收器
 *
 * @author: 袁首京<yuanshoujing@gmail.com>
 */
public class MsgReceiver extends XGPushBaseReceiver {
    private static final String TAG = "XGMsgReceiver";

    private Intent intent = new Intent("com.qq.xgdemo.activity.UPDATE_LISTVIEW");

    /**
     * 展示消息到通知栏
     *
     * @param context
     * @param notifiShowedRlt
     */
    @Override
    public void onNotifactionShowedResult(Context context,
                                          XGPushShowedResult notifiShowedRlt) {
        if (context == null || notifiShowedRlt == null) {
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("title", notifiShowedRlt.getTitle());
        bundle.putString("message", notifiShowedRlt.getContent());
        bundle.putInt("msgcnt", 1);
        bundle.putLong("msgId", notifiShowedRlt.getMsgId());

        this.createNotification(context, bundle);
    }

    @Override
    public void onRegisterResult(Context context, int errorCode,
                                 XGPushRegisterResult message) {
        if (context == null || message == null) {
            return;
        }

        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = message + "注册成功";
        }
        else {
            text = message + "注册失败，错误码：" + errorCode;
        }
        Log.d(TAG, text);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("event", "register");
            jsonObject.put("registerResult", text);

            Messenger.sendJavascript(jsonObject);
        }
        catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onUnregisterResult(Context context, int errorCode) {
        if (context == null) {
            return;
        }

        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "反注册成功";
        }
        else {
            text = "反注册失败" + errorCode;
        }
        Log.d(TAG, text);
    }

    @Override
    public void onSetTagResult(Context context, int errorCode, String tagName) {
        if (context == null) {
            return;
        }

        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"设置成功";
        }
        else {
            text = "\"" + tagName + "\"设置失败,错误码：" + errorCode;
        }
        Log.d(TAG, text);
    }

    @Override
    public void onDeleteTagResult(Context context, int errorCode, String tagName) {
        if (context == null) {
            return;
        }

        String text = "";
        if (errorCode == XGPushBaseReceiver.SUCCESS) {
            text = "\"" + tagName + "\"删除成功";
        }
        else {
            text = "\"" + tagName + "\"删除失败,错误码：" + errorCode;
        }
        Log.d(TAG, text);
    }

    @Override
    public void onNotifactionClickedResult(Context context,
                                           XGPushClickedResult message) {
        Log.d(TAG, "onNotifactionClickedResult");
    }

    @Override
    public void onTextMessage(Context context, XGPushTextMessage message) {
        if (context == null || message == null) {
            return;
        }

        if (message.getContent() == null || message.getContent().trim().length() == 0) {
            return;
        }

        String text = "收到消息:" + message.toString();
        Log.d(TAG, text);

        Bundle bundle = new Bundle();
        bundle.putString("title", message.getTitle());
        bundle.putString("message", message.getContent());
        bundle.putInt("msgcnt", 1);
        bundle.putInt("msgId", 0);

        if (Messenger.isInForeground()) {
            bundle.putBoolean("foreground", true);
            Messenger.sendExtras(bundle);
        }
        else {
            bundle.putBoolean("foreground", false);
            createNotification(context, bundle);
        }
    }

    private static String getAppName(Context context) {
        CharSequence appName =
                context.getPackageManager().getApplicationLabel(context.getApplicationInfo());
        return (String) appName;
    }

    public void createNotification(Context context, Bundle extras) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String appName = getAppName(context);

        Intent notificationIntent = new Intent(context, MsgHandlerActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra("_msg_bundle_", extras);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        int defaults = Notification.DEFAULT_ALL;

        if (extras.getString("defaults") != null) {
            try {
                defaults = Integer.parseInt(extras.getString("defaults"));
            }
            catch (NumberFormatException e) {
            }
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setDefaults(defaults)
                        .setSmallIcon(context.getApplicationInfo().icon)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle(extras.getString("title"))
                        .setTicker(extras.getString("title"))
                        .setContentIntent(contentIntent)
                        .setAutoCancel(true);

        String message = extras.getString("message");
        if (message != null) {
            mBuilder.setContentText(message);
        }
        else {
            mBuilder.setContentText("<missing message content>");
        }

        String msgcnt = extras.getString("msgcnt");
        if (msgcnt != null) {
            mBuilder.setNumber(Integer.parseInt(msgcnt));
        }

        int msgId = 0;

        try {
            msgId = Integer.parseInt(extras.getString("msgId"));
        }
        catch (NumberFormatException e) {
            Log.e(TAG, "Number format exception - Error parsing Notification ID: " + e.getMessage());
        }
        catch (Exception e) {
            Log.e(TAG, "Number format exception - Error parsing Notification ID" + e.getMessage());
        }

        mNotificationManager.notify(appName, msgId, mBuilder.build());
    }

}

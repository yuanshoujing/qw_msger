package com.qw.cordova.msg.xg;

import android.content.Context;

import com.qw.cordova.msg.Provider;
import com.tencent.android.tpush.XGPushManager;

/**
 * @author: 袁首京<yuanshoujing@gmail.com>
 */
public class XGProvider implements Provider {

    private static XGProvider instance;

    private XGProvider() {

    }

    public static XGProvider getInstance() {
        if (instance == null) {
            instance = new XGProvider();
        }

        return instance;
    }

    @Override
    public void register(Context context, String account) {
        XGPushManager.registerPush(context, account);
    }

    @Override
    public void unBindAccount(Context context) {
        XGPushManager.registerPush(context, "*");
    }

    @Override
    public void unRegister(Context context) {
        XGPushManager.unregisterPush(context);
    }
}

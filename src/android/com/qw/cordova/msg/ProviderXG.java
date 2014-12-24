package com.qw.cordova.msg;

import android.content.Context;

import com.tencent.android.tpush.XGPushManager;

/**
 * @author: 袁首京<yuanshoujing@gmail.com>
 */
public class ProviderXG implements Provider {

    private static ProviderXG instance;

    private ProviderXG() {

    }

    public static ProviderXG getInstance() {
        if (instance == null) {
            instance = new ProviderXG();
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

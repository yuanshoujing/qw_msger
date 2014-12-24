package com.qw.cordova.msg;

import android.content.Context;

/**
 * 消息提供者
 *
 * @author: 袁首京<yuanshoujing@gmail.com>
 */
public interface Provider {

    void register(Context context, String account);

    void unBindAccount(Context context);

    void unRegister(Context context);
}

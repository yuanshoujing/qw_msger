package com.qw.cordova.msg;

import com.qw.cordova.msg.xg.XGProvider;

/**
 * 消息提供者工厂
 *
 * @author: 袁首京<yuanshoujing@gmail.com>
 */
public abstract class ProviderFactory {

    public static final String VENDOR_XG = "xg";

    public static Provider getProvider(String vendor) {
        Provider result = null;

        if (VENDOR_XG.equals(vendor)) {
            return XGProvider.getInstance();
        }

        return result;
    }
}

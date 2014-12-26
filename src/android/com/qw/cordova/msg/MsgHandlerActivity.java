package com.qw.cordova.msg;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

/**
 * 消息处理
 *
 * @author: 袁首京<yuanshoujing@gmail.com>
 */
public class MsgHandlerActivity extends Activity {
    private static final String TAG = "MsgHandlerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean isMsgPluginActive = QwMsger.isActive();
        processBundle(isMsgPluginActive);

        this.finish();

        if (!isMsgPluginActive) {
            forceMainActivityReload();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        final NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private void processBundle(boolean isMsgPluginActive) {
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            Bundle originalExtras = extras.getBundle("_msg_bundle_");
            originalExtras.putBoolean("foreground", false);
            originalExtras.putBoolean("coldstart", !isMsgPluginActive);

            QwMsger.sendExtras(originalExtras);
        }
    }

    private void forceMainActivityReload() {
        PackageManager packageManager = getPackageManager();
        Intent launchIntent = packageManager.getLaunchIntentForPackage(getApplicationContext().getPackageName());
        startActivity(launchIntent);
    }
}

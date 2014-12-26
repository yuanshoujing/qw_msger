package com.qw.cordova.msg;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * 消息推送插件
 *
 * @author: 袁首京<yuanshoujing@gmail.com>
 */
public class QwMsger extends CordovaPlugin {
    public static final String TAG = "QwMsger";

    public static final String REGISTER = "register";
    public static final String UNREGISTER = "unRegister";
    public static final String UNBINDACCOUNT = "unBindAccount";
    public static final String SETEVENTCALLBACK = "setEventCallback";
    public static final String EXIT = "exit";

    private static CordovaWebView cordovaWebView;
    private static boolean isForeground;
    private static String account;
    private static Bundle cachedBundle;
    private static String vendor;

    private static Provider provider;
    private static CallbackContext savedCallbackContext;

    private Context getApplicationContext() {
        return this.cordova.getActivity().getApplicationContext();
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) {
        boolean result = false;

        Log.v(TAG, "==> 执行的 action：" + action);

        if (REGISTER.equals(action)) {
            cordovaWebView = this.webView;

            JSONObject jsonObject = null;
            try {
                jsonObject = args.getJSONObject(0);
                Log.v(TAG, "==> 传入的参数：" + jsonObject.toString());

                account = (String) jsonObject.get("account");
                Log.v(TAG, "==> 目标用户：" + account);

                vendor = (String) jsonObject.get("vendor");
                Log.v(TAG, "==> SDK 厂商：" + vendor);

                provider = ProviderFactory.getProvider(vendor);
                if (provider == null) {
                    result = false;
                    callbackContext.error("无效的 SDK 厂商：" + vendor);
                }
                else {
                    provider.register(getApplicationContext(), account);
                    result = true;
                    callbackContext.success();
                }
            }
            catch (JSONException e) {
                Log.e(TAG, "execute: Got JSON Exception " + e.getMessage());
                result = false;
                callbackContext.error(e.getMessage());
            }

            if (cachedBundle != null) {
                Log.v(TAG, "==> 发送缓存的数据");
                sendExtras(cachedBundle);
                cachedBundle = null;
            }
        }
        else if (UNBINDACCOUNT.equals(action)) {
            if (provider == null) {
                result = false;
                callbackContext.error("尚未正确注册");
            }
            else {
                provider.unBindAccount(getApplicationContext());
                result = true;
                callbackContext.success();
            }
        }
        else if (UNREGISTER.equals(action)) {
            if (provider == null) {
                result = false;
                callbackContext.error("尚未正确注册");
            }
            else {
                provider.unRegister(getApplicationContext());
                result = true;
                callbackContext.success();
            }
        }
        else if (SETEVENTCALLBACK.equals(action)) {
            if (provider == null) {
                result = false;
                callbackContext.error("尚未正确注册");
            }
            else {
                savedCallbackContext = callbackContext;
                result = true;
            }
        }
        else {
            result = false;
            Log.e(TAG, "==> 无效的 action : " + action);
            callbackContext.error("无效的 action : " + action);
        }

        return result;
    }

    /*
     * Sends a json object to the client as parameter to a method which is defined in gECB.
	 */
    public static void sendJavascript(JSONObject _json) {
        if (_json == null) {
            return;
        }
//        String _d = "javascript:" + pendingCallback + "(" + _json.toString() + ")";
        Log.v(TAG, "sendJavascript: " + _json.toString());

//        if (pendingCallback != null && cordovaWebView != null) {
//            cordovaWebView.sendJavascript(_d);
//        }
        sendMessage(_json);
    }

    public static void sendMessage(JSONObject jsonObject) {
        if (savedCallbackContext == null) {
            return;
        }
        
        PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, jsonObject);
        pluginResult.setKeepCallback(true);
        savedCallbackContext.sendPluginResult(pluginResult);
    }

    /*
     * Sends the pushbundle extras to the client application.
     * If the client application isn't currently active, it is cached for later processing.
     */
    public static void sendExtras(Bundle extras) {
        if (extras != null) {
            if (cordovaWebView != null) {
                sendJavascript(convertBundleToJson(extras));
            }
            else {
                Log.v(TAG, "sendExtras: caching extras to send at a later time.");
                cachedBundle = extras;
            }
        }
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        isForeground = true;
    }

    @Override
    public void onPause(boolean multitasking) {
        super.onPause(multitasking);
        isForeground = false;
        final NotificationManager notificationManager =
                (NotificationManager) cordova.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    public void onResume(boolean multitasking) {
        super.onResume(multitasking);
        isForeground = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isForeground = false;
        cordovaWebView = null;
        account = null;
        cachedBundle = null;
        vendor = null;
        provider = null;
        savedCallbackContext = null;
    }

    /*
     * serializes a bundle to JSON.
     */
    private static JSONObject convertBundleToJson(Bundle extras) {
        try {
            JSONObject json;
            json = new JSONObject().put("event", "message");

            JSONObject jsondata = new JSONObject();
            Iterator<String> it = extras.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Object value = extras.get(key);

                // System data from Android
                if (key.equals("from") || key.equals("collapse_key")) {
                    json.put(key, value);
                }
                else if (key.equals("foreground")) {
                    json.put(key, extras.getBoolean("foreground"));
                }
                else if (key.equals("coldstart")) {
                    json.put(key, extras.getBoolean("coldstart"));
                }
                else {
                    // Maintain backwards compatibility
                    if (key.equals("message") || key.equals("msgcnt") || key.equals("soundname")) {
                        json.put(key, value);
                    }

                    if (value instanceof String) {
                        // Try to figure out if the value is another JSON object

                        String strValue = (String) value;
                        if (strValue.startsWith("{")) {
                            try {
                                JSONObject json2 = new JSONObject(strValue);
                                jsondata.put(key, json2);
                            }
                            catch (Exception e) {
                                jsondata.put(key, value);
                            }
                            // Try to figure out if the value is another JSON array
                        }
                        else if (strValue.startsWith("[")) {
                            try {
                                JSONArray json2 = new JSONArray(strValue);
                                jsondata.put(key, json2);
                            }
                            catch (Exception e) {
                                jsondata.put(key, value);
                            }
                        }
                        else {
                            jsondata.put(key, value);
                        }
                    }
                }
            } // while
            json.put("payload", jsondata);

            Log.v(TAG, "extrasToJSON: " + json.toString());

            return json;
        }
        catch (JSONException e) {
            Log.e(TAG, "extrasToJSON: JSON exception");
        }
        return null;
    }

    public static boolean isInForeground() {
        return isForeground;
    }

    public static boolean isActive() {
        return cordovaWebView != null;
    }
}

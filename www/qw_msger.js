var channel = require('cordova/channel');
var exec = require('cordova/exec');
var cordova = require('cordova');

var emptyfunc = function (error) {
    console.log("==> qw_msger: " + error);
};

var qw_msger = function() {
};

/**
 * 注册
 */
qw_msger.prototype.register = function(vendor, onSuccess, onError, account) {
    if (onError == null) {
        onError = emptyfunc;
    }

    if (typeof onError != "function")  {
        console.log("qw_msger.register failure: onError parameter not a function");
        return ;
    }

    if (typeof onSuccess != "function") {
        console.log("qw_msger.register failure: onSuccess parameter not a function");
        return ;
    }

    var options = {
        "vendor": vendor,
        "account": account
    };

    channel.onCordovaReady.subscribe(function () {
        exec(onSuccess, onError, "qw_msger", "register", [options]);
    });
};

/**
 * 设置事件回调
 */
qw_msger.prototype.setEventCallback = function(callback) {
    if (typeof callback != "function") {
        console.log("qw_msger.setEventCallback failure: callback parameter not a function");
        return ;
    }

    channel.onCordovaReady.subscribe(function () {
        exec(callback, emptyfunc, "qw_msger", "setEventCallback", []);
    });
};



/**
 * 注销
 */
qw_msger.prototype.unRegister = function(onSuccess, onError) {
    if (onError == null) {
        onError = emptyfunc;
    }

    if (typeof onError != "function")  {
        console.log("qw_msger.unRegiste failure: onError parameter not a function");
        return ;
    }

    if (typeof onSuccess != "function") {
        console.log("qw_msger.unRegister failure: onSuccess parameter not a function");
        return ;
    }

    channel.onCordovaReady.subscribe(function () {
        exec(onSuccess, onError, "qw_msger", "unRegister", []);
    });
};

/**
 * 解除账户绑定
 */
qw_msger.prototype.unBindAccount = function(onSuccess, onError) {
    if (onError == null) {
        onError = emptyfunc;
    }

    if (typeof onError != "function")  {
        console.log("qw_msger.unBindAccount failure: onError parameter not a function");
        return ;
    }

    if (typeof onSuccess != "function") {
        console.log("qw_msger.unBindAccount failure: onSuccess parameter not a function");
        return ;
    }

    channel.onCordovaReady.subscribe(function () {
        exec(onSuccess, onError, "qw_msger", "unBindAccount", []);
    });
};

module.exports = new qw_msger();

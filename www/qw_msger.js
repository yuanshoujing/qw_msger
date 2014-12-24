var qw_msger = function() {
};

/**
 * 注册
 */
qw_msger.prototype.register = function(successCallback, errorCallback, options) {
    if (errorCallback == null) { errorCallback = function() {}}

    if (typeof errorCallback != "function")  {
        console.log("qw_msger.register failure: failure parameter not a function");
        return ;
    }

    if (typeof successCallback != "function") {
        console.log("qw_msger.register failure: success callback parameter must be a function");
        return ;
    }

    cordova.exec(successCallback, errorCallback, "Messenger", "register", [options]);
};

/**
 * 注销
 */
qw_msger.prototype.unRegister = function(successCallback, errorCallback, options) {
    if (errorCallback == null) { errorCallback = function() {}}

    if (typeof errorCallback != "function")  {
        console.log("qw_msger.unregister failure: failure parameter not a function");
        return ;
    }

    if (typeof successCallback != "function") {
        console.log("qw_msger.unregister failure: success callback parameter must be a function");
        return ;
    }

     cordova.exec(successCallback, errorCallback, "Messenger", "unregister", [options]);
};

/**
 * 解除账户绑定
 */
qw_msger.prototype.unBindAccount = function(successCallback, errorCallback, options) {
    if (errorCallback == null) { errorCallback = function() {}}

    if (typeof errorCallback != "function")  {
        console.log("qw_msger.unBindAccount failure: failure parameter not a function");
        return ;
    }

    if (typeof successCallback != "function") {
        console.log("qw_msger.unBindAccount failure: success callback parameter must be a function");
        return ;
    }

     cordova.exec(successCallback, errorCallback, "Messenger", "unBindAccount", [options]);
};

//-------------------------------------------------------------------

if(!window.plugins) {
    window.plugins = {};
}

if (!window.plugins.qw_msger) {
    window.plugins.qw_msger = new qw_msger();
}

if (typeof module != 'undefined' && module.exports) {
  module.exports = qw_msger;
}

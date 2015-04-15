var cordova = require("cordova");
var channel = require("cordova/channel");
var exec = require("cordova/exec");

var onError = function (e) {
    console.log(JSON.stringify(e));
};

var result = {

    register: function(accessId, accessKey, onSuccess) {
        if (typeof onSuccess != "function") {
            console.log("参数错误：onSuccess 不是函数");
            return;
        }

        exec(onSuccess, onError, "QwMsger", "register", [accessId, accessKey]);
    },

    unRegister: function(onSuccess) {
        if (typeof onSuccess != "function") {
            console.log("参数错误：onSuccess 不是函数");
            return;
        }

        exec(onSuccess, onError, "QwMsger", "unRegister", []);
    },

    bindAccount: function(account, onSuccess) {
        if (typeof onSuccess != "function") {
            console.log("参数错误：onSuccess 不是函数");
            return;
        }

        exec(onSuccess, onError, "QwMsger", "bindAccount", [account]);
    },

    unBindAccount: function(onSuccess) {
        if (typeof onSuccess != "function") {
            console.log("参数错误：onSuccess 不是函数");
            return;
        }

        exec(onSuccess, onError, "QwMsger", "unBindAccount", []);
    },

    setBadge: function(number, onSuccess) {
        if (typeof onSuccess != "function") {
            console.log("参数错误：onSuccess 不是函数");
            return;
        }

        exec(onSuccess, onError, "QwMsger", "setBadge", [number]);
    },

    onMessage: function(callback) {
        if (typeof callback != "function") {
            console.log("参数错误：callback 不是函数");
            return;
        }

        exec(callback, onError, "QwMsger", "onMessage", []);
    }

};

module.exports = result;

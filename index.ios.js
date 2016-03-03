/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 */
'use strict';

var { NativeModules, PushNotificationIOS } = require('react-native');
var XG = NativeModules.TencentXG;

function enableDebug(enable) {
  XG.enableDebug(enable);
}

function setCredential(accessId, accessKey) {
  XG.startAPP(accessId, accessKey);
}

function register(account, deviceToken) {
  XG.registerDevice(deviceToken);
  if (account) XG.setAccount('' + account);
  return Promise.resolve(deviceToken);
}

function sendLocalNotification(title, content, triggerTsInus) {
  if (triggerTsInus) {
    PushNotificationIOS.scheduleLocalNotification({
      alertBody: content,
      fireDate: triggerTsInus
    });
  } else {
    PushNotificationIOS.presentLocalNotification({
      alertBody: content,
    });
  }
}

function addEventListener(event, listener) {
  throw new Error('unimplemented');
}

function removeEventListener(event, listener) {
  throw new Error('unimplemented');
}

module.exports = {
  enableDebug,
  setCredential,
  register,
  sendLocalNotification,
  addEventListener,
  removeEventListener
};

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

function getDeviceToken() {
  return new Promise((resolve, reject) => {
    PushNotification.addEventListener('register', resolve);
  });
}

function setCredential(accessId, accessKey) {
  XG.setCredential(accessId, accessKey);
}

function register(account, ticket, ticketType, qua) {
}

function sendLocalNotification(title, content, triggerTsInus) {
  PushNotificationIOS.presentLocalNotification({
    alertBody: 'local notification'
  });
  // var date = new Date(triggerTsInus);
  // var dateString = '' + date.getFullYear() + (date.getMonth() + 1) + date.getDate();
  // var hourString = '' + date.getHours();
  // var minuteString = '' + date.getMinutes();
  // XG.addLocalNotification(title, content, dateString, hourString, minuteString);
}

function addEventListener(event, listener) {
  throw new Error('unimplemented');
}

function removeEventListener(event, listener) {
  throw new Error('unimplemented');
}

module.exports = {
  enableDebug,
  getDeviceToken,
  setCredential,
  register,
  sendLocalNotification,
  addEventListener,
  removeEventListener
};

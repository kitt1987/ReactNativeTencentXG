'use strict';

var {
  NativeModules,
  DeviceEventEmitter
} = require('react-native');
var XG = NativeModules.TencentXG;

function allEvents() {
  return [
    XG.LocalNotificationEvent,
    XG.RemoteNotificationEvent,
    XG.RegisteredEvent,
    XG.FailureEvent
  ];
}

function register(account, ticket, ticketType, qua) {
  if (ticket) return XG.registerPushWithTicket('' + account, ticket, ticketType, qua);
  if (account) return XG.registerPushAndBindAccount('' + account);
  return XG.registerPush();
}

function sendLocalNotification(title, content, triggerTsInus) {
  var date = new Date(triggerTsInus);
  var dateString = '' + date.getFullYear() + (date.getMonth() + 1) + date.getDate();
  var hourString = '' + date.getHours();
  var minuteString = '' + date.getMinutes();
  XG.addLocalNotification(title, content, dateString, hourString, minuteString);
}

function addEventListener(event, listener) {
  if (allEvents().indexOf(event) < 0) return;
  return DeviceEventEmitter.addListener(event, listener);
}

function nothing() {}

module.exports = {
  addEventListener,
  allEvents,
  register,
  disableIOS: nothing,
  enableDebug: enable => XG.enableDebug(enable || true),
  setCredential: (accessId, accessKey) => {
    return XG.setCredential(accessId, accessKey);
  },
  checkPermissions: () => XG.checkPermissions(),
  getApplicationIconBadgeNumber: () => {
    return XG.getApplicationIconBadgeNumber();
  },
  presentLocalNotification: obj => {
    return XG.presentLocalNotification(obj);
  },
  scheduleLocalNotification: obj => {
    return XG.scheduleLocalNotification(obj);
  },
  cancelLocalNotifications: () => XG.cancelLocalNotifications(),
  cancelAllLocalNotifications: () => {
    return XG.cancelAllLocalNotifications();
  },
  setTag: tag => XG.setTag(tag),
  delTag: tag => XG.delTag(tag),
  unregister: () => XG.unRegisterDevice(),
};

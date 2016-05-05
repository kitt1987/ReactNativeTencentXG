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

function scheduleLocalNotification(obj) {
  var date = new Date(obj.fireDate || Date.now());
  var dateString = '' + date.getFullYear() + (date.getMonth() + 1) +
    date.getDate();
  var hourString = '' + date.getHours();
  var minuteString = '' + date.getMinutes();
  XG.addLocalNotification(obj.title, obj.alertBody, dateString, hourString,
    minuteString);
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
  scheduleLocalNotification,
  disableIOS: nothing,
  enableDebug: enable => XG.enableDebug(enable || true),
  setCredential: (accessId, accessKey) => {
    return XG.setCredential(accessId, accessKey);
  },
  checkPermissions: () => Promise.resolve({
    alert: true,
    badge: false,
    sound: true
  }),
  getApplicationIconBadgeNumber: () => Promise.resolve(0),
  setApplicationIconBadgeNumber: nothing,
  cancelLocalNotifications: nothing,
  cancelAllLocalNotifications: nothing,
  setTag: tag => XG.setTag(tag),
  delTag: tag => XG.delTag(tag),
  unregister: () => XG.unregisterPush(),
};

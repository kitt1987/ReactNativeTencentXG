'use strict';

var {
  NativeModules,
  DeviceEventEmitter
} = require('react-native');
var XG = NativeModules.TencentXG;

function nothing() {}

function allEvents() {
  return [
    XG.LocalNotificationEvent,
    XG.RemoteNotificationEvent,
    XG.RegisteredEvent,
    XG.FailureEvent
  ];
}

function register(account, ticket, ticketType, qua) {
  if (typeof ticket !== 'string') ticket = null;
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
  console.log(dateString + ':' + hourString + ':' + minuteString);
  XG.addLocalNotification(obj.title, obj.alertBody, dateString, hourString,
    minuteString);
}

function addEventListener(event, listener) {
  if (allEvents().indexOf(event) < 0) return;
  if (event === XG.LocalNotificationEvent) {
    console.warn(XG.LocalNotificationEvent + ' is not supported on Android');
    return {
      remove: nothing
    };
  }

  return DeviceEventEmitter.addListener(event, listener);
}

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

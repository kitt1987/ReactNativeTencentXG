'use strict';

import {
  NativeModules,
  NativeAppEventEmitter
} from 'react-native';

var XG = NativeModules.TencentXG;

function allEvents() {
  return [
    XG.LocalNotificationEvent,
    XG.RemoteNotificationEvent,
    XG.RegisteredEvent,
    XG.FailureEvent
  ];
}

function addEventListener(event, listener) {
  if (allEvents().indexOf(event) < 0) return;
  return NativeAppEventEmitter.addListener(
    event, listener
  );
}

module.exports = {
  addEventListener,
  allEvents,
  enableDebug: enable => XG.enableDebug(enable || true),
  setCredential: (accessId, accessKey) => XG.setCredential(accessId, accessKey),
  register: (account, permissions) => XG.register(account, permissions),
  checkPermissions: () => XG.checkPermissions(),
  getApplicationIconBadgeNumber: () => XG.getApplicationIconBadgeNumber(),
  presentLocalNotification: obj => XG.presentLocalNotification(obj),
  scheduleLocalNotification: obj => XG.scheduleLocalNotification(obj),
  cancelLocalNotifications: () => XG.cancelLocalNotifications(),
  cancelAllLocalNotifications: () => XG.cancelAllLocalNotifications(),
  setTag: tag => XG.setTag(tag),
  delTag: tag => XG.delTag(tag),
  unregister: () => XG.unRegisterDevice(),
};

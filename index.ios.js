'use strict';

import {
  NativeModules,
  NativeAppEventEmitter
} from 'react-native';

var XG = NativeModules.TencentXG;

var disableIOS;

function allEvents() {
  return [
    XG.LocalNotificationEvent,
    XG.RemoteNotificationEvent,
    XG.RegisteredEvent,
    XG.FailureEvent
  ];
}

function addEventListener(event, listener) {
  if (disableIOS) return disableIOS;
  if (allEvents().indexOf(event) < 0) return;
  return NativeAppEventEmitter.addListener(
    event, listener
  );
}

module.exports = {
  addEventListener,
  allEvents,
  disableIOS: () => disableIOS = true,
  enableDebug: enable => disableIOS || XG.enableDebug(enable || true),
  setCredential: (accessId, accessKey) => {
    return disableIOS || XG.setCredential(accessId, accessKey);
  },
  register: (account, permissions) => {
    return disableIOS || XG.register(account, permissions);
  },
  checkPermissions: () => disableIOS || XG.checkPermissions(),
  getApplicationIconBadgeNumber: () => {
    return disableIOS || XG.getApplicationIconBadgeNumber();
  },
  presentLocalNotification: obj => {
    return disableIOS || XG.presentLocalNotification(obj);
  },
  scheduleLocalNotification: obj => {
    return disableIOS || XG.scheduleLocalNotification(obj);
  },
  cancelLocalNotifications: () => disableIOS || XG.cancelLocalNotifications(),
  cancelAllLocalNotifications: () => {
    return disableIOS || XG.cancelAllLocalNotifications();
  },
  setTag: tag => disableIOS || XG.setTag(tag),
  delTag: tag => disableIOS || XG.delTag(tag),
  unregister: () => disableIOS || XG.unRegisterDevice(),
};

'use strict';

import {
  NativeModules,
  NativeAppEventEmitter
} from 'react-native';

var XG = NativeModules.TencentXG;
var disableIOS;

function allEvents() {
  return [
    XG.RemoteNotificationEvent,
    XG.RegisteredEvent,
    XG.FailureEvent
  ];
}

function eventHandle(event, listener, dataBack) {
  var data = dataBack;
  if (event === XG.RemoteNotificationEvent) {
    if (dataBack.aps) {
      data = {};
      data.alertBody = dataBack.aps.alert;
      data.badge = dataBack.aps.badge;
      Object.keys(dataBack).filter(k => k !== 'aps')
        .forEach(k => data[k] = dataBack[k]);
    }
  }

  listener(data);
}

function addEventListener(event, listener) {
  if (disableIOS) return disableIOS;
  if (allEvents().indexOf(event) < 0) return;
  return NativeAppEventEmitter.addListener(
    event, eventHandle.bind(null, event, listener)
  );
}

function scheduleLocalNotification(obj) {
  if (disableIOS) return;
  if (obj.fireDate) {
    XG.scheduleLocalNotification(obj);
  } else {
    XG.presentLocalNotification(obj);
  }
}

module.exports = {
  addEventListener,
  allEvents,
  scheduleLocalNotification,
  disableIOS: disabled => disableIOS = (disabled === undefined ? true : disabled),
  enableDebug: enable => {
    return disableIOS || XG.enableDebug(enable === undefined ? true : enable);
  },
  setCredential: (accessId, accessKey) => {
    return disableIOS || XG.setCredential(accessId, accessKey);
  },
  register: (account, permissions) => {
    return disableIOS || XG.register(account, permissions);
  },
  checkPermissions: () => {
    if (disableIOS) return Promise.resolve(disableIOS);
    return XG.checkPermissions();
  },
  getApplicationIconBadgeNumber: () => {
    if (disableIOS) return Promise.resolve();
    return XG.getApplicationIconBadgeNumber();
  },
  setApplicationIconBadgeNumber: number => {
    if (disableIOS) return;
    return XG.setApplicationIconBadgeNumber(number);
  },
  cancelLocalNotifications: userInfo => {
    return disableIOS || XG.cancelLocalNotifications(userInfo);
  },
  cancelAllLocalNotifications: () => {
    return disableIOS || XG.cancelAllLocalNotifications();
  },
  setTag: tag => disableIOS || XG.setTag(tag),
  delTag: tag => disableIOS || XG.delTag(tag),
  unregister: () => disableIOS || XG.unRegisterDevice(),
};

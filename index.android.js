/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 */
'use strict';

var { NativeModules } = require('react-native');
var XG = NativeModules.TencentXG;

function enableDebug(enable) {
  XG.enableDebug(enable);
}

function setCredential(accessId, accessKey) {
  XG.setCredential(accessId, accessKey);
}

function register(account, ticket, ticketType, qua) {
  if (ticket) return XG.registerPushWithTicketThen(account, ticket, ticketType, qua);
  if (account) return XG.registerPushAndBindAccountThen(account);
  return XG.registerPushThen();
}

function sendLocalNotification(title, content, triggerTsInus) {
  var date = new Date(triggerTsInus);
  var dateString = '' + date.getFullYear() + (date.getMonth() + 1) + date.getDate();
  var hourString = '' + date.getHours();
  var minuteString = '' + date.getMinutes();
  XG.addLocalNotification(title, content, dateString, hourString, minuteString);
}

function addEventListener(event, listener) {

}

function removeEventListener(event, listener) {

}

module.exports = {
  enableDebug,
  setCredential,
  register,
  sendLocalNotification,
  addEventListener,
  removeEventListener
};

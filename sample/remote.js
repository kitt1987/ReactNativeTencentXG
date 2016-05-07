'use strict';

const querystring = require('query-string');
import {
  Platform
} from 'react-native';
const md5 = require('./md5');

function calcSignature(method, url, params, secretKey) {
  var matched = url.match(/https?:\/\/([^\/]+)(.*)/);
  if (!matched) throw new Error('url mismatched');
  var temp = method + matched[1] + matched[2] +
    Object.keys(params).sort().reduce((p, c) => {
      return p + c + '=' + params[c];
    }, '') + secretKey;

  return md5(temp);
}

function buildGeneralParams(devToken) {
  var params = {
    access_id: Platform.OS === 'ios' ? 2200197326 : 2100182505,
    timestamp: Math.ceil(Date.now() / 1000),
  };

  if (devToken) params.device_token = devToken;

  return params;
}

function formatTs(ts) {
  var date = new Date(ts);
  return '' + date.getFullYear() + '-' + (date.getMonth() + 1) +
    '-' + date.getDate() + ' ' + date.getHours() + ':' + date.getMinutes() +
    ':' + date.getSeconds();
}

function buildMessage(params, title, content) {
  if (Platform.OS === 'ios') {
    params.environment = 2;
    params.message_type = 0;
    params.message = JSON.stringify({
      aps: {
        alert: content
      }
    });
  } else {
    params.message_type = 1;
    params.message = JSON.stringify({
      content,
      title,
      vibrate: 1,
    });
  }

  params.send_time = formatTs(Date.now() + 5000);
  return params;
}

function getSecretKey() {
  return Platform.OS === 'ios' ? '66d2e142bcccebad0aa6e1d6c6b227b8' :
    'a28042d4ebdc2019924f7748461d7dd5';
}

function push(devToken, content, title) {
  if (!devToken) return;
  var params = buildMessage(buildGeneralParams(devToken), title, content);
  params.sign = calcSignature(
    'POST',
    'http://openapi.xg.qq.com/v2/push/single_device',
    params, getSecretKey());
  fetch(
      'http://openapi.xg.qq.com/v2/push/single_device?params', {
        method: 'POST',
        headers: {
          "Content-type": "application/x-www-form-urlencoded"
        },
        body: querystring.stringify(params)
      }
    )
    .then(res => console.log(res.text()))
    .catch(err => console.log(err));
}

function broadcast(content, title) {
  var params = buildMessage(buildGeneralParams(), title, content);
  params.sign = calcSignature(
    'POST',
    'http://openapi.xg.qq.com/v2/push/all_device',
    params, getSecretKey());
  fetch(
      'http://openapi.xg.qq.com/v2/push/all_device?params', {
        method: 'POST',
        headers: {
          "Content-type": "application/x-www-form-urlencoded"
        },
        body: querystring.stringify(params)
      }
    )
    .then(res => console.log(res.text()))
    .catch(err => console.log(err));
}

exports = module.exports = {
  push,
  broadcast
};

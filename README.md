# ReactNativeTencentXG

React Native component for Tencent message push service [XinGe(腾讯信鸽)](http://xg.qq.com/)

# SDK version
* Android v2.45_20160510_1845
* iOS 2.4.6

# Installation

npm install --save react-native-tencent-xg

# Linking to your project

## Android

1.Follow the [official tutorial](http://developer.qq.com/wiki/xg/Android%E6%8E%A5%E5%85%A5/Android%20SDK%E5%BF%AB%E9%80%9F%E6%8E%A5%E5%85%A5/Android%20SDK%E5%BF%AB%E9%80%9F%E6%8E%A5%E5%85%A5.html) to modify Androidmanifest.xml file.

2.If you want to receive notification content, replace **XGPushReceiver** registration in Androidmanifest.xml with **com.kh.tencentxg.XGMessageReceiver**.
```
<!-- Remove the official Receiver!!! -->
<!--<receiver-->
  <!--android:name="com.tencent.android.tpush.XGPushReceiver"-->
  <!--android:process=":xg_service_v2" >-->
  <!--<intent-filter android:priority="0x7fffffff" >-->
      <!--&lt;!&ndash; 【必须】 信鸽SDK的内部广播 &ndash;&gt;-->
      <!--<action android:name="com.tencent.android.tpush.action.SDK" />-->
      <!--<action android:name="com.tencent.android.tpush.action.INTERNAL_PUSH_MESSAGE" />-->
      <!--&lt;!&ndash; 【必须】 系统广播：开屏和网络切换 &ndash;&gt;-->
      <!--<action android:name="android.intent.action.USER_PRESENT" />-->
      <!--<action android:name="android.net.conn.CONNECTIVITY_CHANGE" />-->

      <!--&lt;!&ndash; 【可选】 一些常用的系统广播，增强信鸽service的复活机会，请根据需要选择。当然，你也可以添加APP自定义的一些广播让启动service &ndash;&gt;-->
      <!--<action android:name="android.bluetooth.adapter.action.STATE_CHANGED" />-->
      <!--<action android:name="android.intent.action.ACTION_POWER_CONNECTED" />-->
      <!--<action android:name="android.intent.action.ACTION_POWER_DISCONNECTED" />-->
  <!--</intent-filter>-->
<!--</receiver>-->
<!-- Register the receiver !!! -->
<receiver android:name="com.kh.tencentxg.XGMessageReceiver">
    <intent-filter>
        <action android:name="com.tencent.android.tpush.action.PUSH_MESSAGE" />
        <action android:name="com.tencent.android.tpush.action.FEEDBACK" />
    </intent-filter>
</receiver>
```

3.Append dependency to build.gradle of Module:app
```
dependencies {
    compile fileTree(dir: "libs", include: ["*.jar"])
    compile "com.android.support:appcompat-v7:23.0.1"
    compile "com.facebook.react:react-native:+"  // From node_modules
    compile project(':react-native-tencent-xg')  <---- Here
}
```
4.Append build settings to settings.gradle
```
// Append following lines to seetings.gradle
include ':react-native-tencent-xg'
project(':react-native-tencent-xg').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-tencent-xg/android')
```
5.Create package dependency in MainActivity.java
```
import com.kh.tencentxg.TencentXGPackage;     <---- Here
public class MainActivity extends ReactActivity {
  //...
  @Override
    protected List<ReactPackage> getPackages() {
        return Arrays.<ReactPackage>asList(
            new MainReactPackage(),
            new TencentXGPackage()            <---- And Here
        );
    }
  //...
}
```

## iOS

1.Link ReactNativeTencentXG to your project. Just follow the [official tutorial](http://facebook.github.io/react-native/docs/linking-libraries-ios.html)

2.Append more Libraries to **Build Phases->Link Binary With Libraries**. Which are:
* libz.tbd
* CoreTelephony.framework
* libsqlit3.tbd

3.Append the following path to **Build Settings->Search Paths->Headers Search Paths**.
```
$(SRCROOT)/../node_modules/react-native-tencent-xg/ios
```

4.Modify **AppDelegate.m** to receive events.Just like the official PushNotificationIOS.
```
#import "AppDelegate.h"
#import "RCTRootView.h"

#import "ReactNativeTencentXG.h"   <--- Here

@implementation AppDelegate

<--- Append the following private methods

- (void)application:(UIApplication *)application didRegisterUserNotificationSettings:(UIUserNotificationSettings *)notificationSettings
{
  [TencentXG didRegisterUserNotificationSettings:notificationSettings];
}

- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
  [TencentXG didRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
}

- (void)application:(UIApplication *)app didFailToRegisterForRemoteNotificationsWithError:(NSError *)err
{
  [TencentXG didFailToRegisterForRemoteNotificationsWithError:err];
}

- (void)application:(UIApplication*)application didReceiveRemoteNotification:(NSDictionary*)userInfo
{
  [TencentXG didReceiveRemoteNotification:userInfo];
}

-(void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification
{
  [TencentXG didReceiveLocalNotification:notification];
}

<===The End

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{

```

# Notification Structure
You will use or get this sort of notification when a remote or local notification received and send a local notification.
```
{
  alertBody: 'The content you sent',
  title: 'title on Android',
  badget: 5,
  userInfo: {
    customThing: 'something custom'
  },
  xgCustomKey: 'xgCustomValue',
  fireDate: set when you send local notification. Such as Date.now() + 5000
}
```

# Event
There are 3 sort of events TXG supported.

Event|Argument|When to file
-----|--------|------------
'notification'|notification|on remote or local notification receive
'register'|device token|on register succeed
'error'|error|on any errors occur

# API

* Import the component;
```
import * as XG from 'react-native-tencent-xg';
```

* `XG.allEvents()`

  Get all event names XG supported.

* `XG.disableIOS()`

  Disable all feature on iOS if you want to use official PushNotificationIOS on iOS. Ignored on Android.

* `XG.enableDebug()`

  Enable debug outputs of XinGe SDK.

* `XG.setCredential(accessId, accessKey)`

  Set your accessId(number) and accessKey.You can also set them in Androidmanifest.xml on Android.
```
XG.setCredential(accessId, accessKey);
```

* `XG.addEventListener(event, listener)`

  Handle event to get notification or result of registration.
```
var errorHolder = XG.addEventListener('error', err => {
  // Handle error
});
if (!errorHolder) throw new Error('Fail to register listener of error');
errorHolder.remove();
```

* `XG.checkPermissions().then(permission => {})`

  Just like official PushNotificationIOS.checkPermissions(). It returns a Promise which resolves permissions your app owned. Permissions always are `{alert:true, badge: false, sound: true}` on Android.

* `XG.register(account, ticketOrPermission, ticketType, qua)`

  Register to XG server. Each of arguments could be null. The 2nd arguments is ticket on Android and permissions on iOS. The permission definition is just like which official **PushNotificationIOS** defines. Permission is ignored on Android and ticket is ignore and iOS, so the following code could run on both Android and iOS. You will get event `notification` or `event` fired after register done.
```
// The 2nd, 3rd and 4th args are ignored on iOS.
XG.register(account, ticket, ticketType, qua);
// The 2nd args are ignored on Android.
var permissions = {alert: true, badge: true, sound: true};
XG.register(account, permissions);
```

* `XG.scheduleLocalNotification(notification)`

  Send local notification.
```
var fireDate = Date.now() + 60000;
XG.scheduleLocalNotification({
  fireDate,
  alertBody: 'content of ' + fireDate,
  userInfo: this.state.localUserInfo
});
```

* `XG.cancelLocalNotifications(userInfo)`

  Cancel a local notification you've send. The only argument is userInfo you saved in that notification.
```
XG.cancelLocalNotifications(this.state.localUserInfo);
```

* `XG.cancelAllLocalNotifications()`

  Cancel all local notifications.

* `XG.getApplicationIconBadgeNumber().then(badgeNum => {})`

  Get badge number. It returns a promise which resolve the number. Ignored on Android.

* `XG.setApplicationIconBadgeNumber(badgeNum)`

  Set badge number. Ignored on Android.

* `XG.setTag(tag)`

* `XG.delTag(tag)`

* `XG.unregisterPush()`

# Sample
You may found more details in [Sample](https://github.com/kitt1987/ReactNativeTencentXG/tree/master/sample).

# TODO
- [ ] update to the newest SDK
- [ ] a cooler sample

# ReactNativeTencentXG

React Native component for Tencent message push service XinGe(腾讯信鸽) http://xg.qq.com/

# SDK version
* Android v2.42_20160111_1539
* iOS 2.4.6

# Installation

npm install --save react-native-tencent-xg

# Linking to your project

## Android

1. Follow the [official tutorial](http://developer.qq.com/wiki/xg/Android%E6%8E%A5%E5%85%A5/Android%20SDK%E5%BF%AB%E9%80%9F%E6%8E%A5%E5%85%A5/Android%20SDK%E5%BF%AB%E9%80%9F%E6%8E%A5%E5%85%A5.html) to modify Androidmanifest.xml file.
2. Append dependency to build.gradle of Module:app
```
dependencies {
    compile fileTree(dir: "libs", include: ["*.jar"])
    compile "com.android.support:appcompat-v7:23.0.1"
    compile "com.facebook.react:react-native:+"  // From node_modules
    compile project(':react-native-tencent-xg')  <---- Here
}
```
3. Append build settings to settings.gradle
```
// Append following lines to seetings.gradle
include ':react-native-tencent-xg'
project(':react-native-tencent-xg').projectDir = new File(rootProject.projectDir, '../node_modules/react-native-tencent-xg/android')
```
4. Create package dependency in MainActivity.java
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
5. See more details in [Sample](https://github.com/kitt1987/ReactNativeTencentXG/tree/master/sample)

## iOS

**Uncompleted**

# API

1. Set your accessId(number) and accessKey.You can also set them in Androidmanifest.xml on Android. This is a synchronous call.
```
XG.setCredential(accessId, accessKey);
```
2. Register to XG server. Each of arguments could be null and a promise is returned.
```
XG.register(account, ticket, ticketType, qua)
.then(devToken => {})
.catch(err => {});
```
3. Send local notification
```
XG.sendLocalNotification('title', 'content', Date.now() + 5000);
```

# TODO
- [ ] For iOS
- [ ] Push Receiver for Android
- [x] Complete API documentation
- [x] Sample project

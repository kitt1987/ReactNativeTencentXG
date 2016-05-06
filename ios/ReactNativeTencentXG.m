//
//  ReactNativeTencentXG.m
//  ReactNativeTencentXG
//
//  Created by Kitt Hsu on 2/26/16.
//  Copyright © 2016 Kitt Hsu. All rights reserved.
//

#import "ReactNativeTencentXG.h"
#import "XGPush.h"
#import "XGSetting.h"
#import "RCTEventDispatcher.h"
#import "RCTUtils.h"
#import "RCTConvert.h"

#if __IPHONE_OS_VERSION_MIN_REQUIRED < __IPHONE_8_0

#define UIUserNotificationTypeAlert UIRemoteNotificationTypeAlert
#define UIUserNotificationTypeBadge UIRemoteNotificationTypeBadge
#define UIUserNotificationTypeSound UIRemoteNotificationTypeSound
#define UIUserNotificationTypeNone  UIRemoteNotificationTypeNone
#define UIUserNotificationType      UIRemoteNotificationType

#endif

NSString *const RCTLocalNotificationReceived = @"LocalNotificationReceived";
NSString *const RCTRemoteNotificationReceived = @"RemoteNotificationReceived";
NSString *const RCTRemoteNotificationsRegistered = @"RemoteNotificationsRegistered";
NSString *const RCTFailToRegisterRemoteNotification = @"FailToRegisterRemoteNotification";

NSString *const RCTLocalNotificationEvent = @"localNotification";
NSString *const RCTRemoteNotificationEvent = @"notification";
NSString *const RCTRegisteredEvent = @"register";
NSString *const RCTFailureEvent = @"error";

@implementation RCTConvert (UILocalNotification)

+ (UILocalNotification *)UILocalNotification:(id)json
{
    NSDictionary<NSString *, id> *details = [self NSDictionary:json];
    UILocalNotification *notification = [UILocalNotification new];
    notification.fireDate = [RCTConvert NSDate:details[@"fireDate"]] ?: [NSDate date];
    notification.alertBody = [RCTConvert NSString:details[@"alertBody"]];
    notification.alertAction = [RCTConvert NSString:details[@"alertAction"]];
    notification.soundName = [RCTConvert NSString:details[@"soundName"]] ?: UILocalNotificationDefaultSoundName;
    notification.userInfo = [RCTConvert NSDictionary:details[@"userInfo"]];
//    notification.category = [RCTConvert NSString:details[@"category"]];
    if (details[@"applicationIconBadgeNumber"]) {
        notification.applicationIconBadgeNumber = [RCTConvert NSInteger:details[@"applicationIconBadgeNumber"]];
    }
    return notification;
}

@end

@implementation TencentXG

RCT_EXPORT_MODULE();
@synthesize bridge = _bridge;

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)setBridge:(RCTBridge *)bridge
{
    _bridge = bridge;

    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(handleLocalNotificationReceived:)
                                                 name:RCTLocalNotificationReceived
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(handleRemoteNotificationReceived:)
                                                 name:RCTRemoteNotificationReceived
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(handleRemoteNotificationsRegistered:)
                                                 name:RCTRemoteNotificationsRegistered
                                               object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(handleFailToRegisterRemoteNotifications:)
                                                 name:RCTFailToRegisterRemoteNotification
                                               object:nil];
}

- (NSDictionary *)constantsToExport
{
    return @{
             @"LocalNotificationEvent": RCTLocalNotificationEvent,
             @"RemoteNotificationEvent": RCTRemoteNotificationEvent,
             @"RegisteredEvent": RCTRegisteredEvent,
             @"FailureEvent": RCTFailureEvent
            };
}


RCT_EXPORT_METHOD(enableDebug:(BOOL)enable)
{
    XGSetting *setting = [XGSetting getInstance];
    [setting enableDebug:enable];
}

RCT_EXPORT_METHOD(setCredential:(NSInteger)appId appKey:(NSString *)appKey)
{
    NSLog(@"[XGPush] App ID is %u", (uint32_t)appId);
    [XGPush startApp:(uint32_t)appId appKey:appKey];
}

RCT_EXPORT_METHOD(presentLocalNotification:(UILocalNotification *)notification)
{
    [RCTSharedApplication() presentLocalNotificationNow:notification];
}

RCT_EXPORT_METHOD(scheduleLocalNotification:(UILocalNotification *)notification)
{
    NSLog(@"[XGPush] scheduleLocalNotification");
    [RCTSharedApplication() scheduleLocalNotification:notification];
}

RCT_EXPORT_METHOD(cancelAllLocalNotifications)
{
    [RCTSharedApplication() cancelAllLocalNotifications];
}

RCT_EXPORT_METHOD(cancelLocalNotifications:(NSDictionary *)userInfo)
{
    for (UILocalNotification *notification in [UIApplication sharedApplication].scheduledLocalNotifications) {
        __block BOOL matchesAll = YES;
        NSDictionary *notificationInfo = notification.userInfo;
        [userInfo enumerateKeysAndObjectsUsingBlock:^(NSString *key, id obj, BOOL *stop) {
            if (![notificationInfo[key] isEqual:obj]) {
                matchesAll = NO;
                *stop = YES;
            }
        }];
        if (matchesAll) {
            [[UIApplication sharedApplication] cancelLocalNotification:notification];
        }
    }
}

RCT_EXPORT_METHOD(register:(NSString *)account permissions:(NSDictionary *)permissions)
{
    if (RCTRunningInAppExtension()) {
        return;
    }

    void (^successCallback)(void) = ^(void){
        if(![XGPush isUnRegisterStatus])
        {
            UIUserNotificationType types = UIUserNotificationTypeNone;
            if (permissions) {
                if ([RCTConvert BOOL:permissions[@"alert"]]) {
                    types |= UIUserNotificationTypeAlert;
                }
                if ([RCTConvert BOOL:permissions[@"badge"]]) {
                    types |= UIUserNotificationTypeBadge;
                }
                if ([RCTConvert BOOL:permissions[@"sound"]]) {
                    types |= UIUserNotificationTypeSound;
                }
            } else {
                types = UIUserNotificationTypeAlert | UIUserNotificationTypeBadge | UIUserNotificationTypeSound;
            }

            UIApplication *app = RCTSharedApplication();
            if ([app respondsToSelector:@selector(registerUserNotificationSettings:)]) {
                UIUserNotificationSettings *notificationSettings =
                [UIUserNotificationSettings settingsForTypes:(NSUInteger)types categories:nil];
                [app registerUserNotificationSettings:notificationSettings];
            } else {
                [app registerForRemoteNotificationTypes:(NSUInteger)types];
            }
        }
    };

    if (account) self.account = account;

    [XGPush initForReregister: successCallback];
}

RCT_REMAP_METHOD(checkPermissions, resolver:(RCTPromiseResolveBlock)resolve
                 rejecter:(RCTPromiseRejectBlock)reject)
{
    if (RCTRunningInAppExtension()) {
        resolve(@[@{@"alert": @NO, @"badge": @NO, @"sound": @NO}]);
        return;
    }

    NSUInteger types = 0;
    if ([UIApplication instancesRespondToSelector:@selector(currentUserNotificationSettings)]) {
        types = [RCTSharedApplication() currentUserNotificationSettings].types;
    } else {

#if __IPHONE_OS_VERSION_MIN_REQUIRED < __IPHONE_8_0

        types = [RCTSharedApplication() enabledRemoteNotificationTypes];

#endif

    }

    resolve(@[@{
                   @"alert": @((types & UIUserNotificationTypeAlert) > 0),
                   @"badge": @((types & UIUserNotificationTypeBadge) > 0),
                   @"sound": @((types & UIUserNotificationTypeSound) > 0),
                   }]);
}

RCT_EXPORT_METHOD(setApplicationIconBadgeNumber:(NSInteger)number)
{
    RCTSharedApplication().applicationIconBadgeNumber = number;
}

RCT_REMAP_METHOD(getApplicationIconBadgeNumber, resolver2:(RCTPromiseResolveBlock)resolve
                 rejecter2:(RCTPromiseRejectBlock)reject)
{
    resolve(@[@(RCTSharedApplication().applicationIconBadgeNumber)]);
}

RCT_EXPORT_METHOD(setTag:(NSString *)tag)
{
    [XGPush setTag: tag];
}

RCT_EXPORT_METHOD(delTag:(NSString *)tag)
{
    [XGPush delTag: tag];
}

RCT_EXPORT_METHOD(unRegisterDevice)
{
    [XGPush unRegisterDevice];
}

+ (void)didRegisterUserNotificationSettings:(__unused UIUserNotificationSettings *)notificationSettings
{
    if ([UIApplication instancesRespondToSelector:@selector(registerForRemoteNotifications)]) {
        [[UIApplication sharedApplication] registerForRemoteNotifications];
    }
}

+ (void)didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
    [[NSNotificationCenter defaultCenter] postNotificationName:RCTRemoteNotificationsRegistered
                                                        object:self
                                                      userInfo:@{@"deviceToken" : deviceToken}];
}

- (void)handleRemoteNotificationsRegistered:(NSNotification *)notification
{
    NSData* deviceToken = notification.userInfo[@"deviceToken"];
    NSMutableString *hexString = [NSMutableString string];
    NSUInteger deviceTokenLength = deviceToken.length;
    const unsigned char *bytes = deviceToken.bytes;
    for (NSUInteger i = 0; i < deviceTokenLength; i++) {
        [hexString appendFormat:@"%02x", bytes[i]];
    }

    void (^successBlock)(void) = ^(void){
        NSLog(@"[XGPush]register succeed");
        [self.bridge.eventDispatcher sendAppEventWithName:RCTRegisteredEvent
                                                     body:[hexString copy]];
    };

    void (^errorBlock)(void) = ^(void){
        NSLog(@"[XGPush]register failure");
        [self.bridge.eventDispatcher sendAppEventWithName:RCTFailureEvent
                                                     body:@"Fail to register to xinge"];
    };

    if (self.account) [XGPush setAccount:self.account];

    NSLog(@"[XGPush]Raw device token:%@", notification.userInfo[@"deviceToken"]);

    NSString * deviceTokenStr = [XGPush registerDevice:deviceToken successCallback:successBlock errorCallback:errorBlock];

    NSLog(@"[XGPush] deviceTokenStr is %@", deviceTokenStr);

}

+ (void)didFailToRegisterForRemoteNotificationsWithError:(NSError *)err
{
    NSMutableDictionary *details = [NSMutableDictionary new];
    if (err.userInfo) {
        details[@"error"] = RCTJSONClean(err.userInfo);
    } else if (err.domain) {
        details[@"error"] = err.domain;
    }

    [[NSNotificationCenter defaultCenter] postNotificationName:RCTFailToRegisterRemoteNotification
                                                        object:self
                                                      userInfo:details];
}

- (void)handleFailToRegisterRemoteNotifications:(NSNotification *)notification
{
    [self.bridge.eventDispatcher sendAppEventWithName:RCTFailureEvent
                                                 body:notification.userInfo];
}

+ (void)didReceiveRemoteNotification:(NSDictionary*)notification
{
    [[NSNotificationCenter defaultCenter] postNotificationName:RCTRemoteNotificationReceived
                                                        object:self
                                                      userInfo:notification];
}

- (void)handleRemoteNotificationReceived:(NSNotification *)notification
{
    [self.bridge.eventDispatcher sendAppEventWithName:RCTRemoteNotificationEvent
                                                 body:notification.userInfo];
}

+ (void)didReceiveLocalNotification:(UILocalNotification *)notification
{
    NSMutableDictionary *details = [NSMutableDictionary new];
    if (notification.alertBody) {
        details[@"alertBody"] = notification.alertBody;
    }

    if (notification.userInfo) {
        details[@"userInfo"] = RCTJSONClean(notification.userInfo);
    }

    [[NSNotificationCenter defaultCenter] postNotificationName:RCTRemoteNotificationReceived
                                                        object:self
                                                      userInfo:details];
}

- (void)handleLocalNotificationReceived:(NSNotification *)notification
{
    [self.bridge.eventDispatcher sendAppEventWithName:RCTLocalNotificationEvent
                                                 body:notification.userInfo];
}

@end

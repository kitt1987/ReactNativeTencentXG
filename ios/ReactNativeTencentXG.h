//
//  ReactNativeTencentXG.h
//  ReactNativeTencentXG
//
//  Created by Kitt Hsu on 2/26/16.
//  Copyright © 2016 Kitt Hsu. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "RCTBridgeModule.h"

@interface TencentXG : NSObject <RCTBridgeModule>

+ (void)didRegisterUserNotificationSettings:(UIUserNotificationSettings *)notificationSettings;
+ (void)didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken;
+ (void)didFailToRegisterForRemoteNotificationsWithError:(NSError *)error;
+ (void)didReceiveRemoteNotification:(NSDictionary *)notification;
+ (void)didReceiveLocalNotification:(UILocalNotification *)notification;

@property NSString* account;

@end

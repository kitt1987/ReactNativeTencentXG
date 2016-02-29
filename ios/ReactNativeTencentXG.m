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

@implementation TencentXG

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(enableDebug:(BOOL)enable)
{
    XGSetting *setting = [XGSetting getInstance];
    [setting enableDebug:enable];
}

RCT_EXPORT_METHOD(registerDevice:(NSString *)deviceToken)
{
    [XGPush registerDeviceStr: deviceToken];
}

RCT_EXPORT_METHOD(startAPP:(uint32_t)appId appKey:(NSString *)appKey)
{
    [XGPush startApp:appId appKey:appKey];
}

RCT_EXPORT_METHOD(setAccount:(NSString *)account)
{
    [XGPush setAccount:account];
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

@end

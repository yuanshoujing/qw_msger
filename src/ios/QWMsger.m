//
//  QWMsger.m
//  InvoiceIs
//
//  Created by rocker on 15/4/15.
//
//

#import "QWMsger.h"

@implementation QWMsger

- (void)register:(CDVInvokedUrlCommand *)command {
    NSLog(@"==> register");
    
    CDVPluginResult *result = nil;
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onCDVRemoteNotification:) name:CDVRemoteNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onXGReceiveRemoteNotification:) name:@"XGReceiveRemoteNotification" object:nil];
    
    NSNumber *accessId = [command argumentAtIndex:0];
    NSString *accessKey = [command argumentAtIndex:1];
    
    [XGPush startApp:[accessId intValue] appKey:accessKey];
    
    void (^successCallback)(void) = ^(void) {
        if (![XGPush isUnRegisterStatus]) {
#if __IPHONE_OS_VERSION_MAX_ALLOWED >= _IPHONE80_
            float sysVer = [[[UIDevice currentDevice] systemVersion] floatValue];
            if (sysVer < 8) {
                [self registerPush];
            }
            else {
                [self registerPushForIOS8];
            }
#else
            [self registerPush];
#endif
        }
    };
    [XGPush initForReregister:successCallback];
    
    void (^successBlock)(void) = ^(void) {
        NSLog(@"[XGPUSH] handleLaunching's success block");
    };
    
    void (^errorBlock)(void) = ^(void) {
        NSLog(@"[XGPUSH] handleLaunching's error block");
    };
    
    [XGPush handleLaunching:nil successCallback:successBlock errorCallback:errorBlock];
    
    // 角标清零
    [[UIApplication sharedApplication] setApplicationIconBadgeNumber:0];
    
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"ok"];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
    
}

- (void)unRegister:(CDVInvokedUrlCommand *)command {
    NSLog(@"==> unRegister");
    
    CDVPluginResult *result = nil;
    self.callbackId = nil;
    
    [[UIApplication sharedApplication] unregisterForRemoteNotifications];
    [XGPush unRegisterDevice];
    
    result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"ok"];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)bindAccount:(CDVInvokedUrlCommand *)command {
    NSLog(@"==> bindAccount");
    
    self.account = [command argumentAtIndex:0];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"ok"];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)unBindAccount:(CDVInvokedUrlCommand *)command {
    NSLog(@"==> unBindAccount");
    
    [XGPush setAccount:@"*"];
    [XGPush registerDeviceStr:self.deviceToken];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"ok"];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)setBadge:(CDVInvokedUrlCommand *)command {
    NSLog(@"==> setBadge");
    
    NSNumber *number = [command argumentAtIndex:0];
    [[UIApplication sharedApplication] setApplicationIconBadgeNumber:[number intValue]];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"ok"];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

- (void)onMessage:(CDVInvokedUrlCommand *)command {
    NSLog(@"==> onMessage");
    
    self.callbackId = command.callbackId;
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"ok"];
    [result setKeepCallbackAsBool:true];
    [self.commandDelegate sendPluginResult:result callbackId:command.callbackId];
}

// 注册成功回调
- (void)onCDVRemoteNotification:(NSNotification *)notification {
    NSLog(@"==> onCDVRemoteNotification");
    NSString *nameString = [notification name];
    NSString *objectString = [notification object];
    NSLog(@"name = %@, object = %@", nameString, objectString);
    
    self.deviceToken = objectString;
    
    // 绑定账号
    [XGPush setAccount:self.account];
    [XGPush registerDeviceStr:self.deviceToken];
}

// 收到消息回调
- (void)onXGReceiveRemoteNotification:(NSNotification *)notification {
    NSLog(@"==> onXGReceiveRemoteNotification");
    
    NSDictionary *userInfo = [notification object];
    if (userInfo == nil) {
        return;
    }
    
    for (id key in userInfo) {
        id v = [userInfo objectForKey:key];
        NSLog(@"key: %@, value: %@", key, v);
    }
    
    NSLog(@"==> self.callbackId: %@", self.callbackId);
    
    NSMutableString *msg = [[userInfo objectForKey:@"aps"] objectForKey:@"alert"];
    NSLog(@"==> msg: %@", msg);
    
    [XGPush handleReceiveNotification:userInfo];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:msg];
    [self.commandDelegate sendPluginResult:result callbackId:self.callbackId];
}

- (void)registerPush{
    [[UIApplication sharedApplication] registerForRemoteNotificationTypes:(UIRemoteNotificationTypeAlert | UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeSound)];
}

- (void)registerPushForIOS8{
#if __IPHONE_OS_VERSION_MAX_ALLOWED >= _IPHONE80_
    
    //Types
    UIUserNotificationType types = UIUserNotificationTypeBadge | UIUserNotificationTypeSound | UIUserNotificationTypeAlert;
    
    //Actions
    UIMutableUserNotificationAction *acceptAction = [[UIMutableUserNotificationAction alloc] init];
    
    acceptAction.identifier = @"ACCEPT_IDENTIFIER";
    acceptAction.title = @"Accept";
    
    acceptAction.activationMode = UIUserNotificationActivationModeForeground;
    acceptAction.destructive = NO;
    acceptAction.authenticationRequired = NO;
    
    //Categories
    UIMutableUserNotificationCategory *inviteCategory = [[UIMutableUserNotificationCategory alloc] init];
    
    inviteCategory.identifier = @"INVITE_CATEGORY";
    
    [inviteCategory setActions:@[acceptAction] forContext:UIUserNotificationActionContextDefault];
    
    [inviteCategory setActions:@[acceptAction] forContext:UIUserNotificationActionContextMinimal];
    
    NSSet *categories = [NSSet setWithObjects:inviteCategory, nil];
    
    
    UIUserNotificationSettings *mySettings = [UIUserNotificationSettings settingsForTypes:types categories:categories];
    
    [[UIApplication sharedApplication] registerUserNotificationSettings:mySettings];
    
    
    [[UIApplication sharedApplication] registerForRemoteNotifications];
#endif
}

@end

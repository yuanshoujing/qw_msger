//
//  QWMsger.h
//  InvoiceIs
//
//  Created by rocker on 15/4/15.
//
//

#import <UIKit/UIKit.h>
#import <Cordova/CDVPlugin.h>
#import "XGPush.h"

@interface QWMsger : CDVPlugin

@property (nonatomic, copy) NSString *account;
@property (nonatomic, copy) NSString *deviceToken;
@property (nonatomic, copy) NSString *callbackId;

- (void)register:(CDVInvokedUrlCommand *)command;
- (void)unRegister:(CDVInvokedUrlCommand *)command;
- (void)bindAccount:(CDVInvokedUrlCommand *)command;
- (void)unBindAccount:(CDVInvokedUrlCommand *)command;
- (void)setBadge:(CDVInvokedUrlCommand *)command;
- (void)onMessage:(CDVInvokedUrlCommand *)command;

@end

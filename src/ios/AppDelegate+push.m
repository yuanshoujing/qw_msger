#import "AppDelegate+push.h"

@implementation AppDelegate (push)

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
    NSLog(@"didReceiveNotification");
    
    [[NSNotificationCenter defaultCenter] postNotificationName:@"XGReceiveRemoteNotification" object:userInfo];
}

@end

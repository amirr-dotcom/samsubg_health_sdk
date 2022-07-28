import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'samsung_health_sdk_platform_interface.dart';

/// An implementation of [SamsungHealthSdkPlatform] that uses method channels.
class MethodChannelSamsungHealthSdk extends SamsungHealthSdkPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('samsung_health_sdk');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}

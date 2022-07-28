import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'samsung_health_sdk_method_channel.dart';

abstract class SamsungHealthSdkPlatform extends PlatformInterface {
  /// Constructs a SamsungHealthSdkPlatform.
  SamsungHealthSdkPlatform() : super(token: _token);

  static final Object _token = Object();

  static SamsungHealthSdkPlatform _instance = MethodChannelSamsungHealthSdk();

  /// The default instance of [SamsungHealthSdkPlatform] to use.
  ///
  /// Defaults to [MethodChannelSamsungHealthSdk].
  static SamsungHealthSdkPlatform get instance => _instance;
  
  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [SamsungHealthSdkPlatform] when
  /// they register themselves.
  static set instance(SamsungHealthSdkPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}

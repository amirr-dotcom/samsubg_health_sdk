
import 'samsung_health_sdk_platform_interface.dart';

class SamsungHealthSdk {
  Future<String?> getPlatformVersion() {
    return SamsungHealthSdkPlatform.instance.getPlatformVersion();
  }
}

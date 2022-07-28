import 'package:flutter_test/flutter_test.dart';
import 'package:samsung_health_sdk/samsung_health_sdk.dart';
import 'package:samsung_health_sdk/samsung_health_sdk_platform_interface.dart';
import 'package:samsung_health_sdk/samsung_health_sdk_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockSamsungHealthSdkPlatform 
    with MockPlatformInterfaceMixin
    implements SamsungHealthSdkPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final SamsungHealthSdkPlatform initialPlatform = SamsungHealthSdkPlatform.instance;

  test('$MethodChannelSamsungHealthSdk is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelSamsungHealthSdk>());
  });

  test('getPlatformVersion', () async {
    SamsungHealthSdk samsungHealthSdkPlugin = SamsungHealthSdk();
    MockSamsungHealthSdkPlatform fakePlatform = MockSamsungHealthSdkPlatform();
    SamsungHealthSdkPlatform.instance = fakePlatform;
  
    expect(await samsungHealthSdkPlugin.getPlatformVersion(), '42');
  });
}

import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:samsung_health_sdk/samsung_health_sdk_method_channel.dart';

void main() {
  MethodChannelSamsungHealthSdk platform = MethodChannelSamsungHealthSdk();
  const MethodChannel channel = MethodChannel('samsung_health_sdk');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}

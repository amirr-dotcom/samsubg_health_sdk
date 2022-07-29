
import 'package:samsung_health_sdk/samsung_health_sdk_method_channel.dart';


class SamsungHealthSdk {

  final MethodChannelSamsungHealthSdk _method=MethodChannelSamsungHealthSdk();


  Future<String?> initiate() {
    return _method.initiate();
  }

  void disconnect() {
    return _method.disconnect();
  }

}

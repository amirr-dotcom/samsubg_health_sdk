package com.devnation.samsung_health_sdk;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthDataStore;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** SamsungHealthSdkPlugin */
public class SamsungHealthSdkPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {

  private ActivityPluginBinding activityPlugin;


  @Override
  public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
    // TODO: your plugin is now attached to an Activity

    activityPlugin=activityPluginBinding;
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding activityPluginBinding) {

  }

  @Override
  public void onDetachedFromActivity() {

  }

  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;


  private Context context;



  private HealthDataStore mStore;


  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "samsung_health_sdk");
    channel.setMethodCallHandler(this);
    context= flutterPluginBinding.getApplicationContext();

  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {

      mStore = new HealthDataStore(context, mConnectionListener);
      mStore.connectService();
    } else {
      result.notImplemented();
    }
  }





  private final HealthDataStore.ConnectionListener mConnectionListener = new HealthDataStore.ConnectionListener() {


    @Override
    public void onConnected() {
      Log.i("SamsungSDK","Connected");
    }

    @Override
    public void onConnectionFailed(HealthConnectionErrorResult error) {
      Log.i("SamsungSDK","Connection Failed");

      if (error.getErrorCode() == HealthConnectionErrorResult.USER_AGREEMENT_NEEDED) {
        error.resolve(activityPlugin.getActivity());
      }

    }

    @Override
    public void onDisconnected() {
      Log.i("SamsungSDK","Disconnected");
    }

  };




  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}

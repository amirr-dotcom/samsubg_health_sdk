package com.devnation.samsung_health_sdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Filter;

import androidx.annotation.NonNull;

import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

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
  private HealthConnectionErrorResult mConnError;


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
  private Set<HealthPermissionManager.PermissionKey> mKeySet;



  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "samsung_health_sdk");
    channel.setMethodCallHandler(this);
    context= flutterPluginBinding.getApplicationContext();

  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("initiate")) {
      initiateHeathService();

    }
    else if (call.method.equals("disconnect")) {
      disconnectHealthService();

    }
    else {
      result.notImplemented();
    }
  }

  public static long getTodayStartUtcTime() {
    Calendar today = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

    today.set(Calendar.HOUR_OF_DAY, 0);
    today.set(Calendar.MINUTE, 0);
    today.set(Calendar.SECOND, 0);
    today.set(Calendar.MILLISECOND, 0);

    return today.getTimeInMillis();
  }

//  public void readTodayStepCountData() {
//
//    // Suppose that the required permission has been acquired already
//
//    // Create a filter for today's steps from all source devices
//    Filter filter = Filter.and(
//            Filter.eq(HealthConstants.StepDailyTrend.DAY_TIME, getTodayStartUtcTime()),
//            Filter.eq(HealthConstants.StepDailyTrend.SOURCE_TYPE, StepDailyTrend.SOURCE_TYPE_ALL));
//
//    ReadRequest request = new ReadRequest.Builder()
//            // Set the data type
//            .setDataType(StepDailyTrend.HEALTH_DATA_TYPE)
//            // Set a filter
//            .setFilter(filter)
//            // Build
//            .build();
//
//    mResolver = new HealthDataResolver(store, null);
//
//    try {
//      mResolver.read(request).setResultListener(result -> {
//        long dayTime = 0;
//        int totalCount = 0;
//
//        try {
//          Iterator iterator = result.iterator();
//          if (iterator.hasNext()) {
//            HealthData data = iterator.next();
//            dayTime = data.getLong(StepDailyTrend.DAY_TIME)
//            totalCount = data.getInt(StepDailyTrend.COUNT);
//          }
//        } finally {
//          result.close();
//        }
//      });
//    } catch (Exception e) {
//      Log.e(MainActivity.APP_TAG, e.getClass().getName() + " - " + e.getMessage());
//    }
//  }




  private void initiateHeathService(){
    mKeySet = new HashSet<HealthPermissionManager.PermissionKey>();
    mKeySet.add(new HealthPermissionManager.PermissionKey(HealthConstants.StepCount.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.READ));
    mStore = new HealthDataStore(context, mConnectionListener);
    mStore.connectService();
  }


  private void disconnectHealthService(){
    mStore.disconnectService();
  }


  private final HealthDataStore.ConnectionListener mConnectionListener = new HealthDataStore.ConnectionListener() {


    @Override
    public void onConnected() {
      Log.i("SamsungSDK","Connected");

      HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);

      try {
        Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = pmsManager.isPermissionAcquired(mKeySet);

        if (resultMap.containsValue(Boolean.FALSE)) {
          // Request the permission for reading step counts if it is not acquired
          pmsManager.requestPermissions(mKeySet, activityPlugin.getActivity()).setResultListener(mPermissionListener);
        } else {
          // Get the current step count and display it
          // ...
        }
      } catch (Exception e) {
        Log.i("SamsungSDKPermissionException", e.getClass().getName() + " - " + e.getMessage());
        Log.i("SamsungSDKPermissionException", "Permission setting fails.");
      }
    }

    @Override
    public void onConnectionFailed(HealthConnectionErrorResult error) {
      Log.i("SamsungSDK","Connection Failed "+error.getErrorCode());

//      if (error.getErrorCode() == HealthConnectionErrorResult.USER_AGREEMENT_NEEDED) {
//        error.resolve(activityPlugin.getActivity());
//      }
//
//      else if (error.getErrorCode() == HealthConnectionErrorResult.PLATFORM_NOT_INSTALLED) {
//        error.resolve(activityPlugin.getActivity());
//      }

      showConnectionFailureDialog(error);

    }

    @Override
    public void onDisconnected() {
      Log.i("SamsungSDK","Disconnected");
    }

  };





  private void showConnectionFailureDialog(HealthConnectionErrorResult error) {

    AlertDialog.Builder alert = new AlertDialog.Builder(context);
    mConnError = error;
    String message = "Connection with Samsung Health is not available";

    if (mConnError.hasResolution()) {
      switch(error.getErrorCode()) {
        case HealthConnectionErrorResult.PLATFORM_NOT_INSTALLED:
          message = "Please install Samsung Health";
          break;
        case HealthConnectionErrorResult.OLD_VERSION_PLATFORM:
          message = "Please upgrade Samsung Health";
          break;
        case HealthConnectionErrorResult.PLATFORM_DISABLED:
          message = "Please enable Samsung Health";
          break;
        case HealthConnectionErrorResult.USER_AGREEMENT_NEEDED:
          message = "Please agree with Samsung Health policy";
          break;
        default:
          message = "Please make Samsung Health available";
          break;
      }
    }

    alert.setMessage(message);

    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int id) {
        if (mConnError.hasResolution()) {
          mConnError.resolve(activityPlugin.getActivity());
        }
      }
    });

    if (error.hasResolution()) {
      alert.setNegativeButton("Cancel", null);
    }

    alert.show();
  }


  private final HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult> mPermissionListener =
          new HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult>() {

            @Override
            public void onResult(HealthPermissionManager.PermissionResult result) {
              Log.i("SamsungSDK", "Permission callback is received.");
              Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = result.getResultMap();

              if (resultMap.containsValue(Boolean.FALSE)) {
                // Requesting permission fails
              } else {
                // Get the current step count and display it
              }
            }
          };





  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }
}

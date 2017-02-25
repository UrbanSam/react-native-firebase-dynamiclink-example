package com.fcm.dynamiclink;

import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.net.Uri;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.BaseActivityEventListener;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.lang.Object;
import java.util.Map;
import java.util.HashMap;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

public class FIRDynamicLinkModule extends ReactContextBaseJavaModule implements 
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener,
    ActivityEventListener,
    LifecycleEventListener{

    private static final String TAG = "FIR_DYNAMIC_LINK";
    private static final int REQUEST_INVITE = 0;
    private GoogleApiClient mGoogleApiClient;
    private ReactContext mReactContext;

    public FIRDynamicLinkModule(ReactApplicationContext reactContext) 
    {
        super(reactContext);
        // Add the listener for `onActivityResult`
        this.mReactContext = reactContext;

        reactContext.addLifecycleEventListener(this);
        reactContext.addActivityEventListener(this);
    } 

    @Override
    public String getName() {
        return "RNFIRDynamicLink";
    }

    private void sendEvent(String eventName, Object params) {
        getReactApplicationContext()
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
    }

    @ReactMethod
    public void authorize()
    {
        Log.d(TAG, "Authorizing");
        // Create an auto-managed GoogleApiClient with access to App Invites.
        mGoogleApiClient = new GoogleApiClient.Builder(mReactContext.getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(AppInvite.API)
                .build();

        mGoogleApiClient.connect();

        this.getDynamicLink();
    }


    @ReactMethod
    public void getDynamicLink()
    {
        if(mGoogleApiClient == null)
        {
            return;
        }

        Log.d(TAG, "Get Invite");
        // Check for App Invite invitations and launch deep-link activity if possible.
        // Requires that an Activity is registered in AndroidManifest.xml to handle
        // deep-link URLs.
        boolean autoLaunchDeepLink = false;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, getCurrentActivity(), autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() 
                        {
                            @Override
                            public void onResult(AppInviteInvitationResult result) 
                            {
                                Log.d(TAG, "getInvitation:onResult:" + result.getStatus());
                                if (result.getStatus().isSuccess()) 
                                {
                                    // Extract information from the intent
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);
                                    String invitationId = AppInviteReferral.getInvitationId(intent);

                                    Log.d(TAG, "Sent Event");
                                    // Because autoLaunchDeepLink = true we don't have to do anything
                                    // here, but we could set that to false and manually choose
                                    // an Activity to launch to handle the deep link here.
                                    // ...

                                    //Compile data and send thru event
                                    WritableMap fdlData = Arguments.createMap();
									fdlData.putString("url", deepLink);
									fdlData.putString("invitationId", invitationId);
                                    sendEvent("FIRDynamicLinkReceived", fdlData);
                                }
                            }
                        });
    }

    //Google API 
    @Override
    public void onConnected(@Nullable Bundle bundle) 
    {
        Log.i(TAG, "Connected");
    }

    @Override
    public void onConnectionSuspended(int i) 
    {
        Log.i(TAG, "Connection Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) 
    {
        Log.i(TAG, "Failed Authorization " + connectionResult);
    }

	//Result For Sending Invitation
    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) 
    {
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) 
        {
            if (resultCode == Activity.RESULT_OK) 
            {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) 
                {
                    Log.d(TAG, "onActivityResult: sent invitation " + id);
                }
            } 
            else 
            {
                // Sending failed or it was canceled, show failure message to the user
                // [START_EXCLUDE]
                showMessage(TAG + " Send Failed");
                // [END_EXCLUDE]
            }
        }
    }

    @Override
    public void onNewIntent(Intent intent) 
    {
    }

    private void showMessage(String msg) 
    {
        Toast.makeText(getReactApplicationContext(), msg, 3).show();
    }

    @Override
    public void onHostResume() 
    {
        Log.i(TAG, "Resumed");
        this.getDynamicLink();
    }

    @Override
    public void onHostPause() 
    {
    }

    @Override
    public void onHostDestroy() 
    {

    }

}

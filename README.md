# react-native-firebase-dynamiclink
Example of integrating Firebase Dynamic Link for React Native.


[Make sure you have followed the instruction guide on firebase dynamic for receiving dynamic links.](https://firebase.google.com/docs/dynamic-links/android)

This is for android only.

In your ../AndroidManifest.xml
```javascript
<intent-filter>
    <action android:name="android.intent.action.VIEW"/>
    <category android:name="android.intent.category.DEFAULT"/>
    <category android:name="android.intent.category.BROWSABLE"/>
    <data android:host="example.com" android:scheme="http"/>
    <data android:host="example.com" android:scheme="https"/>
</intent-filter>
```

In your ../MainApplication.java
```java
import com.fcm.dynamiclink.FIRDynamicLinkPackage; //! Add this at top after your package

@Override
protected List<ReactPackage> getPackages() {
  return Arrays.<ReactPackage>asList(
      new MainReactPackage(),
      new FIRDynamicLinkPackage() //! Add this 
  );
}
```

[Then copy this folder to your android/app/src/main/java/com](../master/example/android/app/src/main/java/com/fcm)
So it would looks like you have com/fcm/dynamiclink, I haven't figure out how to make it like a npm package. 
But it would do for now.

[Finally, copy this folder to anywhere of your project.](../master/example/scripts/RNFIRDynamicLink/)

[Now you should able to import it and use the function, check the example index file for more info.](../master/example/index.android.js)

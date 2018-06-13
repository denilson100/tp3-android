package br.edu.infnet.tp3_android_proj.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

public class Permissions {

    private static final int REQUEST = 1;
    private static String[] PERMISSIONS_BASIC = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.RECEIVE_SMS
    };

    //persmission method.
    public static void verifyPermissions(Activity activity) {
        // Check if we have read or write permission
        int accessLocation = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int receiverSms = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_SMS);

        if (accessLocation != PackageManager.PERMISSION_GRANTED || receiverSms != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_BASIC,
                    REQUEST
            );
        }
    }
}

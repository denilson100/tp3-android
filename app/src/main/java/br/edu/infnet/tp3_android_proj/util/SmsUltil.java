package br.edu.infnet.tp3_android_proj.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class SmsUltil {

    public static void sendSms(Activity activity, String phoneNumber, String message) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + phoneNumber));
        intent.putExtra("sms_body", message);
        activity.startActivity(intent);
    }
}

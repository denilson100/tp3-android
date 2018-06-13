package br.edu.infnet.tp3_android_proj.util;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

import br.edu.infnet.tp3_android_proj.MainActivity;


public class SmsBroadcastReceiver extends BroadcastReceiver {
    //interface

    TextToSpeech textToSpeech;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
            SmsMessage[] msgs = null;
            String msg_from = null;
            if (bundle != null){
                //---retrieve the SMS message received---
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    String msgBody = null;
                    for(int i=0; i<msgs.length; i++){
                        msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        msgBody = msgs[i].getMessageBody();
                        Log.d("TAG", "MSG: " + msgBody);
                        Toast toast = Toast.makeText(context, "De: " + msg_from + " MSG: " + msgBody, Toast.LENGTH_LONG);
                        toast.show();
                    }

                    String msgCompleta = "Você recebeu uma mensagem de: " + msg_from + ", dizendo: " + msgBody;

                    // Envia a intent para classe executar a fala
                    Intent speechIntent = new Intent();
                    speechIntent.setClass(context, MainActivity.class);
                    speechIntent.putExtra("MESSAGE", msgCompleta);
                    speechIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |  Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    context.startActivity(speechIntent);

                }catch(Exception e){
                    Log.d("Exception caught",e.getMessage());
                }
            }
        }

    }

}
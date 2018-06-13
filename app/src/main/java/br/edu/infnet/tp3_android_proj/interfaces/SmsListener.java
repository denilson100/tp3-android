package br.edu.infnet.tp3_android_proj.interfaces;

import android.content.Context;

import br.edu.infnet.tp3_android_proj.MainActivity;

public interface SmsListener {
    public void messageReceived(String messageText);
}

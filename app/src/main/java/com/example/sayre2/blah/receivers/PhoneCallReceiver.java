package com.example.sayre2.blah.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Method;

public class PhoneCallReceiver extends BroadcastReceiver {
    Context context = null;
    private static final String TAG = PhoneCallReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.v(TAG, "Receving....");

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(context);
        String block_number = prefs.getString("block_number", null);
        final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //Turn ON the mute

        audioManager.setStreamMute(AudioManager.STREAM_RING, true);
        final TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                if(state == TelephonyManager.CALL_STATE_RINGING){
                    System.out.println("incomingNumber : "+incomingNumber);
                    try {
                        Class clazz = Class.forName(telephonyManager.getClass().getName());
                        Method method = clazz.getDeclaredMethod("getITelephony");
                        method.setAccessible(true);
                        Object telephonyService = method.invoke(telephonyManager);
                        Method endCall = telephonyService.getClass().getDeclaredMethod("endCall");
                        endCall.setAccessible(true);
                        Object result = endCall.invoke(telephonyService);
                        Log.i(TAG, result.toString());
//                        String phoneNumber = telephonyManager.getLine1Number();
                        Toast.makeText(context, incomingNumber, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                    }
                    //Turn OFF the mute
                    audioManager.setStreamMute(AudioManager.STREAM_RING, false);
                }
            }
        },PhoneStateListener.LISTEN_CALL_STATE);


    }

}

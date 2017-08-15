package com.iflytek.mkl.smscodeget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.mkl.smscodeget.Util.GetCode;

import java.util.regex.Pattern;

public class SMSReceiver extends BroadcastReceiver {
    private static final String TAG = "smscode";

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();
        SmsMessage msg = null;
        if (null != bundle) {
            Object[] smsObj = (Object[]) bundle.get("pdus");
            for (Object object : smsObj) {
                msg = SmsMessage.createFromPdu((byte[]) object);

                Log.d(TAG, "number:" + msg.getOriginatingAddress()
                        + "   body:" + msg.getDisplayMessageBody() + "  time:"
                        + msg.getTimestampMillis());

                Toast.makeText(context, "broadcast receive sms body: " + msg.getMessageBody(), Toast.LENGTH_SHORT).show();
                String code = GetCode.Get(msg.getMessageBody());
                if (code != null) {
                    Log.d(TAG, "broadcast receive SMS code is : " + code);
//                    Toast.makeText(context, "broadcast receive SMS code is : " + code, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

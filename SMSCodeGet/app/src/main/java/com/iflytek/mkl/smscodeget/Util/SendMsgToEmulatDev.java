package com.iflytek.mkl.smscodeget.Util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import java.util.List;

/**
 * Created by Administrator on 2017/8/8.
 */

public class SendMsgToEmulatDev {
    public static void send(String message, Context context) {

        // 移动运营商允许每次发送的字节数据有限，我们可以使用Android给我们提供 的短信工具。
        if (message != null) {
            SmsManager sms = SmsManager.getDefault();

            // 如果短信没有超过限制长度，则返回一个长度的List。
            List<String> texts = sms.divideMessage(message);


            PendingIntent pintent = PendingIntent.getBroadcast(context, 0, new Intent(), 0);

            for (String text : texts) {
                sms.sendTextMessage("5554", null, text, pintent, null);
            }
        }
    }}

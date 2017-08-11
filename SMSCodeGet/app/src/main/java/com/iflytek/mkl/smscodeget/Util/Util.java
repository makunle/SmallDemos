package com.iflytek.mkl.smscodeget.Util;

import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2017/8/8.
 */

public class Util {
    private static final String TAG = "smscodeget";

    public static void doTest(InputStream inputStream, boolean desire) {
        try {
            InputStreamReader ir = new InputStreamReader(inputStream);
            BufferedReader bs = new BufferedReader(ir);
            String line;
            int wrongNum = 0;
            while ((line = bs.readLine()) != null) {
                if (!TextUtils.isEmpty(line)) {
                    if(!desire){
                        String getCode = GetCode.Get(line);
                        if(getCode != null)
                            Log.d(TAG, "++++>" + getCode + " msg:-->" + line);
                        else
                            Log.d(TAG, "----: msg" + line);
                    }else {
                        String[] split = line.split("  ");
                        if (split.length < 2) continue;
                        String desireCode = split[0].replaceAll(" ", "");
                        String smsMsg = split[1];
                        for (int i = 1; i < split.length; i++) {
                            smsMsg += split[i];
                        }
                        smsMsg = smsMsg.replaceAll("  ", "");
                        String getCode = GetCode.Get(smsMsg);
                        if (!desireCode.equals(getCode)){
                            wrongNum++;
                        }
                        Log.d(TAG, "get code:|" + getCode + "| desiredCode:|" + desireCode + "|\nmsg:" + desireCode + "     " + smsMsg);
                    }
                }
            }
            Log.d(TAG, "doTest: test done wrong num: " + wrongNum);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

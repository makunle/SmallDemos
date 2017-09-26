package com.iflytek.mkl.list.check;

import android.content.Context;
import android.util.Log;

import com.iflytek.mkl.imeantiadstest.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by makunle on 2017/9/26.
 * 用于判断C是否为敏感的应用：浏览器、购物App等
 */

public class SensitiveListUtil {
    private static final String TAG = "SensitiveListUtil";

    private static Context context;
    private static ArrayList<Pattern> sensitiveListPattern;

    public static void init(Context context){
        SensitiveListUtil.context = context;
    }

    public static boolean isSensitive(String packageName){
        if(context == null){
            Log.e(TAG, "isSensitive: haven't init");
            return false;
        }

        if(sensitiveListPattern == null){
            sensitiveListPattern = new ArrayList<>();
            InputStream inputStream = context.getResources().openRawResource(R.raw.sensitivelist);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String s = br.readLine();
                while (s != null) {
                    sensitiveListPattern.add(Pattern.compile(s));
                    s = br.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (Pattern pattern : sensitiveListPattern) {
            if (pattern.matcher(packageName).matches()) {
                return true;
            }
        }

        return false;
    }
}

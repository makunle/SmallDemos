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
 * 用于判断B是否在白名单中
 */

public class WhiteListUtil {
    private static final String TAG = "WhiteListUtil";

    private static Context context;
    private static ArrayList<Pattern> whiteListPattern;

    public static void init(Context context) {
        WhiteListUtil.context = context;
    }

    /***
     * 判断应用是否在白名单中
     * @param packageName
     */
    public static boolean isInWhiteList(String packageName) {
        if (context == null) {
            Log.e(TAG, "isInWhiteList: haven't init");
            return false;
        }

        if (whiteListPattern == null) {
            whiteListPattern = new ArrayList<>();
            InputStream inputStream = context.getResources().openRawResource(R.raw.whitelist);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String s = br.readLine();
                while (s != null) {
                    whiteListPattern.add(Pattern.compile(s));
                    s = br.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (Pattern pattern : whiteListPattern) {
            if (pattern.matcher(packageName).matches()) {
                return true;
            }
        }
        return false;
    }
}

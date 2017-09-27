package com.iflytek.mkl.list.check;

import android.content.Context;
import com.iflytek.mkl.log.Log;

import com.iflytek.mkl.imeantiadstest.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
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
     * 根据whitelist.txt中的正则表达式判断应用是否在白名单中
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
                String s = null;
                while ((s = br.readLine()) != null) {
                    whiteListPattern.add(Pattern.compile(s));
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

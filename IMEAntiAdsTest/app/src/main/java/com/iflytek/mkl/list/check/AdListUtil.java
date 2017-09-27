package com.iflytek.mkl.list.check;

import android.content.Context;
import android.content.pm.PackageManager;

import com.iflytek.mkl.log.Log;

import com.iflytek.mkl.db.DBUtil;
import com.iflytek.mkl.imeantiadstest.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;

import dalvik.system.DexFile;

/**
 * Created by makunle on 2017/9/26.
 * 用于判断B是否包含广告SDK
 */

public class AdListUtil {
    private static final String TAG = "AdListUtil";

    private static Context context = null;

    private static ArrayList<String> adList;

    public static void init(Context context) {
        AdListUtil.context = context;
    }

    /***
     * 判断应用所有类名中是否有广告SDK中的类
     * @param packageName
     * @return
     */
    public static boolean isInAdList(String packageName) {
        if (context == null) {
            Log.e(TAG, "isInAdList: haven't init");
            return false;
        }

        int res = DBUtil.getAdSdkCheckResult(packageName);
        if (res == 1) return true;
        if (res == 0) return false;
        boolean in = checkIfIn(packageName);
        DBUtil.setAdSdkCheckResult(packageName, in);
        return in;
    }

    public static boolean checkIfIn(String packageName) {
        if (adList == null) {
            adList = new ArrayList<>();
            InputStream inputStream = context.getResources().openRawResource(R.raw.adlist);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            try {
                String s = null;
                while ((s = br.readLine()) != null) {
                    adList.add(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            String path = context.getPackageManager().getApplicationInfo(packageName, 0)
                    .sourceDir;
            DexFile dexFile = new DexFile(path);
            Enumeration<String> entries = dexFile.entries();
            while (entries.hasMoreElements()) {
                //获取到一个完整的类名
                String className = entries.nextElement();
                for (String str : adList) {
                    if (className.contains(str)) {
                        Log.d(TAG, packageName + " contain ad sdk: " + str);
                        return true;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}

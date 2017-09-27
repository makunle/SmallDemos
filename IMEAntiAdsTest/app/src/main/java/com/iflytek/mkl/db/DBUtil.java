package com.iflytek.mkl.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iflytek.mkl.log.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库操作工具类
 */

public class DBUtil {

    private static final String TAG = "DBUtil";

    private static DBUtil instance = new DBUtil();

    private DBUtil() {
    }

    AdDetectUtilDbHelper helper;

    public static void initDb(Context context) {
        if (instance.helper == null) {
            instance.helper = new AdDetectUtilDbHelper(context, "ad_detect.db", null, 3);
        }
    }

    public static SQLiteDatabase getDb() {
        if (instance.helper == null) {
            Log.e(TAG, "getDb: haven't init");
            return null;
        }
        return instance.helper.getWritableDatabase();
    }

    /***
     * 从数据库获取一个给定包名应用是否包含广告SDK
     * @param packageName
     * @return 1包含  0不包含  -1还未检测过
     */
    public static int getAdSdkCheckResult(String packageName) {
        if (instance.helper == null) {
            Log.e(TAG, "getIfHaveAdSdk, haven't init");
            return -1;
        }

        int result = -1;

        SQLiteDatabase db = instance.helper.getWritableDatabase();
        if (db == null) return -1;

        Cursor cursor = db.query("ad_sdk_check", null, "pkgname = ?", new String[]{packageName}, null, null, null);
        if (cursor.moveToNext()) {
            result = cursor.getInt(cursor.getColumnIndex("have_ad_sdk"));
        }
        cursor.close();

        return result;
    }

    /***
     * 将一个给定包名的应用是否包含广告sdk检测结果写入数据库
     * @param packageName
     * @param have
     */
    public static void setAdSdkCheckResult(String packageName, boolean have) {
        if (instance.helper == null) {
            Log.e(TAG, "setIfHaveAdSdk, haven't init");
            return;
        }

        if (getAdSdkCheckResult(packageName) != -1) return;

        SQLiteDatabase db = instance.helper.getWritableDatabase();
        if (db == null) return;

        ContentValues value = new ContentValues();
        value.put("pkgname", packageName);
        value.put("have_ad_sdk", have ? 1 : 0);
        db.insert("ad_sdk_check", null, value);
    }

    /***
     * 记录一条Detect数据到数据库
     * @param packageName
     * @param score
     */
    public static void setDetectResult(String packageName, float score, String rule, long time) {
        if (instance.helper == null) {
            Log.e(TAG, "setDetectReuslt, haven't init");
            return;
        }

        SQLiteDatabase db = instance.helper.getWritableDatabase();
        if (db == null) {
            Log.e(TAG, "setDetectReuslt: failed");
            return;
        }

        ContentValues value = new ContentValues();
        value.put("pkgname", packageName);
        value.put("score", score);
        value.put("rule", rule);
        value.put("time", time);
        db.insert("detect_result", null, value);
    }

    /***
     * 当一个应用被检测到包含输入操作时，记录到数据库
     * @param packageName
     */
    public static void setContainInput(String packageName) {
        if (instance.helper == null) {
            Log.e(TAG, "setContainInput, haven't init");
            return;
        }


        if (getContainInput(packageName)) return;

        SQLiteDatabase db = instance.helper.getWritableDatabase();
        if (db == null) {
            Log.e(TAG, "setContainInput: failed");
            return;
        }

        ContentValues value = new ContentValues();
        value.put("pkgname", packageName);
        db.insert("contain_input", null, value);
    }

    /***
     * 从数据库读取，判断给定包名应用内是否包含输入操作
     * @param packageName
     * @return
     */
    public static boolean getContainInput(String packageName) {
        if (instance.helper == null) {
            Log.e(TAG, "getContainInput, haven't init");
            return false;
        }

        boolean result = false;

        SQLiteDatabase db = instance.helper.getWritableDatabase();
        if (db == null) return false;

        Cursor cursor = db.query("contain_input", null, "pkgname = ?", new String[]{packageName}, null, null, null);
        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();

        return result;
    }

    /***
     * 获取前topNum个高分应用名称
     * @param topNum 前N个高分应用，-1表示全部
     * @return List<String>，单个String格式为pkgName|score|time
     */
    public static List<String> getTopScorePkg(int topNum) {
        if (instance.helper == null) {
            Log.e(TAG, "getTopScorePkg, haven't init");
            return null;
        }

        SQLiteDatabase db = instance.helper.getWritableDatabase();
        if (db == null) return null;

        List<String> res = new ArrayList<>();

        Cursor cursor = db.query("detect_result", null, null, null, null, null, "score desc", null);
        String prePkgName = "";
        while (cursor.moveToNext()) {
            StringBuilder sb = new StringBuilder();
            String pkgName = cursor.getString(cursor.getColumnIndex("pkgname"));
            if (!prePkgName.equals(pkgName)) {
                if (topNum >= 0) {
                    topNum--;
                    if (topNum < 0) break;
                }
                prePkgName = pkgName;
            }
            sb.append(pkgName);
            sb.append("|").append(cursor.getString(cursor.getColumnIndex("score")));
            sb.append("|").append(cursor.getString(cursor.getColumnIndex("time")));
            res.add(sb.toString());

        }
        cursor.close();
        return res;
    }
}

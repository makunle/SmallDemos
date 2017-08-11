package com.iflytek.mkl.smscodeget.Util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by Administrator on 2017/8/8.
 */

public class ForMiui
{

    // 检测MIUI
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    /**
     * 检查手机是否是miui
     *
     * @ref http://dev.xiaomi.com/doc/p=254/index.html
     * @return
     */
    public static boolean isMIUI()
    {
        String device = Build.MANUFACTURER;
        System.out.println("Build.MANUFACTURER = " + device);
        if (device.equals("Xiaomi"))
        {
            System.out.println("this is a xiaomi device");
            Properties prop = new Properties();
            try
            {
                prop.load(new FileInputStream(new File(Environment
                        .getRootDirectory(), "build.prop")));
            } catch (IOException e)
            {
                e.printStackTrace();
                return false;
            }

            return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        } else
        {
            return false;
        }
    }

    /**
     * 跳转到应用权限设置页面 http://www.tuicool.com/articles/jUby6rA
     *
     * @param context
     *            传入app 或者 activity
     *            context，通过context获取应用packegename，之后通过packegename跳转制定应用
     * @return 是否是miui
     */
    public static boolean gotoPermissionSettings(Context context)
    {
        boolean mark = isMIUI();

        if (mark)
        {

            // 只兼容miui v5/v6 的应用权限设置页面，否则的话跳转应用设置页面（权限设置上一级页面）
            try
            {
                Intent localIntent = new Intent(
                        "miui.intent.action.APP_PERM_EDITOR");
                localIntent
                        .setClassName("com.miui.securitycenter",
                                "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                localIntent.putExtra("extra_pkgname", context.getPackageName());
                context.startActivity(localIntent);

            } catch (ActivityNotFoundException e)
            {
                Intent intent = new Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(),
                        null);
                intent.setData(uri);
                context.startActivity(intent);
            }
        }

        return mark;
    }
}
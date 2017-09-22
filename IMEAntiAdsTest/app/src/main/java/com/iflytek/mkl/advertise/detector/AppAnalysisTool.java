package com.iflytek.mkl.advertise.detector;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import dalvik.system.DexFile;

/**
 * Created by makunle on 2017/9/22.
 */

public class AppAnalysisTool {
    private static final String TAG = "AppAnalysisTool";

    public static void getClassesInPkg(String packageName, Context context) {
        try {
            String path = context.getPackageManager().getApplicationInfo(packageName, 0)
                    .sourceDir;
            DexFile dexFile = new DexFile(path);
            Enumeration<String> entries = dexFile.entries();
            int count = 0;
            while (entries.hasMoreElements()) {
                entries.nextElement();
                count++;
            }
            Log.d(TAG, "package " + packageName + " have " + count +" class");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String a(String paramString1, String paramString2)
    {
        Object localObject1;
        if ((paramString2 == null) || (paramString2.length() == 0))
        {
            localObject1 = null;
            IvParameterSpec paramStringa = new IvParameterSpec("0123456789ABCDEF".getBytes());
            SecretKeySpec paramStringb = a(paramString1);
            Cipher localObject2 = null;
            try {
                localObject2 = Cipher.getInstance("AES/CBC/PKCS5Padding");
                ((Cipher)localObject2).init(2, paramStringb, paramStringa);
                return new String(((Cipher)localObject2).doFinal((byte[])localObject1), "UTF-8");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
        int j = paramString2.length() / 2;
        byte localObject2[] = new byte[j];
        int i = 0;
        for (;;)
        {
            localObject1 = localObject2;
            if (i >= j) {
                break;
            }
            localObject2[i] = ((byte)(Integer.parseInt(paramString2.substring(i * 2, i * 2 + 2), 16) & 0xFF));
            i += 1;
        }
        try {
            return new String(localObject2, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static SecretKeySpec a(String paramString1)
    {
        Object localObject = null;
        String str = paramString1;
        if (paramString1 == null) {
            str = "";
        }
        StringBuffer paramString = new StringBuffer(16);
        paramString.append(str);
        while (paramString.length() < 16) {
            paramString.append("0");
        }
        if (paramString.length() > 16) {
            paramString.setLength(16);
        }
        try
        {
            byte[] bytes = paramString.toString().getBytes("UTF-8");
            return new SecretKeySpec(bytes, "AES");
        }
        catch (Exception e)
        {
        }
        return null;
    }
}

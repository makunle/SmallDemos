package com.iflytek.mkl.imeantiadstest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.iflytek.mkl.advertise.detector.AppAnalysisTool;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.b1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String dualiad = AppAnalysisTool.a("dualiad", "043A48E4A53FADE6A11CE763588DCEA2");
//                Log.d(TAG, "onClick: " + dualiad);

                String des = decryptDES("043A48E4A53FADE6A11CE763588DCEA2", "dualiad");
                Log.d(TAG, "onClick: " + des);

                Properties properties = new Properties();
                try {
                    properties.load(getResources().openRawResource(R.raw.adlibrary));
                    for (int i = 0; i < properties.size(); i++) {
                        String str = properties.getProperty("" + i);
                        String des1 = decryptDES(str, "dualiad");
                        Log.d(TAG, "" + des1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static String decryptDES(String decryptString, String decryptKey) {

        int j = decryptString.length() / 2;
        byte[] localObject2 = new byte[j];
        int i = 0;
        for (;;)
        {
            if (i >= j) {
                break;
            }
            localObject2[i] = ((byte)(Integer.parseInt(decryptString.substring(i * 2, i * 2 + 2), 16) & 0xFF));
            i += 1;
        }

        try {
            //先使用Base64解密
//            byte[] byteMi = Base64.decode(decryptString, 0);
            byte[] byteMi = decryptString.getBytes("UTF-8");
            //实例化IvParameterSpec对象使用指定的初始化向量
            IvParameterSpec zeroIv = new IvParameterSpec("0123456789ABCDEF".getBytes());
            //实例化SecretKeySpec，根据传入的密钥获得字节数组来构造SecretKeySpec,
            StringBuffer dk = new StringBuffer();
            dk.append(decryptKey);
            while (dk.length() < 16) {
                dk.append("0");
            }
            SecretKeySpec key = new SecretKeySpec(dk.toString().getBytes("UTF-8"), "AES");
            //创建密码器
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            //用密钥初始化Cipher对象,上面是加密，这是解密模式
            cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
            //获取解密后的数据
            byte[] decryptedData = cipher.doFinal(localObject2);
            return new String(decryptedData, "UTF-8");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}

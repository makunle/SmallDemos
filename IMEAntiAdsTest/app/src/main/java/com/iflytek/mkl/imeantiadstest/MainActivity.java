package com.iflytek.mkl.imeantiadstest;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.iflytek.mkl.advertise.detect.AdDetect;
import com.iflytek.mkl.db.DBUtil;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.iflytek.mkl.log.Log;

import static com.iflytek.mkl.db.AdDetectUtilDbHelper.CREATE_DB_AD_CHECK;
import static com.iflytek.mkl.db.AdDetectUtilDbHelper.CREATE_DB_CONTAIN_INPUT;
import static com.iflytek.mkl.db.AdDetectUtilDbHelper.CREATE_DB_DETECT_RESULT;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView output = (TextView) findViewById(R.id.output);

        findViewById(R.id.b1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                output.setText("");
                SQLiteDatabase db = DBUtil.getDb();
                StringBuffer sb = new StringBuffer();
                sb.append("--------------------ad sdk-------------------\n");
                Cursor cursor = db.query("ad_sdk_check", null, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    sb.append(cursor.getString(0)).append("   ").append(cursor.getString(1)).append("\n");
                }
                cursor.close();
                cursor = db.query("detect_result", null, null, null, null, null, null);
                sb.append("-----------------detect score-----------\n");
                while (cursor.moveToNext()) {
                    sb.append(cursor.getString(1)).append("   ").append(cursor.getString(2)).append("   ").append(cursor.getString(3)).append("   ").append("\n");
                }
                cursor.close();
                sb.append("-----------------contain_input-----------\n");
                cursor = db.query("contain_input", null, null, null, null, null, null);
                while (cursor.moveToNext()) {
                    sb.append(cursor.getString(0)).append("\n");
                }
                cursor.close();

                sb.append("------------------top -------------------\n");
                List<String> topScorePkg = DBUtil.getTopScorePkg(-1);
                for (String str : topScorePkg) {
                    sb.append(str+"\n");
                }

                sb.append("------------------top 2-------------------\n");
                topScorePkg = DBUtil.getTopScorePkg(2);
                for (String str : topScorePkg) {
                    sb.append(str+"\n");
                }

                output.setText(sb);
            }
        });

        AdDetect.init(this);

//        DBUtil.setDetectResult("com.text.test", 8);
//        DBUtil.setAdSdkCheckResult("com.aa.f", true);
        int a = DBUtil.getAdSdkCheckResult("com.aa.f");
//        int b = DBUtil.getAdSdkCheckResult("com.bb.f");
//        Log.d(TAG, "onCreate: " + a + " " + b);
    }


    public static String decryptDES(String decryptString, String decryptKey) {

        int j = decryptString.length() / 2;
        byte[] localObject2 = new byte[j];
        int i = 0;
        for (; ; ) {
            if (i >= j) {
                break;
            }
            localObject2[i] = ((byte) (Integer.parseInt(decryptString.substring(i * 2, i * 2 + 2), 16) & 0xFF));
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

    public void clearDb(View view) {
        SQLiteDatabase db = DBUtil.getDb();
        db.execSQL("drop table if exists ad_sdk_check;");
        db.execSQL("drop table if exists detect_result;");
        db.execSQL("drop table if exists contain_input;");

        db.execSQL(CREATE_DB_AD_CHECK);
        db.execSQL(CREATE_DB_DETECT_RESULT);
        db.execSQL(CREATE_DB_CONTAIN_INPUT);
    }
}

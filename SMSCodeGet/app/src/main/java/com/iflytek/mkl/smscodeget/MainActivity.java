package com.iflytek.mkl.smscodeget;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.iflytek.mkl.smscodeget.Util.EnDeCode;
import com.iflytek.mkl.smscodeget.Util.ForMiui;
import com.iflytek.mkl.smscodeget.Util.SendMsgToEmulatDev;
import com.iflytek.mkl.smscodeget.Util.GetCode;
import com.iflytek.mkl.smscodeget.Util.Util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GetCode";
    private static final int REQUEST_CODE = 1;

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.msg);

        Util.doTest(getResources().openRawResource(R.raw.deleted_sms_save_test), false);
//        Util.doTest(getResources().openRawResource(R.raw.sms_code_sample), true);
//        Util.doTest(getResources().openRawResource(R.raw.wrong_code), true);
    }

    private boolean getReadSMSPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, REQUEST_CODE);
            return false;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, REQUEST_CODE);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "can't get permission", Toast.LENGTH_SHORT).show();
            } else {

            }
        }
    }

    /**
     * 跳转到App对应的设置页面
     *
     * @param v
     */
    public void jump(View v) {
        if (!getReadSMSPermission()) return;

        if (ForMiui.isMIUI()) {
            ForMiui.gotoPermissionSettings(this);
        }

        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", getPackageName());
        }
        startActivity(localIntent);
    }

    public void sendSMS(View v) {
        String msg = editText.getText().toString();
        String newMsg = EnDeCode.utf8ToUnicode(msg);
        SendMsgToEmulatDev.send(newMsg, this);
    }


    Handler handler = new Handler(){

    };

    private SmsOberver smsOberver = new SmsOberver(new Handler(), this);

    public void registObserver(View v){
        getContentResolver().registerContentObserver(
                Uri.parse("content://sms"), true,
                smsOberver
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(smsOberver);
    }
}

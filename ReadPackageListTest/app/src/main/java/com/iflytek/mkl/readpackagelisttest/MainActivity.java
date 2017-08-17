package com.iflytek.mkl.readpackagelisttest;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Process;
import android.os.UserHandle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";

    private TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        output = (TextView) findViewById(R.id.out_put);

        findViewById(R.id.get_with_shell).setOnClickListener(this);
        findViewById(R.id.get_packages).setOnClickListener(this);
        findViewById(R.id.clear).setOnClickListener(this);
        findViewById(R.id.get_applications).setOnClickListener(this);
        findViewById(R.id.get_package_name).setOnClickListener(this);
        findViewById(R.id.query_intent).setOnClickListener(this);
        findViewById(R.id.get_packages_for_uid).setOnClickListener(this);
    }

    //181027
    private void getListWithPkgMgr_getInstalledPackages() {
        PackageManager pm = getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(0);
        Collections.sort(packages, new Comparator<PackageInfo>() {
            @Override
            public int compare(PackageInfo o1, PackageInfo o2) {
                return o1.applicationInfo.uid - o2.applicationInfo.uid;
            }
        });
        int count = 0;
        for (int i = 0; i < packages.size(); i++) {
//            // 判断系统/非系统应用
//            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) // 非系统应用
//            {
//                show(packageInfo.packageName);
//                count++;
//            }
            ApplicationInfo appinfo = packages.get(i).applicationInfo;
            show(appinfo.uid + "\t\t" + appinfo.packageName);
            count++;
        }
        show("packages count:" + count);
        Toast.makeText(this, "count: " + count, Toast.LENGTH_SHORT).show();
    }

    private void getListWithPkgMgr_getInstalledApplications() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        Collections.sort(apps, new Comparator<ApplicationInfo>() {
            @Override
            public int compare(ApplicationInfo o1, ApplicationInfo o2) {
                return o1.uid - o2.uid;
            }
        });
        int count = 0;
        for (int i = 0; i < apps.size(); i++) {
//            if ((apps.get(i).flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
            show(apps.get(i).uid + "\t\t" + apps.get(i).packageName);
            count++;
//            }
        }
        show("application count:" + count);
        Toast.makeText(this, "count: " + count, Toast.LENGTH_SHORT).show();
    }

    private void getListWithShellCmd() {
        try {
            java.lang.Process process = Runtime.getRuntime().exec("pm list package");
            BufferedReader bis = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int count = 0;
            while ((line = bis.readLine()) != null) {
                show(line);
                count++;
            }
            show("packages count: " + count);
            Toast.makeText(this, "count: " + count, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            show("IOException: " + e);
        }
    }

    private void getListByQueryMainAction() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        String last = "";
        String nowPkg;
        int count = 0;
        for (int i = 0; i < resolveInfos.size(); i++) {
            nowPkg = resolveInfos.get(i).activityInfo.packageName;
            if (!nowPkg.equals(last)) {
                last = nowPkg;
                show(nowPkg);
                count++;
            }
        }
        show("count: " + count);
        Toast.makeText(this, "count: " + count, Toast.LENGTH_SHORT).show();
    }

    private void getByPackageName() {
        String name = ((EditText) (findViewById(R.id.package_name))).getText().toString();
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(name, 0);
            show(packageInfo.toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            show("not found");
        }
    }

    private void show(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            Log.d(TAG, msg);
            if (output != null) {
                output.append(msg + "\n");
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_with_shell:
                getListWithShellCmd();
                break;
            case R.id.get_packages:
                getListWithPkgMgr_getInstalledPackages();
                break;
            case R.id.get_applications:
                getListWithPkgMgr_getInstalledApplications();
                break;
            case R.id.clear:
                output.setText("");
                break;
            case R.id.get_package_name:
                getByPackageName();
                break;
            case R.id.query_intent:
                getListByQueryMainAction();
                break;
            case R.id.get_packages_for_uid:
                getListByIterateUid();
                break;

        }
    }

    private void getListByIterateUid() {
        int uid = Process.myUid();
        show("uid: " + uid);
        uid /= 1000;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            UserHandle userHandle = Process.myUserHandle();
            show("userhandle: " + userHandle.toString());
        }

//        String[] packagesForUid = getPackageManager().getPackagesForUid(10);
//        for(String str :packagesForUid){
//            show(str);
//        }
//        show("count: " + packagesForUid.length);

        int count = 0;

        //all third part  uid: 10000 ~ 19999
//        for (int i = Process.FIRST_APPLICATION_UID; i <= Process.LAST_APPLICATION_UID ; i++) {
//            String[] nameForUid = getPackageManager().getPackagesForUid(i);
//            if (nameForUid != null) {
//                for (String name : nameForUid) {
//                    //name is package name
//                }
//            }
//        }

        //uid: 100 ~ 10999
        List<String> part = new ArrayList<>();
        String fuid = null;
        for (int i = 0; i < 1000; i++) {
            part.clear();
            part.add("" + i);
            if (i < 100) {
                part.add("0" + i);
            }
            if (i < 10) {
                part.add("00" + i);
            }
            for (String p : part) {
                fuid = uid + p;
                int fid = Integer.parseInt(fuid);
                String[] nameForUid = getPackageManager().getPackagesForUid(fid);
                if (nameForUid != null) {
                    for (String name : nameForUid) {
                        show(fid + "\t\t" + name);
                        count++;
                    }
                }
            }
        }
        show("count: " + count);
        Toast.makeText(this, "count: " + count, Toast.LENGTH_SHORT).show();
    }
}

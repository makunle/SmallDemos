package com.iflytek.mkl.readnotificationtest;

import android.app.Notification;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "NotificationObserveServ";

    private TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        output = (TextView) findViewById(R.id.output);

        findViewById(R.id.get_permission).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                        intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                    } else {
                        intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                    }
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "onClick: permission check: " + isEnabled());
            }
        });
        findViewById(R.id.clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                output.setText("");
            }
        });

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        toggleNotificationListenerService();
    }

    private void toggleNotificationListenerService() {
        output.append("running: " + isRunning() + " enable: " + isEnabled() +"\n");

        PackageManager pm = getPackageManager();

        pm.setComponentEnabledSetting(
                new ComponentName(this, SMSNotificationListenerService.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        pm.setComponentEnabledSetting(
                new ComponentName(this, SMSNotificationListenerService.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationGet(SMSNotificationListenerService.Msg msg) {
        Notification notification = msg.sbn.getNotification();
        if (output != null) {
            output.append("==================================================\n");
            output.append("package name: " + msg.sbn.getPackageName() + "\n");
            output.append("ticker text: " + notification.tickerText + "\n--------------------------\n");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Bundle bundle = notification.extras;
                for (String key : bundle.keySet()) {
                    output.append(key + ":" + bundle.get(key) + "\n");
                }
            }
            output.append("contentView is null: " + (notification.contentView == null) + "\n");
            output.append("tickerView is null: " + (notification.tickerView == null) + "\n");
            output.append("bigContentView is null: " + (notification.bigContentView == null) + "\n");

            if(notification.contentView == null) return;

            RemoteViews views = notification.contentView;
            Class secretClass = views.getClass();

//            try {
//                Field mActions = secretClass.getDeclaredField("mActions");
//                mActions.setAccessible(true);
//                ArrayList<Object> actions = (ArrayList<Object>) mActions.get(views);
//                for (Object action : actions) {
//                    Field innerFields[] = action.getClass().getDeclaredFields();
//                    output.append("action: " + action.getClass().getName() +"\n");
//                    for (Field field : innerFields) {
//                        if (field.getName().equals("value")) {
//                            field.setAccessible(true);
//                            output.append("action:" + field.get(action).toString() + "  " + field.get(action).getClass().getName() + "\n");
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            try {
                Field field = views.getClass().getDeclaredField("mActions");
                field.setAccessible(true);
                @SuppressWarnings("unchecked")
                ArrayList<Parcelable> actions = (ArrayList<Parcelable>) field.get(views);
                // Find the setText() and setTime() reflection actions
                for (Parcelable p : actions) {
                    Parcel parcel = Parcel.obtain();
                    p.writeToParcel(parcel, 0);
                    parcel.setDataPosition(0);
                    // The tag tells which type of action it is (2 is ReflectionAction, from the source)
                    int tag = parcel.readInt();
                    if (tag != 2) continue;
                    // View ID
                    parcel.readInt();
                    String methodName = parcel.readString();
                    if (null == methodName) {
                        continue;
                    } else if (methodName.equals("setText")) {
                        // Parameter type (10 = Character Sequence)
                        parcel.readInt();
                        // Store the actual string
                        String t = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel).toString().trim();
                        output.append("setText: " + t + "\n");
                    }
                    parcel.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Parcelable parcelable = output.onSaveInstanceState();
        outState.putParcelable("output", parcelable);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        output.onRestoreInstanceState(savedInstanceState.getParcelable("output"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private boolean isEnabled() {
        Set<String> packageNames = NotificationManagerCompat.getEnabledListenerPackages(this);
        if (packageNames.contains(getPackageName())) {
            return true;
        }
        return false;
    }

    private boolean isRunning() {
        boolean enable = false;
        String packageName = getPackageName();
        String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (flat != null) {
            enable = flat.contains(packageName);
        }
        return enable;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }
}

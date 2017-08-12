package com.iflytek.mkl.readnotificationtest;

import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNotificationGet(NotificationObserveService.Msg msg){
        Notification notification = msg.sbn.getNotification();
        if(output != null && notification.tickerText != null){
            output.append("==================================================\n");
            output.append(notification.tickerText+"\n--------------------------\n");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Bundle bundle = notification.extras;
                for(String key : bundle.keySet()){
                    output.append(key + ":" + bundle.get(key)+"\n");
                }
            }
            output.append("contentView is null: " + (notification.contentView == null) + "\n");
            output.append("tickerView is null: " + (notification.tickerView == null) + "\n");
            output.append("bigContentView is null: " + (notification.bigContentView == null) + "\n");

            RemoteViews views = notification.contentView;
            Class secretClass = views.getClass();

            try {
                Map<Integer, String> text = new HashMap<Integer, String>();

                Field outerFields[] = secretClass.getDeclaredFields();
                for (int i = 0; i < outerFields.length; i++) {
                    if (!outerFields[i].getName().equals("mActions")) continue;

                    outerFields[i].setAccessible(true);

                    ArrayList<Object> actions = (ArrayList<Object>) outerFields[i]
                            .get(views);
                    for (Object action : actions) {
                        Field innerFields[] = action.getClass().getDeclaredFields();

                        Object value = null;
                        Integer type = null;
                        Integer viewId = null;
                        for (Field field : innerFields) {
                            field.setAccessible(true);
                            if (field.getName().equals("value")) {
                                value = field.get(action);
                            } else if (field.getName().equals("type")) {
                                type = field.getInt(action);
                            } else if (field.getName().equals("viewId")) {
                                viewId = field.getInt(action);
                            }
                        }

//                        if (type == 9 || type == 10) {
                            text.put(viewId, value.toString());
                            output.append(viewId + ":" +value.toString() + "\n");
//                        }
                    }
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
        boolean enable = false;
        String packageName = getPackageName();
        String flat= Settings.Secure.getString(getContentResolver(),"enabled_notification_listeners");
        if (flat != null) {
            enable= flat.contains(packageName);
        }
        return enable;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }
}

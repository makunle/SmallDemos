package com.iflytek.mkl.wcbdtest;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private String DBType = "sqlite";

    private TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View.OnClickListener radioClickLsn = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.sqlcipher:
                        DBType = "sqlcipher";
                        break;
                    case R.id.sqlite:
                        DBType = "sqlite";
                        break;
                }
            }
        };
        findViewById(R.id.sqlite).setOnClickListener(radioClickLsn);
        findViewById(R.id.sqlcipher).setOnClickListener(radioClickLsn);

        output = (TextView) findViewById(R.id.output);
    }

    public void create(View view) {
        show("create: " + DBType);
    }

    public void delete(View view) {
        show("delete: " + DBType);
    }

    public void update(View view) {
        show("update: " + DBType);
    }

    public void query(View view) {
        show("query: " + DBType);
    }

    private void show(String msg) {
        Log.d(TAG, msg);
        output.append(msg + "\n");
    }
}

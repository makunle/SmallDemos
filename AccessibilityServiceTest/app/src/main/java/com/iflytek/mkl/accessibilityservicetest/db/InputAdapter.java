package com.iflytek.mkl.accessibilityservicetest.db;

import android.content.Intent;

import java.util.List;

/**
 * Created by Administrator on 2017/8/15.
 */

public class InputAdapter {
    int id;

    String packageName;                 //包名
    int editTextCount;                  //EditText控件个数

    List<Integer> codeAreaIds;          //验证码输入框列表 为正常输入框时，size=1， 有多个时，size>1且按顺序排列
    List<Integer> numberAreaIds;         //手机号

}

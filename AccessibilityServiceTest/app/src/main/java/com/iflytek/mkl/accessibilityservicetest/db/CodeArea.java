package com.iflytek.mkl.accessibilityservicetest.db;

/**
 * Created by Administrator on 2017/8/15.
 */

public class CodeArea {
    int id;
    boolean codeEditTextHaveId;         //验证码输入框有是否具有resourceId
    String codeEditTextId;              //验证码输入框resourceId
    int codeEditTextOrderInDFS;         //验证码输入框是深度优先搜索中第var个EditText
}

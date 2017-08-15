package com.iflytek.mkl.accessibilityservicetest.db;

/**
 * Created by Administrator on 2017/8/15.
 */

public class NumberArea {
    int id;
    boolean numberEditTextHaveId;       //手机号码输入框是否具有resourceId
    String numberEditTextId;            //手机号码输入框resourceId
    int numberEditTextOrderInDFS;       //手机号码输入框在深度优先搜索中的位置
    String phoneNumber;                 //手机号, null时不填写
}

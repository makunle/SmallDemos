package com.iflytek.mkl.smscodeget.Util;

import java.io.UnsupportedEncodingException;

/**
 * Created by Administrator on 2017/8/8.
 */

public class EnDeCode {
    public static String getUncStringFromUtf(String str){
        try {
            String uncStr = new String(str.getBytes("utf-8"), "");
            return uncStr;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String utf8ToUnicode(String inStr) {
        char[] myBuffer = inStr.toCharArray();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < inStr.length(); i++) {
            Character.UnicodeBlock ub = Character.UnicodeBlock.of(myBuffer[i]);
            if(ub == Character.UnicodeBlock.BASIC_LATIN){
                //英文及数字等
                sb.append(myBuffer[i]);
            }else if(ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS){
                //全角半角字符
                int j = (int) myBuffer[i] - 65248;
                sb.append((char)j);
            }else{
                //汉字
                short s = (short) myBuffer[i];
                String hexS = Integer.toHexString(s);
                String unicode = "\\u"+hexS;
                sb.append(unicode.toLowerCase());
            }
        }
        return sb.toString();
    }
}

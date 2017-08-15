package com.iflytek.mkl.accessibilityservicetest;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/8/7.
 */

public class VerificationCodeGetter {

    private static final String TAG = "VerificationCodeGetter";

    private static final String signReg = "(?:是|为| is|:|：|（)";
    private static final String codeReg = "([a-zA-Z0-9]*)";
    private static final String typeReg = "((?:验证|交易|授权|随机|登录密|验证代|提货|兑换|动态)码|code)";

    /**
     * 执行预处理操作
     * 1、删除短信开头的【名称】
     * 2、合并flag所在区域：
     * a、（xx flag xx） -->  flag
     * b、 flag sign -->  flag
     *
     * @param str
     * @return
     */
    public static String getCode(String str) {

        String type = getCodeType(str);           //获取验证码类型
        if (type == null) return null;
        String code = getSpecificCode(str, type);
        if(code != null) return code;
        str = removeNameSurround(str, type);      //删除(xxx type xxx)     ,xxx type xxx, 中的xxx
        str = removeTitleSurround(str);           //删除【xxx】类似的title
        code = getCode(str, type);
        return code;
    }

    private static String removeTitleSurround(String str) {
        String nameSurround = "(【.*?】)";
        Matcher m = Pattern.compile(nameSurround, Pattern.COMMENTS).matcher(str);
        if (m.lookingAt()) {
            str = m.replaceAll("");
        }
        return str;
    }

    /**
     * //删除(xxx type xxx)中的xxx
     *
     * @param str
     * @return
     */
    private static String removeNameSurround(String str, String type) {
        String nameSurround = "(【.*?" + type + ".*?】) | " +
                "(\\(.*?" + type + ".*?\\)) | " +
                "(\\[*?" + type + ".*?\\]) | " +
                "(（.*?" + type + ".*?）) | " +
                "(，.*?" + type + "，) |" +
                "(,.*?" + type + ",) ";
        Matcher m = Pattern.compile(nameSurround, Pattern.COMMENTS).matcher(str);

        int start = 0;
        String minGropStr = "  ";
        while (m.find(start)) {
            if (start == 0) minGropStr = m.group();
            else if (minGropStr.length() > m.group().length()) {
                minGropStr = m.group();
            }
            start = m.start() + 1;
        }
        str = str.replaceAll(minGropStr, type);
        return str;
    }

    /**
     * 获取验证码类型
     * 有比较特殊的短信，"您正在办理掌厅随机码登录业务，验证码是399243，" 包含随机码，验证码
     * 优先选择 typeReg signReg+ 的 [.*码]
     * @param str
     * @return
     */
    private static String getCodeType(String str) {
        final Matcher withSign = Pattern.compile("(" + typeReg + ")" + signReg + "+").matcher(str);
        if (withSign.find()) {
            return withSign.group(1);
        }
        final Matcher m = Pattern.compile(typeReg).matcher(str);
        if (m.find()) {
            return m.group();
        }
        return null;
    }

    /**
     * 获取有type + signReg类型的code
     */
    private static String getSpecificCode(String msg, String type){
        final String flagReg = type + signReg;

        Matcher m = Pattern.compile(codeReg).matcher(msg);
        Matcher fullFlag = Pattern.compile(flagReg + "+").matcher(msg);

        //有明确的标识时，直接确定验证码
        int index = -1;
        if (fullFlag.find()) {
            index = fullFlag.end();
        }
        String haveFlagRes = null;
        while (index != -1 && m.find(index)) {
            if (!TextUtils.isEmpty(m.group())) {
                haveFlagRes = m.group();
                break;
            }
            index = m.end() + 1;
            if (index >= msg.length()) break;
        }
        if (!TextUtils.isEmpty(haveFlagRes)) return haveFlagRes;
        return null;
    }
    /**
     * 获取验证短信中的验证码
     * 首先看是否有  验证码[是\为\:\(]之类的字样，有的话验证码为其后紧跟的内容
     * 如果没有的话先找到  验证码  所在的位置，然后找距离它最近的 可能为code的内容
     * 距离相同时，默认左边为有效内容
     *
     * @param msg
     * @return
     */
    private static String getCode(String msg, String type) {

        final String flagReg = type + signReg;

        Matcher m = Pattern.compile(codeReg).matcher(msg);
        Matcher flag = Pattern.compile(flagReg + "*").matcher(msg);

        if (!flag.find()) return null;

        //无明确标识时，找寻距离type最近的
        String withoutFlagRes = null;
        int dist = Integer.MAX_VALUE;
        int newDist = -1;

        while (m.find()) {
            if (!TextUtils.isEmpty(m.group())) {
                if (m.start() < 3) return m.group(); //最开始的一般是验证码

                if (m.end() <= flag.start()) {
                    newDist = flag.start() - m.end();
                } else {
                    newDist = m.start() - flag.end();
                }
                if (newDist < dist) {
                    withoutFlagRes = m.group();
                    dist = newDist;
                }
            }
        }
        return withoutFlagRes;
    }
}

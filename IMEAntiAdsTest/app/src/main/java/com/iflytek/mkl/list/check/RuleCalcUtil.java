package com.iflytek.mkl.list.check;

import android.content.Context;
import android.util.Log;

import com.iflytek.mkl.advertise.detect.AppState;
import com.iflytek.mkl.db.DBUtil;
import com.iflytek.mkl.imeantiadstest.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by makunle on 2017/9/26.
 */

public class RuleCalcUtil {

    private static final String TAG = "RuleCalcUtil";

    private static Context context = null;

    public static void init(Context context) {
        RuleCalcUtil.context = context;
    }

    /**
     * r1:A.state==1
     * true:+1
     * false:-1
     * <p>
     * r2:A.isSensitive
     * true:+1
     * false:-1
     */
    public static class Rule {
        String ruleCondition;   // A.state==1   A.isSensitive
        String trueRes;         //+1
        String falseRes;        //-1
        String ruleName;        //r1
    }

    private static ArrayList<Rule> rules;

    /***
     * 根据rule.txt中制定的规则给B计算一个分值
     * @param A
     * @param B
     * @param C
     */
    public static void calculate(AppState A, AppState B, AppState C) {
        if (context == null) {
            Log.e(TAG, "calculate: haven't init");
        }

        //规则列表不存在则生成
        if (rules == null) {
            makeRules();
        }

        float Add = 0, Multi = 1;
        String ruleRec = "";

        //支持的三种比较符号
        Pattern sign = Pattern.compile("(==|>|<)");

        for (Rule rule : rules) {
            String appState = null;
            String property = null;
            String condition = null;
            String function = null;
            String symbole = null;

            //取出规则内容，appState名、属性、比较符号、比较数值
            Matcher matcher = sign.matcher(rule.ruleCondition);
            if (matcher.find()) {
                symbole = matcher.group();
                String comb = rule.ruleCondition.substring(0, matcher.start());
                appState = comb.split("\\.")[0];
                property = comb.split("\\.")[1];
                condition = rule.ruleCondition.substring(matcher.end());
            } else {
                appState = rule.ruleCondition.split("\\.")[0];
                function = rule.ruleCondition.split("\\.")[1];
            }

            //根据规则中属性名获取对应AppState
            AppState dealAppState = null;
            if (appState.equals("A")) {
                dealAppState = A;
            } else if (appState.equals("B")) {
                dealAppState = B;
            } else if (appState.equals("C")) {
                dealAppState = C;
            }

            int res = -1;
            //A.state == 1 与给定属性值比较计算
            if (symbole != null && dealAppState != null) {
                long value = -1;
                if (property.equals("state")) {
                    value = dealAppState.getState();
                } else if (property.equals("duration")) {
                    value = dealAppState.getDurTime();
                }
                long compareValue = Long.parseLong(condition);
                if (value != -1) {
                    if (symbole.equals("==")) {
                        res = compareValue == value ? 1 : 0;
                    } else if (symbole.equals(">")) {
                        res = value > compareValue ? 1 : 0;
                    } else if (symbole.equals("<")) {
                        res = value < compareValue ? 1 : 0;
                    }
                }
            }
            //A.inSensitive 属性操作函数
            else {
                if ("isSensitive".equals(function) && dealAppState != null) {
                    res = SensitiveListUtil.isSensitive(dealAppState.packageName) ? 1 : 0;
                } else if ("inputed".equals(function) && dealAppState != null) {
                    res = dealAppState.isInputed()? 1 : 0;
                } else if ("isNull".equals(function)) {
                    res = dealAppState == null ? 1 : 0;
                } else if ("containInput".equals(function) && dealAppState != null) {
                    res = DBUtil.getContainInput(dealAppState.packageName) ? 1 : 0;
                } else if ("inWhiteList".equals(function) && dealAppState != null) {
                    res = WhiteListUtil.isInWhiteList(dealAppState.packageName) ? 1 : 0;
                } else if ("containAdSdk".equals(function) && dealAppState != null) {
                    res = AdListUtil.isInAdList(dealAppState.packageName) ? 1 : 0;
                }
            }

            //根据属性计算结果true/false进行数值计算
            if (rule.trueRes != null && res == 1) {
                ruleRec += rule.ruleName + rule.trueRes + " ";
                if (rule.trueRes.startsWith("+")) {
                    Add += Float.parseFloat(rule.trueRes.substring(1));
                } else if (rule.trueRes.startsWith("x")) {
                    Multi *= Float.parseFloat(rule.trueRes.substring(1));
                }
            } else if (res == 0 && rule.falseRes != null) {
                ruleRec += rule.ruleName + rule.falseRes + " ";
                if (rule.falseRes.startsWith("+")) {
                    Add += Float.parseFloat(rule.falseRes.substring(1));
                } else if (rule.falseRes.startsWith("x")) {
                    Multi *= Float.parseFloat(rule.falseRes.substring(1));
                }
            }
        }

        //计算结果，存入数据库
        float total = Add * Multi;
        DBUtil.setDetectResult(B.packageName, total, ruleRec);

    }


    private static void makeRules() {
        rules = new ArrayList<>();
        InputStream inputStream = context.getResources().openRawResource(R.raw.rule);
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String s = null;
            Rule rule = null;
            while ((s = br.readLine()) != null) {
                if (s.startsWith("r")) {
                    if (rule != null) {
                        rules.add(rule);
                    }
                    rule = new Rule();
                    rule.ruleName = s.split(":")[0];
                    rule.ruleCondition = s.split(":")[1];
                } else if (rule != null && s.length() > 0) {
                    String[] split = s.split(":");
                    if (split.length == 2) {
                        if (split[0].equals("true")) {
                            rule.trueRes = split[1];
                        } else if (split[0].equals("false")) {
                            rule.falseRes = split[1];
                        }
                    }
                } else {
                    if (rule != null) {
                        rules.add(rule);
                        rule = null;
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

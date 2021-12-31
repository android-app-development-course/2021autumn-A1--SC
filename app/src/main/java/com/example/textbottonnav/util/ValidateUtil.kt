package com.example.textbottonnav.util

import java.util.regex.Pattern

/**
 * 字符串验空
 * @param value：输入的号码
 * @return true -->>空串正确，false-->>非空串
 * textutils.isempty
 */
object ValidateUtil {
    fun ValidateEmpty(value: String): Boolean {
        if (value == ""){
            return true
        }
        else{
            return false
        }
    }

    /**
     * 手机号码检测
     * @param phone：输入的号码
     * @return true -->>号码正确，false-->>号码不正确
     */
    @JvmStatic
    fun PhoneCheck(phone: String?): Boolean {
        val ChineseMainland =
            "^((13[0-9])|(14[5,7,9])|(15[0-3,5-9])|(166)|(17[3,5,6,7,8])" + "|(18[0-9])|(19[8,9]))\\d{8}$"
        val HongKong = "^(5|6|8|9)\\d{7}$"
        val C = Pattern.compile(ChineseMainland).matcher(phone)
        val H = Pattern.compile(HongKong).matcher(phone)
        return C.matches() || H.matches()
    }
    /**
     * 密码检测（是否符合最少3个字母的要求）
     * @param password：输入的密码
     * @return true-->>密码格式正确，false-->>密码格式不正确
     */
    @JvmStatic
    fun PasswordCheck(password: String): Boolean {
        val s = password.toCharArray()
        var count = 0
        for (i in s.indices) {
            if (s[i] >= 'a' && s[i] <= 'z' || s[i] >= 'A' && s[i] <= 'Z') {
                count++
            }
        }
        return if (count >= 3) {
            true
        } else {
            false
        }
    }

    /**
     * 判断字符串是否为地址
     * 目前只能判断字符串中是否有包含省、市
     * @param adress 输入待判断的地址
     * @return true-->>输入为地址信息，false-->>输入不为地址信息
     */
    fun AdressCheck(adress: String): Boolean {
        if ("省" in adress && "市" in adress){
            return false
        } else {
            return true
        }
    }
}

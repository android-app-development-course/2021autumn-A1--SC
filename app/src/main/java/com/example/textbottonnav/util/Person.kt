package com.example.textbottonnav.util

import cn.bmob.v3.BmobObject
import cn.bmob.v3.datatype.BmobFile

class         Person() : BmobObject() {

    var ni_cheng : String? = null       //昵称
        get() {
            //如果不是null，返回，否则返回"Null"字符串
            return field?:"NULL"
        }
        set(value) {
            //value为null，赋值成"Null"字符串，否则赋值
            field = value?:"NULL"
        }
    var schoolNum: String? = null          //学号
        get() {
            //如果不是null，返回，否则返回"Null"字符串
            return field?:"NULL"
        }
        set(value) {
            //value为null，赋值成"Null"字符串，否则赋值
            field = value?:"NULL"
        }
    var school:String? = null           //学院
        get() {
            //如果不是null，返回，否则返回"Null"字符串
            return field?:"NULL"
        }
        set(value) {
            //value为null，赋值成"Null"字符串，否则赋值
            field = value?:"NULL"
        }
    var sex:String? = null              //性别
        get() {
            //如果不是null，返回，否则返回"Null"字符串
            return field?:"NULL"
        }
        set(value) {
            //value为null，赋值成"Null"字符串，否则赋值
            field = value?:"NULL"
        }
    var address:String? = null          //地址
        get() {
            //如果不是null，返回，否则返回"Null"字符串
            return field?:"NULL"
        }
        set(value) {
            //value为null，赋值成"Null"字符串，否则赋值
            field = value?:"NULL"
        }

    var username:String? = null           //账号
        get() {
            //如果不是null，返回，否则返回"Null"字符串
            return field?:"NULL"
        }
        set(value) {
            //value为null，赋值成"Null"字符串，否则赋值
            field = value?:"NULL"
        }

    var user_pic:BmobFile?=null
        get(){
            return field
        }
        set(value) {
            field = value
        }

    var collect_goods:ArrayList<String> = ArrayList<String>()
        get(){
            return field
        }
        set(value) {
            field = value
        }

}
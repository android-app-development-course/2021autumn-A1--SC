package com.example.textbottonnav.util

import cn.bmob.v3.BmobObject
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobFile

class Goods: BmobObject() {
    var title:String? = null
        get() {
            //如果不是null，返回，否则返回"Null"字符串
            return field?:"NULL"
        }
        set(value) {
            //value为null，赋值成"Null"字符串，否则赋值
            field = value?:"NULL"
        }

    var description:String? = null
        set(value) {
            field = value?:"NULL"
        }
        get() {
            return field?:"NULL"
        }

    var price:String? = null
        set(value) {
            field = value?:"NULL"
        }
        get() {
            return field?:"NULL"
        }

    var type:String?=null
        set(value) {
            field = value
        }
        get() {
            return field
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
    var user_nicheng:String? = null
        get() {
            //如果不是null，返回，否则返回"Null"字符串
            return field?:"NULL"
        }
        set(value) {
            //value为null，赋值成"Null"字符串，否则赋值
            field = value?:"NULL"
        }
    var onshow:Boolean? = null
        get() {
            //如果不是null，返回，否则返回"Null"字符串
            return (field?: false) as Boolean?
        }
        set(value) {
            //value为null，赋值成"Null"字符串，否则赋值
            field = (value?: false) as Boolean?
        }


    var pic_urls: MutableList<String>? = null
        set(value) {
            field = value
        }
        get() {
            return field
        }

//    var owner: BmobUser? = null
//        get() {
//            return field?:BmobUser()
//        }
//        set(value) {
//            field = value?:BmobUser()
//        }
}
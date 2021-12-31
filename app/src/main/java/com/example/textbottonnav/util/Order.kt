package com.example.textbottonnav.util

import cn.bmob.v3.BmobObject
import cn.bmob.v3.datatype.BmobDate
import java.util.*

class Order:BmobObject() {
    var Goods: Goods? = null
        get() {
            //如果不是null，返回，否则返回空物品对象
            return field?:Goods()
        }
        set(value) {
            //value为null，赋值空物品对象，否则赋值
            field = value?:Goods()
        }

    var buyer:String? = null
        set(value) {
            field = value?:"NULL"
        }
        get() {
            return field?:"NULL"
        }
    var seller:String? = null
        set(value) {
            field = value?:"NULL"
        }
        get() {
            return field?:"NULL"
        }

    var date: BmobDate?=null
        set(value) {
            field = value?:BmobDate(Date())
        }
        get() {
            return field?: BmobDate(Date())
        }
    var price: Number?=null
        set(value) {
            field = value?:0.0
        }
        get() {
            return field?:0.0
        }
    var contact: String?=null
        set(value) {
            field = value?:"NULL"
        }
        get() {
            return field?:"NULL"
        }
    var address: String?=null
        set(value) {
            field = value?:"NULL"
        }
        get() {
            return field?:"NULL"
        }
}
package com.example.textbottonnav

import android.app.Application
import android.content.Context
import android.view.View
import cn.bmob.v3.Bmob

//充当全局变量的角色，在不同的Activity之间传递变量
class MyApplication: Application() {
    companion object{
        lateinit var context:Context
    }

    override fun onCreate() {
        super.onCreate()
        context = baseContext

        //初始化Bomb
        Bmob.initialize(this, "09597e6fcd8ef3f144b2f2dcabf44590");
    }


}
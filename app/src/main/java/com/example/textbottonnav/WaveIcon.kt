package com.example.textbottonnav

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.os.HandlerCompat.postDelayed
import cn.bmob.v3.helper.BmobNative.init

class WaveIcon : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wave_icon)

        Thread(){
            kotlin.run {
                Thread.sleep(2550)
                runOnUiThread(Runnable {
                    run{
                        startNextActivity()
                    }
                })
            }
        }.start()

    }
    fun startNextActivity(){
        var intent:Intent = Intent()
        intent.setClass(this, LoginFragment::class.java)
        startActivity(intent)
        finish()
    }
}
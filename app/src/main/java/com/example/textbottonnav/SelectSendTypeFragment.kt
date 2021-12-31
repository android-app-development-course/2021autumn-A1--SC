package com.example.textbottonnav

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SelectSendTypeFragment: AppCompatActivity() {

    lateinit var fragment_select_sendtype_shiwu:LinearLayout
    lateinit var fragment_select_sendtype_xianzhi:LinearLayout
    lateinit var fragment_select_sendtype_back:FloatingActionButton
    val REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_select_sendtype)

        //设置状态栏文字颜色及图标为深色
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        //绑定控件
        fragment_select_sendtype_shiwu = findViewById(R.id.fragment_select_sendtype_shiwu)
        fragment_select_sendtype_xianzhi = findViewById(R.id.fragment_select_sendtype_xianzhi)
        fragment_select_sendtype_back = findViewById(R.id.fragment_select_sendtype_back)

        //返回按钮
        fragment_select_sendtype_back.setOnClickListener{ finish() }

        //发布闲置
        fragment_select_sendtype_xianzhi.setOnClickListener {
            val intent = Intent(this,SendActivity::class.java)
            startActivityForResult(intent,REQUEST_CODE)
        }

        //发布失物
        fragment_select_sendtype_shiwu.setOnClickListener {
            val intent = Intent(this,SendShiWuFragment::class.java)
            startActivityForResult(intent,REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        finish()        //从发布页面返回后，直接跳过该页面，返回到主页面
    }
}
package com.example.textbottonnav.util

import android.widget.Toast
import com.example.textbottonnav.MyApplication

//封装的工具函数
fun String.showToast(){
    Toast.makeText(MyApplication.context,this,Toast.LENGTH_SHORT).show()
}
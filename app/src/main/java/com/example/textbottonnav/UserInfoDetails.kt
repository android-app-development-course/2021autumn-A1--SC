package com.example.textbottonnav

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.example.textbottonnav.util.Person
import com.example.textbottonnav.util.showToast
import kotlinx.android.synthetic.main.fragment_send_shiwu.*
import kotlinx.android.synthetic.main.fragment_user_info.*


class UserInfoDetails : AppCompatActivity() {
    var person = Person()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_user_info)

        //设置状态栏文字颜色及图标为深色
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        val currentUser = BmobUser.getCurrentUser()
        val username = currentUser.username         //用户账号

        //显示用户账号
        user_account.text = username

        //查询数据库，查看用户的个人信息是否已经创建，查询结果保存在全局变量user_exited中
        query(username)
        info_back_button.setOnClickListener { finish() }

        //保存按钮的点击事件
        save_button.setOnClickListener {
            person.username = username
            person.ni_cheng = ni_cheng_edit.text.toString()
            person.schoolNum = school_num_edit.text.toString()
            person.school = school_edit.text.toString()
            person.sex = sex_edit.text.toString()
            person.address = address_edit.text.toString()

            update_info()
            Thread.sleep(1000)      //等待一秒，等待在“个人信息”里面编辑的数据上传成功
            finish()
        }

    }//end onCreate

    private fun update_info() {
        //根据ObjectId更新数据，如果数据已经存在，那么ObjectId在第一次查询时，就保存到了person中
        person.update(object :UpdateListener(){
            override fun done(p0: BmobException?) {
                if (p0!=null)
                    "更新失败"+p0.message!!.showToast()
            }
        })
    }

    private fun query(username:String)
    {
        val bmobQuery: BmobQuery<Person> = BmobQuery()
        bmobQuery.addWhereEqualTo("username",username)
        bmobQuery.findObjects(object : FindListener<Person>(){
            override fun done(p0: MutableList<Person>?, p1: BmobException?) {
                if(p1==null){
                    person = p0!![0]
                    display()               //显示个人信息
                }
            }
        })
    }

    private fun display()
    {
        if (person.ni_cheng!="NULL"){ ni_cheng_edit.setText(person.ni_cheng) }
        if (person.schoolNum!="NULL"){ school_num_edit.setText(person.schoolNum)}
        if (person.school!="NULL"){ school_edit.setText(person.school)}
        if (person.sex!="NULL"){  sex_edit.setText(person.sex)}
        if (person.address!="NULL"){  address_edit.setText(person.address)}
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        //获取状态栏高度
        val resourcedId = resources.getIdentifier("status_bar_height","dimen","android")
        var top_height = -1
        if(resourcedId > 0){
            top_height = resources.getDimensionPixelSize(resourcedId)   //获取状态栏高度
        }
        //Log.e("查看状态栏高度","状态栏高度:"+top_height)
        //设置第一个view距离状态栏的高度
        val lp = top_bar.getLayoutParams() as LinearLayout.LayoutParams
        lp.topMargin = top_height+20
        top_bar.setLayoutParams(lp)
    }
}
package com.example.textbottonnav

import com.example.textbottonnav.util.ValidateUtil.PhoneCheck
import com.example.textbottonnav.util.ValidateUtil.PasswordCheck
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.BmobSMS
import cn.bmob.v3.listener.UpdateListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.QueryListener
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.listener.FindListener
import com.example.textbottonnav.util.Person
import com.example.textbottonnav.util.showToast
import com.google.android.material.appbar.AppBarLayout
import java.io.File

class RegisterFragment : AppCompatActivity() {

    var AccountText //账号
            : EditText? = null
    var PasswordText //密码
            : EditText? = null
    var Repeat_PasswordText //密码
            : EditText? = null
    var SMS_Code //验证码
            : EditText? = null
    var RegisterButton //注册按钮
            : Button? = null
    var GetCode //获取验证码按钮
            : Button? = null
    lateinit var register_topbar:AppBarLayout
    lateinit var register_back:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_register)

        //设置状态栏文字颜色及图标为深色
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        init()

        //测试用，找bug
//        var person = Person()
//        person.username = "15622180218"
//        person.save(object: SaveListener<String>(){
//            override fun done(p0: String?, p1: BmobException?) {
//                if (p1==null)
//                    "创建成功".showToast()
//                else
//                    "创建失败"+p1.message?.showToast()
//            }
//
//        })

        //获取验证码
        GetCode!!.setOnClickListener {
            //获取客户端输入的账号
            val Account = AccountText!!.text.toString().trim { it <= ' ' }
            val Password = PasswordText!!.text.toString().trim { it <= ' ' }
            val reat_Password = Repeat_PasswordText!!.text.toString().trim { it <= ' ' }
            //isEmpty()方法判断是否为空
            if (TextUtils.isEmpty(Account)) {
                Toast.makeText(this@RegisterFragment, "请填写手机号码", Toast.LENGTH_SHORT).show()
            } else if (PhoneCheck(Account.trim { it <= ' ' }) != true) {
                Toast.makeText(this@RegisterFragment, "请填写正确的手机号码", Toast.LENGTH_SHORT).show()
            }
            if (TextUtils.isEmpty(Password)) {
                Toast.makeText(this@RegisterFragment, "请填写密码", Toast.LENGTH_SHORT).show()
            } else if (Password.length < 6) {
                Toast.makeText(this@RegisterFragment, "密码不得少于6位数", Toast.LENGTH_SHORT).show()
            } else if (Password.length > 16) {
                Toast.makeText(this@RegisterFragment, "密码不得多于16位数", Toast.LENGTH_SHORT).show()
            } else if (PasswordCheck(Password) != true) {
                Toast.makeText(this@RegisterFragment, "密码最少包含3个字母", Toast.LENGTH_SHORT).show()
            } else if (!Password.equals(reat_Password)) {
                Toast.makeText(this@RegisterFragment, "两次密码输入不一致", Toast.LENGTH_SHORT).show()
            } else {
                val bmobQuery = BmobQuery<BmobUser>()
                bmobQuery.findObjects(object : FindListener<BmobUser>() {
                    override fun done(`object`: List<BmobUser>, e: BmobException?) {
                        if (e == null) {
                            var count = 0 //判断是否查询到尾
                            for (user_table in `object`) {
                                if (user_table.username == Account) {
                                    Toast.makeText(this@RegisterFragment, "该账号已注册过", Toast.LENGTH_SHORT)
                                        .show()
                                    break
                                }
                                count++
                            }
                            //查询到尾，说明没有重复账号
                            if (count == `object`.size) {
                                SendSMS(Account)
                            }
                        } else {
                            Log.e("异常",e.toString())
                        }
                    }
                })
            }
        }
        /**
         * 注册
         */
        RegisterButton!!.setOnClickListener {
            //账号(Account)、密码(Password)
            val Account = AccountText!!.text.toString().trim { it <= ' ' }
            val Password = PasswordText!!.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(Password)) {
                Toast.makeText(this@RegisterFragment, "请填写密码", Toast.LENGTH_SHORT).show()
            } else if (Password.length < 6) {
                Toast.makeText(this@RegisterFragment, "密码不得少于6位数", Toast.LENGTH_SHORT).show()
            } else if (Password.length > 16) {
                Toast.makeText(this@RegisterFragment, "密码不得多于16位数", Toast.LENGTH_SHORT).show()
            } else if (PasswordCheck(Password) != true) {
                Toast.makeText(this@RegisterFragment, "密码最少包含3个字母", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(SMS_Code!!.text.toString().trim { it <= ' ' })) {
                Toast.makeText(this@RegisterFragment, "请填写验证码", Toast.LENGTH_SHORT).show()
            } else {
                //短信验证码效验
                val code = SMS_Code!!.text.toString().trim { it <= ' ' }
                BmobSMS.verifySmsCode(
                    Account,
                    code,
                    object : UpdateListener() {
                        override fun done(e: BmobException?) {
                            if (e == null) {

                                //先创建个人信息Person记录，再进行User注册，因为signUp函数执行完会直接kill进程，所以需要先等Person注册完之后，再注册User
                                var person = Person()
                                person.username = Account
                                person.save(object: SaveListener<String>(){
                                    override fun done(p0: String?, p1: BmobException?) {
                                        if (p1!=null){
                                            "创建个人信息失败"+p1.message?.showToast()
                                        }
                                        else{
                                            //将用户信息存储到Bmob云端数据
                                            val user = BmobUser()
                                            user.username = Account
                                            user.setPassword(Password)

                                            user.signUp(object : SaveListener<String>() {
                                                override fun done(s: String, e: BmobException?) {
                                                    if (e == null) {
                                                        //注册成功，回到登录页面
                                                        Toast.makeText(
                                                            this@RegisterFragment,
                                                            "注册成功",
                                                            Toast.LENGTH_SHORT
                                                        ).show()

                                                    } else {
                                                        Toast.makeText(
                                                            this@RegisterFragment,
                                                            "注册失败",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            })
                                        }
                                    }
                                })


                            } else {
                                SMS_Code!!.setText("")
                                Log.e("异常",e.toString())
                                Toast.makeText(
                                    this@RegisterFragment,
                                    "验证码错误" + e.errorCode,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    })
            }
        }

        //返回登陆界面
        register_back.setOnClickListener { finish() }
    }

    private fun register_person(username:String)
    {
        val person = Person()
        person.username = username
        Log.e("执行","person.save")
        person.save(object: SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if (p1!=null){
                    "创建个人信息失败"+p1.message?.showToast()
                }
            }
        })
    }

    fun init() {
        //注册标题(Title)、账号(Account)、密码(Password)、验证码(SMS_Code)
        AccountText = findViewById(R.id.user_phone)
        PasswordText = findViewById(R.id.validatecode)
        Repeat_PasswordText = findViewById(R.id.repeat_password)
        SMS_Code = findViewById(R.id.validateCode)

        //回到登录按钮(Login)、注册按钮(Register)、验证码获取按钮(GetCode)
        GetCode = findViewById(R.id.sendValidateCode)
        RegisterButton = findViewById(R.id.register)
        register_topbar = findViewById(R.id.register_topbar)
        register_back = findViewById(R.id.register_back)
    }

    /**
     * 发送验证码
     * @param account：输入的手机号码
     * SMS 为Bmob短信服务自定义的短信模板名字
     */
    private fun SendSMS(account: String) {
        BmobSMS.requestSMSCode(account, "", object : QueryListener<Int>() {
            override fun done(smsId: Int, e: BmobException?) {
                if (e == null) {
                    Toast.makeText(this@RegisterFragment, "验证码已发送", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(
                        this@RegisterFragment,
                        "发送验证码失败：" + e.errorCode + "-" + e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
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
        val lp = register_topbar.getLayoutParams() as ConstraintLayout.LayoutParams
        lp.topMargin = top_height
        register_topbar.setLayoutParams(lp)
    }
}
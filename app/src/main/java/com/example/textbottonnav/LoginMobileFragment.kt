package com.example.textbottonnav

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import cn.bmob.v3.Bmob
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobSMS
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.QueryListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.example.textbottonnav.util.ValidateUtil
import com.google.android.material.appbar.AppBarLayout

class LoginMobileFragment : AppCompatActivity() {

    var AccountText: EditText? = null    //账号
    var SMS_Code: EditText? = null   //验证码
    var Login: Button? = null   //登录按钮
    var GetCode: Button? = null     //获取验证码按钮
    lateinit var login_mobile_topbar:AppBarLayout
    lateinit var login_mobile_back:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_mobile)
        Bmob.initialize(this@LoginMobileFragment, "你的Application ID（Bmob设置里可以看到）")
        init()

        //设置状态栏文字颜色及图标为深色
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        //获取验证码
        GetCode!!.setOnClickListener {
            //获取客户端输入的账号
            val Account = AccountText!!.text.toString().trim { it <= ' ' }
            //isEmpty()方法判断是否为空
            if (TextUtils.isEmpty(Account)) {
                Toast.makeText(this@LoginMobileFragment, "请填写手机号码", Toast.LENGTH_SHORT).show()
            } else if (ValidateUtil.PhoneCheck(Account.trim { it <= ' ' }) != true) {
                Toast.makeText(this@LoginMobileFragment, "请填写正确的手机号码", Toast.LENGTH_SHORT).show()
            } else {
                val bmobQuery = BmobQuery<BmobUser>()
                bmobQuery.findObjects(object : FindListener<BmobUser>() {
                    override fun done(`object`: List<BmobUser>, e: BmobException?) {
                        if (e == null) {
                            var count = 0 //判断是否查询到尾
                            for (user_table in `object`) {
                                if (user_table.username == Account) {
                                    SendSMS(Account)
                                    Toast.makeText(this@LoginMobileFragment, "已发送验证码", Toast.LENGTH_SHORT)
                                        .show()
                                    break
                                }
                                count++
                            }
                            //查询到尾，说明没有重复账号
                            if (count == `object`.size) {
                                Toast.makeText(this@LoginMobileFragment, "该手机号尚未注册", Toast.LENGTH_SHORT)
                                    .show()
                                val intent = Intent(this@LoginMobileFragment, RegisterFragment::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Log.e("异常",e.toString())
                            SendSMS(Account)
                        }
                    }
                })
            }
        }

        /**
         * 登录
         */
        Login!!.setOnClickListener{
            //获取客户端输入的账号
            val Account = AccountText!!.text.toString().trim { it <= ' ' }
            //isEmpty()方法判断是否为空
            if (TextUtils.isEmpty(Account)) {
                Toast.makeText(this@LoginMobileFragment, "请填写手机号码", Toast.LENGTH_SHORT).show()
            } else if (ValidateUtil.PhoneCheck(Account.trim { it <= ' ' }) != true) {
                Toast.makeText(this@LoginMobileFragment, "请填写正确的手机号码", Toast.LENGTH_SHORT).show()
            } else {
                //短信验证码效验
                val code = SMS_Code!!.text.toString().trim { it <= ' ' }
                BmobSMS.verifySmsCode(Account, code, object : UpdateListener() {
                    override fun done(e: BmobException?) {
                        if (e == null) {
                            //登录成功，回到登录页面
                            Toast.makeText(this@LoginMobileFragment, "登录成功", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@LoginMobileFragment, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            SMS_Code!!.setText("")
                            Log.e("异常",e.toString())
                            Toast.makeText(this@LoginMobileFragment, "验证码错误" + e.errorCode, Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }

        login_mobile_back.setOnClickListener { finish() }
    }


    fun init() {
        //注册标题(Title)、账号(Account)、密码(Password)、验证码(SMS_Code)
        AccountText = findViewById(R.id.username)
        SMS_Code = findViewById(R.id.validatecode)
        GetCode = findViewById(R.id.send_button)
        login_mobile_topbar=findViewById(R.id.login_mobile_topbar)
        login_mobile_back = findViewById(R.id.login_mobile_back)

        //登录按钮、忘记密码
        Login = findViewById(R.id.login)
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
                    Toast.makeText(this@LoginMobileFragment, "验证码已发送", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(
                        this@LoginMobileFragment,
                        "发送验证码失败：" + e.errorCode + "-" + e.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
        //设置按钮60s等待点击
        val timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                GetCode!!.isEnabled = false
                GetCode!!.text = "重新获取(" + millisUntilFinished / 1000 + "s)"
            }

            override fun onFinish() {
                GetCode!!.isEnabled = true
                GetCode!!.text = "获取验证码"
            }
        }.start()
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
        val lp = login_mobile_topbar.getLayoutParams() as ConstraintLayout.LayoutParams
        lp.topMargin = top_height
        login_mobile_topbar.setLayoutParams(lp)
    }
}
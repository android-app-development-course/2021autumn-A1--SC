package com.example.textbottonnav

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import cn.bmob.v3.Bmob
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobSMS
import cn.bmob.v3.BmobUser
import com.example.textbottonnav.util.ValidateUtil.PhoneCheck
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.QueryListener
import cn.bmob.v3.listener.UpdateListener
import com.example.textbottonnav.util.ValidateUtil.PasswordCheck
import com.google.android.material.appbar.AppBarLayout

class ForgetpsdFragment : AppCompatActivity() {
    var AccountText: EditText? = null
    var PasswordText: EditText? = null
    var SMS_Code: EditText? = null
    var ModifyButton: Button? = null
    var GetCode: Button? = null
    lateinit var forgetpassword_back:ImageView
    lateinit var forgetpassword_topbar:AppBarLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.forgetpassword)

        //设置状态栏文字颜色及图标为深色
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        Bmob.initialize(this@ForgetpsdFragment, "你的Application ID（Bmob设置里可以看到）")
        init()
        /**
         * 获取验证码
         */
        GetCode!!.setOnClickListener {
            //获取账号(Account)
            val Account = AccountText!!.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(Account)) {
                Toast.makeText(this@ForgetpsdFragment, "请填写账号", Toast.LENGTH_SHORT).show()
            } else if (PhoneCheck(Account) != true) {
                Toast.makeText(this@ForgetpsdFragment, "请填写正确的手机号码", Toast.LENGTH_SHORT).show()
            } else {
                val bmobQuery: BmobQuery<BmobUser> = BmobQuery<BmobUser>()
                bmobQuery.findObjects(object : FindListener<BmobUser?>() {
                    override fun done(`object`: List<BmobUser?>, e: BmobException?) {
                        if (e == null) {
                            var count = 0
                            for (user_table in `object`) {
                                //检查是否存在该账号
                                if (user_table?.username.equals(Account)) {
                                    SendSMS(Account)
                                    break
                                }
                                count++
                            }
                            if (count >= `object`.size) {
                                Toast.makeText(this@ForgetpsdFragment, "该账户不存在", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            Toast.makeText(this@ForgetpsdFragment, "该账户不存在", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }
        /**
         * 修改密码
         */
        ModifyButton!!.setOnClickListener {
            //获取输入的账号(Account)、密码(Password)、验证码(Code)
            val Account = AccountText!!.text.toString().trim { it <= ' ' }
            val Password = PasswordText!!.text.toString().trim { it <= ' ' }
            val Code = SMS_Code!!.text.toString().trim { it <= ' ' }
            if (TextUtils.isEmpty(Account)) {
                Toast.makeText(this@ForgetpsdFragment, "请填写账号", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(Code)) {
                Toast.makeText(this@ForgetpsdFragment, "请填写验证码", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(Password)) {
                Toast.makeText(this@ForgetpsdFragment, "请填写密码", Toast.LENGTH_SHORT).show()
            } else if (Password.length < 6) {
                Toast.makeText(this@ForgetpsdFragment, "密码不得少于6位数", Toast.LENGTH_SHORT).show()
            } else if (Password.length > 16) {
                Toast.makeText(this@ForgetpsdFragment, "密码不得多于16位数", Toast.LENGTH_SHORT).show()
            } else if (PasswordCheck(Password) != true) {
                Toast.makeText(this@ForgetpsdFragment, "密码最少包含3个字母", Toast.LENGTH_SHORT).show()
            } else {
                //发送验证码
                BmobSMS.verifySmsCode(Account, Code, object : UpdateListener() {
                    override fun done(e: BmobException?) {
                        if (e == null) {
                            val bmobQuery: BmobQuery<BmobUser> = BmobQuery<BmobUser>()
                            bmobQuery.findObjects(object : FindListener<BmobUser?>() {
                                override fun done(`object`: List<BmobUser?>, e: BmobException?) {
                                    if (e == null) {
                                        for (user_table in `object`) {
                                            if (user_table?.username.equals(Account)) {
                                                //Bmob云端数据更新
                                                val user = BmobUser()
                                                user.setPassword(Password)
                                                user.update(
                                                    user_table?.getObjectId(),
                                                    object : UpdateListener() {
                                                        override fun done(e: BmobException?) {
                                                            if (e == null) {
                                                                Toast.makeText(this@ForgetpsdFragment, "密码修改成功", Toast.LENGTH_SHORT).show()
                                                            } else {
                                                                Toast.makeText(this@ForgetpsdFragment, "修改失败错误代码：${e.errorCode}".trimIndent(), Toast.LENGTH_LONG).show()
                                                            }
                                                        }
                                                    })
                                                break
                                            }
                                        }
                                    } else {
                                        Toast.makeText(this@ForgetpsdFragment, "该账号不存在", Toast.LENGTH_LONG).show()
                                    }
                                }
                            })
                            finish()
                        } else {
                            Toast.makeText(this@ForgetpsdFragment, "验证码错误", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }
        }

        forgetpassword_back.setOnClickListener { finish() }

    }

    private fun init() {
        //账号、密码、验证码
        AccountText = findViewById(R.id.username)
        PasswordText = findViewById(R.id.newpassword)
        SMS_Code = findViewById(R.id.validatecode)

        //回到登录按钮、获取验证码按钮、修改密码按钮
        GetCode = findViewById(R.id.send_button)
        ModifyButton = findViewById(R.id.login)
        forgetpassword_back = findViewById(R.id.forgetpassword_back)
        forgetpassword_topbar = findViewById(R.id.forgetpassword_topbar)
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
                    Toast.makeText(this@ForgetpsdFragment, "验证码已发送", Toast.LENGTH_LONG).show()
                } else {
                    Log.e("异常", e.toString())
                    Toast.makeText(
                        this@ForgetpsdFragment,
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
        val lp =  forgetpassword_topbar.getLayoutParams() as ConstraintLayout.LayoutParams
        lp.topMargin = top_height
        forgetpassword_topbar.setLayoutParams(lp)
    }
}

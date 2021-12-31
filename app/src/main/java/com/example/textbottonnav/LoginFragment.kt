package com.example.textbottonnav

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import com.example.textbottonnav.util.ValidateUtil.ValidateEmpty
import com.example.textbottonnav.util.showToast
import kotlinx.android.synthetic.main.fragment_login.*


class LoginFragment:AppCompatActivity() {

    lateinit var login:Button //登陆
    lateinit var register:TextView  //注册新用户
    lateinit var forget_password:TextView   //忘记密码
    var Username:EditText? = null  //手机号
    var Password:EditText? = null  //密码
    lateinit var Login_mobile:TextView  //手机号短信登录

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_login);
        init()
        //设置状态栏文字颜色及图标为深色
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        // 已登录状态
        val prefs = getSharedPreferences("data",Context.MODE_PRIVATE)
        val isLoginin = prefs.getBoolean("isLogin", false)
        if (isLoginin) {
            val intent = Intent(this@LoginFragment, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        val account = prefs.getString("account", "")
        Username!!.setText(account)
        // 判断记住密码
        val isRemember = prefs.getBoolean("remember_password", false)
        if (isRemember) {
            val psd = prefs.getString("password", "")
            Password!!.setText(psd)
            rememberPsd.isChecked = true
        }

        //获取布局中的控件
        login = findViewById(R.id.login)
        register = findViewById(R.id.register)
        forget_password = findViewById(R.id.forget_password)
        Username = findViewById(R.id.username)
        Password = findViewById(R.id.validatecode)
        Login_mobile = findViewById(R.id.login_mobile)

        //设置登陆按钮点击事件
        login.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                //验证输入的账号密码
                var input_username = Username!!.text.toString()
                var input_password = Password!!.text.toString()

                if (ValidateEmpty(input_username.toString())){
                    "用户名不能为空".showToast()
                } else if (ValidateEmpty(input_password.toString())) {
                    "密码不能为空".showToast()
                } else {
                    var user = BmobUser()
                    user.username = input_username
                    user.setPassword(input_password)
                    user.login(object : SaveListener<BmobUser>(){
                        override fun done(currentUser: BmobUser?, ex: BmobException?) {
                            if (ex == null){
                                "登录成功".showToast()
                                startActivity(Intent(MyApplication.context, MainActivity::class.java))
                                // 将登录信息保存到SharedPreferences中
                                val editor = prefs.edit()
                                if (rememberPsd.isChecked) {
                                    editor.putBoolean("remember_password", true)
                                    editor.putString("password", input_password)
                                } else {
                                    editor.clear()
                                }
                                editor.putString("account", input_username)
                                editor.putBoolean("isLogin", true)
                                editor.apply()
                                finish()
                            } else {
                                Toast.makeText(MyApplication.context, ex.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
                }
            }
        })

        //设置注册新用户点击事件
        register.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?){
                //跳转到注册页面
                val intent = Intent(this@LoginFragment, RegisterFragment::class.java)
                startActivity(intent)
            }
        })

        // 设置用户手机号登录界面
        Login_mobile.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?){
                //跳转到手机号登录页面
                val intent = Intent(this@LoginFragment, LoginMobileFragment::class.java)
                startActivity(intent)
            }
        })

        // 跳转忘记密码界面
        forget_password.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?){
                //跳转到手机号登录页面
                val intent = Intent(this@LoginFragment, ForgetpsdFragment::class.java)
                startActivity(intent)
            }
        })
    }

    fun init(){
        Username = findViewById(R.id.username)
        Password = findViewById(R.id.validatecode)
    }
}
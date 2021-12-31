package com.example.textbottonnav

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.UpdateListener
import cn.bmob.v3.listener.UploadFileListener
import com.bumptech.glide.Glide
import com.example.textbottonnav.util.Person
import com.example.textbottonnav.util.showToast
import kotlinx.android.synthetic.main.fragment_my.*
import java.io.File

class MyFragment():Fragment() {

    var cur_person = Person()     //记录当前用户的个人信息
    var cur_username:String=""    //手机号
    val INFO_EDIT_CODE = 1        //编辑个人信息活动，返回的值
    val IMAGE_REQUEST_CODE = 2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my,container,false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //显示顶部信息（昵称+账号）
        cur_username = BmobUser.getCurrentUser().username
        user_name_view.text = cur_username
        seek_cur_person()     //搜索并显示昵称

        //更换头像
        user_pic_view.setOnClickListener {
            //设计功能，从系统相册选择一张照片，上传person表，上传成功后更新UI
            change_user_pic()
        }

        //“个人信息”编辑按钮
        user_info_id.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?){
                //跳转到个人信息页面
                val intent :Intent=Intent(activity, UserInfoDetails::class.java)
                startActivityForResult(intent,INFO_EDIT_CODE)
            }
        })

        //“收藏”编辑按钮
        collect_btn.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?){
                //跳转到个人信息页面
                val intent :Intent=Intent(activity, CollectActivity::class.java)
                startActivity(intent)
            }
        })

        // 点击订单页面
        my_order.setOnClickListener {
            val intent = Intent(activity, OrderListActivity::class.java)
            startActivity(intent)
        }

        //退出登陆按钮
        logout_button.setOnClickListener {
            val prefs = getActivity()?.getSharedPreferences("data", Context.MODE_PRIVATE)
            val editor = prefs?.edit()
            editor?.putBoolean("isLogin", false)
            editor?.apply()

            val intent = Intent(MyApplication.context,LoginFragment::class.java)
            startActivity(intent)

            activity?.finish()
        }
    }//end onActivityCreated

    private fun seek_cur_person(){
        //根据手机号，搜索并显示昵称信息
        var bmobQuery: BmobQuery<Person> = BmobQuery()
        bmobQuery.addWhereEqualTo("username",cur_username)
        bmobQuery.findObjects(object : FindListener<Person>(){
            override fun done(p0: MutableList<Person>?, p1: BmobException?) {
                if (p1==null && p0!!.size>0){
                    cur_person = p0[0]
                    display()
                }
            }
        })
    }

    private fun change_user_pic()
    {
        //从相册选择原生照片，跳转到手机系统相册里面
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)   //
        startActivityForResult(intent,IMAGE_REQUEST_CODE)   //参数二：自定义的int类型变量，从activityB中返回来的时候。会携带回来，可以用这个参数来判断是从哪个activity中返回的
    }

    private fun display()
    {
        if(cur_person.ni_cheng!="NULL")
            ni_cheng_view.setText(cur_person.ni_cheng)
        if (cur_person.user_pic != null) {
            Glide.with(this).load(cur_person.user_pic!!.url).into(user_pic_view)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            INFO_EDIT_CODE -> {
                //重新查询昵称，并刷新UI
                seek_cur_person()
            }
            IMAGE_REQUEST_CODE -> if (resultCode == RESULT_OK){
                val selectedImage: Uri = data?.data!! //获取系统返回的照片的Uri
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val cursor: Cursor? = MyApplication.context.contentResolver.query(
                    selectedImage!!,
                    filePathColumn, null, null, null
                ) //从系统表中查询指定Uri对应的照片
                cursor?.moveToFirst()
                val columnIndex: Int = cursor!!.getColumnIndex(filePathColumn[0])
                val user_pic_path = cursor!!.getString(columnIndex) //获取照片路径
                cursor?.close()

                val file = File(user_pic_path)
                val bmobFile = BmobFile(file)
                bmobFile.upload(object : UploadFileListener(){
                    override fun done(p0: BmobException?) {
                        cur_person.user_pic = bmobFile
                        cur_person.update(object :UpdateListener(){
                            override fun done(p0: BmobException?) {
                                if(p0!=null)
                                    "头像更新失败"+p0.message?.showToast()
                                seek_cur_person()
                            }
                        })
                    }
                })
            }
        }
    }

}// end MyFragment()

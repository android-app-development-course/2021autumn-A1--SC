package com.example.textbottonnav

import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobFile
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UploadBatchListener
import com.example.textbottonnav.util.Goods
import com.example.textbottonnav.util.showToast
import kotlinx.android.synthetic.main.fragment_send_shiwu.*
import kotlinx.android.synthetic.main.pic_card.*
import java.lang.Exception

class SendShiWuFragment: AppCompatActivity() {

    lateinit var mLayoutManager:RecyclerView.LayoutManager
    var IMAGE_REQUEST_CODE = 1              //从系统相册返回的记录值
    var pic_uri_list = ArrayList<Uri>()
    var pic_path_list = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_send_shiwu)

        //设置状态栏文字颜色及图标为深色
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        //获取/drawable/add_bold的uri
        init()
        //取消按钮
        back_button.setOnClickListener { finish() }

        //设置RecyclerView为三列瀑布流布局
        val spanCount = 3
        mLayoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
        select_pic.layoutManager = mLayoutManager

        //绑定视图的数据源
        select_pic.adapter = PicAdapter(pic_uri_list)

        //发布按钮
        send_button.setOnClickListener {

            //E/照片路径: /storage/emulated/0/DCIM/Camera/IMG_20211224_110812.jpg
            //E/照片路径: /storage/emulated/0/DCIM/Camera/IMG_20211224_110749.jpg

            //将ArrayList转为Array
            val pre_pic_path_list = pic_path_list.toArray(arrayOfNulls<String>(pic_path_list.size))
            //去除Array中的空值
            var non_null_num = 0
            for (p in  pre_pic_path_list)
                p?.let { if(p!=""){ non_null_num++ } }
            val real_pic_path_list =  arrayOfNulls<String>(non_null_num)
            var iter=0
            for (p in  pre_pic_path_list)
                p?.let { if (p!=""){real_pic_path_list[iter++] = p}}
//            Log.e("去空前",pre_pic_path_list.size.toString())
//            Log.e("去空后",real_pic_path_list.size.toString())

            //上传多张照片
            BmobFile.uploadBatch(real_pic_path_list ,object : UploadBatchListener {
                override fun onSuccess(p0: MutableList<BmobFile>?, p1: MutableList<String>?) {
                    if (p1 != null) {
                        if (p1.size==real_pic_path_list.size) {
                            //上传文字信息，并绑定照片
                            val goods = Goods()
                            goods.type = "失物"
                            goods.title = title_edit.text.toString()
                            goods.description = describe_edit.text.toString()
                            goods.username=BmobUser.getCurrentUser().username
                            goods.onshow=true
                            goods.addAllUnique("pic_urls",p1)               //绑定图片的url列表
                            goods.save(object :SaveListener<String>(){
                                override fun done(p0: String?, p1: BmobException?) {
                                    if (p1!=null){
                                        ("创建数据失败：" + p1.message).showToast()
                                    }
                                }
                            })
                        }
                    }
                }

                override fun onProgress(p0: Int, p1: Int, p2: Int, p3: Int) {
                    //"上传进度：$p0".showToast()
                }

                override fun onError(p0: Int, p1: String?) {
                    "上传出错：$p1".showToast()
                }

            })
            finish()        //上传完毕之后，返回主界面
        }
    }

    private fun init()
    {
        //获取/drawable/add_bold.png的Uri
        val add_bold_uri = Uri.parse("android.resource://"+packageName+R.drawable.add_bold)

        pic_uri_list.add(add_bold_uri)
        pic_path_list.add("")
    }

    inner class PicAdapter(val pic_url_list:ArrayList<Uri>): RecyclerView.Adapter<PicViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.pic_card,parent,false)  //实例化布局加载器对象,并加载它的方法
            return PicViewHolder(itemView)
        }

        //绑定数据和视图
        override fun onBindViewHolder(holder: PicViewHolder, position: Int) {
            val uri = pic_url_list[position]
            if (uri.toString().startsWith("content://"))
            {
                val resolver: ContentResolver = MyApplication.context.contentResolver
                val bitmap = MediaStore.Images.Media.getBitmap(resolver,uri)                //问题出现在这句！！！！！！！！
                holder.pic.setImageBitmap(bitmap)
            }

            //设置点击事件
            holder.pic.setOnClickListener {
                //获取到选择的图片的uri，替换掉原来的uri
                //从相册选择原生照片，跳转到手机系统相册里面
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)   //
                startActivityForResult(intent,IMAGE_REQUEST_CODE)   //参数二：自定义的int类型变量，从activityB中返回来的时候。会携带回来，可以用这个参数来判断是从哪个activity中返回的
            }

        }

        override fun getItemCount(): Int {
            return pic_url_list.size
        }
    }

    //绑定视图中的控件到适配器中的变量
    inner class PicViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val pic:ImageView = itemView.findViewById(R.id.pic)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //在相册里面选择好相片之后调回到现在的这个activity中
        when (requestCode) {
            IMAGE_REQUEST_CODE -> if (resultCode == RESULT_OK) { //resultcode是setResult里面设置的code值，用来判断有没有返回成功
                try {
                    val selectedImage: Uri = data?.data!! //获取系统返回的照片的Uri
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor: Cursor? = contentResolver.query(
                        selectedImage!!,
                        filePathColumn, null, null, null
                    ) //从系统表中查询指定Uri对应的照片
                    cursor?.moveToFirst()
                    val columnIndex: Int = cursor!!.getColumnIndex(filePathColumn[0])
                    val path = cursor!!.getString(columnIndex) //获取照片路径
                    cursor?.close()

                    if (isFull()) {
                        pic_uri_list[pic_uri_list.size - 1] = selectedImage
                        pic_path_list[pic_path_list.size - 1] = path
                    }
                    else{
                        pic_uri_list.add(pic_uri_list.size-1,selectedImage)
                        pic_path_list.add(pic_path_list.size-1,path)
                    }

                    select_pic.adapter?.notifyDataSetChanged()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
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
        val lp = appBarLayout.getLayoutParams() as ConstraintLayout.LayoutParams
        lp.topMargin = top_height+10
        appBarLayout.setLayoutParams(lp)
    }

    //判断pic_uri_list容量是否达到6个
    private fun isFull():Boolean
    {
        if(pic_uri_list.size == 6)
            return true
        return false
    }
}
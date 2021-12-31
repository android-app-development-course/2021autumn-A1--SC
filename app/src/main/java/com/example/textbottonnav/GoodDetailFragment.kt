package com.example.textbottonnav

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.QueryListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.bumptech.glide.Glide
import com.example.textbottonnav.util.Goods
import com.example.textbottonnav.util.Person
import com.example.textbottonnav.util.showToast
import kotlinx.android.synthetic.main.fragment_goods_detail.*
import kotlinx.android.synthetic.main.fragment_my.*
import kotlinx.android.synthetic.main.goods_detail_pic_card.*

class GoodDetailFragment(): AppCompatActivity() {


    var current_goods = Goods()
    var goods_pic_urls = ArrayList<String>()
    var goods_type = ""
    var cur_username = ""
    var cur_person = Person()

    var person = Person()
    var IS_COLLECT = false   //是否收藏了该商品
    var user_exited = false   //用户个人信息是否已存在
    var collect_list:ArrayList<String> = ArrayList<String>()    //收藏列表

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_goods_detail)

        //设置状态栏文字颜色及图标为深色
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        //返回按钮
        goods_detail_back.setOnClickListener {
            finish()
        }

        //获取当前用户账号
        val currentUser = BmobUser.getCurrentUser()
        val username = currentUser.username

        //接收从FragmentGoods传递过来的ObjectId
        val intent = getIntent()
        val goods_objectId = intent.getStringExtra("Id")
        goods_type = intent.getStringExtra("Type")!!

        //查询数据库，查看用户的个人信息是否已经创建，若创建则获取收藏列表
        if (goods_objectId != null) {
            query(username, goods_objectId)
        }


        //”收藏“按钮
        goods_detail_bottom_star.setOnClickListener {
            //收藏
            if(IS_COLLECT==false){
                IS_COLLECT = true
                //星星图标切换成有颜色的
                goods_detail_bottom_star.setImageResource(R.drawable.star_bling)

                //在收藏列表中添加该商品id
                collect_list.add(goods_objectId!!)
                person.collect_goods = collect_list

                Log.e("user_exited",user_exited.toString())
                //用户信息存在
                if(user_exited){
                    //更新Person数据库
                    update_info()
                    Log.e("点击","update_info()")
                }
                //用户信息不存在
                else{
                    save_info()
                    Log.e("点击","save_info()")
                }
            }
            //取消收藏
            else if(IS_COLLECT==true){
                IS_COLLECT = false
                //星星图标切换成无颜色的
                goods_detail_bottom_star.setImageResource(R.drawable.star)

                //从收藏列表中删除该商品的id
                collect_list.remove(goods_objectId)
                //更新Person数据库
                person.collect_goods = collect_list
                update_info()
            }
        }

        //“我想要”按钮
        goods_detail_buy.setOnClickListener {
            val intent = Intent(this@GoodDetailFragment,OrderActivity::class.java)
            intent.putExtra("objectID", current_goods.objectId)
            intent.putExtra("goods_name",current_goods.title)
            intent.putExtra("price",current_goods.price)
            if (current_goods.pic_urls!!.size>0)
                intent.putExtra("pic_url",current_goods.pic_urls!![0])
            startActivity(intent)
        }

        goods_detail_pic_view.layoutManager = LinearLayoutManager(MyApplication.context)    //为显示照片的RecyclerView控件设置线性布局
        goods_detail_pic_view.adapter = GoodsDetailPicAdapter(goods_pic_urls)       //绑定视图的数据源

        //接收从FragmentGoods传递过来的ObjectId
//        val intent = getIntent()
//        val goods_objectId = intent.getStringExtra("Id")

        //根据ObjectId查询数据库内容，并显示到视图中
        val bmobQuery: BmobQuery<Goods> = BmobQuery()
        bmobQuery.getObject(goods_objectId , object : QueryListener<Goods>() {
            override fun done(goods: Goods?, ex: BmobException?) {
                if (ex == null) {
                    if (goods != null) {
                        current_goods = goods
                        cur_username = goods.username!!
                        //goods_pic_urls = goods.pic_urls   这种赋值方式是不对的，会导致notifyDataSetChanged()失效！！！！！！
                        //refresh()         //刷新数据和UI
                        seek_ni_cheng()     //先查出昵称，再刷新UI
                    }
                } else {
                    ex.message?.showToast()
                }
            }
        })
    }

    private fun refresh()
    {
        //加载头像
        if (cur_person.user_pic!=null)
            Glide.with(this).load(cur_person.user_pic!!.url+"!/fwfh/50x50").into(goods_detail_user_pic)

        //加载昵称
        if (cur_person.ni_cheng!="NULL")
            goods_detail_nicheng.setText(cur_person.ni_cheng)

        //如果是闲置，显示价格信息
        if (goods_type == "闲置")
            goods_detail_goods_price.setText("￥"+current_goods.price)

        //显示描述信息
        goods_detail_goods_description.setText(current_goods.description)
        goods_pic_urls.clear()

        //更新图片列表
        for (u in current_goods.pic_urls!!)
            goods_pic_urls.add(u)

        //刷新图片UI
        goods_detail_pic_view.adapter?.notifyDataSetChanged()
    }

    private fun seek_ni_cheng(){
        //根据手机号，搜索并显示昵称信息
        val bmobQuery: BmobQuery<Person> = BmobQuery()
        bmobQuery.addWhereEqualTo("username",cur_username)
        bmobQuery.findObjects(object : FindListener<Person>(){
            override fun done(p0: MutableList<Person>?, p1: BmobException?) {
                if (p1==null && p0!!.size>0){
                    cur_person = p0[0]
                }
                refresh()
            }
        })
    }


    inner class GoodsDetailPicAdapter(val pic_urls:ArrayList<String>): RecyclerView.Adapter<GoodsDetailPicViewHolder>(){
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): GoodsDetailPicViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.goods_detail_pic_card,parent,false)
            return GoodsDetailPicViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: GoodsDetailPicViewHolder, position: Int) {
            Glide.with(this@GoodDetailFragment).load(pic_urls[position]+"!/format/webp").into(holder.pic_view)
        }

        override fun getItemCount(): Int {
            return pic_urls.size
        }
    }

    //绑定视图中的控件到适配器中的变量
    inner class GoodsDetailPicViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val pic_view:ImageView = itemView.findViewById(R.id.goods_detail_goods_pic)
    }

    //动态设置view顶部外边距，解决页面被状态栏遮盖的wenti
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
        val lp = goods_detail_topbar.getLayoutParams() as RelativeLayout.LayoutParams
        lp.topMargin = top_height+20
        goods_detail_topbar.setLayoutParams(lp)
    }

    private fun query(username:String,objectId:String)
    {
        val bmobQuery: BmobQuery<Person> = BmobQuery()
        bmobQuery.addWhereEqualTo("username",username)
        bmobQuery.findObjects(object : FindListener<Person>(){
            override fun done(p0: MutableList<Person>?, p1: BmobException?) {
                if(p1==null){
                    if (p0!!.size>0){           //如果p0的大小大于0，说明改用户的个人信息存在
                        person = p0!![0]
                        user_exited = true      //用户的个人信息已经存在了
                        Log.e("query",user_exited.toString())
                        collect_list = person.collect_goods     //获取用户的收藏列表
                        IS_COLLECT = collect_list!!.contains(objectId)
                    }
                    else{
                        user_exited = false     //否则，返回的记录个数为0，说明用户的个人信息不存在
                        IS_COLLECT = false
                    }
                    //收藏图标
                    collect_stat(IS_COLLECT)
                }
            }
        })
    }

    private fun update_info() {
        //根据ObjectId更新数据，如果数据已经存在，那么ObjectId在第一次查询时，就保存到了person中
        person.update(object : UpdateListener(){
            override fun done(p0: BmobException?) {
                if (p0!=null)
                    "更新失败"+p0.message!!.showToast()
            }
        })
    }

    private fun save_info() {
        person.save(object : SaveListener<String>(){
            override fun done(p0: String?, p1: BmobException?) {
                if (p1 !=null)
                    "保存信息失败"+p1.message!!.showToast()
            }
        })
    }

    private fun collect_stat(IS_COLLECT:Boolean){
        if(IS_COLLECT){
            //星星图标切换成有颜色的
            goods_detail_bottom_star.setImageResource(R.drawable.star_bling)
        }
        else{
            //星星图标切换成无颜色的
            goods_detail_bottom_star.setImageResource(R.drawable.star)
        }
    }
}
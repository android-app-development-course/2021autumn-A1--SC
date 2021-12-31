package com.example.textbottonnav

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.bumptech.glide.Glide
import com.example.textbottonnav.MyApplication.Companion.context
import com.example.textbottonnav.util.Goods
import com.example.textbottonnav.util.Person
import kotlinx.android.synthetic.main.activity_collect.*
import kotlinx.android.synthetic.main.collect_details.*
import kotlinx.android.synthetic.main.fragment_goods.*
import kotlinx.android.synthetic.main.fragment_goods_detail.*
import kotlin.concurrent.thread

class CollectActivity : AppCompatActivity() {

    var goods = Goods()
    var person = Person()
    var collect_list_isnull = true                                  //是否收藏有商品
    var collect_list:ArrayList<String> = ArrayList<String>()        //收藏列表
    var show_collect_goods:ArrayList<Goods> = ArrayList<Goods>()    //显示的收藏商品

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collect)

        //返回按钮
        collect_back.setOnClickListener {
            finish()
        }

        //设置状态栏文字颜色及图标为深色
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        //获取当前用户账号
        val currentUser = BmobUser.getCurrentUser()
        val username = currentUser.username

        //加载数据，绑定视图的数据源
        collect_recycle_id.layoutManager = LinearLayoutManager(context)
        collect_recycle_id.adapter = GoodsAdapter(show_collect_goods)

        /*
        查看当前用户的收藏列表是否为空
        ①若为空，没有收藏商品，返回文字说明和图标
        ②不为空，有收藏的商品，展示在recycleview中
            获取商品列表
            在Goods表中找到对应的商品
            把商品信息加载到collect_details中
            展示在recycleView中
         */
        query(username)

        //添加下拉刷线功能
        collect_goods_refresh.setColorSchemeColors(Color.parseColor("#4091c9"))
        collect_goods_refresh.setOnRefreshListener {
            thread {
                Thread.sleep(700) //延迟0.7秒，等待加载完成
                collect_list_isnull = true
                show_collect_goods.clear()              //不能重新赋值，因为=ArrayList<String>之后，它的地址就变了，adpter是找不到新的地址的
                Log.e("show_collect_goods",collect_list.isEmpty().toString())
                this.runOnUiThread{
                    query(username)                         //下拉刷新，重新查询数据，并更新视图
                    collect_goods_refresh.isRefreshing = false      //不加这一句，刷新的圈圈就一直不消失
                }
            }
        }
    }


    private fun query(username:String)
    {
        val bmobQuery: BmobQuery<Person> = BmobQuery()
        bmobQuery.addWhereEqualTo("username",username)
        bmobQuery.findObjects(object : FindListener<Person>(){
            override fun done(p0: MutableList<Person>?, p1: BmobException?) {
                if(p1==null){
                    if (p0!!.size>0){           //如果p0的大小大于0，说明改用户的个人信息存在
                        person = p0!![0]
                        collect_list = person.collect_goods     //获取用户的收藏列表
                        collect_list_isnull = collect_list.isEmpty()
                    }
                    else{
                        collect_list_isnull = true
                    }
                    collect_list_stat(collect_list_isnull)
                    Log.e("collect_list_isnull",collect_list_isnull.toString())
                }
            }
        })
    }
    private fun collect_list_stat(isnull:Boolean){
        //无收藏
        if(isnull){
            collect_recycle_id.setVisibility(View.INVISIBLE)
            collect_null_pic.setImageResource(R.drawable.box)
            collect_null.setText("暂无收藏的商品噢~\n快去首页看看吧~")
        }
        //有收藏
        else{
            for (G in collect_list){
                Log.e("商品",G.toString())
                val bmobQuery: BmobQuery<Goods> = BmobQuery()
                bmobQuery.addWhereEqualTo("objectId",G)
                bmobQuery.findObjects(object : FindListener<Goods>(){
                    override fun done(p0: MutableList<Goods>?, p1: BmobException?) {
                        if(p1==null){
                            if (p0!!.size>0){           //如果p0的大小大于0，说明该商品存在
                                goods = p0!![0]
                                Log.e("商品goods",goods.objectId.toString())
                                show_collect_goods.add(goods)
                                Log.e("商品collect",show_collect_goods.toString())
                            }
                            collect_recycle_id.adapter?.notifyDataSetChanged()
                        }
                    }
                })
            }
        }
    }

    //自定义适配器类，完成数据加载绑定到视图中
    inner class GoodsAdapter(val goodsList:ArrayList<Goods>): RecyclerView.Adapter<GoodsViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodsViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.collect_details,parent,false)  //实例化布局加载器对象,并加载它的方法
            return GoodsViewHolder(itemView)
        }

        //绑定数据和视图
        override fun onBindViewHolder(holder: GoodsViewHolder, position: Int) {
            val goods = goodsList[position]
            //把数据中的描述、价格 绑定 到视图中的对应栏
            holder.description.text = goods.description
            holder.price.text = "￥"+ goods.price
            holder.tilte.text = goods.title

            //图片的加载需要借助开源的图片加载框架, bumptech/glide
            //图片加载
            if (goods.pic_urls!!.size>0)
                Glide.with(this@CollectActivity).load(goods.pic_urls?.get(0)).into(holder.image)

            //设置点击事件,只要点击cardview，就会触发
            holder.itemView.setOnClickListener{
                val pos = holder.adapterPosition        //获取recyclerview中点击的那个item对应的下标

                //跳转到详情页面，获取对象的ObjectId，传递给GoodDetailFragment
                val intent = Intent(MyApplication.context,GoodDetailFragment::class.java)
                intent.putExtra("Id",goods.objectId)
                intent.putExtra("Type",goods.type)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return goodsList.size
        }

    }

    //绑定视图中的控件到适配器中的变量
    inner class GoodsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val description: TextView = itemView.findViewById(R.id.collect_goods_describe)
        val image: ImageView = itemView.findViewById(R.id.collect_goods_pic)
        val price: TextView = itemView.findViewById(R.id.collect_goods_price)
        val tilte: TextView = itemView.findViewById(R.id.collect_goods_title)
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
        val lp = collect_topbar.getLayoutParams() as RelativeLayout.LayoutParams
        lp.topMargin = top_height+20
        collect_topbar.setLayoutParams(lp)
    }

}

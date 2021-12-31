package com.example.textbottonnav

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.textbottonnav.util.Goods
import com.example.textbottonnav.util.showToast
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.snackbar.Snackbar

import cn.bmob.v3.exception.BmobException

import cn.bmob.v3.BmobQuery
import cn.bmob.v3.listener.FindListener
import com.bumptech.glide.Glide


class SearchableActivity : AppCompatActivity() {

    lateinit var cancel_button:TextView
    lateinit var fragment_search_appbarlayout:AppBarLayout
    lateinit var fragment_search_edit:EditText
    lateinit var fragment_search_display:RecyclerView
    lateinit var search_content:String                      //搜索框输入的文本
    var search_goods_list = ArrayList<Goods>()         //从数据库匹配到的Goods
    lateinit var mLayoutManager:RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_search)

        //设置状态栏文字颜色及图标为深色
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        //绑定控件
        cancel_button = findViewById(R.id.fragment_search_cancel_button)
        fragment_search_appbarlayout = findViewById(R.id.fragment_search_appbarlayout)
        fragment_search_edit = findViewById(R.id.fragment_search_edit)
        fragment_search_display=findViewById(R.id.fragment_search_display)

        //取消按钮的响应事件
        cancel_button.setOnClickListener {
            finish()
        }

        //搜索框监听事件
        fragment_search_edit.addTextChangedListener {

            //动态获取每次搜索框的修改
            //Log.e("搜索框：",fragment_search_edit.text.toString())
            search_content = fragment_search_edit.text.toString()
            refresh()
        }

        //设置RecyclerView
        val spanCount = 2
        mLayoutManager = StaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL)
        fragment_search_display.layoutManager = mLayoutManager
        //绑定视图的数据源
        fragment_search_display.adapter = GoodsAdapter(search_goods_list)
    }

    //自定义适配器类，完成数据加载绑定到视图中
    inner class GoodsAdapter(val goodsList:ArrayList<Goods>): RecyclerView.Adapter<GoodsViewHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodsViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.goods_card,parent,false)  //实例化布局加载器对象,并加载它的方法
            return GoodsViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: GoodsViewHolder, position: Int) {
            val goods = goodsList[position]
            holder.title.text = goods.title         //把数据中的标题 绑定 到视图中的标题栏
            holder.description.text = goods.description
            holder.price.text = "￥"+ goods.price

            //图片的加载需要借助开源的图片加载框架, bumptech/glide
            //图片加载
            if (goods.pic_urls!!.size>0)
                Glide.with(this@SearchableActivity).load(goods.pic_urls!![0]).into(holder.image)

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
    inner class GoodsViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        val title: TextView = itemView.findViewById(R.id.goods_title)
        val description: TextView = itemView.findViewById(R.id.goods_describe)
        val image: ImageView = itemView.findViewById(R.id.goods_image)
        val price:TextView = itemView.findViewById(R.id.goods_price)
    }

    //动态获取搜索输入，并更新视图
    private fun refresh()
    {
        search_goods_list.clear()   //每次查询前，先把原有的内容清空，因为每次查询匹配的结果都是不一样的
        search(search_content)      //按照输入内容查询数据库
    }

    //Bomb查询，传入查询字符串，查询结果保存到search_goods_list中
    private fun search(str:String)
    {
        val goodsBmobQuery: BmobQuery<Goods> = BmobQuery<Goods>()
        goodsBmobQuery.addWhereEqualTo("title", str)
        goodsBmobQuery.findObjects(object : FindListener<Goods?>() {
            override fun done(goods_list: List<Goods?>, e: BmobException?) {
                if (e == null) {
                    for (good: Goods? in goods_list) {
                        //Log.e("图片：",good.pic?.url.toString())
                        //把数据加载到缓存区的cache_goods_list
                        if (good != null) {
                            search_goods_list.add(good)
                        }
                    }
                    //search_goods_list = goods_list as ArrayList<Goods>        不能用这种方法赋值，没有用！！
                    fragment_search_display.adapter?.notifyDataSetChanged()   //加载数据完成之后，通知view视图，数据有变化，需要更新UI
                    //Log.e("匹配数量：",goods_list.size.toString())
                    //Log.e("search_goods_list",search_goods_list.size.toString())
                } else {
                    Log.e("BMOB", e.toString())
                }
            }
        })
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
        val lp = fragment_search_appbarlayout.getLayoutParams() as LinearLayout.LayoutParams
        lp.topMargin = top_height+20
        fragment_search_appbarlayout.setLayoutParams(lp)
    }

}
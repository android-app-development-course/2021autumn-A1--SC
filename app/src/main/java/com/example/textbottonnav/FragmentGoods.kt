package com.example.textbottonnav

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.bumptech.glide.Glide
import com.example.textbottonnav.util.Goods
import com.example.textbottonnav.util.showToast
import kotlinx.android.synthetic.main.fragment_goods.*
import kotlin.concurrent.thread

class FragmentGoods(var goodsType:String):Fragment() {

    lateinit var mLayoutManager:RecyclerView.LayoutManager

    //缓存区物品列表，从数据库加载数据
    var cache_goods_list = ArrayList<Goods>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_goods,container,false)

        //加载Bomb数据库中Goods表中的数据，异步性，在子线程中执行，页面渲染完成之后，数据才加载完成，所以一打开页面是空的！！
        queryObjects()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //goodsRecyclerView.layoutManager = LinearLayoutManager(MyApplication.context) //为RecyclerView控件设置线性布局

        //设置RecyclerView为两列瀑布流布局
        val spanCount = 2
        mLayoutManager = StaggeredGridLayoutManager(spanCount,StaggeredGridLayoutManager.VERTICAL)
        goods_recycler_view.layoutManager = mLayoutManager

        //绑定视图的数据源
        goods_recycler_view.adapter = GoodsAdapter(cache_goods_list)

        //添加下拉刷线功能
        goods_refresh.setColorSchemeColors(Color.parseColor("#4091c9"))
        goods_refresh.setOnRefreshListener {
            thread {
                Thread.sleep(700)                       //延迟0.7秒，等待加载完成
                activity?.runOnUiThread{
                    queryObjects()                          //下拉刷新，重新查询数据，并更新视图
                    goods_refresh.isRefreshing = false      //不加这一句，刷新的圈圈就一直不消失
                }
            }
        }
    }

    //自定义适配器类，完成数据加载绑定到视图中
    inner class GoodsAdapter(val goodsList:ArrayList<Goods>): RecyclerView.Adapter<GoodsViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodsViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.goods_card,parent,false)  //实例化布局加载器对象,并加载它的方法
            return GoodsViewHolder(itemView)
        }

        //绑定数据和视图
        override fun onBindViewHolder(holder: GoodsViewHolder, position: Int) {
            val goods = goodsList[position]
            holder.title.text = goods.title         //把数据中的标题 绑定 到视图中的标题栏
            holder.description.text = goods.description
            if (goodsType == "闲置")
                holder.price.text = "￥"+ goods.price

            //图片的加载需要借助开源的图片加载框架, bumptech/glide
            //图片加载
            if (goods.pic_urls!!.size>0)
                Glide.with(this@FragmentGoods).load(goods.pic_urls!![0]+"!/format/webp").into(holder.image)

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

    /**
     * bmob 查询数据列表,按照goodTypes类别查询
     */
    private fun queryObjects() {
        var bmobQuery: BmobQuery<Goods> = BmobQuery()
        bmobQuery.addWhereEqualTo("type",goodsType)
        bmobQuery.addWhereEqualTo("onshow",true)
        bmobQuery.findObjects(object : FindListener<Goods>() {
            override fun done(query_goods_list: MutableList<Goods>?, ex: BmobException?) {

                if (ex == null) {
                    //"查询成功".showToast()
                    if (query_goods_list != null) {
                        cache_goods_list.clear()
                        for (good: Goods in query_goods_list) {
                            //Log.e("图片：",good.pic?.url.toString())
                            //把数据加载到缓存区的cache_goods_list
                            cache_goods_list.add(good)
                        }
                        goods_recycler_view.adapter?.notifyDataSetChanged()   //加载数据完成之后，通知view视图，数据有变化，需要更新UI
                    }
                } else {
                    ex.message?.showToast()
                }
            }
        })
    }

}
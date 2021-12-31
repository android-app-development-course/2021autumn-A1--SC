package com.example.textbottonnav

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import com.bumptech.glide.Glide
import com.example.textbottonnav.R
import com.example.textbottonnav.util.Goods
import com.example.textbottonnav.util.Order
import com.example.textbottonnav.util.showToast
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.fragment_goods.*
import kotlinx.android.synthetic.main.fragment_goods.goods_recycler_view
import kotlinx.android.synthetic.main.fragment_send.*
import kotlinx.android.synthetic.main.order_list.*
import kotlinx.android.synthetic.main.order_detail.*
import kotlin.concurrent.thread

class OrderListActivity : AppCompatActivity() {

    //缓存区物品列表，从数据库加载数据
    var cache_orders_list = ArrayList<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.order_list)

        //设置状态栏文字颜色及图标为深色
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        queryObjects()
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        order_recycleview.layoutManager = layoutManager
        //绑定视图的数据源
        order_recycleview.adapter = OrdersAdapter(cache_orders_list)

        order_back_button.setOnClickListener { finish() }
    }

    //绑定视图中的控件到适配器中的变量
    inner class OrdersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val goods_name:TextView = itemView.findViewById(R.id.goods_name)
        val order_user:TextView = itemView.findViewById(R.id.order_detail_username)
        val image:ImageView = itemView.findViewById(R.id.order_detail_pic)
        val contact:Button = itemView.findViewById(R.id.contact)
        val price:TextView = itemView.findViewById(R.id.order_detail_price)
        val labal:TextView = itemView.findViewById(R.id.sell_or_buy)
        val avatar:CircleImageView = itemView.findViewById(R.id.order_detail_avatar)
    }

    //自定义适配器类，完成数据加载绑定到视图中
    inner class OrdersAdapter(val ordersList:ArrayList<Order>): RecyclerView.Adapter<OrdersViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.order_detail,parent,false)  //实例化布局加载器对象,并加载它的方法
            return OrdersViewHolder(itemView)
        }

        //绑定数据和视图
        override fun onBindViewHolder(holder: OrdersViewHolder, position: Int) {
            val orders = ordersList[position]
            val goods:Goods? = orders.Goods
            holder.order_user.text = BmobUser.getCurrentUser().username

            if (orders.buyer == holder.order_user.text){
                // 如果是购买的订单
                holder.labal.text = "我买到的"
                holder.contact.text = "联系卖家"
            } else {
                // 如果是卖出的订单
                holder.labal.text = "我卖出的"
                holder.contact.text = "联系买家"
            }
            holder.goods_name.text = goods!!.title
            holder.price.text = "实付款：￥" + orders.price.toString()

            //图片加载
            if (goods.pic_urls!!.size>0)
                Glide.with(this@OrderListActivity).load(goods.pic_urls!![0]).into(holder.image)

            //设置点击事件,只要点击cardview，就会触发
            holder.itemView.setOnClickListener{

                //跳转到详情页面，获取对象的ObjectId，传递给GoodDetailFragment
                val intent = Intent(MyApplication.context, GoodDetailFragment::class.java)
                intent.putExtra("Id",goods.objectId)
//                intent.putExtra("goods_name",goods.title)
//                intent.putExtra("price",orders.price.toString())
//                intent.putExtra("pic_url",goods.pic_urls!![0])
                intent.putExtra("Type",goods.type)
                startActivity(intent)
            }
        }

        override fun getItemCount(): Int {
            return ordersList.size
        }
    }

    /**
     * bmob 查询数据列表,按照goodTypes类别查询
     */
    private fun queryObjects() {
        var bmobQuery1: BmobQuery<Order> = BmobQuery()
        var bmobQuery2: BmobQuery<Order> = BmobQuery()
        val user = BmobUser.getCurrentUser().username
        bmobQuery1.addWhereEqualTo("buyer",user)
        bmobQuery2.addWhereEqualTo("seller", user)
        val queries = ArrayList<BmobQuery<Order>>()
        queries.add(bmobQuery1)
        queries.add(bmobQuery2)
        val bmobQuery: BmobQuery<Order> = BmobQuery()
        bmobQuery.or(queries)
        bmobQuery.include("Goods")
        bmobQuery.findObjects(object : FindListener<Order>() {
            override fun done(query_goods_list: MutableList<Order>?, ex: BmobException?) {
                if (ex == null) {
                    //"查询成功".showToast()
                    if (query_goods_list != null) {
                        cache_orders_list.clear()
                        for (order: Order in query_goods_list) {

                            //把数据加载到缓存区的cache_goods_list
                            cache_orders_list.add(order)
                        }
                        order_recycleview.adapter?.notifyDataSetChanged()   //加载数据完成之后，通知view视图，数据有变化，需要更新UI
                    }
                } else {
                    ex.message?.showToast()
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
        val lp = top_bar.getLayoutParams() as LinearLayout.LayoutParams
        lp.topMargin = top_height+20
        top_bar.setLayoutParams(lp)
    }
}
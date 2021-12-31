package com.example.textbottonnav

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.example.textbottonnav.util.showToast
import kotlinx.android.synthetic.main.orderfragment.*
import android.view.MotionEvent

import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobDate
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.QueryListener
import cn.bmob.v3.listener.SaveListener
import cn.bmob.v3.listener.UpdateListener
import com.example.textbottonnav.util.Goods
import com.example.textbottonnav.util.Order
import com.example.textbottonnav.util.ValidateUtil
import com.example.textbottonnav.util.ValidateUtil.AdressCheck
import java.util.*


class OrderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.orderfragment)

        //设置状态栏文字颜色及图标为深色
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        /**
         * 点击对应的商品图标，进入到该页面，通过intent将商品名称以及商品价格传递过来
         * @goods_name 商品名称
         * @price 商品价格
         */
        val goods_id = intent.getStringExtra("objectID")
        val goods_name = intent.getStringExtra("goods_name")
        val price = intent.getStringExtra("price")
        val picture = intent.getStringExtra("pic_url")


        // 设置商品名称与商品价格
        order_goods_name.text = goods_name
        order_price.text = price
        // 图片加载
        Glide.with(this@OrderActivity).load(picture).into(order_goods_pic)
        // 单击地址以输入收件地址
        order_address.setOnClickListener {
            var et = EditText(this)
            AlertDialog.Builder(this).apply {
                setTitle("请输入收货地址")
                setView(et)
                setPositiveButton("确定"){
                        dialog, which -> order_address.text = et.text
                }
                setNegativeButton("取消") {
                        dialog, which ->
                }
                show()
            }
        }

        //"确认"
        confirm_button.setOnClickListener{
            // 在确认购买之前进行一个简单的信息验证
            if (false && AdressCheck(order_address.text.toString())){
                "请重新填写地址信息".showToast()
            } else if (!ValidateUtil.PhoneCheck(phone_receiver.text.toString())){
                "请重新填写收件手机号码".showToast()
            } else {
                "购买成功！".showToast()
                updateGoodsOnshow(goods_id)
                var bmobQuery: BmobQuery<Goods> = BmobQuery()
                bmobQuery.getObject(goods_id, object : QueryListener<Goods>() {
                    override fun done(Good: Goods?, ex: BmobException?) {
                        if (ex == null) {
                            var order = Order()
                            order.Goods = Good
                            order.buyer = BmobUser.getCurrentUser().username
                            order.date = BmobDate(Date())
                            order.address = order_address.text.toString()
                            order.contact = phone_receiver.text.toString()
                            order.seller = Good!!.username.toString()
                            order.price = Good.price!!.toFloat()
                            order.save(object: SaveListener<String>(){
                                override fun done(objectId: String?, ex: BmobException?) {
                                    if (ex == null){
                                        "下单成功".showToast()
                                        val intent = Intent(MyApplication.context, OrderListActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Log.e("新增订单", "异常" + ex.message)
                                    }
                                }
                            })
                        } else {
                            Toast.makeText(this@OrderActivity, ex.message, Toast.LENGTH_LONG).show()
                            Log.e("查找商品", "异常" + ex.message)
                        }
                    }
                })
            }
            // 后面可以添加验证事件
        }
        // 返回
        orderfragment_back.setOnClickListener { finish() }
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
        val lp = orderfragment_topbar.getLayoutParams() as ConstraintLayout.LayoutParams
        lp.topMargin = top_height
        orderfragment_topbar.setLayoutParams(lp)
    }

    /**
     * 更新商品数据，使该商品下架
     */
    private fun updateGoodsOnshow(objectId: String?) {
        var good = Goods()
        good.onshow = false
        good.update(objectId, object : UpdateListener() {
            override fun done(ex: BmobException?) {
                if (ex != null){
                    Log.e("更新数据异常", "异常" + ex.message)
                }
            }
        })
    }
}
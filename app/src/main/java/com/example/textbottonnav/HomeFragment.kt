/*主页子页面，用来展示物品*/

package com.example.textbottonnav

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class HomeFragment:Fragment() {

    //发布按钮
    lateinit var send_good_bt:FloatingActionButton
    lateinit var tab_layout:TabLayout
    lateinit var goods_view_pager:ViewPager
    lateinit var search_button:TextView

    val fragmentList = ArrayList<FragmentGoods>()
    val goodsTypeList = listOf("闲置","失物")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home,container,false)
        send_good_bt = view.findViewById(R.id.send_goods_button)
        tab_layout = view.findViewById(R.id.tab_layout)
        goods_view_pager = view.findViewById(R.id.goods_view_pager)
        search_button = view.findViewById(R.id.goods_fragment_search)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //发布按钮的响应事件
        send_good_bt.setOnClickListener(object :View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(activity,SelectSendTypeFragment::class.java)
                startActivity(intent)
            }
        })

        //搜索框的响应事件
        search_button.setOnClickListener {
            val intent = Intent(activity,SearchableActivity::class.java)
            startActivity(intent)
        }

        //向fragmentlist添加不同类别的物品页面对象
        for (goodsType in goodsTypeList){
            fragmentList.add(FragmentGoods(goodsType))
        }
        //将fragmentlist里面的fragment放进viewpager里面，从而渲染到视图上
        goods_view_pager.adapter = activity?.supportFragmentManager?.let { TabAdapter(it) }

        //实现viewpager左右滑动与tablayout这个标签选择器的联动
        tab_layout.setupWithViewPager(goods_view_pager)
    }

    inner class TabAdapter(fm: FragmentManager):FragmentPagerAdapter(fm,
        BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

        //设置标题
        override fun getPageTitle(position: Int): CharSequence? {
            return goodsTypeList[position]
        }

    }
}
package com.example.textbottonnav

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView

import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import cn.bmob.v3.Bmob
import com.example.textbottonnav.util.Person


class MainActivity : AppCompatActivity() {

    //把三个fragment放到同一个list里面
    val fragmentList = listOf(HomeFragment(),MessageFragment(),MyFragment())
    lateinit var main_relative_layout:RelativeLayout
    lateinit var main_content_view:ViewPager

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
        val lp = main_content_view.getLayoutParams() as RelativeLayout.LayoutParams
        lp.topMargin = top_height+20
        main_content_view.setLayoutParams(lp)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)      //将Activity与Layout布局文件进行关联
        main_relative_layout = findViewById(R.id.main_relative_layout)
        main_content_view = findViewById(R.id.content_view_pager)

        //设置状态栏文字颜色及图标为深色
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR


        //适配器，把多个fragment放到主页面中
        val contentViewPager = findViewById<ViewPager>(R.id.content_view_pager)     //通过findViewById将Layout中的控件找出来，并赋给一个变量，然后就可以通过这个变量
        contentViewPager.offscreenPageLimit = 3 //设置fragment页面的缓存数量
        contentViewPager.adapter = MyAdapter(supportFragmentManager)

        //设置点击底部菜单栏的响应事件
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
        bottomNav.setOnNavigationItemReselectedListener {
            when(it.itemId){
                R.id.nav_home -> contentViewPager.currentItem = 0   //如果点击Home按钮，ViewPager显示fragmentList[0]
                R.id.nav_message -> contentViewPager.currentItem = 1
                R.id.nav_my -> contentViewPager.currentItem = 2
            }
            false
        }

        contentViewPager.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                bottomNav.menu.getItem(position).isChecked=true
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    //内部类，适配器，把多个fragment放到主页面中
    inner class MyAdapter(fm: FragmentManager):
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
        override fun getCount(): Int {
            return fragmentList.size
        }

        override fun getItem(position: Int): Fragment {
            return fragmentList[position]
        }

    }

}

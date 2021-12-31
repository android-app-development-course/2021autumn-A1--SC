# APP前端开发过程记录

## 11月12日

参照视频[安卓新闻App开发（1）新闻列表布局 ——用kotlin语言写Android系列_哔哩哔哩_bilibili](https://www.bilibili.com/video/BV12N411R7yS?spm_id_from=333.999.0.0)

制作了底部菜单栏，（主页、发布页、消息页、我的页）

<img src="C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211112204629945.png" alt="image-20211112204629945" style="zoom: 50%;" />



实现的思路：

- activity_main.xml中设置一个ViewPager（放置不同的子页面，每次只显示一个，通过滑动或者底部导航切换。通过适配器实现切换）和BottomNavigationView（底部导航栏）



- 每个子页面新建一个Kotlin类和Layout布局文件，Kotlin类里面实现具体的功能，如显示图片文字等。Layout布局文件设置布局

- 底部导航栏的内容通过新建一个menu resource file 来实现，然后套用到activity_main.xml的BottomNavigationView中（app:menu="@menu/bottom_nav_menu"）
- 子页面切换的逻辑：在MainActivity.kt文件的MainActivity类的onCreate方法里，设置点击底部菜单栏的响应事件，识别菜单栏的id，然后通过适配器（不确定是不是适配器）切换到对应id的fragment子页面





【遇到的问题】

Window刚安装的Android studio，抄了一小时代码，编译的时候突然报错，找不到gradle tools version 30.0.1，编译不成功；第一次参照csdn把build.gradle文件给删除了，结果原来的问题没解决，build.gradle文件又找不回来了。

在另外一个项目上也编译了一次，发现也编译不过，所以是IDE本身的问题。最后通过修改gradle tools 的version解决了这个问题。

具体步骤：File->Project Structure->Modules Build Tools Version，修改版本。

![image-20211112205135297](C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211112205135297.png)



下一步，打算先制作主页面的布局（失物信息）

主页面竖向对半分成两个ScrollView，参照闲鱼的布局，上面图片，下面描述



<img src="C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211112224949871.png" alt="image-20211112224949871" style="zoom:50%;" />

这是目前完成的设计



## 11月25日

- 遇到的问题

  layout文件中，无法通过android:background 修改按钮的背景颜色

  【原因】：控件的背景颜色要通过android:backgroundTint 修改





## 11月27日

- 将登陆界面作为启动界面，点击登陆跳转到主界面，点击注册跳转到注册页面

<img src="C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211127103922726.png" alt="image-20211127103922726" style="zoom:33%;" /><img src="C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211127103948671.png" alt="image-20211127103948671" style="zoom:33%;" /><img src="C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211127104020889.png" alt="image-20211127104020889" style="zoom:33%;" />



- 获取EditView的文本

  ```
  val input_username = username.text.toString()
  ```

- 通过Toast输出提示

  ```
  Toast.makeText(this@LoginActivity,"账号不存在，或者密码错误",Toast.LENGTH_SHORT).show()
  ```

- 清空EditView文本

  ```
  username.setText("")
  ```

  



## 12月2日

- 主页面使用viewpager来切换，那每个页面的功能逻辑要写到哪？怎么写？

  ![image-20211202220239129](C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211202220239129.png)

  还是写到布局对应的.kt文件中



- 隐藏指定Activity的顶部标题栏

  <img src="C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211202155146216.png" alt="image-20211202155146216" style="zoom:50%;" />



- 文字获取

  设计点击事件，从控件获取

- 图片要怎么上传获取？





## 12月3日

备份代码

```
package com.example.textbottonnav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.textbottonnav.goods_unit.Goods
import com.example.textbottonnav.util.showToast

class MessageFragment():Fragment() {

    //列表控件
    lateinit var newsRecyclerView:RecyclerView

    //物品列表
    val goodsList = listOf(
        Goods("标题1","一段描述......","https://imgm.gmw.cn/attachement/jpg/site215/20211203/2581004167966144863.jpg"),
        Goods("标题2","一段描述......","https://imgm.gmw.cn/attachement/jpg/site215/20211203/2581004167966144863.jpg"),
        Goods("标题3","一段描述......","https://imgm.gmw.cn/attachement/jpg/site215/20211203/2581004167966144863.jpg")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_message,container,false)
        newsRecyclerView = view.findViewById(R.id.goods_recycler_view)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        newsRecyclerView.layoutManager = LinearLayoutManager(MyApplication.context) //为RecyclerView控件设置线性布局

        newsRecyclerView.adapter = GoodsAdapter(goodsList)
    }

    //自定义适配器类，完成数据加载绑定到视图中
    inner class GoodsAdapter(val goodsList:List<Goods>): RecyclerView.Adapter<GoodsViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodsViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.image,parent,false)  //实例化布局加载器对象,并加载它的方法
            return GoodsViewHolder(itemView)
        }

        //绑定数据和视图
        override fun onBindViewHolder(holder: GoodsViewHolder, position: Int) {
            val goods = goodsList[position]
            holder.title.text = goods.title         //把数据中的标题 绑定 到视图中的标题栏
            holder.description.text = goods.describe

            //图片的加载需要借助开源的图片加载框架, bumptech/glide
            //图片加载
            Glide.with(this@MessageFragment).load(goods.imageURL).into(holder.image)

            //设置点击事件,只要点击cardview，就会触发
            holder.itemView.setOnClickListener{
                "点击跳转".showToast()
            }
        }

        override fun getItemCount(): Int {
            return goodsList.size
        }

    }

    inner class GoodsViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        val title:TextView = itemView.findViewById(R.id.goods_title)
        val description:TextView = itemView.findViewById(R.id.goods_describe)
        val image:ImageView = itemView.findViewById(R.id.goods_image)
    }
}
```



## 12月4日

- 让app自带的actionbar隐藏的更推荐的方式，而且不会影响底部导航栏

  ![image-20211204093951616](C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211204093951616.png)

  改为继承自：Theme.MaterialComponents.DayNight.NoActionBar



- 取消按钮的逻辑实现

  普通控件的实现

  ![image-20211204113550488](C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211204113550488.png)

  标题栏ToolBar返回按钮的实现

  ![image-20211204113658249](C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211204113658249.png)



- 调用系统相册

  ```
  public class PhotoSelectOne extends AppCompatActivity {
      private Button select_photo;
      private ImageView iv_photo;
      //得到图片的路径
      private String path;
  
      private static final int IMAGE_REQUEST_CODE = 0;
      @Override
      protected void onCreate(@Nullable Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_photo);
          select_photo=(Button)findViewById(R.id.select_photo);
          iv_photo=(ImageView) findViewById(R.id.iv_photo);
          select_photo.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  selectFromGallery();
              }
          });
      }
  
      /**
       * 从相册选择原生的照片（不裁切）
       */
      private void selectFromGallery() {
          //在这里跳转到手机系统相册里面
          Intent intent = new Intent(
                  Intent.ACTION_PICK,
                  android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
          startActivityForResult(intent, IMAGE_REQUEST_CODE);
      }
      @Override
      protected void onActivityResult(int requestCode, int resultCode, Intent data) {
  
          super.onActivityResult(requestCode, resultCode, data);
          //在相册里面选择好相片之后调回到现在的这个activity中
          switch (requestCode) {
              case IMAGE_REQUEST_CODE://这里的requestCode是我自己设置的，就是确定返回到那个Activity的标志
                  if (resultCode == RESULT_OK) {//resultcode是setResult里面设置的code值
                      try {
                          Uri selectedImage = data.getData(); //获取系统返回的照片的Uri
                          String[] filePathColumn = {MediaStore.Images.Media.DATA};
                          Cursor cursor = getContentResolver().query(selectedImage,
                                  filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
                          cursor.moveToFirst();
                          int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                          path = cursor.getString(columnIndex);  //获取照片路径
                          cursor.close();
                          Bitmap bitmap = BitmapFactory.decodeFile(path);
                          iv_photo.setImageBitmap(bitmap);
                      } catch (Exception e) {
                          // TODO Auto-generatedcatch block
                          e.printStackTrace();
                      }
                  }
                  break;
          }
      }
  }
  ```



- 完成进度：底部导航栏（主页、消息、我的），主页（闲置+失物两个标签页，实现了双列瀑布流布局），主页搜索栏+搜索页，发布页（打开系统相册添加图片），系统状态栏改为白底黑字

  <img src="C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211204231916142.png" alt="image-20211204231916142" style="zoom: 33%;" /><img src="C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211204231930627.png" alt="image-20211204231930627" style="zoom: 33%;" /><img src="C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211204232059802.png" alt="image-20211204232059802" style="zoom:33%;" />

  

<img src="C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211204231954144.png" alt="image-20211204231954144" style="zoom: 33%;" /><img src="C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211204232144565.png" alt="image-20211204232144565" style="zoom:33%;" />



- 下一步计划：
  - 将（描述、价格、图片）上传到mysql数据库
  - 从mysql数据库加载数据插入到主页中
  - 商品页下拉刷新的实现



## 12月5日

### 读写手机内存

```
fun save(fileName:String,fileContent:String){
        try {
            /* 根据用户提供的文件名，以及文件的应用模式，打开一个输出流.文件不存系统会为你创建一个的， 
       * 至于为什么这个地方还有FileNotFoundException抛出，我也比较纳闷。在Context中是这样定义的 
       *  public abstract FileOutputStream openFileOutput(String name, int mode) 
       *  throws FileNotFoundException; 
       * openFileOutput(String name, int mode); 
       * 第一个参数，代表文件名称，注意这里的文件名称不能包括任何的/或者/这种分隔符，只能是文件名 
       *     该文件会被保存在/data/data/应用名称/files/chenzheng_java.txt 
       * 第二个参数，代表文件的操作模式 
       *     MODE_PRIVATE 私有（只能创建它的应用访问） 重复写入时会文件覆盖 
       *     MODE_APPEND 私有  重复写入时会在文件的末尾进行追加，而不是覆盖掉原来的文件 
       *     MODE_WORLD_READABLE 公用 可读 
       *     MODE_WORLD_WRITEABLE 公用 可读写 
       * */
            val outputStream: FileOutputStream = openFileOutput(
                fileName,
                MODE_PRIVATE
            )
            outputStream.write(fileContent.toByteArray())
            outputStream.flush()
            outputStream.close()
            Toast.makeText(this, "保存成功", Toast.LENGTH_LONG).show()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
```

```
fun read(fileName:String){

        try {
            val input_stream:FileInputStream = this.openFileInput(fileName)
            val bytes = ByteArray(1024)
            val array_output_stream:ByteArrayOutputStream = ByteArrayOutputStream()
            while (input_stream.read(bytes)!=-1){
                array_output_stream.write(bytes,0,bytes.lastIndex)
            }
            input_stream.close()
            array_output_stream.close()
            val content = String(array_output_stream.toByteArray())
            Toast.makeText(this,content,Toast.LENGTH_SHORT).show()
        }catch (e:FileNotFoundException){
            e.printStackTrace()
        }catch (e:IOException){
            e.printStackTrace()
        }
    }
    
    
```



## 12月6日

### 获取app下保存文件的路径

```
在Activity类里面调用
this.filesDir.path 
```



### 在Activity中得到新打开Activity 关闭后返回的数据

需要使用系统提供的startActivityForResult(Intent intent, int requestCode)方法打开新的Activity，新的Activity 关闭后会向前面的Activity传回数据，为了得到传回的数据，必须在前面的Activity中重写onActivityResult(int requestCode, int resultCode, Intent data)方法。

```
//得到新打开Activity关闭后返回的数据
     //第二个参数为请求码，可以根据业务需求自己编号
     startActivityForResult(new Intent(MainActivity.this, OtherActivity.class), 1);
                
/**
     * 为了得到传回的数据，必须在前面的Activity中（指MainActivity类）重写onActivityResult方法
     * 
     * requestCode 请求码，即调用startActivityForResult()传递过去的值
     * resultCode 结果码，结果码用于标识返回数据来自哪个新Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String result = data.getExtras().getString("result");//得到新Activity 关闭后返回的数据
        Log.i(TAG, result);
    }
```



### 怎么获得RecyclerView中某个元素的position

<img src="C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211209214716394.png" alt="image-20211209214716394"  />



## 12月10日

### app先实现本地储存





## 12月24日

### 一、解决UI问题

解决页面内容被状态栏遮蔽的问题

参考：https://blog.csdn.net/a_running_wolf/article/details/50477965  获取状态栏高度

参考：https://blog.csdn.net/xgangzai/article/details/81437003    动态设置控件布局属性，只能设置顶层布局以下的view，而无法控制最顶层的布局view（一开始犯的错误）

- 解决办法：动态设置view的topMargin为状态栏高度
- 代码修改在MainActivity.kt的onWindowFocusChanged函数，已添加注释。



- 修改部分：
  1. MainActivity
  2. SendActivity



### 二、实现商品信息（文字+图片）上传数据库

- SendActivity.kt
- 文件域名绑定，没有域名无法上传文件，购买独立域名，花了100元
- 无法读取系统相册里面的照片，导致上传失败，解决办法，参考：https://www.jianshu.com/p/112e9ae03e7f
- 关于内部存储和外部储存，两个概念讲的很好的一篇文章：https://www.jianshu.com/p/23b203f1b848



- 新建一个Goods类，继承BmobObject()



### 三、实现文件（照片）和文字的绑定





## 12月25日

### 一、遇到问题

#### 1、问题一：页面和数据异步加载

- 加载Bomb数据库中Goods表中的数据，异步性，在子线程中执行，页面渲染完成之后，数据才加载完成，所以一打开页面是空的！！
- 解决办法：在queryObjects()语句，数据加载成功，并且完成后，调用goodsRecyclerView.adapter?.notifyDataSetChanged()方法，通知视图数据发生了变化。



#### 2、问题二：怎么刷新实时数据

- 解决办法：通过下拉刷新实现
- 添加依赖项，在maven仓库[[Maven Repository: Search/Browse/Explore (mvnrepository.com)](https://mvnrepository.com/)]搜索swiperefreshlayout



#### 3、问题三：获取上传图片的url

- ```
  goods.pic?.url
  ```



#### 4、问题四：怎么从FragmentGoods页面跳转到GoodsDetailFragment页面

- intent要怎么写？

  ```
  val intent = Intent(MyApplication.context,GoodDetailFragment::class.java)
  ```

- 报错：

  ```
  android.content.ActivityNotFoundException: Unable to find explicit activity class
  ```

- 问题所在：没有在Mainfest.xml文件中注册GoodsDetailFragment



### 二、在子线程中调用主线程

<img src="C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211225100021178.png" alt="image-20211225100021178" style="zoom:50%;" />



### 三、加载标题栏的返回按钮

<img src="C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211225100719085.png" alt="image-20211225100719085" style="zoom:50%;" />



### 四、设计商品详情页

#### 1、问题一

- 状态栏的文字和图标消失了
- 没有在GoodDetailFragment，设置状态栏，和动态设置视图高度（重载onWindowFocusChanged方法）



#### 2、设置跳转事件，传递ObjectId到新Activity



### 五、设计查询页

- 数据模糊查询，需要付费，99一个月，太贵了。所以暂时采用对商品标题的精确查询



## 12月26日

### 一、增加物品分类

- 增加Goods字段type值
- 修改SendActivity（发布闲置的活动页面），为创建的Goods对象自动添加一个“xianzhi"字段



### 二、设计“失物招领”页面

- 输入框设置一个ScrollView
- 图片框设置一个ScrollView，内部嵌套一个RecyclerView（设置3列），通过代码初始化它
- 设置最大图片数量为9张



### 三、遇到问题

![image-20211226215407614](C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211226215407614.png)

- 在这里使用MediaStore.Images.Media.getBitmap(resolver,uri)加载系统相册的方法去加载项目/drawable下的文件，导致报了很奇怪的错误！！！！

  ```
  报错：Unable to instantiate fragment com.example.textbottonnav.FragmentGoods: could not find Fragment constructor
  ```



- 系统相册返回的Uri：

  ```
  content://com.google.android.apps.photos.contentprovider/-1/1/content%3A%2F%2Fmedia%2Fexternal%2Fimages%2Fmedia%2F27/ORIGINAL/NONE/1880237058
  ```

- /drawable返回的Uri：

  ```
  android.resource://com.example.textbottonnav2131165277
  ```

  



## 12月28日

### 一、多图片上传遇到问题

- 报错：莫名其妙的错，并不是真正的错

  ```
  Unable to instantiate fragment com.example.textbottonnav.FragmentGoods: could not find Fragment constructor
  ```

- 问题：提供给API的类型出错，BmobFile.uploadBatch所需的第一个参数是照片的路径数组，是Array<String>类型，但是它没有add方法，无法动态添加。但是图片的数量是动态决定的，所以无法一开始必须使用ArrayList<String>，只能在ArrayList添加完之后，再调用toArray()转换成Array。但是一开始没有找到对应的方法，

- 解决办法：ArrayList<String>转Array<String>

  ```
  val path_list = pic_path_list.toArray(arrayOfNulls<String>(pic_path_list.size))
  ```



### 二、问题二

- 如何去掉Array<String>中的空值，否则上传过程中有一个路径为空，会导致不必要的错误

- 解决思路：遍历原来的Array<String>，把非空的元素形成一个新的Array<String>



### 三、修改商品详情页

- 改为显示多图，使用RecyclerView，
- 添加一个goods_detail_pic_card，作为上述RecyclerView的子元素

#### 1、遇到一个问题：notifyDataSetChanged()失效了

- 参考解决方案：https://blog.csdn.net/qq_39197781/article/details/104945704



### 四、修改发布闲置页面



## 12月29日

### 一、问题一

- 个人页查询的速度比渲染的速度慢，seek_ni_cheng的调用顺序错了，放在了BmobUser.getCurrentUser().username，所以用空字段去seek_ni_cheng了，当然查不到东西
- 还有一个问题，即使bmobQuery.findObjects没有找到指定的数据，返回的p0: MutableList<Person>?也不会为null，所以需要使用p0.size > 0 来检测是否有对应的数据



### 二、EditView相关

- 输入时显示光标

  ```
  android:cursorVisible="true"
  android:textCursorDrawable="@null"
  ```

- 弹出数组键盘

  ```
  android:inputType="number|numberDecimal"
  ```

  

### 3、问题三

- 个人信息编辑页，每次点击保存，都会调用save添加一条记录
- 问题所在：还是Bmob.query的



### 4、问题四

- 搜索页面突然闪退
- 原因：因为修改后，增加了需要传递给GoodDetailFragment::class.java的参数，intent.putExtra("Type",goods.type)。但是搜索.kt中还没有加上，所以闪退。添加之后，解决



## b站学习记录

- 【2021最新版】Android从零开始学习入门篇，涵大量案例实战 android studio



### 1、如何新建Activity

#### （1）新建一个类（java或者kotlin）

- 让这个类继承 AppCompatActivity()，（并声明为public类，暂时不知道什么作用），重写onCreate方法

- 关联布局文件setContentView()

  ![image-20211126212307364](C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211126212307364.png)

  

  

#### （2）AndroidManifest.xml文件

- 新建一个Activity之后，需要在AndroidManifest.xml文件中声明它（否则是无法启动的）

  ```
  <activity android:name=".NewActivity"/>
  ```

- （推荐！！！）或者光标选中Activity，Alt+Enter 直接 Add activity to manifest

  ![image-20211126212626764](C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211126212626764.png)

- **【文件解析】**

  ```
  <activity
              android:name=".MainActivity"
              android:exported="true"
              android:label="Home">		Activity的名称，运行时显示在状态栏上
              <intent-filter>				将该Activity设置为应用程序的主入口（点击进入的图标）
                  <action android:name="android.intent.action.MAIN" />
  
                  <category android:name="android.intent.category.LAUNCHER" />
              </intent-filter>
          </activity>
  ```

  <img src="C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211126213849817.png" alt="image-20211126213849817" style="zoom:50%;" />

  



#### （3）Activity与Layout之间的关系

- 在Activity文件中，通过重载的onCreate()方法（添加下面这句代码），将布局文件和Activity关联起来

  ```
  setContentView(R.layout.你的布局文件名)
  ```

  

### 2、Activity与View之间的关系

- 在Activity文件（.kt或.java）中，使用 findViewById方法

  ```
  TextView textView = findViewById(R.id.你的空间id);
  或者
  val view = findviewById(R.id.你的空间id)
  ```

  这样，布局文件中的控件，就可以在Activity文件中调用控件的方法进行控制了

  

- 常用函数，点击事件

  ```
  控件名.setOnClickListener(new View.OnClickListener()){
  	@Override
  	public void onClick(View v){
  		//写你的代码
  	}
  };
  ```



### 3、Activity之间的跳转（先设置点击事件）

#### （1）在类文件中设置控件变量

- 打开MyFragment.kt文件，在MyFragment类中设置控件对应的常量

  ![image-20211126223231213](C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211126223231213.png)

- 在onCreateView方法中，使用view.findViewById方法将布局中的控件与常变量关联起来

  ![image-20211126223351422](C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211126223351422.png)



#### （2）为控件设置点击事件

- kotlin代码与java代码存在好大差别！！！

- 在onActivityCreated方法中，为控件设置点击事件（这一步摸索好久！！）

  ![image-20211126223441536](C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211126223441536.png)



#### （3）使用Intent跳转

**【注意】：这里实现的是从一个Activity的Fragment跳转到另外一个Activity！！**

- 在点击事件中，新建一个Intent，

  ```
  这是java的写法，复制到kotlin会报错
  Intent intent = new Intent(TestActivity.this,NewActivity.class); //参数为（当前Activity，要跳转的Activity）
  
  这是kotlin的写法
  val intent = Intent(activity,LoinActivity::class.java)
  
  附：从Activity跳转Activity的设置
  val intent = Intent(this@LoginActivity,MainActivity::class.java)
  ```

- 调用startActivity

  ```
  startActivity(intent)
  ```



#### 【实践记录】在Fragment中输入 vs 在Activity中输入

- 在Fragment中输入，输入框弹出会导致下面的控件上移，覆盖掉另外一些控件；而Activity中弹出输入框不存在这种问题



### 4、Activity的启动模式

- #### 四种模式

1. standard模式

   

2. singleTop模式

   栈顶不重复

   <img src="C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211126203553047.png" alt="image-20211126203553047" style="zoom:50%;" />

   

3. singleTask模式

   再次启动B时，把栈顶的D和C清除

   <img src="C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211126203707676.png" alt="image-20211126203707676" style="zoom: 67%;" />

   

4. singleInstance模式

   一个Activity独占一个栈，如下例，E就是该模式

   <img src="C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211126203803261.png" alt="image-20211126203803261" style="zoom: 50%;" />



- #### 设置方式

  方式一：在AndroidManifest.xml文件中，在指定的<activity>内部加一句

  ```
  android:launchMode=xxx
  ```

  方式二：在类文件里面，通过代码来决定各种行为

  ```
  val intent = Intent(activity,LoinActivity::class.java)
  intent.addFlags(Intent.xxx)			//通过代码来决定各种模式
  startActivity(intent)
  ```




### 5、线程相关

![image-20211203212438554](C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211203212438554.png)

- 在主线程中创建新线程
- 在子线程中，把一些操作放回主线程操作



### 6、设置返回按键、销毁当前activity

![image-20211203213822501](C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211203213822501.png)



## Google文档笔记

### 可搜索框

- ![image-20211203141909376](C:\Users\mi\AppData\Roaming\Typora\typora-user-images\image-20211203141909376.png)


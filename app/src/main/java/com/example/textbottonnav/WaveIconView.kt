package com.example.textbottonnav

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.pow

class WaveIconView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val wavePath: Path = Path()
    private val mPaint: Paint = Paint()
    private var waveLength: Float = 0f //水波宽度，即二阶贝塞尔P0-P2的距离
    private var waveHeight: Float = 0f  //波峰控制点的偏移量，即P1的y值与P0的
    private var waveY: Float = 0f     //横向偏移量
    private var waveBitmap: Bitmap? = null  //当前P0的y坐标

    private val iconDrawable: Drawable?

    private val TAG = "WaveIconView"

    private var distance = 0f

    private var width: Float = 0f
    private var height: Float = 0f

    private var waveCanvas: Canvas? = null   //新建一个Canvas用于把资源文件的bitmap和贝塞尔曲线的path组合成新的waveBitmap

    private var icon: Bitmap? = null

    private var handler: IncreaseHandler? = null

    init {
        mPaint.isAntiAlias = true

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveIconView)
        iconDrawable = typedArray.getDrawable(R.styleable.WaveIconView_iconDrawable)
        typedArray.recycle()
        if (iconDrawable == null) {
            try {
                throw NullPointerException("iconDrawable must be set")
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    /**
     * 拿到资源文件的bitmap
     */
    private fun getBitmapFromDrawable(): Bitmap {
        if (icon == null) {
            val config = if (iconDrawable!!.opacity != PixelFormat.OPAQUE)
                Bitmap.Config.ARGB_8888
            else
                Bitmap.Config.RGB_565
            icon = Bitmap.createBitmap(width.toInt(), height.toInt(), config)
            //注意，下面三行代码要用到，否在在View或者surfaceView里的canvas.drawBitmap会看不到图
            val canvas = Canvas(icon!!)
            iconDrawable.setBounds(0, 0, width.toInt(), height.toInt())
            iconDrawable.draw(canvas)
        }

        return icon!!
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        wavePath.reset()
        //3个贝塞尔曲线
        wavePath.moveTo(-distance, waveY)
        for (i in 0..2) {
            wavePath.rQuadTo(
                waveLength / 2,
                waveHeight * (-1.0).pow(i.toDouble()).toFloat(),
                waveLength,
                0f
            )
        }
        //封闭path
        wavePath.lineTo(width, height)
        wavePath.lineTo(0f, height)
        wavePath.close()
        mPaint.reset()
        mPaint.isAntiAlias = true

        //通过waveCanvas取出组合图形，"交给"waveBitmap
        waveCanvas!!.drawPath(wavePath, mPaint)
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        waveCanvas!!.drawBitmap(getBitmapFromDrawable(), 0f, 0f, mPaint)

        mPaint.reset()
        mPaint.isAntiAlias = true
        canvas.drawBitmap(waveBitmap!!, 0f, 0f, mPaint)

        updateDistance()
    }

    private var distanceTemp = 0f

    private fun updateDistance() {
        distanceTemp += waveLength / 50f
        val residual = distanceTemp % waveLength
        distance =
            if ((distanceTemp / waveLength).toInt() and 1 == 1)
                waveLength - residual
            else residual
        waveY -= height / 100f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        width = getWidth().toFloat()
        height = getHeight().toFloat()
        waveHeight = height * 0.12f
        waveLength = width * 2 / 3f
        reset()
        icon = null
        if (handler != null) {
            handler!!.sendEmptyMessageDelayed(INVALIDATE, 25)
        }
    }

    private fun reset() {
        waveY = height
        waveBitmap = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
        waveCanvas = Canvas(waveBitmap!!)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (handler == null) {
            handler = IncreaseHandler()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler!!.removeCallbacksAndMessages(null)
        handler = null
    }

    internal inner class IncreaseHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                INVALIDATE -> {
                    Log.d(TAG, "waveY：$waveY")
                    if (waveY < -waveHeight) {
                        reset()
                        Log.d(TAG, "reset waveBitmap")
                    }
                    invalidate()
                    sendEmptyMessageDelayed(INVALIDATE, 20)
                }
            }
        }
    }

    companion object {

        private const val INVALIDATE = 0X777
    }
}

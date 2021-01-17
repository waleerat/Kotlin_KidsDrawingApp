package com.wgoweb.kidsdrawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var mDrawPath: CustomPath? = null
    private var mCanvasBitMap: Bitmap? = null
    private var mDrawPaint: Paint? = null
    private  var mCanvasPaint: Paint? = null
    private  var mBrushSize: Float = 0.toFloat()
    private  var color = Color.BLACK
    private var canvas: Canvas? = null
    private val mUndoPaths = ArrayList<CustomPath>()
    // make drawing stay
    private  val mPaths = ArrayList<CustomPath>()

    init {
        setUpDrawingAtStart()
    }

    private fun setUpDrawingAtStart(){
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color, mBrushSize)
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.SQUARE

        mCanvasPaint = Paint(Paint.DITHER_FLAG)

    }
    // If size has been change
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mCanvasBitMap = Bitmap.createBitmap(h,w, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitMap!!)
    }
    // When wanted to draw
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitMap!!, 0f, 0f, mCanvasPaint)

        for(path in mPaths) {
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path, mDrawPaint!!)
        }

        if (!mDrawPath!!.isEmpty) {
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize

                mDrawPath!!.reset()
                if (touchX != null && touchY != null) {
                        mDrawPath!!.moveTo(touchX, touchY)
                }

            }
            MotionEvent.ACTION_MOVE -> {
                if (touchX != null && touchY != null) {
                    mDrawPath!!.lineTo(touchX, touchY)
                }
            }
            MotionEvent.ACTION_UP -> {
                mPaths.add(mDrawPath!!)
                mDrawPath = CustomPath(color, mBrushSize)

            }
            else -> return  false
        }
        invalidate()
        return true
    }

    fun setSizeForBrush(newSize: Float) {
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, resources.displayMetrics)

        mDrawPaint!!.strokeWidth = mBrushSize
    }

    fun setColor(newColor: String) {
        color = Color.parseColor(newColor)
        mDrawPaint!!.color = color
    }

    fun clearCanvas(){
        if (mPaths.size > 0) {
            mPaths.clear()
            invalidate() // Invalidate the whole view. If the view is visible
        }
    }
    fun onClickUndo() {
        if (mPaths.size > 0) {
            mUndoPaths.add(mPaths.removeAt(mPaths.size - 1))
            invalidate() // Invalidate the whole view. If the view is visible
        }
    }


    internal  inner class CustomPath(var color: Int,
                                     var brushThickness: Float
                                     ): Path() {


    }
}
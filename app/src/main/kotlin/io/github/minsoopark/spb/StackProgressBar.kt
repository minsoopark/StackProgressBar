package io.github.minsoopark.spb;

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View;
import android.widget.TextView

import io.github.minsoopark.spb.R

/**
 * Created by mspark on 2015. 6. 23..
 */
public class StackProgressBar : View {
    private val stackBgPaint: Paint
    private val stackPaint: Paint

    public var stackColor: Int
    public var stackBgColor: Int
    public var cellHeight: Int
    public var cellsCount: Int
    public var min: Int
    public var max: Int
    public var progress: Int
        set(value) {
            if (value > max || value < min) {
                return
            }

            $progress = value

            invalidate()

            $progressListener?.onProgress($progress)
        }

    public var progressListener: OnProgressListener? = null

    public interface OnProgressListener {
        fun onProgress(progress: Int)
    }

    @jvmOverloads
    constructor(context: Context,
                attrs: AttributeSet, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.StackProgressBar)

        stackColor = ta.getInteger(R.styleable.StackProgressBar_stackColor, 0xFF000000.toInt())
        stackBgColor = ta.getInteger(R.styleable.StackProgressBar_stackBgColor, 0xFFDDDDDD.toInt())
        cellHeight = ta.getDimensionPixelSize(R.styleable.StackProgressBar_cellHeight, 300)
        cellsCount = ta.getInteger(R.styleable.StackProgressBar_cellsCount, 10)
        min = ta.getInteger(R.styleable.StackProgressBar_min, 0)
        max = ta.getInteger(R.styleable.StackProgressBar_max, 100)
        progress = ta.getInteger(R.styleable.StackProgressBar_progress, 0)

        stackBgPaint = createBackgroundPaint()
        stackPaint = createForegroundPaint()

        ta.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawProgress(canvas)
    }

    private fun drawProgress(canvas: Canvas) {
        val width = getMeasuredWidth()
        val height = getMeasuredHeight()

        val gapsCount = cellsCount - 1
        val gap = (height - (cellHeight * cellsCount)) / gapsCount

        var currentTop = 0

        for (i in 0..cellsCount.minus(1)) {
            val bg = cellsCount - (cellsCount * progress / max)
            val rect = Rect(0, currentTop, width, currentTop + cellHeight)

            val paint = if (i < bg) stackBgPaint else stackPaint
            canvas.drawRect(rect, paint)

            currentTop += cellHeight + gap
        }
    }

    private fun createBackgroundPaint(): Paint {
        val paint = Paint()
        paint.setStyle(Paint.Style.FILL)
        paint.setColor(stackBgColor)

        return paint
    }

    private fun createForegroundPaint(): Paint {
        val paint = Paint()
        paint.setStyle(Paint.Style.FILL)
        paint.setColor(stackColor)

        return paint
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.getAction()) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                progress = max - (max * event.getY() / getMeasuredHeight()).toInt()
                return true
            }
            MotionEvent.ACTION_OUTSIDE -> return true
        }
        return super.onTouchEvent(event)
    }
}

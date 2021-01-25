package com.udacity.nanodegree.loadapp

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.withStyledAttributes
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private val backgroundColor: Int =
        ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorAccent, null)
    private val textColor = ResourcesCompat.getColor(resources, R.color.white, null)
    private val rectangleProgressColor =
        ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)
    private var rectF: RectF
    var currentSweepAngle = 0f
    var rectangleProgress = RectF()

    private val LOADING = context.getString(R.string.button_loading)
    private val DOWNLOADING = context.getString(R.string.button_name)

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }

    private var fontSize = 0f
    private var circleRadius = 0f
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    private val textPaint = Paint().apply {
        color = textColor
        textSize = fontSize
        textAlign = Paint.Align.CENTER
    }

    private val rectanglePaint = Paint().apply {
        color = rectangleProgressColor
    }

    private val circlePaint = Paint().apply {
        color = drawColor
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        textSize = fontSize
    }
    private val clipRectRight = resources.getDimension(R.dimen.clipRectRight)
    private val clipRectBottom = resources.getDimension(R.dimen.clipRectBottom)
    private val clipRectTop = resources.getDimension(R.dimen.clipRectTop)
    private val clipRectLeft = resources.getDimension(R.dimen.clipRectLeft)

    init {
        isClickable = true
        val defFontSize = context.resources.getDimension(R.dimen.default_text_size);

        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            fontSize = getDimension(R.styleable.LoadingButton_textSize, defFontSize)
            circleRadius = getDimension(R.styleable.LoadingButton_circleRadius, defFontSize)
        }
        rectF = RectF(0f, 0f, circleRadius * 2f, circleRadius * 2f)
        textPaint.textSize = fontSize
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(backgroundColor)
        var currText = ""
        var showRect = false
        var showCircle = false
        when (buttonState) {
            ButtonState.Clicked -> {
                showRect = true
                showCircle = true
            }
            ButtonState.Loading -> {
                currText = LOADING
                showRect = true
                showCircle = true
            }
            ButtonState.Completed -> {
                showRect = false
                showCircle = false
                currText = DOWNLOADING
            }
        }

        val yPos =
            (height / 2 - (textPaint.descent() + textPaint.ascent()) / 2).toInt()


        val bounds = getTextBounds(currText, textPaint)
        if (showRect) {
            canvas.drawRect(rectangleProgress, rectanglePaint)
        }

        canvas.save()
        canvas.translate(
            widthSize / 2f + (bounds.right.toFloat() / 2) + circleRadius,
            heightSize / 2f - circleRadius
        )
        canvas.clipRect(
            clipRectLeft, clipRectTop,
            clipRectRight, clipRectBottom
        )
        if (showCircle) {
            canvas.drawArc(rectF, 0f, currentSweepAngle, true, circlePaint)
        }
        canvas.restore()
        canvas.drawText(currText, widthSize / 2f, yPos.toFloat(), textPaint)


    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)
        rectangleProgress = RectF(0f, 0f, 1f, heightSize.toFloat())
    }

    private fun startAnimation() {
        val cicleMultiplier = 3.6f
        val rectMultiplier = widthSize.toFloat() / 100
        buttonState = ButtonState.Clicked

        ValueAnimator.ofFloat(0f, 100f).apply {
            duration = 2000
            interpolator = LinearOutSlowInInterpolator()
            addUpdateListener { valueAnimator ->
                rectangleProgress.right = valueAnimator.animatedValue as Float * rectMultiplier
                currentSweepAngle = valueAnimator.animatedValue as Float * cicleMultiplier
                buttonState = ButtonState.Loading
                invalidate()
            }
            addListener({
                buttonState = ButtonState.Completed
                invalidate()
            })
        }?.start()

    }

    fun startCircleAnimation() {
        ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 650
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                currentSweepAngle = valueAnimator.animatedValue as Float
                invalidate()
            }
        }?.start()
    }

    fun startRectangleAnimation() {
        ValueAnimator.ofFloat(0f, widthSize.toFloat()).apply {
            duration = 650
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                rectangleProgress.right = valueAnimator.animatedValue as Float
                invalidate()
            }
        }?.start()
    }

    override fun performClick(): Boolean {
        buttonState = ButtonState.Clicked
        startAnimation()

        invalidate()
        super.performClick()
        return true
    }

    private fun getTextBounds(text: String, paint: Paint): Rect {
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        return bounds
    }

}
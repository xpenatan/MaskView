package com.github.xpenatan.maskview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class MaskView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var maskBackGroundColor: Int = Color.argb(150, 0, 0, 0)
    private val finalPath = Path()
    private val maskPath = Path()
    private val backgroundPath = Path()
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var pathViewID = -1
    private var pathView: PathView? = null

    init {
        initAttributes(attrs)
        finalPath.fillType = Path.FillType.EVEN_ODD

        backgroundPaint.style = Paint.Style.FILL;
        backgroundPaint.color = maskBackGroundColor
    }

    private fun initAttributes(attrs: AttributeSet?) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.MaskView)
        attributes.getColor(R.styleable.MaskView_mv_backgroundColor, maskBackGroundColor).let {
            maskBackGroundColor = it
        }
        attributes.getResourceId(R.styleable.MaskView_mv_pathView, -1).let {
            pathViewID = it
        }

        attributes.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        if (pathViewID != -1) {
            post {
                val parent = parent
                if (parent != null) {
                    val parentView = parent as View
                    pathView = parentView.findViewById(pathViewID)
                    pathView?.setUpdateListener(object: PathUpdateListener {
                        override fun onUpdate(pathView: PathView) {
                            invalidate()
                        }
                    })
                    invalidate()
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        pathView?.let {
            configBackground()
            configMaskPath()

            finalPath.reset()
            finalPath.addPath(maskPath)
            finalPath.addPath(backgroundPath)

            canvas?.drawPath(finalPath, backgroundPaint)
        }
    }

    private fun configBackground() {
        backgroundPath.reset()

        val widthF = width.toFloat()
        val heightF = height.toFloat()

        backgroundPath.moveTo(0f,0f)
        backgroundPath.lineTo(widthF, 0f)
        backgroundPath.lineTo(widthF, heightF)
        backgroundPath.lineTo(0f, heightF)
        backgroundPath.lineTo(0f, 0f)
    }

    private fun configMaskPath() {
        maskPath.reset()
        pathView?.let {
            maskPath.addPath(it.getPath())
        }
    }
}
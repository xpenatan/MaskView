package com.github.xpenatan.maskview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import androidx.core.graphics.PathParser
import org.xmlpull.v1.XmlPullParser

class PathView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val pathPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val originalPath = Path()
    private val scaledPath = Path()
    private val translatedPath = Path()
    private var xmlModel: XmlModel? = null
    private var vectorDrawableID = -1
    private val pathMatrix = Matrix()
    private val pathTranslatedMatrix = Matrix()
    private val mTempSrc = RectF()
    private val mTempDst = RectF()
    private var pathDirty = true
    private var updateListener: PathUpdateListener? = null
    private var scaleType = Matrix.ScaleToFit.CENTER
    private var previewColor = Color.BLACK

    init {
        initAttributes(attrs)
        initXml()
        initPaint()
    }

    private fun initAttributes(attrs: AttributeSet?) {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.PathView)
        attributes.getResourceId(R.styleable.PathView_pv_vectorDrawable, -1).let {
            vectorDrawableID = it
        }
        attributes.getInt(R.styleable.PathView_pv_scaleType, Matrix.ScaleToFit.CENTER.ordinal).let {
            Matrix.ScaleToFit.values().forEach { scaleToFit ->
                if(scaleToFit.ordinal == it)
                    scaleType = scaleToFit
            }
        }
        attributes.getColor(R.styleable.PathView_pv_previewColor, previewColor).let {
            previewColor = it
        }

        attributes.recycle()
    }

    private fun initXml() {
        if(vectorDrawableID == -1)
            return;

        val xmlParser = resources.getXml(vectorDrawableID)
        var tempPosition: Int = -1
        xmlModel = XmlModel()
        var event: Int = xmlParser.getEventType()

        while (event != XmlPullParser.END_DOCUMENT) {
            val name: String? = xmlParser.name
            when (event) {

                XmlPullParser.START_TAG -> {
                    if (name == "vector") {
                        tempPosition = getAttrPosition(xmlParser, "viewportWidth")
                        if(tempPosition != -1)
                            xmlModel?.viewportWidth = xmlParser.getAttributeValue(tempPosition).toFloat()

                        tempPosition = getAttrPosition(xmlParser, "viewportHeight")
                        if(tempPosition != -1)
                            xmlModel?.viewportHeight = xmlParser.getAttributeValue(tempPosition).toFloat()

                        tempPosition = getAttrPosition(xmlParser, "width")
                        if(tempPosition != -1) {
                            var attributeValue = xmlParser.getAttributeValue(tempPosition)
                            attributeValue = attributeValue.replace("dip", "")
                            xmlModel?.width = convertDpToPixel(getFloatFromXml(attributeValue)).toInt()
                        }

                        tempPosition = getAttrPosition(xmlParser, "height")
                        if(tempPosition != -1) {
                            var attributeValue = xmlParser.getAttributeValue(tempPosition)
                            attributeValue = attributeValue.replace("dip", "")
                            xmlModel?.height = convertDpToPixel(getFloatFromXml(attributeValue)).toInt()
                        }
                    }
                    else if (name == "path") {
                        val pathModel = XmlPathModel()

                        tempPosition = getAttrPosition(xmlParser, "fillColor")
                        if(tempPosition != -1)
                            pathModel.fillColor = Color.parseColor(xmlParser.getAttributeValue(tempPosition))

                        tempPosition = getAttrPosition(xmlParser, "pathData")
                        if(tempPosition != -1)
                            pathModel.pathData = xmlParser.getAttributeValue(tempPosition)

                        xmlModel?.paths?.add(pathModel)
                    }
                }
            }
            event = xmlParser.next()
        }
        initPath()
    }

    private fun initPath() {
        originalPath.reset()
        xmlModel?.paths?.let {
            if(it.isNotEmpty()) {
                val pathModel = it[0]
                originalPath.addPath(PathParser.createPathFromPathData(pathModel.pathData))
            }
        }
    }

    private fun initPaint() {
        pathPaint.style = Paint.Style.FILL
        pathPaint.color = previewColor
    }

    private fun getFloatFromXml(value: String): Float {
        return if (value.contains("dip")) value.substring(0, value.length - 3)
            .toFloat() else value.substring(0, value.length - 2).toFloat()
    }

    private fun getAttrPosition(xmlParser: XmlPullParser, attrName: String): Int {
        for (i in 0 until xmlParser.attributeCount) {
            if (xmlParser.getAttributeName(i) == attrName) {
                return i
            }
        }
        return -1
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasure = widthMeasureSpec
        var heightMeasure = heightMeasureSpec

        xmlModel?.let {
            val widthMode = MeasureSpec.getMode(widthMeasureSpec)
            val heightMode = MeasureSpec.getMode(heightMeasureSpec)
            if (widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST) {
                widthMeasure = MeasureSpec.makeMeasureSpec(it.width, MeasureSpec.EXACTLY)
            }
            if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST) {
                heightMeasure = MeasureSpec.makeMeasureSpec(it.height, MeasureSpec.EXACTLY)
            }
        }
        pathDirty = true
        setMeasuredDimension(widthMeasure, heightMeasure)

    }

    private fun convertDpToPixel(dp: Float): Float {
        return dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    override fun onDraw(canvas: Canvas?) {
        xmlModel?.let {
            if(pathDirty) {
                pathDirty = false
                calculateFinalPath(it)
            }
        }

        if(isInEditMode) {
            canvas?.drawPath(scaledPath, pathPaint)
        }
    }

    private fun calculateFinalPath(model: XmlModel) {
        scaledPath.reset()
        pathMatrix.reset()
        mTempSrc.set(0f, 0f, model.viewportWidth, model.viewportHeight);
        mTempDst.set(0f, 0f, width.toFloat(), height.toFloat());
        pathMatrix.setRectToRect(mTempSrc, mTempDst, scaleType)
        scaledPath.addPath(originalPath, pathMatrix)
        pathTranslatedMatrix.reset()
        pathTranslatedMatrix.setTranslate(left.toFloat(), top.toFloat())
        translatedPath.addPath(scaledPath, pathTranslatedMatrix)
        updateListener?.onUpdate(this)
    }

    fun getPath(): Path = translatedPath

    fun setUpdateListener(listener: PathUpdateListener) {
        this.updateListener = listener
    }
}
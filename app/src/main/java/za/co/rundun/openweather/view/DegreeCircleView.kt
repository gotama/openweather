package za.co.rundun.openweather.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat
import za.co.rundun.openweather.R
import za.co.rundun.openweather.view.FanSpeed.*
import java.lang.Integer.min

private enum class FanSpeed(val label: Int) {
    OFF(R.string.fan_off),
    LOW(R.string.fan_low),
    MEDIUM(R.string.fan_medium),
    HIGH(R.string.fan_high);

    fun next() = when (this) {
        OFF -> LOW
        LOW -> MEDIUM
        MEDIUM -> HIGH
        HIGH -> OFF
    }
}

private const val RADIUS_OFFSET_LABEL = 30          //Offset from dial radius to draw text label
private const val RADIUS_OFFSET_INDICATOR = -35     //Offset from dial radius to draw indicator

//  Custom Views are a true love of mine,  if I had time I would build this 360 degree dial
// to display the wind direction
//
class DegreeCircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = R.attr.materialCardViewStyle
) : View(context, attrs, defStyle) {
    private val rect: RectF = RectF(0f, 0f, 0f, 0f)
    private var startAngle = 0f

    var colors: List<Color> = emptyList()
        set(value) {
            if (field != value || arcs.isEmpty()) {
                field = value
                computeArcs()
            }
        }

    private var animator: ValueAnimator? = null
    private var currentSweepAngle = 0

    private val paint: Paint = Paint()
    private val greyColor = ContextCompat.getColor(context, R.color.dark_blue_gray)
    private var arcs = emptyList<Arc>()

    private val colorMap = mapOf(
        Color.WHITE to ContextCompat.getColor(context, R.color.white),
        Color.BLUE to ContextCompat.getColor(context, R.color.dark_blue_gray),
        Color.BLACK to ContextCompat.getColor(context, R.color.black),
        Color.RED to ContextCompat.getColor(context, R.color.african_violet),
        Color.GREEN to ContextCompat.getColor(context, R.color.dark_slate_gray)
    )

    private fun computeArcs() {
        arcs = if (colors.isEmpty()) {
            listOf(Arc(0f, 360f, greyColor))
        } else {
            val sweepSize: Float = 360f / colors.size
            colors.mapIndexed { index, color ->
                val startAngle = index * sweepSize
                Arc(start = startAngle, sweep = sweepSize, color = colorMap.getValue(color))
            }
        }
//        startAnimation()
    }

    init {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DegreeCircleView)
        startAngle = typedArray.getFloat(R.styleable.DegreeCircleView_startDegree, 0f)
        typedArray.recycle()

        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
        computeArcs()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0f, 0f, w.toFloat(), h.toFloat())
    }

    public fun startAnimation() {
        animator?.cancel()
        animator = ValueAnimator.ofInt(0, 360).apply {
            duration = 3000
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                currentSweepAngle = valueAnimator.animatedValue as Int
                invalidate()
            }
        }
        animator?.start()
    }

    override fun onDraw(canvas: Canvas) {
        arcs.forEach { arc ->
            if (currentSweepAngle > arc.start + arc.sweep) {
                paint.color = arc.color
                canvas.drawArc(
                    rect,
                    startAngle + arc.start,
                    arc.sweep,
                    true,
                    paint
                )
            } else {
                if (currentSweepAngle > arc.start) {
                    paint.color = arc.color
                    canvas.drawArc(
                        rect,
                        startAngle + arc.start,
                        currentSweepAngle - arc.start,
                        true,
                        paint
                    )
                }
            }
        }
    }
}


private class Arc(val start: Float, val sweep: Float, val color: Int)

enum class Color {
    WHITE, BLUE, BLACK, RED, GREEN
}
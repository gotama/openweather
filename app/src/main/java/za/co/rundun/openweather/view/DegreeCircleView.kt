package za.co.rundun.openweather.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import za.co.rundun.openweather.R
import kotlin.properties.Delegates

private const val DEGREE_OFFSET = 90f

//  Custom Views are a true love of mine,  if I had time I would build this 360 degree dial
//  to display the wind direction
//
class DegreeCircleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = R.attr.materialCardViewStyle
) : View(context, attrs, defStyle) {

    private var currentSweepAngle: Float = 0f
    private val rect: RectF = RectF(0f, 0f, 0f, 0f)
    private var animator: ValueAnimator? = null
    private val paint: Paint = Paint()
    private var arcs = emptyList<Arc>()
    private var sweepAngle: Float = 0f
    private var degreeAngle: Float by Delegates.observable(0f) { prop, old, new ->
        computeArcs()
        startAnimation()
    }

    init {
        paint.style = Paint.Style.FILL
        paint.isAntiAlias = true
    }

    fun updateDegreeAngle(degree: Float, sweep: Float) {
        sweepAngle = sweep
        degreeAngle = degree
    }

    private fun computeArcs() {
        arcs = listOf(
            Arc(
                start = degreeAngle,
                sweep = sweepAngle * 2,
                ContextCompat.getColor(context, R.color.dark_blue_gray),
                false
            ),
            Arc(
                start = 0f,
                sweep = 360f,
                ContextCompat.getColor(context, R.color.black),
                true
            )
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0f, 0f, w.toFloat(), h.toFloat())
    }

    private fun startAnimation() {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 500
            interpolator = LinearInterpolator()
            addUpdateListener { valueAnimator ->
                currentSweepAngle = valueAnimator.animatedValue as Float
                invalidate()
            }
        }
        animator?.start()
    }

    override fun onDraw(canvas: Canvas) {
        arcs.forEach { arc ->
            if (arc.outline) {
                paint.strokeWidth = 3f
                paint.color = arc.color
                paint.style = Paint.Style.STROKE
                Log.d("ANIMATE_MONITOR: ", "----------")
                Log.d("ANIMATE_MONITOR: ", "currentSweepAngle: $currentSweepAngle")
                Log.d("ANIMATE_MONITOR: ", "Arc.start: " + arc.start.toString())
                Log.d("ANIMATE_MONITOR: ", "Arc.sweep: " + arc.sweep.toString())
                canvas.drawArc(
                    rect,
                    arc.start,
                    currentSweepAngle,
                    true,
                    paint
                )
            } else {
                paint.color = arc.color
                paint.style = Paint.Style.FILL
                canvas.drawArc(
                    rect,
                    arc.start.minus(DEGREE_OFFSET),
                    arc.sweep,
                    true,
                    paint
                )
            }
        }
    }
}

private class Arc(val start: Float, val sweep: Float, val color: Int, val outline: Boolean)
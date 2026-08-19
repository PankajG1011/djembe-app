package com.djembe.android.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.djembe.android.audio.DjembeSoundEngine
import kotlin.math.hypot
import kotlin.math.min

/**
 * A single circular drum head split into three concentric playing zones:
 *   - Center (BASS)
 *   - Middle ring (TONE)
 *   - Outer ring / edge (SLAP)
 *
 * Supports multi-touch so two fingers (or two hands, on a tablet) can play
 * independently - important for realistic djembe technique practice.
 */
class DjembeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    private val soundEngine = DjembeSoundEngine(context)

    private val headPaint = Paint().apply { color = Color.parseColor("#D9A066"); isAntiAlias = true }
    private val rimPaint = Paint().apply { color = Color.parseColor("#5C3A21"); isAntiAlias = true }
    private val zoneLinePaint = Paint().apply {
        color = Color.parseColor("#00000040")
        style = Paint.Style.STROKE
        strokeWidth = 3f
        isAntiAlias = true
    }

    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f

    // Zone boundaries as a fraction of radius
    private val bassZoneFraction = 0.35f
    private val toneZoneFraction = 0.75f
    // beyond toneZoneFraction, up to radius, is the SLAP zone

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        radius = min(w, h) / 2f * 0.9f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Rim
        canvas.drawCircle(centerX, centerY, radius, rimPaint)
        // Head
        canvas.drawCircle(centerX, centerY, radius * toneZoneFraction + (radius * (1 - toneZoneFraction)) * 0.9f, headPaint)
        // Zone guide lines (subtle - visual affordance for where zones are)
        canvas.drawCircle(centerX, centerY, radius * bassZoneFraction, zoneLinePaint)
        canvas.drawCircle(centerX, centerY, radius * toneZoneFraction, zoneLinePaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // Handle each pointer independently so multi-finger playing works
        val actionIndex = event.actionIndex
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_POINTER_DOWN -> {
                triggerFromTouch(event.getX(actionIndex), event.getY(actionIndex), event.getPressure(actionIndex))
                return true
            }
        }
        return true
    }

    private fun triggerFromTouch(x: Float, y: Float, pressure: Float) {
        val distance = hypot((x - centerX).toDouble(), (y - centerY).toDouble()).toFloat()
        if (distance > radius) return // touch landed outside the drum head

        val tone = when {
            distance <= radius * bassZoneFraction -> DjembeSoundEngine.Tone.BASS
            distance <= radius * toneZoneFraction -> DjembeSoundEngine.Tone.TONE
            else -> DjembeSoundEngine.Tone.SLAP
        }

        // Pressure isn't reliable on all devices (often always 1.0); clamp and
        // use it as a soft velocity signal rather than the sole source of dynamics.
        val velocity = pressure.coerceIn(0.4f, 1f)
        soundEngine.play(tone, velocity)
    }

    fun release() {
        soundEngine.release()
    }
}

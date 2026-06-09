package com.app.musicplayer.core.media

import kotlin.math.*

/**
 * Digital Biquad Filter implementation.
 * Supports Peaking EQ, Low Shelf, High Shelf filter types.
 * Based on "Audio EQ Cookbook" by Robert Bristow-Johnson.
 *
 * Transfer function:
 * H(z) = (b0 + b1*z^-1 + b2*z^-2) / (a0 + a1*z^-1 + a2*z^-2)
 */
class BiquadFilter {

    private var b0 = 1.0
    private var b1 = 0.0
    private var b2 = 0.0
    private var a1 = 0.0
    private var a2 = 0.0

    // State variables (Direct Form II)
    private var z1 = 0.0
    private var z2 = 0.0

    /**
     * Configure as Peaking EQ filter.
     *
     * @param sampleRate Audio sample rate in Hz
     * @param centerFreq Center frequency in Hz
     * @param gainDb Gain in dB (positive = boost, negative = cut)
     * @param q Quality factor (bandwidth)
     */
    fun configurePeaking(sampleRate: Int, centerFreq: Int, gainDb: Float, q: Double) {
        val A = 10.0.pow(gainDb / 40.0) // sqrt of linear gain
        val w0 = 2.0 * PI * centerFreq / sampleRate
        val sinW0 = sin(w0)
        val cosW0 = cos(w0)
        val alpha = sinW0 / (2.0 * q)

        val a0 = 1.0 + alpha / A
        b0 = (1.0 + alpha * A) / a0
        b1 = (-2.0 * cosW0) / a0
        b2 = (1.0 - alpha * A) / a0
        a1 = (-2.0 * cosW0) / a0
        a2 = (1.0 - alpha / A) / a0
    }

    /**
     * Configure as Low Shelf filter.
     */
    fun configureLowShelf(sampleRate: Int, freq: Int, gainDb: Float, q: Double) {
        val A = 10.0.pow(gainDb / 40.0)
        val w0 = 2.0 * PI * freq / sampleRate
        val sinW0 = sin(w0)
        val cosW0 = cos(w0)
        val alpha = sinW0 / (2.0 * q)
        val sqrtA = sqrt(A)

        val a0 = (A + 1) + (A - 1) * cosW0 + 2 * sqrtA * alpha
        b0 = (A * ((A + 1) - (A - 1) * cosW0 + 2 * sqrtA * alpha)) / a0
        b1 = (2 * A * ((A - 1) - (A + 1) * cosW0)) / a0
        b2 = (A * ((A + 1) - (A - 1) * cosW0 - 2 * sqrtA * alpha)) / a0
        a1 = (-2 * ((A - 1) + (A + 1) * cosW0)) / a0
        a2 = ((A + 1) + (A - 1) * cosW0 - 2 * sqrtA * alpha) / a0
    }

    /**
     * Configure as High Shelf filter.
     */
    fun configureHighShelf(sampleRate: Int, freq: Int, gainDb: Float, q: Double) {
        val A = 10.0.pow(gainDb / 40.0)
        val w0 = 2.0 * PI * freq / sampleRate
        val sinW0 = sin(w0)
        val cosW0 = cos(w0)
        val alpha = sinW0 / (2.0 * q)
        val sqrtA = sqrt(A)

        val a0 = (A + 1) - (A - 1) * cosW0 + 2 * sqrtA * alpha
        b0 = (A * ((A + 1) + (A - 1) * cosW0 + 2 * sqrtA * alpha)) / a0
        b1 = (-2 * A * ((A - 1) + (A + 1) * cosW0)) / a0
        b2 = (A * ((A + 1) + (A - 1) * cosW0 - 2 * sqrtA * alpha)) / a0
        a1 = (2 * ((A - 1) - (A + 1) * cosW0)) / a0
        a2 = ((A + 1) - (A - 1) * cosW0 - 2 * sqrtA * alpha) / a0
    }

    /**
     * Process a single audio sample (Direct Form II Transposed).
     */
    fun process(input: Float): Float {
        val x = input.toDouble()
        val y = b0 * x + z1
        z1 = b1 * x - a1 * y + z2
        z2 = b2 * x - a2 * y
        return y.toFloat()
    }

    /**
     * Reset filter state (call when seeking or switching tracks).
     */
    fun reset() {
        z1 = 0.0
        z2 = 0.0
    }
}

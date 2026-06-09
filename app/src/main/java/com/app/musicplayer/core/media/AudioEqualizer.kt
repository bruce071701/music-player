package com.app.musicplayer.core.media

import javax.inject.Inject
import javax.inject.Singleton

/**
 * 10-band parametric equalizer using Biquad (peaking EQ) filters.
 * Based on Audio EQ Cookbook by Robert Bristow-Johnson.
 *
 * Center frequencies: 31, 62, 125, 250, 500, 1000, 2000, 4000, 8000, 16000 Hz
 */
@Singleton
class AudioEqualizer @Inject constructor() {

    companion object {
        val CENTER_FREQUENCIES = intArrayOf(31, 62, 125, 250, 500, 1000, 2000, 4000, 8000, 16000)
        const val BAND_COUNT = 10
        const val DEFAULT_Q = 1.414 // sqrt(2), standard Q value
        const val MIN_GAIN_DB = -12f
        const val MAX_GAIN_DB = 12f
    }

    private val filters = Array(BAND_COUNT) { BiquadFilter() }
    private var sampleRate: Int = 44100
    private var enabled: Boolean = false
    private var preampGain: Float = 0f
    private val bandGains = FloatArray(BAND_COUNT) { 0f }

    fun setEnabled(enabled: Boolean) {
        this.enabled = enabled
    }

    fun isEnabled(): Boolean = enabled

    fun setSampleRate(rate: Int) {
        sampleRate = rate
        recalculateAll()
    }

    fun setBandGain(band: Int, gainDb: Float) {
        if (band !in 0 until BAND_COUNT) return
        bandGains[band] = gainDb.coerceIn(MIN_GAIN_DB, MAX_GAIN_DB)
        filters[band].configurePeaking(sampleRate, CENTER_FREQUENCIES[band], bandGains[band], DEFAULT_Q)
    }

    fun getBandGain(band: Int): Float {
        return if (band in 0 until BAND_COUNT) bandGains[band] else 0f
    }

    fun setAllBands(gains: List<Float>) {
        gains.forEachIndexed { index, gain ->
            if (index < BAND_COUNT) {
                bandGains[index] = gain.coerceIn(MIN_GAIN_DB, MAX_GAIN_DB)
            }
        }
        recalculateAll()
    }

    fun setPreamp(gainDb: Float) {
        preampGain = gainDb.coerceIn(MIN_GAIN_DB, MAX_GAIN_DB)
    }

    fun getPreamp(): Float = preampGain

    fun reset() {
        for (i in 0 until BAND_COUNT) {
            bandGains[i] = 0f
            filters[i].reset()
        }
        preampGain = 0f
    }

    /**
     * Process a single audio sample through the EQ chain.
     */
    fun processSample(sample: Float): Float {
        if (!enabled) return sample

        // Apply preamp
        var result = sample * Math.pow(10.0, (preampGain / 20.0).toDouble()).toFloat()

        // Pass through each filter band
        for (filter in filters) {
            result = filter.process(result)
        }

        return result
    }

    /**
     * Process a buffer of audio samples (mono).
     */
    fun processBuffer(buffer: FloatArray): FloatArray {
        if (!enabled) return buffer
        for (i in buffer.indices) {
            buffer[i] = processSample(buffer[i])
        }
        return buffer
    }

    private fun recalculateAll() {
        for (i in 0 until BAND_COUNT) {
            filters[i].configurePeaking(sampleRate, CENTER_FREQUENCIES[i], bandGains[i], DEFAULT_Q)
        }
    }
}

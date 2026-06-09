package com.app.musicplayer.core.model

data class EqPreset(
    val id: Long = 0,
    val name: String,
    val isBuiltin: Boolean = false,
    val bands: List<Float> = List(10) { 0f },  // 10-band gains in dB
    val preamp: Float = 0f                      // Pre-amplifier gain in dB (-12 to +12)
) {
    companion object {
        val BAND_FREQUENCIES = intArrayOf(31, 62, 125, 250, 500, 1000, 2000, 4000, 8000, 16000)

        fun flat() = EqPreset(name = "Flat", isBuiltin = true)
        fun rock() = EqPreset(name = "Rock", isBuiltin = true, bands = listOf(4f, 3f, 1f, 0f, -1f, 0f, 2f, 3f, 4f, 4f))
        fun pop() = EqPreset(name = "Pop", isBuiltin = true, bands = listOf(-1f, 1f, 3f, 4f, 3f, 0f, -1f, -1f, 1f, 2f))
        fun jazz() = EqPreset(name = "Jazz", isBuiltin = true, bands = listOf(3f, 2f, 1f, 2f, -1f, -1f, 0f, 1f, 2f, 3f))
        fun classical() = EqPreset(name = "Classical", isBuiltin = true, bands = listOf(4f, 3f, 2f, 1f, -1f, -1f, 0f, 2f, 3f, 4f))
        fun bass() = EqPreset(name = "Bass Boost", isBuiltin = true, bands = listOf(6f, 5f, 4f, 2f, 0f, 0f, 0f, 0f, 0f, 0f))
        fun vocal() = EqPreset(name = "Vocal", isBuiltin = true, bands = listOf(-2f, -1f, 0f, 2f, 4f, 4f, 3f, 1f, 0f, -1f))

        fun builtinPresets() = listOf(flat(), rock(), pop(), jazz(), classical(), bass(), vocal())
    }
}

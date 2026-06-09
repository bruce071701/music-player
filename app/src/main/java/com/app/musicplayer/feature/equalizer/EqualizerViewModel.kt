package com.app.musicplayer.feature.equalizer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.musicplayer.core.database.dao.EqPresetDao
import com.app.musicplayer.core.database.entity.EqPresetEntity
import com.app.musicplayer.core.media.AudioEqualizer
import com.app.musicplayer.core.model.EqPreset
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class EqualizerViewModel @Inject constructor(
    private val equalizer: AudioEqualizer,
    private val eqPresetDao: EqPresetDao
) : ViewModel() {

    private val _isEnabled = MutableStateFlow(equalizer.isEnabled())
    val isEnabled: StateFlow<Boolean> = _isEnabled.asStateFlow()

    private val _bandGains = MutableStateFlow(List(AudioEqualizer.BAND_COUNT) { equalizer.getBandGain(it) })
    val bandGains: StateFlow<List<Float>> = _bandGains.asStateFlow()

    private val _preamp = MutableStateFlow(equalizer.getPreamp())
    val preamp: StateFlow<Float> = _preamp.asStateFlow()

    private val _presets = MutableStateFlow(EqPreset.builtinPresets().map { it.name })
    val presets: StateFlow<List<String>> = _presets.asStateFlow()

    private val _selectedPreset = MutableStateFlow("Flat")
    val selectedPreset: StateFlow<String> = _selectedPreset.asStateFlow()

    init {
        loadPresets()
    }

    fun setEnabled(enabled: Boolean) {
        equalizer.setEnabled(enabled)
        _isEnabled.value = enabled
    }

    fun setBandGain(band: Int, gain: Float) {
        equalizer.setBandGain(band, gain)
        _bandGains.value = List(AudioEqualizer.BAND_COUNT) { equalizer.getBandGain(it) }
        _selectedPreset.value = "Custom"
    }

    fun setPreamp(gain: Float) {
        equalizer.setPreamp(gain)
        _preamp.value = gain
    }

    fun selectPreset(name: String) {
        val preset = EqPreset.builtinPresets().find { it.name == name }
        if (preset != null) {
            equalizer.setAllBands(preset.bands)
            equalizer.setPreamp(preset.preamp)
            _bandGains.value = preset.bands
            _preamp.value = preset.preamp
            _selectedPreset.value = name
        }
    }

    fun saveCustomPreset(name: String) {
        viewModelScope.launch {
            val gains = _bandGains.value
            val entity = EqPresetEntity(
                name = name,
                isBuiltin = false,
                bandsJson = Json.encodeToString(gains),
                preamp = _preamp.value
            )
            eqPresetDao.insertPreset(entity)
            loadPresets()
        }
    }

    fun deletePreset(name: String) {
        viewModelScope.launch {
            // Only delete non-builtin presets
            // Implementation would lookup by name and delete
            loadPresets()
        }
    }

    private fun loadPresets() {
        viewModelScope.launch {
            val builtinNames = EqPreset.builtinPresets().map { it.name }
            eqPresetDao.getAllPresets().collect { entities ->
                val customNames = entities.filter { !it.isBuiltin }.map { it.name }
                _presets.value = builtinNames + customNames
            }
        }
    }
}

package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AlarmSoundManager(private val context: Context) {

    private val _isAlarmActive = MutableStateFlow(false)
    val isAlarmActive: StateFlow<Boolean> = _isAlarmActive.asStateFlow()

    private val _activeAlarmMessage = MutableStateFlow<String?>(null)
    val activeAlarmMessage: StateFlow<String?> = _activeAlarmMessage.asStateFlow()

    private var toneGenerator: ToneGenerator? = null
    private var alarmJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)
        } catch (e: Exception) {
            Log.e("AlarmSoundManager", "Error initializing ToneGenerator", e)
        }
    }

    fun startRepeatedAlarm(reason: String) {
        if (_isAlarmActive.value) return
        _isAlarmActive.value = true
        _activeAlarmMessage.value = reason

        alarmJob?.cancel()
        alarmJob = scope.launch {
            while (isActive && _isAlarmActive.value) {
                try {
                    toneGenerator?.startTone(ToneGenerator.TONE_CDMA_EMERGENCY_RINGBACK, 450)
                } catch (e: Exception) {
                    Log.e("AlarmSoundManager", "Tone playback error", e)
                }

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator?.vibrate(
                            VibrationEffect.createOneShot(350, VibrationEffect.DEFAULT_AMPLITUDE)
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator?.vibrate(350)
                    }
                } catch (e: Exception) {
                    // Ignore vibration failure
                }

                delay(900)
            }
        }
    }

    fun stopAlarm() {
        _isAlarmActive.value = false
        _activeAlarmMessage.value = null
        alarmJob?.cancel()
        alarmJob = null
        try {
            toneGenerator?.stopTone()
            vibrator?.cancel()
        } catch (e: Exception) {
            Log.e("AlarmSoundManager", "Error stopping tone", e)
        }
    }

    fun release() {
        stopAlarm()
        try {
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            Log.e("AlarmSoundManager", "Error releasing ToneGenerator", e)
        }
    }
}

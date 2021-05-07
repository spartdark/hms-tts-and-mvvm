package com.spartdark.ttsandmvvm.tts

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.huawei.hms.mlsdk.common.MLApplication
import com.huawei.hms.mlsdk.tts.*


class TextToSpeechViewModel : ViewModel() {

    companion object {
        private const val TTS_ZH_HANS = "zh-Hans"
        private const val TTS_SPEAKER_MALE_ZH = "zh-Hans-st-1"
        private lateinit var mlTtsEngine: MLTtsEngine
        var isPlaying = false

    }

    // The current word
    private val _mText = MutableLiveData<String>()
    val mText: LiveData<String>
        get() = _mText


    init {
        MLApplication.getInstance().apiKey = ""
        initTTS()
    }


    private fun initTTS() {
        // Use customized parameter settings to create a TTS engine.
        // Use customized parameter settings to create a TTS engine.
        val mlTtsConfig = MLTtsConfig() // Set the text converted from speech to Chinese.
                .setLanguage(MLTtsConstants.TTS_EN_US) // Set the Chinese timbre.
                .setPerson(MLTtsConstants.TTS_SPEAKER_FEMALE_EN) // Set the speech speed. The range is (0,5.0]. 1.0 indicates a normal speed.
                .setSpeed(1.0f) // Set the volume. The range is (0,2). 1.0 indicates a normal volume.
                .setVolume(1.0f)
        mlTtsEngine = MLTtsEngine(mlTtsConfig)
// Set the volume of the built-in player, in dBs. The value is in the range of [0, 100].
// Set the volume of the built-in player, in dBs. The value is in the range of [0, 100].
        mlTtsEngine.setPlayerVolume(20)
// Update the configuration when the engine is running.
// Update the configuration when the engine is running.
        mlTtsEngine.updateConfig(mlTtsConfig)
    }


    fun playText() {
        mlTtsEngine.setTtsCallback(getTTSCallBack())
        /**
         *First parameter sourceText: text information to be synthesized. The value can contain a maximum of 500 characters.
         * Second parameter indicating the synthesis mode: The format is configA | configB | configC.
         *configA:
         *    MLTtsEngine.QUEUE_APPEND: After a TTS task is generated, the task is processed as follows: If playback is going on, the task is added to the queue for execution in sequence; if playback pauses, the playback is resumed and the task is added to the queue for execution in sequence; if there is no playback, the TTS task is executed immediately.
         *    MLTtsEngine.QUEUE_FLUSH: The ongoing TTS task and playback are stopped immediately, all TTS tasks in the queue are cleared, and the current TTS task is executed immediately and played.
         *configB:
         *    MLTtsEngine.OPEN_STREAM: The synthesized audio data is output through onAudioAvailable.
         *configC:
         *    MLTtsEngine.EXTERNAL_PLAYBACK: external playback mode. The player provided by the SDK is shielded. You need to process the audio output by the onAudioAvailable callback API. In this case, the playback-related APIs in the callback APIs become invalid, and only the callback APIs related to audio synthesis can be listened.
         */
// Use the built-in player of the SDK to play speech in queuing mode.
        /**
         * First parameter sourceText: text information to be synthesized. The value can contain a maximum of 500 characters.
         * Second parameter indicating the synthesis mode: The format is configA | configB | configC.
         * configA:
         * MLTtsEngine.QUEUE_APPEND: After a TTS task is generated, the task is processed as follows: If playback is going on, the task is added to the queue for execution in sequence; if playback pauses, the playback is resumed and the task is added to the queue for execution in sequence; if there is no playback, the TTS task is executed immediately.
         * MLTtsEngine.QUEUE_FLUSH: The ongoing TTS task and playback are stopped immediately, all TTS tasks in the queue are cleared, and the current TTS task is executed immediately and played.
         * configB:
         * MLTtsEngine.OPEN_STREAM: The synthesized audio data is output through onAudioAvailable.
         * configC:
         * MLTtsEngine.EXTERNAL_PLAYBACK: external playback mode. The player provided by the SDK is shielded. You need to process the audio output by the onAudioAvailable callback API. In this case, the playback-related APIs in the callback APIs become invalid, and only the callback APIs related to audio synthesis can be listened.
         */
// Use the built-in player of the SDK to play speech in queuing mode.
        Log.i("My Text", _mText.value.toString())
        val id = mlTtsEngine.speak(_mText.value.toString(), MLTtsEngine.EXTERNAL_PLAYBACK)
// In queuing mode, the synthesized audio stream is output through onAudioAvailable, and the built-in player of the SDK is used to play the speech.
// String id = mlTtsEngine.speak(sourceText, MLTtsEngine.QUEUE_APPEND | MLTtsEngine.OPEN_STREAM);
// In queuing mode, the synthesized audio stream is output through onAudioAvailable, and the audio stream is not played, but controlled by you.
// String id = mlTtsEngine.speak(sourceText, MLTtsEngine.QUEUE_APPEND | MLTtsEngine.OPEN_STREAM | MLTtsEngine.EXTERNAL_PLAYBACK);
    }

    private fun getTTSCallBack(): MLTtsCallback? {
        return object : MLTtsCallback {
            override fun onError(taskId: String, err: MLTtsError) {
                // Processing logic for TTS failure.
                Log.d("TTSGulf", err.errorMsg)
            }

            override fun onWarn(taskId: String, warn: MLTtsWarn) {
                // Alarm handling without affecting service logic.
                Log.d("TTSGulf", warn.warnMsg)
            }

            // Return the mapping between the currently played segment and text. start: start position of the audio segment in the input text; end (excluded): end position of the audio segment in the input text.
            override fun onRangeStart(taskId: String, start: Int, end: Int) {
                // Process the mapping between the currently played segment and text.
                Log.d("TTSGulf", "OnRangeStart")
            }

            // taskId: ID of a TTS task corresponding to the audio.
            // audioFragment: audio data.
            // offset: offset of the audio segment to be transmitted in the queue. One TTS task corresponds to a TTS queue.
            // range: text area where the audio segment to be transmitted is located; range.first (included): start position; range.second (excluded): end position.


            override fun onAudioAvailable(
                    p0: String?,
                    p1: MLTtsAudioFragment?,
                    p2: Int,
                    p3: android.util.Pair<Int, Int>?,
                    p4: Bundle?
            ) {
                Log.d("TTSGulf", "onAudioAvailable")
            }


            override fun onEvent(taskId: String, eventId: Int, bundle: Bundle) {
                // Callback method of a TTS event. eventId indicates the event name.
                when (eventId) {
                    MLTtsConstants.EVENT_PLAY_START ->                         // Called when playback starts.
                        isPlaying = true
                    MLTtsConstants.EVENT_PLAY_STOP -> {
                        // Called when playback stops.
                        val isInterrupted =
                                bundle.getBoolean(MLTtsConstants.EVENT_PLAY_STOP_INTERRUPTED)
                        isPlaying = false
                    }
                    MLTtsConstants.EVENT_PLAY_RESUME -> {
                    }
                    MLTtsConstants.EVENT_PLAY_PAUSE -> {
                    }
                    MLTtsConstants.EVENT_SYNTHESIS_START -> {
                    }
                    MLTtsConstants.EVENT_SYNTHESIS_END -> {
                    }
                    MLTtsConstants.EVENT_SYNTHESIS_COMPLETE -> {                        // TTS is complete. All synthesized audio streams are passed to the app.
                        var isInterrupted1: Boolean =
                                bundle.getBoolean(MLTtsConstants.EVENT_SYNTHESIS_INTERRUPTED)
                    }
                    else -> {
                    }
                }
            }
        }
    }

    fun controlTTSEngine(action: String) {
        when (action) {
            "pause" -> {

                // Pause playback.
                mlTtsEngine.pause()
            }
            "resume" -> {

                // Resume playback.
                mlTtsEngine.resume()
            }
            "stop" -> {

                // Stop playback.
                mlTtsEngine.stop()
            }
            "shutdown" -> {
                if (mlTtsEngine != null) {
                    mlTtsEngine.shutdown()
                }
            }
        }
    }

}
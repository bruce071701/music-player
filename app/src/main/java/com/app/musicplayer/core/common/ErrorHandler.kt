package com.app.musicplayer.core.common

import androidx.media3.common.PlaybackException

/**
 * Unified error handling for the application.
 * Categorizes errors and provides user-friendly messages.
 */
sealed class AppError(val message: String) {
    data class FileNotFound(val path: String) : AppError("File not found, skipped")
    data class DecodeFailed(val fileName: String) : AppError("Unable to play this file")
    data class FormatNotSupported(val format: String) : AppError("Format not supported")
    data class PermissionDenied(val permission: String) : AppError("Storage permission required")
    data class NetworkError(val cause: Throwable?) : AppError("Network connection failed")
    data class ApiQuotaExhausted(val service: String) : AppError("Daily search limit reached, will reset tomorrow")
    data class PlaybackError(val cause: Throwable?) : AppError("Playback error")
    data class ServiceKilled(val info: String) : AppError("Playback service recovered")
}

object ErrorHandler {

    /**
     * Classify a PlaybackException into an AppError type.
     */
    fun classifyPlaybackError(error: PlaybackException): AppError {
        return when {
            error.errorCode == PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND ->
                AppError.FileNotFound("")
            error.errorCode == PlaybackException.ERROR_CODE_DECODING_FAILED ->
                AppError.DecodeFailed("")
            error.errorCode == PlaybackException.ERROR_CODE_DECODING_FORMAT_UNSUPPORTED ->
                AppError.FormatNotSupported("")
            error.errorCode == PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED ->
                AppError.NetworkError(error)
            else ->
                AppError.PlaybackError(error)
        }
    }

    /**
     * Determine the appropriate recovery action for an error.
     */
    fun getRecoveryAction(error: AppError): RecoveryAction {
        return when (error) {
            is AppError.FileNotFound -> RecoveryAction.SKIP_TO_NEXT
            is AppError.DecodeFailed -> RecoveryAction.SKIP_TO_NEXT
            is AppError.FormatNotSupported -> RecoveryAction.SKIP_TO_NEXT
            is AppError.PermissionDenied -> RecoveryAction.REQUEST_PERMISSION
            is AppError.NetworkError -> RecoveryAction.RETRY
            is AppError.ApiQuotaExhausted -> RecoveryAction.SHOW_MESSAGE
            is AppError.PlaybackError -> RecoveryAction.STOP
            is AppError.ServiceKilled -> RecoveryAction.RESTORE_STATE
        }
    }
}

enum class RecoveryAction {
    SKIP_TO_NEXT,
    STOP,
    RETRY,
    SHOW_MESSAGE,
    REQUEST_PERMISSION,
    RESTORE_STATE
}

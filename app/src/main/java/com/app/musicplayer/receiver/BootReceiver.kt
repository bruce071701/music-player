package com.app.musicplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // TODO: Check if auto-resume is enabled in preferences
            // If enabled, start MusicPlaybackService and restore last playback state
        }
    }
}

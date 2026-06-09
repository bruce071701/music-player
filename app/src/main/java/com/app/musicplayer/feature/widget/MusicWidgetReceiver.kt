package com.app.musicplayer.feature.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.app.musicplayer.MainActivity
import com.penji.musicplayer.offline.R

/**
 * 4x2 Widget (compact single row)
 */
class MusicWidget4x2Receiver : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { widgetId ->
            val views = buildRemoteViews(context)
            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }

    private fun buildRemoteViews(context: Context): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_music_compact)

        // Click on title opens app
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val openPi = PendingIntent.getActivity(context, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.widget_title, openPi)

        // Previous button
        val prevIntent = Intent(ACTION_PREVIOUS).setPackage(context.packageName)
        val prevPi = PendingIntent.getBroadcast(context, 1, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.widget_prev, prevPi)

        // Play/Pause button
        val playIntent = Intent(ACTION_PLAY_PAUSE).setPackage(context.packageName)
        val playPi = PendingIntent.getBroadcast(context, 2, playIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.widget_play, playPi)

        // Next button
        val nextIntent = Intent(ACTION_NEXT).setPackage(context.packageName)
        val nextPi = PendingIntent.getBroadcast(context, 3, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        views.setOnClickPendingIntent(R.id.widget_next, nextPi)

        return views
    }

    companion object {
        const val ACTION_PLAY_PAUSE = "com.penji.musicplayer.offline.ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "com.penji.musicplayer.offline.ACTION_NEXT"
        const val ACTION_PREVIOUS = "com.penji.musicplayer.offline.ACTION_PREVIOUS"
    }
}

/**
 * 4x1 Widget (same compact layout)
 */
class MusicWidget4x1Receiver : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { widgetId ->
            val views = RemoteViews(context.packageName, R.layout.widget_music_compact)

            val openIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val openPi = PendingIntent.getActivity(context, 10, openIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget_title, openPi)

            val prevPi = PendingIntent.getBroadcast(context, 11, Intent(MusicWidget4x2Receiver.ACTION_PREVIOUS).setPackage(context.packageName), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget_prev, prevPi)

            val playPi = PendingIntent.getBroadcast(context, 12, Intent(MusicWidget4x2Receiver.ACTION_PLAY_PAUSE).setPackage(context.packageName), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget_play, playPi)

            val nextPi = PendingIntent.getBroadcast(context, 13, Intent(MusicWidget4x2Receiver.ACTION_NEXT).setPackage(context.packageName), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widget_next, nextPi)

            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }
}

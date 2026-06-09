package com.app.musicplayer.feature.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.action.actionSendBroadcast
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.app.musicplayer.MainActivity

/**
 * 4x2 Standard Widget: Cover + Title + Controls + Progress
 */
class MusicWidget4x2 : GlanceAppWidget() {

    override val sizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            MusicWidgetContent()
        }
    }
}

class MusicWidget4x2Receiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MusicWidget4x2()
}

/**
 * 4x1 Compact Widget: Title + Basic controls
 */
class MusicWidget4x1 : GlanceAppWidget() {

    override val sizeMode = SizeMode.Single

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            CompactWidgetContent()
        }
    }
}

class MusicWidget4x1Receiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = MusicWidget4x1()
}

private const val ACTION_PLAY_PAUSE = "com.app.musicplayer.ACTION_PLAY_PAUSE"
private const val ACTION_NEXT = "com.app.musicplayer.ACTION_NEXT"
private const val ACTION_PREVIOUS = "com.app.musicplayer.ACTION_PREVIOUS"

@Composable
private fun MusicWidgetContent() {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(12.dp)
            .clickable(actionStartActivity<MainActivity>())
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Album art placeholder
            Box(
                modifier = GlanceModifier.size(56.dp)
            ) {
                Text(
                    text = "♪",
                    style = TextStyle(fontSize = 24.sp)
                )
            }

            Spacer(modifier = GlanceModifier.width(12.dp))

            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = "Music Player",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    maxLines = 1
                )
                Text(
                    text = "Tap to open",
                    style = TextStyle(fontSize = 12.sp),
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        // Controls row with broadcast actions
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⏮",
                modifier = GlanceModifier
                    .padding(8.dp)
                    .clickable(actionSendBroadcast(Intent(ACTION_PREVIOUS).setPackage("com.app.musicplayer"))),
                style = TextStyle(fontSize = 20.sp)
            )
            Spacer(modifier = GlanceModifier.width(16.dp))
            Text(
                text = "▶",
                modifier = GlanceModifier
                    .padding(8.dp)
                    .clickable(actionSendBroadcast(Intent(ACTION_PLAY_PAUSE).setPackage("com.app.musicplayer"))),
                style = TextStyle(fontSize = 24.sp)
            )
            Spacer(modifier = GlanceModifier.width(16.dp))
            Text(
                text = "⏭",
                modifier = GlanceModifier
                    .padding(8.dp)
                    .clickable(actionSendBroadcast(Intent(ACTION_NEXT).setPackage("com.app.musicplayer"))),
                style = TextStyle(fontSize = 20.sp)
            )
        }
    }
}

@Composable
private fun CompactWidgetContent() {
    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(8.dp)
            .clickable(actionStartActivity<MainActivity>()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = GlanceModifier.defaultWeight()) {
            Text(
                text = "Music Player",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 13.sp),
                maxLines = 1
            )
        }

        Text(
            text = "⏮",
            modifier = GlanceModifier
                .padding(6.dp)
                .clickable(actionSendBroadcast(Intent(ACTION_PREVIOUS).setPackage("com.app.musicplayer"))),
            style = TextStyle(fontSize = 18.sp)
        )
        Text(
            text = "▶",
            modifier = GlanceModifier
                .padding(6.dp)
                .clickable(actionSendBroadcast(Intent(ACTION_PLAY_PAUSE).setPackage("com.app.musicplayer"))),
            style = TextStyle(fontSize = 20.sp)
        )
        Text(
            text = "⏭",
            modifier = GlanceModifier
                .padding(6.dp)
                .clickable(actionSendBroadcast(Intent(ACTION_NEXT).setPackage("com.app.musicplayer"))),
            style = TextStyle(fontSize = 18.sp)
        )
    }
}

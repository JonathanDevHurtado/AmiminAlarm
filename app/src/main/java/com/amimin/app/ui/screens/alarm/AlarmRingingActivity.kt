package com.amimin.app.ui.screens.alarm

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.activity.compose.setContent
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Snooze
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.amimin.app.R
import com.amimin.app.data.database.AlarmService
import com.amimin.app.data.database.AlarmReceiver
import com.amimin.app.data.repository.SettingsRepository
import com.amimin.app.ui.animations.SakuraPetalSystem
import com.amimin.app.ui.animations.VideoLiveWallpaper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private fun snoozeAlarm(
    context: Context,
    alarmId: Long,
    minutes: Int,
    label: String,
    ringtone: String?,
    vibrate: Boolean,
    volume: Int,
    language: String
) {
    val intent = Intent(context, AlarmReceiver::class.java).apply {
        putExtra("alarm_id", alarmId)
        putExtra("alarm_label", label)
        putExtra("alarm_ringtone", ringtone)
        putExtra("alarm_vibrate", vibrate)
        putExtra("alarm_volume", volume)
        putExtra("language", language)
        putExtra("snooze", true)
    }
    val pendingIntent = android.app.PendingIntent.getBroadcast(
        context,
        alarmId.toInt() + 900,
        intent,
        android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
    )
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
    val triggerTime = System.currentTimeMillis() + minutes * 60_000L
    try {
        alarmManager.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    } catch (e: SecurityException) {
        alarmManager.set(android.app.AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
    }
}

@AndroidEntryPoint
class AlarmRingingActivity : ComponentActivity() {

    @Inject
    lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val keyguard = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keyguard?.requestDismissKeyguard(this, null)
        }

        val alarmId = intent.getLongExtra("alarm_id", 0L)
        val label = intent.getStringExtra("alarm_label") ?: getString(R.string.app_name)
        val ringtone = intent.getStringExtra("alarm_ringtone")
        val vibrate = intent.getBooleanExtra("alarm_vibrate", true)
        val volume = intent.getIntExtra("alarm_volume", 80)
        val language = intent.getStringExtra("language") ?: "en"

        lifecycleScope.launch {
            val backgroundType = settingsRepository.alarmBackgroundType.first()
            val backgroundUri = settingsRepository.alarmBackgroundUri.first()
            if (isFinishing || isDestroyed) return@launch

            setContent {
                AlarmRingingScreen(
                    label = label,
                    backgroundType = backgroundType,
                    backgroundUri = backgroundUri,
                    onDismiss = {
                        AlarmService.stop(this@AlarmRingingActivity)
                        finish()
                    },
                    onSnooze = {
                        snoozeAlarm(
                            this@AlarmRingingActivity,
                            alarmId,
                            5,
                            label,
                            ringtone,
                            vibrate,
                            volume,
                            language
                        )
                        AlarmService.stop(this@AlarmRingingActivity)
                        finish()
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

@Composable
fun AlarmRingingScreen(
    label: String,
    backgroundType: String = "default",
    backgroundUri: String = "",
    onDismiss: () -> Unit,
    onSnooze: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "pulseScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1A0A2E), Color(0xFF3D1A6E), Color(0xFF1A0A2E))
                )
            )
    ) {
        when {
            backgroundType == "video" && backgroundUri.isNotBlank() -> {
                VideoLiveWallpaper(
                    uriString = backgroundUri,
                    muted = true,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                )
            }
            backgroundType == "image" && backgroundUri.isNotBlank() -> {
                AsyncImage(
                    model = backgroundUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.45f))
                )
            }
            else -> {
                SakuraPetalSystem(modifier = Modifier.fillMaxSize())
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(pulse)
                    .clip(RoundedCornerShape(34.dp))
                    .background(Color.White.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.amimin_logo),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier.size(116.dp).clip(RoundedCornerShape(28.dp))
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(64.dp))

            Button(
                onClick = onSnooze,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.18f))
            ) {
                Icon(Icons.Default.Snooze, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
                Text(stringResource(R.string.snooze_minutes, 5), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D6D))
            ) {
                Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(12.dp))
                Text(stringResource(R.string.dismiss), color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

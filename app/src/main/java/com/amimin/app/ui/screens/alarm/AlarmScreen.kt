package com.amimin.app.ui.screens.alarm

import android.media.Ringtone
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.amimin.app.R
import com.amimin.app.data.model.Alarm
import com.amimin.app.ui.animations.*
import com.amimin.app.ui.components.AlarmSounds
import com.amimin.app.ui.components.StickerPicker
import com.amimin.app.ui.components.StickerPreview
import com.amimin.app.ui.components.vibrateShort
import com.amimin.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmScreen(
    viewModel: AlarmViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val alarms by viewModel.alarms.collectAsStateWithLifecycle()
    val showTimePicker by viewModel.showTimePicker.collectAsStateWithLifecycle()
    val use24HourFormat by viewModel.use24HourFormat.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var selectedHour by remember { mutableIntStateOf(8) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var alarmLabel by remember { mutableStateOf("") }
    var selectedDays by remember { mutableStateOf(setOf<Int>()) }
    var selectedSticker by remember { mutableStateOf<String?>(null) }
    var selectedPhotoUri by remember { mutableStateOf<String?>(null) }
    var selectedSoundUri by remember { mutableStateOf<String?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) { }
            selectedPhotoUri = it.toString()
        }
    }

    val audioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                // Some providers don't support persistable permissions
            }
            selectedSoundUri = it.toString()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.anime_alarm_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    vibrateShort(context)
                    viewModel.showTimePicker()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.shadow(8.dp, shape = CircleShape)
            ) {
                Icon(Icons.Default.Add, stringResource(R.string.add_alarm), tint = Color.White)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            if (alarms.isEmpty()) {
                EmptyAlarmContent()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
                ) {
                    items(alarms, key = { it.id }) { alarm ->
                        AlarmCard(
                            alarm = alarm,
                            use24HourFormat = use24HourFormat,
                            onToggle = {
                                vibrateShort(context)
                                viewModel.toggleAlarm(alarm)
                            },
                            onDelete = { viewModel.deleteAlarm(alarm) }
                        )
                    }
                }
            }

            if (showTimePicker) {
                AnimeAlarmDialog(
                    hour = selectedHour,
                    minute = selectedMinute,
                    label = alarmLabel,
                    selectedDays = selectedDays,
                    selectedSticker = selectedSticker,
                    selectedPhotoUri = selectedPhotoUri,
                    selectedSoundUri = selectedSoundUri,
                    use24HourFormat = use24HourFormat,
                    onHourChange = { selectedHour = it },
                    onMinuteChange = { selectedMinute = it },
                    onLabelChange = { alarmLabel = it },
                    onDayToggle = { day ->
                        selectedDays = if (day in selectedDays) selectedDays - day else selectedDays + day
                    },
                    onStickerChange = { selectedSticker = it },
                    onPickPhoto = { photoPicker.launch(arrayOf("image/*")) },
                    onClearPhoto = { selectedPhotoUri = null },
                    onSoundChange = { selectedSoundUri = it },
                    onPickCustomSound = { audioPicker.launch(arrayOf("audio/*")) },
                    onConfirm = {
                        viewModel.addAlarm(
                            selectedHour, selectedMinute, alarmLabel, selectedDays.toList(),
                            selectedSticker, selectedPhotoUri, selectedSoundUri
                        )
                        viewModel.hideTimePicker()
                        alarmLabel = ""
                        selectedDays = emptySet()
                        selectedSticker = null
                        selectedPhotoUri = null
                        selectedSoundUri = null
                    },
                    onDismiss = {
                        viewModel.hideTimePicker()
                        alarmLabel = ""
                        selectedDays = emptySet()
                        selectedSticker = null
                        selectedPhotoUri = null
                        selectedSoundUri = null
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyAlarmContent() {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        SakuraPetalSystem(modifier = Modifier.fillMaxWidth().height(200.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.no_alarms),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.no_alarms_hint),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AlarmCard(
    alarm: Alarm,
    use24HourFormat: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().animeCardAnimation(isVisible = true),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (alarm.isEnabled)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (alarm.isEnabled) 4.dp else 0.dp
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!alarm.photoUri.isNullOrBlank()) {
                AsyncImage(
                    model = alarm.photoUri,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp).clip(RoundedCornerShape(14.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = formatAlarmTime(alarm.hour, alarm.minute, use24HourFormat),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (alarm.isEnabled)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!alarm.sticker.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        StickerPreview(sticker = alarm.sticker)
                    }
                }
                Text(
                    text = alarm.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (alarm.repeatDays.isNotEmpty()) {
                    Text(
                        text = formatRepeatDays(alarm.repeatDays),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                }
            }

            Switch(
                checked = alarm.isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeAlarmDialog(
    hour: Int,
    minute: Int,
    label: String,
    selectedDays: Set<Int>,
    selectedSticker: String?,
    selectedPhotoUri: String?,
    selectedSoundUri: String?,
    use24HourFormat: Boolean,
    onHourChange: (Int) -> Unit,
    onMinuteChange: (Int) -> Unit,
    onLabelChange: (String) -> Unit,
    onDayToggle: (Int) -> Unit,
    onStickerChange: (String?) -> Unit,
    onPickPhoto: () -> Unit,
    onClearPhoto: () -> Unit,
    onSoundChange: (String?) -> Unit,
    onPickCustomSound: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var editingLabel by remember { mutableStateOf(label) }
    var expandedSection by remember { mutableIntStateOf(0) }
    var previewRingtone by remember { mutableStateOf<Ringtone?>(null) }
    val soundOptions = remember { AlarmSounds.getSystemSounds(context) }
    val timeState = rememberTimePickerState(
        initialHour = hour,
        initialMinute = minute,
        is24Hour = use24HourFormat
    )

    LaunchedEffect(timeState.hour, timeState.minute) {
        onHourChange(timeState.hour)
        onMinuteChange(timeState.minute)
    }

    DisposableEffect(Unit) {
        onDispose { AlarmSounds.stopPreview(previewRingtone) }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.94f).padding(12.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 8.dp, top = 12.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.set_anime_alarm),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(R.string.cancel))
                    }
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TimeInput(
                        state = timeState,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editingLabel,
                        onValueChange = {
                            editingLabel = it
                            onLabelChange(it)
                        },
                        label = { Text(stringResource(R.string.alarm_label)) },
                        placeholder = { Text(stringResource(R.string.alarm_label_hint)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Outlined.Label, null) }
                    )

                    Text(
                        text = stringResource(R.string.repeat_on),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val days = listOf("M", "T", "W", "T", "F", "S", "S")
                        days.forEachIndexed { index, day ->
                            val dayNum = index + 1
                            val isSelected = dayNum in selectedDays
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    vibrateShort(context)
                                    onDayToggle(dayNum)
                                },
                                label = { Text(day, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    ExpandableSection(
                        title = stringResource(R.string.sticker),
                        subtitle = selectedSticker ?: stringResource(R.string.no_sticker),
                        icon = Icons.Outlined.EmojiEmotions,
                        expanded = expandedSection == 1,
                        onToggle = { expandedSection = if (expandedSection == 1) 0 else 1 }
                    ) {
                        StickerPicker(
                            selectedSticker = selectedSticker,
                            onStickerSelected = { onStickerChange(it) }
                        )
                    }

                    ExpandableSection(
                        title = stringResource(R.string.alarm_sound),
                        subtitle = selectedSoundUri?.let { uri ->
                            soundOptions.firstOrNull { it.uri == uri }?.title
                        } ?: stringResource(R.string.sound_default),
                        icon = Icons.Outlined.MusicNote,
                        expanded = expandedSection == 2,
                        onToggle = { expandedSection = if (expandedSection == 2) 0 else 2 }
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = onPickCustomSound,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Outlined.LibraryMusic, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.pick_custom_sound))
                            }
                            SoundRow(
                                title = stringResource(R.string.sound_default),
                                selected = selectedSoundUri.isNullOrBlank(),
                                onSelect = { onSoundChange(null) },
                                onPreview = {
                                    AlarmSounds.stopPreview(previewRingtone)
                                    previewRingtone = AlarmSounds.playPreview(context, "")
                                }
                            )
                            soundOptions.take(20).forEach { option ->
                                SoundRow(
                                    title = option.title,
                                    selected = selectedSoundUri == option.uri,
                                    onSelect = { onSoundChange(option.uri) },
                                    onPreview = {
                                        AlarmSounds.stopPreview(previewRingtone)
                                        previewRingtone = AlarmSounds.playPreview(context, option.uri)
                                    }
                                )
                            }
                        }
                    }

                    ExpandableSection(
                        title = stringResource(R.string.photo),
                        subtitle = if (selectedPhotoUri.isNullOrBlank()) stringResource(R.string.no_sticker) else stringResource(R.string.photo),
                        icon = Icons.Outlined.Image,
                        expanded = expandedSection == 3,
                        onToggle = { expandedSection = if (expandedSection == 3) 0 else 3 }
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (!selectedPhotoUri.isNullOrBlank()) {
                                AsyncImage(
                                    model = selectedPhotoUri,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxWidth().height(160.dp).clip(RoundedCornerShape(14.dp)),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = onPickPhoto,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Outlined.AddPhotoAlternate, null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(stringResource(R.string.add_photo))
                                }
                                if (!selectedPhotoUri.isNullOrBlank()) {
                                    OutlinedButton(
                                        onClick = onClearPhoto,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    Button(
                        onClick = {
                            AlarmSounds.stopPreview(previewRingtone)
                            onConfirm()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Alarm, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.set_alarm))
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableSection(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onToggle() }.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            AnimatedVisibility(visible = expanded) {
                Box(modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp)) {
                    content()
                }
            }
        }
    }
}

@Composable
fun SoundRow(title: String, selected: Boolean, onSelect: () -> Unit, onPreview: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onSelect() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onSelect)
        Text(title, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        IconButton(onClick = onPreview) {
            Icon(Icons.Outlined.PlayArrow, contentDescription = "Preview", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

fun formatRepeatDays(days: List<Int>): String {
    val dayNames = mapOf(
        1 to "Mon", 2 to "Tue", 3 to "Wed", 4 to "Thu",
        5 to "Fri", 6 to "Sat", 7 to "Sun"
    )
    return days.mapNotNull { dayNames[it] }.joinToString(", ")
}

fun formatAlarmTime(hour: Int, minute: Int, use24HourFormat: Boolean): String {
    return if (use24HourFormat) {
        String.format("%02d:%02d", hour, minute)
    } else {
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        val suffix = if (hour < 12) "AM" else "PM"
        String.format("%d:%02d %s", displayHour, minute, suffix)
    }
}

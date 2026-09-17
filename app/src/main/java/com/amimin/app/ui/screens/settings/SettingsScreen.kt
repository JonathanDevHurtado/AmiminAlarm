package com.amimin.app.ui.screens.settings

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.amimin.app.R
import com.amimin.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val themeName by viewModel.themeName.collectAsStateWithLifecycle()
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
    val animeStyle by viewModel.animeStyle.collectAsStateWithLifecycle()
    val particlesEnabled by viewModel.particlesEnabled.collectAsStateWithLifecycle()
    val animationsEnabled by viewModel.animationsEnabled.collectAsStateWithLifecycle()
    val showAnimeStickers by viewModel.showAnimeStickers.collectAsStateWithLifecycle()
    val showThemeDialog by viewModel.showThemeDialog.collectAsStateWithLifecycle()
    val showColorPickerDialog by viewModel.showColorPickerDialog.collectAsStateWithLifecycle()
    val language by viewModel.language.collectAsStateWithLifecycle()
    val wallpaperHasImage by viewModel.wallpaperHasImage.collectAsStateWithLifecycle()
    val useWallpaperColors by viewModel.useWallpaperColors.collectAsStateWithLifecycle()
    val colorSource by viewModel.colorSource.collectAsStateWithLifecycle()
    val liveWallpaperEnabled by viewModel.liveWallpaperEnabled.collectAsStateWithLifecycle()
    val liveWallpaperStyle by viewModel.liveWallpaperStyle.collectAsStateWithLifecycle()
    val wallpaperBitmap by viewModel.wallpaperBitmap.collectAsStateWithLifecycle()
    val customPrimaryColor by viewModel.customPrimaryColor.collectAsStateWithLifecycle()
    val customSecondaryColor by viewModel.customSecondaryColor.collectAsStateWithLifecycle()
    val wallpaperPrimaryColor by viewModel.wallpaperPrimaryColor.collectAsStateWithLifecycle()
    val wallpaperSecondaryColor by viewModel.wallpaperSecondaryColor.collectAsStateWithLifecycle()
    val username by viewModel.username.collectAsStateWithLifecycle()
    val use24HourFormat by viewModel.use24HourFormat.collectAsStateWithLifecycle()
    val liveWallpaperVideoUri by viewModel.liveWallpaperVideoUri.collectAsStateWithLifecycle()
    val liveWallpaperMuted by viewModel.liveWallpaperMuted.collectAsStateWithLifecycle()
    val reminder1h by viewModel.reminder1h.collectAsStateWithLifecycle()
    val reminder30m by viewModel.reminder30m.collectAsStateWithLifecycle()
    val reminder10m by viewModel.reminder10m.collectAsStateWithLifecycle()
    val eventNotifications by viewModel.eventNotifications.collectAsStateWithLifecycle()
    val alarmBackgroundUri by viewModel.alarmBackgroundUri.collectAsStateWithLifecycle()
    val alarmBackgroundType by viewModel.alarmBackgroundType.collectAsStateWithLifecycle()
    val backupMessage by viewModel.backupMessage.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    val createBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let { viewModel.backupToUri(it) }
    }

    val restoreBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.restoreFromUri(it) }
    }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            takePersistablePermission(context, it)
            viewModel.setLiveWallpaperVideo(it)
        }
    }

    val alarmBgImagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            takePersistablePermission(context, it)
            viewModel.setAlarmBackground("image", it.toString())
        }
    }

    val alarmBgVideoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            takePersistablePermission(context, it)
            viewModel.setAlarmBackground("video", it.toString())
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let { viewModel.setWallpaperFromUri(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.settings), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            Surface(tonalElevation = 3.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(stringResource(R.string.cancel))
                    }
                    Button(
                        onClick = onNavigateBack,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.save))
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // ===== Profile =====
            item { SettingsSectionHeader(stringResource(R.string.settings_profile), stringResource(R.string.help_profile)) }
            item {
                UsernameSection(
                    username = username,
                    onUsernameChange = { viewModel.setUsername(it) }
                )
            }

            // ===== Appearance =====
            item { SettingsSectionHeader(stringResource(R.string.settings_appearance), stringResource(R.string.help_appearance)) }
            item {
                ThemeSelector(
                    currentTheme = themeName,
                    enabled = colorSource == "theme",
                    onShowDialog = { viewModel.showThemeDialog() }
                )
            }
            item {
                SettingsSwitchItem(
                    icon = Icons.Outlined.DarkMode,
                    title = stringResource(R.string.dark_mode),
                    description = stringResource(R.string.dark_mode_desc),
                    checked = isDarkMode,
                    onCheckedChange = { viewModel.setDarkMode(it) }
                )
            }

            // ===== Color Mode =====
            item { SettingsSectionHeader(stringResource(R.string.color_mode), stringResource(R.string.help_color_mode)) }
            item {
                ColorModeSection(
                    colorSource = colorSource,
                    hasWallpaper = wallpaperHasImage,
                    onSelectTheme = { viewModel.setTheme(themeName) },
                    onSelectCustom = { viewModel.selectCustomColors(); viewModel.showColorPickerDialog() },
                    onSelectWallpaper = { viewModel.setUseWallpaperColors(true) },
                    customPrimaryColor = customPrimaryColor,
                    customSecondaryColor = customSecondaryColor,
                    wallpaperPrimaryColor = wallpaperPrimaryColor,
                    wallpaperSecondaryColor = wallpaperSecondaryColor
                )
            }

            // ===== Wallpaper =====
            item { SettingsSectionHeader(stringResource(R.string.settings_wallpaper), stringResource(R.string.help_wallpaper)) }
            item {
                WallpaperSection(
                    wallpaperBitmap = wallpaperBitmap,
                    hasWallpaper = wallpaperHasImage,
                    onPickImage = { imagePickerLauncher.launch(arrayOf("image/*")) },
                    onClearWallpaper = { viewModel.clearWallpaper() }
                )
            }

            // ===== Live Wallpaper =====
            item { SettingsSectionHeader(stringResource(R.string.live_wallpaper), stringResource(R.string.help_live_wallpaper)) }
            item {
                LiveWallpaperSection(
                    enabled = liveWallpaperEnabled,
                    style = liveWallpaperStyle,
                    muted = liveWallpaperMuted,
                    hasVideo = liveWallpaperVideoUri.isNotBlank(),
                    onToggle = { viewModel.setLiveWallpaperEnabled(it) },
                    onSelectStyle = { style ->
                        if (style == "video") videoPickerLauncher.launch(arrayOf("video/*"))
                        else viewModel.setLiveWallpaperStyle(style)
                    },
                    onMutedChange = { viewModel.setLiveWallpaperMuted(it) }
                )
            }

            // ===== Anime Style =====
            item { SettingsSectionHeader(stringResource(R.string.settings_anime_style), stringResource(R.string.help_anime_style)) }
            item {
                AnimeStyleSelector(currentStyle = animeStyle, onSelectStyle = { viewModel.setAnimeStyle(it) })
            }

            // ===== Animations =====
            item { SettingsSectionHeader(stringResource(R.string.settings_animations), stringResource(R.string.help_animations)) }
            item {
                SettingsSwitchItem(
                    icon = Icons.Outlined.AutoAwesome,
                    title = stringResource(R.string.particle_effects),
                    description = stringResource(R.string.particle_effects_desc),
                    checked = particlesEnabled,
                    onCheckedChange = { viewModel.setParticlesEnabled(it) }
                )
            }
            item {
                SettingsSwitchItem(
                    icon = Icons.Outlined.Animation,
                    title = stringResource(R.string.animations),
                    description = stringResource(R.string.animations_desc),
                    checked = animationsEnabled,
                    onCheckedChange = { viewModel.setAnimationsEnabled(it) }
                )
            }
            item {
                SettingsSwitchItem(
                    icon = Icons.Outlined.Star,
                    title = stringResource(R.string.anime_stickers),
                    description = stringResource(R.string.anime_stickers_desc),
                    checked = showAnimeStickers,
                    onCheckedChange = { viewModel.setShowAnimeStickers(it) }
                )
            }

            // ===== Notifications / Reminders =====
            item { SettingsSectionHeader(stringResource(R.string.settings_reminders), stringResource(R.string.help_reminders)) }
            item {
                SettingsSwitchItem(
                    icon = Icons.Outlined.NotificationsActive,
                    title = stringResource(R.string.reminder_1h),
                    description = stringResource(R.string.reminder_notify_desc),
                    checked = reminder1h,
                    onCheckedChange = { viewModel.setReminder1h(it) }
                )
            }
            item {
                SettingsSwitchItem(
                    icon = Icons.Outlined.NotificationsActive,
                    title = stringResource(R.string.reminder_30m),
                    description = stringResource(R.string.reminder_notify_desc),
                    checked = reminder30m,
                    onCheckedChange = { viewModel.setReminder30m(it) }
                )
            }
            item {
                SettingsSwitchItem(
                    icon = Icons.Outlined.NotificationsActive,
                    title = stringResource(R.string.reminder_10m),
                    description = stringResource(R.string.reminder_notify_desc),
                    checked = reminder10m,
                    onCheckedChange = { viewModel.setReminder10m(it) }
                )
            }
            item {
                SettingsSwitchItem(
                    icon = Icons.Outlined.EventAvailable,
                    title = stringResource(R.string.event_notifications),
                    description = stringResource(R.string.event_notifications_desc),
                    checked = eventNotifications,
                    onCheckedChange = { viewModel.setEventNotifications(it) }
                )
            }

            // ===== Alarm screen background =====
            item { SettingsSectionHeader(stringResource(R.string.alarm_background), stringResource(R.string.help_alarm_background)) }
            item {
                AlarmBackgroundSection(
                    type = alarmBackgroundType,
                    hasCustom = alarmBackgroundUri.isNotBlank(),
                    onSelectDefault = { viewModel.setAlarmBackground("default", "") },
                    onPickImage = { alarmBgImagePicker.launch(arrayOf("image/*")) },
                    onPickVideo = { alarmBgVideoPicker.launch(arrayOf("video/*")) }
                )
            }

            // ===== General =====
            item { SettingsSectionHeader(stringResource(R.string.settings_general), stringResource(R.string.help_general)) }
            item {
                SettingsSwitchItem(
                    icon = Icons.Outlined.Schedule,
                    title = stringResource(R.string.time_format_24h),
                    description = stringResource(R.string.time_format_24h_desc),
                    checked = use24HourFormat,
                    onCheckedChange = { viewModel.setUse24HourFormat(it) }
                )
            }
            item {
                LanguageSelector(
                    currentLanguage = language,
                    onSelectLanguage = { viewModel.setLanguage(it) },
                    getDisplayName = { viewModel.getLanguageDisplayName(it) }
                )
            }

            // Backup Section
            item { SettingsSectionHeader(stringResource(R.string.backup_restore), stringResource(R.string.help_backup)) }
            item {
                BackupSection(
                    message = backupMessage,
                    onLocalBackup = { createBackupLauncher.launch(viewModel.suggestedBackupName()) },
                    onCloudBackup = {
                        viewModel.shareBackupToCloud { intent -> context.startActivity(intent) }
                    },
                    onRestore = {
                        restoreBackupLauncher.launch(arrayOf("application/json", "application/octet-stream", "*/*"))
                    },
                    onDismissMessage = { viewModel.clearBackupMessage() }
                )
            }

            item { AboutSection() }
        }
    }

    if (showThemeDialog) {
        ThemeSelectionDialog(
            themes = viewModel.getAvailableThemes(),
            currentTheme = themeName,
            onSelect = { viewModel.setTheme(it); viewModel.hideThemeDialog() },
            onDismiss = { viewModel.hideThemeDialog() }
        )
    }

    if (showColorPickerDialog) {
        CustomColorPickerDialog(
            primaryColor = customPrimaryColor,
            secondaryColor = customSecondaryColor,
            onPrimaryChange = { viewModel.setCustomPrimaryColor(it) },
            onSecondaryChange = { viewModel.setCustomSecondaryColor(it) },
            onDismiss = { viewModel.hideColorPickerDialog() }
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String, helpText: String? = null) {
    var showHelp by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        if (helpText != null) {
            IconButton(
                onClick = { showHelp = true },
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    Icons.Outlined.HelpOutline,
                    contentDescription = stringResource(R.string.help),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    if (showHelp && helpText != null) {
        AlertDialog(
            onDismissRequest = { showHelp = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp),
            icon = { Icon(Icons.Outlined.HelpOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
            title = { Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
            text = { Text(helpText, style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = { showHelp = false }) {
                    Text(stringResource(R.string.done))
                }
            }
        )
    }
}

@Composable
fun UsernameSection(username: String, onUsernameChange: (String) -> Unit) {
    var text by remember(username) { mutableStateOf(username) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.username), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    Text(stringResource(R.string.username_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                    onUsernameChange(it)
                },
                singleLine = true,
                placeholder = { Text(stringResource(R.string.username_hint)) },
                leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun SettingsSwitchItem(icon: ImageVector, title: String, description: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
fun WallpaperSection(wallpaperBitmap: android.graphics.Bitmap?, hasWallpaper: Boolean, onPickImage: () -> Unit, onClearWallpaper: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Image, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.set_wallpaper), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    Text(stringResource(R.string.wallpaper_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (wallpaperBitmap != null) {
                Box(modifier = Modifier.fillMaxWidth().height(180.dp).clip(RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                    androidx.compose.foundation.Image(bitmap = wallpaperBitmap.asImageBitmap(), contentDescription = "Wallpaper", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    Box(modifier = Modifier.align(Alignment.BottomStart).padding(8.dp).background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                        Text(stringResource(R.string.wallpaper_active), color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onPickImage, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                    Icon(Icons.Outlined.Photo, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.pick_image))
                }
                if (hasWallpaper) {
                    OutlinedButton(onClick = onClearWallpaper, shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ColorModeSection(
    colorSource: String,
    hasWallpaper: Boolean,
    onSelectTheme: () -> Unit,
    onSelectCustom: () -> Unit,
    onSelectWallpaper: () -> Unit,
    customPrimaryColor: Int,
    customSecondaryColor: Int,
    wallpaperPrimaryColor: Int,
    wallpaperSecondaryColor: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            ColorSourceRow(
                icon = Icons.Outlined.Palette,
                title = stringResource(R.string.color_theme),
                description = stringResource(R.string.color_source_theme_desc),
                selected = colorSource == "theme",
                colors = listOf(customPrimaryColor, customSecondaryColor),
                enabled = true,
                onClick = onSelectTheme
            )
            ColorSourceRow(
                icon = Icons.Outlined.ColorLens,
                title = stringResource(R.string.custom_colors),
                description = stringResource(R.string.customize_colors_manually),
                selected = colorSource == "custom",
                colors = listOf(customPrimaryColor, customSecondaryColor),
                enabled = true,
                onClick = onSelectCustom
            )
            ColorSourceRow(
                icon = Icons.Outlined.Wallpaper,
                title = stringResource(R.string.apply_colors),
                description = if (hasWallpaper) stringResource(R.string.use_wallpaper_colors) else stringResource(R.string.wallpaper_none),
                selected = colorSource == "wallpaper",
                colors = listOf(wallpaperPrimaryColor, wallpaperSecondaryColor),
                enabled = hasWallpaper,
                onClick = onSelectWallpaper
            )
        }
    }
}

@Composable
fun ColorSourceRow(
    icon: ImageVector,
    title: String,
    description: String,
    selected: Boolean,
    colors: List<Int>,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 10.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = { if (enabled) onClick() }, enabled = enabled)
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            icon,
            contentDescription = null,
            tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (enabled) 1f else 0.5f)
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            colors.forEach { color ->
                Box(modifier = Modifier.size(20.dp).clip(CircleShape).background(Color(color)))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LiveWallpaperSection(
    enabled: Boolean,
    style: String,
    muted: Boolean,
    hasVideo: Boolean,
    onToggle: (Boolean) -> Unit,
    onSelectStyle: (String) -> Unit,
    onMutedChange: (Boolean) -> Unit
) {
    val styles = listOf(
        "gradient" to stringResource(R.string.live_gradient),
        "sakura" to stringResource(R.string.live_sakura),
        "stars" to stringResource(R.string.live_stars),
        "aurora" to stringResource(R.string.live_aurora),
        "video" to stringResource(R.string.live_wallpaper_video)
    )
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Movie, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.live_wallpaper), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    Text(stringResource(R.string.live_wallpaper_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = enabled, onCheckedChange = onToggle)
            }

            if (enabled) {
                Divider(modifier = Modifier.padding(vertical = 4.dp))
                Text(stringResource(R.string.live_wallpaper_style), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                androidx.compose.foundation.layout.FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    styles.forEach { (styleId, name) ->
                        FilterChip(
                            selected = style == styleId,
                            onClick = { onSelectStyle(styleId) },
                            label = { Text(name) },
                            leadingIcon = if (styleId == "video") {
                                { Icon(Icons.Outlined.VideoLibrary, null, modifier = Modifier.size(16.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                if (style == "video") {
                    Text(
                        text = if (hasVideo) stringResource(R.string.live_wallpaper_video_desc)
                        else stringResource(R.string.pick_video),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (hasVideo) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error
                    )
                    OutlinedButton(
                        onClick = { onSelectStyle("video") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.VideoLibrary, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (hasVideo) stringResource(R.string.pick_video) else stringResource(R.string.pick_video))
                    }
                }

                Divider(modifier = Modifier.padding(vertical = 4.dp))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.VolumeUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.live_wallpaper_sound), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                        Text(stringResource(R.string.live_wallpaper_sound_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(checked = !muted, onCheckedChange = { onMutedChange(!it) })
                }
            }
        }
    }
}

@Composable
fun ThemeSelector(currentTheme: String, enabled: Boolean, onShowDialog: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(enabled = enabled) { onShowDialog() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = if (enabled) 0.3f else 0.15f)
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.Palette,
                contentDescription = null,
                tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.color_theme),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                )
                Text(
                    if (enabled) currentTheme.replaceFirstChar { it.uppercase() } else stringResource(R.string.locked_by_color_mode),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (enabled) 1f else 0.4f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeStyleSelector(currentStyle: String, onSelectStyle: (String) -> Unit) {
    val styles = listOf("cute" to "Kawaii", "elegant" to "Elegant", "cyberpunk" to "Cyberpunk", "minimal" to "Minimal", "retro" to "Retro")
    LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(styles.size) { index ->
            val (style, name) = styles[index]
            val isSelected = style == currentStyle
            FilterChip(
                selected = isSelected,
                onClick = { onSelectStyle(style) },
                label = { Text(name, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MaterialTheme.colorScheme.primary, selectedLabelColor = Color.White)
            )
        }
    }
}

@Composable
fun LanguageSelector(currentLanguage: String, onSelectLanguage: (String) -> Unit, getDisplayName: (String) -> String) {
    val languages = listOf("en", "es", "ja", "ko", "zh", "ru", "fr", "de")
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(stringResource(R.string.language), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(12.dp))
            languages.forEach { code ->
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { onSelectLanguage(code) }.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(selected = code == currentLanguage, onClick = { onSelectLanguage(code) })
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(getDisplayName(code))
                }
            }
        }
    }
}

@Composable
fun BackupSection(
    message: String?,
    onLocalBackup: () -> Unit,
    onCloudBackup: () -> Unit,
    onRestore: () -> Unit,
    onDismissMessage: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.CloudSync, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.backup_restore), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    Text(stringResource(R.string.backup_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Button(
                onClick = onLocalBackup,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Outlined.SaveAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.backup_local))
            }

            OutlinedButton(
                onClick = onCloudBackup,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Outlined.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.backup_google))
            }

            OutlinedButton(
                onClick = onRestore,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Outlined.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.restore_backup))
            }

            if (message != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(message, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                        IconButton(onClick = onDismissMessage) {
                            Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmBackgroundSection(
    type: String,
    hasCustom: Boolean,
    onSelectDefault: () -> Unit,
    onPickImage: () -> Unit,
    onPickVideo: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Wallpaper, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.alarm_background), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    Text(stringResource(R.string.alarm_background_desc), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = type == "default",
                    onClick = onSelectDefault,
                    label = { Text(stringResource(R.string.alarm_bg_default)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = type == "image",
                    onClick = onPickImage,
                    label = { Text(stringResource(R.string.alarm_bg_image)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = type == "video",
                    onClick = onPickVideo,
                    label = { Text(stringResource(R.string.alarm_bg_video)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }

            if (type != "default" && !hasCustom) {
                Text(
                    stringResource(R.string.add_photo),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun AboutSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("AmiminAlarm", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(stringResource(R.string.version), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun ThemeSelectionDialog(themes: List<Pair<String, String>>, currentTheme: String, onSelect: (String) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(stringResource(R.string.select_theme), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        },
        text = {
            Column {
                themes.forEach { (themeId, themeName) ->
                    val isSelected = themeId == currentTheme
                    val themeColors = when (themeId) {
                        "sakura" -> AnimeThemePalettes.Sakura
                        "midnight" -> AnimeThemePalettes.Midnight
                        "cherry" -> AnimeThemePalettes.Cherry
                        "ocean" -> AnimeThemePalettes.Ocean
                        "neon" -> AnimeThemePalettes.Neon
                        "pastel" -> AnimeThemePalettes.Pastel
                        else -> AnimeThemePalettes.Sakura
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { onSelect(themeId) }.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(Brush.linearGradient(listOf(themeColors.primary, themeColors.secondary))))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(themeName, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        if (isSelected) Icon(Icons.Default.Check, "Selected", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.done)) } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomColorPickerDialog(
    primaryColor: Int,
    secondaryColor: Int,
    onPrimaryChange: (Int) -> Unit,
    onSecondaryChange: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val presetColors = listOf(
        0xFFFFB7C5.toInt(), 0xFFE91E63.toInt(), 0xFF9C27B0.toInt(), 0xFF673AB7.toInt(),
        0xFF2196F3.toInt(), 0xFF00BCD4.toInt(), 0xFF4CAF50.toInt(), 0xFF8BC34A.toInt(),
        0xFFFF9800.toInt(), 0xFFFF5722.toInt(), 0xFFF44336.toInt(), 0xFF795548.toInt(),
        0xFF607D8B.toInt(), 0xFF009688.toInt(), 0xFFCDDC39.toInt(), 0xFFFFEB3B.toInt()
    )

    var selectedTab by remember { mutableIntStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(stringResource(R.string.custom_colors), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Tab selector
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = selectedTab == 0, onClick = { selectedTab = 0 }, label = { Text(stringResource(R.string.primary)) })
                    FilterChip(selected = selectedTab == 1, onClick = { selectedTab = 1 }, label = { Text(stringResource(R.string.secondary)) })
                }

                // Current color preview
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.current) + " ", style = MaterialTheme.typography.bodyMedium)
                    Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color(if (selectedTab == 0) primaryColor else secondaryColor)).border(2.dp, MaterialTheme.colorScheme.outline, CircleShape))
                }

                // Color grid
                Text(stringResource(R.string.presets), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(presetColors.size) { index ->
                        val color = presetColors[index]
                        val isSelected = color == (if (selectedTab == 0) primaryColor else secondaryColor)
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(color))
                                .then(if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.primary, CircleShape) else Modifier)
                                .clickable {
                                    if (selectedTab == 0) onPrimaryChange(color) else onSecondaryChange(color)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) Icon(Icons.Default.Check, "Selected", tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.done)) } }
    )
}

private fun takePersistablePermission(context: android.content.Context, uri: Uri) {
    try {
        context.contentResolver.takePersistableUriPermission(
            uri,
            android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    } catch (e: Exception) {
        // Provider may not offer persistable permissions (e.g. some gallery apps)
    }
}

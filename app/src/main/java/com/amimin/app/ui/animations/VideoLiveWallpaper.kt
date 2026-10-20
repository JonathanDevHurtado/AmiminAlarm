package com.amimin.app.ui.animations

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun VideoLiveWallpaper(
    uriString: String,
    muted: Boolean,
    modifier: Modifier = Modifier,
    fallbackStyle: String = "gradient"
) {
    val context = LocalContext.current
    var failed by remember(uriString) { mutableStateOf(false) }

    val exoPlayer = remember(uriString) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(uriString))
            repeatMode = Player.REPEAT_MODE_ALL
            volume = if (muted) 0f else 1f
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(uriString) {
        val listener = object : Player.Listener {
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                failed = true
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
            exoPlayer.release()
        }
    }

    LaunchedEffect(muted) {
        exoPlayer.volume = if (muted) 0f else 1f
    }

    if (failed) {
        LiveWallpaperBackground(style = fallbackStyle, modifier = modifier)
        return
    }

    AndroidView(
        factory = { ctx ->
            android.view.TextureView(ctx).also { textureView ->
                textureView.surfaceTextureListener = object : android.view.TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(surface: android.graphics.SurfaceTexture, width: Int, height: Int) {
                        exoPlayer.setVideoTextureView(textureView)
                    }
                    override fun onSurfaceTextureSizeChanged(surface: android.graphics.SurfaceTexture, width: Int, height: Int) {}
                    override fun onSurfaceTextureDestroyed(surface: android.graphics.SurfaceTexture): Boolean = true
                    override fun onSurfaceTextureUpdated(surface: android.graphics.SurfaceTexture) {}
                }
            }
        },
        modifier = modifier
    )
}

suspend fun extractColorFromVideo(context: Context, uriString: String): Int = withContext(Dispatchers.IO) {
    val fallback = 0xFF7C4DFF.toInt()
    try {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, Uri.parse(uriString))
        val frame: Bitmap? = try {
            retriever.getFrameAtTime(1_500_000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                ?: retriever.frameAtTime
        } catch (e: Exception) {
            null
        }
        retriever.release()
        if (frame != null) {
            val palette = Palette.from(frame).generate()
            palette.getVibrantColor(
                palette.getLightVibrantColor(
                    palette.getDarkVibrantColor(fallback)
                )
            )
        } else fallback
    } catch (e: Exception) {
        fallback
    }
}

package com.amimin.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.amimin.app.R

object AnimeStickers {
    data class StickerCategory(
        val id: String,
        val labelRes: Int,
        val stickers: List<String>
    )

    val categories = listOf(
        StickerCategory(
            "kawaii", R.string.category_kawaii,
            listOf("🌸", "🌺", "🎀", "💖", "✨", "🌟", "🧸", "🎴", "💮", "🏮", "👘", "🍥")
        ),
        StickerCategory(
            "emotions", R.string.category_emotions,
            listOf("😊", "😍", "🥰", "😴", "😭", "😆", "🤩", "😎", "😳", "🥺", "😇", "🤗")
        ),
        StickerCategory(
            "symbols", R.string.category_symbols,
            listOf("⭐", "💫", "🌙", "☀️", "🍀", "🔥", "❄️", "💧", "⚡", "🎵", "💕", "🌈")
        ),
        StickerCategory(
            "animals", R.string.category_animals,
            listOf("🐱", "🐰", "🦊", "🐼", "🐨", "🐸", "🦄", "🐧", "🐥", "🦋", "🐙", "🐉")
        ),
        StickerCategory(
            "food", R.string.category_food,
            listOf("🍣", "🍜", "🍡", "🍙", "🍰", "🍓", "🍧", "🍩", "🧁", "🍵", "🍱", "🥟")
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StickerPicker(
    selectedSticker: String?,
    onStickerSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableIntStateOf(0) }
    val category = AnimeStickers.categories[selectedCategory]

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(AnimeStickers.categories.size) { index ->
                val cat = AnimeStickers.categories[index]
                FilterChip(
                    selected = selectedCategory == index,
                    onClick = { selectedCategory = index },
                    label = { Text(stringResourceSafe(cat.labelRes)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(category.stickers) { sticker ->
                val isSelected = sticker == selectedSticker
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                        .then(
                            if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp))
                            else Modifier
                        )
                        .clickable { onStickerSelected(if (isSelected) null else sticker) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = sticker, fontSize = 22.sp, textAlign = TextAlign.Center)
                }
            }
        }

        if (selectedSticker != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResourceSafe(R.string.sticker) + ": $selectedSticker",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { onStickerSelected(null) }) {
                    Text(stringResourceSafe(R.string.no_sticker))
                }
            }
        }
    }
}

@Composable
fun StickerPreview(sticker: String?, modifier: Modifier = Modifier) {
    if (sticker.isNullOrBlank()) return
    Box(
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = sticker, fontSize = 20.sp)
    }
}

@Composable
private fun stringResourceSafe(resId: Int): String {
    return androidx.compose.ui.res.stringResource(resId)
}

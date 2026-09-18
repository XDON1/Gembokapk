package com.example.gembok.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gembok.util.DomainExtractor
import kotlin.math.absoluteValue

/**
 * Palette of calm, earthy, premium tone accents for monogram fallback backgrounds.
 */
private val MonogramColors = listOf(
    Color(0xFF5E7A4A), // Muted Olive
    Color(0xFF4A6B82), // Slate Blue
    Color(0xFF825D4A), // Earth Terra
    Color(0xFF6B587B), // Dusty Plum
    Color(0xFF4A7B70), // Pine Teal
    Color(0xFF7A6A4A)  // Dark Ochre
)

/**
 * ServiceLogoAvatar:
 * Uses only local content so the offline vault never sends a saved domain to a
 * remote favicon service.
 */
@Composable
fun ServiceLogoAvatar(
    title: String,
    siteOrApp: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    val initials = remember(title) {
        DomainExtractor.getInitials(title)
    }

    // Stable background tint picked by title hash
    val fallbackColor = remember(title) {
        val hash = title.hashCode().absoluteValue
        MonogramColors[hash % MonogramColors.size]
    }

    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(
                fallbackColor.copy(alpha = 0.18f)
            )
            .border(
                1.dp,
                fallbackColor.copy(alpha = 0.4f),
                shape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (initials.isNotBlank() && initials != "?") {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.38f).sp,
                    letterSpacing = (-0.5).sp
                ),
                color = fallbackColor
            )
        } else {
            val isWebLike = siteOrApp.contains(".") || siteOrApp.startsWith("http")
            Icon(
                imageVector = if (isWebLike) Icons.Default.Language else Icons.Default.Apps,
                contentDescription = "Ikon default",
                tint = fallbackColor,
                modifier = Modifier.size(size * 0.5f)
            )
        }
    }
}

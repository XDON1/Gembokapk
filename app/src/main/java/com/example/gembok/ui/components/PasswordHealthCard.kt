package com.example.gembok.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gembok.data.PasswordHealthStats
import com.example.gembok.ui.theme.HealthFair
import com.example.gembok.ui.theme.HealthOptimal
import com.example.gembok.ui.theme.HealthVulnerable

/**
 * Glassmorphic Vault Health & Security Status Card.
 * Adheres strictly to:
 * - Glass morphism styling only on this primary security summary card (translucent dual brush + thin border).
 * - Real metrics derived solely from actual stored entries (no artificial benchmark claims or fake percentages).
 */
@Composable
fun PasswordHealthCard(
    stats: PasswordHealthStats,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    // Glassmorphism effect tokens
    val glassBg = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0x382F3730), // translucent olive-charcoal tint
                Color(0x1F1E2220)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xD9FFFFFF), // frosted milky warm white
                Color(0x99F0ECE4)
            )
        )
    }

    val glassBorder = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0x528FA87B), // soft luminous olive rim
                Color(0x1A8FA87B)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0x805E7A4A),
                Color(0x2B5E7A4A)
            )
        )
    }

    val scoreColor = when {
        stats.totalCount == 0 -> MaterialTheme.colorScheme.onSurfaceVariant
        (stats.healthScore ?: 0) >= 75 -> HealthOptimal
        (stats.healthScore ?: 0) >= 45 -> HealthFair
        else -> HealthVulnerable
    }

    val scoreLabel = when {
        stats.totalCount == 0 -> "Vault Kosong"
        (stats.healthScore ?: 0) >= 80 -> "Kuat"
        (stats.healthScore ?: 0) >= 50 -> "Cukup"
        else -> "Perlu Perhatian"
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(glassBg)
            .border(1.dp, glassBorder, RoundedCornerShape(20.dp))
            .clickable { expanded = !expanded }
            .padding(18.dp)
            .testTag("health_card")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(scoreColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Status Keamanan",
                            tint = scoreColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Kesehatan Kata Sandi",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = (-0.2).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (stats.totalCount == 0) "Belum ada entri" else "$scoreLabel • ${stats.totalCount} entri tersimpan",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stats.healthScore?.let { "$it%" } ?: "—",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = scoreColor
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Detail",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            if (stats.totalCount > 0) {
                Spacer(modifier = Modifier.height(14.dp))
                LinearProgressIndicator(
                    progress = { (stats.healthScore ?: 0) / 100f },
                    color = scoreColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = "Metrik Berdasarkan Data Nyata",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricBadge(
                            label = "Kuat",
                            count = stats.strongCount,
                            color = HealthOptimal,
                            modifier = Modifier.weight(1f)
                        )
                        MetricBadge(
                            label = "Cukup",
                            count = stats.fairCount,
                            color = HealthFair,
                            modifier = Modifier.weight(1f)
                        )
                        MetricBadge(
                            label = "Lemah",
                            count = stats.weakCount,
                            color = HealthVulnerable,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (stats.reusedCount > 0) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(HealthVulnerable.copy(alpha = 0.1f))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = HealthVulnerable,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${stats.reusedCount} entri menggunakan sandi yang sama",
                                style = MaterialTheme.typography.bodySmall,
                                color = HealthVulnerable
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricBadge(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
            .border(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp, horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$count",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

package com.example.gembok.ui.components

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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gembok.data.PasswordEntity
import com.example.gembok.data.PasswordHealthEvaluator
import com.example.gembok.ui.theme.HealthFair
import com.example.gembok.ui.theme.HealthOptimal
import com.example.gembok.ui.theme.HealthVulnerable

/**
 * Solid, calm list card component.
 * Adheres strictly to:
 * - Solid surface (no glassmorphism on list cards).
 * - Subtle outline, 14dp rounded corners, generous inner spacing.
 * - Clear hierarchy: Title, Username, Service/URL, copy, edit, delete actions.
 * - Minimum 44px/48dp touch targets.
 */
@Composable
fun PasswordItemCard(
    item: PasswordEntity,
    onCopyPassword: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    val strengthLevel = PasswordHealthEvaluator.evaluateSingle(item.password)
    val strengthColor = when (strengthLevel) {
        PasswordHealthEvaluator.StrengthLevel.STRONG -> HealthOptimal
        PasswordHealthEvaluator.StrengthLevel.FAIR -> HealthFair
        PasswordHealthEvaluator.StrengthLevel.WEAK -> HealthVulnerable
    }

    // Solid surface with crisp subtle border
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
            .padding(16.dp)
            .testTag("password_item_${item.id}")
    ) {
        Column {
            // Header Row: Service Logo + Service title, strength pill, site/app tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ServiceLogoAvatar(
                        title = item.title,
                        siteOrApp = item.siteOrApp,
                        size = 42.dp
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = (-0.2).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (item.siteOrApp.isNotBlank()) {
                            Text(
                                text = item.siteOrApp,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Strength indicator dot
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(strengthColor.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(strengthColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = when (strengthLevel) {
                            PasswordHealthEvaluator.StrengthLevel.STRONG -> "Kuat"
                            PasswordHealthEvaluator.StrengthLevel.FAIR -> "Cukup"
                            PasswordHealthEvaluator.StrengthLevel.WEAK -> "Lemah"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = strengthColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Account username
            Text(
                text = item.username,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password bar + Quick Action Controls (minimum 48dp touch targets)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (passwordVisible) item.password else "••••••••••••",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = if (passwordVisible) 0.5.sp else 2.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { passwordVisible = !passwordVisible }
                )

                // Toggle visibility
                IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (passwordVisible) "Sembunyikan sandi" else "Tampilkan sandi",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Copy password
                IconButton(
                    onClick = onCopyPassword,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("copy_password_btn_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Salin kata sandi",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Edit entry
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("edit_password_btn_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit entri",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Delete entry
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("delete_password_btn_${item.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus entri",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

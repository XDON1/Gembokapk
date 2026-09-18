package com.example.gembok.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gembok.data.PasswordHealthEvaluator
import com.example.gembok.ui.theme.HealthFair
import com.example.gembok.ui.theme.HealthOptimal
import com.example.gembok.ui.theme.HealthVulnerable
import kotlin.random.Random

/**
 * Clean, calm Password Generator modal/dialog.
 * Pure solid surface, iOS-inspired spacing, switches, slider, and copy/apply actions.
 */
@Composable
fun PasswordGeneratorSheet(
    onDismiss: () -> Unit,
    onApplyPassword: ((String) -> Unit)? = null,
    onCopyPassword: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var length by remember { mutableFloatStateOf(16f) }
    var includeUpper by remember { mutableStateOf(true) }
    var includeLower by remember { mutableStateOf(true) }
    var includeNumbers by remember { mutableStateOf(true) }
    var includeSymbols by remember { mutableStateOf(true) }
    var generatedPassword by remember { mutableStateOf("") }

    fun generate() {
        val upperChars = "ABCDEFGHJKLMNPQRSTUVWXYZ"
        val lowerChars = "abcdefghijkmnopqrstuvwxyz"
        val numberChars = "23456789"
        val symbolChars = "!@#$%^&*()-_=+[]{}<>?"

        val pool = StringBuilder()
        val guaranteed = mutableListOf<Char>()

        if (includeUpper) {
            pool.append(upperChars)
            guaranteed.add(upperChars[Random.nextInt(upperChars.length)])
        }
        if (includeLower) {
            pool.append(lowerChars)
            guaranteed.add(lowerChars[Random.nextInt(lowerChars.length)])
        }
        if (includeNumbers) {
            pool.append(numberChars)
            guaranteed.add(numberChars[Random.nextInt(numberChars.length)])
        }
        if (includeSymbols) {
            pool.append(symbolChars)
            guaranteed.add(symbolChars[Random.nextInt(symbolChars.length)])
        }

        if (pool.isEmpty()) {
            pool.append(lowerChars)
            guaranteed.add(lowerChars[Random.nextInt(lowerChars.length)])
        }

        val targetLen = length.toInt()
        val remaining = (targetLen - guaranteed.size).coerceAtLeast(0)
        val result = guaranteed.toMutableList()

        for (i in 0 until remaining) {
            result.add(pool[Random.nextInt(pool.length)])
        }
        result.shuffle()
        generatedPassword = result.joinToString("")
    }

    LaunchedEffect(length, includeUpper, includeLower, includeNumbers, includeSymbols) {
        generate()
    }

    val strengthLevel = PasswordHealthEvaluator.evaluateSingle(generatedPassword)
    val strengthColor = when (strengthLevel) {
        PasswordHealthEvaluator.StrengthLevel.STRONG -> HealthOptimal
        PasswordHealthEvaluator.StrengthLevel.FAIR -> HealthFair
        PasswordHealthEvaluator.StrengthLevel.WEAK -> HealthVulnerable
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Generator Sandi",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.3).sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Buat sandi acak dengan entropi tinggi",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(44.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Tutup",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Output preview box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = generatedPassword,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )

                    Row {
                        IconButton(
                            onClick = { generate() },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Acak ulang",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(
                            onClick = { onCopyPassword(generatedPassword) },
                            modifier = Modifier.size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Salin sandi",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Strength hint
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Panjang: ${length.toInt()} karakter",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Kekuatan: " + when (strengthLevel) {
                        PasswordHealthEvaluator.StrengthLevel.STRONG -> "Kuat"
                        PasswordHealthEvaluator.StrengthLevel.FAIR -> "Cukup"
                        PasswordHealthEvaluator.StrengthLevel.WEAK -> "Lemah"
                    },
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = strengthColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Slider
            Slider(
                value = length,
                onValueChange = { length = it },
                valueRange = 8f..32f,
                steps = 23,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Options (Toggles)
            ToggleRow(
                label = "Huruf Besar (A-Z)",
                checked = includeUpper,
                onCheckedChange = { includeUpper = it }
            )
            ToggleRow(
                label = "Huruf Kecil (a-z)",
                checked = includeLower,
                onCheckedChange = { includeLower = it }
            )
            ToggleRow(
                label = "Angka (0-9)",
                checked = includeNumbers,
                onCheckedChange = { includeNumbers = it }
            )
            ToggleRow(
                label = "Simbol Khusus (!@#$)",
                checked = includeSymbols,
                onCheckedChange = { includeSymbols = it }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons (min 44px)
            if (onApplyPassword != null) {
                Button(
                    onClick = {
                        onApplyPassword(generatedPassword)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Gunakan Kata Sandi Ini",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    onCopyPassword(generatedPassword)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(
                    text = "Salin & Tutup",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
            }
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
            )
        )
    }
}

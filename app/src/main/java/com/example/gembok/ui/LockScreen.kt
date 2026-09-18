package com.example.gembok.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gembok.ui.theme.HealthFair

/**
 * Clean, serene PIN setup, authentication, and recovery screen.
 * Supports:
 * 1. Master PIN authentication (4-6 digits)
 * 2. New Vault Master PIN creation with confirm step
 * 3. Opsi 1: "Reset Brankas" (Factory Reset) with serious security warning confirmation
 * 4. Opsi 2: "Pulihkan dengan Recovery Key" (16-character backup key) to reset PIN without losing vault data
 */
@Composable
fun LockScreen(
    hasMasterPin: Boolean,
    isDark: Boolean,
    onUnlock: (String) -> Boolean,
    onRecoverWithKey: (recoveryKey: String, newPin: String) -> Boolean,
    onFactoryResetVault: () -> Unit,
    modifier: Modifier = Modifier
) {
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isConfirmStep by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Dialog state for Forgot PIN options
    var showForgotPinDialog by remember { mutableStateOf(false) }
    var showRecoveryKeyDialog by remember { mutableStateOf(false) }
    var showFactoryResetConfirmDialog by remember { mutableStateOf(false) }

    // Input state inside Recovery Key Dialog
    var enteredRecoveryKey by remember { mutableStateOf("") }
    var newRecoveryPin by remember { mutableStateOf("") }
    var recoveryErrorMessage by remember { mutableStateOf<String?>(null) }

    fun handleDigit(d: String) {
        if (pin.length < 6) {
            pin += d
            errorMessage = null
        }
    }

    fun handleBackspace() {
        if (pin.isNotEmpty()) {
            pin = pin.dropLast(1)
            errorMessage = null
        }
    }

    fun submitPin() {
        if (!hasMasterPin) {
            if (!isConfirmStep) {
                if (pin.length < 4) {
                    errorMessage = "PIN minimal 4 angka"
                    return
                }
                confirmPin = pin
                pin = ""
                isConfirmStep = true
            } else {
                if (pin != confirmPin) {
                    errorMessage = "PIN konfirmasi tidak cocok"
                    pin = ""
                    isConfirmStep = false
                    return
                }
                val ok = onUnlock(pin)
                if (!ok) {
                    errorMessage = "Gagal membuat PIN"
                }
            }
        } else {
            if (pin.length < 4) {
                errorMessage = "PIN minimal 4 angka"
                return
            }
            val ok = onUnlock(pin)
            if (!ok) {
                errorMessage = "PIN tidak sesuai"
                pin = ""
            }
        }
    }

    // Glass panel for the upper key status
    val glassBg = if (isDark) {
        Brush.verticalGradient(
            listOf(Color(0x382F3730), Color(0x1F1E2220))
        )
    } else {
        Brush.verticalGradient(
            listOf(Color(0xE6FFFFFF), Color(0xB3F0ECE4))
        )
    }
    val glassBorder = if (isDark) Color(0x3D8FA87B) else Color(0x405E7A4A)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 380.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Glass Security Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(glassBg)
                    .border(1.dp, glassBorder, RoundedCornerShape(20.dp))
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "GEMBOK Vault",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "GEMBOK",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = when {
                            !hasMasterPin && !isConfirmStep -> "Buat PIN Master (4-6 angka)"
                            !hasMasterPin && isConfirmStep -> "Konfirmasi PIN Master Anda"
                            else -> "Masukkan PIN untuk membuka"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // PIN Dot Indicators (4-6 indicator slots)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val displaySlots = maxOf(4, pin.length)
                for (i in 0 until displaySlots) {
                    val isFilled = i < pin.length
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(
                                if (isFilled) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .border(
                                width = 1.dp,
                                color = if (isFilled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                    )
                }
            }

            // Error text space
            Column(
                modifier = Modifier
                    .height(36.dp)
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                AnimatedVisibility(visible = errorMessage != null) {
                    errorMessage?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Numeric Keypad (Solid surfaces, generous spacing, 68dp size)
            val keypad = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("C", "0", "OK")
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (row in keypad) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (key in row) {
                            when (key) {
                                "C" -> {
                                    Box(
                                        modifier = Modifier
                                            .size(68.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                            .clickable { handleBackspace() }
                                            .testTag("pin_key_backspace"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Backspace,
                                            contentDescription = "Hapus digit",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                "OK" -> {
                                    Box(
                                        modifier = Modifier
                                            .size(68.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.primary)
                                            .clickable { submitPin() }
                                            .testTag("pin_key_submit"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "OK",
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                                else -> {
                                    Box(
                                        modifier = Modifier
                                            .size(68.dp)
                                            .clip(CircleShape)
                                            .background(MaterialTheme.colorScheme.surface)
                                            .border(
                                                1.dp,
                                                MaterialTheme.colorScheme.outline,
                                                CircleShape
                                            )
                                            .clickable { handleDigit(key) }
                                            .testTag("pin_key_$key"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = key,
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Normal,
                                                fontSize = 24.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Bottom Actions: Forgot PIN button or Cancel Confirm Step
            if (hasMasterPin) {
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = { showForgotPinDialog = true },
                    colors = ButtonDefaults.textButtonColors(),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("forgot_pin_button")
                ) {
                    Text(
                        text = "Lupa 4 Digit Kata Sandi?",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else if (isConfirmStep) {
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = {
                        isConfirmStep = false
                        pin = ""
                        errorMessage = null
                    },
                    colors = ButtonDefaults.textButtonColors(),
                    modifier = Modifier.height(44.dp)
                ) {
                    Text(
                        text = "Kembali ke langkah awal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    // Modal Pilihan Lupa PIN: Opsi 1 (Reset Brankas) atau Opsi 2 (Kunci Pemulihan)
    if (showForgotPinDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPinDialog = false },
            shape = RoundedCornerShape(18.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Pemulihan Akses Brankas",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Pilih cara untuk memulihkan atau mereset akses ke brankas Anda:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Option 2 button: Recover with Key
                    Button(
                        onClick = {
                            showForgotPinDialog = false
                            enteredRecoveryKey = ""
                            newRecoveryPin = ""
                            recoveryErrorMessage = null
                            showRecoveryKeyDialog = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("option_use_recovery_key")
                    ) {
                        Icon(imageVector = Icons.Default.Key, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gunakan Kunci Pemulihan",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }

                    // Option 1 button: Factory Reset Vault
                    OutlinedButton(
                        onClick = {
                            showForgotPinDialog = false
                            showFactoryResetConfirmDialog = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("option_reset_vault")
                    ) {
                        Icon(imageVector = Icons.Default.WarningAmber, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Reset Total Brankas",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(
                    onClick = { showForgotPinDialog = false },
                    colors = ButtonDefaults.textButtonColors(),
                    modifier = Modifier.height(44.dp)
                ) {
                    Text("Tutup", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        )
    }

    // Modal Input Kunci Pemulihan (Opsi 2)
    if (showRecoveryKeyDialog) {
        AlertDialog(
            onDismissRequest = { showRecoveryKeyDialog = false },
            shape = RoundedCornerShape(18.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Kunci Pemulihan Cadangan",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Masukkan kunci 16 karakter cadangan yang telah Anda simpan dan tentukan 4 digit PIN baru:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = enteredRecoveryKey,
                        onValueChange = {
                            enteredRecoveryKey = it
                            recoveryErrorMessage = null
                        },
                        label = { Text("Kunci Pemulihan") },
                        placeholder = { Text("Contoh: ABCD-EFGH-1234-5678") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Characters,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_recovery_key"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    OutlinedTextField(
                        value = newRecoveryPin,
                        onValueChange = {
                            if (it.length <= 6 && it.all { ch -> ch.isDigit() }) {
                                newRecoveryPin = it
                                recoveryErrorMessage = null
                            }
                        },
                        label = { Text("PIN Master Baru (4-6 angka)") },
                        placeholder = { Text("Contoh: 1234") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.NumberPassword,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_new_pin_via_recovery"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )

                    if (recoveryErrorMessage != null) {
                        Text(
                            text = recoveryErrorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (enteredRecoveryKey.isBlank()) {
                            recoveryErrorMessage = "Masukkan kunci pemulihan"
                            return@Button
                        }
                        if (newRecoveryPin.length < 4) {
                            recoveryErrorMessage = "PIN baru minimal 4 angka"
                            return@Button
                        }
                        val success = onRecoverWithKey(enteredRecoveryKey, newRecoveryPin)
                        if (success) {
                            showRecoveryKeyDialog = false
                        } else {
                            recoveryErrorMessage = "Kunci pemulihan tidak valid"
                        }
                    },
                    modifier = Modifier.height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Pulihkan", color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showRecoveryKeyDialog = false },
                    modifier = Modifier.height(44.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Batal")
                }
            }
        )
    }

    // Modal Konfirmasi Reset Total Brankas (Opsi 1)
    if (showFactoryResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showFactoryResetConfirmDialog = false },
            shape = RoundedCornerShape(18.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Konfirmasi Reset Brankas",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "PERINGATAN KEAMANAN: Karena brankas ini bersifat offline dan tidak menyimpan data di cloud, mereset brankas akan MENGHAPUS PERMANEN seluruh kata sandi yang tersimpan di perangkat ini.",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Setelah reset selesai, Anda dapat langsung membuat PIN Master baru dari awal. Apakah Anda yakin ingin melanjutkan?",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFactoryResetConfirmDialog = false
                        onFactoryResetVault()
                    },
                    modifier = Modifier.height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(
                        text = "Ya, Hapus Semua & Reset",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onError
                    )
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showFactoryResetConfirmDialog = false },
                    modifier = Modifier.height(44.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Batal")
                }
            }
        )
    }
}

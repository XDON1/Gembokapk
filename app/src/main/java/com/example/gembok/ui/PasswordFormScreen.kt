package com.example.gembok.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gembok.data.PasswordEntity
import com.example.gembok.data.PasswordHealthEvaluator
import com.example.gembok.ui.components.ServiceLogoAvatar
import com.example.gembok.ui.theme.HealthFair
import com.example.gembok.ui.theme.HealthOptimal
import com.example.gembok.ui.theme.HealthVulnerable
import kotlin.random.Random

/**
 * Unified Add & Edit Password Screen.
 * Solid surfaces, clear iOS-style section grouping, precise input contrast,
 * generator helper, and password strength feedback.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordFormScreen(
    initialItem: PasswordEntity? = null,
    draft: PasswordDraft,
    onDraftChange: (PasswordDraft) -> Unit,
    onSave: (id: String?, title: String, username: String, pass: String, siteOrApp: String) -> Boolean,
    onCancel: () -> Unit,
    onOpenGenerator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEditMode = initialItem != null

    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    var showError by rememberSaveable { mutableStateOf(false) }

    val strengthLevel = PasswordHealthEvaluator.evaluateSingle(draft.password)
    val (strengthLabel, strengthColor, strengthProgress) = when {
        draft.password.isEmpty() -> Triple("", MaterialTheme.colorScheme.outline, 0f)
        strengthLevel == PasswordHealthEvaluator.StrengthLevel.WEAK -> Triple("Lemah", HealthVulnerable, 0.33f)
        strengthLevel == PasswordHealthEvaluator.StrengthLevel.FAIR -> Triple("Cukup", HealthFair, 0.66f)
        else -> Triple("Kuat", HealthOptimal, 1.0f)
    }

    fun handleQuickRandom() {
        val chars = "abcdefghjkmnpqrstuvwxyzABCDEFGHJKLMNPQRSTUVWXYZ23456789!@#$%&*"
        val generated = (1..16).map { chars[Random.nextInt(chars.length)] }.joinToString("")
        onDraftChange(draft.copy(password = generated))
        passwordVisible = true
    }

    fun handleSave() {
        if (draft.title.isBlank() || draft.username.isBlank() || draft.password.isBlank()) {
            showError = true
            return
        }
        val success = onSave(initialItem?.id, draft.title, draft.username, draft.password, draft.siteOrApp)
        if (!success) {
            showError = true
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Kata Sandi" else "Tambah Kata Sandi",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.3).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel, modifier = Modifier.size(48.dp)) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Primary Form Card (Solid Surface)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Informasi Akun",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Real-time live icon preview
                            ServiceLogoAvatar(
                                 title = draft.title.ifBlank { "Baru" },
                                 siteOrApp = draft.siteOrApp,
                                size = 36.dp
                            )
                        }

                        // Title / Service
                        OutlinedTextField(
                            value = draft.title,
                            onValueChange = {
                                onDraftChange(draft.copy(title = it))
                                showError = false
                            },
                            label = { Text("Nama Layanan / Judul *") },
                            placeholder = { Text("Contoh: Google, GitHub, Bank Mandiri") },
                            isError = showError && draft.title.isBlank(),
                            supportingText = if (showError && draft.title.isBlank()) {
                                { Text("Nama layanan wajib diisi", color = MaterialTheme.colorScheme.error) }
                            } else null,
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("form_input_title"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        // Username / Email
                        OutlinedTextField(
                            value = draft.username,
                            onValueChange = {
                                onDraftChange(draft.copy(username = it))
                                showError = false
                            },
                            label = { Text("Nama Pengguna / Email *") },
                            placeholder = { Text("Contoh: user@domain.com") },
                            isError = showError && draft.username.isBlank(),
                            supportingText = if (showError && draft.username.isBlank()) {
                                { Text("Nama pengguna wajib diisi", color = MaterialTheme.colorScheme.error) }
                            } else null,
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("form_input_username"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        // Website / App identifier
                        OutlinedTextField(
                            value = draft.siteOrApp,
                            onValueChange = { onDraftChange(draft.copy(siteOrApp = it)) },
                            label = { Text("Situs Web atau Aplikasi (Opsional)") },
                            placeholder = { Text("Contoh: https://accounts.google.com") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Uri,
                                imeAction = ImeAction.Next
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("form_input_site"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }

                // Password Card (Solid Surface)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardDefaults.outlinedCardBorder()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kata Sandi",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary
                            )

                            // Open full generator dialog
                            Button(
                                onClick = onOpenGenerator,
                                colors = ButtonDefaults.textButtonColors(),
                                modifier = Modifier.height(36.dp)
                            ) {
                                Text(
                                    text = "Generator Sandi",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        OutlinedTextField(
                            value = draft.password,
                            onValueChange = {
                                onDraftChange(draft.copy(password = it))
                                showError = false
                            },
                            label = { Text("Kata Sandi *") },
                            placeholder = { Text("Masukkan atau buat kata sandi") },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { handleQuickRandom() },
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoFixHigh,
                                            contentDescription = "Acak cepat",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { passwordVisible = !passwordVisible },
                                        modifier = Modifier.size(44.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = if (passwordVisible) "Sembunyikan" else "Tampilkan",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            },
                            isError = showError && draft.password.isBlank(),
                            supportingText = if (showError && draft.password.isBlank()) {
                                { Text("Kata sandi wajib diisi", color = MaterialTheme.colorScheme.error) }
                            } else null,
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("form_input_password"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        // Strength Meter
                        AnimatedVisibility(visible = draft.password.isNotEmpty()) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Kekuatan Sandi",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = strengthLabel,
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                                        color = strengthColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                LinearProgressIndicator(
                                    progress = { strengthProgress },
                                    color = strengthColor,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons (Minimum 48dp)
                Button(
                    onClick = { handleSave() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("form_submit_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = if (isEditMode) "Simpan Perubahan" else "Simpan ke Brankas",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("form_cancel_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Batal",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

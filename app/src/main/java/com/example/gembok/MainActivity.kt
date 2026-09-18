package com.example.gembok

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.gembok.ui.ActiveSheet
import com.example.gembok.ui.GembokViewModel
import com.example.gembok.ui.HomeScreen
import com.example.gembok.ui.LockScreen
import com.example.gembok.ui.PasswordFormScreen
import com.example.gembok.ui.UiState
import com.example.gembok.ui.components.ErrorState
import com.example.gembok.ui.components.LoadingState
import com.example.gembok.ui.components.PasswordGeneratorSheet
import com.example.gembok.ui.components.RecoveryKeyNoticeDialog
import com.example.gembok.ui.theme.GembokTheme

class MainActivity : ComponentActivity() {
    private val viewModel: GembokViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val systemIsDark = isSystemInDarkTheme()
            val isDarkEffective = systemIsDark

            val isLocked by viewModel.isLocked.collectAsState()
            val hasMasterPin by viewModel.hasMasterPin.collectAsState()
            val recoveryKey by viewModel.recoveryKey.collectAsState()
            val newlyGeneratedRecoveryKey by viewModel.newlyGeneratedRecoveryKey.collectAsState()
            val activeSheet by viewModel.activeSheet.collectAsState()
            val passwordDraft by viewModel.passwordDraft.collectAsState()
            val rawPasswords by viewModel.rawPasswords.collectAsState()
            val filteredPasswords by viewModel.filteredPasswords.collectAsState()
            val searchQuery by viewModel.searchQuery.collectAsState()
            val healthStats by viewModel.healthStats.collectAsState()
            val uiState by viewModel.uiState.collectAsState()

            LaunchedEffect(Unit) {
                viewModel.toastMessage.collect { message ->
                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                }
            }

            GembokTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(WindowInsets.systemBars.asPaddingValues())
                ) {
                    when (uiState) {
                        is UiState.Loading -> {
                            LoadingState()
                        }
                        is UiState.Error -> {
                            ErrorState(
                                errorMessage = (uiState as UiState.Error).message,
                                onRetry = { viewModel.resetErrorState() }
                            )
                        }
                        is UiState.Ready -> {
                            if (isLocked) {
                                LockScreen(
                                    hasMasterPin = hasMasterPin,
                                    isDark = isDarkEffective,
                                    onUnlock = { pin -> viewModel.unlock(pin) },
                                    onRecoverWithKey = { key, newPin ->
                                        viewModel.recoverWithKey(key, newPin)
                                    },
                                    onFactoryResetVault = {
                                        viewModel.factoryResetVault()
                                    }
                                )
                            } else {
                                when (val sheet = activeSheet) {
                                    is ActiveSheet.None, is ActiveSheet.Generator, is ActiveSheet.ShowRecoveryKey -> {
                                        HomeScreen(
                                            passwords = filteredPasswords,
                                            totalEntriesCount = rawPasswords.size,
                                            searchQuery = searchQuery,
                                            healthStats = healthStats,
                                            isDark = isDarkEffective,
                                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                            onLock = { viewModel.lock() },
                                            onNavigateToAdd = { viewModel.openSheet(ActiveSheet.Add) },
                                            onNavigateToEdit = { item -> viewModel.openSheet(ActiveSheet.Edit(item)) },
                                            onOpenGenerator = { viewModel.openSheet(ActiveSheet.Generator) },
                                            onShowRecoveryKey = { viewModel.openSheet(ActiveSheet.ShowRecoveryKey) },
                                            onCopyPassword = { pass -> viewModel.copyToClipboard(pass, "password") },
                                            onDeletePassword = { id -> viewModel.deletePassword(id) }
                                        )

                                        if (sheet is ActiveSheet.Generator) {
                                            Dialog(
                                                onDismissRequest = { viewModel.closeSheet() },
                                                properties = DialogProperties(usePlatformDefaultWidth = false)
                                            ) {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    PasswordGeneratorSheet(
                                                        onDismiss = { viewModel.closeSheet() },
                                                        onCopyPassword = { pass ->
                                                            viewModel.copyToClipboard(pass, "generated_password")
                                                        }
                                                    )
                                                }
                                            }
                                        }

                                        if (sheet is ActiveSheet.ShowRecoveryKey) {
                                            Dialog(
                                                onDismissRequest = { viewModel.closeSheet() },
                                                properties = DialogProperties(usePlatformDefaultWidth = false)
                                            ) {
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    RecoveryKeyNoticeDialog(
                                                        recoveryKey = recoveryKey ?: "KUNCI-TIDAK-DITEMUKAN",
                                                        onCopyKey = { key ->
                                                            viewModel.copyToClipboard(key, "recovery_key")
                                                        },
                                                        onDismiss = { viewModel.closeSheet() }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    is ActiveSheet.Add -> {
                                        PasswordFormScreen(
                                            initialItem = null,
                                            draft = passwordDraft,
                                            onDraftChange = { viewModel.updatePasswordDraft(it) },
                                            onSave = { _, title, user, pass, site ->
                                                viewModel.savePassword(null, title, user, pass, site)
                                            },
                                            onCancel = { viewModel.closeSheet() },
                                            onOpenGenerator = { viewModel.openSheet(ActiveSheet.Generator) }
                                        )
                                    }
                                    is ActiveSheet.Edit -> {
                                        PasswordFormScreen(
                                            initialItem = sheet.item,
                                            draft = passwordDraft,
                                            onDraftChange = { viewModel.updatePasswordDraft(it) },
                                            onSave = { id, title, user, pass, site ->
                                                viewModel.savePassword(id, title, user, pass, site)
                                            },
                                            onCancel = { viewModel.closeSheet() },
                                            onOpenGenerator = { viewModel.openSheet(ActiveSheet.Generator) }
                                        )
                                    }
                                }

                                // If a new PIN was just created, display the Recovery Key notice dialog once
                                newlyGeneratedRecoveryKey?.let { newKey ->
                                    Dialog(
                                        onDismissRequest = { viewModel.dismissNewlyGeneratedRecoveryKey() },
                                        properties = DialogProperties(usePlatformDefaultWidth = false)
                                    ) {
                                        Box(
                                            modifier = Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            RecoveryKeyNoticeDialog(
                                                recoveryKey = newKey,
                                                onCopyKey = { key ->
                                                    viewModel.copyToClipboard(key, "recovery_key")
                                                },
                                                onDismiss = { viewModel.dismissNewlyGeneratedRecoveryKey() }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.lock()
    }
}

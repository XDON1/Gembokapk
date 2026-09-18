package com.example.gembok.ui

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gembok.data.AppDatabase
import com.example.gembok.data.PasswordEntity
import com.example.gembok.data.PasswordHealthEvaluator
import com.example.gembok.data.PasswordHealthStats
import com.example.gembok.data.PreferencesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed interface ActiveSheet {
    data object None : ActiveSheet
    data class Edit(val item: PasswordEntity) : ActiveSheet
    data object Add : ActiveSheet
    data object Generator : ActiveSheet
    data object ShowRecoveryKey : ActiveSheet
}

data class PasswordDraft(
    val title: String = "",
    val username: String = "",
    val password: String = "",
    val siteOrApp: String = ""
)

sealed interface UiState {
    data object Loading : UiState
    data object Ready : UiState
    data class Error(val message: String) : UiState
}

class GembokViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val dao = database.passwordDao()
    private val prefs = PreferencesRepository(application)
    private val crypto = com.example.gembok.data.VaultCrypto()
    private val clipboardManager = application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    private val _isLocked = MutableStateFlow(true)
    val isLocked: StateFlow<Boolean> = _isLocked.asStateFlow()

    private val _hasMasterPin = MutableStateFlow(false)
    val hasMasterPin: StateFlow<Boolean> = _hasMasterPin.asStateFlow()

    private val _recoveryKey = MutableStateFlow<String?>(null)
    val recoveryKey: StateFlow<String?> = _recoveryKey.asStateFlow()

    // Flag to prompt user to save their recovery key upon initial vault creation
    private val _newlyGeneratedRecoveryKey = MutableStateFlow<String?>(null)
    val newlyGeneratedRecoveryKey: StateFlow<String?> = _newlyGeneratedRecoveryKey.asStateFlow()

    private val _activeSheet = MutableStateFlow<ActiveSheet>(ActiveSheet.None)
    val activeSheet: StateFlow<ActiveSheet> = _activeSheet.asStateFlow()

    private val _passwordDraft = MutableStateFlow(PasswordDraft())
    val passwordDraft: StateFlow<PasswordDraft> = _passwordDraft.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow<UiState>(UiState.Ready)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage.asSharedFlow()

    private var clipboardClearJob: Job? = null

    val rawPasswords: StateFlow<List<PasswordEntity>> = dao.getAllPasswords()
        .map { entries -> entries.map(::decryptEntry) }
        .catch { error ->
            _uiState.value = UiState.Error(
                error.message ?: "Data vault tidak dapat dibuka."
            )
            emit(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredPasswords: StateFlow<List<PasswordEntity>> = combine(rawPasswords, _searchQuery) { list, query ->
        if (query.isBlank()) {
            list
        } else {
            val q = query.trim().lowercase()
            list.filter {
                it.title.lowercase().contains(q) ||
                it.username.lowercase().contains(q) ||
                it.siteOrApp.lowercase().contains(q)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val healthStats: StateFlow<PasswordHealthStats> = rawPasswords
        .map { PasswordHealthEvaluator.calculate(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PasswordHealthStats(0, 0, 0, 0, 0, null))

    init {
        runCatching { refreshVaultState() }
            .onFailure { error ->
                _uiState.value = UiState.Error(
                    error.message ?: "Keamanan vault tidak dapat dibaca."
                )
            }
        migrateUnencryptedEntries()
    }

    private fun refreshVaultState() {
        val pin = prefs.getMasterPin()
        val key = prefs.getRecoveryKey()
        _recoveryKey.value = key
        if (pin.isNullOrEmpty()) {
            _hasMasterPin.value = false
            _isLocked.value = true
        } else {
            _hasMasterPin.value = true
            _isLocked.value = true
        }
    }

    private fun migrateUnencryptedEntries() {
        viewModelScope.launch {
            dao.getAllPasswords().first()
                .filter { !it.password.startsWith("ENC1:") }
                .forEach { dao.insertPassword(encryptEntry(it)) }
        }
    }

    private fun generate16CharRecoveryKey(): String {
        val chars = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"
        val raw = (1..16).map { chars[Random.nextInt(chars.length)] }.joinToString("")
        // Format: XXXX-XXXX-XXXX-XXXX
        return raw.chunked(4).joinToString("-")
    }

    fun unlock(inputPin: String): Boolean {
        val storedPin = prefs.getMasterPin()
        return if (storedPin.isNullOrEmpty()) {
            if (inputPin.length < 4) {
                false
            } else {
                prefs.setMasterPin(inputPin)
                val newKey = generate16CharRecoveryKey()
                prefs.setRecoveryKey(newKey)
                _recoveryKey.value = newKey
                _newlyGeneratedRecoveryKey.value = newKey
                _hasMasterPin.value = true
                _isLocked.value = false
                viewModelScope.launch { _toastMessage.emit("PIN Master berhasil dibuat") }
                true
            }
        } else {
            if (inputPin == storedPin) {
                _isLocked.value = false
                true
            } else {
                false
            }
        }
    }

    /**
     * Opsi 2: Verifikasi Recovery Key dan perbarui Master PIN tanpa menghapus data
     */
    fun recoverWithKey(enteredKey: String, newPin: String): Boolean {
        val storedKey = prefs.getRecoveryKey()
        if (storedKey.isNullOrBlank() || newPin.length < 4) {
            return false
        }
        val cleanEntered = enteredKey.trim().replace("-", "").uppercase()
        val cleanStored = storedKey.trim().replace("-", "").uppercase()

        return if (cleanEntered == cleanStored) {
            prefs.setMasterPin(newPin)
            // Hasilkan Recovery Key baru untuk keamanan berikutnya
            val refreshedKey = generate16CharRecoveryKey()
            prefs.setRecoveryKey(refreshedKey)
            _recoveryKey.value = refreshedKey
            _newlyGeneratedRecoveryKey.value = refreshedKey
            _isLocked.value = false
            viewModelScope.launch {
                _toastMessage.emit("PIN berhasil diperbarui dengan Kunci Pemulihan")
            }
            true
        } else {
            false
        }
    }

    /**
     * Opsi 1: Reset Brankas (Factory Reset)
     * Menghapus seluruh kata sandi dari database Room dan mengosongkan Master PIN
     */
    fun factoryResetVault() {
        viewModelScope.launch {
            try {
                dao.deleteAll()
                prefs.clearAll()
                _hasMasterPin.value = false
                _isLocked.value = true
                _recoveryKey.value = null
                _newlyGeneratedRecoveryKey.value = null
                _activeSheet.value = ActiveSheet.None
                _toastMessage.emit("Brankas telah direset. Silakan buat PIN baru.")
            } catch (e: Exception) {
                _toastMessage.emit("Gagal mereset brankas: ${e.localizedMessage}")
            }
        }
    }

    fun dismissNewlyGeneratedRecoveryKey() {
        _newlyGeneratedRecoveryKey.value = null
    }

    fun lock() {
        if (_hasMasterPin.value) {
            _isLocked.value = true
        }
    }

    fun openSheet(sheet: ActiveSheet) {
        _activeSheet.value = sheet
        _passwordDraft.value = when (sheet) {
            is ActiveSheet.Edit -> PasswordDraft(sheet.item.title, sheet.item.username, sheet.item.password, sheet.item.siteOrApp)
            ActiveSheet.Add -> PasswordDraft()
            else -> _passwordDraft.value
        }
    }

    fun closeSheet() {
        _activeSheet.value = ActiveSheet.None
        _passwordDraft.value = PasswordDraft()
    }

    fun updatePasswordDraft(draft: PasswordDraft) {
        _passwordDraft.value = draft
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun savePassword(
        id: String? = null,
        title: String,
        username: String,
        pass: String,
        siteOrApp: String
    ): Boolean {
        if (title.isBlank() || username.isBlank() || pass.isBlank()) {
            return false
        }
        viewModelScope.launch {
            try {
                val entity = PasswordEntity(
                    id = id ?: java.util.UUID.randomUUID().toString(),
                    title = crypto.encrypt(title.trim()),
                    username = crypto.encrypt(username.trim()),
                    password = crypto.encrypt(pass),
                    siteOrApp = crypto.encrypt(siteOrApp.trim()),
                    createdAt = System.currentTimeMillis()
                )
                dao.insertPassword(entity)
                _activeSheet.value = ActiveSheet.None
                _toastMessage.emit(if (id == null) "Entri disimpan" else "Entri diperbarui")
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Gagal menyimpan: ${e.localizedMessage}")
            }
        }
        return true
    }

    private fun decryptEntry(entry: PasswordEntity): PasswordEntity = try {
        entry.copy(
            title = crypto.decrypt(entry.title),
            username = crypto.decrypt(entry.username),
            password = crypto.decrypt(entry.password),
            siteOrApp = crypto.decrypt(entry.siteOrApp)
        )
    } catch (error: Exception) {
        _uiState.value = UiState.Error("Data vault tidak dapat dibuka. Perbaiki atau pulihkan kunci perangkat, lalu coba lagi.")
        throw error
    }

    private fun encryptEntry(entry: PasswordEntity): PasswordEntity = entry.copy(
        title = crypto.encrypt(entry.title),
        username = crypto.encrypt(entry.username),
        password = crypto.encrypt(entry.password),
        siteOrApp = crypto.encrypt(entry.siteOrApp)
    )

    fun deletePassword(id: String) {
        viewModelScope.launch {
            try {
                dao.deleteById(id)
                _toastMessage.emit("Entri dihapus")
            } catch (e: Exception) {
                _toastMessage.emit("Gagal menghapus entri")
            }
        }
    }

    fun copyToClipboard(text: String, label: String = "password") {
        val clip = ClipData.newPlainText(label, text)
        clipboardManager.setPrimaryClip(clip)

        viewModelScope.launch {
            _toastMessage.emit("Disalin ke papan klip. Otomatis dibersihkan dlm 60 dtk.")
        }

        clipboardClearJob?.cancel()
        clipboardClearJob = viewModelScope.launch {
            delay(60_000)
            try {
                val currentClip = clipboardManager.primaryClip
                if (currentClip != null && currentClip.itemCount > 0) {
                    val currentText = currentClip.getItemAt(0).text?.toString()
                    if (currentText == text) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            clipboardManager.clearPrimaryClip()
                        } else {
                            val emptyClip = ClipData.newPlainText("", "")
                            clipboardManager.setPrimaryClip(emptyClip)
                        }
                    }
                }
            } catch (_: Exception) {}
        }
    }

    fun resetErrorState() {
        _uiState.value = UiState.Ready
    }
}

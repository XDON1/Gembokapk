package com.example.gembok.data

import android.content.Context
import android.content.SharedPreferences

class PreferencesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("gembok_prefs", Context.MODE_PRIVATE)
    private val crypto = VaultCrypto()

    companion object {
        private const val KEY_MASTER_PIN = "gembok_pin"
        private const val KEY_RECOVERY_KEY = "gembok_recovery_key"
    }

    fun getMasterPin(): String? {
        return prefs.getString(KEY_MASTER_PIN, null)?.let { stored ->
            if (stored.startsWith("ENC1:")) crypto.decrypt(stored)
            else stored.also { prefs.edit().putString(KEY_MASTER_PIN, crypto.encrypt(it)).apply() }
        }
    }

    fun setMasterPin(pin: String) {
        prefs.edit().putString(KEY_MASTER_PIN, crypto.encrypt(pin)).apply()
    }

    fun getRecoveryKey(): String? {
        return prefs.getString(KEY_RECOVERY_KEY, null)?.let { stored ->
            if (stored.startsWith("ENC1:")) crypto.decrypt(stored)
            else stored.also { prefs.edit().putString(KEY_RECOVERY_KEY, crypto.encrypt(it)).apply() }
        }
    }

    fun setRecoveryKey(key: String) {
        prefs.edit().putString(KEY_RECOVERY_KEY, crypto.encrypt(key)).apply()
    }

    fun clearVaultSecurity() {
        prefs.edit()
            .remove(KEY_MASTER_PIN)
            .remove(KEY_RECOVERY_KEY)
            .apply()
    }

    fun clearAll() = prefs.edit().clear().apply()

}

package com.example.gembok.data

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class VaultCrypto {
    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }

    private fun key(): SecretKey {
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build())
        return generator.generateKey()
    }

    fun encrypt(value: String): String {
        if (value.isEmpty()) return value
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key())
        return PREFIX + Base64.encodeToString(
            cipher.iv + cipher.doFinal(value.toByteArray(StandardCharsets.UTF_8)),
            Base64.NO_WRAP
        )
    }

    fun decrypt(value: String): String {
        if (!value.startsWith(PREFIX)) return value
        val bytes = try {
            Base64.decode(value.removePrefix(PREFIX), Base64.NO_WRAP)
        } catch (error: IllegalArgumentException) {
            throw VaultCryptoException("Format data terenkripsi tidak valid", error)
        }
        if (bytes.size <= IV_LENGTH) throw VaultCryptoException("Data terenkripsi tidak lengkap")
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(TAG_LENGTH, bytes.copyOf(IV_LENGTH)))
            String(cipher.doFinal(bytes.copyOfRange(IV_LENGTH, bytes.size)), StandardCharsets.UTF_8)
        } catch (error: Exception) {
            throw VaultCryptoException("Data terenkripsi tidak dapat dibuka", error)
        }
    }

    class VaultCryptoException(message: String, cause: Throwable? = null) : Exception(message, cause)

    companion object {
        private const val KEY_ALIAS = "gembok_vault_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val PREFIX = "ENC1:"
        private const val IV_LENGTH = 12
        private const val TAG_LENGTH = 128
    }
}

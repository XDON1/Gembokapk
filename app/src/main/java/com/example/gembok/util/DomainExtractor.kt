package com.example.gembok.util

import java.net.URI

object DomainExtractor {
    /**
     * Extracts a clean domain or query token from user input.
     * Examples:
     * - "https://accounts.google.com/signin" -> "google.com"
     * - "www.github.com" -> "github.com"
     * - "netflix.com" -> "netflix.com"
     * - "spotify" -> "spotify.com"
     * - "Bank Mandiri" -> "bankmandiri.com"
     */
    fun extractDomain(siteOrApp: String, title: String): String? {
        val input = if (siteOrApp.isNotBlank()) siteOrApp.trim() else title.trim()
        if (input.isBlank()) return null

        // If it looks like a URL or domain
        try {
            val candidate = if (input.startsWith("http://", ignoreCase = true) ||
                input.startsWith("https://", ignoreCase = true)
            ) {
                URI(input).host ?: input
            } else {
                // If it contains a dot, might be domain (e.g. google.com or mail.yahoo.co.id)
                if (input.contains(".") && !input.contains(" ")) {
                    val hostPart = input.substringBefore("/").substringBefore("?")
                    hostPart
                } else {
                    // It's a brand/app name, e.g. "Google", "GitHub", "Netflix", "Discord"
                    val sanitized = input.lowercase().replace("[^a-z0-9]".toRegex(), "")
                    if (sanitized.isNotEmpty()) "$sanitized.com" else null
                }
            }

            val cleaned = candidate?.removePrefix("www.")?.lowercase()?.trim()
            return if (!cleaned.isNullOrBlank() && cleaned.contains(".")) cleaned else null
        } catch (_: Exception) {
            val sanitized = input.lowercase().replace("[^a-z0-9]".toRegex(), "")
            return if (sanitized.isNotEmpty()) "$sanitized.com" else null
        }
    }

    /**
     * Build Google Favicon URL with 128px size for sharp display.
     */
    fun getFaviconUrl(siteOrApp: String, title: String): String? {
        val domain = extractDomain(siteOrApp, title) ?: return null
        return "https://www.google.com/s2/favicons?domain=$domain&sz=128"
    }

    /**
     * Generate 1-2 initials for monogram fallback, e.g.
     * "Google" -> "G"
     * "Bank Mandiri" -> "BM"
     * "GitHub" -> "GH"
     */
    fun getInitials(title: String): String {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return "?"

        val words = trimmed.split("\\s+".toRegex()).filter { it.isNotEmpty() }
        return when {
            words.size >= 2 -> {
                "${words[0].first().uppercaseChar()}${words[1].first().uppercaseChar()}"
            }
            trimmed.length >= 2 && trimmed[1].isUpperCase() -> {
                // CamelCase like "GitHub" -> "GH"
                val upperChars = trimmed.filter { it.isUpperCase() }
                if (upperChars.length >= 2) upperChars.take(2) else trimmed.take(2).uppercase()
            }
            else -> trimmed.take(1).uppercase()
        }
    }
}

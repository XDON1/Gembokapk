package com.example.gembok.data

data class PasswordHealthStats(
    val totalCount: Int,
    val weakCount: Int,       // Length < 8 or lowercase only
    val fairCount: Int,       // Length 8..11 or missing symbols
    val strongCount: Int,     // Length >= 12 with mix
    val reusedCount: Int,     // Passwords that appear more than once across entries
    val healthScore: Int?     // Null until real stored entries exist
)

object PasswordHealthEvaluator {

    fun calculate(items: List<PasswordEntity>): PasswordHealthStats {
        if (items.isEmpty()) {
            return PasswordHealthStats(
                totalCount = 0,
                weakCount = 0,
                fairCount = 0,
                strongCount = 0,
                reusedCount = 0,
                healthScore = null
            )
        }

        var weak = 0
        var fair = 0
        var strong = 0

        // Count password values to detect real reused passwords
        val passwordFrequency = mutableMapOf<String, Int>()
        for (item in items) {
            val p = item.password
            passwordFrequency[p] = (passwordFrequency[p] ?: 0) + 1
        }

        var reused = 0
        for (item in items) {
            val count = passwordFrequency[item.password] ?: 0
            if (count > 1) {
                reused++
            }

            val p = item.password
            val length = p.length
            val hasUpper = p.any { it.isUpperCase() }
            val hasLower = p.any { it.isLowerCase() }
            val hasDigit = p.any { it.isDigit() }
            val hasSpecial = p.any { !it.isLetterOrDigit() }

            val categories = listOf(hasUpper, hasLower, hasDigit, hasSpecial).count { it }

            when {
                length < 8 || categories <= 1 -> weak++
                length >= 12 && categories >= 3 -> strong++
                else -> fair++
            }
        }

        // Real health score formula:
        // Strong gives 1.0 point, Fair gives 0.5 points, Weak gives 0 points.
        // Reused penalizes by 20% proportional to total.
        val baseScore = ((strong * 1.0f + fair * 0.55f) / items.size.toFloat()) * 100f
        val reusePenalty = (reused.toFloat() / items.size.toFloat()) * 25f
        val finalScore = (baseScore - reusePenalty).coerceIn(0f, 100f).toInt()

        return PasswordHealthStats(
            totalCount = items.size,
            weakCount = weak,
            fairCount = fair,
            strongCount = strong,
            reusedCount = reused,
            healthScore = finalScore
        )
    }

    enum class StrengthLevel {
        WEAK,
        FAIR,
        STRONG
    }

    fun evaluateSingle(password: String): StrengthLevel {
        if (password.isEmpty()) return StrengthLevel.WEAK
        val length = password.length
        val hasUpper = password.any { it.isUpperCase() }
        val hasLower = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }
        val categories = listOf(hasUpper, hasLower, hasDigit, hasSpecial).count { it }

        return when {
            length < 8 || categories <= 1 -> StrengthLevel.WEAK
            length >= 12 && categories >= 3 -> StrengthLevel.STRONG
            else -> StrengthLevel.FAIR
        }
    }
}

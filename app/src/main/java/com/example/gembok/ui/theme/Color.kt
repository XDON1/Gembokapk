package com.example.gembok.ui.theme

import androidx.compose.ui.graphics.Color

// Light Palette (Warm White + Charcoal + Warm Olive Accent)
// Warm, calm, pristine off-white canvas inspired by high-end editorial and serene iOS layouts
val LightWarmBg = Color(0xFFF9F8F6)        // Gentle warm white / limestone
val LightWarmSurface = Color(0xFFFFFFFF)   // Pure solid crisp white for list cards and inputs
val LightWarmSurfaceElevated = Color(0xFFF2EFE9) // Subtle grouping container
val LightWarmBorder = Color(0xFFE5E0D8)    // Soft sand-tinted border line
val LightCharcoalText = Color(0xFF1F2220)  // Deep soft charcoal, easy on the eyes, avoids harsh #000
val LightSubtext = Color(0xFF6B726C)       // Medium muted charcoal-sage for secondary text

// Dark Palette (True Deep Charcoal + Warm Charcoal Surfaces + Luminous Olive Accent)
val DarkCharcoalBg = Color(0xFF141615)     // Refined deep warm charcoal canvas
val DarkCharcoalSurface = Color(0xFF1D201E)// Solid warm charcoal card surface
val DarkCharcoalSurfaceElevated = Color(0xFF262A27) // Nested/focused control background
val DarkCharcoalBorder = Color(0xFF2F3430) // Low-contrast hairline border
val DarkWarmText = Color(0xFFF2F0EB)       // Soft ivory white text, non-glare
val DarkSubtext = Color(0xFF8F9891)        // Subdued sage-charcoal secondary text

// Single Intentional Accent: Warm Olive Green (Herbaceous, grounding, secure, non-electric)
val OliveAccent = Color(0xFF5E7A4A)        // Refined olive primary on light mode
val OliveAccentDark = Color(0xFF8FA87B)    // Luminous olive primary on dark mode
val OliveAccentSubtleLight = Color(0x1F5E7A4A) // Translucent fill
val OliveAccentSubtleDark = Color(0x288FA87B)

// Functional Status Tints (Grounded, muted tones, no eye-searing neon)
val HealthVulnerable = Color(0xFFC75440)   // Terracotta red for vulnerable/short
val HealthFair = Color(0xFFCA8A2A)         // Warm amber for fair/reused
val HealthOptimal = Color(0xFF4D8553)      // Forest olive for strong unique passwords

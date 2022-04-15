package me.nathanfallet.unlockpremium.models

import java.io.Serializable

data class PremiumArgument(
    val title: String,
    val description: String,
    val icon: Int
) : Serializable
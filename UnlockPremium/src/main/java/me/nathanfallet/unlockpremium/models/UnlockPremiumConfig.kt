package me.nathanfallet.unlockpremium.models

import java.io.Serializable

data class UnlockPremiumConfig(
    val arguments: List<PremiumArgument>,
    val sku: String,
    val introMode: Boolean
) : Serializable {

    constructor(
        arguments: List<PremiumArgument>,
        sku: String
    ) : this(arguments, sku, false)

}

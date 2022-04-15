package me.nathanfallet.unlockpremium.viewmodels

import android.app.Activity
import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.*
import com.android.billingclient.api.*
import me.nathanfallet.unlockpremium.models.UnlockPremiumConfig

class UnlockPremiumViewModel(application: Application, val configuration: UnlockPremiumConfig) :
    AndroidViewModel(application),
    PurchasesUpdatedListener {

    // Properties

    private val paymentSucceeded = MutableLiveData(false)

    private val skuDetails = MutableLiveData<SkuDetails>(null)

    // Billing client

    private var billingClient = BillingClient.newBuilder(getApplication())
        .setListener(this)
        .enablePendingPurchases()
        .build()

    // Getters

    fun getPaymentSucceeded(): LiveData<Boolean> {
        return paymentSucceeded
    }

    fun getSkuDetails(): LiveData<SkuDetails> {
        return skuDetails
    }

    // Methods

    fun onAppear() {
        // Load products
        startConnection()
    }

    fun startConnection() {
        // Start a connection with billing client
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    querySkuDetails()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                startConnection()
            }
        })
    }

    private fun querySkuDetails() {
        val skuList = ArrayList<String>()
        skuList.add(configuration.sku)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)

        // leverage querySkuDetails Kotlin extension function
        billingClient.querySkuDetailsAsync(params.build()) { _, details ->
            Handler(Looper.getMainLooper()).post {
                skuDetails.value = details?.firstOrNull()
            }
        }
    }

    fun launchPurchase(activity: Activity) {
        skuDetails.value?.let {
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(it)
                .build()
            billingClient.launchBillingFlow(activity, flowParams).responseCode
        }
    }

    fun launchRestore() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.INAPP) { _, purchases ->
            Handler(Looper.getMainLooper()).post {
                for (purchase in purchases) {
                    handlePurchase(purchase)
                }
            }
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.

        } else {
            // Handle any other error codes.

        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken).build()
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) {
                    if (it.responseCode == BillingClient.BillingResponseCode.OK) {
                        // Succeeded
                        paymentSucceeded.value = true
                    }
                }
            } else {
                // Restore
                paymentSucceeded.value = true
            }
        }
    }

}

class UnlockPremiumViewModelFactory(
    private val application: Application,
    private val configuration: UnlockPremiumConfig
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UnlockPremiumViewModel(application, configuration) as T
    }

}
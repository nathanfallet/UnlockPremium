package me.nathanfallet.unlockpremiumsample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import me.nathanfallet.unlockpremium.activities.UnlockPremiumActivity
import me.nathanfallet.unlockpremium.models.PremiumArgument
import me.nathanfallet.unlockpremium.models.UnlockPremiumConfig

class MainActivity : AppCompatActivity() {

    private lateinit var button1: Button
    private lateinit var button2: Button

    private val unlockPremiumRequest =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            it.data?.getBooleanExtra(UnlockPremiumActivity.EXTRAS.SUCCESS, false)?.let { success ->
                if (success) {
                    Toast.makeText(this, "Premium unlocked!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)

        button1.setOnClickListener {
            showPremium(false)
        }
        button2.setOnClickListener {
            showPremium(true)
        }
    }

    private fun showPremium(introMode: Boolean) {
        val config = UnlockPremiumConfig(
            listOf(
                PremiumArgument(
                    "A feature name",
                    "A feature description",
                    R.drawable.ic_baseline_apps_24
                )
            ),
            "myAppSKU.premiumPurchase",
            introMode
        )

        val intent = Intent(this, UnlockPremiumActivity::class.java)
        intent.putExtra(UnlockPremiumActivity.EXTRAS.CONFIGURATION, config)
        unlockPremiumRequest.launch(intent)
    }

}
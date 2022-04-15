package me.nathanfallet.unlockpremium.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.nathanfallet.unlockpremium.R
import me.nathanfallet.unlockpremium.models.PremiumArgument
import me.nathanfallet.unlockpremium.models.UnlockPremiumConfig
import me.nathanfallet.unlockpremium.viewmodels.UnlockPremiumViewModel
import me.nathanfallet.unlockpremium.viewmodels.UnlockPremiumViewModelFactory

class UnlockPremiumActivity : AppCompatActivity() {

    object EXTRAS {
        const val CONFIGURATION = "configuration"
        const val SUCCESS = "success"
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var unlockButton: Button
    private lateinit var restoreButton: Button

    private lateinit var recyclerViewAdapter: PremiumRecyclerViewAdapter

    private lateinit var viewModel: UnlockPremiumViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // View model instance
        val configuration =
            intent.getSerializableExtra(EXTRAS.CONFIGURATION) as? UnlockPremiumConfig
                ?: throw IllegalArgumentException("No configuration passed to UnlockPremiumActivity")
        viewModel =
            ViewModelProvider(this, UnlockPremiumViewModelFactory(application, configuration))
                .get(UnlockPremiumViewModel::class.java)

        // Content view
        setContentView(R.layout.activity_unlock_premium)
        setTitle(if (configuration.introMode) R.string.premium_unlock_intro else R.string.premium_unlock)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Configure recycler view
        recyclerView = findViewById(R.id.recyclerView)
        recyclerViewAdapter = PremiumRecyclerViewAdapter(viewModel)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = recyclerViewAdapter
        progressBar = findViewById(R.id.progressBar)
        unlockButton = findViewById(R.id.unlock_button)
        restoreButton = findViewById(R.id.restore_button)

        // Configure observers
        viewModel.getSkuDetails().observe(this) {
            progressBar.visibility = if (it != null) View.GONE else View.VISIBLE
            unlockButton.visibility = if (it != null) View.VISIBLE else View.GONE
            restoreButton.visibility = if (it != null) View.VISIBLE else View.GONE
        }
        viewModel.getPaymentSucceeded().observe(this) {
            if (it) {
                val data = Intent()
                data.putExtra(EXTRAS.SUCCESS, it)
                setResult(RESULT_OK, data)
                finish()
            }
        }
        unlockButton.setOnClickListener {
            viewModel.launchPurchase(this)
        }
        restoreButton.setOnClickListener {
            if (configuration.introMode) {
                finish()
            } else {
                viewModel.launchRestore()
            }
        }

        // Intro mode changes
        if (configuration.introMode) {
            restoreButton.setText(R.string.premium_no_thanks)
        }
    }

    override fun onResume() {
        super.onResume()

        // Tell the view model that the view has appeared
        viewModel.onAppear()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    inner class PremiumRecyclerViewAdapter(private val viewModel: UnlockPremiumViewModel) :
        RecyclerView.Adapter<PremiumRecyclerViewAdapter.PremiumViewHolder>() {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): PremiumRecyclerViewAdapter.PremiumViewHolder {
            val root = LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_unlock_premium_item, parent, false)
            return PremiumViewHolder(root)
        }

        override fun onBindViewHolder(
            holder: PremiumRecyclerViewAdapter.PremiumViewHolder,
            position: Int
        ) {
            holder.bind(viewModel.configuration.arguments[position])
        }

        override fun getItemCount(): Int {
            return viewModel.configuration.arguments.size
        }

        inner class PremiumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            private val premiumIcon: ImageView = itemView.findViewById(R.id.premium_icon)
            private val premiumTitle: TextView = itemView.findViewById(R.id.premium_title)
            private val premiumDescription: TextView =
                itemView.findViewById(R.id.premium_description)

            fun bind(arg: PremiumArgument) {
                premiumIcon.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this@UnlockPremiumActivity,
                        arg.icon
                    )
                )
                premiumTitle.text = arg.title
                premiumDescription.text = arg.description
            }

        }

    }

}
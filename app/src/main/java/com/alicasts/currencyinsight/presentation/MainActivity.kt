package com.alicasts.currencyinsight.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.alicasts.currencyinsight.databinding.ActivityMainBinding
import com.alicasts.currencyinsight.presentation.currency_pair_list.CurrencyPairListActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val sharedPreferences by lazy {
        getSharedPreferences("com.alicasts.currencyinsight", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupTheme()
        handleFirstRun()
        startCurrencyPairListActivity()
    }

    private fun setupTheme() {
        val isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode_enabled", false)
        val nightMode = if (isDarkModeEnabled) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    private fun handleFirstRun() {
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)
        if (isFirstRun) {
            sharedPreferences.edit().apply {
                putInt("days_to_fetch_data", 15)
                putBoolean("isFirstRun", false)
                apply()
            }
        }
    }

    private fun startCurrencyPairListActivity() {
        startActivity(Intent(this, CurrencyPairListActivity::class.java))
        finish()
    }
}
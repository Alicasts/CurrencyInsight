package com.alicasts.currencyinsight.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alicasts.currencyinsight.presentation.currency_pair_list.CurrencyPairListActivity
import com.alicasts.currencyinsight.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate( layoutInflater )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )

        val sharedPreferences = getSharedPreferences("com.alicasts.currencyinsight", Context.MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

        if (isFirstRun) {
            sharedPreferences.edit().apply {
                putInt("days_to_fetch_data", 15)
                putBoolean("isFirstRun", false)
                apply()
            }
        }

        startActivity(Intent(this, CurrencyPairListActivity::class.java))

        finish()
    }
}
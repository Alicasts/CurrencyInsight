package com.alicasts.currencyinsight.presentation

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

        startActivity(Intent(this, CurrencyPairListActivity::class.java))

        finish()
    }
}
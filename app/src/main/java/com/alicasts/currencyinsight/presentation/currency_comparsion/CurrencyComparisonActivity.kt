package com.alicasts.currencyinsight.presentation.currency_comparsion

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.alicasts.currencyinsight.databinding.ActivityCurrencyComparisonBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrencyComparisonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCurrencyComparisonBinding
    private val currencyComparisonViewModel: CurrencyComparisonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrencyComparisonBinding.inflate(layoutInflater)
        setContentView(binding.root)


        observeViewModel()
    }

    private fun observeViewModel() {
        currencyComparisonViewModel.state.observe(this) { state ->
            state?.let { handleState(it) }
        }
    }

    fun handleState(state: CurrencyComparisonState) {
        when {
            state.isLoading -> {
                setViewsVisibility(displayProgressBar = true)
            }
            state.error.isNotEmpty() -> {
                setViewsVisibility(displayErrorText = true)
                binding.errorTextView.show(state.error)
            }
            else -> {
                setViewsVisibility(displayChartAndDetails = true)
            }
        }
    }

    private fun setViewsVisibility(
        displayProgressBar: Boolean = false,
        displayChartAndDetails: Boolean = false,
        displayErrorText: Boolean = false
    ) {
        binding.progressBar.setVisibility(displayProgressBar)
        binding.errorTextView.setVisibility(displayErrorText)
    }

    private fun View.setVisibility(isVisible: Boolean) {
        this.visibility = when {
            isVisible -> View.VISIBLE
            else -> View.GONE
        }
    }

    private fun TextView.show(message: String) {
        this.text = message
        this.visibility = View.VISIBLE
    }
}
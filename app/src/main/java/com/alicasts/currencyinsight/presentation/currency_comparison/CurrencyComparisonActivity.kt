package com.alicasts.currencyinsight.presentation.currency_comparison

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.alicasts.currencyinsight.R
import com.alicasts.currencyinsight.databinding.ActivityCurrencyComparisonBinding
import com.alicasts.currencyinsight.domain.model.currency_comparison.CurrencyComparisonDetails
import com.alicasts.currencyinsight.presentation.currency_comparison.details_fragment.DetailsFragment
import com.alicasts.currencyinsight.presentation.currency_comparison.details_fragment.chart.BidChartComposable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrencyComparisonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCurrencyComparisonBinding
    private val currencyComparisonViewModel: CurrencyComparisonViewModel by viewModels()
    private var isUpdating = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrencyComparisonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeViewModel()
        initializeCards()
    }

    private fun observeViewModel() {
        currencyComparisonViewModel.state.observe(this) { state ->
            state?.let { handleState(it) }
        }
    }

    private fun initializeCards() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.cards_fragment_container, DetailsFragment())
            .commit()
    }

    fun handleState(state: CurrencyComparisonState) {
        when {
            state.isLoading -> {
                setViewsVisibility(displayProgressBar = true)
            }
            state.error.isNotEmpty() -> {
                setViewsVisibility(displayErrorText = true)
                binding.errorText.show(state.error)
            }
            else -> {
                setViewsVisibility(displayChartAndDetails = true)
                setFieldsValues(state.comparisonDetails)
                setupChart(state.comparisonDetails)
            }
        }
    }

    private fun setupChart(comparisonDetails: CurrencyComparisonDetails?) {
        val composeView = binding.composeView
        composeView.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                if (comparisonDetails != null) {
                    BidChartComposable(currencyComparisonDetails = comparisonDetails)
                }
            }
        }
    }

    private fun setFieldsValues(comparisonDetails: CurrencyComparisonDetails?) {
        comparisonDetails?.let {
            val (baseCurrencyName, targetCurrencyName) =
                extractCurrencyPair(comparisonDetails.name)

            binding.baseCurrencyLayout.currencyValueTextView.setText(it.bid)
            binding.targetCurrencyLayout.currencyNameTextView.text = baseCurrencyName
            binding.baseCurrencyLayout.currencyNameTextView.text =targetCurrencyName

            binding.targetCurrencyLayout.currencyValueTextView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(inputText: CharSequence?, start: Int, before: Int, count: Int) {
                    if (isUpdating) return
                    isUpdating = true
                    val targetValue = inputText.toString().toDoubleOrNull() ?: 1.0

                    val baseValue = currencyComparisonViewModel.calculateBaseCurrencyValue(targetValue, it.bid)
                    binding.baseCurrencyLayout.currencyValueTextView.setText(baseValue)
                    isUpdating = false
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            binding.baseCurrencyLayout.currencyValueTextView.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (isUpdating) return
                    isUpdating = true
                    val baseValue = s.toString().toDoubleOrNull() ?: 0.0


                    val targetValue = currencyComparisonViewModel.calculateTargetCurrencyValue(baseValue, it.bid)
                    binding.targetCurrencyLayout.currencyValueTextView.setText(targetValue)
                    isUpdating = false
                }

                override fun afterTextChanged(s: Editable?) {}
            })

        }
    }

    private fun extractCurrencyPair(name: String): Pair<String, String> {
        val currencies = name.split("/")
        return Pair(currencies[0], currencies[1])
    }

    private fun setViewsVisibility(
        displayProgressBar: Boolean = false,
        displayChartAndDetails: Boolean = false,
        displayErrorText: Boolean = false
    ) {
        binding.progressBar.setVisibility(displayProgressBar)
        binding.errorText.setVisibility(displayErrorText)
        binding.cardsFragmentContainer.setVisibility(displayChartAndDetails)
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
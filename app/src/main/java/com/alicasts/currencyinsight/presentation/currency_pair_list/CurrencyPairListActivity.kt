package com.alicasts.currencyinsight.presentation.currency_pair_list

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alicasts.currencyinsight.domain.model.CurrencyPairListItemModel
import com.alicasts.currencyinsight.databinding.ActivityCurrencyPairListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrencyPairListActivity : AppCompatActivity() {

    private val currencyPairListViewModel: CurrencyPairListViewModel by viewModels()

    private val binding by lazy {
        ActivityCurrencyPairListBinding.inflate( layoutInflater )
    }

    private lateinit var adapter: CurrencyPairListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )

        setupRecyclerView()

        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = CurrencyPairListAdapter(emptyList())
        binding.currencyPairsListRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.currencyPairsListRecyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        currencyPairListViewModel.state.observe(this) { state ->
            state?.let { handleState(it) }
        }
    }

    fun handleState(state: CurrencyPairListState) {
        when {
            state.isLoading -> {
                setViewVisibility(displayProgressBar = true)
            }
            state.error.isNotEmpty() -> {
                setViewVisibility(displayErrorText = true)
                binding.errorTextView.show(state.error)
            }
            else -> {
                setViewVisibility(displayCurrencyPairsList = true)
                updateRecyclerView(state.currencyPairList)
            }
        }
    }

    private fun setViewVisibility(
        displayProgressBar: Boolean = false,
        displayCurrencyPairsList: Boolean = false,
        displayErrorText: Boolean = false
    ) {
        binding.progressBar.setVisibility(displayProgressBar)
        binding.currencyPairsListRecyclerView.setVisibility(displayCurrencyPairsList)
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

    private fun updateRecyclerView(currencyList: List<CurrencyPairListItemModel>) {
        adapter = CurrencyPairListAdapter(currencyList)
        binding.currencyPairsListRecyclerView.adapter = adapter
    }
}
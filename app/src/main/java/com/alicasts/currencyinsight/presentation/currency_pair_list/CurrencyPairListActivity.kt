package com.alicasts.currencyinsight.presentation.currency_pair_list

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alicasts.currencyinsight.databinding.ActivityCurrencyPairListBinding
import com.alicasts.currencyinsight.presentation.currency_comparsion.CurrencyComparisonActivity
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

        setupSearchView()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupSearchView() {
        val searchView = binding.searchView

        searchView.setOnClickListener {
            if (searchView.isIconified) {
                searchView.isIconified = false
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    currencyPairListViewModel.filterCurrencyPairList(it)
                }
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                query?.let {
                    if (it.length >= 3) {
                        currencyPairListViewModel.filterCurrencyPairList(it)
                    } else if (it.isEmpty()) {
                        currencyPairListViewModel.filterCurrencyPairList("")
                    }
                }
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = CurrencyPairListAdapter(emptyList()) { currencyPairId ->
            val intent = Intent(this, CurrencyComparisonActivity::class.java).apply {
                putExtra("currency_pair_id", currencyPairId)
            }
            startActivity(intent)
        }
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
                setViewsVisibility(displayProgressBar = true)
            }
            state.error.isNotEmpty() -> {
                setViewsVisibility(displayErrorText = true)
                binding.errorTextView.show(state.error)
            }
            else -> {
                setViewsVisibility(displayCurrencyPairsList = true)
                adapter.updateCurrencyPairList(state.currencyPairList)
            }
        }
    }

    private fun setViewsVisibility(
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
}
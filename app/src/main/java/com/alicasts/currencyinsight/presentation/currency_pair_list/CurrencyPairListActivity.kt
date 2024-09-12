package com.alicasts.currencyinsight.presentation.currency_pair_list

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alicasts.currencyinsight.R
import com.alicasts.currencyinsight.databinding.ActivityCurrencyPairListBinding
import com.alicasts.currencyinsight.presentation.currency_comparison.CurrencyComparisonActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CurrencyPairListActivity : AppCompatActivity() {

    private val currencyPairListViewModel: CurrencyPairListViewModel by viewModels()
    private var isMenuFabExpanded = false

    private val binding by lazy {
        ActivityCurrencyPairListBinding.inflate( layoutInflater )
    }

    private lateinit var adapter: CurrencyPairListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView( binding.root )

        setupSearchView()
        setupRecyclerView()
        setupFloatingActionButtonMenu()
        observeViewModel()

    }

    private fun setupFloatingActionButtonMenu() {
        val menuFloatingActionButton = binding.floatingActionButton
        val buttonsContainer = binding.buttonsContainer
        menuFloatingActionButton.setOnClickListener {

            if (isMenuFabExpanded) {
                collapseButtonContainer(menuFloatingActionButton, buttonsContainer)
            } else {
                expandButtonContainer(menuFloatingActionButton, buttonsContainer)
            }
            isMenuFabExpanded = !isMenuFabExpanded
        }
    }

    private fun expandButtonContainer(floatingActionButton: FloatingActionButton, buttonsContainer: LinearLayout) {
        changeFabIconWithAnimation(floatingActionButton,R.drawable.ic_down_arrow)
        buttonsContainer.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(null)
        }
    }

    private fun collapseButtonContainer(floatingActionButton: FloatingActionButton, buttonsContainer: LinearLayout) {
        changeFabIconWithAnimation(floatingActionButton,R.drawable.ic_menu)
        buttonsContainer.animate()
            .alpha(0f)
            .setDuration(500)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    buttonsContainer.visibility = View.GONE
                }
            })
    }

    private fun changeFabIconWithAnimation(floatingActionButton: FloatingActionButton, newIconRes: Int) {
        val fadeOutAnimator = ObjectAnimator.ofFloat(floatingActionButton.drawable, buildString { append("alpha") }, 1f, 0f)

        fadeOutAnimator.duration = 450

        fadeOutAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                floatingActionButton.setImageResource(newIconRes)

                val fadeInAnimator = ObjectAnimator.ofFloat(
                    floatingActionButton.drawable,
                    buildString { append("alpha") },
                    0f,
                    1f
                )
                fadeInAnimator.duration = 450
                fadeInAnimator.start()
            }
        })
        fadeOutAnimator.start()
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
                    if (it.isNotEmpty()) {
                        currencyPairListViewModel.filterCurrencyPairList(it)
                    } else {
                        currencyPairListViewModel.filterCurrencyPairList("")
                    }
                }
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = CurrencyPairListAdapter(mutableListOf()) { currencyPairId ->
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
                binding.errorText.show(state.error)
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
        binding.errorText.setVisibility(displayErrorText)
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
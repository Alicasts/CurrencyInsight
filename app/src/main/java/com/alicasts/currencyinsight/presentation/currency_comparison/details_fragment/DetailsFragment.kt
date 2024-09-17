package com.alicasts.currencyinsight.presentation.currency_comparison.details_fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.alicasts.currencyinsight.databinding.FragmentDetailsBinding
import com.alicasts.currencyinsight.presentation.currency_comparison.CurrencyComparisonViewModel

class DetailsFragment : Fragment() {

    lateinit var binding: FragmentDetailsBinding
    private val currencyComparisonViewModel: CurrencyComparisonViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeViewModel()
    }

    private fun observeViewModel() {
        currencyComparisonViewModel.state.observe(viewLifecycleOwner) { state ->
            state.comparisonDetails?.let { details ->

                val todayCard = generateCard(
                    details.timestamp.toLong(),
                    details.pctChange,
                    details.bid,
                    details.ask,
                    details.varBid,
                    details.high,
                    details.low
                )

                val historicalCards = details.historicalData.map { data ->
                    generateCard(
                        data.timestamp.toLong(),
                        data.pctChange,
                        data.bid,
                        data.ask,
                        data.varBid,
                        data.high,
                        data.low
                    )
                }

                val cards = listOf(todayCard) + historicalCards
                binding.horizontalRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = CardAdapter(cards)
                }

            }
        }
    }

    fun generateCard(
        timestamp: Long,
        pctChange: String,
        bid: String,
        ask: String,
        varBid: String,
        high: String,
        low: String
    ): List<Pair<String, String>> {
        return listOf(
            "Date(DD/MM):" to timestamp.toFormattedDate(),
            "Pct Change:" to pctChange,
            "Bid:" to bid,
            "Ask:" to ask,
            "Var Bid:" to varBid,
            "High:" to high,
            "Low:" to low
        )
    }

    private fun Long.toFormattedDate(): String {
        val sdf = java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault())
        val date = java.util.Date(this * 1000)
        return sdf.format(date)
    }
}
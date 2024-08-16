package com.alicasts.currencyinsight.presentation.currency_pair_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alicasts.currencyinsight.domain.model.CurrencyPairListItemModel
import com.alicasts.currencyinsight.databinding.ItemCurrencyPairBinding

class CurrencyPairListAdapter(
    private val currencyPairList: List<CurrencyPairListItemModel>
) : RecyclerView.Adapter<CurrencyPairListAdapter.CurrencyPairViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyPairViewHolder {
        val binding = ItemCurrencyPairBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CurrencyPairViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CurrencyPairViewHolder, position: Int) {
        holder.bind(currencyPairList[position])
    }

    override fun getItemCount(): Int = currencyPairList.size

    inner class CurrencyPairViewHolder(binding: ItemCurrencyPairBinding) : RecyclerView.ViewHolder(binding.root) {

        val binding by lazy {
            ItemCurrencyPairBinding.bind(itemView)
        }

        fun bind(currencyPair: CurrencyPairListItemModel) {
            binding.currencyPairAbbreviationsTextView.text = currencyPair.currencyPairAbbreviations
            binding.currencyPairFullNamesTextView.text = currencyPair.currencyPairFullNames
        }
    }
}
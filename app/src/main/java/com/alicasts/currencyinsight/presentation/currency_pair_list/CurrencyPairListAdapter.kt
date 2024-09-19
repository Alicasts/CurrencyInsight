package com.alicasts.currencyinsight.presentation.currency_pair_list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alicasts.currencyinsight.databinding.ItemListCurrencyPairBinding
import com.alicasts.currencyinsight.domain.model.currency_pair_list.CurrencyPairListItemModel

class CurrencyPairListAdapter(
    private var currencyPairList: MutableList<CurrencyPairListItemModel>,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<CurrencyPairListAdapter.CurrencyPairViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyPairViewHolder {
        val binding = ItemListCurrencyPairBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CurrencyPairViewHolder(binding, onItemClicked)
    }

    override fun onBindViewHolder(holder: CurrencyPairViewHolder, position: Int) {
        holder.bind(currencyPairList[position])
    }

    override fun getItemCount(): Int = currencyPairList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateCurrencyPairList(newList: List<CurrencyPairListItemModel>) {
        currencyPairList.clear()
        currencyPairList.addAll(newList)
        notifyDataSetChanged()
    }

    class CurrencyPairViewHolder(
        private val binding: ItemListCurrencyPairBinding,
        private val onItemClicked: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(currencyPair: CurrencyPairListItemModel) {
            binding.currencyPairAbbreviationsText.text = currencyPair.currencyPairAbbreviations
            binding.currencyPairFullNamesText.text = currencyPair.currencyPairFullNames

            binding.root.setOnClickListener {
                onItemClicked(currencyPair.currencyPairAbbreviations)
            }
        }
    }
}
package com.alicasts.currencyinsight.presentation.currency_pair_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alicasts.currencyinsight.domain.model.currency_pair_list.CurrencyPairListItemModel
import com.alicasts.currencyinsight.databinding.ItemCurrencyPairBinding

class CurrencyPairListAdapter(
    private var currencyPairList: List<CurrencyPairListItemModel>,
    private val onItemClicked: (String) -> Unit
) : RecyclerView.Adapter<CurrencyPairListAdapter.CurrencyPairViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyPairViewHolder {
        val binding = ItemCurrencyPairBinding.inflate(
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

    fun updateCurrencyPairList(newList: List<CurrencyPairListItemModel>) {
        val oldListSize = currencyPairList.size
        currencyPairList = newList
        val newListSize = currencyPairList.size

        when {
            newListSize > oldListSize -> {
                notifyItemRangeInserted(oldListSize, newListSize - oldListSize)
            }
            newListSize < oldListSize -> {
                notifyItemRangeRemoved(newListSize, oldListSize - newListSize)
            }
            else -> {
                notifyItemRangeChanged(0, newListSize)
            }
        }
    }

    class CurrencyPairViewHolder(
        private val binding: ItemCurrencyPairBinding,
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
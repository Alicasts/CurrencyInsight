package com.alicasts.currencyinsight.presentation.currency_pair_list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alicasts.currencyinsight.domain.model.CurrencyPairListItemModel
import com.example.currencyinsight.R

class CurrencyPairListAdapter(
    private val currencyPairList: List<CurrencyPairListItemModel>
) : RecyclerView.Adapter<CurrencyPairListAdapter.CurrencyPairViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyPairViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_currency_pair, parent, false)

        return CurrencyPairViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CurrencyPairViewHolder, position: Int) {
        val currentItem = currencyPairList[position]

        holder.currencyPairAbbreviationsTextView.text =
            currentItem.currencyPairAbbreviations

        holder.currencyPairFullNamesTextView.text =
            currentItem.currencyPairFullNames
    }

    override fun getItemCount() = currencyPairList.size

    class CurrencyPairViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val currencyPairAbbreviationsTextView: TextView =
            itemView.findViewById(R.id.currencyPairAbbreviationsTextView)

        val currencyPairFullNamesTextView: TextView =
            itemView.findViewById(R.id.currencyPairFullNamesTextView)
    }
}
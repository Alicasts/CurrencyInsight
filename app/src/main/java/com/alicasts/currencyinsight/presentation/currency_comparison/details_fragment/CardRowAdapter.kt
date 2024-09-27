package com.alicasts.currencyinsight.presentation.currency_comparison.details_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.alicasts.currencyinsight.databinding.ItemCardRowBinding

class CardRowAdapter(private val rows: List<Pair<String, String>>) :
    RecyclerView.Adapter<CardRowAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCardRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val row = rows[position]
        holder.binding.labelTextView.text = row.first
        holder.binding.valueTextView.text = row.second
    }

    override fun getItemCount(): Int = rows.size
}
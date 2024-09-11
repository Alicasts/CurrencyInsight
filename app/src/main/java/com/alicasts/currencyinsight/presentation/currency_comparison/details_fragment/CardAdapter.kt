package com.alicasts.currencyinsight.presentation.currency_comparison.details_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alicasts.currencyinsight.databinding.ItemCardBinding

class CardAdapter(private val cards: List<List<Pair<String, String>>>) :
    RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cardRows = cards[position]

        holder.binding.verticalRecyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.binding.verticalRecyclerView.adapter = CardRowAdapter(cardRows)
    }

    override fun getItemCount(): Int = cards.size
}
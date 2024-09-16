package com.alicasts.currencyinsight.presentation.currency_pair_list.fragments

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.alicasts.currencyinsight.R
import com.alicasts.currencyinsight.databinding.FragmentSettingsBinding

class SettingsDialogFragment : DialogFragment() {

    private lateinit var sharedPreferences: SharedPreferences

    private val binding by lazy {
        FragmentSettingsBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_background)

        isCancelable = false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRadioGroupButtons()

        binding.closeSettingsButton.setOnClickListener {
            dismiss()
        }
    }

    private fun setupRadioGroupButtons() {
        sharedPreferences = requireContext().getSharedPreferences("com.alicasts.currencyinsight", Context.MODE_PRIVATE)

        val daysToFetchNumber = sharedPreferences.getInt("days_to_fetch_data", 15)

        when (daysToFetchNumber) {
            15 -> binding.radioButton15.isChecked = true
            30 -> binding.radioButton30.isChecked = true
            90 -> binding.radioButton90.isChecked = true
        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val editor = sharedPreferences.edit()

            when (checkedId) {
                R.id.radioButton15 -> editor.putInt("days_to_fetch_data", 15)
                R.id.radioButton30 -> editor.putInt("days_to_fetch_data", 30)
                R.id.radioButton90 -> editor.putInt("days_to_fetch_data", 90)
            }

            editor.apply()
        }
    }
}
package com.edustack.edustack.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edustack.edustack.databinding.ItemSettingsOptionBinding

data class SettingsOption(
    val title: String,
    val subtitle: String = "",
    val icon: Int,
    val action: String
)

class SettingsAdapter(
    private val optionsList: List<SettingsOption>,
    private val onOptionClick: (SettingsOption) -> Unit
) : RecyclerView.Adapter<SettingsAdapter.SettingsViewHolder>() {

    class SettingsViewHolder(
        private val binding: ItemSettingsOptionBinding,
        private val onOptionClick: (SettingsOption) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(option: SettingsOption) {
            binding.optionTitle.text = option.title
            binding.optionSubtitle.text = option.subtitle
            binding.optionIcon.setImageResource(option.icon)

            // Set click listener
            binding.root.setOnClickListener {
                onOptionClick(option)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettingsViewHolder {
        val binding = ItemSettingsOptionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SettingsViewHolder(binding, onOptionClick)
    }

    override fun onBindViewHolder(holder: SettingsViewHolder, position: Int) {
        holder.bind(optionsList[position])
    }

    override fun getItemCount(): Int = optionsList.size
}
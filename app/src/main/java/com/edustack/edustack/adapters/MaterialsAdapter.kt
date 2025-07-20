package com.edustack.edustack.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.edustack.edustack.databinding.ItemMaterialBinding
import com.edustack.edustack.model.CourseMaterial
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MaterialsAdapter(
    private val materialsList: List<CourseMaterial>,
    private val onMaterialClick: (CourseMaterial) -> Unit
) : RecyclerView.Adapter<MaterialsAdapter.MaterialsViewHolder>() {

    class MaterialsViewHolder(
        private val binding: ItemMaterialBinding,
        private val onMaterialClick: (CourseMaterial) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(material: CourseMaterial) {
            binding.materialTitle.text = material.title
            binding.materialDate.text = formatDate(material.date)
            binding.materialType.text = material.fileType

            // Set file type icon based on file type
            when (material.fileType.uppercase()) {
                "PDF" -> binding.materialIcon.setImageResource(android.R.drawable.ic_menu_view)
                "DOCX" -> binding.materialIcon.setImageResource(android.R.drawable.ic_menu_edit)
                "ZIP" -> binding.materialIcon.setImageResource(android.R.drawable.ic_menu_upload)
                else -> binding.materialIcon.setImageResource(android.R.drawable.ic_menu_agenda)
            }

            // Set click listener
            binding.root.setOnClickListener {
                onMaterialClick(material)
            }
        }

        private fun formatDate(timestamp: Long): String {
            return try {
                val date = Date(timestamp)
                val format = SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
                format.format(date)
            } catch (e: Exception) {
                "Unknown Date"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialsViewHolder {
        val binding = ItemMaterialBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return MaterialsViewHolder(binding, onMaterialClick)
    }

    override fun onBindViewHolder(holder: MaterialsViewHolder, position: Int) {
        holder.bind(materialsList[position])
    }

    override fun getItemCount(): Int = materialsList.size
}
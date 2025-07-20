package com.edustack.edustack.ui.Teacher.Results

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edustack.edustack.R

class MaterialsAdapter(
    private val onDownloadClick: (String) -> Unit
) : RecyclerView.Adapter<MaterialsAdapter.MaterialViewHolder>() {

    private var materials = listOf<String>()

    fun updateMaterials(newMaterials: List<String>) {
        materials = newMaterials
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaterialViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_material, parent, false)
        return MaterialViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaterialViewHolder, position: Int) {
        holder.bind(materials[position], position + 1)
    }

    override fun getItemCount() = materials.size

    inner class MaterialViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textMaterialName: TextView = itemView.findViewById(R.id.textMaterialName)
        private val buttonDownload: Button = itemView.findViewById(R.id.buttonDownload)

        fun bind(materialUrl: String, position: Int) {
            textMaterialName.text = "Submission Material $position"

            buttonDownload.setOnClickListener {
                onDownloadClick(materialUrl)
            }
        }
    }
}

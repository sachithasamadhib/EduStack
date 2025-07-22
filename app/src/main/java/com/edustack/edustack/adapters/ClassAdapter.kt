package com.edustack.edustack.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.edustack.edustack.R
import com.edustack.edustack.model.ClassItem

class ClassAdapter(private var classList: List<ClassItem>,
                   private val onItemClick: (ClassItem) -> Unit) :
    RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

    inner class ClassViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val txtClassName: TextView = itemView.findViewById(R.id.txtClassName)
//        val txtDescription: TextView = itemView.findViewById(R.id.txtDescription)
//        val txtTime: TextView = itemView.findViewById(R.id.txtTime)
//        val txtWeekDay: TextView = itemView.findViewById(R.id.txtWeekDay)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_class, parent, false)
        return ClassViewHolder(view)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        val currentItem = classList[position]
//        holder.txtClassName.text = currentItem.name
//        holder.txtDescription.text = currentItem.description
//        holder.txtTime.text = "${currentItem.startTime} - ${currentItem.endTime}"
//        holder.txtWeekDay.text = currentItem.weekDay

        holder.itemView.setOnClickListener {
            onItemClick(currentItem)
        }
    }

    override fun getItemCount(): Int = classList.size

    fun updateData(newList: List<ClassItem>) {
        classList = newList
        notifyDataSetChanged()
    }
}

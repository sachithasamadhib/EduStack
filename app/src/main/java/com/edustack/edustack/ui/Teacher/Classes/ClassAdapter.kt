package com.edustack.edustack.ui.Teacher.Classes

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.edustack.edustack.R
import com.edustack.edustack.databinding.ItemClassBinding

class ClassAdapter(
    private val onFilesClick: (ClassItem) -> Unit,
    private val onCancelClick: (ClassItem) -> Unit
) : ListAdapter<ClassItem, ClassAdapter.ClassViewHolder>(ClassDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val binding = ItemClassBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ClassViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ClassViewHolder(
        private val binding: ItemClassBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(classItem: ClassItem) {
            binding.apply {
                textClassName.text = classItem.name
                textStudentCount.text = "${classItem.studentCount} Students"
                textClassTime.text = "${classItem.startTime} - ${classItem.endTime}"
                textHallInfo.text = "Room ${classItem.hallId}"

                when {
                    classItem.isCancelled -> {
                        chipStatus.text = "Cancelled"
                        setChipBackground(chipStatus, R.color.red_500)
                    }
                    classItem.isActive -> {
                        chipStatus.text = "Active"
                        setChipBackground(chipStatus, R.color.green_500)
                    }
                    else -> {
                        chipStatus.text = "Inactive"
                        setChipBackground(chipStatus, R.color.gray_500)
                    }
                }

                textClassInitial.text = classItem.name.firstOrNull()?.toString()?.uppercase() ?: "C"

                buttonFiles.setOnClickListener {
                    onFilesClick(classItem)
                }

                buttonCancel.setOnClickListener {
                    onCancelClick(classItem)
                }

                buttonCancel.isEnabled = !classItem.isCancelled
                buttonCancel.alpha = if (classItem.isCancelled) 0.5f else 1.0f
            }
        }

        private fun setChipBackground(textView: android.widget.TextView, colorRes: Int) {
            val drawable = GradientDrawable()
            drawable.shape = GradientDrawable.RECTANGLE
            drawable.cornerRadius = 24f
            drawable.setColor(ContextCompat.getColor(textView.context, colorRes))
            textView.background = drawable
        }
    }
}

class ClassDiffCallback : DiffUtil.ItemCallback<ClassItem>() {
    override fun areItemsTheSame(oldItem: ClassItem, newItem: ClassItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ClassItem, newItem: ClassItem): Boolean {
        return oldItem == newItem
    }
}
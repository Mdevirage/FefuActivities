package ru.fefu.activitiesfefu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.core.content.ContextCompat

class ActivityTypeAdapter(
    private val activityTypes: List<String>,
    private val onTypeClick: (String) -> Unit
) : RecyclerView.Adapter<ActivityTypeAdapter.TypeViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_activity_type, parent, false)
        return TypeViewHolder(view, onTypeClick)
    }

    override fun onBindViewHolder(holder: TypeViewHolder, position: Int) {
        holder.bind(activityTypes[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = activityTypes.size

    fun setSelected(position: Int) {
        if (selectedPosition != position) {
            notifyItemChanged(selectedPosition)
            selectedPosition = position
            notifyItemChanged(selectedPosition)
        }
    }

    class TypeViewHolder(itemView: View, val onClick: (String) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val typeText: TextView = itemView.findViewById(R.id.activity_type_text)
        private var currentType: String? = null

        init {
            itemView.setOnClickListener {
                currentType?.let { onClick(it) }
            }
        }

        fun bind(type: String, isSelected: Boolean) {
            currentType = type  // <-- добавьте эту строку
            typeText.text = type  // <-- используйте typeText, а не textView
            if (isSelected) {
                itemView.setBackgroundResource(R.drawable.rounded_corner_background_selected)
            } else {
                itemView.setBackgroundResource(R.drawable.rounded_corner_background)
            }
        }
    }
} 
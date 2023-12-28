package com.example.customapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate

class RowAdapter(private val data: List<LocalDate>
                 , private val listener: (LocalDate) -> Unit
) : RecyclerView.Adapter<RowAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RowAdapter.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        // Apply layout for ViewHolder
        val view = layoutInflater.inflate(R.layout.layout_row, parent, false) as View
        return ViewHolder(view)
    }

    override fun getItemCount() = data.size

    // Bind data
    override fun onBindViewHolder(holder: RowAdapter.ViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    inner class ViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        private val textDay = v.findViewById<TextView>(R.id.text_day)
        private val textDate= v.findViewById<TextView>(R.id.text_date)
        private val textMonth= v.findViewById<TextView>(R.id.text_month)

        fun bind(item: LocalDate) {
            textDay.text = item.dayOfWeek.toString().take(3)
            textDate.text = item.dayOfMonth.toString()
            textMonth.text = item.month.toString().take(3)
            v.setOnClickListener {
                listener(item)
            }
        }
    }
}
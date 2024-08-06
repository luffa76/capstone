package com.example.tutle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AlarmAdapter(
    private val alarmList: List<AlarmData>,
    private val onItemClick: (AlarmData) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    class AlarmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val alarmTextView: TextView = view.findViewById(R.id.alarmTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_item, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarmList[position]
        holder.alarmTextView.text = "${alarm.description} (${String.format("%02d:%02d", alarm.hour, alarm.minute)})"
        holder.itemView.setOnClickListener {
            onItemClick(alarm)
        }
    }

    override fun getItemCount(): Int {
        return alarmList.size
    }
}

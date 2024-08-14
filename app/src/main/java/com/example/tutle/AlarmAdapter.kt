package com.example.tutle

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//class AlarmAdapter(
//    private val alarmList: List<AlarmData>,
//    private val onItemClick: (AlarmData) -> Unit
//) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {
//
//    class AlarmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val alarmTextView: TextView = view.findViewById(R.id.alarmTextView)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_item, parent, false)
//        return AlarmViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
//        val alarm = alarmList[position]
//        holder.alarmTextView.text = "${alarm.description} (${String.format("%02d:%02d", alarm.hour, alarm.minute)})"
//        holder.itemView.setOnClickListener {
//            onItemClick(alarm)
//        }
//    }
//
//    override fun getItemCount(): Int {
//        return alarmList.size
//    }
//}
class AlarmAdapter(
    private val alarms: List<AlarmData>,
    private val onItemClick: (AlarmData) -> Unit,
    private val onDeleteClick: (AlarmData) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    inner class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvCardTitle)
        private val timeTextView: TextView = itemView.findViewById(R.id.tvCardTime)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(alarm: AlarmData) {
            titleTextView.text = alarm.description
            timeTextView.text = String.format("%02d:%02d", alarm.hour, alarm.minute)

            // 아이템 클릭 시 수정 다이얼로그 표시
            itemView.setOnClickListener {
                onItemClick(alarm)
            }

            // 삭제 버튼 클릭 시 삭제 기능 수행
            deleteButton.setOnClickListener {
                onDeleteClick(alarm)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_alarm_item, parent, false)  // 새로 생성한 레이아웃으로 변경
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(alarms[position])
    }

    override fun getItemCount(): Int = alarms.size
}
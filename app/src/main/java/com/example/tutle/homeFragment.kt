package com.example.tutle

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tutle.AlarmData
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import kotlin.concurrent.thread

class homeFragment : Fragment() {

    companion object {
        const val CHANNEL_ID = "my_channel_id"
        private const val NOTIFICATION_ID = 1
        private const val REQUEST_CODE = 101
    }

    private lateinit var btnSetAlarm: Button
    private lateinit var alarmRecyclerView: RecyclerView
    private lateinit var alarmAdapter: AlarmAdapter
    private val alarmList = mutableListOf<AlarmData>()

    private lateinit var auth: FirebaseAuth
    private lateinit var db: AppDatabase
    private lateinit var userEmailTextView: TextView

    // 1. 알림 채널을 생성하는 함수
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
    }

    // 2. 알림 채널을 생성하는 함수
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "My Channel"
            val descriptionText = "This is my channel description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableVibration(true)
            }
            val notificationManager: NotificationManager? = context?.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    // 3. 알림을 예약하는 함수
    private fun scheduleNotification(calendar: Calendar, description: String) {
        val context = context ?: return

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("notification_id", NOTIFICATION_ID)
            putExtra("channel_id", CHANNEL_ID)
            putExtra("description", description)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        Toast.makeText(context, "알림이 예약되었습니다.", Toast.LENGTH_SHORT).show()
    }

    // 4. 알람 설정을 위한 다이얼로그를 표시하는 함수
    private fun showAlarmDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_set_alarm, null)
        val descriptionEditText: EditText = dialogView.findViewById(R.id.descriptionEditText)

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                val description = descriptionEditText.text.toString()
                setAlarm(selectedHour, selectedMinute, description)
            },
            hour,
            minute,
            false
        )

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Set Alarm")
            .setView(dialogView)
            .setPositiveButton("Set") { _, _ ->
                timePickerDialog.show()
            }
            .setNegativeButton("Cancel", null)
            .create()

        alertDialog.show()
    }

    // 5. 알람 수정 다이얼로그를 표시하는 함수
    private fun showEditAlarmDialog(alarmData: AlarmData) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_set_alarm, null)
        val descriptionEditText: EditText = dialogView.findViewById(R.id.descriptionEditText)
        descriptionEditText.setText(alarmData.description)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val description = descriptionEditText.text.toString()
                editAlarm(alarmData, selectedHour, selectedMinute, description)
            },
            alarmData.hour,
            alarmData.minute,
            false
        )

        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Edit Alarm")
            .setView(dialogView)
            .setPositiveButton("Set") { _, _ ->
                timePickerDialog.show()
            }
            .setNegativeButton("Cancel", null)
            .create()

        alertDialog.show()
    }
    // 6. 알람을 설정하는 함수
    private fun setAlarm(hour: Int, minute: Int, description: String) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(requireContext(), AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(requireContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            alarmIntent
        )
        Toast.makeText(requireContext(), "Alarm is set", Toast.LENGTH_SHORT).show()

//        alarmList.add(AlarmData(hour, minute, description))
//        alarmAdapter.notifyDataSetChanged()
        val newAlarm = AlarmData(hour = hour, minute = minute, description = description)
        alarmList.add(newAlarm)
        alarmAdapter.notifyDataSetChanged()

        // 알람을 데이터베이스에 저장
        thread {
            db.alarmDao().insertAlarm(newAlarm)
        }
        // 알림 예약
        scheduleNotification(calendar, description)
    }
    // 7. 알람을 수정하는 함수
    private fun editAlarm(oldAlarmData: AlarmData, newHour: Int, newMinute: Int, newDescription: String) {
        val index = alarmList.indexOf(oldAlarmData)
        if (index != -1) {
//            alarmList[index] = AlarmData(newHour, newMinute, newDescription)
//            alarmAdapter.notifyDataSetChanged()
            val updatedAlarm = oldAlarmData.copy(hour = newHour, minute = newMinute, description = newDescription)
            alarmList[index] = updatedAlarm
            alarmAdapter.notifyDataSetChanged()

            // 데이터베이스에서 알람 수정
            thread {
                db.alarmDao().updateAlarm(updatedAlarm)
            }
        }
    }
    // 8. 뷰를 생성하고 초기화하는 함수
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        btnSetAlarm = view.findViewById(R.id.btn_alarmset)
        alarmRecyclerView = view.findViewById(R.id.alarmListView)
        userEmailTextView = view.findViewById(R.id.userid)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db = AppDatabase.getDatabase(requireContext(), userId)
        } else {
            // 로그인 상태가 아니면 처리하는 코드
        }

        alarmAdapter = AlarmAdapter(alarmList) { alarmData ->
            showEditAlarmDialog(alarmData)
        }
        alarmRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        alarmRecyclerView.adapter = alarmAdapter

        btnSetAlarm.setOnClickListener {
            showAlarmDialog()
        }

        userEmailTextView.text = auth.currentUser?.email ?: "사용자 이메일 없음"
        userEmailTextView.setOnClickListener {
            showNicknameDialog()
        }
        loadAlarms()

        loadNickname()

        return view
    }
    // 9. 데이터베이스에서 알람을 불러오는 함수
    private fun loadAlarms() {
        thread {
            val alarms = db.alarmDao().getAllAlarms()
            alarmList.clear()
            alarmList.addAll(alarms)
            activity?.runOnUiThread {
                alarmAdapter.notifyDataSetChanged()
            }
        }
    }

    // 10. 권한 요청 결과를 처리하는 함수
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용됨
                // 필요에 따라 알림을 보내는 함수 호출
            } else {
                // 권한이 거부됨
                Toast.makeText(context, "알림 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }



    /////// 닉네임 ///////
    private fun showNicknameDialog() {
        //val editText = EditText(requireContext())
        val editText = EditText(requireContext()).apply {
            hint = "닉네임을 입력하세요"
            inputType = InputType.TYPE_CLASS_TEXT // 텍스트 입력 타입 설정
        }
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("닉네임 입력")
            .setView(editText)
            .setPositiveButton("저장") { dialog, _ ->
                val nickname = editText.text.toString()
                saveNickname(nickname)
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun saveNickname(nickname: String) {
        thread {
            // 기존 닉네임 삭제
            db.nicknameDao().deleteAllNicknames()

            // 새로운 닉네임 추가
            val nicknameEntity = Nickname(nickname = nickname)
            db.nicknameDao().insertNickname(nicknameEntity)

            loadNickname()
        }
    }

    private fun loadNickname() {
        thread {
            val nickname = db.nicknameDao().getNickname()
            activity?.runOnUiThread {
                userEmailTextView.text = nickname?.nickname ?: "닉네임 없음"
            }
        }
    }
}

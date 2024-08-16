package com.example.tutle

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tutle.AlarmData
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import kotlin.concurrent.thread
import com.example.tutle.AlarmDao
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import de.hdodenhof.circleimageview.CircleImageView

class homeFragment : Fragment() {

    companion object {
        const val CHANNEL_ID = "my_channel_id"
        private const val NOTIFICATION_ID = 1
        private const val REQUEST_CODE = 101
        private const val REQUEST_CODE_GALLERY = 102 // 갤러리 접근 권한 요청 코드 추가
        private const val REQUEST_CODE_PERMISSION = 1000
        //private const val REQUEST_CODE_GALLERY = 2000
        const val REQUEST_CODE_SELECT_IMAGE = 2000
    }

    private lateinit var btnSetAlarm: Button
    private lateinit var alarmRecyclerView: RecyclerView
    private lateinit var alarmAdapter: AlarmAdapter
    private val alarmList = mutableListOf<AlarmData>()

    private lateinit var auth: FirebaseAuth
    private lateinit var db: AppDatabase
    private lateinit var userEmailTextView: TextView
    private lateinit var profileImageView: ImageView // 프로필 이미지뷰 추가///circler뷰 아닌가?

    private lateinit var ivProfile: CircleImageView

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
            android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
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
        profileImageView = view.findViewById(R.id.imageView) // 프로필 이미지뷰 초기화
// 프로필 이미지뷰 클릭 리스너 추가
        profileImageView.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestGalleryPermissionForAndroid13()
            } else {
                requestGalleryPermissionForBelowAndroid13()
            }
        }
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            db = AppDatabase.getDatabase(requireContext(), userId)
        } else {
            // 로그인 상태가 아니면 처리하는 코드
        }

        alarmAdapter = AlarmAdapter(alarmList, { alarmData ->
            showEditAlarmDialog(alarmData)
        }, { alarmData ->
            deleteAlarmDirectly(alarmData)// 삭제 처리추가한것 문제시 삭제
        })
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

        // 로그아웃 버튼 클릭 리스너 설정
        val logoutButton: Button = view.findViewById(R.id.btn_logout)
        logoutButton.setOnClickListener {
            logout()
        }
        // CircleImageView를 찾아 클릭 리스너 추가
        val profileImageView: CircleImageView = view.findViewById(R.id.imageView)
        profileImageView.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestGalleryPermissionForAndroid13()
            } else {
                requestGalleryPermissionForBelowAndroid13()
            }
        }
        ivProfile = view.findViewById(R.id.imageView)
        initImageViewProfile()

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
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 알림 권한이 허용됨
                    // 필요에 따라 알림을 보내는 함수 호출
                } else {
                    // 알림 권한이 거부됨
                    Toast.makeText(context, "알림 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_CODE_GALLERY -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 갤러리 접근 권한이 허용된 경우 갤러리 열기
                    navigateGallery() // openGallery()가 아닌 navigateGallery()로 변경
                } else {
                    // 갤러리 접근 권한이 거부된 경우 메시지 표시
                    Toast.makeText(context, "갤러리 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // 11. 알람을 직접 삭제하는 함수
    private fun deleteAlarmDirectly(alarm: AlarmData) {
        // Coroutine을 직접 시작해서 AlarmDao 호출
        CoroutineScope(Dispatchers.IO).launch {
            db.alarmDao().deleteAlarm(alarm)

            // UI 갱신은 MainDispatcher에서 처리
            withContext(Dispatchers.Main) {
                alarmList.remove(alarm)
                alarmAdapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "알람이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // 로그아웃 기능 구현
    private fun logout() {
        // Firebase에서 로그아웃
        Firebase.auth.signOut()

        // 로그아웃 후 로그인 화면으로 이동
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)

        // 홈 프래그먼트가 포함된 액티비티를 종료
        activity?.finish()
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
/////////////////

    private fun initImageViewProfile() {
        ivProfile.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Android 13(API level 33) 이상일 경우
                requestGalleryPermissionForAndroid13()
            } else {
                // Android 13 미만일 경우
                requestGalleryPermissionForBelowAndroid13()
            }
        }
    }
    private fun requestGalleryPermissionForAndroid13() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED -> {
                navigateGallery()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES) -> {
                showPermissionContextPopup()
            }

            else -> requestPermissions(
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                REQUEST_CODE_GALLERY
            )
        }
    }

    private fun requestGalleryPermissionForBelowAndroid13() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {
                navigateGallery()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                showPermissionContextPopup()
            }

            else -> requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_GALLERY
            )
        }
    }
    private fun navigateGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(requireContext())
            .setTitle("권한이 필요합니다.")
            .setMessage("프로필 이미지를 바꾸기 위해서는 갤러리 접근 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_GALLERY)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }

    // 갤러리 오픈 함수
    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // 권한이 있는 경우 갤러리 열기
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*" // 이미지만 선택
            }
            startActivityForResult(intent, REQUEST_CODE_GALLERY)
        } else {
            // 권한이 없는 경우 권한 요청
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_PERMISSION)
        }
    }
    // 이미지 선택 결과 처리 함수 추가
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return

        when (requestCode) {
            REQUEST_CODE_SELECT_IMAGE -> {
                val selectedImageUri: Uri? = data?.data
                if (selectedImageUri != null) {
                    ivProfile.setImageURI(selectedImageUri)
                } else {
                    Toast.makeText(requireContext(), "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}

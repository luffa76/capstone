//로그인 화면
package com.example.tutle

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tutle.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater)}
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth

        val currentUser = auth.currentUser
        if (currentUser != null) {
            // 사용자가 이미 로그인된 상태라면, 메인 화면으로 이동
            val intent = Intent(this, NaviActivity::class.java)
            startActivity(intent)
            finish()  // 이 액티비티를 종료하여 돌아갈 수 없도록 함
        }



        val join = findViewById<Button>(R.id.join)
        val login = findViewById<Button>(R.id.login)

        join.setOnClickListener {
//            val email = findViewById<EditText>(R.id.email)
//            val password = findViewById<EditText>(R.id.password)
//            auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString())
//                .addOnCompleteListener(this) { task ->
//                    if (task.isSuccessful) {
//                        Toast.makeText(this, "회원가입 완료", Toast.LENGTH_LONG).show()
//                    } else {
//                        Toast.makeText(this, "회원가입 실패", Toast.LENGTH_LONG).show()
//                    }
//                }
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        login.setOnClickListener {
            val email = findViewById<EditText>(R.id.email)
            val password = findViewById<EditText>(R.id.password)
            auth.signInWithEmailAndPassword(email.text.toString(), password.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "로그인 완료", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, NaviActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "로그인 실패", Toast.LENGTH_LONG).show()
                    }
                }
        }
//        val logout = findViewById<Button>(R.id.logout)
//        logout.setOnClickListener {
//            Firebase.auth.signOut()
//            Toast.makeText(this, "로그아웃 완료", Toast.LENGTH_LONG).show()
//        }
    }

}
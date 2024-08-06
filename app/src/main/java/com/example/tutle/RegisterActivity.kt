package com.example.tutle

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val registerButton = findViewById<Button>(R.id.register)
        val checkEmailButton = findViewById<Button>(R.id.check_email)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)

        checkEmailButton.setOnClickListener {
            val emailText = email.text.toString()
            if (emailText.isEmpty()) {
                Toast.makeText(this, "이메일을 입력하세요.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.fetchSignInMethodsForEmail(emailText)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val signInMethods = task.result?.signInMethods
                        if (signInMethods.isNullOrEmpty()) {
                            Toast.makeText(this, "사용 가능한 이메일입니다.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "이미 사용 중인 이메일입니다.", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this, "이메일 중복 확인 실패.", Toast.LENGTH_LONG).show()
                    }
                }
        }

        registerButton.setOnClickListener {
            val emailText = email.text.toString()
            val passwordText = password.text.toString()

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(this, "모든 필드를 입력하세요.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val userData = hashMapOf(
                            "uid" to user?.uid
                        )
                        db.collection("users").document(user?.uid ?: "")
                            .set(userData)
                            .addOnSuccessListener {
                                Toast.makeText(this, "회원가입 완료", Toast.LENGTH_LONG).show()
                                finish()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "회원 정보 저장 실패", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(this, "회원가입 실패", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}

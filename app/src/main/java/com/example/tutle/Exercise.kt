package com.example.tutle

// 운동 데이터를 저장할 데이터 클래스
data class Exercise(
    val title: String,       // 운동 제목
    val description: String, // 운동 설명
    val imageResId: Int      // 운동 이미지 리소스 ID
)
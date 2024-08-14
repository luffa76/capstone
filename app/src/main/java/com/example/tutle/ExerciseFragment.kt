package com.example.tutle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class ExerciseFragment : Fragment() {

    private var latestValue: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // mypageFragment에서 전달된 최신 값 받기
        parentFragmentManager.setFragmentResultListener("latestValueKey", this) { _, bundle ->
            latestValue = bundle.getFloat("latest_value")
            updateFragment(latestValue)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 기본 Exercise 레이아웃 인플레이트
        return inflater.inflate(R.layout.fragment_exercise, container, false)
    }

    private fun updateFragment(latestValue: Float?) {

        // 현재 프래그먼트의 뷰 그룹을 가져옴
        val viewGroup = view as? ViewGroup ?: return

        // 기존의 모든 뷰를 제거함
        viewGroup.removeAllViews()

        // 최신 값에 따라 동적으로 추가할 레이아웃을 선택
        val layoutId = when {
            latestValue == null -> R.layout.fragment_exercise
            latestValue < 3 -> R.layout.fragment_ex1
            latestValue < 6 -> R.layout.fragment_ex2
            else -> R.layout.fragment_ex3
        }

        // 새 레이아웃을 동적으로 인플레이트하여 추가
        LayoutInflater.from(requireContext()).inflate(layoutId, viewGroup, true)
    }
}
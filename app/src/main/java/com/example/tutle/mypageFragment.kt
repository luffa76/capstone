package com.example.tutle

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.animation.Easing
import com.google.firebase.auth.FirebaseAuth
import kotlin.concurrent.thread

class mypageFragment : Fragment() {
    private lateinit var lineChart: LineChart
    private lateinit var yInput: EditText
    private lateinit var addPointButton: Button
    private val entries = ArrayList<Entry>()
    private lateinit var lineDataSet: LineDataSet
    private var xValue = 1f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mypage, container, false)

        lineChart = view.findViewById(R.id.chart)
        yInput = view.findViewById(R.id.y_input)
        addPointButton = view.findViewById(R.id.add_point_button)

        initializeChart()
        loadEntries() // 저장된 데이터를 불러옵니다.

        addPointButton.setOnClickListener {
            val yValue = yInput.text.toString().toFloatOrNull()
            if (yValue != null && yValue >= 0) {
                addEntry(yValue)
                yInput.text.clear()
                saveEntry(xValue, yValue)
                xValue += 1
            } else {
                yInput.error = "올바른 값을 입력하세요."
            }
        }

        return view
    }

    private fun initializeChart() {
        lineDataSet = LineDataSet(entries, "Y 값 변화").apply {
            lineWidth = 2f
            circleRadius = 6f
            setCircleColor(Color.parseColor("#FFA1B4DC"))
            color = Color.parseColor("#FFA1B4DC")
            setDrawCircleHole(true)
            setDrawValues(false)
        }

        lineChart.apply {
            data = LineData(lineDataSet)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.textColor = Color.BLACK
            axisLeft.textColor = Color.BLACK
            axisRight.isEnabled = false
            description = Description().apply { text = "" }
            animateY(2000)
            invalidate()
        }
    }

    private fun addEntry(y: Float) {
        entries.add(Entry(xValue, y))
        lineDataSet.notifyDataSetChanged()
        lineChart.data.notifyDataChanged()
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()

        sendLatestValueToExerciseFragment(y)
    }

    private fun saveEntry(x: Float, y: Float) {
        val sharedPreferences = requireContext().getSharedPreferences("ChartData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // 정확한 x, y 값을 저장
        editor.putFloat("x_$x", x)
        editor.putFloat("y_$x", y)
        editor.putFloat("last_x_value", x)
        editor.apply()
    }

    private fun loadEntries() {
        val sharedPreferences = requireContext().getSharedPreferences("ChartData", Context.MODE_PRIVATE)
        entries.clear()

        val lastXValue = sharedPreferences.getFloat("last_x_value", 0f)
        var x = 1f

        while (x <= lastXValue) {
            val savedX = sharedPreferences.getFloat("x_$x", x)
            val savedY = sharedPreferences.getFloat("y_$x", 0f)
            if (savedY >= 0) {
                entries.add(Entry(savedX, savedY))
            }
            x += 1f
        }
        if (lastXValue > 0) {
            xValue = lastXValue + 1f
        }

        lineDataSet.notifyDataSetChanged()
        lineChart.data.notifyDataChanged()
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()
    }

    private fun sendLatestValueToExerciseFragment(latestValue: Float) {
        val bundle = Bundle().apply {
            putFloat("latest_value", latestValue)
        }
        parentFragmentManager.setFragmentResult("latestValueKey", bundle)
    }
}

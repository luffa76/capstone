package com.example.tutle

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.highlight.Highlight
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

    private lateinit var auth: FirebaseAuth
    private lateinit var db: AppDatabase
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mypage, container, false)

        lineChart = view.findViewById(R.id.chart)
        yInput = view.findViewById(R.id.y_input)
        addPointButton = view.findViewById(R.id.add_point_button)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            userId = currentUser.uid
            db = AppDatabase.getDatabase(requireContext(), userId!!)
            loadEntries()
        } else {
            // 로그인되지않았을때의 상황
        }

        initializeChart()

        addPointButton.setOnClickListener {
            val yValue = yInput.text.toString().toFloatOrNull()

            if (yValue != null) {
                addEntry(yValue)
                yInput.text.clear()
                saveEntry(xValue, yValue)
                xValue += 1
            }
        }

        return view
    }

    private fun initializeChart() {
        lineDataSet = LineDataSet(entries, "Y 값 변화").apply {
            lineWidth = 2f
            circleRadius = 6f
            setCircleColor(Color.parseColor("#FFA1B4DC"))
            circleHoleColor = Color.BLUE
            color = Color.parseColor("#FFA1B4DC")
            setDrawCircleHole(true)
            setDrawCircles(true)
            setDrawHorizontalHighlightIndicator(false)
            setDrawHighlightIndicators(false)
            setDrawValues(false)
        }

        val lineData = LineData(lineDataSet)
        lineChart.data = lineData

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.BLACK
        xAxis.enableGridDashedLine(8f, 24f, 0f)

        val yLAxis = lineChart.axisLeft
        yLAxis.textColor = Color.BLACK

        val yRAxis = lineChart.axisRight
        yRAxis.setDrawLabels(false)
        yRAxis.setDrawAxisLine(false)
        yRAxis.setDrawGridLines(false)

        val description = Description().apply {
            text = ""
        }

        lineChart.apply {
            setDoubleTapToZoomEnabled(false)
            setDrawGridBackground(false)
            setDescription(description)
            animateY(2000, Easing.EaseInCubic)
            invalidate()
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    e?.let {
                        deleteEntry(it)
                    }
                }

                override fun onNothingSelected() {
                    // Do nothing
                }
            })
        }
    }

    private fun addEntry(y: Float) {
        entries.add(Entry(xValue, y))
        lineDataSet.notifyDataSetChanged()
        lineChart.data.notifyDataChanged()
        lineChart.notifyDataSetChanged()
        lineChart.invalidate()

        // 최신 값 전달
        sendLatestValueToExerciseFragment(y)


//        if (y < 0) {
//            Toast.makeText(context, "음수 값은 입력할 수 없습니다.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        // xValue를 올바르게 계산
//        val lastXValue = entries.maxByOrNull { it.x }?.x ?: 0f
//        if (xValue <= lastXValue) {
//            xValue = lastXValue + 1
//        }
//
//        entries.add(Entry(xValue, y))
//        updateChart()
//
//        saveEntry(xValue, y)
//        xValue += 1
    }
//    private fun updateChart() {
//        lineDataSet.notifyDataSetChanged()
//        lineChart.data.notifyDataChanged()
//        lineChart.notifyDataSetChanged()
//        lineChart.invalidate()
//    }
    private fun sendLatestValueToExerciseFragment(latestValue: Float) {
        val bundle = Bundle().apply {
            putFloat("latest_value", latestValue)
        }
        parentFragmentManager.setFragmentResult("latestValueKey", bundle)
    }

    private fun deleteEntry(entry: Entry) {
        thread {
            userId?.let {
                db.graphDataDao().deleteGraphData(it, entry.x, entry.y)
                activity?.runOnUiThread {
                    entries.remove(entry)

                    // xValue를 업데이트하여 차트에서 Y축의 최대값을 줄임
                    if (entries.isNotEmpty()) {
                        xValue = entries.maxByOrNull { it.x }?.x ?: 1f
                    } else {
                        xValue = 1f
                    }

                    // 음수 값이 될 가능성을 방지
                    if (xValue < 0) {
                        xValue = 1f
                    }

                    lineDataSet.notifyDataSetChanged()
                    lineChart.data.notifyDataChanged()
                    lineChart.notifyDataSetChanged()
                    lineChart.invalidate()
                }
            }
        }
    }

    private fun saveEntry(x: Float, y: Float) {
        thread {
            userId?.let {
                val graphData = GraphData(userId = it, xValue = x, yValue = y)
                db.graphDataDao().insertGraphData(graphData)
            }
        }
    }

    private fun loadEntries() {
        thread {
            userId?.let {
                val graphDataList = db.graphDataDao().getGraphDataForUser(it)
                entries.clear()
                for (data in graphDataList) {
                    entries.add(Entry(data.xValue, data.yValue))
                }
                if (graphDataList.isNotEmpty()) {
                    xValue = graphDataList.last().xValue + 1
                }
                activity?.runOnUiThread {
                    lineDataSet.notifyDataSetChanged()
                    lineChart.data.notifyDataChanged()
                    lineChart.notifyDataSetChanged()
                    lineChart.invalidate()
                }
            }
        }
    }
}

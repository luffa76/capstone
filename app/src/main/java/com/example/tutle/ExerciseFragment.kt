package com.example.tutle

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class ExerciseFragment : Fragment() {

    private var latestValue: Float? = null
    private var isFdPExercise = false // 운동 전환 여부
    private var lastExerciseType = "default" // 마지막으로 사용된 운동 타입을 기억

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
        val view = inflater.inflate(R.layout.fragment_exercise, container, false)

        // 기본 운동 설정 (처음 화면에서 기본 운동이 나오도록)
        setExercises(view, exercises)
        val exerciseTitle: TextView = view.findViewById(R.id.exercise0Title)
        exerciseTitle.text = "기본 운동"

        // 버튼 설정 (FdP 운동 전환용)
        val changeExerciseButton: Button = view.findViewById(R.id.changeButton)
        changeExerciseButton.setOnClickListener {
            if (isFdPExercise) {
                // 이전 운동으로 복귀
                updateFragment(latestValue)
                changeExerciseButton.text = "일자목"
            } else {
                // FdP 운동으로 전환
                setExercises(view, fdPExercises)
                changeExerciseButton.text = "이전 운동"
            }
            isFdPExercise = !isFdPExercise
        }
        // **기본적으로 운동 리스트를 설정**
        updateFragment(null)

        return view
    }

    private fun updateFragment(latestValue: Float?) {
        val viewGroup = view as? ViewGroup ?: return

        // 최신 값에 맞는 운동 리스트 적용
        val exercisesToShow = when {
            latestValue == null -> exercises
            latestValue < 3 -> exercises1
            latestValue < 6 -> exercises2
            else -> exercises3
        }

        // **운동 리스트 및 UI 업데이트**
        updateUI(exercisesToShow, latestValue)
        // 운동 리스트 설정
        setExercises(viewGroup, exercisesToShow)
        val exercise0_1Title: TextView? = view?.findViewById(R.id.exercise0_1Title)

        if (latestValue == null) {
            // 입력값이 없을 때 안내 텍스트 표시
            exercise0_1Title?.visibility = View.VISIBLE
        } else {
            // 입력값이 있을 때 안내 텍스트 숨김
            exercise0_1Title?.visibility = View.GONE
        }
    }

    // 운동 리스트와 제목을 업데이트하는 함수
    private fun updateUI(exercisesToShow: List<Exercise>, latestValue: Float?) {
        val exercise0Title: TextView? = view?.findViewById(R.id.exercise0Title)
        if (exercise0Title != null) {
            when {
                latestValue == null -> {
                    exercise0Title.text = "기본 운동"
                    setExercises(requireView(), exercises)
                }
                latestValue < 3 -> {
                    exercise0Title.text = "운동1"
                    lastExerciseType = "1" // 마지막 운동1로 설정
                    setExercises(requireView(), exercises1)
                }
                latestValue < 6 -> {
                    exercise0Title.text = "운동2"
                    lastExerciseType = "2" // 마지막 운동2로 설정
                    setExercises(requireView(), exercises2)
                }
                else -> {
                    exercise0Title.text = "운동3"
                    lastExerciseType = "3" // 마지막 운동3로 설정
                    setExercises(requireView(), exercises3)
                }
            }
        }
    }



    // 운동 리스트를 설정하는 함수
    private fun setExercises(view: View?, exercises: List<Exercise>) {
        // view가 null일 경우 함수 실행을 종료
        if (view == null) return

        // 운동 리스트에서 5개를 랜덤으로 선택
        val randomExercises = exercises.shuffled().take(5)

        // 첫 번째 운동 설정
        val exercise1Title: TextView? = view.findViewById(R.id.ex1_1Title)
        val exercise1Description: TextView? = view.findViewById(R.id.exercise1_1Description)
        val exercise1Img: ImageView? = view.findViewById(R.id.ex1_1Img)

        if (exercise1Title != null && exercise1Description != null && exercise1Img != null) {
            val exercise1 = randomExercises[0]
            exercise1Title.text = exercise1.title
            exercise1Description.text = exercise1.description
            exercise1Img.setImageResource(exercise1.imageResId)
        }

        // 두 번째 운동 설정
        val exercise2Title: TextView? = view.findViewById(R.id.ex1_2Title)
        val exercise2Description: TextView? = view.findViewById(R.id.exercise1_2Description)
        val exercise2Img: ImageView? = view.findViewById(R.id.ex1_2Img)

        if (exercise2Title != null && exercise2Description != null && exercise2Img != null) {
            val exercise2 = randomExercises[1]
            exercise2Title.text = exercise2.title
            exercise2Description.text = exercise2.description
            exercise2Img.setImageResource(exercise2.imageResId)
        }
        // 세 번째 운동 설정
        val exercise3Title: TextView? = view.findViewById(R.id.ex1_3Title)
        val exercise3Description: TextView? = view.findViewById(R.id.exercise1_3Description)
        val exercise3Img: ImageView? = view.findViewById(R.id.ex1_3Img)

        if (exercise3Title != null && exercise3Description != null && exercise3Img != null) {
            val exercise3 = randomExercises[2]
            exercise3Title.text = exercise3.title
            exercise3Description.text = exercise3.description
            exercise3Img.setImageResource(exercise3.imageResId)
        }

        // 네 번째 운동 설정
        val exercise4Title: TextView? = view.findViewById(R.id.ex1_4Title)
        val exercise4Description: TextView? = view.findViewById(R.id.exercise1_4Description)
        val exercise4Img: ImageView? = view.findViewById(R.id.ex1_4Img)

        if (exercise4Title != null && exercise4Description != null && exercise4Img != null) {
            val exercise4 = randomExercises[3]
            exercise4Title.text = exercise4.title
            exercise4Description.text = exercise4.description
            exercise4Img.setImageResource(exercise4.imageResId)
        }

        // 다섯 번째 운동 설정
        val exercise5Title: TextView? = view.findViewById(R.id.ex1_5Title)
        val exercise5Description: TextView? = view.findViewById(R.id.exercise1_5Description)
        val exercise5Img: ImageView? = view.findViewById(R.id.ex1_5Img)

        if (exercise5Title != null && exercise5Description != null && exercise5Img != null) {
            val exercise5 = randomExercises[4]
            exercise5Title.text = exercise5.title
            exercise5Description.text = exercise5.description
            exercise5Img.setImageResource(exercise5.imageResId)
        }
    }

    // 기존 운동 리스트들
    private val exercises = listOf(
        Exercise(
            "목 측면 스트레칭",
            "한쪽 손을 머리 위로 올려 반대쪽 귀 옆을 잡고, 머리를 천천히 손쪽으로 당깁니다. 각각의 방향으로 10초간 유지하고, 5회 반복합니다.",
            R.drawable.ex3_4
        ),
        Exercise(
            "목 후방 스트레칭",
            "등을 곧게 편 상태에서 턱을 살짝 당겨 목을 뒤로 당깁니다. 10초간 유지 후 풀어줍니다. 10회 반복.",
            R.drawable.fhpex1
        ),
        Exercise(
            "어깨 돌리기",
            "양 어깨를 천천히 앞에서 뒤로, 뒤에서 앞으로 돌립니다. 각 방향으로 10회씩, 2세트 수행합니다.",
            R.drawable.ex1_2
        ),
        Exercise(
            "턱 당기기",
            "턱을 살짝 당겨서 목이 곧게 펴지도록 유지합니다. 5~10초간 유지하고, 10회 반복합니다.",
            R.drawable.ex1_34
        ),
        Exercise(
            "상체 회전 스트레칭",
            "의자에 앉아 상체를 천천히 좌우로 돌려줍니다. 각각의 방향으로 5회 반복합니다.",
            R.drawable.ex1_5
        ),
        Exercise(
            "가슴 펴기 스트레칭",
            "양팔을 뒤로 깍지 끼고, 가슴을 펴며 어깨를 뒤로 당깁니다. 10초간 유지하고, 5회 반복합니다.",
            R.drawable.ex0_5
        ),
        Exercise(
            "벽에 기대어 목 스트레칭",
            "벽에 등을 기대고, 목을 뒤로 젖히며 스트레칭합니다. 10초간 유지하고 5회 반복합니다.",
            R.drawable.ex0_6
        ),
        Exercise(
            "팔을 흔들기",
            "양팔을 양옆으로 쭉 펴고, 천천히 팔을 앞뒤로 흔들어줍니다. 10회 반복합니다.",
            R.drawable.ex0_7
        ),
        Exercise(
            "목 스트레칭 (좌우 회전)",
            "양쪽 어깨를 편안하게 유지한 상태에서 목을 천천히 좌우로 돌려줍니다. 각각의 방향으로 10~15초 정도 유지합니다.",
            R.drawable.ex1_1
        )
    )

    private val exercises1 = listOf(
        Exercise(
            "목 스트레칭 (좌우 회전)",
            "양쪽 어깨를 편안하게 유지한 상태에서 목을 천천히 좌우로 돌려줍니다. 각각의 방향으로 10~15초 정도 유지합니다.",
            R.drawable.ex1_1
        ),
        Exercise(
            "목 측면 스트레칭",
            "한쪽 손을 머리 위로 올려 반대쪽 귀 옆을 잡고, 머리를 천천히 손쪽으로 당깁니다. 각각의 방향으로 10초간 유지하고, 5회 반복합니다.",
            R.drawable.ex3_4
        ),
        Exercise(
            "어깨 돌리기",
            "어깨를 위로 올렸다가 뒤로 돌리고, 다시 아래로 내리는 동작을 천천히 반복합니다. 10회씩 2~3세트 수행합니다.",
            R.drawable.ex1_2
        ),
        Exercise(
            "목 스트레칭 (앞뒤로 기울이기)",
            "목을 천천히 앞으로 기울여 턱이 가슴에 닿도록 하고, 이후 뒤로 젖힙니다. 각각의 방향으로 10초씩 유지합니다.",
            R.drawable.ex1_34
        ),
        Exercise(
            "고개 끄덕이기",
            "고개를 천천히 앞뒤로 끄덕여줍니다. 이때, 목에 과도한 힘을 주지 않도록 주의합니다. 10회 반복합니다.",
            R.drawable.ex1_34
        ),
        Exercise(
            "흉추 스트레칭",
            "양손을 가슴 앞에서 깍지 끼고, 상체를 좌우로 천천히 돌려줍니다. 10회 반복합니다.",
            R.drawable.ex1_5
        ),
        Exercise(
            "목 앞뒤로 밀기",
            " 목을 앞으로 천천히 내밀고, 뒤로 젖힙니다. 각각의 방향으로 10초간 유지합니다. 10회 반복합니다.",
            R.drawable.ex1_6
        ),
        Exercise(
            "의자에서 턱 당기기",
            " 의자에 앉아 등을 벽에 기대고, 턱을 가슴 쪽으로 당겨 목이 일직선이 되도록 합니다. 5초간 유지하고, 10회 반복합니다.",
            R.drawable.ex1_7
        ),
        Exercise(
            "어깨 스트레칭",
            "한쪽 팔을 가슴 앞으로 쭉 뻗고, 반대쪽 손으로 팔을 가슴 쪽으로 당겨줍니다. 각 방향으로 10초간 유지합니다.",
            R.drawable.ex1_8
        ),
        Exercise(
            "고개 옆으로 기울이기",
            "목을 옆으로 기울여서 귀가 어깨에 가까워지도록 합니다. 10초간 유지하고 반대쪽도 반복합니다.",
            R.drawable.ex1_9
        ),
        Exercise(
            "목 힘주기",
            "손으로 이마를 눌러 목에 저항을 주며 5초간 유지합니다. 이 운동을 10회 반복합니다.",
            R.drawable.ex1_10
        )

    )

    private val exercises2 = listOf(
        Exercise(
            "거북목 교정 운동 (턱 당기기)",
            "벽에 등을 대고 서서 턱을 살짝 당겨서 목이 곧게 펴지도록 유지합니다. 5~10초간 유지하고, 10회 반복합니다.",
            R.drawable.ex2_1
        ),
        Exercise(
            "등 상부 스트레칭 (벽 밀기)",
            "양팔을 벽에 대고 팔꿈치를 편 상태에서 벽을 미는 것처럼 상체를 천천히 밀어냅니다. 10초간 유지하고, 10회 반복합니다.",
            R.drawable.ex2_2
        ),
        Exercise(
            "천장 보기 스트레칭",
            "의자에 앉아 양손을 머리 뒤로 깍지 끼고, 목을 천천히 뒤로 젖혀서 천장을 바라봅니다. 10초간 유지하고, 10회 반복합니다.",
            R.drawable.ex2_3
        ),
        Exercise(
            "상체 회전 스트레칭",
            "의자에 앉아 상체를 좌우로 돌려줍니다. 양손은 허리에 두고, 돌릴 때마다 10초간 유지합니다. 각각 5회 반복합니다.",
            R.drawable.ex2_4
        ),
        Exercise(
            "어깨 및 상체 스트레칭",
            "팔을 등 뒤로 엇갈리게 두고, 한 손으로 반대쪽 팔꿈치를 잡고 당겨줍니다. 10초간 유지하고, 각각 5회 반복합니다.",
            R.drawable.ex2_5
        ),
        Exercise(
            "견갑골 돌리기",
            "양손을 어깨에 올리고, 팔꿈치로 원을 그리듯이 어깨를 돌립니다. 앞뒤로 각각 10회 반복합니다.",
            R.drawable.ex2_6
        ) ,
        Exercise(
                "양팔 벌리기 스트레칭",
                "팔을 양옆으로 벌리고, 천천히 뒤로 당깁니다. 어깨와 가슴을 최대한 펴고 5초간 유지합니다. 10회 반복합니다.",
        R.drawable.ex2_7
        ),
        Exercise(
            "턱 당기기",
            " 턱을 천천히 가슴 쪽으로 당겨 목을 곧게 펴줍니다. 5초간 유지하고 10회 반복합니다.",
            R.drawable.ex2_8
        ),
        Exercise(
            "상체 이완 스트레칭",
            " 두 팔을 위로 쭉 펴고, 상체를 옆으로 기울입니다. 각 방향으로 10초간 유지합니다.",
            R.drawable.ex2_9
        ),
        Exercise(
            "어깨 세우기",
            " 어깨를 귀 방향으로 올리고 3초간 유지한 후, 천천히 내립니다. 10회 반복합니다.",
            R.drawable.ex2_10
        ),
        Exercise(
            "목 스트레칭 (사선으로)",
            " 목을 대각선 방향으로 기울이며 스트레칭합니다. 각 방향으로 10초간 유지합니다.",
            R.drawable.ex1_9
        )
    )

    private val exercises3 = listOf(
        Exercise(
            "밴드 이용한 저항 운동 (목 저항 운동)",
            "탄력 밴드를 머리 뒤에 걸고, 밴드를 당겨 목을 저항하면서 천천히 당겨줍니다. 10초간 유지하고, 5회 반복합니다.",
            R.drawable.ex3_1
        ),
        Exercise(
            "흉추 이완 스트레칭 (캣카우 스트레칭)",
            "네발로 엎드려서 등을 아치형으로 만들어 올렸다가, 다시 내려줍니다. 각각의 자세를 5초간 유지하며, 10회 반복합니다.",
            R.drawable.ex3_2
        ),
        Exercise(
            "견갑골 압박 운동",
            "양 팔을 옆으로 벌리고, 어깨 날개뼈를 중앙으로 모으듯이 당깁니다. 5초간 유지하고, 10회 반복합니다.",
            R.drawable.ex3_3
        ),
        Exercise(
            "목 측면 스트레칭",
            "오른손으로 머리를 왼쪽으로 살짝 당겨주고, 반대쪽도 같은 방법으로 스트레칭합니다. 각각 10초간 유지하며, 5회 반복합니다.",
            R.drawable.ex3_4
        ),
        Exercise(
            "디스크 교정 운동 (턱 당기기 및 등 굽히기)",
            "등과 목을 천천히 굽혀 등을 원형으로 만들고, 다시 펴면서 턱을 당겨줍니다. 각각 10초간 유지하고, 5회 반복합니다.",
            R.drawable.ex3_5
        ),
        Exercise(
            "목 저항 운동 (앞뒤)",
            "손바닥을 이마에 대고, 머리를 앞으로 밀어 저항합니다. 그런 다음, 손을 뒤쪽으로 옮겨 목을 뒤로 젖히며 저항합니다. 각각 5초간 유지하고, 5회 반복합니다.",
            R.drawable.ex3_6
        ),
        Exercise(
            "흉추 회전 스트레칭",
            "양손을 등 뒤로 깍지 끼고, 상체를 좌우로 천천히 회전시킵니다. 각각의 방향으로 10초간 유지합니다. 5회 반복합니다.",
            R.drawable.ex3_7
        ),
        Exercise(
            "벽에 기대어 고개 숙이기",
            "벽에 등을 대고 고개를 천천히 숙여 턱이 가슴에 닿도록 합니다. 10초간 유지하고 5회 반복합니다.",
            R.drawable.ex3_8
        ),
        Exercise(
            "양팔 스트레칭",
            "두 팔을 천천히 앞으로 뻗고, 손끝이 벽에 닿도록 합니다. 각 방향에서 10초간 유지합니다.",
            R.drawable.ex3_7
        ),
        Exercise(
            "어깨 날개뼈 이완 운동",
            "양 팔을 쭉 펴고, 양쪽 어깨를 최대한 아래로 내리면서 5초간 유지합니다. 10회 반복합니다.",
            R.drawable.ex2_2
        )
    )

    // FdP 운동 리스트
    private val fdPExercises = listOf(
        Exercise(
            "목 후방 스트레칭",
            "등을 곧게 편 상태에서 턱을 살짝 당겨 목을 뒤로 당깁니다. 10초간 유지 후 풀어줍니다. 10회 반복.",
            R.drawable.fhpex1
        ),
        Exercise(
            "견갑골 안정화 운동",
            "양쪽 어깨를 위로 올리고 뒤로 당긴 후, 견갑골을 서로 모아 5초간 유지합니다. 10회 반복.",
            R.drawable.fhpex2
        ),
        Exercise(
            "거북목 방지 스트레칭",
            "양손을 머리 뒤로 두고, 고개를 뒤로 젖히며 목을 천천히 스트레칭 합니다. 10회 반복.",
            R.drawable.fhpex3
        ),
        Exercise(
            "흉추(등) 스트레칭",
            "등을 곧게 편 상태에서 팔을 머리 위로 들어 올리고, 상체를 뒤로 살짝 젖힙니다. 10초간 유지하고 5회 반복.",
            R.drawable.fhpex4
        ),
        Exercise(
            "어깨 돌리기",
            "어깨를 위로 올렸다가 뒤로 돌리고, 아래로 내리는 동작을 천천히 반복합니다. 10회씩 2~3세트 수행.",
            R.drawable.ex1_2
        ),
        Exercise(
            "목 측면 스트레칭",
            "오른손으로 머리를 왼쪽으로 살짝 당겨주고, 반대쪽도 같은 방법으로 스트레칭합니다. 각각 10초간 유지하며, 5회 반복합니다.",
            R.drawable.ex3_4
        ),
        Exercise(
            "스핑크스 자세",
            "바닥에 엎드려 팔꿈치를 어깨 바로 아래에 두고 상체를 천천히 들어 올리면서 목과 등 근육을 스트레칭합니다. 10~15초간 유지 후 천천히 내려옵니다. 5회 반복합니다.",
            R.drawable.fhpex5
        ),
        Exercise(
            "목 앞뒤로 기울이기",
            "목을 천천히 앞으로 기울여 턱이 가슴에 닿도록 하고, 이후 뒤로 젖힙니다. 각각의 방향에서 10초씩 유지합니다.",
            R.drawable.ex1_34
        ),
        Exercise(
            "어깨 올라가기",
            "어깨를 최대한 위로 올린 후, 천천히 내려줍니다. 이 동작을 10회 반복하며 어깨 주변의 긴장을 풀어줍니다.",
            R.drawable.ic_exercise
        )
    )
}

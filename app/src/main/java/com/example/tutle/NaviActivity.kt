package com.example.tutle

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.tutle.databinding.ActivityMainBinding
import com.example.tutle.databinding.ActivityNaviBinding

private const val TAG_MY_PAGE = "mypage_fragment"
private const val TAG_HOME = "home_fragment"
private const val TAG_CAMERA = "camera_fragment"
private const val TAG_EXERCISE = "exercise_fragment"

class NaviActivity : AppCompatActivity() {

private lateinit var binding : ActivityNaviBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setFragment(TAG_HOME, homeFragment())

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.cameraFragment -> setFragment(TAG_CAMERA, cameraFragment())
                R.id.homeFragment -> setFragment(TAG_HOME, homeFragment())
                R.id.mypageFragment-> setFragment(TAG_MY_PAGE, mypageFragment())
                R.id.ExerciseFragment -> setFragment(TAG_EXERCISE, ExerciseFragment()) // 새로운 항목 추가
            }
            true
        }
    }

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null){
            fragTransaction.add(R.id.frame_layout, fragment, tag)
        }

        val camera = manager.findFragmentByTag(TAG_CAMERA)
        val home = manager.findFragmentByTag(TAG_HOME)
        val myPage = manager.findFragmentByTag(TAG_MY_PAGE)
        val exercise = manager.findFragmentByTag(TAG_EXERCISE) // 새로운 프래그먼트 가져오기

        if (camera != null){
            fragTransaction.hide(camera)
        }

        if (home != null){
            fragTransaction.hide(home)
        }

        if (myPage != null) {
            fragTransaction.hide(myPage)
        }

        if (exercise != null) {
            fragTransaction.hide(exercise)
        }

        if (tag == TAG_CAMERA) {
            if (camera!=null){
                fragTransaction.show(camera)
            }
        }
        else if (tag == TAG_HOME) {
            if (home != null) {
                fragTransaction.show(home)
            }
        }

        else if (tag == TAG_MY_PAGE){
            if (myPage != null){
                fragTransaction.show(myPage)
            }
        }

        else if (tag == TAG_EXERCISE){
            if (exercise != null){
                fragTransaction.show(exercise)
            }
        }
        fragTransaction.commitAllowingStateLoss()
    }
}

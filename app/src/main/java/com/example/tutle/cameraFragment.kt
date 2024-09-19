package com.example.tutle

import android.Manifest
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Environment
import android.widget.Toast
import androidx.core.app.ActivityCompat

class cameraFragment : Fragment() {
    private lateinit var imageView: ImageView
    private lateinit var captureButton: Button
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_CAMERA_PERMISSION = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_camera, container, false)
        imageView = view.findViewById(R.id.imgView)
        captureButton = view.findViewById(R.id.btn_captureImg)

        captureButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                // 권한 요청
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
            }
        }
        showAlertDialog()

        return view
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            imageView.setImageBitmap(imageBitmap)
        }
    }
    // 권한 요청 결과 처리
//    override fun onRequestPermissionsResult(
//        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_CAMERA_PERMISSION) {
//            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                // 권한이 허용된 경우 카메라 실행
//                dispatchTakePictureIntent()
//            } else {
//                // 권한이 거부된 경우
//                Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
    private val cameraPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dispatchTakePictureIntent()
        } else {
            Toast.makeText(requireContext(), "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("알림")
        builder.setMessage("1. 조명을 밝게하세요. 2. 카메라를 세로로 하세요. 3. 옆에서 상체 절반정도 나오도록 하세요.")
        builder.setPositiveButton("확인") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                cameraPermissionRequest.launch(android.Manifest.permission.CAMERA)
            } else {
                dispatchTakePictureIntent()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

}

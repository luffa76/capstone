package com.example.tutle

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
import android.content.pm.PackageManager
import android.os.Environment

class cameraFragment : Fragment() {

    private lateinit var captureIV: ImageView
    private lateinit var imageUrl: Uri
    private lateinit var currentPhotoPath: String

    private val cameraPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dispatchTakePictureIntent()
        } else {
            // 권한이 거부된 경우 처리할 로직
        }
    }

    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            captureIV.setImageURI(imageUrl)
        } else {
            // 사진이 촬영되지 않았을 때 처리할 로직
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 안내메시지
        val view = inflater.inflate(R.layout.fragment_camera, container, false)

        captureIV = view.findViewById(R.id.imgView)
        val captureButton: Button = view.findViewById(R.id.btn_captureImg)
        captureButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                cameraPermissionRequest.launch(android.Manifest.permission.CAMERA)
            } else {
                dispatchTakePictureIntent()
            }
        }
        showAlertDialog()

        return view
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

    private fun dispatchTakePictureIntent() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            null
        }
        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                it
            )
            imageUrl = photoURI
            takePicture.launch(photoURI)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }
}

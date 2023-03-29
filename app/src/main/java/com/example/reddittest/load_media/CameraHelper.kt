package com.example.reddittest.load_media

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

interface CameraHelper {

    fun startCameraActivity(activity: Activity, requestCode: Int, fileProviderAuthority: String): Uri? {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(activity.packageManager) != null) {

            val imageFile = createImageFile(activity)
            if (imageFile != null) {
                val photoURI = FileProvider.getUriForFile(activity, fileProviderAuthority, imageFile)

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

                activity.startActivityForResult(cameraIntent, requestCode)

                return photoURI
            }
        }

        return null
    }

    private fun createImageFile(activity: Activity): File? {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "JPEG_${timeStamp}_"
        val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(fileName, ".jpg", storageDir)
    }
}
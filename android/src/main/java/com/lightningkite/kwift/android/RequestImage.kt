package com.lightningkite.kwift.android

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

/**
 * Starts an intent with a direct callback.
 */
fun ActivityAccess.startIntent(
    intent: Intent,
    options: Bundle = Bundle(),
    onResult: (Int, Intent?) -> Unit = { _, _ -> }
) {
    activity?.startActivityForResult(intent, prepareOnResult(onResult = onResult), options)
}

fun ActivityAccess.intentGallery(
    callback: (Uri?) -> Unit
) {
    requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE) {
        if (it) {
            val getIntent = Intent(Intent.ACTION_GET_CONTENT)
            getIntent.type = "image/*"

            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.type = "image/*"

            val chooserIntent = Intent.createChooser(getIntent, "Select Image")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))

            this.startIntent(chooserIntent) { code, result ->
                val uri = result?.data
                if (code == Activity.RESULT_OK && uri != null) {
                    val intent = CropImage.activity(uri)
                        .setRequestedSize(1024, 1024, CropImageView.RequestSizeOptions.SAMPLING)
                        .setAspectRatio(1,1)
                        .setFixAspectRatio(true)
                        .getIntent(context)
                    startIntent(intent) { resultCode, data ->
                        if (resultCode == Activity.RESULT_OK) {
                            val finalUri = CropImage.getActivityResult(data).uri
                            Log.i("ImageUploadLayout", finalUri.toString())
                            callback(finalUri)
                        }
                    }
                } else callback(null)
            }
        } else {
            callback(null)
        }
    }
}

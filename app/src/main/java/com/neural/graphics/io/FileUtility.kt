package com.neural.graphics.io

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class FileUtility {

    companion object{
        fun grantStoragePermission(context: Activity){
            // check for storage permission
            if (checkStoragePermissionDenied(context)) {
                // ask for the permission
                ActivityCompat.requestPermissions(
                    context, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 1
                )
            }
        }
         fun checkStoragePermissionDenied(context: Activity): Boolean {
            // check for storage permission
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        }
    }
}
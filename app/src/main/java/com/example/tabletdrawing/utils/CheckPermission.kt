package com.example.tabletdrawing.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings

// 1. Storage 읽고 쓰기 Permission 체크
private const val READ_PERMISSION_REQUEST_CODE = 100
private const val WRITE_PERMISSION_REQUEST_CODE = 200

fun Activity.checkReadStoragePermission() {
    val isGrantedReadPermission = this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    if (!isGrantedReadPermission) {
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            startActivity(getSettingIntent())
        } else {
            requestReadStoragePermission()
        }
    }
}

fun Activity.checkWriteStoragePermission() {
    val isGrantedWritePermission = this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    if (!isGrantedWritePermission) {
        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            startActivity(getSettingIntent())
        } else {
            requestWriteStoragePermission()
        }
    }
}

fun Activity.requestReadStoragePermission() {
    this.requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), READ_PERMISSION_REQUEST_CODE)
}

fun Activity.requestWriteStoragePermission() {
    this.requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), WRITE_PERMISSION_REQUEST_CODE)
}

fun Activity.getSettingIntent(): Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
    data = Uri.parse("package:${packageName}")
}


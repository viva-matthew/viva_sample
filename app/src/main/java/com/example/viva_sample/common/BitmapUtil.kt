package com.example.viva_sample.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory

class BitmapUtil {

    fun ByteArray.toBitmap():Bitmap{
        return BitmapFactory.decodeByteArray(this,0,size)
    }
}
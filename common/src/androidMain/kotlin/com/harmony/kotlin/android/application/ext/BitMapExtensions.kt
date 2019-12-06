package com.harmony.kotlin.android.application.ext

import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Base64
import androidx.annotation.CheckResult
import java.io.ByteArrayOutputStream
import java.io.IOException

/*these extensions are super usefull to deal with devices (like Samsung)
that rotate images because yes*/


fun Bitmap.encodeBase64(): String {
  ByteArrayOutputStream().use {
    this.compress(Bitmap.CompressFormat.JPEG, 10, it)
    val b = it.toByteArray()
    return Base64.encodeToString(b, Base64.DEFAULT)
  }
}

@CheckResult
fun Bitmap.rotate(degrees: Float): Bitmap {
  val matrix = Matrix()
  matrix.postRotate(degrees)
  return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}

@CheckResult
fun Bitmap.flip(horizontal: Boolean, vertical: Boolean): Bitmap {
  val matrix = Matrix()
  matrix.preScale(if (horizontal) -1f else 1f, if (vertical) -1f else 1f)
  return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
}

@Throws(IOException::class)
@CheckResult
fun Bitmap.modifyOrientation(image_absolute_path: String): Bitmap {
  if (image_absolute_path.isBlank()) {
    return this
  }
  val ei = ExifInterface(image_absolute_path)
  val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

  return when (orientation) {
    ExifInterface.ORIENTATION_ROTATE_90 -> this.rotate(90f)

    ExifInterface.ORIENTATION_ROTATE_180 -> this.rotate(180f)

    ExifInterface.ORIENTATION_ROTATE_270 -> this.rotate(270f)

    ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> this.flip(true, false)

    ExifInterface.ORIENTATION_FLIP_VERTICAL -> this.flip(false, true)

    else -> this
  }
}

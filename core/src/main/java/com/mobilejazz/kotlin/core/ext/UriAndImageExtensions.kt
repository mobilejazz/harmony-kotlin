package com.mobilejazz.kotlin.core.ext

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.annotation.CheckResult
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.IOException

/*these extensions are super usefull to deal with devices (like Samsung)
that rotate images because yes*/


fun Uri.toBase64(context: Context): String {
  return BitmapFactory.decodeStream(context.contentResolver.openInputStream(this))
      .modifyOrientation(this.getPath(context) ?: "")
      .encodeBase64()
}

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


fun Uri.getPath(context: Context): String? {

  // DocumentProvider
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
    if (DocumentsContract.isDocumentUri(context, this)) {
      // ExternalStorageProvider
      if (this.isExternalStorageDocument()) {
        val docId = DocumentsContract.getDocumentId(this)
        val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val type = split[0]

        if ("primary".equals(type, ignoreCase = true)) {
          return "${Environment.getExternalStorageDirectory()}/${split[1]}"
        }

        // TODO handle non-primary volumes
      } else if (this.isDownloadsDocument()) {

        val id = DocumentsContract.getDocumentId(this)
        val contentUri = ContentUris.withAppendedId(
            Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)!!)

        return contentUri.getDataColumn(context)
      } else if (this.isMediaDocument()) {
        val docId = DocumentsContract.getDocumentId(this)
        val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val type = split[0]

        var contentUri: Uri? = null
        if ("image" == type) {
          contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        } else if ("video" == type) {
          contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        } else if ("audio" == type) {
          contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val selection = "_id=?"
        val selectionArgs = arrayOf(split[1])

        return contentUri?.getDataColumn(context, selection, selectionArgs)
      }// MediaProvider
      // DownloadsProvider
    }
  } else if ("content".equals(this.scheme, ignoreCase = true)) {
    return this.getDataColumn(context, null, null)
  } else if ("file".equals(this.scheme, ignoreCase = true)) {
    return this.path
  }// File
  // MediaStore (and general)

  return null
}

/**
 * Get the value of the data column for this Uri. This is useful for
 * MediaStore Uris, and other file-based ContentProviders.

 * @param context The context.
 * *
 * @param uri The Uri to query.
 * *
 * @param selection (Optional) Filter used in the query.
 * *
 * @param selectionArgs (Optional) Selection arguments used in the query.
 * *
 * @return The value of the _data column, which is typically a file path.
 */
@SuppressLint("Recycle")
fun Uri.getDataColumn(context: Context, selection: String? = null,
                      selectionArgs: Array<String>? = null): String? {

  val cursor: Cursor?
  val column = "_data"
  val projection = arrayOf(column)

  cursor = context.contentResolver.query(this, projection, selection, selectionArgs, null)
  return cursor?.let {
    it.use {
      if (it.moveToFirst()) {
        val column_index = it.getColumnIndexOrThrow(column)
        return it.getString(column_index)
      } else {
        return null
      }
    }
  }
}

fun Uri.isExternalStorageDocument(): Boolean {
  return "com.android.externalstorage.documents" == this.authority
}

fun Uri.isDownloadsDocument(): Boolean {
  return "com.android.providers.downloads.documents" == this.authority
}

fun Uri.isMediaDocument(): Boolean {
  return "com.android.providers.media.documents" == this.authority
}
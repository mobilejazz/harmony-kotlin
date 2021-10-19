package com.harmony.kotlin.android.application.ext

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore


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


fun Uri.toBase64(context: Context): String {
  return BitmapFactory.decodeStream(context.contentResolver.openInputStream(this))
      .modifyOrientation(this.getPath(context) ?: "")
      .encodeBase64()
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
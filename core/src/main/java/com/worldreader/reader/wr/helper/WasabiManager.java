package com.worldreader.reader.wr.helper;

import android.content.Context;
import com.google.common.base.Throwables;
import com.intertrust.wasabi.ErrorCodeException;
import com.intertrust.wasabi.Runtime;
import com.intertrust.wasabi.jni.ErrorCodeHelper;
import com.intertrust.wasabi.media.MediaStream;
import com.mobilejazz.logger.library.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Singleton public class WasabiManager {

  private static final String TAG = WasabiManager.class.getSimpleName();

  private static final String WASABI_STORAGE_FOLDER = "wasabi_store";
  private static final String WASABI_STORAGE_TMP_FOLDER = "wasabi_store_tmp";

  private final File storeFile;
  private final File storeTmpFile;
  private final Logger logger;

  @Inject public WasabiManager(final Context context, final Logger logger) {
    this.storeFile = context.getDir(WASABI_STORAGE_FOLDER, Context.MODE_PRIVATE);
    this.storeTmpFile = context.getDir(WASABI_STORAGE_TMP_FOLDER, Context.MODE_PRIVATE);
    this.logger = logger;
  }

  public void initialize() {
    try {
      // Initialize Wasabi SDK
      Runtime.initialize(storeFile.getAbsolutePath());
    } catch (ErrorCodeException e) {
      logger.e(TAG, "Something went wrong while initializing the SDK. Error: " + Throwables.getStackTraceAsString(e));
      logger.e(TAG, "Possible explanation for this exception. Explanation: " + ErrorCodeHelper.explainErrorCode(e.getErrorCode()));
    }
  }

  public boolean isPersonalized() {
    return Runtime.isPersonalized();
  }

  public void personalize() {
    try {
      Runtime.personalize();
    } catch (ErrorCodeException e) {
      logger.e(TAG, "Something went wrong while personalizing the SDK. Error: " + Throwables.getStackTraceAsString(e));
      logger.e(TAG, "Possible explanation for this exception. Explanation: " + ErrorCodeHelper.explainErrorCode(e.getErrorCode()));
    }
  }

  public void processToken(String token) {
    try {
      Runtime.processServiceToken(token);
    } catch (ErrorCodeException e) {
      logger.e(TAG, "Something went wrong while processing the token. Error: " + Throwables.getStackTraceAsString(e));
      logger.e(TAG, "Possible explanation for this exception. Explanation: " + ErrorCodeHelper.explainErrorCode(e.getErrorCode()));
    }
  }

  public File decrypt(String bookId, File file) {
    try {
      // Generate keys and IVs
      final MediaStream.FormatInfoGeneric fi = new MediaStream.FormatInfoGeneric();
      fi.content_id = bookId.replace("-", "");
      fi.iv = hexStringToByteArray(md5(bookId));

      // Create MediaStream
      final MediaStream ms = new MediaStream("file://" + file.getAbsolutePath(), MediaStream.SourceType.AES128CBC, fi);

      // Create tmp file
      final File tmp = File.createTempFile(bookId, ".tmp", storeTmpFile);

      // Decrypt content
      final byte[] buffer = new byte[4096];
      final FileOutputStream fos = new FileOutputStream(tmp);
      int bytesRead;
      while (-1 != (bytesRead = ms.read(buffer))) {
        fos.write(buffer, 0, bytesRead);
      }
      ms.close();
      fos.close();

      return tmp;
    } catch (NullPointerException | ErrorCodeException | IOException e) {
      logger.e(TAG, "Problem while decrypting book with id: " + bookId + "! Error: " + Throwables.getStackTraceAsString(e));
      if (e instanceof ErrorCodeException) {
        logger.e(TAG, "Possible explanation for this exception. Explanation: " + ErrorCodeHelper.explainErrorCode(((ErrorCodeException) e).getErrorCode()));
      }
      return null;
    }
  }

  public void clearTmp() {
    clearDirectoryContent(storeTmpFile);
  }

  public void nuke() {
    try {
      Runtime.shutdown();
      clearTmp();
      clearStore();
    } catch (ErrorCodeException e) {
      logger.e(TAG, "Problem while trying to nuke Wasabi SDK! Error: " + Throwables.getStackTraceAsString(e));
      logger.e(TAG, "Possible explanation for this exception. Explanation: " + ErrorCodeHelper.explainErrorCode(e.getErrorCode()));
    }
  }

  private void clearStore() {
    clearDirectoryContent(storeFile);
  }

  private void clearDirectoryContent(File file) {
    final File[] files = file.listFiles();
    for (File f : files) {
      f.delete();
    }
  }

  private byte[] hexStringToByteArray(String s) {
    final int len = s.length();
    final byte[] data = new byte[len / 2];
    for (int i = 0; i < len; i += 2) {
      data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
    }
    return data;
  }

  private String md5(String s) {
    try {
      // Create MD5 Hash
      final MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
      digest.update(s.getBytes());

      final byte messageDigest[] = digest.digest();

      // Create Hex String
      final StringBuilder hexString = new StringBuilder();
      for (byte aMessageDigest : messageDigest) {
        hexString.append(Integer.toHexString(0xFF & aMessageDigest));
      }

      return hexString.toString();
    } catch (NoSuchAlgorithmException e) {
      logger.e(TAG, "MD5 issue! Error: " + Throwables.getStackTraceAsString(e));
    }

    return "";
  }

}

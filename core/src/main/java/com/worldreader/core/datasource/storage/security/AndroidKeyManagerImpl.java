package com.worldreader.core.datasource.storage.security;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.util.Base64;
import com.worldreader.core.datasource.storage.security.exception.KeyNotFoundException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.security.auth.x500.X500Principal;
import java.io.*;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.util.*;

import static java.security.KeyStore.PrivateKeyEntry;
import static java.security.KeyStore.getInstance;

/** A more robust and secure implementation of the KeyManager that tries to use the
 * AndroidKeyStore.
 * */
@Deprecated @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2) public class AndroidKeyManagerImpl
    extends AbstractKeyManager<MigrationWrapperPreferences> {

  private static final String ALIAS = KeyManager.class.getCanonicalName();
  private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
  private static final String CIPHER_ALGORITHM = "RSA/ECB/PKCS1Padding";
  private static final String CIPHER_PROVIDER = "AndroidOpenSSL";
  private static final String RSA = "RSA";

  private Cipher encryptor;
  private Cipher decryptor;

  public AndroidKeyManagerImpl(Context context, MigrationWrapperPreferences keyManagerPreferences)
      throws KeyStoreException {
    super(context, keyManagerPreferences);
    try {
      initializeKeystore();
    } catch (CertificateException | NoSuchAlgorithmException | IOException |
        InvalidAlgorithmParameterException | NoSuchProviderException |
        NoSuchPaddingException | UnrecoverableEntryException | InvalidKeyException |
        IllegalStateException e) {
      throw new KeyStoreException(e);
    }
  }

  private void initializeKeystore()
      throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException,
      NoSuchProviderException, InvalidAlgorithmParameterException, UnrecoverableEntryException,
      NoSuchPaddingException, InvalidKeyException {

    // Prepare keystore
    KeyStore keyStore = getInstance(ANDROID_KEYSTORE);
    keyStore.load(null);

    // Check if we have currently have a private/public key in our keystore
    if (!keyStore.containsAlias(ALIAS)) {
      Calendar initialDate = Calendar.getInstance(Locale.ENGLISH);
      Calendar endDate = Calendar.getInstance(Locale.ENGLISH);
      endDate.add(Calendar.YEAR, 20);
      X500Principal x500Principal =
          new X500Principal("CN=Worlreader Certificate, O=Worldreader Authority");

      KeyPairGenerator generator = KeyPairGenerator.getInstance(RSA, ANDROID_KEYSTORE);
      AlgorithmParameterSpec spec;

      if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        spec = new KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_ENCRYPT
            | KeyProperties.PURPOSE_DECRYPT).setCertificateSerialNumber(BigInteger.ONE).
            setCertificateSubject(x500Principal).
            setKeyValidityStart(initialDate.getTime()).setKeyValidityEnd(endDate.getTime()).
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1).
            build();
      } else {
        //noinspection deprecation
        spec = new KeyPairGeneratorSpec.Builder(context).setAlias(ALIAS)
            .setSubject(x500Principal)
            .setSerialNumber(BigInteger.ONE)
            .setStartDate(initialDate.getTime())
            .setEndDate(endDate.getTime())
            .build();
      }

      generator.initialize(spec);
      generator.generateKeyPair();
    }

    // Public key
    PrivateKeyEntry privateKeyEntry = (PrivateKeyEntry) keyStore.getEntry(ALIAS, null);
    RSAPublicKey publicKey = (RSAPublicKey) privateKeyEntry.getCertificate().getPublicKey();

    encryptor = Cipher.getInstance(CIPHER_ALGORITHM, CIPHER_PROVIDER);
    encryptor.init(Cipher.ENCRYPT_MODE, publicKey);

    // Private key
    // Can't be casted to RSAPrivateKey because of https://code.google.com/p/android/issues/detail?id=205450
    decryptor = Cipher.getInstance(CIPHER_ALGORITHM, CIPHER_PROVIDER);
    decryptor.init(Cipher.DECRYPT_MODE, privateKeyEntry.getPrivateKey());
  }

  @Override public boolean storeKey(@NonNull String key) {
    try {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, encryptor);
      cipherOutputStream.write(key.getBytes("UTF-8"));
      cipherOutputStream.close();

      byte[] vals = outputStream.toByteArray();
      String encryptedText = Base64.encodeToString(vals, Base64.DEFAULT);

      return preferences.edit().putString(KEY, encryptedText).commit();
    } catch (IOException e) {
      e.printStackTrace(); // Should be ignored!
      return false;
    }
  }

  @Override public String retrieveKey() throws KeyNotFoundException {
    String encryptedText = preferences.getString(KEY, null);

    if (encryptedText == null) {
      throw new KeyNotFoundException();
    }

    CipherInputStream cipherInputStream = new CipherInputStream(
        new ByteArrayInputStream(Base64.decode(encryptedText, Base64.DEFAULT)), decryptor);

    List<Byte> values = new ArrayList<>();
    int nextByte;

    try {
      while ((nextByte = cipherInputStream.read()) != -1) {
        values.add((byte) nextByte);
      }
    } catch (IOException e) {
      e.printStackTrace(); // Should be ignored!
    }

    byte[] bytes = new byte[values.size()];
    for (int i = 0; i < bytes.length; i++) {
      bytes[i] = values.get(i);
    }

    try {
      String decryptedKey = new String(bytes, 0, bytes.length, "UTF-8");
      return decryptedKey;
    } catch (UnsupportedEncodingException e) {
      return null;
    }
  }

  @Override public boolean existsKey() {
    return preferences.contains(KEY);
  }
}

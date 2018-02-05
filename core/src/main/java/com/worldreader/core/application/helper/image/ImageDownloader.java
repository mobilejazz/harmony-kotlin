package com.worldreader.core.application.helper.image;

import java.io.*;

public interface ImageDownloader {

  void download(String key, String url);

  boolean delete(String key);

  File getImage(String key);

  boolean hasImage(String key);
}

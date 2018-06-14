package com.worldreader.reader.wr.helper;

import java.io.*;

public interface WasabiManager {


  public void initialize();

  public boolean isPersonalized();

  public void personalize();

  public void processToken(String token);

  public File decrypt(String bookId, File file);

  public void clearTmp();

  public void nuke() ;


}

package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import net.nightwhistler.htmlspanner.FontFamily;
import net.nightwhistler.htmlspanner.SystemFontResolver;
import com.worldreader.reader.epublib.nl.siegmann.epublib.domain.Resource;
import com.worldreader.reader.epublib.nl.siegmann.epublib.util.IOUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: alex
 * Date: 6/23/13
 * Time: 9:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class EpubFontResolver extends SystemFontResolver {

  public static final String TAG = EpubFontResolver.class.getSimpleName();

  private Map<String, FontFamily> loadedTypeFaces = new HashMap<String, FontFamily>();
  private TextLoader textLoader;

  private Context context;

  public EpubFontResolver(TextLoader loader, Context context) {

    this.textLoader = loader;
    loader.setFontResolver(this);

    this.context = context;
  }

  @Override protected FontFamily resolveFont(String name) {
    Log.d(TAG, "Trying lookup for font " + name);

    if (loadedTypeFaces.containsKey(name)) {
      return loadedTypeFaces.get(name);
    }

    Log.d(TAG, "Font is not in cache, falling back to super.");

    return super.resolveFont(name);
  }

  public void loadEmbeddedFont(String name, String resourceHRef) {
    Log.d(TAG, "Attempting to load custom font from href " + resourceHRef);

    if (loadedTypeFaces.containsKey(name)) {
      Log.d(TAG, "Already have font " + resourceHRef + ", aborting.");
      return;
    }

    Resource res = textLoader.getCurrentBook().getResources().getByFileName(resourceHRef);

    if (res == null) {
      Log.e(TAG, "No resource found for href " + resourceHRef);
      return;
    }

    File tempFile = new File(context.getCacheDir(), UUID.randomUUID().toString());

    try {
      IOUtil.copy(res.getInputStream(), new FileOutputStream(tempFile));
      res.close();

      Typeface typeface = Typeface.createFromFile(tempFile);

      FontFamily fontFamily = new FontFamily(name, typeface);

      Log.d(TAG, "Loaded embedded font with name " + name);
      loadedTypeFaces.put(name, fontFamily);
    } catch (IOException io) {
      Log.e(TAG, "Could not load embedded font " + name, io);
    } finally {
      tempFile.delete();
    }
  }
}

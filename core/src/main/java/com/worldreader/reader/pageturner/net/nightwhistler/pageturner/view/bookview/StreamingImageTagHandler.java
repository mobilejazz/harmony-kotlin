package com.worldreader.reader.pageturner.net.nightwhistler.pageturner.view.bookview;

import android.text.SpannableStringBuilder;
import net.nightwhistler.htmlspanner.SpanStack;
import net.nightwhistler.htmlspanner.TagNodeHandler;
import org.htmlcleaner.TagNode;

public class StreamingImageTagHandler extends TagNodeHandler {

  public static final String TAG = StreamingImageTagHandler.class.getSimpleName();

  private final TextLoader textLoader;
  private final ResourcesLoader resourcesLoader;
  private final boolean fakeImages;

  public StreamingImageTagHandler(TextLoader textLoader, ResourcesLoader resourcesLoader,
      boolean fakeImages) {
    this.textLoader = textLoader;
    this.resourcesLoader = resourcesLoader;
    this.fakeImages = fakeImages;
  }

  @Override
  public void handleTagNode(TagNode node, SpannableStringBuilder builder, int start, int end,
      SpanStack span) {
    String src = node.getAttributeByName("src");

    if (src == null) {
      src = node.getAttributeByName("href");
    }

    if (src == null) {
      src = node.getAttributeByName("xlink:href");
    }

    if (src == null) {
      return;
    }

    builder.append("\uFFFC");

    /*if (src.startsWith("data:image")) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {

        try {
          String dataString = src.substring(src.indexOf(',') + 1);

          byte[] binData = Base64.decode(dataString, Base64.DEFAULT);

          setImageSpan(builder, new BitmapDrawable(getContext().getResources(),
                  BitmapFactory.decodeByteArray(binData, 0, binData.length)), start,
              builder.length());
        } catch (OutOfMemoryError | IllegalArgumentException ia) {
          //Out of memory or invalid Base64, ignore
        }
      }
    } else if (spine != null) {

      String resolvedHref = spine.resolveHref(src);

      if (textLoader.hasCachedImage(resolvedHref) && !fakeImages) {
        Drawable drawable = textLoader.getCachedImage(resolvedHref);
        setImageSpan(builder, drawable, start, builder.length());
        Log.d(TAG, "Got cached href: " + resolvedHref);
      } else {
        Log.d(TAG, "Loading href: " + resolvedHref);
        this.registerCallback(resolvedHref,
            new ImageCallback(resolvedHref, builder, start, builder.length(), fakeImages));
      }
    }*/
  }
}

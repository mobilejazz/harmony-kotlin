package com.worldreader.core.application.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.worldreader.core.R;
import com.worldreader.core.common.intents.Intents;

public class WorldReaderWebViewActivity extends AppCompatActivity {

  public static final String KEY_URL = "webview.url.key";
  public static final String TITLE_URL = "webview.title.key";

  public enum Url {
    TERMS_CONDITIONS("http://wrm.worldreader.org/terms.html"),
    PRIVACY_POLICY("http://wrm.worldreader.org/privacy.html");

    String url;

    Url(String url) {
      this.url = url;
    }

    public String getUrl() {
      return url;
    }
  }

  public static Intent getCallingIntent(Context context, Url url, String title) {
    if (url == null) {
      throw new IllegalArgumentException("URL must be not null");
    }

    return Intents.with(context, WorldReaderWebViewActivity.class)
        .putExtra(KEY_URL, url)
        .putExtra(TITLE_URL, title)
        .build();
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.worldreader_webview_activity);

    init();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int itemId = item.getItemId();
    switch (itemId) {
      case android.R.id.home:
        onBackPressed();
        break;
    }

    return super.onOptionsItemSelected(item);
  }

  private void init() {
    Url url = (Url) getIntent().getExtras().getSerializable(KEY_URL);
    final String title = getIntent().getExtras().getString(TITLE_URL);

    initializeActionBar(title);

    final WebView webView = (WebView) findViewById(R.id.worldreader_webview_activity_wv);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.setWebViewClient(new WebViewClient());
    webView.loadUrl(url.getUrl());
  }

  private void initializeActionBar(String title) {
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle(title);
    }
  }
}

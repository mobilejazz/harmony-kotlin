package com.worldreader.reader.wr.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.worldreader.core.R;
import com.worldreader.core.domain.model.WordDefinition;

import java.util.Map;
import java.util.regex.Pattern;

public class DefinitionView extends LinearLayout {

  private View loadingContainer;
  private View definitionContainer;
  private View wordDefinitionsContainer;

  private LinearLayout nounContainer;
  private LinearLayout verbContainer;
  private LinearLayout adjectiveContainer;
  private LinearLayout adverbContainer;
  private TextView noResultsContainer;

  private ImageButton closeBtn;

  private TextView definitionWordTv;
  private TextView nounDefinitionTv;
  private TextView verbDefinitionTv;
  private TextView adjectiveDefinitionTv;
  private TextView adverbDefinitionTv;

  private OnClickCrossListener listener;
  private WordDefinition wordDefinition;

  public interface OnClickCrossListener {
    void onClick(DefinitionView view);
  }

  public DefinitionView(Context context) {
    super(context);
  }

  public DefinitionView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  public DefinitionView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init();
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public DefinitionView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    init();
  }

  public void showLoading() {
    loadingContainer.setVisibility(View.VISIBLE);
    definitionContainer.setVisibility(View.GONE);
  }

  public void showDefinition() {
    loadingContainer.setVisibility(View.GONE);
    definitionContainer.setVisibility(View.VISIBLE);
  }

  public boolean isLoadingVisible() {
    return loadingContainer.getVisibility() == View.VISIBLE;
  }

  public void setOnClickCrossListener(OnClickCrossListener listener) {
    this.listener = listener;
  }

  public void setWordDefinition(WordDefinition wordDefinition) {
    this.wordDefinition = wordDefinition;

    definitionWordTv.setText(wordDefinition.getEntry());

    // Fill view data
    Map<WordDefinition.WordType, String> definitions = wordDefinition.getMeaning();

    String noun = definitions.get(WordDefinition.WordType.NOUN);
    if (!TextUtils.isEmpty(noun)) {
      noun = Cleaner.clean(noun);
      nounDefinitionTv.setText(noun);
      nounContainer.setVisibility(View.VISIBLE);
    } else {
      nounContainer.setVisibility(View.GONE);
    }

    String adjective = definitions.get(WordDefinition.WordType.ADJECTIVE);
    if (!TextUtils.isEmpty(adjective)) {
      adjective = Cleaner.clean(adjective);
      adjectiveDefinitionTv.setText(adjective);
      adjectiveContainer.setVisibility(View.VISIBLE);
    } else {
      adjectiveContainer.setVisibility(View.GONE);
    }

    String verb = definitions.get(WordDefinition.WordType.VERB);
    if (!TextUtils.isEmpty(verb)) {
      verb = Cleaner.clean(verb);
      verbDefinitionTv.setText(verb);
      verbContainer.setVisibility(View.VISIBLE);
    } else {
      verbContainer.setVisibility(View.GONE);
    }

    String adverb = definitions.get(WordDefinition.WordType.ADVERB);
    if (!TextUtils.isEmpty(adverb)) {
      adverb = Cleaner.clean(adverb);
      adverbDefinitionTv.setText(adverb);
      adverbContainer.setVisibility(View.VISIBLE);
    } else {
      adverbContainer.setVisibility(View.GONE);
    }

    if (nounContainer.getVisibility() == View.GONE
        && adjectiveContainer.getVisibility() == View.GONE
        && verbContainer.getVisibility() == View.GONE
        && adverbContainer.getVisibility() == View.GONE) {
      noResultsContainer.setVisibility(View.VISIBLE);
      wordDefinitionsContainer.setVisibility(View.GONE);
    } else {
      noResultsContainer.setVisibility(View.GONE);
      wordDefinitionsContainer.setVisibility(View.VISIBLE);
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////

  private void init() {
    LayoutInflater.from(getContext()).inflate(R.layout.definition_view, this, true);

    this.loadingContainer = findViewById(R.id.definition_view_loading_container);
    this.definitionContainer = findViewById(R.id.definition_view_definition_container);
    this.wordDefinitionsContainer = findViewById(R.id.definition_view_word_definitions_sv);

    this.nounContainer = (LinearLayout) findViewById(R.id.definition_view_noun_container);
    this.verbContainer = (LinearLayout) findViewById(R.id.definition_view_verb_container);
    this.adjectiveContainer = (LinearLayout) findViewById(R.id.definition_view_adjective_container);
    this.adverbContainer = (LinearLayout) findViewById(R.id.definition_view_adverb_container);
    this.noResultsContainer = (TextView) findViewById(R.id.definition_view_no_results_container);

    this.closeBtn = (ImageButton) findViewById(R.id.definition_view_close_button);

    this.definitionWordTv = (TextView) findViewById(R.id.definition_view_title_tv);
    this.nounDefinitionTv = (TextView) findViewById(R.id.definition_view_noun_definition_tv);
    this.verbDefinitionTv = (TextView) findViewById(R.id.definition_view_verb_definition_tv);
    this.adjectiveDefinitionTv = (TextView) findViewById(R.id.definition_view_adjective_definition_tv);
    this.adverbDefinitionTv = (TextView) findViewById(R.id.definition_view_adverb_definition_tv);

    // Configuration of the view
    setOrientation(VERTICAL);
    setClickable(true);

    // Set default click listener
    closeBtn.setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        if (listener != null) {
          listener.onClick(DefinitionView.this);
        }
      }
    });
  }

  ///////////////////////////////////////////////////////////////////////////
  // Helper classes
  ///////////////////////////////////////////////////////////////////////////

  private static class Cleaner {

    private static String CLEANER_PATTERN_STRING = "\\((?:nou|vrb|adj|adv)\\)\\s";
    private static Pattern CLEANER_PATTERN = Pattern.compile(CLEANER_PATTERN_STRING);

    public static String clean(String originalText) {
      return "\u2022\u0020" + originalText.replaceAll(CLEANER_PATTERN_STRING, "")
          .replaceAll("\\n", "\n\u2022\u0020")
          .trim();
    }
  }
}

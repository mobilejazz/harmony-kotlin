package com.worldreader.reader.wr.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.worldreader.core.R;
import com.worldreader.core.application.ui.widget.CheckableImageButton;
import com.worldreader.core.application.ui.widget.discretebar.DiscreteSeekBar;
import com.worldreader.reader.pageturner.net.nightwhistler.pageturner.configuration.Configuration;
import com.worldreader.reader.wr.adapter.ReaderFontFamilies;
import com.worldreader.reader.wr.adapter.ReaderFontFamiliesAdapter;
import com.worldreader.reader.wr.adapter.ReaderFontSizes;
import com.worldreader.reader.wr.adapter.ReaderFontSizesAdapter;
import com.worldreader.reader.wr.helper.BrightnessManager;

import java.util.*;

public class ReaderSettingsDialog extends DialogFragment {

  public static final String TAG = ReaderSettingsDialog.class.getSimpleName();

  private Action action = Action.NOT_MODIFIED;

  private Set<SettingsModified> modifiedSettings = new LinkedHashSet<>();
  private ModifyReaderSettingsListener listener;

  private BrightnessManager brightnessManager;
  private Configuration configuration;

  private CheckableImageButton dayProfileButton;
  private CheckableImageButton nightProfileButton;
  private CheckableImageButton creamProfileButton;
  private DiscreteSeekBar brightnessSeekBar;
  private Spinner fontFamilySpinner;
  private Spinner fontSizesSpinner;

  public enum Action {
    MODIFIED, NOT_MODIFIED
  }

  private enum SettingsModified {
    COLOR_PROFILE, FONT_SIZE, FONT_FAMILY,
  }

  public interface ModifyReaderSettingsListener {

    void onReaderSettingsModified(Action action);
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    final View view = LayoutInflater.from(getActivity()).inflate(R.layout.modify_reader_settings_dialog, null);

    this.dayProfileButton = view.findViewById(R.id.display_options_dialog_day_profile_button);
    this.nightProfileButton = view.findViewById(R.id.display_options_dialog_night_profile_button);
    this.creamProfileButton = view.findViewById(R.id.display_options_dialog_cream_profile_button);
    this.brightnessSeekBar = view.findViewById(R.id.display_options_brightness_seekbar);
    this.fontFamilySpinner = view.findViewById(R.id.display_options_font_family_spinner);
    this.fontSizesSpinner = view.findViewById(R.id.display_options_font_sizes_spinner);

    onSetupViewListeners();

    return new MaterialDialog.Builder(getActivity()).customView(view, false)
        .positiveText(android.R.string.ok)
        .positiveColorRes(R.color.reader_settings_accept)
        .negativeText(android.R.string.cancel)
        .negativeColorRes(R.color.reader_settings_cancel)
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
            if (listener != null) {
              listener.onReaderSettingsModified(action);
              if (action.equals(Action.MODIFIED)) {
                saveModifiedSettings();
              }
            }
          }
        })
        .build();
  }

  private void onSetupViewListeners() {
    // Listeners
    dayProfileButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dayProfileButton.setChecked(true);
        nightProfileButton.setChecked(false);
        creamProfileButton.setChecked(false);
        action = Action.MODIFIED;
        modifiedSettings.add(SettingsModified.COLOR_PROFILE);
      }
    });
    nightProfileButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dayProfileButton.setChecked(false);
        nightProfileButton.setChecked(true);
        creamProfileButton.setChecked(false);
        action = Action.MODIFIED;
        modifiedSettings.add(SettingsModified.COLOR_PROFILE);
      }
    });
    creamProfileButton.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        dayProfileButton.setChecked(false);
        nightProfileButton.setChecked(false);
        creamProfileButton.setChecked(true);
        action = Action.MODIFIED;
        modifiedSettings.add(SettingsModified.COLOR_PROFILE);
      }
    });

    brightnessSeekBar.setProgress(configuration.getBrightness());
    brightnessSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
      @Override public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
        brightnessManager.setBrightness(getActivity().getWindow(), value);
        configuration.setBrightness(value);
      }

      @Override public void onStartTrackingTouch(DiscreteSeekBar seekBar) {
      }

      @Override public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
      }
    });

    // Adapters
    fontFamilySpinner.setAdapter(new ReaderFontFamiliesAdapter(getContext()));
    fontFamilySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        action = Action.MODIFIED;
        modifiedSettings.add(SettingsModified.FONT_FAMILY);
      }

      @Override public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    fontSizesSpinner.setAdapter(new ReaderFontSizesAdapter(getContext()));
    fontSizesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        action = Action.MODIFIED;
        modifiedSettings.add(SettingsModified.FONT_SIZE);
      }

      @Override public void onNothingSelected(AdapterView<?> parent) {
      }
    });

    // State
    switch (this.configuration.getColourProfile()) {
      case DAY:
        dayProfileButton.setChecked(true);
        break;
      case NIGHT:
        nightProfileButton.setChecked(true);
        break;
      case CREAM:
        creamProfileButton.setChecked(true);
        break;
    }

    final ReaderFontSizesAdapter fontSizesAdapter = (ReaderFontSizesAdapter) fontSizesSpinner.getAdapter();
    fontSizesSpinner.setSelection(fontSizesAdapter.getPosition(ReaderFontSizes.getByFontSize(configuration.getTextSize())));

    final ReaderFontFamiliesAdapter fontFamiliesAdapter = (ReaderFontFamiliesAdapter) fontFamilySpinner.getAdapter();
    fontFamilySpinner.setSelection(fontFamiliesAdapter.getPosition(ReaderFontFamilies.getByFontName(configuration.getSerifFontFamilyString())));
  }

  private void saveModifiedSettings() {
    if (modifiedSettings.contains(SettingsModified.COLOR_PROFILE)) {
      if (dayProfileButton.isChecked()) {
        configuration.setColourProfile(Configuration.ColorProfile.DAY);
      } else if (nightProfileButton.isChecked()) {
        configuration.setColourProfile(Configuration.ColorProfile.NIGHT);
      } else {
        configuration.setColourProfile(Configuration.ColorProfile.CREAM);
      }
    }

    if (modifiedSettings.contains(SettingsModified.FONT_SIZE)) {
      configuration.setTextSize(((ReaderFontSizes) fontSizesSpinner.getSelectedItem()).getSize());
    }

    if (modifiedSettings.contains(SettingsModified.FONT_FAMILY)) {
      configuration.setSerifFontFamily(((ReaderFontFamilies) fontFamilySpinner.getSelectedItem()).getFontFamily());
    }
  }

  public void setOnModifyReaderSettingsListener(ModifyReaderSettingsListener listener) {
    this.listener = listener;
  }

  public void setBrightnessManager(final BrightnessManager brightnessManager) {
    this.brightnessManager = brightnessManager;
  }

  public void setConfiguration(final Configuration configuration) {
    this.configuration = configuration;
  }
}

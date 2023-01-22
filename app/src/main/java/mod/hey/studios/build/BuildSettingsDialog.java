package mod.hey.studios.build;

import static mod.SketchwareUtil.getDip;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;
import com.sketchware.remod.R;

import mod.SketchwareUtil;
import mod.hey.studios.util.Helper;

public class BuildSettingsDialog {

    private final Activity activity;
    private final BuildSettings settings;
    private View[] viewArr;

    public BuildSettingsDialog(Activity activity, String sc_id) {
        this.activity = activity;
        settings = new BuildSettings(sc_id);
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        View inflate = activity.getLayoutInflater().inflate(R.layout.project_config_layout, null);

        ImageView icon = inflate.findViewById(R.id.project_config_icon);
        TextView title = inflate.findViewById(R.id.project_config_title);
        LinearLayout contentView = inflate.findViewById(R.id.project_config_pref_layout);
        TextView cancel = inflate.findViewById(R.id.text_cancel);
        TextView save = inflate.findViewById(R.id.text_save);

        icon.setImageResource(R.drawable.side_menu_setting_icon_over);
        title.setText("Build Settings");

        setupSettings(contentView);

        builder.setView(inflate);

        AlertDialog buildSettingsDialog = builder.create();
        buildSettingsDialog.show();
        cancel.setOnClickListener(Helper.getDialogDismissListener(buildSettingsDialog));
        save.setOnClickListener(v -> {
            settings.setValues(viewArr);
            buildSettingsDialog.dismiss();
        });
    }
    
    private void setupSettings(LinearLayout contentView) {
        viewArr = new View[] {
                addInputPref(BuildSettings.SETTING_ANDROID_JAR_PATH, "", "Custom android.jar", EditorInfo.TYPE_CLASS_TEXT, contentView),
                addInputPref(BuildSettings.SETTING_CLASSPATH, "", "Classpath (separated by :)", EditorInfo.TYPE_CLASS_TEXT, contentView),
                addSingleChoicePref(BuildSettings.SETTING_DEXER, new String[]{"Dx", "D8"}, "Dx", "Dexer", contentView),
                addSingleChoicePref(BuildSettings.SETTING_JAVA_VERSION, BuildSettingsDialogBridge.getAvailableJavaVersions(), "1.7", "Java version", contentView),
                addSingleChoicePref(BuildSettings.SETTING_SHRINKER, BuildSettingsDialogBridge.getAvailableShrinkers(), BuildSettings.SETTING_SHRINKER_PROGUARD, "Code Shrinker", contentView),
                addTogglePref(BuildSettings.SETTING_NO_WARNINGS, true, "Hide warnings in error log", 12, contentView),
                addTogglePref(BuildSettings.SETTING_NO_HTTP_LEGACY, false, "Don't include http-legacy-28.dex", 12, contentView),
                addTogglePref(BuildSettings.SETTING_ENABLE_LOGCAT, true, "Enable debug logcat logs viewable in Logcat Reader. Not enabled in exported AABs/APKs.", 12, contentView)
        };
        
        if (Build.VERSION.SDK_INT <= 26) {
            viewArr = new View[] {
                addInputPref(BuildSettings.SETTING_ANDROID_JAR_PATH, "", "Custom android.jar", EditorInfo.TYPE_CLASS_TEXT, contentView),
                addInputPref(BuildSettings.SETTING_CLASSPATH, "", "Classpath (separated by :)", EditorInfo.TYPE_CLASS_TEXT, contentView),
                addTogglePref(BuildSettings.SETTING_NO_WARNINGS, true, "Hide warnings in error log", 12, contentView),
                addTogglePref(BuildSettings.SETTING_NO_HTTP_LEGACY, false, "Don't include http-legacy-28.dex", 12, contentView),
                addTogglePref(BuildSettings.SETTING_ENABLE_LOGCAT, true, "Enable debug logcat logs viewable in Logcat Reader. Not enabled in exported AABs/APKs.", 12, contentView)
            };
        }
    }

    private RadioGroup addSingleChoicePref(String key, String[] choices, String defaultValue, String title, LinearLayout addTo) {
        TextView textView = new TextView(activity);

        textView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText(title);
        textView.setTextSize(14f);
        textView.setTextColor(0xff008DCD);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setPadding(
                0,
                (int) getDip(12),
                0,
                (int) getDip(12)
        );

        addTo.addView(textView);

        RadioGroup radioGroup = new RadioGroup(activity);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        radioGroup.setTag(key);
        radioGroup.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        addTo.addView(radioGroup);

        for (String choice : choices) {
            RadioButton radioButton = new RadioButton(activity);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1.0f);
            radioButton.setLayoutParams(layoutParams);
            radioButton.setId(View.generateViewId());
            radioButton.setText(choice);
            radioButton.setTextColor(0xff000000);
            radioButton.setTextSize(16f);

            if (settings.getValue(key, defaultValue).equals(choice)) {
                radioButton.setChecked(true);
            }

            radioGroup.addView(radioButton);
            radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isChecked) return;

                if (key.equals(BuildSettings.SETTING_JAVA_VERSION)) {
                    BuildSettingsDialogBridge.handleJavaVersionChange(choice);
                } else if (key.equals(BuildSettings.SETTING_DEXER)) {
                    BuildSettingsDialogBridge.handleDexerChange(choice);
                }
            });
        }
        return radioGroup;
    }

    private CheckBox addTogglePref(String key, boolean defaultValue, String label, int leftMargin, LinearLayout addTo) {
        CheckBox checkBox = new CheckBox(activity);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(
                0,
                (int) getDip(leftMargin),
                0,
                0
        );
        checkBox.setLayoutParams(layoutParams);

        addTo.addView(checkBox);

        String value = settings.getValue(key, defaultValue ? "true" : "false");
        checkBox.setText(label);
        checkBox.setChecked(value.equals("true"));
        checkBox.setTextColor(0xff000000);
        checkBox.setPadding(
                (int) getDip(4),
                (int) getDip(8),
                (int) getDip(8),
                (int) getDip(8)
        );
        checkBox.setTag(key);

        if (key.equals(BuildSettings.SETTING_NO_HTTP_LEGACY)) {
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    SketchwareUtil.toast("Note that this option may cause issues if RequestNetwork component is used");
                }
            });
        }

        return checkBox;
    }

    private EditText addInputPref(String key, String defaultValue, String hint, int inputType, LinearLayout addTo) {
        TextInputLayout textInputLayout = new TextInputLayout(activity);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(
                0,
                (int) getDip(12),
                0,
                0
        );

        textInputLayout.setLayoutParams(layoutParams);

        addTo.addView(textInputLayout);

        EditText editText = new EditText(activity);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        editText.setPadding(
                (int) getDip(4),
                (int) getDip(8),
                (int) getDip(8),
                (int) getDip(8)
        );
        editText.setTextSize(16f);
        editText.setTextColor(0xff000000);
        editText.setHint(hint);
        editText.setHintTextColor(0xff607d8b);
        editText.setText(settings.getValue(key, defaultValue));
        editText.setTag(key);
        editText.setInputType(inputType);

        textInputLayout.addView(editText);

        return editText;
    }
}

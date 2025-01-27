package com.besome.sketch.projects;

import static mod.hey.studios.util.ProjectFile.getDefaultColor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.core.content.FileProvider;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.besome.sketch.lib.base.BaseAppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import a.a.a.GB;
import a.a.a.LB;
import a.a.a.MA;
import a.a.a.UB;
import a.a.a.VB;
import a.a.a.Zx;
import a.a.a.aB;
import a.a.a.lC;
import a.a.a.mB;
import a.a.a.nB;
import a.a.a.oB;
import a.a.a.wB;
import a.a.a.wq;
import a.a.a.yB;
import mod.hey.studios.project.ProjectSettings;
import mod.hey.studios.util.Helper;
import mod.hey.studios.util.ProjectFile;
import mod.hilal.saif.activities.tools.ConfigActivity;
import pro.sketchware.R;
import pro.sketchware.activities.iconcreator.IconCreatorActivity;
import pro.sketchware.control.VersionDialog;
import pro.sketchware.databinding.MyprojectSettingBinding;
import pro.sketchware.utility.FileUtil;

public class MyProjectSettingActivity extends BaseAppCompatActivity implements View.OnClickListener {

    public MyprojectSettingBinding binding;

    private static final int REQUEST_CODE_CREATE_ICON = 200212;
    private final String[] themeColorKeys = {"color_accent", "color_primary", "color_primary_dark", "color_control_highlight", "color_control_normal"};
    private final String[] themeColorLabels = {"colorAccent", "colorPrimary", "colorPrimaryDark", "colorControlHighlight", "colorControlNormal"};
    private final int[] projectThemeColors = new int[themeColorKeys.length];
    private UB projectPackageNameValidator;
    private VB projectNameValidator;
    private LB projectAppNameValidator;
    private boolean projectHasCustomIcon = false;
    private boolean updatingExistingProject = false;
    private int projectVersionCode = 1;
    private int projectVersionNameFirstPart;
    private int projectVersionNameSecondPart;
    private boolean shownPackageNameChangeWarning;
    private boolean isIconAdaptive;
    private Bitmap icon;
    private String sc_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MyprojectSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(arg0 -> onBackPressed());

        if (!isStoragePermissionGranted()) finish();

        sc_id = getIntent().getStringExtra("sc_id");
        updatingExistingProject = getIntent().getBooleanExtra("is_update", false);

        binding.verCode.setSelected(true);
        binding.verName.setSelected(true);

        binding.appIconLayout.setOnClickListener(this);
        binding.verCodeHolder.setOnClickListener(this);
        binding.verNameHolder.setOnClickListener(this);
        binding.imgThemeColorHelp.setOnClickListener(this);
        binding.okButton.setOnClickListener(this);
        binding.cancel.setOnClickListener(this);

        binding.tilAppName.setHint(Helper.getResString(R.string.myprojects_settings_hint_enter_application_name));
        binding.tilPackageName.setHint(Helper.getResString(R.string.myprojects_settings_hint_enter_package_name));
        binding.tilProjectName.setHint(Helper.getResString(R.string.myprojects_settings_hint_enter_project_name));

        projectAppNameValidator = new LB(getApplicationContext(), binding.tilAppName);
        projectPackageNameValidator = new UB(getApplicationContext(), binding.tilPackageName);
        projectNameValidator = new VB(getApplicationContext(), binding.tilProjectName);
        binding.tilPackageName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (!shownPackageNameChangeWarning && !((EditText) v).getText().toString().trim().contains("com.my.newproject")) {
                    showPackageNameChangeWarning();
                }
            }
        });

        projectThemeColors[0] = getDefaultColor(ProjectFile.COLOR_ACCENT);
        projectThemeColors[1] = getDefaultColor(ProjectFile.COLOR_PRIMARY);
        projectThemeColors[2] = getDefaultColor(ProjectFile.COLOR_PRIMARY_DARK);
        projectThemeColors[3] = getDefaultColor(ProjectFile.COLOR_CONTROL_HIGHLIGHT);
        projectThemeColors[4] = getDefaultColor(ProjectFile.COLOR_CONTROL_NORMAL);

        for (int i = 0; i < themeColorKeys.length; i++) {
            ThemeColorView colorView = new ThemeColorView(this, i);
            colorView.name.setText(themeColorLabels[i]);
            colorView.color.setBackgroundColor(Color.WHITE);
            binding.layoutThemeColors.addView(colorView);
            colorView.setOnClickListener(v -> {
                if (!mB.a()) {
                    pickColor(v, (Integer) v.getTag());
                }
            });
        }
        if (updatingExistingProject) {
            /* Set the dialog's title & save button label */
            binding.toolbar.setTitle("Project Settings");
            HashMap<String, Object> metadata = lC.b(sc_id);
            binding.etPackageName.setText(yB.c(metadata, "my_sc_pkg_name"));
            binding.etProjectName.setText(yB.c(metadata, "my_ws_name"));
            binding.etAppName.setText(yB.c(metadata, "my_app_name"));
            binding.okButton.setText("Save changes");
            projectVersionCode = parseInt(yB.c(metadata, "sc_ver_code"), 1);
            parseVersion(yB.c(metadata, "sc_ver_name"));
            binding.verCode.setText(yB.c(metadata, "sc_ver_code"));
            binding.verName.setText(yB.c(metadata, "sc_ver_name"));
            projectHasCustomIcon = yB.a(metadata, "custom_icon");
            if (projectHasCustomIcon) {
                if (Build.VERSION.SDK_INT >= 24) {
                    binding.appIcon.setImageURI(FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", getCustomIcon()));
                } else {
                    binding.appIcon.setImageURI(Uri.fromFile(getCustomIcon()));
                }
            }

            for (int i = 0; i < themeColorKeys.length; i++) {
                projectThemeColors[i] = yB.a(metadata, themeColorKeys[i], projectThemeColors[i]);
            }
        } else {
            /* Set the dialog's title & create button label */
            String newProjectName = getIntent().getStringExtra("my_ws_name");
            String newProjectPackageName = getIntent().getStringExtra("my_sc_pkg_name");
            if (sc_id == null || sc_id.isEmpty()) {
                sc_id = lC.b();
                newProjectName = lC.c();
                newProjectPackageName = "com.my." + newProjectName.toLowerCase();
            }
            binding.etPackageName.setText(newProjectPackageName);
            binding.etProjectName.setText(newProjectName);
            binding.etAppName.setText(getIntent().getStringExtra("my_app_name"));

            String newProjectVersionCode = getIntent().getStringExtra("sc_ver_code");
            String newProjectVersionName = getIntent().getStringExtra("sc_ver_name");
            if (newProjectVersionCode == null || newProjectVersionCode.isEmpty()) {
                newProjectVersionCode = "1";
            }
            if (newProjectVersionName == null || newProjectVersionName.isEmpty()) {
                newProjectVersionName = "1.0";
            }
            projectVersionCode = parseInt(newProjectVersionCode, 1);
            parseVersion(newProjectVersionName);
            binding.verCode.setText(newProjectVersionCode);
            binding.verName.setText(newProjectVersionName);
            projectHasCustomIcon = getIntent().getBooleanExtra("custom_icon", false);
            if (projectHasCustomIcon) {
                if (Build.VERSION.SDK_INT >= 24) {
                    binding.appIcon.setImageURI(FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", getCustomIcon()));
                } else {
                    binding.appIcon.setImageURI(Uri.fromFile(getCustomIcon()));
                }
            }
        }
        syncThemeColors();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }

        if (requestCode == REQUEST_CODE_CREATE_ICON && resultCode == RESULT_OK) {
            if (data.getParcelableExtra("appIco") != null) {
                icon = data.getParcelableExtra("appIco");

                isIconAdaptive = data.getBooleanExtra("isIconAdaptive", false);
                binding.appIcon.setImageBitmap(icon);
                projectHasCustomIcon = true;
            }
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.app_icon_layout) {
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), IconCreatorActivity.class);
            intent.putExtra("sc_id", sc_id);
            startActivityForResult(intent, REQUEST_CODE_CREATE_ICON);
        } else if (id == R.id.ok_button) {
            mB.a(v);
            if (isInputValid()) {
                new SaveProjectAsyncTask(getApplicationContext()).execute();
                if (icon != null) saveBitmapTo(icon, getCustomIconPath());
            }
        } else if (id == R.id.cancel) {
            finish();
        } else if (id == R.id.img_theme_color_help) {
            animateLayoutChanges(binding.getRoot());
            if (binding.imgColorGuide.getVisibility() == View.VISIBLE) {
                binding.imgColorGuide.setVisibility(View.GONE);
            } else {
                binding.imgColorGuide.setVisibility(View.VISIBLE);
            }
        } else if (id == R.id.ver_code_holder || id == R.id.ver_name_holder) {
            if (ConfigActivity.isSettingEnabled(ConfigActivity.SETTING_USE_NEW_VERSION_CONTROL)) {
                new VersionDialog(this).show();
            } else {
                showOldVersionControlDialog();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isStoragePermissionGranted()) {
            finish();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        oB oBVar = new oB();
        oBVar.f(wq.e() + File.separator + sc_id);
        oBVar.f(wq.g() + File.separator + sc_id);
        oBVar.f(wq.t() + File.separator + sc_id);
        oBVar.f(wq.d() + File.separator + sc_id);
        File o = getCustomIcon();
        if (!o.exists()) {
            try {
                o.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showOldVersionControlDialog() {
        aB dialog = new aB(this);
        dialog.a(R.drawable.numbers_48);
        dialog.b(Helper.getResString(R.string.myprojects_settings_version_control_title));
        View view = wB.a(getApplicationContext(), R.layout.property_popup_version_control);
        ((TextView) view.findViewById(R.id.tv_code)).setText(Helper.getResString(R.string.myprojects_settings_version_control_title_code));
        ((TextView) view.findViewById(R.id.tv_name)).setText(Helper.getResString(R.string.myprojects_settings_version_control_title_name));

        NumberPicker versionCodePicker = view.findViewById(R.id.version_code);
        NumberPicker versionNameFirstPartPicker = view.findViewById(R.id.version_name1);
        NumberPicker versionNameSecondPartPicker = view.findViewById(R.id.version_name2);

        versionCodePicker.setWrapSelectorWheel(false);
        versionNameFirstPartPicker.setWrapSelectorWheel(false);
        versionNameSecondPartPicker.setWrapSelectorWheel(false);

        int versionCode = Integer.parseInt(binding.verCode.getText().toString());
        int versionCodeMinimum = versionCode - 5;
        int versionNameFirstPartMinimum = 1;
        if (versionCodeMinimum <= 0) {
            versionCodeMinimum = 1;
        }
        versionCodePicker.setMinValue(versionCodeMinimum);
        versionCodePicker.setMaxValue(versionCode + 5);
        versionCodePicker.setValue(versionCode);

        String[] split = binding.verName.getText().toString().split("\\.");
        AtomicInteger projectNewVersionNameFirstPart = new AtomicInteger(parseInt(split[0], 1));
        AtomicInteger projectNewVersionNameSecondPart = new AtomicInteger(parseInt(split[1], 0));
        if (projectNewVersionNameFirstPart.get() - 5 > 0) {
            versionNameFirstPartMinimum = projectNewVersionNameFirstPart.get() - 5;
        }
        versionNameFirstPartPicker.setMinValue(versionNameFirstPartMinimum);
        versionNameFirstPartPicker.setMaxValue(projectNewVersionNameFirstPart.get() + 5);
        versionNameFirstPartPicker.setValue(projectNewVersionNameFirstPart.get());

        versionNameSecondPartPicker.setMinValue(Math.max(projectNewVersionNameSecondPart.get() - 20, 0));
        versionNameSecondPartPicker.setMaxValue(projectNewVersionNameSecondPart.get() + 20);
        versionNameSecondPartPicker.setValue(projectNewVersionNameSecondPart.get());
        dialog.a(view);

        versionCodePicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (oldVal > newVal && newVal < projectVersionCode) {
                picker.setValue(projectVersionCode);
            }
        });
        versionNameFirstPartPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            projectNewVersionNameFirstPart.set(newVal);
            if (oldVal > newVal) {
                if (newVal < projectVersionNameFirstPart) {
                    versionCodePicker.setValue(projectVersionNameFirstPart);
                }
                if (projectNewVersionNameFirstPart.get() == projectVersionNameFirstPart || projectNewVersionNameSecondPart.get() <= projectVersionNameSecondPart) {
                    versionNameSecondPartPicker.setValue(projectVersionNameSecondPart);
                    projectNewVersionNameSecondPart.set(projectVersionNameSecondPart);
                }
            }
        });
        versionNameSecondPartPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            projectNewVersionNameSecondPart.set(newVal);
            if (oldVal > newVal && newVal < projectVersionNameSecondPart && projectNewVersionNameFirstPart.get() < projectVersionNameFirstPart) {
                picker.setValue(projectVersionNameSecondPart);
            }
        });
        dialog.b(Helper.getResString(R.string.common_word_save), v -> {
            if (!mB.a()) {
                binding.verCode.setText(String.valueOf(versionCodePicker.getValue()));
                binding.verName.setText(projectNewVersionNameFirstPart + "." + projectNewVersionNameSecondPart);
                dialog.dismiss();
            }
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    private void syncThemeColors() {
        for (int i = 0; i < projectThemeColors.length; i++) {
            ((ThemeColorView) binding.layoutThemeColors.getChildAt(i)).color.setBackgroundColor(projectThemeColors[i]);
        }
    }

    private void parseVersion(String toParse) {
        try {
            String[] split = toParse.split("\\.");
            projectVersionNameFirstPart = parseInt(split[0], 1);
            projectVersionNameSecondPart = parseInt(split[1], 0);
        } catch (Exception ignored) {
        }
    }

    private void pickColor(View anchorView, int colorIndex) {
        Zx zx = new Zx(this, projectThemeColors[colorIndex], false, false);
        zx.a((new Zx.b() {
            @Override
            public void a(int var1) {
                projectThemeColors[colorIndex] = var1;
                syncThemeColors();
            }

            @Override
            public void a(String var1, int var2) {
                projectThemeColors[colorIndex] = var2;
                syncThemeColors();
            }
        }
        ));
        zx.showAtLocation(anchorView, Gravity.CENTER, 0, 0);
    }

    private void showResetIconConfirmation() {
        aB dialog = new aB(this);
        dialog.b(Helper.getResString(R.string.common_word_settings));
        dialog.a(R.drawable.default_icon);
        dialog.a(Helper.getResString(R.string.myprojects_settings_confirm_reset_icon));
        dialog.b(Helper.getResString(R.string.common_word_reset), v -> {
            binding.appIcon.setImageResource(R.drawable.default_icon);
            projectHasCustomIcon = false;
            dialog.dismiss();
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    private File getCustomIcon() {
        return new File(getCustomIconPath());
    }

    private String getCustomIconPath() {
        return wq.e() + File.separator + sc_id + File.separator + "icon.png";
    }


    private String getTempIconsFolderPath(String foldername) {
        return wq.e() + File.separator + sc_id + File.separator + foldername;
    }

    private String getIconsFolderPath() {
        return wq.e() + File.separator + sc_id + File.separator + "mipmaps" + File.separator;
    }

    private boolean isInputValid() {
        return projectPackageNameValidator.b() && projectNameValidator.b() && projectAppNameValidator.b();
    }

    private void showPackageNameChangeWarning() {
        shownPackageNameChangeWarning = true;
        aB dialog = new aB(this);
        dialog.b(Helper.getResString(R.string.common_word_warning));
        dialog.a(R.drawable.break_warning_96_red);
        dialog.a(Helper.getResString(R.string.myprojects_settings_message_package_rename));
        dialog.b(Helper.getResString(R.string.common_word_ok), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    private int parseInt(String input, int fallback) {
        try {
            return Integer.parseInt(input);
        } catch (Exception unused) {
            return fallback;
        }
    }

    public static void saveBitmapTo(Bitmap bitmap, String path) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
        } catch (IOException ignored) {
        }
    }

    private static class ThemeColorView extends LinearLayout {

        private TextView color;
        private TextView name;

        public ThemeColorView(Context context, int tag) {
            super(context);
            initialize(context, tag);
        }

        private void initialize(Context context, int tag) {
            setTag(tag);
            wB.a(context, this, R.layout.myproject_color);
            color = findViewById(R.id.color);
            name = findViewById(R.id.name);
        }
    }

    private class SaveProjectAsyncTask extends MA {

        public SaveProjectAsyncTask(Context context) {
            super(context);
            addTask(this);
            k();
        }

        @Override
        public void a() {
            h();
            Intent intent = getIntent();
            intent.putExtra("sc_id", sc_id);
            intent.putExtra("is_new", !updatingExistingProject);
            intent.putExtra("index", intent.getIntExtra("index", -1));
            setResult(RESULT_OK, intent);
            finish();
        }

        @Override
        public void b() {
            HashMap<String, Object> data = new HashMap<>();
            data.put("sc_id", sc_id);
            data.put("my_sc_pkg_name", binding.etPackageName.getText().toString());
            data.put("my_ws_name", binding.etProjectName.getText().toString());
            data.put("my_app_name", binding.etAppName.getText().toString());
            if (updatingExistingProject) {
                data.put("custom_icon", projectHasCustomIcon);
                data.put("isIconAdaptive", isIconAdaptive);
                data.put("sc_ver_code", binding.verCode.getText().toString());
                data.put("sc_ver_name", binding.verName.getText().toString());
                data.put("sketchware_ver", GB.d(getApplicationContext()));
                for (int i = 0; i < themeColorKeys.length; i++) {
                    data.put(themeColorKeys[i], projectThemeColors[i]);
                }
                lC.b(sc_id, data);
                updateProjectResourcesContents(data);
            } else {
                data.put("my_sc_reg_dt", new nB().a("yyyyMMddHHmmss"));
                data.put("custom_icon", projectHasCustomIcon);
                data.put("isIconAdaptive", isIconAdaptive);
                data.put("sc_ver_code", binding.verCode.getText().toString());
                data.put("sc_ver_name", binding.verName.getText().toString());
                data.put("sketchware_ver", GB.d(getApplicationContext()));
                for (int i = 0; i < themeColorKeys.length; i++) {
                    data.put(themeColorKeys[i], projectThemeColors[i]);
                }
                lC.a(sc_id, data);
                updateProjectResourcesContents(data);
                wq.a(getApplicationContext(), sc_id);
                new oB().b(wq.b(sc_id));
                new ProjectSettings(sc_id).setValue(ProjectSettings.SETTING_NEW_XML_COMMAND, ProjectSettings.SETTING_GENERIC_VALUE_TRUE);
            }
            try {
                FileUtil.deleteFile(getTempIconsFolderPath("mipmaps" + File.separator));
                FileUtil.copyDirectory(new File(getTempIconsFolderPath("temp_icons" + File.separator)), new File(getIconsFolderPath()));
                FileUtil.deleteFile(getTempIconsFolderPath("temp_icons" + File.separator));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private void updateProjectResourcesContents(HashMap<String, Object> data) {
            String baseDir = wq.b(sc_id) + "/files/resource/values/";
            String stringsFilePath = baseDir + "strings.xml";
            String colorsFilePath = baseDir + "colors.xml";
            String newAppName = Objects.requireNonNull(data.get("my_app_name")).toString();

            if (FileUtil.isExistFile(stringsFilePath)) {
                String xmlContent = FileUtil.readFile(stringsFilePath);
                xmlContent = xmlContent.replaceAll("(<string\\s+name=\"app_name\">)(.*?)(</string>)", "$1" + newAppName + "$3");
                FileUtil.writeFile(stringsFilePath, xmlContent);
            }

            if (FileUtil.isExistFile(colorsFilePath)) {
                String xmlContent = FileUtil.readFile(colorsFilePath);
                for (int i = 0; i < themeColorKeys.length; i++) {
                    String colorName = themeColorLabels[i];
                    String newColor = String.format("#%06X", (0xFFFFFF & projectThemeColors[i]));
                    xmlContent = xmlContent.replaceAll("(<color\\s+name=\"" + colorName + "\">)(.*?)(</color>)", "$1" + newColor + "$3");
                }
                FileUtil.writeFile(colorsFilePath, xmlContent);
            }

        }

        @Override
        public void a(String str) {
            h();
        }

    }

    private void animateLayoutChanges(View view) {
        var autoTransition = new AutoTransition();
        autoTransition.setDuration((short) 200);
        TransitionManager.beginDelayedTransition((ViewGroup) view, autoTransition);
    }
}

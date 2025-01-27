package pro.sketchware.activities.resources.editors;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.SearchView;

import com.besome.sketch.lib.base.BaseAppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import a.a.a.aB;
import a.a.a.lC;
import a.a.a.wq;

import a.a.a.yq;
import a.a.a.jC;

import mod.hey.studios.code.SrcCodeEditor;
import mod.hey.studios.util.Helper;
import pro.sketchware.R;
import pro.sketchware.activities.resources.editors.fragments.ArraysEditor;
import pro.sketchware.activities.resources.editors.models.ColorModel;
import pro.sketchware.databinding.ResourcesEditorImportDialogBinding;
import pro.sketchware.databinding.ResourcesEditorsActivityBinding;
import pro.sketchware.activities.resources.editors.adapters.EditorsAdapter;
import pro.sketchware.activities.resources.editors.fragments.ColorsEditor;
import pro.sketchware.activities.resources.editors.fragments.StringsEditor;
import pro.sketchware.activities.resources.editors.fragments.StylesEditor;
import pro.sketchware.activities.resources.editors.fragments.ThemesEditor;
import pro.sketchware.databinding.ResourcesVariantSelectorDialogBinding;
import pro.sketchware.utility.FileUtil;
import pro.sketchware.utility.PropertiesUtil;
import pro.sketchware.utility.SketchwareUtil;
import pro.sketchware.utility.UI;

public class ResourcesEditorActivity extends BaseAppCompatActivity {

    private ResourcesEditorsActivityBinding binding;

    private MaterialAlertDialogBuilder builder;

    public yq yq;

    public boolean isComingFromSrcCodeEditor;

    public String sc_id;
    public String variant;
    private final String variantFullNameStarts = "values-";
    public String stringsFilePath;
    public String colorsFilePath;
    public String stylesFilePath;
    public String themesFilePath;
    public String arrayFilePath;

    public StringsEditor stringsEditor;
    public ColorsEditor colorsEditor;
    public StylesEditor stylesEditor;
    public ThemesEditor themesEditor;
    public ArraysEditor arraysEditor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupUI();
        initializeManagers();
        initializeEditors();
        setupListeners();
        setupBackPressedHandler();
        initializeBackgroundTask("");
    }

    private void setupUI() {
        binding = ResourcesEditorsActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.topAppBar);
        binding.viewPager.setOffscreenPageLimit(4);
    }

    private void initializeManagers() {
        sc_id = getIntent().getStringExtra("sc_id");

        yq = new yq(getApplicationContext(), sc_id);
        yq.a(jC.c(sc_id), jC.b(sc_id), jC.a(sc_id), false);
    }

    private void initializeEditors() {
        stringsEditor = new StringsEditor(this);
        colorsEditor = new ColorsEditor(this);
        stylesEditor = new StylesEditor(this);
        themesEditor = new ThemesEditor();
        arraysEditor = new ArraysEditor(this);
    }

    private void initializeBackgroundTask(String variant) {
        this.variant = variant;
        String baseDir = String.format("%s/files/resource/values%s/", wq.b(sc_id), variant);
        stringsFilePath = baseDir + "strings.xml";
        colorsFilePath = baseDir + "colors.xml";
        stylesFilePath = baseDir + "styles.xml";
        themesFilePath = baseDir + "themes.xml";
        arrayFilePath = baseDir + "arrays.xml";

        setupViewPager();
        startBackgroundTask();
    }

    private void setupListeners() {
        binding.topAppBar.setNavigationOnClickListener(Helper.getBackPressedClickListener(this));

        binding.fab.setOnClickListener(view -> {
            int currentItem = binding.viewPager.getCurrentItem();
            switch (currentItem) {
                case 0 -> stringsEditor.addStringDialog();
                case 1 -> colorsEditor.showColorEditDialog(null, -1);
                case 2 -> stylesEditor.showAddStyleDialog();
                case 3 -> themesEditor.showAddThemeDialog();
                case 4 -> arraysEditor.showAddArrayDialog();
            }
        });
    }

    private void startBackgroundTask() {
        k();

        Executors.newSingleThreadExecutor().execute(() -> new Handler(Looper.getMainLooper()).postDelayed(() -> {
            loadDataInBackground();
            runOnUiThread(this::h);
        }, 1000));
    }

    private void loadDataInBackground() {
        stringsEditor.updateStringsList(stringsFilePath, 0, false);
        colorsEditor.updateColorsList(colorsFilePath, 0, false);
        stylesEditor.updateStylesList(stylesFilePath, 0, false);
        themesEditor.updateThemesList(themesFilePath, 0, false);
        arraysEditor.updateArraysList(arrayFilePath, 0, false);
    }

    public void checkForInvalidResources() {
        if (stringsEditor.stringsEditorManager.isDataLoadingFailed) {
            showLoadFailedDialog("strings.xml", stringsFilePath);
            return;
        }
        if (colorsEditor.colorsEditorManager.isDataLoadingFailed) {
            showLoadFailedDialog("colors.xml", colorsFilePath);
            return;
        }
        if (stylesEditor.stylesEditorManager.isDataLoadingFailed) {
            showLoadFailedDialog("styles.xml", stylesFilePath);
            return;
        }
        if (themesEditor.themesEditorManager.isDataLoadingFailed) {
            showLoadFailedDialog("themes.xml", themesFilePath);
        }

        if (arraysEditor.arraysEditorManager.isDataLoadingFailed) {
            showLoadFailedDialog("arrays.xml", arrayFilePath);
        }
    }

    private void goToCodeEditor(String title, String contentPath) {
        builder = null;
        isComingFromSrcCodeEditor = true;
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), SrcCodeEditor.class);
        intent.putExtra("title", title);
        intent.putExtra("content", contentPath);
        startActivity(intent);
    }

    private void showLoadFailedDialog(String title, String contentPath) {
        if (builder != null) return;
        builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(Helper.getResString(R.string.resources_manager_xml_load_failed_title))
                .setMessage(String.format(Helper.getResString(R.string.resources_manager_xml_load_failed_message), title))
                .setPositiveButton("Open code editor", (dialog, which) -> goToCodeEditor(title, contentPath))
                .setNegativeButton(Helper.getResString(R.string.common_word_exit), ((dialogInterface, i) -> finish()))
                .setCancelable(false)
                .create()
                .show();
    }

    private void setupBackPressedHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ArrayList<String> unsavedFiles = getUnsavedFilesList();

                if (!unsavedFiles.isEmpty()) {
                    String files = formatUnsavedFilesList(unsavedFiles);
                    String message = String.format(Helper.getResString(R.string.resources_editor_unsaved_changes_message), files);

                    new MaterialAlertDialogBuilder(ResourcesEditorActivity.this)
                            .setTitle(Helper.getResString(R.string.common_word_warning))
                            .setMessage(message)
                            .setPositiveButton(Helper.getResString(R.string.design_quit_button_save_and_exit), (dialog, which) -> {
                                saveEditorsChanges();
                                finish();
                            })
                            .setNegativeButton(Helper.getResString(R.string.common_word_exit), (dialog, which) -> finish())
                            .setNeutralButton(Helper.getResString(R.string.common_word_cancel), null)
                            .create()
                            .show();
                    return;
                }

                setResult(RESULT_OK);
                finish();
            }
        });
    }

    private String formatUnsavedFilesList(ArrayList<String> unsavedFiles) {
        if (unsavedFiles.size() == 1) {
            return unsavedFiles.get(0);
        } else if (unsavedFiles.size() == 2) {
            return String.join(" and ", unsavedFiles);
        } else {
            String lastFile = unsavedFiles.remove(unsavedFiles.size() - 1);
            return String.join(", ", unsavedFiles) + ", and " + lastFile;
        }
    }

    private ArrayList<String> getUnsavedFilesList() {
        ArrayList<String> unsavedFiles = new ArrayList<>();

        if (stringsEditor.hasUnsavedChanges) {
            unsavedFiles.add("strings.xml");
        }
        if (colorsEditor.hasUnsavedChanges) {
            unsavedFiles.add("colors.xml");
        }
        if (stylesEditor.hasUnsavedChanges) {
            unsavedFiles.add("styles.xml");
        }
        if (themesEditor.hasUnsavedChanges) {
            unsavedFiles.add("themes.xml");
        }
        if (arraysEditor.hasUnsavedChanges) {
            unsavedFiles.add("arrays.xml");
        }
        return unsavedFiles;
    }
    @Override
    public void onResume() {
        if (isComingFromSrcCodeEditor) {
            int currentItem = binding.viewPager.getCurrentItem();

            if (currentItem == 0 || stringsEditor.stringsEditorManager.isDataLoadingFailed) {
                stringsEditor.updateStringsList(stringsFilePath, 0, false);
            }

            if (currentItem == 1 || colorsEditor.colorsEditorManager.isDataLoadingFailed) {
                colorsEditor.updateColorsList(colorsFilePath, 0, false);
            }

            if (currentItem == 2 || stylesEditor.stylesEditorManager.isDataLoadingFailed) {
                stylesEditor.updateStylesList(stylesFilePath, 0, false);
            }

            if (currentItem == 3 || themesEditor.themesEditorManager.isDataLoadingFailed) {
                themesEditor.updateThemesList(themesFilePath, 0, false);
            }

            if (currentItem == 4 || arraysEditor.arraysEditorManager.isDataLoadingFailed) {
                arraysEditor.updateArraysList(arrayFilePath, 0, false);
            }
            checkForInvalidResources();
        }
        isComingFromSrcCodeEditor = false;
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.resources_editor_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    newText = newText.toLowerCase().trim();
                    int currentItem = binding.viewPager.getCurrentItem();
                    if (currentItem == 0) {
                        stringsEditor.adapter.filter(newText);
                    } else if (currentItem == 1) {
                        colorsEditor.adapter.filter(newText);
                    } else if (currentItem == 2) {
                        stylesEditor.adapter.filter(newText);
                    } else if (currentItem == 3) {
                        themesEditor.adapter.filter(newText);
                    } else if (currentItem == 4) {
                        arraysEditor.adapter.filter(newText);
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }
            });
        }
        MenuItem getFromVariantItem = menu.findItem(R.id.action_get_from_variant);
        if (getFromVariantItem != null) {
            getFromVariantItem.setVisible(!variant.isEmpty());
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            saveEditorsChanges();
        } else if (id == R.id.action_select_variant) {
            changeTheVariantDialog();
        } else if (id == R.id.action_get_from_variant) {
            importFromDefaultVariant();
        } else if (id != R.id.action_search) {
            int currentItem = binding.viewPager.getCurrentItem();
            if (currentItem == 0) {
                stringsEditor.saveStringsFile();
                goToCodeEditor("strings.xml", stringsFilePath);
            } else if (currentItem == 1) {
                colorsEditor.saveColorsFile();
                goToCodeEditor("colors.xml", colorsFilePath);
            } else if (currentItem == 2) {
                stylesEditor.saveStylesFile();
                goToCodeEditor("styles.xml", stylesFilePath);
            } else if (currentItem == 3) {
                themesEditor.saveThemesFile();
                goToCodeEditor("themes.xml", themesFilePath);
            } else if (currentItem == 4) {
                arraysEditor.saveArraysFile();
                goToCodeEditor("arrays.xml", arrayFilePath);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveEditorsChanges() {
        stringsEditor.saveStringsFile();
        colorsEditor.saveColorsFile();
        stylesEditor.saveStylesFile();
        themesEditor.saveThemesFile();
        arraysEditor.saveArraysFile();
        updateProjectMetadata();
        SketchwareUtil.toast("Save completed");
    }

    private void updateProjectMetadata() {
        if (variant.isEmpty()) {
            HashMap<String, Object> metadata = yq.metadata;

            for (ColorModel color : colorsEditor.colorList) {
                if (colorsEditor.defaultColors.containsKey(color.getColorName())) {
                    metadata.put(colorsEditor.defaultColors.get(color.getColorName()), PropertiesUtil.parseColor(colorsEditor.colorsEditorManager.getColorValue(getApplicationContext(), color.getColorValue(), 3)));
                }
            }

            for (HashMap<String, Object> map : stringsEditor.listmap) {
                if (Objects.requireNonNull(map.get("key")).toString().equals("app_name")) {
                    metadata.put("my_app_name", Objects.requireNonNull(map.get("text")).toString());
                }
            }

            lC.a(sc_id, metadata);
        }
    }

    private void setupViewPager() {
        EditorsAdapter adapter = new EditorsAdapter(this);
        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("strings" + variant + ".xml");
                    break;
                case 1:
                    tab.setText("colors" + variant + ".xml");
                    break;
                case 2:
                    tab.setText("styles" + variant + ".xml");
                    break;
                case 3:
                    tab.setText("themes" + variant + ".xml");
                    break;
                case 4:
                    tab.setText("arrays" + variant + ".xml");
            }
        }).attach();
        UI.animateLayoutChanges(binding.viewPager);
    }

    private void changeTheVariantDialog() {
        ArrayList<String> resourcesDir = new ArrayList<>();
        FileUtil.listDir(wq.b(sc_id) + "/files/resource/", resourcesDir);

        ArrayList<String> variants = extractVariants(resourcesDir);
        AtomicInteger selectedChoice = new AtomicInteger(variants.indexOf("values" + variant));

        ResourcesVariantSelectorDialogBinding binding = ResourcesVariantSelectorDialogBinding.inflate(getLayoutInflater());
        aB dialog = new aB(this);
        dialog.b(Helper.getResString(R.string.common_word_select_variant));

        setupDialog(binding, dialog, variants, selectedChoice);

        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.a(binding.getRoot());
        dialog.show();
    }

    private void setupDialog(ResourcesVariantSelectorDialogBinding binding, aB dialog, ArrayList<String> variants, AtomicInteger selectedChoice) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, variants);
        binding.listView.setAdapter(adapter);
        binding.listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        binding.listView.setItemChecked(selectedChoice.get(), true);

        binding.listView.setOnItemClickListener((parent, view, which, id) -> {
            selectedChoice.set(which);
            binding.input.setText("");
            binding.input.clearFocus();
        });

        if (!(selectedChoice.get() >= 0 && selectedChoice.get() < variants.size() && variants.get(selectedChoice.get()).equals("values" + variant))) {
            binding.input.setText("values" + variant);
        }
        binding.input.setText(variantFullNameStarts);

        binding.input.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;

                String inputText = s.toString();
                if (!inputText.startsWith(variantFullNameStarts)) {
                    isUpdating = true;
                    binding.input.setText(variantFullNameStarts);
                    binding.input.setSelection(variantFullNameStarts.length());
                    isUpdating = false;
                } else {
                    String newText = inputText.trim();
                    if (!newText.equals(variantFullNameStarts)) {
                        for (int i = 0; i < binding.listView.getCount(); i++) {
                            binding.listView.setItemChecked(i, false);
                        }
                    } else {
                        if (selectedChoice.get() >= 0 && selectedChoice.get() < binding.listView.getCount()) {
                            binding.listView.setItemChecked(selectedChoice.get(), true);
                        }
                    }
                }
            }
        });

        dialog.b(Helper.getResString(R.string.common_word_save), v -> saveVariant(binding, variants, selectedChoice));
    }

    private void saveVariant(ResourcesVariantSelectorDialogBinding binding, ArrayList<String> variants, AtomicInteger selectedChoice) {
        String newVariant = Objects.requireNonNull(binding.input.getText()).toString().trim();
        if (!newVariant.equals(variantFullNameStarts)) {
            if (newVariant.startsWith(variantFullNameStarts)) {
                initializeBackgroundTask(newVariant.replace("values", ""));
            } else {
                SketchwareUtil.toastError("Invalid variant input");
            }
        } else {
            initializeBackgroundTask(variants.get(selectedChoice.get()).replace("values", ""));
        }
    }

    public ArrayList<String> extractVariants(ArrayList<String> resourcesDir) {
        ArrayList<String> variants = new ArrayList<>();
        for (String dir : resourcesDir) {
            String regex = "(values(-[^/]+)*)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(dir);

            if (matcher.find()) {
                variants.add(matcher.group(1));
            }
        }
        return variants;
    }

    private void importFromDefaultVariant() {
        ResourcesEditorImportDialogBinding binding = ResourcesEditorImportDialogBinding.inflate(getLayoutInflater());
        aB dialog = new aB(this);
        dialog.a(binding.getRoot());
        dialog.b(Helper.getResString(R.string.common_word_import_variant));

        ArrayList<String> resourcesFileNames = new ArrayList<>(List.of(
                "strings.xml",
                "colors.xml",
                "styles.xml",
                "themes.xml",
                "arrays.xml"
        ));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, resourcesFileNames);

        ListView listView = binding.resourcesListView;
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        dialog.b(Helper.getResString(R.string.common_word_save), view -> {
            SparseBooleanArray selectedItems = listView.getCheckedItemPositions();
            boolean isSkippingMode = binding.chipMergeAndSkip.isChecked();
            boolean isMergeAndReplace = binding.chipMergeAndReplace.isChecked();
            int updateMode = isMergeAndReplace ? 2 : isSkippingMode ? 1 : 0;
            for (int i = 0; i < resourcesFileNames.size(); i++) {
                if (selectedItems.get(i)) {
                    if (i == 0)
                        stringsEditor.updateStringsList(stringsFilePath.replace(variant, ""), updateMode, true);
                    if (i == 1)
                        colorsEditor.updateColorsList(colorsFilePath.replace(variant, ""), updateMode, true);
                    if (i == 2)
                        stylesEditor.updateStylesList(stylesFilePath.replace(variant, ""), updateMode, true);
                    if (i == 3)
                        themesEditor.updateThemesList(themesFilePath.replace(variant, ""), updateMode, true);
                    if (i == 4)
                        arraysEditor.updateArraysList(arrayFilePath.replace(variant, ""), updateMode, true);
                }
            }
        });

        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    public static String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "\\'")
                .replace("\n", "&#10;")
                .replace("\r", "&#13;");
    }

}

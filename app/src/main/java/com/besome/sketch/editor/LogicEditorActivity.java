package com.besome.sketch.editor;

import static pro.sketchware.widgets.WidgetsCreatorManager.clearErrorOnTextChanged;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.besome.sketch.beans.BlockBean;
import com.besome.sketch.beans.BlockCollectionBean;
import com.besome.sketch.beans.ComponentBean;
import com.besome.sketch.beans.HistoryBlockBean;
import com.besome.sketch.beans.MoreBlockCollectionBean;
import com.besome.sketch.beans.ProjectFileBean;
import com.besome.sketch.beans.ViewBean;
import com.besome.sketch.design.DesignActivity;
import com.besome.sketch.editor.component.ComponentAddActivity;
import com.besome.sketch.editor.logic.BlockPane;
import com.besome.sketch.editor.logic.LogicTopMenu;
import com.besome.sketch.editor.logic.PaletteBlock;
import com.besome.sketch.editor.logic.PaletteSelector;
import com.besome.sketch.editor.makeblock.MakeBlockActivity;
import com.besome.sketch.editor.manage.ShowBlockCollectionActivity;
import com.besome.sketch.editor.view.ViewDummy;
import com.besome.sketch.editor.view.ViewLogicEditor;
import com.besome.sketch.lib.base.BaseAppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import a.a.a.DB;
import a.a.a.FB;
import a.a.a.Fx;
import a.a.a.GB;
import a.a.a.MA;
import a.a.a.Mp;
import a.a.a.NB;
import a.a.a.Ox;
import a.a.a.Pp;
import a.a.a.Rs;
import a.a.a.Ss;
import a.a.a.Ts;
import a.a.a.Us;
import a.a.a.Vs;
import a.a.a.ZB;
import a.a.a.Zx;
import a.a.a.aB;
import a.a.a.bC;
import a.a.a.eC;
import a.a.a.jC;
import a.a.a.jq;
import a.a.a.kC;
import a.a.a.mB;
import a.a.a.sq;
import a.a.a.uq;
import a.a.a.wB;
import a.a.a.xB;
import a.a.a.yq;
import a.a.a.yy;
import dev.aldi.sayuti.block.ExtraPaletteBlock;

import mod.bobur.XmlToSvgConverter;
import mod.hey.studios.editor.view.IdGenerator;
import mod.hey.studios.moreblock.ReturnMoreblockManager;
import mod.hey.studios.moreblock.importer.MoreblockImporterDialog;
import mod.hey.studios.project.ProjectSettings;
import mod.hey.studios.util.Helper;
import mod.hilal.saif.asd.AsdDialog;
import mod.jbk.editor.manage.MoreblockImporter;
import mod.jbk.util.BlockUtil;
import mod.pranav.viewbinding.ViewBindingBuilder;
import pro.sketchware.R;
import pro.sketchware.activities.resources.editors.utils.StringsEditorManager;
import pro.sketchware.databinding.ImagePickerItemBinding;
import pro.sketchware.databinding.SearchWithRecyclerViewBinding;
import pro.sketchware.databinding.PropertyPopupSelectorSingleBinding;
import pro.sketchware.databinding.ViewStringEditorAddBinding;
import pro.sketchware.menu.ExtraMenuBean;
import pro.sketchware.utility.FilePathUtil;
import pro.sketchware.utility.FileUtil;
import pro.sketchware.utility.SketchwareUtil;
import pro.sketchware.utility.SvgUtils;
import pro.sketchware.activities.resources.editors.ResourcesEditorActivity;
import pro.sketchware.activities.editor.view.CodeViewerActivity;

@SuppressLint({"ClickableViewAccessibility", "RtlHardcoded", "SetTextI18n", "DefaultLocale"})
public class LogicEditorActivity extends BaseAppCompatActivity implements View.OnClickListener, Vs, View.OnTouchListener, MoreblockImporterDialog.CallBack {

    private final Handler Z = new Handler();
    private final int[] v = new int[2];
    public ProjectFileBean M;
    public PaletteBlock m;
    public BlockPane o;
    public String B = "";
    public String C = "";
    public String D = "";
    private Vibrator F;
    private LinearLayout J, K;
    private FloatingActionButton openBlocksMenuButton;
    private LogicTopMenu logicTopMenu;
    private LogicEditorDrawer O;
    private ObjectAnimator U, V, ba, ca, fa, ga;
    private ExtraPaletteBlock extraPaletteBlock;
    private ViewLogicEditor n;
    private ViewDummy p;
    private PaletteSelector paletteSelector;
    private Rs w;
    private float r, q, s, t;
    private int A, S, x, y;
    private int T = -30;
    private View Y;
    private boolean G, u, W, X, da, ea, ha, ia;
    private final Runnable aa = this::r;
    private ArrayList<BlockBean> savedBlockBean = new ArrayList<>();
    private Boolean isViewBindingEnabled;

    private final ActivityResultLauncher<Intent> openResourcesEditor = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            paletteSelector.performClickPalette(-1);
        }
    });

    private void loadEventBlocks() {
        ArrayList<BlockBean> eventBlocks = jC.a(B).a(M.getJavaName(), C + "_" + D);
        if (eventBlocks != null) {
            if (eventBlocks.isEmpty()) {
                e(X);
            }

            boolean needToFindRoot = true;
            HashMap<Integer, Rs> blockIdsAndBlocks = new HashMap<>();
            for (BlockBean next : eventBlocks) {
                if (D.equals("onTextChanged") && next.opCode.equals("getArg") && next.spec.equals("text")) {
                    next.spec = "charSeq";
                }
                Rs b2 = b(next);
                blockIdsAndBlocks.put((Integer) b2.getTag(), b2);
                o.g = Math.max(o.g, (Integer) b2.getTag() + 1);
                o.a(b2, 0, 0);
                b2.setOnTouchListener(this);
                if (needToFindRoot) {
                    o.getRoot().b(b2);
                    needToFindRoot = false;
                }
            }
            for (BlockBean next2 : eventBlocks) {
                Rs block = blockIdsAndBlocks.get(Integer.valueOf(next2.id));
                if (block != null) {
                    Rs subStack1RootBlock;
                    if (next2.subStack1 >= 0 && (subStack1RootBlock = blockIdsAndBlocks.get(next2.subStack1)) != null) {
                        block.e(subStack1RootBlock);
                    }
                    Rs subStack2RootBlock;
                    if (next2.subStack2 >= 0 && (subStack2RootBlock = blockIdsAndBlocks.get(next2.subStack2)) != null) {
                        block.f(subStack2RootBlock);
                    }
                    Rs nextBlock;
                    if (next2.nextBlock >= 0 && (nextBlock = blockIdsAndBlocks.get(next2.nextBlock)) != null) {
                        block.b(nextBlock);
                    }
                    for (int i = 0; i < next2.parameters.size(); i++) {
                        String parameter = next2.parameters.get(i);
                        if (parameter != null && !parameter.isEmpty()) {
                            if (parameter.charAt(0) == '@') {
                                Rs parameterBlock = blockIdsAndBlocks.get(Integer.valueOf(parameter.substring(1)));
                                if (parameterBlock != null) {
                                    block.a((Ts) block.V.get(i), parameterBlock);
                                }
                            } else {
                                ((Ss) block.V.get(i)).setArgValue(parameter);
                                block.m();
                            }
                        }
                    }
                }
            }
            o.getRoot().k();
            o.b();
        }
    }

    private void redo() {
        if (!u) {
            HistoryBlockBean historyBlockBean = bC.d(B).i(s());
            if (historyBlockBean != null) {
                int actionType = historyBlockBean.getActionType();
                if (actionType == HistoryBlockBean.ACTION_TYPE_ADD) {
                    int[] locationOnScreen = new int[2];
                    o.getLocationOnScreen(locationOnScreen);
                    a(historyBlockBean.getAddedData(), historyBlockBean.getCurrentX() + locationOnScreen[0], historyBlockBean.getCurrentY() + locationOnScreen[1], true);
                    if (historyBlockBean.getCurrentParentData() != null) {
                        a(historyBlockBean.getCurrentParentData(), true);
                    }
                } else if (actionType == HistoryBlockBean.ACTION_TYPE_UPDATE) {
                    a(historyBlockBean.getCurrentUpdateData(), true);
                } else if (actionType == HistoryBlockBean.ACTION_TYPE_REMOVE) {
                    ArrayList<BlockBean> removedData = historyBlockBean.getRemovedData();

                    for (int i = removedData.size() - 1; i >= 0; i--) {
                        o.a(removedData.get(i), false);
                    }
                    if (historyBlockBean.getCurrentParentData() != null) {
                        a(historyBlockBean.getCurrentParentData(), true);
                    }
                } else if (actionType == HistoryBlockBean.ACTION_TYPE_MOVE) {
                    for (BlockBean afterMoveData : historyBlockBean.getAfterMoveData()) {
                        o.a(afterMoveData, true);
                    }

                    int[] locationOnScreen = new int[2];
                    o.getLocationOnScreen(locationOnScreen);
                    a(historyBlockBean.getAfterMoveData(), historyBlockBean.getCurrentX() + locationOnScreen[0], historyBlockBean.getCurrentY() + locationOnScreen[1], true);
                    if (historyBlockBean.getCurrentParentData() != null) {
                        a(historyBlockBean.getCurrentParentData(), true);
                    }

                    if (historyBlockBean.getCurrentOriginalParent() != null) {
                        a(historyBlockBean.getCurrentOriginalParent(), true);
                    }
                }
            }
            invalidateOptionsMenu();
        }
    }

    public void C() {
        invalidateOptionsMenu();
    }

    public void E() {
        eC a2 = jC.a(B);
        String javaName = M.getJavaName();
        a2.a(javaName, C + "_" + D, o.getBlocks());
    }

    public void G() {
        aB aBVar = new aB(this);
        aBVar.b(getTranslatedString(R.string.logic_editor_title_add_new_list));
        aBVar.a(R.drawable.ic_mtrl_add);
        View a2 = wB.a(this, R.layout.logic_popup_add_list);
        RadioGroup radioGroup = a2.findViewById(R.id.rg_type);
        TextInputEditText editText = a2.findViewById(R.id.ed_input);
        ZB zb = new ZB(getContext(), a2.findViewById(R.id.ti_input), uq.b, uq.a(), jC.a(B).a(M));
        aBVar.a(a2);
        aBVar.b(getTranslatedString(R.string.common_word_add), v -> {
            if (zb.b()) {
                int i = 1;
                int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                if (checkedRadioButtonId != R.id.rb_int) {
                    if (checkedRadioButtonId == R.id.rb_string) {
                        i = 2;
                    } else if (checkedRadioButtonId == R.id.rb_map) {
                        i = 3;
                    }
                }

                a(i, editText.getText().toString());
                aBVar.dismiss();
            }
        });
        aBVar.a(getTranslatedString(R.string.common_word_cancel), Helper.getDialogDismissListener(aBVar));
        aBVar.show();
    }

    private void showAddNewVariableDialog() {
        aB dialog = new aB(this);
        dialog.b(getTranslatedString(R.string.logic_editor_title_add_new_variable));
        dialog.a(R.drawable.ic_mtrl_add);

        View customView = wB.a(this, R.layout.logic_popup_add_variable);
        RadioGroup radioGroup = customView.findViewById(R.id.rg_type);
        TextInputEditText editText = customView.findViewById(R.id.ed_input);
        ZB nameValidator = new ZB(getContext(), customView.findViewById(R.id.ti_input), uq.b, uq.a(), jC.a(B).a(M));
        dialog.a(customView);
        dialog.b(getTranslatedString(R.string.common_word_add), v -> {
            int variableType = 1;
            if (radioGroup.getCheckedRadioButtonId() == R.id.rb_boolean) {
                variableType = 0;
            } else if (radioGroup.getCheckedRadioButtonId() != R.id.rb_int) {
                if (radioGroup.getCheckedRadioButtonId() == R.id.rb_string) {
                    variableType = 2;
                } else if (radioGroup.getCheckedRadioButtonId() == R.id.rb_map) {
                    variableType = 3;
                }
            }

            if (nameValidator.b()) {
                b(variableType, editText.getText().toString());
                dialog.dismiss();
            }
        });
        dialog.a(getTranslatedString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    private void showAddNewXmlStringDialog() {
        aB dialog = new aB(this);
        ViewStringEditorAddBinding binding = ViewStringEditorAddBinding.inflate(LayoutInflater.from(this));
        dialog.b("Create new string");
        dialog.b("Create", v1 -> {

            String filePath = new FilePathUtil().getPathResource(B) + "/values/strings.xml";
            ArrayList<HashMap<String, Object>> StringsListMap = new ArrayList<>();
            StringsEditorManager stringsEditorManager = new StringsEditorManager();
            stringsEditorManager.convertXmlStringsToListMap(FileUtil.readFile(filePath), StringsListMap);

            clearErrorOnTextChanged(binding.stringKeyInput, binding.stringKeyInputLayout);

            String key = Objects.requireNonNull(binding.stringKeyInput.getText()).toString();
            String value = Objects.requireNonNull(binding.stringValueInput.getText()).toString();

            if (stringsEditorManager.isXmlStringsExist(StringsListMap, key)) {
                binding.stringKeyInputLayout.setError("\"" + key + "\" is already exist");
                return;
            }

            if (key.isEmpty() || value.isEmpty()) {
                SketchwareUtil.toastError("Please fill in all fields");
                return;
            }

            HashMap<String, Object> map = new HashMap<>();
            map.put("key", key);
            map.put("text", value);
            StringsListMap.add(map);
            FileUtil.writeFile(filePath, stringsEditorManager.convertListMapToXmlStrings(StringsListMap, new HashMap<>()));
            paletteSelector.performClickPalette(-1);
            dialog.dismiss();
        });
        dialog.a(getString(R.string.cancel), v1 -> dialog.dismiss());
        dialog.a(binding.getRoot());
        dialog.show();
    }

    private void showRemoveXmlStringDialog() {
        String filePath = new FilePathUtil().getPathResource(B) + "/values/strings.xml";

        ArrayList<HashMap<String, Object>> stringsList = new ArrayList<>();
        StringsEditorManager stringsEditorManager = new StringsEditorManager();
        stringsEditorManager.convertXmlStringsToListMap(FileUtil.readFile(filePath), stringsList);

        aB dialog = new aB(this);
        dialog.b(getTranslatedString(R.string.logic_editor_title_remove_xml_strings));
        dialog.a(R.drawable.delete_96);

        PropertyPopupSelectorSingleBinding binding = PropertyPopupSelectorSingleBinding.inflate(LayoutInflater.from(this));
        ViewGroup viewGroup = binding.rgContent;

        for (HashMap<String, Object> map : stringsList) {
            String key = Objects.requireNonNull(map.get("key")).toString();
            CheckBox checkBox = createCheckBox(key);
            checkBox.setTag(key);
            setOnCheckedListener(checkBox);
            viewGroup.addView(checkBox);
        }

        dialog.a(binding.getRoot());

        dialog.b(getTranslatedString(R.string.common_word_remove), v -> {
            int childCount = viewGroup.getChildCount();

            for (int i = 0; i < childCount; i++) {
                CheckBox checkBox = (CheckBox) viewGroup.getChildAt(i);
                if (checkBox.isChecked()) {
                    removeItem(stringsList, checkBox.getText().toString());
                }
            }

            FileUtil.writeFile(filePath, stringsEditorManager.convertListMapToXmlStrings(stringsList, new HashMap<>()));

            paletteSelector.performClickPalette(-1);
            dialog.dismiss();
        });

        dialog.a(getTranslatedString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    public void setOnCheckedListener(CheckBox checkBox) {
        checkBox.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked && isXmlStringUsed(B, checkBox.getText().toString())) {
                checkBox.setChecked(false);
            }
        });
    }

    public void removeItem(ArrayList<HashMap<String, Object>> listMap, String key) {
        listMap.removeIf(map -> key.equals(map.get("key")));
    }

    private boolean isXmlStringUsed(String projectScId, String key) {
        if ("app_name".equals(key)) {
            return true;
        }
        eC projectDataManager = jC.a(projectScId);

        return isKeyHasNonSavedUsage(key) || isKeyUsedInJavaFiles(projectDataManager, projectScId, key) || isKeyUsedInXmlFiles(projectDataManager, projectScId, key);
    }

    private boolean isKeyHasNonSavedUsage(String key) {
        for (BlockBean block : o.getBlocks()) {
            if (block.opCode.equals("getResStr") && block.spec.equals(key) ||
                    (block.opCode.equals("getResString") && block.parameters.get(0).equals("R.string." + key))) {

                showToastError();
                return true;
            }
        }
        return false;
    }

    private boolean isKeyHasSavedUsage(String key) {
        for (BlockBean block : savedBlockBean) {
            if (block.opCode.equals("getResStr") && block.spec.equals(key) ||
                    (block.opCode.equals("getResString") && block.parameters.get(0).equals("R.string." + key))) {

                return true;
            }
        }
        return false;
    }

    private boolean isKeyUsedInJavaFiles(eC projectDataManager, String projectScId, String key) {

        if ((getStringUsageLengthInJava(projectDataManager, projectScId, key) == 1) && isKeyHasSavedUsage(key) && !isKeyHasNonSavedUsage(key)) {
            return false;
        }

        for (String javaFileName : getAllJavaFileNames(projectScId)) {
            for (Map.Entry<String, ArrayList<BlockBean>> entry : projectDataManager.b(javaFileName).entrySet()) {
                for (BlockBean block : entry.getValue()) {
                    if (block.opCode.equals("getResStr") && block.spec.equals(key) ||
                            (block.opCode.equals("getResString") && block.parameters.get(0).equals("R.string." + key))) {

                        showToastError();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isKeyUsedInXmlFiles(eC projectDataManager, String projectScId, String key) {
        for (String xmlFileName : getAllXmlFileNames(projectScId)) {
            for (ViewBean view : projectDataManager.d(xmlFileName)) {
                if (view.text.text.equals("@string/" + key) ||
                        (view.text.hint.equals("@string/" + key))) {

                    showToastError();
                    return true;
                }
            }
        }
        return false;
    }

    private void showToastError() {
        SketchwareUtil.toastError(Helper.getResString(R.string.logic_editor_title_remove_xml_string_error));
    }

    private int getStringUsageLengthInJava(eC projectDataManager, String projectScId, String key) {
        int length = 0;
        for (String javaFileName : getAllJavaFileNames(projectScId)) {
            for (Map.Entry<String, ArrayList<BlockBean>> entry : projectDataManager.b(javaFileName).entrySet()) {
                for (BlockBean block : entry.getValue()) {
                    if (block.opCode.equals("getResStr") && block.spec.equals(key) ||
                            (block.opCode.equals("getResString") && block.parameters.get(0).equals("R.string." + key))) {

                        length++;
                    }
                }
            }
        }
        return length;
    }

    public static ArrayList<String> getAllJavaFileNames(String projectScId) {
        ArrayList<String> javaFileNames = new ArrayList<>();
        for (ProjectFileBean projectFile : jC.b(projectScId).b()) {
            javaFileNames.add(projectFile.getJavaName());
        }
        return javaFileNames;
    }

    public static ArrayList<String> getAllXmlFileNames(String projectScId) {
        ArrayList<String> xmlFileNames = new ArrayList<>();
        for (ProjectFileBean projectFile : jC.b(projectScId).b()) {
            String xmlName = projectFile.getXmlName();
            if (xmlName != null && !xmlName.isEmpty()) {
                xmlFileNames.add(xmlName);
            }
        }
        return xmlFileNames;
    }

    public void openResourcesEditor() {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), ResourcesEditorActivity.class);
        intent.putExtra("sc_id", B);
        openResourcesEditor.launch(intent);
    }

    public void I() {
        ArrayList<MoreBlockCollectionBean> moreBlocks = Pp.h().f();
        new MoreblockImporterDialog(this, moreBlocks, this).show();
    }

    public void J() {
        aB aBVar = new aB(this);
        aBVar.b(getTranslatedString(R.string.logic_editor_title_remove_list));
        aBVar.a(R.drawable.ic_mtrl_delete);
        View a2 = wB.a(this, R.layout.property_popup_selector_single);
        ViewGroup viewGroup = a2.findViewById(R.id.rg_content);
        for (Pair<Integer, String> list : jC.a(B).j(M.getJavaName())) {
            viewGroup.addView(e(list.second));
        }
        aBVar.a(a2);
        aBVar.b(getTranslatedString(R.string.common_word_remove), v -> {
            int childCount = viewGroup.getChildCount();
            int i = 0;
            while (i < childCount) {
                RadioButton radioButton = (RadioButton) viewGroup.getChildAt(i);
                if (radioButton.isChecked()) {
                    if (!o.b(radioButton.getText().toString())) {
                        if (!jC.a(B).b(M.getJavaName(), radioButton.getText().toString(), C + "_" + D)) {
                            l(radioButton.getText().toString());
                        }
                    }
                    Toast.makeText(getContext(), getTranslatedString(R.string.logic_editor_message_currently_used_list), Toast.LENGTH_SHORT).show();
                    return;
                }
                i++;
            }
            aBVar.dismiss();
        });
        aBVar.a(getTranslatedString(R.string.common_word_cancel), Helper.getDialogDismissListener(aBVar));
        aBVar.show();
    }

    public void K() {
        aB aBVar = new aB(this);
        aBVar.b(getTranslatedString(R.string.logic_editor_title_remove_variable));
        aBVar.a(R.drawable.ic_mtrl_delete);
        View a2 = wB.a(this, R.layout.property_popup_selector_single);
        ViewGroup viewGroup = a2.findViewById(R.id.rg_content);
        for (Pair<Integer, String> next : jC.a(B).k(M.getJavaName())) {
            RadioButton e = e(next.second);
            e.setTag(next.first);
            viewGroup.addView(e);
        }
        aBVar.a(a2);
        aBVar.b(getTranslatedString(R.string.common_word_remove), v -> {
            int childCount = viewGroup.getChildCount();
            int i = 0;
            while (i < childCount) {
                RadioButton radioButton = (RadioButton) viewGroup.getChildAt(i);
                if (radioButton.isChecked()) {
                    if (!o.c(radioButton.getText().toString())) {
                        if (!jC.a(B).c(M.getJavaName(), radioButton.getText().toString(), C + "_" + D)) {
                            m(radioButton.getText().toString());
                        }
                    }
                    Toast.makeText(getContext(), getTranslatedString(R.string.logic_editor_message_currently_used_variable), Toast.LENGTH_SHORT).show();
                    return;
                }
                i++;
            }
            aBVar.dismiss();
        });
        aBVar.a(getTranslatedString(R.string.common_word_cancel), Helper.getDialogDismissListener(aBVar));
        aBVar.show();
    }

    public void L() {
        try {
            new Handler().postDelayed(() -> new ProjectSaver(this).execute(), 500L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void undo() {
        if (!u) {
            HistoryBlockBean history = bC.d(B).j(s());
            if (history != null) {
                int actionType = history.getActionType();
                if (actionType == HistoryBlockBean.ACTION_TYPE_ADD) {
                    ArrayList<BlockBean> addedData = history.getAddedData();
                    for (int i = addedData.size() - 1; i >= 0; i--) {
                        o.a(addedData.get(i), false);
                    }

                    if (history.getPrevParentData() != null) {
                        history.getPrevParentData().print();
                        a(history.getPrevParentData(), true);
                    }
                } else if (actionType == HistoryBlockBean.ACTION_TYPE_UPDATE) {
                    a(history.getPrevUpdateData(), true);
                } else if (actionType == HistoryBlockBean.ACTION_TYPE_REMOVE) {
                    int[] oLocationOnScreen = new int[2];
                    o.getLocationOnScreen(oLocationOnScreen);
                    a(history.getRemovedData(), history.getCurrentX() + oLocationOnScreen[0], history.getCurrentY() + oLocationOnScreen[1], true);

                    if (history.getPrevParentData() != null) {
                        a(history.getPrevParentData(), true);
                    }
                } else if (actionType == HistoryBlockBean.ACTION_TYPE_MOVE) {
                    for (BlockBean beforeMoveBlock : history.getBeforeMoveData()) {
                        o.a(beforeMoveBlock, true);
                    }

                    int[] oLocationOnScreen = new int[2];
                    o.getLocationOnScreen(oLocationOnScreen);
                    a(history.getBeforeMoveData(), history.getPrevX() + oLocationOnScreen[0], history.getPrevY() + oLocationOnScreen[1], true);

                    if (history.getPrevParentData() != null) {
                        a(history.getPrevParentData(), true);
                    }
                    if (history.getPrevOriginalParent() != null) {
                        a(history.getPrevOriginalParent(), true);
                    }
                }
            }

            invalidateOptionsMenu();
        }
    }

    public Rs a(Rs rs, int i, int i2, boolean z) {
        Rs a2 = o.a(rs, i, i2, z);
        if (!z) {
            a2.setOnTouchListener(this);
        }
        return a2;
    }

    public void addDeprecatedBlock(String message, String type, String opCode) {
        m.addDeprecatedBlock(message, type, opCode);
    }

    public View a(String str, String str2) {
        Ts a2 = m.a("", str, str2);
        a2.setTag(str2);
        a2.setClickable(true);
        a2.setOnTouchListener(this);
        return a2;
    }

    public final View a(String str, String str2, String str3) {
        Ts a2 = m.a(str, str2, str3);
        a2.setTag(str3);
        a2.setClickable(true);
        a2.setOnTouchListener(this);
        return a2;
    }

    public final View a(String str, String str2, String str3, String str4) {
        Ts a2 = m.a(str, str2, str3, str4);
        a2.setTag(str4);
        a2.setClickable(true);
        a2.setOnTouchListener(this);
        return a2;
    }

    private ImageView setImageViewContent(String str) {
        Uri fromFile;
        float a2 = wB.a(this, 1.0f);
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        int i = (int) (a2 * 48.0f);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(i, i));
        if (!"NONE".equals(str)) {
            if (str.equals("default_image")) {
                imageView.setImageResource(getResources().getIdentifier(str, "drawable", getContext().getPackageName()));
            } else {
                File file = new File(jC.d(B).f(str));
                if (file.exists()) {
                    Context context = getContext();
                    fromFile = FileProvider.getUriForFile(context, getContext().getPackageName() + ".provider", file);
                    if (file.getAbsolutePath().endsWith(".xml")) {
                        SvgUtils svgUtils = new SvgUtils(this);
                        FilePathUtil fpu = new FilePathUtil();
                        svgUtils.loadImage(imageView, fpu.getSvgFullPath(B, str));
                    } else {
                        Glide.with(getContext()).load(fromFile).signature(kC.n()).error(R.drawable.ic_remove_grey600_24dp).into(imageView);
                    }
                } else {
                    try {
                        XmlToSvgConverter xmlToSvgConverter = new XmlToSvgConverter();
                        xmlToSvgConverter.setImageVectorFromFile(imageView, xmlToSvgConverter.getVectorFullPath(DesignActivity.sc_id, str));
                    } catch (Exception e) {
                        imageView.setImageResource(R.drawable.ic_remove_grey600_24dp);
                    }
                }
            }
        }
        imageView.setBackgroundResource(R.drawable.bg_outline);
        return imageView;
    }

    public final ArrayList<BlockBean> a(ArrayList<BlockBean> arrayList, int i, int i2, boolean z) {
        HashMap<Integer, Integer> hashMap = new HashMap<>();
        ArrayList<BlockBean> arrayList2 = new ArrayList<>();
        for (BlockBean next : arrayList) {
            if (next.id != null && !next.id.isEmpty()) {
                arrayList2.add(next.clone());
            }
        }
        for (BlockBean next2 : arrayList2) {
            if (Integer.parseInt(next2.id) >= 99000000) {
                hashMap.put(Integer.valueOf(next2.id), o.g);
                o.g = o.g + 1;
            } else {
                hashMap.put(Integer.valueOf(next2.id), Integer.valueOf(next2.id));
            }
        }
        int size = arrayList2.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            }
            BlockBean blockBean = arrayList2.get(size);
            if (!a(blockBean)) {
                arrayList2.remove(size);
                hashMap.remove(Integer.valueOf(blockBean.id));
            }
        }
        for (BlockBean block : arrayList2) {
            if (hashMap.containsKey(Integer.valueOf(block.id))) {
                block.id = String.valueOf(hashMap.get(Integer.valueOf(block.id)));
            } else {
                block.id = "";
            }
            for (int j = 0; j < block.parameters.size(); j++) {
                String parameter = block.parameters.get(j);
                if (parameter != null && !parameter.isEmpty() && parameter.charAt(0) == '@') {
                    int parameterId = Integer.parseInt(parameter.substring(1));
                    int parameterAsBlockId = hashMap.containsKey(parameterId) ? hashMap.get(parameterId) : 0;
                    if (parameterAsBlockId >= 0) {
                        block.parameters.set(j, '@' + String.valueOf(parameterAsBlockId));
                    } else {
                        block.parameters.set(j, "");
                    }
                }
            }
            if (block.subStack1 >= 0 && hashMap.containsKey(block.subStack1)) {
                block.subStack1 = hashMap.get(block.subStack1);
            }
            if (block.subStack2 >= 0 && hashMap.containsKey(block.subStack2)) {
                block.subStack2 = hashMap.get(block.subStack2);
            }
            if (block.nextBlock >= 0 && hashMap.containsKey(block.nextBlock)) {
                block.nextBlock = hashMap.get(block.nextBlock);
            }
        }
        Rs firstBlock = null;
        for (int j = 0; j < arrayList2.size(); j++) {
            BlockBean blockBean = arrayList2.get(j);
            if (blockBean.id != null && !blockBean.id.isEmpty()) {
                Rs block = b(blockBean);
                if (j == 0) {
                    firstBlock = block;
                }
                o.a(block, i, i2);
                block.setOnTouchListener(this);
            }
        }
        for (BlockBean block : arrayList2) {
            if (block.id != null && !block.id.isEmpty()) {
                a(block, false);
            }
        }
        if (firstBlock != null && z) {
            firstBlock.p().k();
            o.b();
        }
        return arrayList2;
    }

    @Override
    public void a(int i, int i2) {
        extraPaletteBlock.setBlock(i, i2);
    }

    public void a(int i, String str) {
        jC.a(B).b(M.getJavaName(), i, str);
        a(1, 0xffcc5b22);
    }

    public void a(Rs rs) {
        w = null;
        y = -1;
        x = 0;
        int[] iArr = new int[2];
        Rs rs2 = rs.E;
        if (rs2 != null) {
            w = rs2;
            if (savedBlockBean.isEmpty()) {
                savedBlockBean = o.getBlocks();
            }
        }
        Rs rs3 = w;
        if (rs3 == null) {
            return;
        }
        if (rs3.ha == (Integer) rs.getTag()) {
            x = 0;
        } else if (w.ia == (Integer) rs.getTag()) {
            x = 2;
        } else if (w.ja == (Integer) rs.getTag()) {
            x = 3;
        } else if (w.V.contains(rs)) {
            x = 5;
            y = w.V.indexOf(rs);
        }
    }

    public void a(Rs rs, float f, float f2) {
        for (View next : rs.V) {
            if ((next instanceof Ss) && next.getX() < f && next.getX() + next.getWidth() > f && next.getY() < f2 && next.getY() + next.getHeight() > f2) {
                new ExtraMenuBean(this).defineMenuSelector((Ss) next);
                return;
            }
        }
    }

    public void a(Ss ss, Object obj) {
        BlockBean clone = ss.E.getBean().clone();
        ss.setArgValue(obj);
        ss.E.m();
        ss.E.p().k();
        ss.E.pa.b();
        bC.d(B).a(s(), clone, ss.E.getBean().clone());
        C();
    }

    public void pickImage(Ss ss, String str) {
        boolean selectingBackgroundImage = "property_background_resource".equals(str);
        boolean selectingImage = !selectingBackgroundImage && "property_image".equals(str);
        AtomicReference<String> selectedImage = new AtomicReference<>("");

        SearchWithRecyclerViewBinding binding = SearchWithRecyclerViewBinding.inflate(getLayoutInflater());

        aB dialog = new aB(this);
        if (selectingImage) {
            dialog.b(getTranslatedString(R.string.logic_editor_title_select_image));
        } else if (selectingBackgroundImage) {
            dialog.b(getTranslatedString(R.string.logic_editor_title_select_image_background));
        }

        dialog.a(R.drawable.ic_picture_48dp);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<String> images = jC.d(B).m();
        images.addAll(new XmlToSvgConverter().getVectorDrawables(DesignActivity.sc_id));
        if (selectingImage) {
            images.add(0, "default_image");
        } else if (selectingBackgroundImage) {
            images.add(0, "NONE");
        }

        ImagePickerAdapter adapter = new ImagePickerAdapter(images, (String) ss.getArgValue(), selectedImage::set);
        binding.recyclerView.setAdapter(adapter);


        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().toLowerCase();
                adapter.filter(query);
            }
        });

        dialog.b(getTranslatedString(R.string.common_word_save), view -> {
            String selectedImg = selectedImage.get();
            if (!selectedImg.isEmpty()) {
                a(ss, selectedImage.get());
            }
        });

        dialog.a(binding.getRoot());
        dialog.a(getTranslatedString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    public void a(Ss ss, boolean z) {
        aB aBVar = new aB(this);
        aBVar.b(getTranslatedString(z ? R.string.logic_editor_title_enter_number_value : R.string.logic_editor_title_enter_string_value));
        aBVar.a(R.drawable.rename_96_blue);
        View a2 = wB.a(this, R.layout.property_popup_input_text);
        EditText editText = a2.findViewById(R.id.ed_input);
        if (z) {
            editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            editText.setMaxLines(1);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            editText.setImeOptions(EditorInfo.IME_ACTION_NONE);
        }
        editText.setText(ss.getArgValue().toString());
        aBVar.a(a2);
        aBVar.b(getTranslatedString(R.string.common_word_save), v -> {
            String text = editText.getText().toString();
            emptyStringSetter:
            {
                if (z) {
                    try {
                        double d = Double.parseDouble(text);
                        if (!Double.isNaN(d) && !Double.isInfinite(d)) {
                            break emptyStringSetter;
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else if (!text.isEmpty()) {
                    if (text.charAt(0) == '@') {
                        text = " " + text;
                        break emptyStringSetter;
                    }
                }

                text = "";
            }

            a(ss, text);
            aBVar.dismiss();
        });
        aBVar.a(getTranslatedString(R.string.common_word_cancel), Helper.getDialogDismissListener(aBVar));
        aBVar.show();
    }

    public class ImagePickerAdapter extends RecyclerView.Adapter<ImagePickerAdapter.ViewHolder> {

        private final ArrayList<String> images;
        private String selectedImage;
        private final OnImageSelectedListener listener;
        private final ArrayList<String> filteredImages;
        private final Map<String, View> imageCache = new HashMap<>();

        public interface OnImageSelectedListener {
            void onImageSelected(String image);
        }

        public ImagePickerAdapter(ArrayList<String> images, String selectedImage, OnImageSelectedListener listener) {
            this.images = images;
            this.selectedImage = selectedImage;
            this.listener = listener;
            this.filteredImages = new ArrayList<>(images);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ImagePickerItemBinding binding = ImagePickerItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String image = filteredImages.get(position);

            holder.binding.textView.setText(image);

            View imageView = imageCache.get(image);
            if (imageView == null) {
                imageView = setImageViewContent(image);
                imageCache.put(image, imageView);
            }

            if (imageView.getParent() != null) {
                ((ViewGroup) imageView.getParent()).removeView(imageView);
            }

            holder.binding.layoutImg.removeAllViews();
            holder.binding.layoutImg.addView(imageView);

            holder.binding.radioButton.setChecked(image.equals(selectedImage));

            holder.binding.transparentOverlay.setOnClickListener(v -> {
                if (!image.equals(selectedImage)) {
                    selectedImage = image;
                    listener.onImageSelected(image);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return filteredImages.size();
        }

        public void filter(String query) {
            filteredImages.clear();
            if (query.isEmpty()) {
                filteredImages.addAll(images);
            } else {
                for (String image : images) {
                    if (image.toLowerCase().contains(query)) {
                        filteredImages.add(image);
                    }
                }
            }
            notifyDataSetChanged();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final ImagePickerItemBinding binding;

            public ViewHolder(@NonNull ImagePickerItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public void a(BlockBean blockBean, boolean z) {
        Rs block = o.a(blockBean.id);
        if (block != null) {
            block.ia = -1;
            block.ja = -1;
            block.ha = -1;

            for (int i = 0; i < blockBean.parameters.size(); i++) {
                String parameter = blockBean.parameters.get(i);
                if (parameter != null) {
                    if (!parameter.isEmpty() && parameter.charAt(0) == '@') {
                        int blockId = Integer.parseInt(parameter.substring(1));
                        if (blockId > 0) {
                            Rs parameterBlock = o.a(blockId);
                            if (parameterBlock != null) {
                                block.a((Ts) block.V.get(i), parameterBlock);
                            }
                        }
                    } else {
                        if (block.V.get(i) instanceof Ss ss) {
                            String javaName = M.getJavaName();
                            String xmlName = M.getXmlName();
                            if (D.equals("onBindCustomView")) {
                                var eC = jC.a(B);
                                var view = eC.c(xmlName, C);
                                if (view == null) {
                                    // Event is of a Drawer View
                                    view = eC.c("_drawer_" + xmlName, C);
                                }
                                String customView = view.customView;
                                if (customView != null) {
                                    xmlName = ProjectFileBean.getXmlName(customView);
                                }
                            }

                            if (!parameter.isEmpty()) {
                                if (ss.b.equals("m")) {
                                    eC eC = jC.a(B);

                                    switch (ss.c) {
                                        case "varInt":
                                            eC.f(javaName, ExtraMenuBean.VARIABLE_TYPE_NUMBER, parameter);
                                            break;

                                        case "varBool":
                                            eC.f(javaName, ExtraMenuBean.VARIABLE_TYPE_BOOLEAN, parameter);
                                            break;

                                        case "varStr":
                                            eC.f(javaName, ExtraMenuBean.VARIABLE_TYPE_STRING, parameter);
                                            break;

                                        case "listInt":
                                            eC.e(javaName, ExtraMenuBean.LIST_TYPE_NUMBER, parameter);
                                            break;

                                        case "listStr":
                                            eC.e(javaName, ExtraMenuBean.LIST_TYPE_STRING, parameter);
                                            break;

                                        case "listMap":
                                            eC.e(javaName, ExtraMenuBean.LIST_TYPE_MAP, parameter);
                                            break;

                                        case "list":
                                            boolean b = eC.e(javaName, ExtraMenuBean.LIST_TYPE_NUMBER, parameter);
                                            if (!b) {
                                                b = eC.e(javaName, ExtraMenuBean.LIST_TYPE_STRING, parameter);
                                            }

                                            if (!b) {
                                                eC.e(javaName, ExtraMenuBean.LIST_TYPE_MAP, parameter);
                                            }
                                            break;

                                        case "view":
                                            eC.h(xmlName, parameter);
                                            break;

                                        case "textview":
                                            eC.g(xmlName, parameter);
                                            break;

                                        case "checkbox":
                                            eC.e(xmlName, parameter);
                                            break;

                                        case "imageview":
                                            eC.g(xmlName, ViewBean.VIEW_TYPE_WIDGET_IMAGEVIEW, parameter);
                                            break;

                                        case "seekbar":
                                            eC.g(xmlName, ViewBean.VIEW_TYPE_WIDGET_SEEKBAR, parameter);
                                            break;

                                        case "calendarview":
                                            eC.g(xmlName, ViewBean.VIEW_TYPE_WIDGET_CALENDARVIEW, parameter);
                                            break;

                                        case "adview":
                                            eC.g(xmlName, ViewBean.VIEW_TYPE_WIDGET_ADVIEW, parameter);
                                            break;

                                        case "listview":
                                            eC.g(xmlName, ViewBean.VIEW_TYPE_WIDGET_LISTVIEW, parameter);
                                            break;

                                        case "spinner":
                                            eC.g(xmlName, ViewBean.VIEW_TYPE_WIDGET_SPINNER, parameter);
                                            break;

                                        case "webview":
                                            eC.g(xmlName, ViewBean.VIEW_TYPE_WIDGET_WEBVIEW, parameter);
                                            break;

                                        case "switch":
                                            eC.g(xmlName, ViewBean.VIEW_TYPE_WIDGET_SWITCH, parameter);
                                            break;

                                        case "progressbar":
                                            eC.g(xmlName, ViewBean.VIEW_TYPE_WIDGET_PROGRESSBAR, parameter);
                                            break;

                                        case "mapview":
                                            eC.g(xmlName, ViewBean.VIEW_TYPE_WIDGET_MAPVIEW, parameter);
                                            break;

                                        case "intent":
                                            eC.d(javaName, ComponentBean.COMPONENT_TYPE_INTENT, parameter);
                                            break;

                                        case "file":
                                            eC.d(javaName, ComponentBean.COMPONENT_TYPE_SHAREDPREF, parameter);
                                            break;

                                        case "calendar":
                                            eC.d(javaName, ComponentBean.COMPONENT_TYPE_CALENDAR, parameter);
                                            break;

                                        case "timer":
                                            eC.d(javaName, ComponentBean.COMPONENT_TYPE_TIMERTASK, parameter);
                                            break;

                                        case "vibrator":
                                            eC.d(javaName, ComponentBean.COMPONENT_TYPE_VIBRATOR, parameter);
                                            break;

                                        case "dialog":
                                            eC.d(javaName, ComponentBean.COMPONENT_TYPE_DIALOG, parameter);
                                            break;

                                        case "mediaplayer":
                                            eC.d(javaName, ComponentBean.COMPONENT_TYPE_MEDIAPLAYER, parameter);
                                            break;

                                        case "soundpool":
                                            eC.d(javaName, ComponentBean.COMPONENT_TYPE_SOUNDPOOL, parameter);
                                            break;

                                        case "objectanimator":
                                            eC.d(javaName, ComponentBean.COMPONENT_TYPE_OBJECTANIMATOR, parameter);
                                            break;

                                        case "firebase":
                                            eC.d(javaName, ComponentBean.COMPONENT_TYPE_FIREBASE, parameter);
                                            break;

                                        case "firebaseauth":
                                            eC.d(javaName, ComponentBean.COMPONENT_TYPE_FIREBASE_AUTH, parameter);
                                            break;

                                        case "firebasestorage":
                                            eC.d(javaName, ComponentBean.COMPONENT_TYPE_FIREBASE_STORAGE, parameter);
                                            break;

                                        case "gyroscope":
                                            eC.d(javaName, ComponentBean.COMPONENT_TYPE_GYROSCOPE, parameter);
                                            break;

                                        case "interstitialad":
                                            eC.d(javaName, ComponentBean.COMPONENT_TYPE_INTERSTITIAL_AD, parameter);
                                            break;

                                        case "requestnetwork":
                                            eC.d(javaName, ComponentBean.COMPONENT_TYPE_REQUEST_NETWORK, parameter);
                                            break;

                                        case "texttospeech":
                                            eC.d(javaName, ComponentBean.COMPONENT_TYPE_TEXT_TO_SPEECH, parameter);
                                            break;

                                        case "speechtotext":
                                            eC.d(javaName, ComponentBean.COMPONENT_TYPE_SPEECH_TO_TEXT, parameter);
                                            break;

                                        case "bluetoothconnect":
                                            eC.d(javaName, ComponentBean.COMPONENT_TYPE_BLUETOOTH_CONNECT, parameter);
                                            break;

                                        case "locationmanager":
                                            eC.d(javaName, ComponentBean.COMPONENT_TYPE_LOCATION_MANAGER, parameter);
                                            break;

                                        case "resource_bg":
                                        case "resource":
                                            for (String str : jC.d(B).m()) {
                                                // Like this in vanilla Sketchware. Don't ask me why.
                                                //noinspection StatementWithEmptyBody
                                                if (parameter.equals(str)) {
                                                }
                                            }
                                            break;

                                        case "activity":
                                            for (String str : jC.b(B).d()) {
                                                // Like this in vanilla Sketchware. Don't ask me why.
                                                //noinspection StatementWithEmptyBody
                                                if (parameter.equals(str.substring(str.indexOf(".java")))) {
                                                }
                                            }
                                            break;

                                        case "sound":
                                            for (String str : jC.d(B).p()) {
                                                // Like this in vanilla Sketchware. Don't ask me why.
                                                //noinspection StatementWithEmptyBody
                                                if (parameter.equals(str)) {
                                                }
                                            }
                                            break;

                                        case "videoad":
                                            eC.d(xmlName, ComponentBean.COMPONENT_TYPE_REWARDED_VIDEO_AD, parameter);
                                            break;

                                        case "progressdialog":
                                            eC.d(xmlName, ComponentBean.COMPONENT_TYPE_PROGRESS_DIALOG, parameter);
                                            break;

                                        case "datepickerdialog":
                                            eC.d(xmlName, ComponentBean.COMPONENT_TYPE_DATE_PICKER_DIALOG, parameter);
                                            break;

                                        case "timepickerdialog":
                                            eC.d(xmlName, ComponentBean.COMPONENT_TYPE_TIME_PICKER_DIALOG, parameter);
                                            break;

                                        case "notification":
                                            eC.d(xmlName, ComponentBean.COMPONENT_TYPE_NOTIFICATION, parameter);
                                            break;

                                        case "radiobutton":
                                            eC.g(xmlName, 19, parameter);
                                            break;

                                        case "ratingbar":
                                            eC.g(xmlName, 20, parameter);
                                            break;

                                        case "videoview":
                                            eC.g(xmlName, 21, parameter);
                                            break;

                                        case "searchview":
                                            eC.g(xmlName, 22, parameter);
                                            break;

                                        case "actv":
                                            eC.g(xmlName, 23, parameter);
                                            break;

                                        case "mactv":
                                            eC.g(xmlName, 24, parameter);
                                            break;

                                        case "gridview":
                                            eC.g(xmlName, 25, parameter);
                                            break;

                                        case "tablayout":
                                            eC.g(xmlName, 30, parameter);
                                            break;

                                        case "viewpager":
                                            eC.g(xmlName, 31, parameter);
                                            break;

                                        case "bottomnavigation":
                                            eC.g(xmlName, 32, parameter);
                                            break;

                                        case "badgeview":
                                            eC.g(xmlName, 33, parameter);
                                            break;

                                        case "patternview":
                                            eC.g(xmlName, 34, parameter);
                                            break;

                                        case "sidebar":
                                            eC.g(xmlName, 35, parameter);
                                            break;

                                        default:
                                            extraPaletteBlock.e(ss.c, parameter);
                                    }
                                }
                            }

                            ss.setArgValue(parameter);
                            block.m();
                        }
                    }
                }
            }

            int subStack1RootBlockId = blockBean.subStack1;
            if (subStack1RootBlockId >= 0) {
                Rs subStack1RootBlock = o.a(subStack1RootBlockId);
                if (subStack1RootBlock != null) {
                    block.e(subStack1RootBlock);
                }
            }

            int subStack2RootBlockId = blockBean.subStack2;
            if (subStack2RootBlockId >= 0) {
                Rs subStack2RootBlock = o.a(subStack2RootBlockId);
                if (subStack2RootBlock != null) {
                    block.f(subStack2RootBlock);
                }
            }

            int nextBlockId = blockBean.nextBlock;
            if (nextBlockId >= 0) {
                Rs nextBlock = o.a(nextBlockId);
                if (nextBlock != null) {
                    block.b(nextBlock);
                }
            }

            block.m();
            if (z) {
                block.p().k();
                o.b();
            }
        }
    }

    public void a(String str, int i) {
        m.a(str, i);
    }

    public void a(String str, Rs rs) {
        ArrayList<String> arrayList;
        ArrayList<Rs> allChildren = rs.getAllChildren();
        ArrayList<BlockBean> arrayList2 = new ArrayList<>();
        for (Rs child : allChildren) {
            BlockBean blockBean = new BlockBean();
            BlockBean bean = child.getBean();
            blockBean.copy(bean);
            blockBean.id = String.format("99%06d", Integer.valueOf(bean.id));
            int i = bean.subStack1;
            if (i > 0) {
                blockBean.subStack1 = i + 99000000;
            }
            int i2 = bean.subStack2;
            if (i2 > 0) {
                blockBean.subStack2 = i2 + 99000000;
            }
            int i3 = bean.nextBlock;
            if (i3 > 0) {
                blockBean.nextBlock = i3 + 99000000;
            }
            blockBean.parameters.clear();
            for (String next : bean.parameters) {
                if (next.length() <= 1 || next.charAt(0) != '@') {
                    arrayList = blockBean.parameters;
                } else {
                    String format = String.format("99%06d", Integer.valueOf(next.substring(1)));
                    arrayList = blockBean.parameters;
                    next = '@' + format;
                }
                arrayList.add(next);
            }
            arrayList2.add(blockBean);
        }
        try {
            Mp.h().a(str, arrayList2, true);
            O.a(str, arrayList2).setOnTouchListener(this);
        } catch (Exception e) {
            // The bytecode is lying. Checked exceptions suck.
            //noinspection ConstantConditions
            if (e instanceof yy) {
                e.printStackTrace();
            } else {
                throw e;
            }
        }
    }

    public void a(boolean z) {
        logicTopMenu.setCopyActive(z);
    }

    public final boolean a(float f, float f2) {
        return logicTopMenu.isInsideCopyArea(f, f2);
    }

    public final boolean a(BlockBean blockBean) {
        if (blockBean.opCode.equals("getArg")) {
            return true;
        }
        if (blockBean.opCode.equals("definedFunc")) {
            Iterator<Pair<String, String>> it = jC.a(B).i(M.getJavaName()).iterator();
            boolean z = false;
            while (it.hasNext()) {
                if (blockBean.spec.equals(ReturnMoreblockManager.getMbName(it.next().second))) {
                    z = true;
                }
            }
            return z;
        }
        return true;
    }

    public Rs b(BlockBean blockBean) {
        return new Rs(this, Integer.parseInt(blockBean.id), blockBean.spec, blockBean.type, blockBean.typeName, blockBean.opCode);
    }

    private RadioButton getFontRadioButton(String fontName) {
        RadioButton radioButton = new RadioButton(this);
        radioButton.setText("");
        radioButton.setTag(fontName);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, (int) (wB.a(this, 1.0f) * 60.0f));
        radioButton.setGravity(Gravity.CENTER | Gravity.LEFT);
        radioButton.setLayoutParams(layoutParams);
        return radioButton;
    }

    public void b(int i, String str) {
        jC.a(B).c(M.getJavaName(), i, str);
        a(0, 0xffee7d16);
    }

    public void b(Rs rs) {
        o.b(rs);
    }

    public void b(Ss ss) {
        Zx zx = new Zx(this, (ss.getArgValue() == null || ss.getArgValue().toString().length() <= 0 || ss.getArgValue().toString().indexOf("0xFF") != 0) ? 0 : Color.parseColor(ss.getArgValue().toString().replace("0xFF", "#")), true, false, B);
        zx.a(new Zx.b() {
            @Override
            public void a(int var1) {
                if (var1 == 0) {
                    LogicEditorActivity.this.a(ss, "Color.TRANSPARENT");
                } else {
                    LogicEditorActivity.this.a(ss, String.format("0x%08X", var1 & (Color.WHITE)));
                }
            }

            @Override
            public void a(String var1, int var2) {
                LogicEditorActivity.this.a(ss, "getResources().getColor(R.color." + var1 + ")");
            }
        });
        zx.materialColorAttr((attr, attrId) -> {
            LogicEditorActivity.this.a(ss, "getMaterialColor(R.attr." + attr + ")");
        });
        zx.showAtLocation(ss, Gravity.CENTER, 0, 0);
    }

    public void b(String str, String str2) {
        TextView a2 = m.a(str);
        a2.setTag(str2);
        a2.setSoundEffectsEnabled(true);
        a2.setOnClickListener(this);
    }

    public void b(String str, String str2, View.OnClickListener onClickListener) {
        TextView a2 = m.a(str);
        a2.setTag(str2);
        a2.setSoundEffectsEnabled(true);
        a2.setOnClickListener(onClickListener);
    }

    public void b(boolean z) {
        logicTopMenu.setDeleteActive(z);
    }

    public final boolean b(float f, float f2) {
        return logicTopMenu.isInsideDeleteArea(f, f2);
    }

    public void c(Rs rs) {
        aB aBVar = new aB(this);
        aBVar.b(getTranslatedString(R.string.logic_block_favorites_save_title));
        aBVar.a(R.drawable.ic_bookmark_red_48dp);
        View a2 = wB.a(this, R.layout.property_popup_save_to_favorite);
        ((TextView) a2.findViewById(R.id.tv_favorites_guide)).setText(getTranslatedString(R.string.logic_block_favorites_save_guide));
        EditText editText = a2.findViewById(R.id.ed_input);
        editText.setPrivateImeOptions("defaultInputmode=english;");
        editText.setLines(1);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        NB nb = new NB(this, a2.findViewById(R.id.ti_input), Mp.h().g());
        aBVar.a(a2);
        aBVar.b(getTranslatedString(R.string.common_word_save), v -> {
            if (nb.b()) {
                a(editText.getText().toString(), rs);
                aBVar.dismiss();
            }
        });
        aBVar.a(getTranslatedString(R.string.common_word_cancel), Helper.getDialogDismissListener(aBVar));
        aBVar.show();
    }

    public void c(Ss ss) {
        aB aBVar = new aB(this);
        aBVar.b(getTranslatedString(R.string.logic_editor_title_enter_string_value));
        aBVar.a(R.drawable.rename_96_blue);
        View a2 = wB.a(this, R.layout.property_popup_input_text);
        ((TextInputLayout) a2.findViewById(R.id.ti_input)).setHint(getTranslatedString(R.string.property_hint_enter_value));
        EditText editText = a2.findViewById(R.id.ed_input);
        editText.setSingleLine(true);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setText(ss.getArgValue().toString());
        aBVar.a(a2);
        aBVar.b(getTranslatedString(R.string.common_word_save), v -> {
            a(ss, editText.getText().toString());
            aBVar.dismiss();
        });
        aBVar.a(getTranslatedString(R.string.common_word_cancel), Helper.getDialogDismissListener(aBVar));
        aBVar.show();
    }

    public void c(String str, String str2) {
        jC.a(B).a(M.getJavaName(), str, str2);
        a(8, 0xff8a55d7);
    }

    public void c(boolean z) {
        logicTopMenu.setDetailActive(z);
    }

    public final boolean c(float f, float f2) {
        return logicTopMenu.isInsideDetailArea(f, f2);
    }

    private LinearLayout getFontPreview(String fontName) {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (wB.a(this, 1.0f) * 60.0f)));
        linearLayout.setGravity(Gravity.CENTER | Gravity.LEFT);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView name = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1.0f;
        name.setLayoutParams(layoutParams);
        name.setText(fontName);
        linearLayout.addView(name);
        TextView preview = new TextView(this);
        preview.setLayoutParams(layoutParams);
        preview.setText("Preview");

        Typeface typeface;
        if (fontName.equalsIgnoreCase("default_font")) {
            typeface = Typeface.DEFAULT;
        } else {
            try {
                typeface = Typeface.createFromFile(jC.d(B).d(fontName));
            } catch (RuntimeException e) {
                typeface = Typeface.DEFAULT;
                preview.setText("Couldn't load font");
            }
        }

        preview.setTypeface(typeface);
        linearLayout.addView(preview);
        return linearLayout;
    }

    public RadioButton d(String type, String id) {
        if (isViewBindingEnabled) {
            id = ViewBindingBuilder.generateParameterFromId(id);
        }
        RadioButton radioButton = new RadioButton(this);
        radioButton.setText(type + " : " + id);
        radioButton.setTag(isViewBindingEnabled ? "binding." + id : id);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (wB.a(this, 1.0f) * 40.0f));
        radioButton.setGravity(Gravity.CENTER | Gravity.LEFT);
        radioButton.setLayoutParams(layoutParams);
        return radioButton;
    }

    public void d(Ss ss) {
        aB dialog = new aB(this);
        dialog.b(getTranslatedString(R.string.logic_editor_title_select_font));
        dialog.a(R.drawable.abc_96_color);

        View customView = wB.a(this, R.layout.property_popup_selector_color);
        RadioGroup radioGroup = customView.findViewById(R.id.rg);
        LinearLayout linearLayout = customView.findViewById(R.id.content);
        ArrayList<String> fontNames = jC.d(B).k();
        fontNames.add(0, "default_font");
        for (String fontName : fontNames) {
            RadioButton font = getFontRadioButton(fontName);
            radioGroup.addView(font);
            if (fontName.equals(ss.getArgValue())) {
                font.setChecked(true);
            }
            LinearLayout fontPreview = getFontPreview(fontName);
            fontPreview.setOnClickListener(v -> font.setChecked(true));
            linearLayout.addView(fontPreview);
        }

        dialog.a(customView);
        dialog.b(getTranslatedString(R.string.common_word_select), v -> {
            for (int i = 0; i < radioGroup.getChildCount(); i++) {
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
                if (radioButton.isChecked()) {
                    a(ss, radioButton.getTag());
                    break;
                }
            }
            dialog.dismiss();
        });
        dialog.a(getTranslatedString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    public void d(boolean z) {
        logicTopMenu.setFavoriteActive(z);
    }

    public final boolean d(float f, float f2) {
        return logicTopMenu.isInsideFavoriteArea(f, f2);
    }

    public final RadioButton e(String str) {
        RadioButton radioButton = new RadioButton(this);
        radioButton.setText(str);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = (int) wB.a(getContext(), 4.0f);
        layoutParams.bottomMargin = (int) wB.a(getContext(), 4.0f);
        radioButton.setGravity(Gravity.CENTER | Gravity.LEFT);
        radioButton.setLayoutParams(layoutParams);
        return radioButton;
    }

    public final CheckBox createCheckBox(String str) {
        CheckBox checkBox = new CheckBox(this);
        checkBox.setText(str);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = (int) wB.a(getContext(), 4.0f);
        layoutParams.bottomMargin = (int) wB.a(getContext(), 4.0f);
        checkBox.setGravity(Gravity.CENTER | Gravity.LEFT);
        checkBox.setLayoutParams(layoutParams);
        return checkBox;
    }

    public void e(Ss ss) {
        aB aBVar = new aB(this);
        aBVar.b(getTranslatedString(R.string.logic_editor_title_enter_data_value));
        aBVar.a(R.drawable.rename_96_blue);
        View a2 = wB.a(this, R.layout.property_popup_input_intent_data);
        ((TextView) a2.findViewById(R.id.tv_desc_intent_usage)).setText(getTranslatedString(R.string.property_description_component_intent_usage));
        EditText editText = a2.findViewById(R.id.ed_input);
        ((TextInputLayout) a2.findViewById(R.id.ti_input)).setHint(getTranslatedString(R.string.property_hint_enter_value));
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editText.setText(ss.getArgValue().toString());
        aBVar.a(a2);
        aBVar.b(getTranslatedString(R.string.common_word_save), v -> {
            a(ss, editText.getText().toString());
            aBVar.dismiss();
        });
        aBVar.a(getTranslatedString(R.string.common_word_cancel), Helper.getDialogDismissListener(aBVar));
        aBVar.show();
    }

    public void e(boolean z) {
        ObjectAnimator objectAnimator;
        if (!W) {
            h(getResources().getConfiguration().orientation);
        }
        if (X == z) {
            return;
        }
        X = z;
        n();
        if (z) {
            g(false);
            objectAnimator = U;
        } else {
            objectAnimator = V;
        }
        objectAnimator.start();
        f(getResources().getConfiguration().orientation);
    }

    public void f(int i) {
        LinearLayout.LayoutParams layoutParams;
        int a2;
        int i2 = ViewGroup.LayoutParams.MATCH_PARENT;
        if (X) {
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            if (width <= height) {
                width = height;
            }
            if (2 == i) {
                i2 = width - ((int) wB.a(this, 320.0f));
                a2 = ViewGroup.LayoutParams.MATCH_PARENT;
            } else {
                a2 = ((width - GB.a(getContext())) - GB.f(getContext())) - ((int) wB.a(this, 240.0f));
            }
            layoutParams = new LinearLayout.LayoutParams(i2, a2);
        } else {
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        n.setLayoutParams(layoutParams);
        n.requestLayout();
    }

    public void f(Ss ss) {
        aB dialog = new aB(this);
        View customView = wB.a(this, R.layout.property_popup_selector_single);
        ViewGroup viewGroup = customView.findViewById(R.id.rg_content);
        String xmlName = M.getXmlName();

        if (D.equals("onBindCustomView")) {
            var eC = jC.a(B);
            var view = eC.c(xmlName, C);
            if (view == null) {
                view = eC.c("_drawer_" + xmlName, C);
            }
            if (view != null && view.customView != null) {
                xmlName = ProjectFileBean.getXmlName(view.customView);
            }
        }

        dialog.b(getTranslatedString(R.string.logic_editor_title_select_view));
        ArrayList<ViewBean> views = jC.a(B).d(xmlName);
        for (ViewBean viewBean : views) {
            String convert = viewBean.convert;
            String typeName = convert.isEmpty() ? ViewBean.getViewTypeName(viewBean.type) : IdGenerator.getLastPath(convert);
            if (!convert.equals("include")) {
                Set<String> toNotAdd = new Ox(new jq(), M).readAttributesToReplace(viewBean);
                if (!toNotAdd.contains("android:id")) {
                    String classInfo = ss.getClassInfo().a();
                    if ((classInfo.equals("CheckBox") && viewBean.getClassInfo().a("CompoundButton")) || viewBean.getClassInfo().a(classInfo)) {
                        viewGroup.addView(d(typeName, viewBean.id));
                    }
                }
                ExtraMenuBean.setupSearchView(customView, viewGroup);
            }
        }

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) viewGroup.getChildAt(i);
            String argValue = ss.getArgValue().toString();
            if (argValue.equals(radioButton.getTag().toString())) {
                radioButton.setChecked(true);
                break;
            }
        }

        dialog.a(customView);
        dialog.configureDefaultButton("Code Editor", v -> {
            AsdDialog editor = new AsdDialog(this);
            editor.setCon(ss.getArgValue().toString());
            editor.show();
            editor.saveLis(this, false, ss, editor);;
            editor.cancelLis(editor);
            dialog.dismiss();
        });
        dialog.b(getTranslatedString(R.string.common_word_select), v -> {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                RadioButton radioButton = (RadioButton) viewGroup.getChildAt(i);
                if (radioButton.isChecked()) {
                    a(ss, radioButton.getTag());
                    break;
                }
            }
            dialog.dismiss();
        });
        dialog.a(getTranslatedString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    public void f(boolean z) {
        logicTopMenu.toggleLayoutVisibility(z);
    }

    @Override
    public void finish() {
        bC.d(B).b(s());
        super.finish();
    }

    private Context getContext() {
        return getApplicationContext();
    }

    public void g(int i) {
        RelativeLayout.LayoutParams layoutParams;
        int orientation;
        if (2 == i) {
            K.setLayoutParams(new LinearLayout.LayoutParams((int) wB.a(this, 320.0f), ViewGroup.LayoutParams.MATCH_PARENT));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER | Gravity.BOTTOM;
            int dimension = (int) getResources().getDimension(R.dimen.action_button_margin);
            params.setMargins(dimension, dimension, dimension, dimension);
            openBlocksMenuButton.setLayoutParams(params);
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layoutParams.topMargin = GB.a(getContext());
            orientation = LinearLayout.HORIZONTAL;
        } else {
            K.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) wB.a(this, 240.0f)));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER | Gravity.RIGHT;
            int dimension2 = (int) getResources().getDimension(R.dimen.action_button_margin);
            params.setMargins(dimension2, dimension2, dimension2, dimension2);
            openBlocksMenuButton.setLayoutParams(params);
            layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            orientation = LinearLayout.VERTICAL;
        }
        J.setOrientation(orientation);
        J.setLayoutParams(layoutParams);
        h(i);
        f(i);
    }

    public void g(boolean z) {
        if (!ha) {
            t();
        }
        if (ia != z) {
            ia = z;
            l();
            (z ? fa : ga).start();
        }
    }

    public void h(int i) {
        boolean var2 = X;
        if (i == 2) {
            if (!var2) {
                J.setTranslationX(wB.a(this, 320.0F));
            } else {
                J.setTranslationX(0.0F);
            }
            J.setTranslationY(0.0F);
        } else {
            if (!var2) {
                J.setTranslationX(0.0F);
                J.setTranslationY(wB.a(this, 240.0F));
            } else {
                J.setTranslationX(0.0F);
                J.setTranslationY(0.0F);
            }
        }

        if (i == 2) {
            U = ObjectAnimator.ofFloat(J, View.TRANSLATION_X, 0.0F);
            V = ObjectAnimator.ofFloat(J, View.TRANSLATION_X, wB.a(this, 320.0F));
        } else {
            U = ObjectAnimator.ofFloat(J, View.TRANSLATION_Y, 0.0F);
            V = ObjectAnimator.ofFloat(J, View.TRANSLATION_Y, wB.a(this, 240.0F));
        }

        U.setDuration(500L);
        U.setInterpolator(new DecelerateInterpolator());
        V.setDuration(300L);
        V.setInterpolator(new DecelerateInterpolator());
        W = true;
    }

    public void h(Ss ss) {
        aB dialog = new aB(this);
        dialog.b(getTranslatedString(R.string.logic_editor_title_select_sound));
        dialog.a(R.drawable.music_48);

        View customView = wB.a(this, R.layout.property_popup_selector_single);
        RadioGroup radioGroup = customView.findViewById(R.id.rg_content);
        SoundPool soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build())
                .build();
        soundPool.setOnLoadCompleteListener((soundPool1, sampleId, status) -> {
            if (soundPool1 != null) {
                soundPool1.play(sampleId, 1, 1, 1, 0, 1);
            }
        });

        for (String soundName : jC.d(B).p()) {
            RadioButton sound = e(soundName);
            radioGroup.addView(sound);
            if (soundName.equals(ss.getArgValue())) {
                sound.setChecked(true);
            }
            sound.setOnClickListener(v -> soundPool.load(jC.d(B).i(sound.getText().toString()), 1));
        }
        dialog.a(customView);
        dialog.b(getTranslatedString(R.string.common_word_select), v -> {
            RadioButton checkedRadioButton = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
            a(ss, checkedRadioButton.getText().toString());
            dialog.dismiss();
        });
        dialog.a(getTranslatedString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    public void h(boolean z) {
        logicTopMenu.setDeleteActive(false);
        logicTopMenu.setCopyActive(false);
        logicTopMenu.setFavoriteActive(false);
        logicTopMenu.setDetailActive(false);
        if (!da) {
            x();
        }
        if (ea == z) {
            return;
        }
        ea = z;
        m();
        (z ? ba : ca).start();
    }

    public void i(Ss ss) {
        aB aBVar = new aB(this);
        aBVar.b(getTranslatedString(R.string.logic_editor_title_select_typeface));
        aBVar.a(R.drawable.abc_96_color);
        View a3 = wB.a(this, R.layout.property_popup_selector_single);
        RadioGroup radioGroup = a3.findViewById(R.id.rg_content);
        for (Pair<Integer, String> pair : sq.a("property_text_style")) {
            RadioButton e = e(pair.second);
            radioGroup.addView(e);
            if (pair.second.equals(ss.getArgValue())) {
                e.setChecked(true);
            }
        }
        aBVar.a(a3);
        aBVar.b(getTranslatedString(R.string.common_word_save), v -> {
            int childCount = radioGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
                if (radioButton.isChecked()) {
                    a(ss, radioButton.getText().toString());
                    break;
                }
            }

            aBVar.dismiss();
        });
        aBVar.a(getTranslatedString(R.string.common_word_cancel), Helper.getDialogDismissListener(aBVar));
        aBVar.show();
    }

    public void l() {
        if (fa.isRunning()) {
            fa.cancel();
        }
        if (ga.isRunning()) {
            ga.cancel();
        }
    }

    public void l(String str) {
        jC.a(B).o(M.getJavaName(), str);
        a(1, 0xffcc5b22);
    }

    public void m() {
        if (ba.isRunning()) {
            ba.cancel();
        }
        if (ca.isRunning()) {
            ca.cancel();
        }
    }

    public void m(String str) {
        jC.a(B).p(M.getJavaName(), str);
        a(0, 0xffee7d16);
    }

    public void n() {
        if (U.isRunning()) {
            U.cancel();
        }
        if (V.isRunning()) {
            V.cancel();
        }
    }

    public void n(String str) {
        aB aBVar = new aB(this);
        aBVar.b(getTranslatedString(R.string.logic_block_favorites_delete_title));
        aBVar.a(R.drawable.high_priority_96_red);
        aBVar.a(getTranslatedString(R.string.logic_block_favorites_delete_message));
        aBVar.b(getTranslatedString(R.string.common_word_delete), v -> {
            Mp.h().a(str, true);
            O.a(str);
            aBVar.dismiss();
        });
        aBVar.a(getTranslatedString(R.string.common_word_cancel), Helper.getDialogDismissListener(aBVar));
        aBVar.show();
    }

    public void o(String str) {
        Intent intent = new Intent(getContext(), ShowBlockCollectionActivity.class);
        intent.putExtra("block_name", str);
        startActivity(intent);
    }

    public boolean o() {
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 222) {
                c(data.getStringExtra("block_name"), data.getStringExtra("block_spec"));
            } else if (requestCode == 224) {
                a(7, 0xff2ca5e2);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (ia) {
            g(false);
            return;
        }
        if (X) {
            e(false);
            return;
        }
        k();
        if (!p()) {
            return;
        }
        L();
    }

    @Override
    public void onClick(View v) {
        if (!mB.a()) {
            Object tag = v.getTag();
            if (tag != null) {
                if (tag.equals("variableAdd")) {
                    showAddNewVariableDialog();
                } else if (tag.equals("variableRemove")) {
                    K();
                } else if (tag.equals("XmlString.Add")) {
                    showAddNewXmlStringDialog();
                } else if (tag.equals("XmlString.remove")) {
                    showRemoveXmlStringDialog();
                } else if (tag.equals("openResourcesEditor")) {
                    openResourcesEditor();
                } else if (tag.equals("listAdd")) {
                    G();
                } else if (tag.equals("listRemove")) {
                    J();
                } else if (tag.equals("blockAdd")) {
                    Intent intent = new Intent(getContext(), MakeBlockActivity.class);
                    intent.putExtra("sc_id", B);
                    intent.putExtra("project_file", M);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivityForResult(intent, 222);
                } else if (tag.equals("componentAdd")) {
                    Intent intent = new Intent(getContext(), ComponentAddActivity.class);
                    intent.putExtra("sc_id", B);
                    intent.putExtra("project_file", M);
                    intent.putExtra("filename", M.getJavaName());
                    startActivityForResult(intent, 224);
                } else if (tag.equals("blockImport")) {
                    I();
                }
            }
            int id = v.getId();
            if (id == R.id.btn_delete) {
                setResult(Activity.RESULT_OK, new Intent());
                finish();
            } else if (id == R.id.btn_cancel) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);
        g(configuration.orientation);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logic_editor);
        if (!super.isStoragePermissionGranted()) {
            finish();
        }
        Parcelable parcelable;
        if (savedInstanceState == null) {
            B = getIntent().getStringExtra("sc_id");
            C = getIntent().getStringExtra("id");
            D = getIntent().getStringExtra("event");
            parcelable = getIntent().getParcelableExtra("project_file");
        } else {
            B = savedInstanceState.getString("sc_id");
            C = savedInstanceState.getString("id");
            D = savedInstanceState.getString("event");
            parcelable = savedInstanceState.getParcelable("project_file");
        }
        isViewBindingEnabled = new ProjectSettings(B).getValue(ProjectSettings.SETTING_ENABLE_VIEWBINDING, "false").equals("true");
        M = (ProjectFileBean) parcelable;
        T = (int) wB.a(getBaseContext(), (float) T);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            if (!mB.a()) {
                onBackPressed();
            }
        });
        G = new DB(getContext(), "P12").a("P12I0", true);
        A = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        F = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        String eventText = getIntent().getStringExtra("event_text");
        toolbar.setTitle(C.equals("_fab") ? "fab" : ReturnMoreblockManager.getMbName(C));
        toolbar.setSubtitle(eventText);
        paletteSelector = findViewById(R.id.palette_selector);
        paletteSelector.setOnBlockCategorySelectListener(this);
        m = findViewById(R.id.palette_block);
        p = findViewById(R.id.dummy);
        n = findViewById(R.id.editor);
        o = n.getBlockPane();
        J = findViewById(R.id.layout_palette);
        K = findViewById(R.id.area_palette);
        openBlocksMenuButton = findViewById(R.id.fab_toggle_palette);
        openBlocksMenuButton.setOnClickListener(v -> e(!X));
        logicTopMenu = findViewById(R.id.top_menu);
        O = findViewById(R.id.right_drawer);
        extraPaletteBlock = new ExtraPaletteBlock(this, isViewBindingEnabled);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logic_menu, menu);
        menu.findItem(R.id.menu_logic_redo).setEnabled(false);
        menu.findItem(R.id.menu_logic_undo).setEnabled(false);
        if (M == null) {
            return true;
        }
        menu.findItem(R.id.menu_logic_redo).setEnabled(bC.d(B).g(s()));
        menu.findItem(R.id.menu_logic_undo).setEnabled(bC.d(B).h(s()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        int itemId = menuItem.getItemId();

        if (itemId == R.id.menu_block_helper) {
            e(false);
            g(!ia);
        } else if (itemId == R.id.menu_logic_redo) {
            redo();
        } else if (itemId == R.id.menu_logic_undo) {
            undo();
        } else if (itemId == R.id.menu_logic_showsource) {
            showSourceCode();
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onPostCreate(Bundle bundle) {
        super.onPostCreate(bundle);

        String title;
        if (D.equals("moreBlock")) {
            title = getTranslatedString(R.string.root_spec_common_define) + " " + ReturnMoreblockManager.getLogicEditorTitle(jC.a(B).b(M.getJavaName(), C));
        } else if (C.equals("_fab")) {
            title = xB.b().a(getContext(), "fab", D);
        } else {
            title = xB.b().a(getContext(), C, D);
        }
        String e1 = title;

        o.a(e1, D);

        ArrayList<String> spec = FB.c(e1);
        int blockId = 0;
        for (int i = 0; i < spec.size(); i++) {
            String specBit = spec.get(i);
            if (specBit.charAt(0) == '%') {
                Rs block = BlockUtil.getVariableBlock(getContext(), blockId + 1, specBit, "getArg");
                if (block != null) {
                    block.setBlockType(1);
                    o.addView(block);
                    o.getRoot().a((Ts) o.getRoot().V.get(blockId), block);
                    block.setOnTouchListener(this);
                    blockId++;
                }
            }
        }

        o.getRoot().k();
        g(getResources().getConfiguration().orientation);
        a(0, 0xffee7d16);
        loadEventBlocks();
        z();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!super.isStoragePermissionGranted()) {
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putString("sc_id", B);
        bundle.putString("id", C);
        bundle.putString("event", D);
        bundle.putParcelable("project_file", M);
        super.onSaveInstanceState(bundle);
        ArrayList<BlockBean> blocks = o.getBlocks();
        eC a2 = jC.a(B);
        String javaName = M.getJavaName();
        a2.a(javaName, C + "_" + D, blocks);
        jC.a(B).k();
    }

    @Override
    public void onSelected(MoreBlockCollectionBean moreBlockCollectionBean) {
        new MoreblockImporter(this, B, M).importMoreblock(moreBlockCollectionBean, () -> a(8, 0xff8a55d7));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int actionMasked = event.getActionMasked();
        if (event.getPointerId(event.getActionIndex()) > 0) {
            return true;
        }
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            u = false;
            Z.postDelayed(aa, ViewConfiguration.getLongPressTimeout() / 2);
            int[] locationOnScreen = new int[2];
            v.getLocationOnScreen(locationOnScreen);
            s = locationOnScreen[0];
            t = locationOnScreen[1];
            q = event.getRawX();
            r = event.getRawY();
            Y = v;
            return true;
        }
        if (actionMasked == MotionEvent.ACTION_MOVE) {
            if (!u) {
                if (Math.abs(q - s - event.getX()) >= A || Math.abs(r - t - event.getY()) >= A) {
                    Y = null;
                    Z.removeCallbacks(aa);
                }
                return false;
            }
            Z.removeCallbacks(aa);
            float rawX = event.getRawX();
            float rawY = event.getRawY();
            p.a(v, rawX - s, rawY - t, q - s, r - t, S, T);
            if (b(event.getRawX(), event.getRawY())) {
                p.setAllow(true);
                b(true);
                a(false);
                d(false);
                c(false);
                return true;
            }
            b(false);
            if (a(event.getRawX(), event.getRawY())) {
                p.setAllow(true);
                a(true);
                d(false);
                c(false);
                return true;
            }
            a(false);
            if (d(event.getRawX(), event.getRawY())) {
                p.setAllow(true);
                d(true);
                c(false);
                return true;
            }
            d(false);
            if (c(event.getRawX(), event.getRawY())) {
                p.setAllow(true);
                c(true);
                return true;
            }
            c(false);
            p.a(this.v);
            if (n.a(this.v[0], this.v[1])) {
                p.setAllow(true);
                o.c((Rs) v, this.v[0], this.v[1]);
            } else {
                p.setAllow(false);
                o.d();
            }
            return true;
        } else if (actionMasked == MotionEvent.ACTION_UP) {
            Y = null;
            Z.removeCallbacks(aa);
            if (!u) {
                if (v instanceof Rs rs) {
                    if (rs.getBlockType() == 0) {
                        a(rs, event.getX(), event.getY());
                    }
                }
                return false;
            }
            m.setDragEnabled(true);
            n.setScrollEnabled(true);
            O.setDragEnabled(true);
            p.setDummyVisibility(View.GONE);
            if (!p.getAllow()) {
                Rs rs2 = (Rs) v;
                if (rs2.getBlockType() == 0) {
                    o.a(rs2, 0);
                    if (w != null) {
                        if (x == 0) {
                            w.ha = (Integer) v.getTag();
                        }
                        if (x == 2) {
                            w.ia = (Integer) v.getTag();
                        }
                        if (x == 3) {
                            w.ja = (Integer) v.getTag();
                        }
                        if (x == 5) {
                            w.a((Ts) w.V.get(y), rs2);
                        }
                        rs2.E = w;
                        w.p().k();
                    } else {
                        rs2.p().k();
                    }
                }
                q();
            } else if (logicTopMenu.isDeleteActive) {
                Rs rs5 = (Rs) v;
                if (rs5.getBlockType() == 2) {
                    g(true);
                    n(rs5.T);
                } else {
                    b(false);
                    int id = Integer.parseInt(rs5.getBean().id);
                    BlockBean blockBean2;
                    if (w != null) {
                        BlockBean clone = w.getBean().clone();
                        if (x == 0) {
                            clone.nextBlock = id;
                        } else if (x == 2) {
                            clone.subStack1 = id;
                        } else if (x == 3) {
                            clone.subStack2 = id;
                        } else if (x == 5) {
                            clone.parameters.set(y, "@" + id);
                        }
                        blockBean2 = clone;
                    } else {
                        blockBean2 = null;
                    }
                    ArrayList<BlockBean> arrayList = new ArrayList<>();
                    for (Rs allChild : rs5.getAllChildren()) {
                        arrayList.add(allChild.getBean().clone());
                    }
                    b(rs5);
                    BlockBean blockBean3 = null;
                    if (w != null) {
                        blockBean3 = w.getBean().clone();
                    }
                    int[] oLocationOnScreen = new int[2];
                    o.getLocationOnScreen(oLocationOnScreen);
                    bC.d(B).b(s(), arrayList, ((int) s) - oLocationOnScreen[0], ((int) t) - oLocationOnScreen[1], blockBean2, blockBean3);
                    C();
                }
            } else if (logicTopMenu.isFavoriteActive) {
                d(false);
                Rs rs7 = (Rs) v;
                o.a(rs7, 0);
                if (w != null) {
                    if (x == 0) {
                        w.ha = (Integer) v.getTag();
                    }
                    if (x == 2) {
                        w.ia = (Integer) v.getTag();
                    }
                    if (x == 3) {
                        w.ja = (Integer) v.getTag();
                    }
                    if (x == 5) {
                        w.a((Ts) w.V.get(y), rs7);
                    }
                    rs7.E = w;
                    w.p().k();
                } else {
                    rs7.p().k();
                }
                c(rs7);
            } else if (logicTopMenu.isDetailActive) {
                c(false);
                if (v instanceof Us) {
                    o(((Us) v).T);
                }
            } else if (logicTopMenu.isCopyActive) {
                a(false);
                Rs rs10 = (Rs) v;
                o.a(rs10, 0);
                if (w != null) {
                    if (x == 0) {
                        w.ha = (Integer) v.getTag();
                    }
                    if (x == 2) {
                        w.ia = (Integer) v.getTag();
                    }
                    if (x == 3) {
                        w.ja = (Integer) v.getTag();
                    }
                    if (x == 5) {
                        w.a((Ts) w.V.get(y), rs10);
                    }
                    rs10.E = w;
                    w.p().k();
                } else {
                    rs10.p().k();
                }
                ArrayList<BlockBean> arrayList2 = new ArrayList<>();
                for (Rs rs : rs10.getAllChildren()) {
                    BlockBean clone2 = rs.getBean().clone();
                    clone2.id = String.valueOf(Integer.parseInt(clone2.id) + 99000000);
                    if (clone2.nextBlock > 0) {
                        clone2.nextBlock = clone2.nextBlock + 99000000;
                    }
                    if (clone2.subStack1 > 0) {
                        clone2.subStack1 = clone2.subStack1 + 99000000;
                    }
                    if (clone2.subStack2 > 0) {
                        clone2.subStack2 = clone2.subStack2 + 99000000;
                    }
                    for (int i = 0; i < clone2.parameters.size(); i++) {
                        String parameter = clone2.parameters.get(i);
                        if (parameter != null && !parameter.isEmpty() && parameter.charAt(0) == '@') {
                            clone2.parameters.set(i, "@" + (Integer.parseInt(parameter.substring(1)) + 99000000));
                        }
                    }
                    arrayList2.add(clone2);
                }
                int[] nLocationOnScreen = new int[2];
                n.getLocationOnScreen(nLocationOnScreen);
                int width = nLocationOnScreen[0] + (n.getWidth() / 2);
                int a2 = nLocationOnScreen[1] + ((int) wB.a(getContext(), 4.0f));
                ArrayList<BlockBean> a3 = a(arrayList2, width, a2, true);
                int[] oLocationOnScreen = new int[2];
                o.getLocationOnScreen(oLocationOnScreen);
                bC.d(B).a(s(), a3, width - oLocationOnScreen[0], a2 - oLocationOnScreen[1], null, null);
                C();
            } else if (v instanceof Rs rs13) {
                p.a(this.v);
                if (rs13.getBlockType() == 1) {
                    int addTargetId = o.getAddTargetId();
                    BlockBean clone3 = addTargetId >= 0 ? o.a(addTargetId).getBean().clone() : null;
                    Rs a4 = a(rs13, this.v[0], this.v[1], false);
                    BlockBean blockBean3 = null;
                    if (addTargetId >= 0) {
                        blockBean3 = o.a(addTargetId).getBean().clone();
                    }
                    int[] locationOnScreen = new int[2];
                    o.getLocationOnScreen(locationOnScreen);
                    bC.d(B).a(s(), a4.getBean().clone(), this.v[0] - locationOnScreen[0], this.v[1] - locationOnScreen[1], clone3, blockBean3);
                    if (clone3 != null) {
                        clone3.print();
                    }
                    if (blockBean3 != null) {
                        blockBean3.print();
                    }
                } else if (rs13.getBlockType() == 2) {
                    int addTargetId2 = o.getAddTargetId();
                    BlockBean clone5 = addTargetId2 >= 0 ? o.a(addTargetId2).getBean().clone() : null;
                    ArrayList<BlockBean> data = ((Us) v).getData();
                    ArrayList<BlockBean> a5 = a(data, this.v[0], this.v[1], true);
                    if (!a5.isEmpty()) {
                        Rs a6 = o.a(a5.get(0).id);
                        a(a6, this.v[0], this.v[1], true);
                        BlockBean blockBean3 = null;
                        if (addTargetId2 >= 0) {
                            blockBean3 = o.a(addTargetId2).getBean().clone();
                        }
                        int[] locationOnScreen = new int[2];
                        o.getLocationOnScreen(locationOnScreen);
                        bC.d(B).a(s(), a5, this.v[0] - locationOnScreen[0], this.v[1] - locationOnScreen[1], clone5, blockBean3);
                    }
                    o.c();
                } else {
                    o.a(rs13, 0);
                    int id = Integer.parseInt(rs13.getBean().id);
                    BlockBean blockBean;
                    if (w != null) {
                        blockBean = w.getBean().clone();
                        if (x == 0) {
                            blockBean.nextBlock = id;
                        } else if (x == 2) {
                            blockBean.subStack1 = id;
                        } else if (x == 3) {
                            blockBean.subStack2 = id;
                        } else if (x == 5) {
                            blockBean.parameters.set(y, "@" + id);
                        }
                    } else {
                        blockBean = null;
                    }
                    Rs a7 = o.a(o.getAddTargetId());
                    BlockBean clone6 = a7 != null ? a7.getBean().clone() : null;
                    ArrayList<Rs> allChildren3 = rs13.getAllChildren();
                    ArrayList<BlockBean> arrayList3 = new ArrayList<>();
                    for (Rs rs : allChildren3) {
                        arrayList3.add(rs.getBean().clone());
                    }
                    a(rs13, this.v[0], this.v[1], true);
                    ArrayList<BlockBean> arrayList4 = new ArrayList<>();
                    for (Rs rs : allChildren3) {
                        arrayList4.add(rs.getBean().clone());
                    }
                    BlockBean clone7 = w != null ? w.getBean().clone() : null;
                    BlockBean blockBean3 = null;
                    if (a7 != null) {
                        blockBean3 = a7.getBean().clone();
                    }
                    if (blockBean == null || clone7 == null || !blockBean.isEqual(clone7)) {
                        int[] locationOnScreen = new int[2];
                        o.getLocationOnScreen(locationOnScreen);
                        int x = locationOnScreen[0];
                        int y = locationOnScreen[1];
                        bC.d(B).a(s(), arrayList3, arrayList4, ((int) s) - x, ((int) t) - y, this.v[0] - x, this.v[1] - y, blockBean, clone7, clone6, blockBean3);
                    }
                    o.c();
                }
                C();
                o.c();
            }
            p.setAllow(false);
            h(false);
            u = false;
            return true;
        } else if (actionMasked == MotionEvent.ACTION_CANCEL) {
            Z.removeCallbacks(aa);
            u = false;
            return false;
        } else if (actionMasked == MotionEvent.ACTION_SCROLL) {
            Z.removeCallbacks(aa);
            u = false;
            return false;
        } else {
            return true;
        }
    }

    public boolean p() {
        return true;
    }

    public void q() {
    }

    private void r() {
        if (Y != null) {
            m.setDragEnabled(false);
            n.setScrollEnabled(false);
            O.setDragEnabled(false);
            if (ia) {
                g(false);
            }

            if (G) {
                F.vibrate(100L);
            }

            u = true;
            if (((Rs) Y).getBlockType() == 0) {
                a((Rs) Y);
                f(true);
                h(true);
                p.a((Rs) Y);
                o.a((Rs) Y, 8);
                o.c((Rs) Y);
                o.a((Rs) Y);
            } else if (((Rs) Y).getBlockType() == 2) {
                f(false);
                h(true);
                p.a((Rs) Y);
                o.a((Rs) Y, ((Us) Y).getData());
            } else {
                p.a((Rs) Y);
                o.a((Rs) Y);
            }

            float a = q - s;
            float b = r - t;
            p.a(Y, a, b, a, b, S, T);
            p.a(v);
            if (n.a(v[0], v[1])) {
                p.setAllow(true);
                o.c((Rs) Y, v[0], v[1]);
            } else {
                p.setAllow(false);
                o.d();
            }
        }
    }

    public final String s() {
        return bC.a(M.getJavaName(), C, D);
    }

    public void showSourceCode() {
        yq yq = new yq(this, B);
        yq.a(jC.c(B), jC.b(B), jC.a(B), false);
        String code = new Fx(M.getActivityName(), yq.N, o.getBlocks(), isViewBindingEnabled).a();
        var intent = new Intent(this, CodeViewerActivity.class);
        intent.putExtra("code", code);
        intent.putExtra("sc_id", B);
        intent.putExtra("scheme", CodeViewerActivity.SCHEME_JAVA);
        startActivity(intent);
    }

    public void t() {
        fa = ObjectAnimator.ofFloat(O, View.TRANSLATION_X, 0.0f);
        fa.setDuration(500L);
        fa.setInterpolator(new DecelerateInterpolator());
        ga = ObjectAnimator.ofFloat(O, View.TRANSLATION_X, O.getHeight());
        ga.setDuration(300L);
        ga.setInterpolator(new DecelerateInterpolator());
        ha = true;
    }

    public void x() {
        ba = ObjectAnimator.ofFloat(logicTopMenu, View.TRANSLATION_Y, 0.0f);
        ba.setDuration(500L);
        ba.setInterpolator(new DecelerateInterpolator());
        ca = ObjectAnimator.ofFloat(logicTopMenu, View.TRANSLATION_Y, logicTopMenu.getHeight() * (-1));
        ca.setDuration(300L);
        ca.setInterpolator(new DecelerateInterpolator());
        da = true;
    }

    public void z() {
        O.a();
        for (BlockCollectionBean next : Mp.h().f()) {
            O.a(next.name, next.blocks).setOnTouchListener(this);
        }
    }

    private static class ProjectSaver extends MA {
        private final WeakReference<LogicEditorActivity> activity;

        public ProjectSaver(LogicEditorActivity logicEditorActivity) {
            super(logicEditorActivity);
            activity = new WeakReference<>(logicEditorActivity);
            logicEditorActivity.addTask(this);
        }

        @Override
        public void a() {
            activity.get().h();
            activity.get().finish();
        }

        @Override
        public void a(String str) {
            Toast.makeText(a, xB.b().a(activity.get().getContext(), R.string.common_error_failed_to_save), Toast.LENGTH_SHORT).show();
            activity.get().h();
        }

        @Override
        public void b() {
            publishProgress("Now saving..");
            activity.get().E();
        }
    }
}

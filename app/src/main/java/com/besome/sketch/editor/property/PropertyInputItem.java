package com.besome.sketch.editor.property;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.besome.sketch.beans.ViewBean;
import com.besome.sketch.beans.ProjectFileBean;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import a.a.a.Jx;
import a.a.a.Kw;
import a.a.a.OB;
import a.a.a.SB;
import a.a.a.TB;
import a.a.a._B;
import a.a.a.aB;
import a.a.a.jC;
import a.a.a.lC;
import a.a.a.mB;
import a.a.a.uq;
import a.a.a.wB;
import a.a.a.yB;
import mod.hey.studios.util.Helper;
import pro.sketchware.R;
import pro.sketchware.activities.resources.editors.utils.StringsEditorManager;
import pro.sketchware.databinding.PropertyInputItemBinding;
import pro.sketchware.databinding.PropertyPopupInputTextBinding;
import pro.sketchware.databinding.PropertyPopupParentAttrBinding;
import pro.sketchware.lib.base.BaseTextWatcher;
import pro.sketchware.lib.highlighter.SyntaxScheme;
import pro.sketchware.utility.FileUtil;

@SuppressLint("ViewConstructor")
public class PropertyInputItem extends RelativeLayout implements View.OnClickListener {

    private final String stringsStart = "@string/";
    private final ArrayList<HashMap<String, Object>> stringsListMap = new ArrayList<>();
    private Context context;
    private String typeView = "";
    private String key = "";
    private String value = "";
    private ImageView imgLeftIcon;
    private int icon;
    private TextView tvName;
    private TextView tvValue;
    private View propertyItem;
    private View propertyMenuItem;
    private String sc_id;
    private ProjectFileBean projectFileBean;
    private Kw valueChangeListener;
    private List<String> keysList = new ArrayList<>();
    private ViewBean bean;

    public PropertyInputItem(Context context, boolean z) {
        super(context);
        initialize(context, z);
    }

    private void setIcon(ImageView imageView) {
        switch (key) {
            case "property_id" -> icon = R.drawable.ic_mtrl_id;
            case "property_text" -> icon = R.drawable.ic_mtrl_text_select;
            case "property_hint" -> icon = R.drawable.ic_mtrl_bulb;
            case "property_weight", "property_weight_sum" -> icon = R.drawable.ic_mtrl_weight;
            case "property_rotate" -> icon = R.drawable.ic_mtrl_rotate;
            case "property_lines" -> icon = R.drawable.ic_mtrl_numbers;
            case "property_progress" -> icon = R.drawable.ic_mtrl_prog_min;
            case "property_max" -> icon = R.drawable.ic_mtrl_prog_max;
            case "property_alpha" -> icon = R.drawable.ic_mtrl_opacity_full;
            case "property_translation_x" -> icon = R.drawable.ic_mtrl_move_x;
            case "property_translation_y" -> icon = R.drawable.ic_mtrl_move_y;
            case "property_scale_y" -> icon = R.drawable.ic_mtrl_scale_y;
            case "property_scale_x" -> icon = R.drawable.ic_mtrl_scale_x;
            case "property_inject" -> icon = R.drawable.ic_mtrl_code;
            case "property_convert" -> icon = R.drawable.ic_mtrl_switch;
        }
        imageView.setImageResource(icon);
    }

    public void setTypeView(String typeView) {
        this.typeView = typeView;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
        int identifier = getResources().getIdentifier(key, "string", getContext().getPackageName());
        if (identifier > 0) {
            tvName.setText(Helper.getResString(identifier));
            if (propertyMenuItem.getVisibility() == VISIBLE) {
                setIcon(findViewById(R.id.img_icon));
                ((TextView) findViewById(R.id.tv_title)).setText(Helper.getResString(identifier));
                return;
            }
            setIcon(imgLeftIcon);
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        tvValue.setText(value);
    }

    public void setBean(ViewBean bean) {
        this.bean = bean;
    }

    @Override
    public void onClick(View v) {
        if (!mB.a()) {
            switch (key) {
                case "property_id" -> showViewIdDialog();
                case "property_text", "property_hint" -> showTextInputDialog(9999, false);
                case "property_weight", "property_weight_sum", "property_rotate", "property_lines",
                     "property_max", "property_progress" -> showNumberInputDialog();
                case "property_alpha" -> showNumberDecimalInputDialog(0, 1);
                case "property_translation_x", "property_translation_y" ->
                        showNumberDecimalInputDialog(-9999, 9999);
                case "property_scale_x", "property_scale_y" -> showNumberDecimalInputDialog(0, 99);
                case "property_convert" -> showAutoCompleteDialog();
                case "property_inject" -> showInjectDialog();
            }
        }
    }

    public void setOnPropertyValueChangeListener(Kw onPropertyValueChangeListener) {
        valueChangeListener = onPropertyValueChangeListener;
    }

    public void setOrientationItem(int orientationItem) {
        if (orientationItem == 0) {
            propertyItem.setVisibility(GONE);
            propertyMenuItem.setVisibility(VISIBLE);
        } else {
            propertyItem.setVisibility(VISIBLE);
            propertyMenuItem.setVisibility(GONE);
        }
    }

    private void initialize(Context context, boolean z) {
        this.context = context;
        wB.a(context, this, R.layout.property_input_item);
        tvName = findViewById(R.id.tv_name);
        tvValue = findViewById(R.id.tv_value);
        imgLeftIcon = findViewById(R.id.img_left_icon);
        propertyItem = findViewById(R.id.property_item);
        propertyMenuItem = findViewById(R.id.property_menu_item);
        if (z) {
            setSoundEffectsEnabled(true);
            setOnClickListener(this);
        }
    }

    private void showViewIdDialog() {
        aB dialog = new aB((Activity) getContext());
        dialog.b(tvName.getText().toString());
        dialog.a(icon);

        PropertyPopupInputTextBinding binding = PropertyPopupInputTextBinding.inflate(LayoutInflater.from(getContext()));

        binding.tiInput.setHint(String.format(Helper.getResString(R.string.property_enter_value), "widget ID"));

        binding.edInput.setSingleLine();
        _B validator = new _B(context, binding.tiInput, uq.b, uq.a(), jC.a(sc_id).a(projectFileBean), value);
        validator.a(value);
        dialog.a(binding.getRoot());
        dialog.b(Helper.getResString(R.string.common_word_save), v -> {
            if (validator.b()) {
                setValue(binding.edInput.getText().toString());
                if (valueChangeListener != null) valueChangeListener.a(key, value);
                dialog.dismiss();
            }
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    public void a(String projectId, ProjectFileBean projectFileBean) {
        sc_id = projectId;
        this.projectFileBean = projectFileBean;
    }

    private void showNumberInputDialog() {
        aB dialog = new aB((Activity) getContext());
        dialog.b(tvName.getText().toString());
        dialog.a(icon);

        PropertyPopupInputTextBinding binding = PropertyPopupInputTextBinding.inflate(LayoutInflater.from(getContext()));
        binding.tiInput.setHint(String.format(Helper.getResString(R.string.property_enter_value), tvName.getText().toString()));

        binding.edInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        binding.edInput.setText(value);

        TB validator = new TB(context, binding.tiInput, 0,
                (key.equals("property_max") || key.equals("property_progress")) ? 0x7fffffff : 999);

        dialog.a(binding.getRoot());
        dialog.b(Helper.getResString(R.string.common_word_save), v -> {
            if (validator.b()) {
                setValue(binding.edInput.getText().toString());
                if (valueChangeListener != null) valueChangeListener.a(key, value);
                dialog.dismiss();
            }
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    private void showTextInputDialog(int maxValue, boolean isInject) {
        aB dialog = new aB((Activity) getContext());
        dialog.b(tvName.getText().toString());
        dialog.a(icon);

        PropertyPopupInputTextBinding binding = PropertyPopupInputTextBinding.inflate(LayoutInflater.from(getContext()));

        binding.tiInput.setHint(String.format(Helper.getResString(R.string.property_enter_value), tvName.getText().toString()));

        SB lengthValidator;

        if (isInject) {
            lengthValidator = new SB(context, binding.tiInput, 0, maxValue);
            binding.tiAutoCompleteInput.setVisibility(View.GONE);
            SyntaxScheme.setXMLHighlighter(binding.edInput);
        } else {
            loadStringsListMap();
            setupTextWatcher(binding.tiAutoCompleteInput, binding.edTiAutoCompleteInput);

            lengthValidator = new SB(context, binding.tiAutoCompleteInput, 0, maxValue);
            binding.tiAutoCompleteInput.setVisibility(View.VISIBLE);
            binding.tiInput.setVisibility(View.GONE);

            dialog.a(binding.getRoot());
            dialog.setDismissOnDefaultButtonClick(false);
            dialog.configureDefaultButton(context.getString(R.string.strings_xml), v -> {
                binding.edTiAutoCompleteInput.setText(stringsStart);
                binding.edTiAutoCompleteInput.setSelection(stringsStart.length());
                binding.edTiAutoCompleteInput.requestFocus();
            });

            setupAutoCompleteTextView(binding.edTiAutoCompleteInput);
        }

        lengthValidator.a(value);
        dialog.a(binding.getRoot());
        dialog.b(Helper.getResString(R.string.common_word_save), v -> handleSave(lengthValidator, binding.edInput, binding.edTiAutoCompleteInput, binding.tiAutoCompleteInput, isInject, dialog));
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    private void setupAutoCompleteTextView(MaterialAutoCompleteTextView autoCompleteTextView) {
        keysList = new ArrayList<>();
        List<String> mergedList = new ArrayList<>();

        for (HashMap<String, Object> map : stringsListMap) {
            String keyValue = map.get("key").toString();
            keysList.add(stringsStart + keyValue);
            mergedList.add(stringsStart + keyValue + " ( " + map.get("text") + " )");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, mergedList);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String value = autoCompleteTextView.getText().toString();
            autoCompleteTextView.setText(value.substring(0, value.indexOf(" (")));
            autoCompleteTextView.setSelection(autoCompleteTextView.getText().length());
        });
    }

    private void handleSave(SB lengthValidator, EditText input,
                            MaterialAutoCompleteTextView autoCompleteTextView, TextInputLayout textAutoCompleteInput,
                            boolean isInject, aB dialog) {
        if (lengthValidator.b() && textAutoCompleteInput.getError() == null) {
            if (isInject) {
                setValue(input.getText().toString());
            } else {
                setValue(autoCompleteTextView.getText().toString());
            }
            if (valueChangeListener != null) {
                String inputText = autoCompleteTextView.getText().toString();

                if (inputText.equals(stringsStart)) {

                    String errorMessage = MessageFormat.format(
                            "Please select a String\n" +
                                    "or remove \"{0}\" and add the value directly",
                            stringsStart
                    );
                    textAutoCompleteInput.setError(errorMessage);
                } else {
                    valueChangeListener.a(key, value);
                    dialog.dismiss();
                }
            }
        }
    }

    private void loadStringsListMap() {
        String filePath = FileUtil.getExternalStorageDir().concat("/.sketchware/data/").concat(sc_id.concat("/files/resource/values/strings.xml"));
        StringsEditorManager stringsEditorManager = new StringsEditorManager();
        stringsEditorManager.convertXmlStringsToListMap(FileUtil.readFileIfExist(filePath), stringsListMap);

        if (!stringsEditorManager.isXmlStringsExist(stringsListMap, "app_name") && filePath != null) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("key", "app_name");
            map.put("text", yB.c(lC.b(sc_id), "my_app_name"));
            stringsListMap.add(0, map);
        }
    }

    public void setupTextWatcher(TextInputLayout textAutoCompleteInput, MaterialAutoCompleteTextView editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString().trim();
                if (!text.startsWith(stringsStart) || text.equals(stringsStart)) {
                    textAutoCompleteInput.setError(null);
                    return;
                }

                boolean isExactMatch = keysList.contains(text);
                textAutoCompleteInput.setError(isExactMatch ? null : "Not found in strings.xml");
            }
        });
    }

    private void showNumberDecimalInputDialog(int minValue, int maxValue) {
        aB dialog = new aB((Activity) getContext());
        dialog.b(tvName.getText().toString());
        dialog.a(icon);

        PropertyPopupInputTextBinding binding = PropertyPopupInputTextBinding.inflate(LayoutInflater.from(getContext()));
        binding.tiInput.setHint(String.format(Helper.getResString(R.string.property_enter_value), tvName.getText().toString()));

        binding.edInput.setInputType((minValue < 0)
                ? InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_NUMBER_FLAG_DECIMAL
                : InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        binding.edInput.setText(value);

        OB validator = new OB(context, binding.tiInput, minValue, maxValue);

        dialog.a(binding.getRoot());
        dialog.b(Helper.getResString(R.string.common_word_save), v -> {
            if (validator.b()) {
                setValue(binding.edInput.getText().toString());
                if (valueChangeListener != null) valueChangeListener.a(key, value);
                dialog.dismiss();
            }
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    private void showAutoCompleteDialog() {
        aB dialog = new aB((Activity) getContext());
        dialog.b(tvName.getText().toString());
        dialog.a(icon);

        PropertyPopupInputTextBinding binding = PropertyPopupInputTextBinding.inflate(LayoutInflater.from(getContext()));
        MaterialAutoCompleteTextView input = binding.edTiAutoCompleteInput;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, getPrioritizedSuggestions(typeView));
        input.setText(value);
        input.setAdapter(adapter);
        binding.tiInput.setVisibility(View.GONE);
        binding.tiAutoCompleteInput.setVisibility(View.VISIBLE);
        SB lengthValidator = new SB(context, binding.tiInput, 0, 99);
        lengthValidator.a(value);
        dialog.a(binding.getRoot());
        dialog.b(Helper.getResString(R.string.common_word_save), v -> {
            if (lengthValidator.b()) {
                setValue(input.getText().toString());
                if (valueChangeListener != null) valueChangeListener.a(key, value);
                dialog.dismiss();
            }
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    public List<String> getPrioritizedSuggestions(String typeView) {
        List<String> prioritizedSuggestions = new ArrayList<>();

        switch (typeView) {
            case "0":  // LinearLayout
                prioritizedSuggestions.addAll(Arrays.asList(
                        "LinearLayout", "FrameLayout", "RelativeLayout",
                        "androidx.constraintlayout.widget.ConstraintLayout",
                        "TableLayout", "GridLayout",
                        "androidx.coordinatorlayout.widget.CoordinatorLayout",
                        "Space"
                ));
                break;

            case "2":  // HorizontalScrollView
            case "12": // VerticalScrollView
                prioritizedSuggestions.addAll(Arrays.asList(
                        "HorizontalScrollView", "VerticalScrollView", "ScrollView",
                        "androidx.core.widget.NestedScrollView"
                ));
                break;

            case "9":  // ListView
            case "25": // GridView
            case "48": // RecyclerView
                prioritizedSuggestions.addAll(Arrays.asList(
                        "ListView", "GridView", "androidx.recyclerview.widget.RecyclerView",
                        "ExpandableListView", "androidx.viewpager2.widget.ViewPager2"
                ));
                break;

            case "3":  // Button
            case "41": // MaterialButton
                prioritizedSuggestions.addAll(Arrays.asList(
                        "Button", "com.google.android.material.button.MaterialButton",
                        "ImageButton", "ToggleButton", "CompoundButton",
                        "androidx.appcompat.widget.AppCompatButton"
                ));
                break;

            case "4":  // TextView
            case "5":  // EditText
            case "38": // TextInputLayout
                prioritizedSuggestions.addAll(Arrays.asList(
                        "TextView", "EditText",
                        "com.google.android.material.textfield.TextInputLayout",
                        "com.google.android.material.textfield.TextInputEditText",
                        "AutoCompleteTextView",
                        "androidx.appcompat.widget.AppCompatTextView",
                        "androidx.appcompat.widget.AppCompatEditText"
                ));
                break;

            case "21": // VideoView
            case "7":  // WebView
                prioritizedSuggestions.addAll(Arrays.asList(
                        "VideoView", "android.webkit.WebView",
                        "androidx.media.widget.VideoView",
                        "android.webkit.WebViewClient", "android.webkit.WebChromeClient"
                ));
                break;

            case "8":  // ProgressBar
            case "14": // SeekBar
                prioritizedSuggestions.addAll(Arrays.asList(
                        "ProgressBar", "SeekBar",
                        "HorizontalScrollBar", "VerticalSeekBar",
                        "IndeterminateProgressBar"
                ));
                break;

            case "11": // CheckBox
            case "19": // RadioButton
                prioritizedSuggestions.addAll(Arrays.asList(
                        "CheckBox", "RadioButton",
                        "androidx.appcompat.widget.AppCompatCheckBox",
                        "androidx.appcompat.widget.AppCompatRadioButton"
                ));
                break;

            case "30": // TabLayout
            case "31": // ViewPager
                prioritizedSuggestions.addAll(Arrays.asList(
                        "com.google.android.material.tabs.TabLayout",
                        "androidx.viewpager.widget.ViewPager",
                        "androidx.viewpager2.widget.ViewPager2",
                        "androidx.fragment.app.FragmentPagerAdapter",
                        "androidx.fragment.app.FragmentStatePagerAdapter",
                        "com.google.android.material.tabs.TabLayoutMediator"
                ));
                break;

            case "32": // BottomNavigationView
                prioritizedSuggestions.add("com.google.android.material.bottomnavigation.BottomNavigationView");
                break;

            case "36": // CardView
                prioritizedSuggestions.addAll(Arrays.asList(
                        "androidx.cardview.widget.CardView",
                        "com.google.android.material.card.MaterialCardView"
                ));
                break;

            case "39": // SwipeRefreshLayout
                prioritizedSuggestions.addAll(Arrays.asList(
                        "androidx.swiperefreshlayout.widget.SwipeRefreshLayout",
                        "com.google.android.material.progressindicator.CircularProgressIndicator",
                        "com.google.android.material.progressindicator.LinearProgressIndicator"
                ));
                break;
        }

        List<String> allSuggestions = new ArrayList<>(getFullSuggestions());

        allSuggestions.removeAll(prioritizedSuggestions);

        List<String> finalSuggestions = new ArrayList<>(prioritizedSuggestions);
        finalSuggestions.addAll(allSuggestions);

        return finalSuggestions;
    }

    public List<String> getFullSuggestions() {
        return Arrays.asList(getResources().getStringArray(R.array.property_convert_options));
    }

    /**
     * Populates additional attributes for specific view types.
     * 
     * You can add more attributes directly to this list instead of introducing 
     * another variable in the ViewBean class. Use the ViewBean#type field 
     * or getClassInfo methods to identify the type of view and add attributes accordingly.
     * 
     * Examples:
     * 
     * // Using ViewBean#type
     * if (bean.type == ViewBean.VIEW_TYPE_WIDGET_TEXTVIEW) {
     *     attrs.add("android:text");
     * }
     * 
     * // Using getClassInfo
     * if (bean.getClassInfo().a("TextView")) {
     *     attrs.add("android:text");
     * }
     * if (bean.getClassInfo().b("LinearLayout")) {
     *     attrs.add("android:orientation");
     * }
     * 
     * Notes for getClassInfo:
     * - a(String): Similar to instanceof for view class names.
     * - b(String): Represents the actual type of the view, I think?. 
     *   Idk if there's a difference between ViewBean#type and this.
     * 
     * @return A list of additional attributes for the specified view type.
     */
    private List<String> populateAttributes() {
        List<String> attrs = new ArrayList<>();
        attrs.add("android:elevation");
        if (bean != null) {
            var simpleName = getSimpleName(bean);
            var classInfo = bean.getClassInfo();
            if (classInfo.b("CardView")) {
                attrs.add("app:cardBackgroundColor");
                attrs.add("app:cardElevation");
                attrs.add("app:cardCornerRadius");
                attrs.add("app:cardUseCompatPadding");
                if (simpleName.equals("MaterialCardView")) {
                    attrs.add("app:strokeColor");
                    attrs.add("app:strokeWidth");
                }
            }
        }
        // Add more attributes here based on the view type
        return attrs;
    }

    private String getSimpleName(ViewBean bean) {
        return Jx.WIDGET_NAME_PATTERN.matcher(bean.convert).replaceAll("");
    }

    private void showInjectDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        var binding = PropertyPopupParentAttrBinding.inflate(LayoutInflater.from(getContext()));
        dialog.setContentView(binding.getRoot());
        dialog.show();

        binding.title.setText(tvName.getText().toString());

        var adapter = new AttributesAdapter();
        adapter.setOnItemClickListener(
                new AttributesAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(LinkedHashMap<String, String> attributes, String attr) {
                        setAttributeValue(attr, attributes);
                        dialog.dismiss();
                    }

                    @Override
                    public void onItemLongClick(LinkedHashMap<String, String> attributes, String attr) {
                        dialog.dismiss();
                        var builder =
                                new MaterialAlertDialogBuilder(getContext())
                                        .setTitle("Delete")
                                        .setMessage("Are you sure you want to delete " + attr + "?")
                                        .setPositiveButton(
                                                R.string.common_word_yes,
                                                (d, w) -> {
                                                    attributes.remove(attr);
                                                    saveAttributes(attributes);
                                                })
                                        .setNegativeButton(R.string.common_word_no, null);
                        var deleteDialog = builder.create();
                        deleteDialog.setOnDismissListener(d -> showInjectDialog());
                        deleteDialog.show();
                    }
                });
        binding.recyclerView.setAdapter(adapter);
        var dividerItemDecoration =
                new DividerItemDecoration(
                        binding.recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        binding.recyclerView.addItemDecoration(dividerItemDecoration);
        var attributes = readAttributes();
        adapter.setAttributes(attributes);
        List<String> keys = new ArrayList<>(attributes.keySet());
        adapter.submitList(keys);

        binding.add.setOnClickListener(
                v -> {
                    addNewAttribute(attributes);
                    dialog.dismiss();
                });
        binding.sourceCode.setVisibility(View.VISIBLE);
        binding.sourceCode.setOnClickListener(
                v -> {
                    showTextInputDialog(9999, true);
                    dialog.dismiss();
                });
    }

    private void addNewAttribute(Map<String, String> attributes) {
        var builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle("Add new attribute");

        PropertyPopupInputTextBinding binding =
                PropertyPopupInputTextBinding.inflate(LayoutInflater.from(getContext()));

        var input = binding.edTiAutoCompleteInput;
        binding.tiInput.setVisibility(View.GONE);
        binding.tiAutoCompleteInput.setVisibility(View.VISIBLE);
        binding.tiAutoCompleteInput.setHint("Enter new attribute");
        input.setAdapter(
                new ArrayAdapter<>(
                        getContext(),
                        android.R.layout.simple_list_item_1,
                        populateAttributes().stream()
                                .filter(attr -> !attributes.containsKey(attr))
                                .collect(Collectors.toList())));

        builder.setView(binding.getRoot());
        builder.setPositiveButton(
                R.string.common_word_next,
                (d, w) -> {
                    var inputValue = input.getText().toString().trim();
                    if (!inputValue.isEmpty()) {
                        setAttributeValue(inputValue, attributes);
                    } else {
                        d.cancel();
                    }
                });
        builder.setNegativeButton(R.string.common_word_cancel, (d, w) -> d.cancel());
        var dialog = builder.create();
        dialog.setOnCancelListener(d -> showInjectDialog());
        dialog.setOnShowListener(
                d -> {
                    var positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setEnabled(!input.getText().toString().trim().isEmpty());

                    input.addTextChangedListener(
                            new BaseTextWatcher() {
                                @Override
                                public void onTextChanged(
                                        CharSequence charSequence,
                                        int start,
                                        int before,
                                        int after) {
                                    positiveButton.setEnabled(
                                            !input.getText().toString().trim().isEmpty());
                                }
                            });
                });
        dialog.show();
    }

    private void setAttributeValue(String attr, Map<String, String> attributes) {
        var builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle(attr);

        PropertyPopupInputTextBinding binding =
                PropertyPopupInputTextBinding.inflate(LayoutInflater.from(getContext()));

        var input = binding.edInput;
        if (attributes.containsKey(attr)) {
            input.setText(attributes.get(attr));
        }

        binding.tiInput.setHint(
                String.format(Helper.getResString(R.string.property_enter_value), attr));

        builder.setView(binding.getRoot());
        builder.setPositiveButton(
                R.string.common_word_save,
                (d, w) -> {
                    var inputValue = input.getText().toString().trim();
                    if (!inputValue.isEmpty()) {
                        attributes.put(attr, inputValue);
                        saveAttributes(attributes);
                    } else {
                        d.cancel();
                    }
                });
        builder.setNegativeButton(R.string.common_word_cancel, null);
        var dialog = builder.create();
        dialog.setOnShowListener(
                d -> {
                    var positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    positiveButton.setEnabled(!input.getText().toString().trim().isEmpty());

                    input.addTextChangedListener(
                            new BaseTextWatcher() {
                                @Override
                                public void onTextChanged(
                                        CharSequence charSequence,
                                        int start,
                                        int before,
                                        int after) {
                                    positiveButton.setEnabled(
                                            !input.getText().toString().trim().isEmpty());
                                }
                            });
                });
        dialog.show();
        dialog.setOnDismissListener(d -> showInjectDialog());
    }

    private void saveAttributes(Map<String, String> attributes) {
        String result =
                attributes.entrySet().stream()
                        .map(entry -> entry.getKey() + "=\"" + entry.getValue() + "\"")
                        .collect(Collectors.joining("\n"));
        setValue(result);
        if (valueChangeListener != null) valueChangeListener.a(key, value);
    }

    private LinkedHashMap<String, String> readAttributes() {
        LinkedHashMap<String, String> attributes = new LinkedHashMap<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader("<tag " + value + "></tag>"));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        attributes.put(parser.getAttributeName(i), parser.getAttributeValue(i));
                    }
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException | RuntimeException ignored) {
        }

        return attributes;
    }

    public static class AttributesAdapter extends ListAdapter<String, AttributesAdapter.ViewHolder> {

        private static final DiffUtil.ItemCallback<String> DIFF_CALLBACK =
                new DiffUtil.ItemCallback<>() {
                    @Override
                    public boolean areItemsTheSame(
                            @NonNull String oldItem, @NonNull String newItem) {
                        return oldItem.equals(newItem);
                    }

                    @Override
                    public boolean areContentsTheSame(
                            @NonNull String oldItem, @NonNull String newItem) {
                        return true;
                    }
                };

        public AttributesAdapter() {
            super(DIFF_CALLBACK);
        }

        private LinkedHashMap<String, String> attributes;
        private ItemClickListener listener;

        public void setAttributes(LinkedHashMap<String, String> attributes) {
            this.attributes = attributes;
        }

        public void setOnItemClickListener(ItemClickListener listener) {
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FrameLayout root = new FrameLayout(parent.getContext());
            return new ViewHolder(root);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(getItem(position));
        }

        private class ViewHolder extends RecyclerView.ViewHolder {
            private final PropertyInputItemBinding binding;

            public ViewHolder(FrameLayout view) {
                super(view);
                binding =
                        PropertyInputItemBinding.inflate(
                                LayoutInflater.from(view.getContext()), view, true);
            }

            void bind(String attr) {
                binding.tvName.setText(attr);
                binding.tvValue.setText(attributes.get(attr));
                binding.imgLeftIcon.setImageResource(R.drawable.ic_mtrl_code);
                binding.getRoot().findViewById(R.id.property_menu_item).setVisibility(View.GONE);
                itemView.setOnClickListener(
                        view -> {
                            if (listener != null) listener.onItemClick(attributes, attr);
                        });
                itemView.setOnLongClickListener(
                        v -> {
                            if (listener != null) listener.onItemLongClick(attributes, attr);
                            return true;
                        });
            }
        }

        public interface ItemClickListener {

            void onItemClick(LinkedHashMap<String, String> attributes, String item);

            void onItemLongClick(LinkedHashMap<String, String> attributes, String item);
        }
    }
}

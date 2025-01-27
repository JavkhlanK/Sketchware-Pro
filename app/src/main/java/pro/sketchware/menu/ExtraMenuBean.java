package pro.sketchware.menu;

import static android.text.TextUtils.isEmpty;

import static pro.sketchware.utility.SketchwareUtil.getDip;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import a.a.a.Ss;
import a.a.a.aB;
import a.a.a.eC;
import a.a.a.jC;
import a.a.a.uq;
import a.a.a.wB;

import com.besome.sketch.beans.AdTestDeviceBean;
import com.besome.sketch.beans.AdUnitBean;
import com.besome.sketch.beans.ComponentBean;
import com.besome.sketch.beans.ProjectFileBean;
import com.besome.sketch.editor.LogicEditorActivity;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.material.textfield.TextInputLayout;

import mod.hey.studios.util.Helper;
import mod.hilal.saif.activities.tools.ConfigActivity;
import mod.hilal.saif.asd.AsdDialog;

import pro.sketchware.R;
import pro.sketchware.activities.resources.editors.utils.StringsEditorManager;
import pro.sketchware.lib.base.BaseTextWatcher;
import pro.sketchware.lib.highlighter.SimpleHighlighter;
import pro.sketchware.utility.CustomVariableUtil;
import pro.sketchware.utility.FilePathUtil;
import pro.sketchware.utility.FileResConfig;
import pro.sketchware.utility.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtraMenuBean {

    public static final int VARIABLE_TYPE_BOOLEAN = 0;
    public static final int VARIABLE_TYPE_NUMBER = 1;
    public static final int VARIABLE_TYPE_MAP = 3;
    public static final int VARIABLE_TYPE_STRING = 2;

    public static final int LIST_TYPE_NUMBER = 1;
    public static final int LIST_TYPE_MAP = 3;
    public static final int LIST_TYPE_STRING = 2;

    public static final String[] adSize = {"AUTO_HEIGHT", "BANNER", "FLUID", "FULL_BANNER", "FULL_WIDTH", "INVALID", "LARGE_BANNER", "LEADERBOARD", "MEDIUM_RECTANGLE", "SEARCH", "SMART_BANNER", "WIDE_SKYSCRAPER"};
    public static final String[] intentKey = {"EXTRA_ALLOW_MULTIPLE", "EXTRA_EMAIL", "EXTRA_INDEX", "EXTRA_INTENT", "EXTRA_PHONE_NUMBER", "EXTRA_STREAM", "EXTRA_SUBJECT", "EXTRA_TEXT", "EXTRA_TITLE"};
    public static final String[] pixelFormat = {"OPAQUE", "RGBA_1010102", "RGBA_8888", "RGBA_F16", "RGBX_8888", "RGB_565", "RGB_888", "TRANSLUCENT", "TRANSPARENT", "UNKNOWN"};
    public static final String[] patternFlags = {"CANON_EQ", "CASE_INSENSITIVE", "COMMENTS", "DOTALL", "LITERAL", "MULTILINE", "UNICODE_CASE", "UNIX_LINES"};
    public static final String[] permission = {"CAMERA", "READ_EXTERNAL_STORAGE", "WRITE_EXTERNAL_STORAGE", "ACCESS_FINE_LOCATION", "ACCESS_COARSE_LOCATION", "RECORD_AUDIO", "READ_CONTACTS", "WRITE_CONTACTS", "READ_SMS", "SEND_SMS", "READ_PHONE_STATE", "CALL_PHONE", "READ_CALENDAR", "WRITE_CALENDAR", "BLUETOOTH", "BLUETOOTH_ADMIN"};

    private final String ASSETS_PATH = FileUtil.getExternalStorageDir() + "/.sketchware/data/%s/files/assets/";
    private final String NATIVE_PATH = FileUtil.getExternalStorageDir() + "/.sketchware/data/%s/files/native_libs/";
    private final DefaultExtraMenuBean defaultExtraMenu;
    private final FilePathUtil fpu;
    private final FilePickerDialog fpd;
    private final FileResConfig frc;
    private final LogicEditorActivity logicEditor;
    private final DialogProperties mProperty = new DialogProperties();
    private final eC projectDataManager;
    private final String sc_id;
    private final String javaName;
    private String splitter;

    public ExtraMenuBean(LogicEditorActivity logicA) {
        fpd = new FilePickerDialog(logicA);
        logicEditor = logicA;
        sc_id = logicA.B;
        fpu = new FilePathUtil();
        frc = new FileResConfig(logicA.B);
        defaultExtraMenu = new DefaultExtraMenuBean(logicA);
        projectDataManager = jC.a(logicA.B);
        javaName = logicA.M.getJavaName();
    }

    public static void setupSearchView(View view, ViewGroup viewGroup) {
        if (viewGroup.getChildCount() == 0) {
            return;
        }
        EditText searchInput = view.findViewById(R.id.searchInput);
        TextInputLayout textInputLayout = view.findViewById(R.id.searchInputLayout);
        textInputLayout.setVisibility(View.VISIBLE);
        searchInput.addTextChangedListener(new BaseTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String filterText = s.toString().toLowerCase();
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    View childView = viewGroup.getChildAt(i);
                    if (childView instanceof TextView textView) {
                        String itemText = textView.getText().toString().toLowerCase();
                        if (itemText.contains(filterText)) {
                            textView.setVisibility(View.VISIBLE);
                        } else {
                            textView.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }

    private void codeMenu(Ss menu) {
        AsdDialog asdDialog = new AsdDialog(logicEditor);
        asdDialog.setCon(menu.getArgValue().toString());
        asdDialog.show();
        /* p2 as true is for number */
        asdDialog.saveLis(logicEditor, false, menu, asdDialog);
        asdDialog.cancelLis(asdDialog);
    }

    public void defineMenuSelector(Ss ss) {
        String menuType = ss.b;
        String menuName = ss.getMenuName();

        switch (menuType) {
            case "d":
                logicEditor.a(ss, true);
                break;

            case "s":
                switch (menuName) {
                    case "intentData":
                        logicEditor.e(ss);
                        return;

                    case "url":
                        logicEditor.c(ss);
                        return;

                    case "inputCode":
                        codeMenu(ss);
                        return;

                    case "import":
                        asdDialog(ss, "Enter the path without import & semicolon");
                        return;

                    default:
                        asdDialog(ss, null);
                }
                break;

            case "m":
                switch (menuName) {
                    case "resource":
                        logicEditor.pickImage(ss, "property_image");
                        return;

                    case "resource_bg":
                        logicEditor.pickImage(ss, "property_background_resource");
                        return;

                    case "sound":
                        logicEditor.h(ss);
                        return;

                    case "font":
                        logicEditor.d(ss);
                        return;

                    case "typeface":
                        logicEditor.i(ss);
                        return;

                    case "color":
                        logicEditor.b(ss);
                        return;

                    case "view":
                    case "textview":
                    case "edittext":
                    case "imageview":
                    case "listview":
                    case "spinner":
                    case "listSpn":
                    case "webview":
                    case "checkbox":
                    case "switch":
                    case "seekbar":
                    case "calendarview":
                    case "compoundButton":
                    case "materialButton":
                    case "adview":
                    case "progressbar":
                    case "mapview":
                    case "radiobutton":
                    case "ratingbar":
                    case "searchview":
                    case "videoview":
                    case "gridview":
                    case "actv":
                    case "mactv":
                    case "tablayout":
                    case "viewpager":
                    case "bottomnavigation":
                    case "badgeview":
                    case "patternview":
                    case "sidebar":
                    case "recyclerview":
                    case "cardview":
                    case "collapsingtoolbar":
                    case "textinputlayout":
                    case "swiperefreshlayout":
                    case "radiogroup":
                    case "lottie":
                    case "otpview":
                    case "signinbutton":
                    case "youtubeview":
                    case "codeview":
                    case "datepicker":
                    case "timepicker":
                        logicEditor.f(ss);
                        return;

                    case "Assets":
                    case "NativeLib":
                        pathSelectorMenu(ss);
                        return;

                    default:
                        defaultMenus(ss);
                }
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void defaultMenus(Ss menu) {
        String menuName = menu.getMenuName();
        aB dialog = new aB(logicEditor);
        View rootView = wB.a(logicEditor, R.layout.property_popup_selector_single);
        ViewGroup viewGroup = rootView.findViewById(R.id.rg_content);
        ArrayList<String> menus = new ArrayList<>();
        String title;
        switch (menuName) {
            case "varInt":
                title = Helper.getResString(R.string.logic_editor_title_select_variable_number);
                menus = getVarMenus(VARIABLE_TYPE_NUMBER);
                break;

            case "varBool":
                title = Helper.getResString(R.string.logic_editor_title_select_variable_boolean);
                menus = getVarMenus(VARIABLE_TYPE_BOOLEAN);
                break;

            case "varStr":
                title = Helper.getResString(R.string.logic_editor_title_select_variable_string);
                menus = getVarMenus(VARIABLE_TYPE_STRING);
                break;

            case "varMap":
                title = Helper.getResString(R.string.logic_editor_title_select_variable_map);
                menus = getVarMenus(VARIABLE_TYPE_MAP);
                break;

            case "listInt":
                title = Helper.getResString(R.string.logic_editor_title_select_list_number);
                menus = getListMenus(LIST_TYPE_NUMBER);
                break;

            case "listStr":
                title = Helper.getResString(R.string.logic_editor_title_select_list_string);
                menus = getListMenus(LIST_TYPE_STRING);
                break;

            case "listMap":
                title = Helper.getResString(R.string.logic_editor_title_select_list_map);
                menus = getListMenus(LIST_TYPE_MAP);
                break;

            case "list":
                title = Helper.getResString(R.string.logic_editor_title_select_list);
                for (String variable : projectDataManager.c(javaName)) {
                    String variableName = CustomVariableUtil.getVariableName(variable);
                    menus.add(variableName != null ? variableName : variable);
                }
                break;

            case "intent":
                title = Helper.getResString(R.string.logic_editor_title_select_component_intent);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_INTENT);
                break;

            case "file":
                title = Helper.getResString(R.string.logic_editor_title_select_component_file);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_SHAREDPREF);
                break;

            case "intentAction":
                title = Helper.getResString(R.string.logic_editor_title_select_component_intent_action);
                menus = new ArrayList<>(Arrays.asList(uq.b()));
                break;

            case "intentFlags":
                title = Helper.getResString(R.string.logic_editor_title_select_component_intent_flags);
                menus = new ArrayList<>(Arrays.asList(uq.c()));
                break;

            case "calendar":
                title = Helper.getResString(R.string.logic_editor_title_select_component_calendar);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_CALENDAR);
                break;

            case "calendarField":
                title = Helper.getResString(R.string.logic_editor_title_select_component_calendar_field);
                menus = new ArrayList<>(Arrays.asList(uq.e));
                break;

            case "vibrator":
                title = Helper.getResString(R.string.logic_editor_title_select_component_vibrator);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_VIBRATOR);
                break;

            case "timer":
                title = Helper.getResString(R.string.logic_editor_title_select_component_timer);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_TIMERTASK);
                break;

            case "firebase":
                title = Helper.getResString(R.string.logic_editor_title_select_component_firebase);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_FIREBASE);
                break;

            case "firebaseauth":
                title = Helper.getResString(R.string.logic_editor_component_firebaseauth_title_select_firebase_auth);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_FIREBASE_AUTH);
                break;

            case "firebasestorage":
                title = Helper.getResString(R.string.logic_editor_title_select_component_firebasestorage);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_FIREBASE_STORAGE);
                break;

            case "dialog":
                title = Helper.getResString(R.string.logic_editor_title_select_component_dialog);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_DIALOG);
                break;

            case "mediaplayer":
                title = Helper.getResString(R.string.logic_editor_title_select_component_mediaplayer);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_MEDIAPLAYER);
                break;

            case "soundpool":
                title = Helper.getResString(R.string.logic_editor_title_select_component_soundpool);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_SOUNDPOOL);
                break;

            case "objectanimator":
                title = Helper.getResString(R.string.logic_editor_title_select_component_objectanimator);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_OBJECTANIMATOR);
                break;

            case "aniRepeatMode":
                title = Helper.getResString(R.string.logic_editor_title_select_animator_repeat_mode);
                menus = new ArrayList<>(Arrays.asList(uq.j));
                break;

            case "aniInterpolator":
                title = Helper.getResString(R.string.logic_editor_title_select_animator_interpolator);
                menus = new ArrayList<>(Arrays.asList(uq.k));
                break;

            case "visible":
                title = Helper.getResString(R.string.logic_editor_title_select_visibility);
                menus = new ArrayList<>(Arrays.asList(uq.g));
                break;

            case "cacheMode":
                title = Helper.getResString(R.string.logic_editor_title_select_cache_mode);
                menus = new ArrayList<>(Arrays.asList(uq.h));
                break;

            case "animatorproperty":
                title = Helper.getResString(R.string.logic_editor_title_select_animator_target_property);
                menus = new ArrayList<>(Arrays.asList(uq.i));
                break;

            case "gyroscope":
                title = Helper.getResString(R.string.logic_editor_title_select_component_gyroscope);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_GYROSCOPE);
                break;

            case "interstitialad":
                title = Helper.getResString(R.string.logic_editor_title_select_component_interstitialad);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_INTERSTITIAL_AD);
                break;

            case "camera":
                title = Helper.getResString(R.string.logic_editor_title_select_component_camera);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_CAMERA);
                break;

            case "filepicker":
                title = Helper.getResString(R.string.logic_editor_title_select_component_filepicker);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_FILE_PICKER);
                break;

            case "directoryType":
                title = Helper.getResString(R.string.logic_editor_title_select_directory_type);
                menus = new ArrayList<>(Arrays.asList(uq.l));
                break;

            case "requestnetwork":
                title = Helper.getResString(R.string.logic_editor_title_select_component_request_network);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_REQUEST_NETWORK);
                break;

            case "method":
                title = Helper.getResString(R.string.logic_editor_title_request_network_method);
                menus = new ArrayList<>(Arrays.asList(uq.n));
                break;

            case "requestType":
                title = Helper.getResString(R.string.logic_editor_title_request_network_request_type);
                menus = new ArrayList<>(Arrays.asList(uq.o));
                break;

            case "texttospeech":
                title = Helper.getResString(R.string.logic_editor_title_select_component_text_to_speech);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_TEXT_TO_SPEECH);
                break;

            case "speechtotext":
                title = Helper.getResString(R.string.logic_editor_title_select_component_speech_to_text);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_SPEECH_TO_TEXT);
                break;

            case "bluetoothconnect":
                title = Helper.getResString(R.string.logic_editor_title_select_component_bluetooth_connect);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_BLUETOOTH_CONNECT);
                break;

            case "locationmanager":
                title = Helper.getResString(R.string.logic_editor_title_select_component_location_manager);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_LOCATION_MANAGER);
                break;

            case "videoad":
                title = Helper.getResString(R.string.logic_editor_title_select_component);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_REWARDED_VIDEO_AD);
                break;

            case "progressdialog":
                title = Helper.getResString(R.string.logic_editor_title_select_component);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_PROGRESS_DIALOG);
                break;

            case "datepickerdialog":
                title = Helper.getResString(R.string.logic_editor_title_select_component);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_DATE_PICKER_DIALOG);
                break;

            case "asynctask":
                title = Helper.getResString(R.string.logic_editor_title_select_component);
                menus = getComponentMenus(36);
                break;

            case "timepickerdialog":
                title = Helper.getResString(R.string.logic_editor_title_select_component);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_TIME_PICKER_DIALOG);
                break;

            case "notification":
                title = Helper.getResString(R.string.logic_editor_title_select_component);
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_NOTIFICATION);
                break;

            case "fragmentAdapter":
                title = "Select a FragmentAdapter Component";
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_FRAGMENT_ADAPTER);
                break;

            case "phoneauth":
                title = "Select a FirebasePhone Component";
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_FIREBASE_AUTH_PHONE);
                break;

            case "dynamiclink":
                title = "Select a DynamicLink Component";
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_FIREBASE_DYNAMIC_LINKS);
                break;

            case "cloudmessage":
                title = "Select a CloudMessage Component";
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_FIREBASE_CLOUD_MESSAGE);
                break;

            case "googlelogin":
                title = "Select a FirebaseGoogle Component";
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_FIREBASE_AUTH_GOOGLE_LOGIN);
                break;

            case "onesignal":
                title = "Select a OneSignal Component";
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_ONESIGNAL);
                break;

            case "fbadbanner":
                title = "Select an FBAdsBanner Component";
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_FACEBOOK_ADS_BANNER);
                break;

            case "fbadinterstitial":
                title = "Select an FBAdsInterstitial Component";
                menus = getComponentMenus(ComponentBean.COMPONENT_TYPE_FACEBOOK_ADS_INTERSTITIAL);
                break;

            case "providerType":
                title = Helper.getResString(R.string.logic_editor_title_location_manager_provider_type);
                menus = new ArrayList<>(Arrays.asList(uq.p));
                break;

            case "mapType":
                title = Helper.getResString(R.string.logic_editor_title_mapview_map_type);
                menus = new ArrayList<>(Arrays.asList(uq.q));
                break;

            case "markerColor":
                title = Helper.getResString(R.string.logic_editor_title_mapview_marker_color);
                menus = new ArrayList<>(Arrays.asList(uq.r));
                break;

            case "service":
                title = "Select a Background Service";
                if (FileUtil.isExistFile(fpu.getManifestService(sc_id))) {
                    menus = frc.getServiceManifestList();
                }
                break;

            case "broadcast":
                title = "Select a Broadcast Receiver";
                if (FileUtil.isExistFile(fpu.getManifestBroadcast(sc_id))) {
                    menus = frc.getBroadcastManifestList();
                }
                break;

            case "activity":
                ArrayList<String> activityMenu = new ArrayList<>();
                title = Helper.getResString(R.string.logic_editor_title_select_activity);
                for (ProjectFileBean projectFileBean : jC.b(sc_id).b()) {
                    activityMenu.add(projectFileBean.getActivityName());
                }
                for (String activity : activityMenu) {
                    viewGroup.addView(logicEditor.e(activity));
                }
                activityMenu = new ArrayList<>();
                if (FileUtil.isExistFile(fpu.getManifestJava(sc_id))) {
                    for (String activity : frc.getJavaManifestList()) {
                        if (activity.contains(".")) {
                            activityMenu.add(activity.substring(1 + activity.lastIndexOf(".")));
                        }
                    }
                    if (!activityMenu.isEmpty()) {
                        TextView txt = new TextView(logicEditor);
                        txt.setText("Custom Activities");
                        txt.setPadding((int) getDip(2), (int) getDip(4), (int) getDip(4), (int) getDip(4));
                        txt.setTextSize(14f);
                        viewGroup.addView(txt);
                    }
                    for (String activity : activityMenu) {
                        viewGroup.addView(logicEditor.e(activity));
                    }
                }
                setupSearchView(rootView, viewGroup);
                break;

            case "customViews":
                title = "Select a Custom View";
                for (ProjectFileBean projectFileBean : jC.b(sc_id).c()) {
                    menus.add(projectFileBean.fileName);
                }
                break;

            case "SignButtonColor":
                title = "Select a SignInButton Color";
                menus.add("COLOR_AUTO");
                menus.add("COLOR_DARK");
                menus.add("COLOR_LIGHT");
                break;

            case "SignButtonSize":
                title = "Select SignInButton Size";
                menus.add("SIZE_ICON_ONLY");
                menus.add("SIZE_STANDARD");
                menus.add("SIZE_WIDE");
                break;

            case "ResString":
                title = "Select a ResString";

                String filePath = FileUtil.getExternalStorageDir().concat("/.sketchware/data/").concat(sc_id.concat("/files/resource/values/strings.xml"));
                ArrayList<HashMap<String, Object>> StringsListMap = new ArrayList<>();
                StringsEditorManager stringsEditorManager = new StringsEditorManager();
                stringsEditorManager.convertXmlStringsToListMap(FileUtil.readFileIfExist(filePath), StringsListMap);

                if (!stringsEditorManager.isXmlStringsExist(StringsListMap, "app_name")) {
                    menus.add("R.string.app_name");
                }
                for (HashMap<String, Object> map : StringsListMap) {
                    menus.add("R.string." + map.get("key"));
                }

                break;
            case "ResStyle":
            case "ResColor":
            case "ResArray":
            case "ResDimen":
            case "ResBool":
            case "ResInteger":
            case "ResAttr":
            case "ResXml":
                title = "Deprecated";
                dialog.a("This Block Menu was initially used to parse resource values, but was too I/O heavy and has been removed due to that. Please use the Code Editor instead.");
                break;

            case "AdUnit":
                dialog.a(R.drawable.unit_96);
                title = "Select an Ad Unit";
                for (AdUnitBean bean : jC.c(sc_id).e.adUnits) {
                    menus.add(bean.id);
                }
                break;

            case "TestDevice":
                dialog.a(R.drawable.ic_test_device_48dp);
                title = "Select a Test device";
                for (AdTestDeviceBean testDevice : jC.c(sc_id).e.testDevices) {
                    menus.add(testDevice.deviceId);
                }
                break;

            case "IntentKey":
                title = "Select an Intent key";
                menus.addAll(new ArrayList<>(Arrays.asList(intentKey)));
                break;

            case "PatternFlag":
                title = "Select a Pattern Flags";
                menus.addAll(new ArrayList<>(Arrays.asList(patternFlags)));
                break;

            case "Permission":
                title = "Select a Permission";
                menus.addAll(new ArrayList<>(Arrays.asList(permission)));
                break;

            case "AdSize":
                title = "Select an Ad size";
                menus.addAll(new ArrayList<>(Arrays.asList(adSize)));
                break;

            case "PixelFormat":
                title = "Select a PixelFormat";
                menus.addAll(new ArrayList<>(Arrays.asList(pixelFormat)));
                break;

            case "Variable":
                title = "Select a Variable";
                for (Pair<Integer, String> integerStringPair : projectDataManager.k(javaName)) {
                    String variable = integerStringPair.second;
                    String variableName = CustomVariableUtil.getVariableName(variable);
                    menus.add(variableName != null ? variableName : variable);
                }
                break;

            case "Component":
                title = "Select a Component";
                for (ComponentBean componentBean : projectDataManager.e(javaName)) {
                    menus.add(componentBean.componentId);
                }
                break;

            case "CustomVar":
                title = "Select a Custom Variable";
                for (String s : projectDataManager.e(javaName, 5)) {
                    Matcher matcher = Pattern.compile("^(\\w+)[\\s]+(\\w+)").matcher(s);
                    while (matcher.find()) {
                        menus.add(matcher.group(2));
                    }
                }
                for (String variable : projectDataManager.e(javaName, 6)) {
                    String variableName = CustomVariableUtil.getVariableName(variable);
                    menus.add(variableName != null ? variableName : variable);
                }
                break;

            default:
                Pair<String, ArrayList<String>> menuPair = defaultExtraMenu.getMenu(menu);
                title = menuPair.first;
                menus = new ArrayList<>(menuPair.second);
        }

        for (String menuArg : menus) {
            viewGroup.addView(logicEditor.e(menuArg));
        }
        setupSearchView(rootView, viewGroup);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof RadioButton rb) {
                if (menu.getArgValue().toString().equals(rb.getText().toString())) {
                    rb.setChecked(true);
                    break;
                }
            }
        }

        dialog.b(title);
        dialog.a(rootView);
        dialog.b(Helper.getResString(R.string.common_word_select), v -> {
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (viewGroup.getChildAt(i) instanceof RadioButton rb) {
                    if (rb.isChecked()) {
                        logicEditor.a(menu, rb.getText().toString());
                    }
                }
            }
            dialog.dismiss();
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.configureDefaultButton("Code Editor", v -> {
            AsdDialog editor = new AsdDialog(logicEditor);
            editor.setCon(menu.getArgValue().toString());
            editor.show();
            editor.saveLis(logicEditor, false, menu, editor);;
            editor.cancelLis(editor);
            dialog.dismiss();
        });
        dialog.show();
    }

    private ArrayList<String> getVarMenus(int type) {
        return projectDataManager.e(javaName, type);
    }

    private ArrayList<String> getListMenus(int type) {
        return projectDataManager.d(javaName, type);
    }

    private ArrayList<String> getComponentMenus(int type) {
        return projectDataManager.b(javaName, type);
    }

    private void asdDialog(Ss ss, String message) {
        aB dialog = new aB(logicEditor);
        dialog.b(Helper.getResString(R.string.logic_editor_title_enter_string_value));
        dialog.a(R.drawable.rename_96_blue);

        if (!isEmpty(message)) dialog.a(message);

        View root = wB.a(logicEditor, R.layout.property_popup_input_text);
        EditText edittext = root.findViewById(R.id.ed_input);
        edittext.setImeOptions(EditorInfo.IME_ACTION_NONE);

        if (ConfigActivity.isSettingEnabled(ConfigActivity.SETTING_USE_ASD_HIGHLIGHTER)) {
            new SimpleHighlighter(edittext);
        }
        edittext.setText(ss.getArgValue().toString());
        dialog.a(root);

        dialog.b(Helper.getResString(R.string.common_word_save), v -> {
            String content = edittext.getText().toString();
            if (!content.isEmpty() && content.charAt(0) == '@') {
                content = " " + content;
            }
            logicEditor.a(ss, content);
            dialog.dismiss();
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.configureDefaultButton("Code Editor", v -> {
            AsdDialog asdDialog = new AsdDialog(logicEditor);
            asdDialog.setCon(edittext.getText().toString());
            asdDialog.show();
            asdDialog.saveLis(logicEditor, false, ss, asdDialog);
            asdDialog.cancelLis(asdDialog);
            dialog.dismiss();
        });
        dialog.show();
    }

    private void pathSelectorMenu(final Ss ss) {
        String menuName = ss.getMenuName();
        ArrayList<String> markedPath = new ArrayList<>();

        mProperty.selection_mode = DialogConfigs.SINGLE_MODE;
        mProperty.selection_type = DialogConfigs.FILE_AND_DIR_SELECT;
        String path;
        if (menuName.equals("Assets")) {
            fpd.setTitle("Select an Asset");
            path = String.format(ASSETS_PATH, sc_id);
            markedPath.add(0, path + ss.getArgValue().toString());
            fpd.markFiles(markedPath);
            mProperty.root = new File(path);
            mProperty.error_dir = new File(path);
            String[] strArr = path.split("/");
            splitter = strArr[strArr.length - 1];
        } else if (menuName.equals("NativeLib")) {
            fpd.setTitle("Select a Native library");
            path = String.format(NATIVE_PATH, sc_id);
            markedPath.add(0, path + ss.getArgValue().toString());
            fpd.markFiles(markedPath);
            mProperty.selection_type = DialogConfigs.FILE_SELECT;
            mProperty.root = new File(path);
            mProperty.error_dir = new File(path);
            String[] strArr = path.split("/");
            splitter = strArr[strArr.length - 1];
        }
        fpd.setProperties(mProperty);
        fpd.setDialogSelectionListener(files -> logicEditor.a(ss, files[0].split(splitter)[1]));
        fpd.show();
    }
}
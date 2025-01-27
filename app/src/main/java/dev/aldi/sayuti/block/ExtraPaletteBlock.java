package dev.aldi.sayuti.block;

import android.util.Pair;

import a.a.a.Ox;
import a.a.a.jC;
import a.a.a.jq;
import a.a.a.kq;

import com.besome.sketch.beans.ComponentBean;
import com.besome.sketch.beans.ProjectFileBean;
import com.besome.sketch.beans.ViewBean;
import com.besome.sketch.editor.LogicEditorActivity;

import mod.agus.jcoderz.beans.ViewBeans;
import mod.hey.studios.editor.view.IdGenerator;
import mod.hey.studios.moreblock.ReturnMoreblockManager;
import mod.hilal.saif.activities.tools.ConfigActivity;
import mod.hilal.saif.blocks.BlocksHandler;
import mod.pranav.viewbinding.ViewBindingBuilder;

import pro.sketchware.activities.resources.editors.utils.StringsEditorManager;
import pro.sketchware.blocks.ExtraBlocks;
import pro.sketchware.control.logic.LogicClickListener;
import pro.sketchware.utility.CustomVariableUtil;
import pro.sketchware.utility.FileResConfig;
import pro.sketchware.utility.FileUtil;
import pro.sketchware.utility.SketchwareUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class ExtraPaletteBlock {

    private final String eventName;
    private final String javaName;
    private final String xmlName;
    private final String sc_id;

    private final LogicClickListener clickListener;
    private final ExtraBlocks extraBlocks;
    private final FileResConfig frc;
    private final HashMap<String, Object> mapSave = new HashMap<>();
    private final ProjectFileBean projectFile;
    public LogicEditorActivity logicEditor;
    private final Boolean isViewBindingEnabled;

    public ExtraPaletteBlock(LogicEditorActivity logicEditorActivity, Boolean isViewBindingEnabled) {
        logicEditor = logicEditorActivity;
        eventName = logicEditorActivity.D;

        projectFile = logicEditor.M;
        javaName = projectFile.getJavaName();
        xmlName = projectFile.getXmlName();
        sc_id = logicEditor.B;
        this.isViewBindingEnabled = isViewBindingEnabled;

        frc = new FileResConfig(sc_id);
        extraBlocks = new ExtraBlocks(logicEditor);
        clickListener = new LogicClickListener(logicEditor);
    }

    private boolean isWidgetUsed(String str) {
        if (ConfigActivity.isSettingEnabled(ConfigActivity.SETTING_SHOW_EVERY_SINGLE_BLOCK)) {
            return true;
        }

        if (mapSave.containsKey(str)) {
            Object strValueInMapSave = mapSave.get(str);
            if (strValueInMapSave instanceof Boolean) {
                return (boolean) strValueInMapSave;
            } else {
                return false;
            }
        }
        if (eventName.equals("onBindCustomView")) {
            var eC = jC.a(sc_id);
            var view = eC.c(xmlName, logicEditor.C);
            if (view == null) {
                // in case the View's in a Drawer
                view = eC.c("_drawer_" + xmlName, logicEditor.C);
            }
            String customView = view.customView;
            if (customView != null && !customView.isEmpty()) {
                for (ViewBean viewBean : jC.a(sc_id).d(ProjectFileBean.getXmlName(customView))) {
                    if (viewBean.getClassInfo().a(str)) {
                        mapSave.put(str, true);
                        return true;
                    }
                }
            }
        } else if (jC.a(sc_id).y(xmlName, str)) {
            mapSave.put(str, true);
            return true;
        }
        mapSave.put(str, false);
        return false;
    }

    /*
     * ExtraPaletteBlock#f(Ss) moved to mod.w3wide.menu.ExtraMenuBean#defineMenuSelector(Ss)
     * for better block menu selections and to add new stuff easily.
     */

    public boolean e(String str, String str2) {
        return switch (str) {
            case "circleimageview" ->
                    jC.a(sc_id).g(xmlName, ViewBeans.VIEW_TYPE_WIDGET_CIRCLEIMAGEVIEW, str2);
            case "onesignal" ->
                    jC.a(sc_id).d(javaName, ComponentBean.COMPONENT_TYPE_ONESIGNAL, str2);
            case "asynctask" -> jC.a(sc_id).d(javaName, 36, str2);
            case "otpview" -> jC.a(sc_id).g(xmlName, ViewBeans.VIEW_TYPE_WIDGET_OTPVIEW, str2);
            case "lottie" ->
                    jC.a(sc_id).g(xmlName, ViewBeans.VIEW_TYPE_WIDGET_LOTTIEANIMATIONVIEW, str2);
            case "phoneauth" ->
                    jC.a(sc_id).d(javaName, ComponentBean.COMPONENT_TYPE_FIREBASE_AUTH_PHONE, str2);
            case "fbadbanner" ->
                    jC.a(sc_id).d(javaName, ComponentBean.COMPONENT_TYPE_FACEBOOK_ADS_BANNER, str2);
            case "codeview" -> jC.a(sc_id).g(xmlName, ViewBeans.VIEW_TYPE_WIDGET_CODEVIEW, str2);
            case "recyclerview" ->
                    jC.a(sc_id).g(xmlName, ViewBeans.VIEW_TYPE_WIDGET_RECYCLERVIEW, str2);
            case "googlelogin" ->
                    jC.a(sc_id).d(javaName, ComponentBean.COMPONENT_TYPE_FIREBASE_AUTH_GOOGLE_LOGIN, str2);
            case "dynamiclink" ->
                    jC.a(sc_id).d(javaName, ComponentBean.COMPONENT_TYPE_FIREBASE_DYNAMIC_LINKS, str2);
            case "youtubeview" ->
                    jC.a(sc_id).g(xmlName, ViewBeans.VIEW_TYPE_WIDGET_YOUTUBEPLAYERVIEW, str2);
            case "signinbutton" ->
                    jC.a(sc_id).g(xmlName, ViewBeans.VIEW_TYPE_WIDGET_SIGNINBUTTON, str2);
            case "cardview" -> jC.a(sc_id).g(xmlName, ViewBeans.VIEW_TYPE_LAYOUT_CARDVIEW, str2);
            case "radiogroup" ->
                    jC.a(sc_id).g(xmlName, ViewBeans.VIEW_TYPE_LAYOUT_RADIOGROUP, str2);
            case "fbadinterstitial" ->
                    jC.a(sc_id).d(javaName, ComponentBean.COMPONENT_TYPE_FACEBOOK_ADS_INTERSTITIAL, str2);
            case "textinputlayout" ->
                    jC.a(sc_id).g(xmlName, ViewBeans.VIEW_TYPE_LAYOUT_TEXTINPUTLAYOUT, str2);
            case "collapsingtoolbar" ->
                    jC.a(sc_id).g(xmlName, ViewBeans.VIEW_TYPE_LAYOUT_COLLAPSINGTOOLBARLAYOUT, str2);
            case "cloudmessage" ->
                    jC.a(sc_id).d(javaName, ComponentBean.COMPONENT_TYPE_FIREBASE_CLOUD_MESSAGE, str2);
            case "datepicker" ->
                    jC.a(sc_id).g(xmlName, ViewBeans.VIEW_TYPE_WIDGET_DATEPICKER, str2);
            case "customVar" -> jC.a(sc_id).f(xmlName, 5, str2);
            case "timepicker" ->
                    jC.a(sc_id).g(xmlName, ViewBeans.VIEW_TYPE_WIDGET_TIMEPICKER, str2);
            case "swiperefreshlayout" ->
                    jC.a(sc_id).g(xmlName, ViewBeans.VIEW_TYPE_LAYOUT_SWIPEREFRESHLAYOUT, str2);
            default -> true;
        };
    }

    /**
     * @see ReturnMoreblockManager#listMoreblocks(Iterator, LogicEditorActivity)
     */
    private void moreBlocks() {
        ReturnMoreblockManager.listMoreblocks(jC.a(sc_id).i(javaName).iterator(), logicEditor);
    }

    private void variables() {
        ArrayList<String> booleanVariables = jC.a(sc_id).e(javaName, 0);
        for (int i = 0; i < booleanVariables.size(); i++) {
            if (i == 0) logicEditor.a("Boolean", 0xff555555);

            logicEditor.a(booleanVariables.get(i), "b", "getVar").setTag(booleanVariables.get(i));
        }

        ArrayList<String> numberVariables = jC.a(sc_id).e(javaName, 1);
        for (int i = 0; i < numberVariables.size(); i++) {
            if (i == 0) logicEditor.a("Number", 0xff555555);

            logicEditor.a(numberVariables.get(i), "d", "getVar").setTag(numberVariables.get(i));
        }

        ArrayList<String> stringVariables = jC.a(sc_id).e(javaName, 2);
        for (int i = 0; i < stringVariables.size(); i++) {
            if (i == 0) logicEditor.a("String", 0xff555555);

            logicEditor.a(stringVariables.get(i), "s", "getVar").setTag(stringVariables.get(i));
        }

        ArrayList<String> mapVariables = jC.a(sc_id).e(javaName, 3);
        for (int i = 0; i < mapVariables.size(); i++) {
            if (i == 0) logicEditor.a("Map", 0xff555555);

            logicEditor.a(mapVariables.get(i), "a", "getVar").setTag(mapVariables.get(i));
        }

        ArrayList<String> customVariables = jC.a(sc_id).e(javaName, 5);
        for (int i = 0; i < customVariables.size(); i++) {
            if (i == 0) logicEditor.a("Custom Variable", 0xff555555);

            String[] split = customVariables.get(i).split(" ");
            if (split.length > 1) {
                logicEditor.a(split[1], "v", split[0], "getVar").setTag(customVariables.get(i));
            } else {
                SketchwareUtil.toastError("Found invalid data of Custom Variable #" + (i + 1) + ": \"" + customVariables.get(i) + "\"");
            }
        }

        ArrayList<String> customVariables2 = jC.a(sc_id).e(javaName, 6);
        for (int i = 0; i < customVariables2.size(); i++) {
            if (i == 0) logicEditor.a("Custom Variable", 0xff555555);

            String variable = customVariables2.get(i);
            String variableType = CustomVariableUtil.getVariableType(variable);
            String variableName = CustomVariableUtil.getVariableName(variable);
            if (variableType != null && variableName != null) {
                String type = switch (variableType) {
                    case "boolean", "Boolean" -> "b";
                    case "String" -> "s";
                    case "double", "Double", "int", "Integer", "float", "Float", "long", "Long", "short", "Short" -> "d";
                    default -> "v";
                };
                logicEditor.a(variableName, type, variableType, "getVar").setTag(variable);
            } else {
                logicEditor.a("Invalid: " + variable, 0xfff44336);
            }
        }
        BlocksHandler.primaryBlocksA(
                logicEditor,
                extraBlocks.isVariableUsed(0),
                extraBlocks.isVariableUsed(1),
                extraBlocks.isVariableUsed(2),
                extraBlocks.isVariableUsed(3)
        );
        blockCustomViews();
        blockDrawer();
        blockEvents();
        extraBlocks.eventBlocks();
        blockComponents();
    }

    private void blockComponents() {
        ArrayList<ComponentBean> components = jC.a(sc_id).e(javaName);
        for (int i = 0, componentsSize = components.size(); i < componentsSize; i++) {
            ComponentBean component = components.get(i);

            if (i == 0) {
                logicEditor.a("Components", 0xff555555);
            }

            if (component.type != 27) {
                logicEditor.a(component.componentId, "p", ComponentBean.getComponentTypeName(component.type), "getVar").setTag(component.componentId);
            }
        }
    }

    private void blockCustomViews() {
        if (eventName.equals("onBindCustomView")) {
            String viewId = logicEditor.C;
            var eC = jC.a(sc_id);
            ViewBean viewBean = eC.c(xmlName, viewId);
            if (viewBean == null) {
                // Event is of a Drawer View
                viewBean = eC.c("_drawer_" + xmlName, viewId);
            }
            String viewBeanCustomView = viewBean.customView;
            if (viewBeanCustomView != null && !viewBeanCustomView.isEmpty()) {
                ArrayList<ViewBean> customViews = jC.a(sc_id).d(ProjectFileBean.getXmlName(viewBeanCustomView));
                for (int i = 0, customViewsSize = customViews.size(); i < customViewsSize; i++) {
                    ViewBean customView = customViews.get(i);

                    if (i == 0) {
                        logicEditor.a("Custom Views", 0xff555555);
                    }

                    if (!customView.convert.equals("include")) {
                        String typeName = customView.convert.isEmpty() ? ViewBean.getViewTypeName(customView.type) : IdGenerator.getLastPath(customView.convert);
                        logicEditor.a(customView.id, "v", typeName, "getVar").setTag(isViewBindingEnabled ? "binding." + ViewBindingBuilder.generateParameterFromId(customView.id) : customView.id);
                    }
                }
            }
            logicEditor.a(" ", "notifyDataSetChanged");
            logicEditor.a("c", "viewOnClick");
            logicEditor.a("c", "viewOnLongClick");
            logicEditor.a("c", "checkboxOnChecked");
            logicEditor.a("b", "checkboxIsChecked");
            return;
        }
        ArrayList<ViewBean> views = jC.a(sc_id).d(xmlName);
        for (int i = 0, viewsSize = views.size(); i < viewsSize; i++) {
            ViewBean view = views.get(i);
            Set<String> toNotAdd = new Ox(new jq(), projectFile).readAttributesToReplace(view);

            if (i == 0) {
                logicEditor.a("Views", 0xff555555);
            }

            if (!view.convert.equals("include")) {
                if (!toNotAdd.contains("android:id")) {
                    String typeName = view.convert.isEmpty() ? ViewBean.getViewTypeName(view.type) : IdGenerator.getLastPath(view.convert);
                    logicEditor.a(isViewBindingEnabled ? "binding." + ViewBindingBuilder.generateParameterFromId(view.id) : view.id, "v", typeName, "getVar").setTag(isViewBindingEnabled ? "binding." + ViewBindingBuilder.generateParameterFromId(view.id) : view.id);
                }
            }
        }
    }

    private void blockDrawer() {
        if (projectFile.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_DRAWER)) {
            ArrayList<ViewBean> drawerViews = jC.a(sc_id).d(projectFile.getDrawerXmlName());
            if (drawerViews != null) {
                for (int i = 0, drawerViewsSize = drawerViews.size(); i < drawerViewsSize; i++) {
                    ViewBean drawerView = drawerViews.get(i);

                    if (i == 0) {
                        logicEditor.a("Drawer Views", 0xff555555);
                    }

                    if (!drawerView.convert.equals("include")) {
                        String id = "_drawer_" + drawerView.id;
                        String typeName = drawerView.convert.isEmpty() ? ViewBean.getViewTypeName(drawerView.type) : IdGenerator.getLastPath(drawerView.convert);
                        logicEditor.a(isViewBindingEnabled ? "binding.drawer." + ViewBindingBuilder.generateParameterFromId(id) : id, "v", typeName, "getVar").setTag(id);
                    }
                }
            }
        }
    }

    private void blockEvents() {
        switch (eventName) {
            case "onTabAdded", "onTabLayoutNewTabAdded" -> {
                logicEditor.a("Fragment & TabLayout", 0xff555555);
                logicEditor.a("f", "returnTitle");
            }
            case "onFragmentAdded" -> {
                logicEditor.a("Fragment & TabLayout", 0xff555555);
                logicEditor.a("f", "returnFragment");
            }
            case "onScrollChanged" -> {
                logicEditor.a("ListView", 0xff555555);
                logicEditor.a("d", "listscrollparam");
                logicEditor.a("d", "getLastVisiblePosition");
            }
            case "onScrollChanged2" -> {
                logicEditor.a("RecyclerView", 0xff555555);
                logicEditor.a("d", "recyclerscrollparam");
            }
            case "onPageChanged" -> {
                logicEditor.a("ViewPager", 0xff555555);
                logicEditor.a("d", "pagerscrollparam");
            }
            case "onCreateOptionsMenu" -> {
                logicEditor.a("Menu", 0xff555555);
                logicEditor.a(" ", "menuInflater");
                logicEditor.a(" ", "menuAddItem");
                logicEditor.a(" ", "menuAddMenuItem");
                logicEditor.a("c", "menuAddSubmenu");
                logicEditor.a(" ", "submenuAddItem");
            }
            default -> {
            }
        }
    }

    private void list() {
        for (Pair<Integer, String> list : jC.a(sc_id).j(javaName)) {
            int type = list.first;
            String name = list.second;

            switch (type) {
                case 1, 2, 3 -> logicEditor.a(name, "l", kq.a(type), "getVar").setTag(name);
                default -> {
                    String variableName = CustomVariableUtil.getVariableName(name);
                    if (variableName != null) {
                        logicEditor.a(variableName, "l", "List", "getVar").setTag(name);
                    } else {
                        logicEditor.a("Invalid: " + name, 0xfff44336);
                    }
                }
            }
        }

        BlocksHandler.primaryBlocksB(
                logicEditor,
                extraBlocks.isListUsed(1),
                extraBlocks.isListUsed(2),
                extraBlocks.isListUsed(3)
        );
    }

    public void setBlock(int paletteId, int paletteColor) {
        // Remove previous palette's blocks
        logicEditor.m.a();

        if (eventName.equals("Import")) {
            if (paletteId == 3) {
                logicEditor.a(" ", "addSourceDirectly");
            } else {
                logicEditor.a("Enter the path without import & semicolon", 0xff555555);
                logicEditor.a(" ", "customImport");
                logicEditor.a(" ", "customImport2");
            }
            return;
        }

        switch (paletteId) {
            case -1:
                String filePath = FileUtil.getExternalStorageDir().concat("/.sketchware/data/").concat(sc_id.concat("/files/resource/values/strings.xml"));
                ArrayList<HashMap<String, Object>> StringsListMap = new ArrayList<>();
                StringsEditorManager stringsEditorManager = new StringsEditorManager();
                stringsEditorManager.convertXmlStringsToListMap(FileUtil.readFileIfExist(filePath), StringsListMap);


                logicEditor.b("Add new String", "XmlString.Add");
                logicEditor.b("Remove String(s)", "XmlString.remove");
                logicEditor.b("Open Resources editor", "openResourcesEditor");

                logicEditor.a("s", "getResString");
                logicEditor.a("Saved Res Strings :", 0xff555555);
                if (!stringsEditorManager.isXmlStringsExist(StringsListMap, "app_name")) {
                    logicEditor.a("app_name", "s", "getResStr").setTag("S98ZCSapp_name");
                }

                for (int i = 0; i < StringsListMap.size(); i++) {
                    String key = StringsListMap.get(i).get("key").toString();
                    logicEditor.a(key, "s", "getResStr").setTag("S98ZCS" + key);
                }
                return;
            case 0:
                logicEditor.b("Add variable", "variableAdd");
                logicEditor.b("Add custom variable", "variableAddNew", clickListener);
                logicEditor.b("Remove variable", "variableRemove", clickListener);
                variables();
                return;

            case 1:
                logicEditor.b("Add list", "listAdd");
                logicEditor.b("Add custom List", "listAddCustom", clickListener);
                logicEditor.b("Remove list", "listRemove", clickListener);
                list();
                return;

            case 2:
                BlocksHandler.primaryBlocksC(logicEditor);
                return;

            case 3:
                BlocksHandler.primaryBlocksD(logicEditor);
                return;

            case 4:
                logicEditor.a("d", "mathGetDip");
                logicEditor.a("d", "mathGetDisplayWidth");
                logicEditor.a("d", "mathGetDisplayHeight");
                logicEditor.a("d", "mathPi");
                logicEditor.a("d", "mathE");
                logicEditor.a("d", "mathPow");
                logicEditor.a("d", "mathMin");
                logicEditor.a("d", "mathMax");
                logicEditor.a("d", "mathSqrt");
                logicEditor.a("d", "mathAbs");
                logicEditor.a("d", "mathRound");
                logicEditor.a("d", "mathCeil");
                logicEditor.a("d", "mathFloor");
                logicEditor.a("d", "mathSin");
                logicEditor.a("d", "mathCos");
                logicEditor.a("d", "mathTan");
                logicEditor.a("d", "mathAsin");
                logicEditor.a("d", "mathAcos");
                logicEditor.a("d", "mathAtan");
                logicEditor.a("d", "mathExp");
                logicEditor.a("d", "mathLog");
                logicEditor.a("d", "mathLog10");
                logicEditor.a("d", "mathToRadian");
                logicEditor.a("d", "mathToDegree");
                return;

            case 5:
                extraBlocks.fileBlocks();
                logicEditor.a("FileUtil Blocks", 0xff555555);
                if (!frc.getAssetsFile().isEmpty()) {
                    logicEditor.a(" ", "getAssetFile");
                    logicEditor.a("s", "copyAssetFile");
                }
                logicEditor.a("s", "fileutilread");
                logicEditor.a(" ", "fileutilwrite");
                logicEditor.a(" ", "fileutilcopy");
                logicEditor.a(" ", "fileutilcopydir");
                logicEditor.a(" ", "fileutilmove");
                logicEditor.a(" ", "fileutildelete");
                logicEditor.a(" ", "renameFile");
                logicEditor.a("b", "fileutilisexist");
                logicEditor.a(" ", "fileutilmakedir");
                logicEditor.a(" ", "fileutillistdir");
                logicEditor.a("b", "fileutilisdir");
                logicEditor.a("b", "fileutilisfile");
                logicEditor.a("d", "fileutillength");
                logicEditor.a("b", "fileutilStartsWith");
                logicEditor.a("b", "fileutilEndsWith");
                logicEditor.a("s", "fileutilGetLastSegmentPath");
                logicEditor.a("s", "getExternalStorageDir");
                logicEditor.a("s", "getPackageDataDir");
                logicEditor.a("s", "getPublicDir");
                logicEditor.a(" ", "resizeBitmapFileRetainRatio");
                logicEditor.a(" ", "resizeBitmapFileToSquare");
                logicEditor.a(" ", "resizeBitmapFileToCircle");
                logicEditor.a(" ", "resizeBitmapFileWithRoundedBorder");
                logicEditor.a(" ", "cropBitmapFileFromCenter");
                logicEditor.a(" ", "rotateBitmapFile");
                logicEditor.a(" ", "scaleBitmapFile");
                logicEditor.a(" ", "skewBitmapFile");
                logicEditor.a(" ", "setBitmapFileColorFilter");
                logicEditor.a(" ", "setBitmapFileBrightness");
                logicEditor.a(" ", "setBitmapFileContrast");
                logicEditor.a("d", "getJpegRotate");
                return;

            case 6:
                logicEditor.a(" ", "setEnable");
                logicEditor.a("b", "getEnable");
                logicEditor.a(" ", "setVisible");
                logicEditor.a("b", "checkViewVisibility");
                logicEditor.a(" ", "setElevation");
                logicEditor.a(" ", "setRotate");
                logicEditor.a("d", "getRotate");
                logicEditor.a(" ", "setAlpha");
                logicEditor.a("d", "getAlpha");
                logicEditor.a(" ", "setTranslationX");
                logicEditor.a("d", "getTranslationX");
                logicEditor.a(" ", "setTranslationY");
                logicEditor.a("d", "getTranslationY");
                logicEditor.a(" ", "setScaleX");
                logicEditor.a("d", "getScaleX");
                logicEditor.a(" ", "setScaleY");
                logicEditor.a("d", "getScaleY");
                logicEditor.a("d", "getLocationX");
                logicEditor.a("d", "getLocationY");
                logicEditor.a("d", "getHeight");
                logicEditor.a("d", "getWidth");
                logicEditor.a(" ", "requestFocus");
                logicEditor.a(" ", "removeView");
                logicEditor.a(" ", "removeViews");
                logicEditor.a(" ", "addView");
                logicEditor.a("v", "viewGetChildAt");
                logicEditor.a(" ", "addViews");
                logicEditor.a(" ", "setGravity");
                logicEditor.a(" ", "setColorFilterView");
                logicEditor.a(" ", "setBgColor");
                logicEditor.a(" ", "setBgResource");
                logicEditor.a(" ", "setBgDrawable");
                logicEditor.a(" ", "setStrokeView");
                logicEditor.a(" ", "setCornerRadiusView");
                logicEditor.a(" ", "setGradientBackground");
                logicEditor.a(" ", "setRadiusAndStrokeView");
            {
                boolean editTextUsed = isWidgetUsed("EditText")
                        || extraBlocks.isCustomVarUsed("EditText");
                boolean textViewUsed = isWidgetUsed("TextView")
                        || extraBlocks.isCustomVarUsed("TextView") || editTextUsed;
                boolean compoundButtonUsed = isWidgetUsed("CompoundButton")
                        || extraBlocks.isCustomVarUsed("CompoundButton")
                        || extraBlocks.isCustomVarUsed("CheckBox")
                        || extraBlocks.isCustomVarUsed("RadioButton")
                        || extraBlocks.isCustomVarUsed("Switch")
                        || extraBlocks.isCustomVarUsed("ToggleButton");
                boolean autoCompleteTextViewUsed = isWidgetUsed("AutoCompleteTextView")
                        || extraBlocks.isCustomVarUsed("AutoCompleteTextView");
                boolean multiAutoCompleteTextViewUsed = isWidgetUsed("MultiAutoCompleteTextView")
                        || extraBlocks.isCustomVarUsed("MultiAutoCompleteTextView");
                boolean imageViewUsed = isWidgetUsed("ImageView") || isWidgetUsed("CircleImageView")
                        || extraBlocks.isCustomVarUsed("ImageView");
                boolean ratingBarUsed = isWidgetUsed("RatingBar")
                        || extraBlocks.isCustomVarUsed("RatingBar");
                boolean seekBarUsed = isWidgetUsed("SeekBar")
                        || extraBlocks.isCustomVarUsed("SeekBar");
                boolean progressBarUsed = isWidgetUsed("ProgressBar")
                        || extraBlocks.isCustomVarUsed("ProgressBar");
                boolean videoViewUsed = isWidgetUsed("VideoView")
                        || extraBlocks.isCustomVarUsed("VideoView");
                boolean webViewUsed = isWidgetUsed("WebView")
                        || extraBlocks.isCustomVarUsed("WebView");

                if (textViewUsed || compoundButtonUsed || autoCompleteTextViewUsed
                        || multiAutoCompleteTextViewUsed || imageViewUsed || ratingBarUsed
                        || seekBarUsed || progressBarUsed || videoViewUsed || webViewUsed) {
                    logicEditor.a("Widgets", 0xff555555);

                    if (textViewUsed) {
                        logicEditor.a(" ", "setText");
                        logicEditor.a("s", "getText");
                        logicEditor.a(" ", "setTypeface");
                        logicEditor.a(" ", "setTextColor");
                        logicEditor.a(" ", "setTextSize");
                    }

                    if (editTextUsed) {
                        logicEditor.a(" ", "setHint");
                        logicEditor.a(" ", "setHintTextColor");
                        logicEditor.a(" ", "EditTextdiableSuggestion");
                        logicEditor.a(" ", "EditTextLines");
                        logicEditor.a(" ", "EditTextSingleLine");
                        logicEditor.a(" ", "EditTextShowError");
                        logicEditor.a(" ", "EditTextSelectAll");
                        logicEditor.a(" ", "EditTextSetSelection");
                        logicEditor.a(" ", "EditTextSetMaxLines");
                        logicEditor.a("d", "EdittextGetselectionStart");
                        logicEditor.a("d", "EdittextGetselectionEnd");
                    }

                    if (compoundButtonUsed) {
                        logicEditor.a(" ", "setChecked");
                        logicEditor.a("b", "getChecked");
                    }

                    if (autoCompleteTextViewUsed) {
                        logicEditor.a(" ", "autoComSetData");
                    }

                    if (multiAutoCompleteTextViewUsed) {
                        logicEditor.a(" ", "multiAutoComSetData");
                        logicEditor.a(" ", "setThreshold");
                        logicEditor.a(" ", "setTokenizer");
                    }

                    if (imageViewUsed) {
                        logicEditor.a(" ", "setImage");
                        logicEditor.a(" ", "setImageCustomRes");
                        logicEditor.a(" ", "setImageIdentifier");
                        logicEditor.a(" ", "setImageFilePath");
                        logicEditor.a(" ", "setImageUrl");
                        logicEditor.a(" ", "setColorFilter");
                    }

                    if (ratingBarUsed) {
                        logicEditor.a("d", "getRating");
                        logicEditor.a(" ", "setRating");
                        logicEditor.a(" ", "setNumStars");
                        logicEditor.a(" ", "setStepSize");
                    }

                    if (seekBarUsed) {
                        logicEditor.a(" ", "seekBarSetProgress");
                        logicEditor.a("d", "seekBarGetProgress");
                        logicEditor.a(" ", "seekBarSetMax");
                        logicEditor.a("d", "seekBarGetMax");
                    }

                    if (progressBarUsed) {
                        logicEditor.a(" ", "progressBarSetIndeterminate");
                    }

                    if (videoViewUsed) {
                        logicEditor.a(" ", "videoviewSetVideoUri");
                        logicEditor.a(" ", "videoviewStart");
                        logicEditor.a(" ", "videoviewPause");
                        logicEditor.a(" ", "videoviewStop");
                        logicEditor.a("b", "videoviewIsPlaying");
                        logicEditor.a("b", "videoviewCanPause");
                        logicEditor.a("b", "videoviewCanSeekForward");
                        logicEditor.a("b", "videoviewCanSeekBackward");
                        logicEditor.a("d", "videoviewGetCurrentPosition");
                        logicEditor.a("d", "videoviewGetDuration");
                    }

                    if (webViewUsed) {
                        logicEditor.a(" ", "webViewLoadUrl");
                        logicEditor.a("s", "webViewGetUrl");
                        logicEditor.a("d", "webviewGetProgress");
                        logicEditor.a(" ", "webViewSetCacheMode");
                        logicEditor.a("b", "webViewCanGoBack");
                        logicEditor.a("b", "webViewCanGoForward");
                        logicEditor.a(" ", "webViewGoBack");
                        logicEditor.a(" ", "webViewGoForward");
                        logicEditor.a(" ", "webViewClearCache");
                        logicEditor.a(" ", "webViewClearHistory");
                        logicEditor.a(" ", "webViewStopLoading");
                        logicEditor.a(" ", "webViewZoomIn");
                        logicEditor.a(" ", "webViewZoomOut");
                    }
                }
            }
            {
                boolean inOnBindCustomView = eventName.equals("onBindCustomView");
                boolean spinnerUsed = isWidgetUsed("Spinner");
                boolean listViewUsed = isWidgetUsed("ListView");
                boolean recyclerViewUsed = isWidgetUsed("RecyclerView");
                boolean gridViewUsed = isWidgetUsed("GridView") || extraBlocks.isCustomVarUsed("GridView");
                boolean viewPagerUsed = isWidgetUsed("ViewPager");

                if (spinnerUsed || listViewUsed || recyclerViewUsed || gridViewUsed || viewPagerUsed) {
                    logicEditor.a("List", 0xff555555);

                    if (spinnerUsed) {
                        logicEditor.a(" ", "spnSetData");
                        logicEditor.a(" ", "spnSetCustomViewData");
                        logicEditor.a(" ", "spnRefresh");
                        logicEditor.a(" ", "spnSetSelection");
                        logicEditor.a("d", "spnGetSelection");
                    }

                    if (!inOnBindCustomView) {
                        if (listViewUsed) {
                            logicEditor.a(" ", "listSetData");
                            logicEditor.a(" ", "listSetCustomViewData");
                            logicEditor.a(" ", "listRefresh");
                            logicEditor.a(" ", "refreshingList");
                            logicEditor.a(" ", "listSmoothScrollTo");
                            logicEditor.a(" ", "listViewSetSelection");
                            logicEditor.a(" ", "listSetTranscriptMode");
                            logicEditor.a(" ", "listSetStackFromBottom");
                            logicEditor.a(" ", "ListViewAddHeader");
                            logicEditor.a(" ", "listViewRemoveHeader");
                            logicEditor.a(" ", "ListViewAddFooter");
                            logicEditor.a(" ", "listViewRemoveFooter");
                        }

                        if (recyclerViewUsed) {
                            logicEditor.a(" ", "recyclerSetCustomViewData");
                            logicEditor.a(" ", "recyclerSetLayoutManager");
                            logicEditor.a(" ", "recyclerSetLayoutManagerHorizontal");
                            logicEditor.a(" ", "recyclerSetHasFixedSize");
                            logicEditor.a(" ", "recyclerSmoothScrollToPosition");
                            logicEditor.a(" ", "recyclerScrollToPositionWithOffset");
                        }

                        if (gridViewUsed) {
                            logicEditor.a(" ", "gridSetCustomViewData");
                            logicEditor.a(" ", "gridSetNumColumns");
                            logicEditor.a(" ", "gridSetColumnWidth");
                            logicEditor.a(" ", "gridSetVerticalSpacing");
                            logicEditor.a(" ", "gridSetHorizontalSpacing");
                            logicEditor.a(" ", "gridSetStretchMode");
                        }

                        if (viewPagerUsed) {
                            logicEditor.a(" ", "pagerSetCustomViewData");
                            logicEditor.a(" ", "pagerSetFragmentAdapter");
                            logicEditor.a("d", "pagerGetOffscreenPageLimit");
                            logicEditor.a(" ", "pagerSetOffscreenPageLimit");
                            logicEditor.a("d", "pagerGetCurrentItem");
                            logicEditor.a(" ", "pagerSetCurrentItem");
                            logicEditor.a(" ", "ViewPagerNotifyOnDtatChange");
                        }
                    } else {
                        logicEditor.a(" ", "setRecyclerViewLayoutParams");
                    }
                }
            }
            {
                boolean drawerUsed = projectFile.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_DRAWER);
                boolean fabUsed = projectFile.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_FAB);
                boolean bottomNavigationViewUsed = isWidgetUsed("BottomNavigationView");
                boolean swipeRefreshLayoutUsed = isWidgetUsed("SwipeRefreshLayout");
                boolean cardViewUsed = isWidgetUsed("CardView");
                boolean tabLayoutUsed = isWidgetUsed("TabLayout");
                boolean textInputLayoutUsed = isWidgetUsed("TextInputLayout") || extraBlocks.isCustomVarUsed("TextInputLayout");

                if (drawerUsed || fabUsed || bottomNavigationViewUsed || swipeRefreshLayoutUsed || cardViewUsed || tabLayoutUsed || textInputLayoutUsed) {
                    logicEditor.a("AndroidX components", 0xff555555);

                    if (drawerUsed) {
                        logicEditor.a("b", "isDrawerOpen");
                        logicEditor.a(" ", "openDrawer");
                        logicEditor.a(" ", "closeDrawer");
                    }

                    if (fabUsed) {
                        logicEditor.a(" ", "fabIcon");
                        logicEditor.a(" ", "fabSize");
                        logicEditor.a(" ", "fabVisibility");
                    }

                    if (bottomNavigationViewUsed) {
                        logicEditor.a(" ", "bottomMenuAddItem");
                    }

                    if (swipeRefreshLayoutUsed) {
                        logicEditor.a("c", "onSwipeRefreshLayout");
                        logicEditor.a(" ", "setRefreshing");
                    }

                    if (cardViewUsed) {
                        logicEditor.a(" ", "setCardBackgroundColor");
                        logicEditor.a(" ", "setCardRadius");
                        logicEditor.a(" ", "setCardElevation");
                        logicEditor.a(" ", "setPreventCornerOverlap");
                        logicEditor.a(" ", "setUseCompatPadding");
                    }

                    if (tabLayoutUsed) {
                        logicEditor.a(" ", "addTab");
                        logicEditor.a(" ", "setupWithViewPager");
                        logicEditor.a(" ", "setInlineLabel");
                        logicEditor.a(" ", "setTabTextColors");
                        logicEditor.a(" ", "setTabRippleColor");
                        logicEditor.a(" ", "setSelectedTabIndicatorColor");
                        logicEditor.a(" ", "setSelectedTabIndicatorHeight");
                    }

                    if (textInputLayoutUsed) {
                        logicEditor.a(" ", "tilSetBoxBgColor");
                        logicEditor.a(" ", "tilSetBoxStrokeColor");
                        logicEditor.a(" ", "tilSetBoxBgMode");
                        logicEditor.a(" ", "tilSetBoxCornerRadii");
                        logicEditor.a(" ", "tilSetError");
                        logicEditor.a(" ", "tilSetErrorEnabled");
                        logicEditor.a(" ", "tilSetCounterEnabled");
                        logicEditor.a(" ", "tilSetCounterMaxLength");
                        logicEditor.a("d", "tilGetCounterMaxLength");
                    }
                }
            }
            {
                boolean waveSideBarUsed = isWidgetUsed("WaveSideBar");
                boolean badgeViewUsed = isWidgetUsed("BadgeView");
                boolean bubbleLayoutUsed = isWidgetUsed("BubbleLayout");
                boolean patternLockViewUsed = isWidgetUsed("PatternLockView");
                boolean codeViewUsed = isWidgetUsed("CodeView");
                boolean lottieAnimationViewUsed = isWidgetUsed("LottieAnimationView");
                boolean otpViewUsed = isWidgetUsed("OTPView");

                if (waveSideBarUsed || badgeViewUsed || bubbleLayoutUsed || patternLockViewUsed || codeViewUsed || lottieAnimationViewUsed) {
                    logicEditor.a("Library", 0xff555555);

                    if (otpViewUsed) {
                        logicEditor.a(" ", "otpViewSetFieldCount");
                        logicEditor.a(" ", "otpViewSetOTPText");
                        logicEditor.a("s", "otpViewGetOTPText");
                        logicEditor.a("c", "otpViewSetOTPListener");
                    }

                    if (waveSideBarUsed) {
                        logicEditor.a(" ", "setCustomLetter");
                    }

                    if (badgeViewUsed) {
                        logicEditor.a("d", "getBadgeCount");
                        logicEditor.a(" ", "setBadgeNumber");
                        logicEditor.a(" ", "setBadgeString");
                        logicEditor.a(" ", "setBadgeBackground");
                        logicEditor.a(" ", "setBadgeTextColor");
                        logicEditor.a(" ", "setBadgeTextSize");
                    }

                    if (bubbleLayoutUsed) {
                        logicEditor.a(" ", "setBubbleColor");
                        logicEditor.a(" ", "setBubbleStrokeColor");
                        logicEditor.a(" ", "setBubbleStrokeWidth");
                        logicEditor.a(" ", "setBubbleCornerRadius");
                        logicEditor.a(" ", "setBubbleArrowHeight");
                        logicEditor.a(" ", "setBubbleArrowWidth");
                        logicEditor.a(" ", "setBubbleArrowPosition");
                    }

                    if (patternLockViewUsed) {
                        logicEditor.a("s", "patternToString");
                        logicEditor.a("s", "patternToMD5");
                        logicEditor.a("s", "patternToSha1");
                        logicEditor.a(" ", "patternSetDotCount");
                        logicEditor.a(" ", "patternSetNormalStateColor");
                        logicEditor.a(" ", "patternSetCorrectStateColor");
                        logicEditor.a(" ", "patternSetWrongStateColor");
                        logicEditor.a(" ", "patternSetViewMode");
                        logicEditor.a(" ", "patternLockClear");
                    }

                    if (codeViewUsed) {
                        logicEditor.a(" ", "codeviewSetCode");
                        logicEditor.a(" ", "codeviewSetLanguage");
                        logicEditor.a(" ", "codeviewSetTheme");
                        logicEditor.a(" ", "codeviewApply");
                    }

                    if (lottieAnimationViewUsed) {
                        logicEditor.a(" ", "lottieSetAnimationFromAsset");
                        logicEditor.a(" ", "lottieSetAnimationFromJson");
                        logicEditor.a(" ", "lottieSetAnimationFromUrl");
                        logicEditor.a(" ", "lottieSetRepeatCount");
                        logicEditor.a(" ", "lottieSetSpeed");
                    }
                }
            }
            {
                boolean signInButtonUsed = isWidgetUsed("SignInButton");
                boolean youtubePlayerViewUsed = isWidgetUsed("YoutubePlayerView");
                boolean adMobUsed = "Y".equals(jC.c(sc_id).b().useYn);
                boolean mapViewUsed = isWidgetUsed("MapView");

                if (signInButtonUsed || youtubePlayerViewUsed || adMobUsed || mapViewUsed) {
                    logicEditor.a("Google", 0xff555555);

                    if (signInButtonUsed) {
                        logicEditor.a(" ", "signInButtonSetColorScheme");
                        logicEditor.a(" ", "signInButtonSetSize");
                    }

                    if (youtubePlayerViewUsed) {
                        logicEditor.a(" ", "YTPVLifecycle");
                        logicEditor.a("c", "YTPVSetListener");
                    }

                    if (adMobUsed) {
                        logicEditor.a(" ", "bannerAdViewLoadAd");
                    }

                    if (mapViewUsed) {
                        logicEditor.a(" ", "mapViewSetMapType");
                        logicEditor.a(" ", "mapViewMoveCamera");
                        logicEditor.a(" ", "mapViewZoomTo");
                        logicEditor.a(" ", "mapViewZoomIn");
                        logicEditor.a(" ", "mapViewZoomOut");
                        logicEditor.a(" ", "mapViewAddMarker");
                        logicEditor.a(" ", "mapViewSetMarkerInfo");
                        logicEditor.a(" ", "mapViewSetMarkerPosition");
                        logicEditor.a(" ", "mapViewSetMarkerColor");
                        logicEditor.a(" ", "mapViewSetMarkerIcon");
                        logicEditor.a(" ", "mapViewSetMarkerVisible");
                    }
                }
            }
            {
                boolean timePickerUsed = isWidgetUsed("TimePicker");
                boolean calendarViewUsed = isWidgetUsed("CalendarView");

                if (timePickerUsed || calendarViewUsed) {
                    logicEditor.a("Date & Time", 0xff555555);

                    if (timePickerUsed) {
                        logicEditor.a(" ", "timepickerSetHour");
                        logicEditor.a(" ", "timepickerSetMinute");
                        logicEditor.a(" ", "timepickerSetCurrentHour");
                        logicEditor.a(" ", "timepickerSetCurrentMinute");
                        logicEditor.a(" ", "timepickerSetIs24Hour");
                    }

                    if (calendarViewUsed) {
                        logicEditor.a(" ", "calendarViewSetDate");
                        logicEditor.a(" ", "calendarViewSetMinDate");
                        logicEditor.a(" ", "calnedarViewSetMaxDate");
                    }
                }
            }
            logicEditor.a("Function", 0xff555555);
            logicEditor.a(" ", "performClick");
            logicEditor.a("c", "viewOnClick");
            logicEditor.a("c", "viewOnLongClick");
            logicEditor.a("c", "viewOnTouch");
            logicEditor.a("c", "showSnackbar");
            return;

            case 7:
                logicEditor.b("Add component", "componentAdd");
                logicEditor.a(" ", "changeStatebarColour");
                logicEditor.a(" ", "LightStatusBar");
                logicEditor.a(" ", "showKeyboard");
                logicEditor.a(" ", "hideKeyboard");
                logicEditor.a(" ", "doToast");
                logicEditor.a(" ", "copyToClipboard");
                logicEditor.a(" ", "setTitle");
                logicEditor.a("b", "intentHasExtra");
                logicEditor.a("s", "intentGetString");
                logicEditor.a("f", "finishActivity");
                logicEditor.a("f", "finishAffinity");
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_INTENT)
                        || extraBlocks.isCustomVarUsed("Intent")) {
                    logicEditor.a("Intent", 0xff555555);
                    logicEditor.a(" ", "intentSetAction");
                    logicEditor.a(" ", "intentSetData");
                    logicEditor.a(" ", "intentSetType");
                    logicEditor.a(" ", "intentSetScreen");
                    logicEditor.a(" ", "launchApp");
                    logicEditor.a(" ", "intentPutExtra");
                    logicEditor.a(" ", "intentRemoveExtra");
                    logicEditor.a(" ", "intentSetFlags");
                    logicEditor.a(" ", "startActivity");
                    logicEditor.a(" ", "startActivityWithChooser");
                }
                if (!frc.getBroadcastFile().isEmpty()) {
                    logicEditor.a("Broadcast", 0xff555555);
                    logicEditor.a(" ", "sendBroadcast");
                }
                if (!frc.getServiceFile().isEmpty()) {
                    logicEditor.a("Service", 0xff555555);
                    logicEditor.a(" ", "startService");
                    logicEditor.a(" ", "stopService");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_SHAREDPREF)) {
                    logicEditor.a("SharedPreferences", 0xff555555);
                    logicEditor.a("b", "fileContainsData");
                    logicEditor.a("s", "fileGetData");
                    logicEditor.a(" ", "fileSetData");
                    logicEditor.a(" ", "fileRemoveData");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_DATE_PICKER_DIALOG)) {
                    logicEditor.a("DatePickerDialog", 0xff555555);
                    logicEditor.a(" ", "datePickerDialogShow");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_TIME_PICKER_DIALOG)) {
                    logicEditor.a("TimePickerDialog", 0xff555555);
                    logicEditor.a(" ", "timePickerDialogShow");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_CALENDAR)) {
                    logicEditor.a("Calendar", 0xff555555);
                    logicEditor.a(" ", "calendarGetNow");
                    logicEditor.a(" ", "calendarAdd");
                    logicEditor.a(" ", "calendarSet");
                    logicEditor.a("s", "calendarFormat");
                    logicEditor.a("d", "calendarDiff");
                    logicEditor.a("d", "calendarGetTime");
                    logicEditor.a(" ", "calendarSetTime");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_VIBRATOR)) {
                    logicEditor.a("Vibrator", 0xff555555);
                    logicEditor.a(" ", "vibratorAction");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_TIMERTASK)
                        || extraBlocks.isCustomVarUsed("Timer")) {
                    logicEditor.a("Timer", 0xff555555);
                    logicEditor.a("c", "timerAfter");
                    logicEditor.a("c", "timerEvery");
                    logicEditor.a(" ", "timerCancel");
                }
                if (extraBlocks.isComponentUsed(36)) {
                    logicEditor.a("AsyncTask", 0xff555555);
                    logicEditor.a(" ", "AsyncTaskExecute");
                    logicEditor.a(" ", "AsyncTaskPublishProgress");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_DIALOG)
                        || extraBlocks.isCustomVarUsed("Dialog")) {
                    logicEditor.a("Dialog", 0xff555555);
                    logicEditor.a(" ", "dialogSetTitle");
                    logicEditor.a(" ", "Dialog SetIcon");
                    logicEditor.a(" ", "dialogSetMessage");
                    logicEditor.a("c", "dialogOkButton");
                    logicEditor.a("c", "dialogCancelButton");
                    logicEditor.a("c", "dialogNeutralButton");
                    logicEditor.a(" ", "dialogShow");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_MEDIAPLAYER)) {
                    logicEditor.a("MediaPlayer", 0xff555555);
                    logicEditor.a(" ", "mediaplayerCreate");
                    logicEditor.a(" ", "mediaplayerStart");
                    logicEditor.a(" ", "mediaplayerPause");
                    logicEditor.a(" ", "mediaplayerSeek");
                    logicEditor.a("d", "mediaplayerGetCurrent");
                    logicEditor.a("d", "mediaplayerGetDuration");
                    logicEditor.a("b", "mediaplayerIsPlaying");
                    logicEditor.a(" ", "mediaplayerSetLooping");
                    logicEditor.a("b", "mediaplayerIsLooping");
                    logicEditor.a(" ", "mediaplayerReset");
                    logicEditor.a(" ", "mediaplayerRelease");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_SOUNDPOOL)) {
                    logicEditor.a("SoundPool", 0xff555555);
                    logicEditor.a(" ", "soundpoolCreate");
                    logicEditor.a("d", "soundpoolLoad");
                    logicEditor.a("d", "soundpoolStreamPlay");
                    logicEditor.a(" ", "soundpoolStreamStop");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_OBJECTANIMATOR)) {
                    logicEditor.a("ObjectAnimator", 0xff555555);
                    logicEditor.a(" ", "objectanimatorSetTarget");
                    logicEditor.a(" ", "objectanimatorSetProperty");
                    logicEditor.a(" ", "objectanimatorSetValue");
                    logicEditor.a(" ", "objectanimatorSetFromTo");
                    logicEditor.a(" ", "objectanimatorSetDuration");
                    logicEditor.a(" ", "objectanimatorSetRepeatMode");
                    logicEditor.a(" ", "objectanimatorSetRepeatCount");
                    logicEditor.a(" ", "objectanimatorSetInterpolator");
                    logicEditor.a(" ", "objectanimatorStart");
                    logicEditor.a(" ", "objectanimatorCancel");
                    logicEditor.a("b", "objectanimatorIsRunning");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_FIREBASE)) {
                    logicEditor.a("Firebase", 0xff555555);
                    logicEditor.a(" ", "firebaseAdd");
                    logicEditor.a(" ", "firebasePush");
                    logicEditor.a("s", "firebaseGetPushKey");
                    logicEditor.a(" ", "firebaseDelete");
                    logicEditor.a("c", "firebaseGetChildren");
                    logicEditor.a(" ", "firebaseStartListen");
                    logicEditor.a(" ", "firebaseStopListen");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_FIREBASE_AUTH)) {
                    logicEditor.a("FirebaseAuth", 0xff555555);
                    logicEditor.a("b", "firebaseauthIsLoggedIn");
                    logicEditor.a("s", "firebaseauthGetCurrentUser");
                    logicEditor.a("s", "firebaseauthGetUid");
                    logicEditor.a(" ", "firebaseauthCreateUser");
                    logicEditor.a(" ", "firebaseauthSignInUser");
                    logicEditor.a(" ", "firebaseauthSignInAnonymously");
                    logicEditor.a(" ", "firebaseauthResetPassword");
                    logicEditor.a(" ", "firebaseauthSignOutUser");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_FIREBASE_DYNAMIC_LINKS)) {
                    logicEditor.a(" ", "setDynamicLinkDataHost");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_GYROSCOPE)) {
                    logicEditor.a("Gyroscope", 0xff555555);
                    logicEditor.a(" ", "gyroscopeStartListen");
                    logicEditor.a(" ", "gyroscopeStopListen");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_INTERSTITIAL_AD)) {
                    logicEditor.a("AdMob Interstitial", 0xff555555);
                    logicEditor.a(" ", "interstitialAdLoad");
                    logicEditor.a(" ", "interstitialAdShow");
                    logicEditor.a("b", "interstitialAdIsLoaded");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_REWARDED_VIDEO_AD)) {
                    logicEditor.a("RewardedVideoAd", 0xff555555);
                    logicEditor.a(" ", "rewardedVideoAdLoad");
                    logicEditor.a(" ", "rewardedVideoAdShow");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_FIREBASE_STORAGE)) {
                    logicEditor.a("Firebase Storage", 0xff555555);
                    logicEditor.a(" ", "firebasestorageUploadFile");
                    logicEditor.a(" ", "firebasestorageDownloadFile");
                    logicEditor.a(" ", "firebasestorageDelete");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_CAMERA)) {
                    logicEditor.a("Camera", 0xff555555);
                    logicEditor.a(" ", "camerastarttakepicture");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_FILE_PICKER)) {
                    logicEditor.a("FilePicker", 0xff555555);
                    logicEditor.a(" ", "filepickerstartpickfiles");
                    logicEditor.a(" ", "imageCrop");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_REQUEST_NETWORK)) {
                    logicEditor.a("RequestNetwork", 0xff555555);
                    logicEditor.a("b", "isConnected");
                    logicEditor.a(" ", "requestnetworkSetParams");
                    logicEditor.a(" ", "requestnetworkSetHeaders");
                    logicEditor.a(" ", "requestnetworkStartRequestNetwork");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_TEXT_TO_SPEECH)) {
                    logicEditor.a("TextToSpeech", 0xff555555);
                    logicEditor.a("b", "textToSpeechIsSpeaking");
                    logicEditor.a(" ", "textToSpeechSetPitch");
                    logicEditor.a(" ", "textToSpeechSetSpeechRate");
                    logicEditor.a(" ", "textToSpeechSpeak");
                    logicEditor.a(" ", "textToSpeechStop");
                    logicEditor.a(" ", "textToSpeechShutdown");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_SPEECH_TO_TEXT)) {
                    logicEditor.a("SpeechToText", 0xff555555);
                    logicEditor.a(" ", "speechToTextStartListening");
                    logicEditor.a(" ", "speechToTextStopListening");
                    logicEditor.a(" ", "speechToTextShutdown");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_BLUETOOTH_CONNECT)) {
                    logicEditor.a("Bluetooth", 0xff555555);
                    logicEditor.a("b", "bluetoothConnectIsBluetoothEnabled");
                    logicEditor.a("b", "bluetoothConnectIsBluetoothActivated");
                    logicEditor.a("s", "bluetoothConnectGetRandomUuid");
                    logicEditor.a(" ", "bluetoothConnectReadyConnection");
                    logicEditor.a(" ", "bluetoothConnectReadyConnectionToUuid");
                    logicEditor.a(" ", "bluetoothConnectStartConnection");
                    logicEditor.a(" ", "bluetoothConnectStartConnectionToUuid");
                    logicEditor.a(" ", "bluetoothConnectStopConnection");
                    logicEditor.a(" ", "bluetoothConnectSendData");
                    logicEditor.a(" ", "bluetoothConnectActivateBluetooth");
                    logicEditor.a(" ", "bluetoothConnectGetPairedDevices");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_LOCATION_MANAGER)) {
                    logicEditor.a("LocationManager", 0xff555555);
                    logicEditor.a(" ", "locationManagerRequestLocationUpdates");
                    logicEditor.a(" ", "locationManagerRemoveUpdates");
                }
                if (extraBlocks.isComponentUsed(ComponentBean.COMPONENT_TYPE_PROGRESS_DIALOG)
                        || extraBlocks.isCustomVarUsed("ProgressDialog")
                        || eventName.equals("onPreExecute") || eventName.equals("onProgressUpdate")
                        || eventName.equals("onPostExecute")) {
                    logicEditor.a("ProgressDialog", 0xff555555);
                    logicEditor.a(" ", "progressdialogCreate");
                    logicEditor.a(" ", "progressdialogSetTitle");
                    logicEditor.a(" ", "progressdialogSetMessage");
                    logicEditor.a(" ", "progressdialogSetMax");
                    logicEditor.a(" ", "progressdialogSetProgress");
                    logicEditor.a(" ", "progressdialogSetCancelable");
                    logicEditor.a(" ", "progressdialogSetCanceledOutside");
                    logicEditor.a(" ", "progressdialogSetStyle");
                    logicEditor.a(" ", "progressdialogShow");
                    logicEditor.a(" ", "progressdialogDismiss");
                    return;
                }
                return;

            case 8:
                logicEditor.b("Create", "blockAdd");
                logicEditor.b("Import From Collection", "blockImport");
                if (ConfigActivity.isSettingEnabled(ConfigActivity.SETTING_SHOW_BUILT_IN_BLOCKS)) {
                    logicEditor.a(" ", "customToast");
                    logicEditor.a(" ", "customToastWithIcon");
                }
                moreBlocks();
                if (ConfigActivity.isSettingEnabled(ConfigActivity.SETTING_SHOW_BUILT_IN_BLOCKS)) {
                    logicEditor.a("Command Blocks", 0xff555555);
                    logicEditor.a("c", "CommandBlockJava");
                    logicEditor.addDeprecatedBlock("Deprecated: Use XML Command Manager", "c", "CommandBlockXML");
                    logicEditor.a("Permission Command Blocks", 0xff555555);
                    logicEditor.a(" ", "addPermission");
                    logicEditor.a(" ", "removePermission");
                    logicEditor.a("Other Command Blocks", 0xff555555);
                    logicEditor.a(" ", "addCustomVariable");
                    logicEditor.a(" ", "addInitializer");
                    return;
                }
                return;

            default:
                int paletteIndex = -1, paletteBlocks = 0;
                ArrayList<HashMap<String, Object>> extraBlockData = ExtraBlockFile.getExtraBlockData();
                for (int i = 0, extraBlockDataSize = extraBlockData.size(); i < extraBlockDataSize; i++) {
                    HashMap<String, Object> map = extraBlockData.get(i);

                    Object palette = map.get("palette");
                    if (palette instanceof String paletteString) {

                        if (paletteString.equals(String.valueOf(paletteId))) {
                            if (paletteIndex == -1) paletteIndex = Integer.parseInt(paletteString);
                            paletteBlocks++;

                            Object type = map.get("type");
                            if (type instanceof String typeString) {

                                if (typeString.equals("h")) {
                                    Object spec = map.get("spec");
                                    if (spec instanceof String specString) {

                                        logicEditor.a(specString, 0xff555555);
                                    } else {
                                        SketchwareUtil.toastError("Custom Block #" + paletteBlocks +
                                                " of current palette has an invalid spec data type");
                                    }
                                } else {
                                    Object name = map.get("name");
                                    if (name instanceof String nameString) {

                                        Object typeName = map.get("typeName");
                                        if (typeName instanceof String typeNameString) {

                                            logicEditor.a("", typeString, typeNameString, nameString);
                                        } else {
                                            logicEditor.a("", typeString, "", nameString);
                                        }
                                    } else {
                                        SketchwareUtil.toastError("Custom Block #" + paletteBlocks +
                                                " of current palette has an invalid name data type");
                                    }
                                }
                            } else {
                                SketchwareUtil.toastError("Custom Block #" + paletteBlocks +
                                        " of current palette has an invalid block type data type");
                            }
                        }
                    } else {
                        SketchwareUtil.toastError("Custom Block #" + paletteBlocks +
                                " of current palette has an invalid block palette data type");
                    }
                }
                break;
        }
    }
}

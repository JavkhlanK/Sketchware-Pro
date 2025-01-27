package a.a.a;

import static dev.aldi.sayuti.block.ExtraBlockFile.getExtraBlockData;

import android.text.TextUtils;
import android.util.Pair;

import com.besome.sketch.beans.BlockBean;
import com.besome.sketch.beans.ComponentBean;
import com.besome.sketch.beans.ProjectFileBean;
import com.besome.sketch.beans.ViewBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import mod.agus.jcoderz.beans.ViewBeans;
import mod.agus.jcoderz.editor.manage.library.locallibrary.ManageLocalLibrary;
import mod.agus.jcoderz.handle.component.ConstVarComponent;
import mod.hey.studios.build.BuildSettings;
import mod.hey.studios.project.ProjectSettings;
import mod.hilal.saif.android_manifest.AndroidManifestInjector;
import mod.hilal.saif.blocks.CommandBlock;
import mod.hilal.saif.events.LogicHandler;
import mod.pranav.viewbinding.ViewBindingBuilder;
import pro.sketchware.control.logic.PermissionManager;

public class Jx {

    public static final String EOL = "\r\n";
    public static final Pattern WIDGET_NAME_PATTERN = Pattern.compile("\\w*\\..*\\.");
    private final ProjectSettings settings;
    private final PermissionManager permissionManager;
    private final String packageName;
    private final ProjectFileBean projectFileBean;
    private final eC projectDataManager;
    private final jq buildConfig;
    private final Ox ox;
    private final Boolean isViewBindingEnabled;
    /**
     * Fields with static initializer that added Components need,
     * e.g. {"private Timer _timer = new Timer();"}
     */
    private final ArrayList<String> fieldsWithStaticInitializers = new ArrayList<>();
    /**
     * Fields of the currently generating class,
     * e.g. {"private FloatingActionBar _fab;"}
     */
    private final ArrayList<String> fields = new ArrayList<>();
    private final ArrayList<String> lists = new ArrayList<>();
    private final ArrayList<String> views = new ArrayList<>();
    /**
     * Field declarations from components. Can include static initializer, but doesn't have to.
     */
    private final ArrayList<String> components = new ArrayList<>();
    /**
     * Statements to be added to initialize(Bundle),
     * e.g. {"_drawer.addDrawerListener(_toggle);"}
     */
    private final ArrayList<String> initializeMethodCode = new ArrayList<>();
    private final ManageLocalLibrary mll;
    /**
     * Component initializer lines which get added to <code>_initialize(Bundle)</code>
     */
    private final ArrayList<String> componentInitializers = new ArrayList<>();
    /**
     * Code of More Blocks that have been created
     */
    private final ArrayList<String> moreBlocks = new ArrayList<>();
    /**
     * Code of inner Adapter classes, used for Widgets like ListView or RecyclerView
     */
    private final ArrayList<String> adapterClasses = new ArrayList<>();
    /**
     * Filled with request code constants for FilePicker components
     */
    private final ArrayList<String> filePickerRequestCodes = new ArrayList<>();
    
    private final ArrayList<HashMap<String, Object>> extraBlocks;
    private Hx eventManager;
    private ArrayList<String> imports = new ArrayList<>();
    private String onCreateEventCode = "";

    public Jx(jq jqVar, ProjectFileBean projectFileBean, eC eCVar) {
        packageName = jqVar.packageName;
        this.projectFileBean = projectFileBean;
        projectDataManager = eCVar;
        buildConfig = jqVar;
        mll = new ManageLocalLibrary(eCVar.a);
        settings = new ProjectSettings(eCVar.a);
        permissionManager = new PermissionManager(eCVar.a, projectFileBean.getJavaName());
        ox = new Ox(buildConfig, projectFileBean);
        extraBlocks = getExtraBlockData();
        isViewBindingEnabled = settings.getValue(ProjectSettings.SETTING_ENABLE_VIEWBINDING, BuildSettings.SETTING_GENERIC_VALUE_FALSE)
                .equals(BuildSettings.SETTING_GENERIC_VALUE_TRUE);
    }

    public String activityResult() {
        ArrayList<BlockBean> blocks = jC.a(projectDataManager.a).a(projectFileBean.getJavaName(), "onActivityResult_onActivityResult");
        return Lx.j(new Fx(projectFileBean.getActivityName(), buildConfig, blocks, isViewBindingEnabled).a(), false);
    }

    public String initializeLogic() {
        ArrayList<BlockBean> blocks = jC.a(projectDataManager.a).a(projectFileBean.getJavaName(), "initializeLogic_initializeLogic");
        return Lx.j(new Fx(projectFileBean.getActivityName(), buildConfig, blocks, isViewBindingEnabled).a(), false);
    }

    private void extraVariables() {
        for (Map.Entry<String, ArrayList<BlockBean>> blocks : jC.a(projectDataManager.a).b(projectFileBean.getJavaName()).entrySet()) {
            for (BlockBean block : blocks.getValue()) {
                switch (block.opCode) {
                    case "addCustomVariable":
                        if (!block.parameters.get(0).trim().isEmpty()) {
                            fields.add(block.parameters.get(0));
                        }
                        break;

                    case "addInitializer":
                        if (!block.parameters.get(0).trim().isEmpty()) {
                            initializeMethodCode.add(block.parameters.get(0));
                        }
                        break;
                }
            }
        }
    }

    private void removeExtraImports() {
        ArrayList<String> newImports = new ArrayList<>();
        for (String value : imports) {
            if (!newImports.contains(value) && !value.trim().isEmpty()) {
                newImports.add(value);
            }
        }
        imports = newImports;
    }

    /**
     * @return Import to be added to the currently generating class
     * (includes import of default launcher activity)
     */
    private String getLauncherActivity(String packageName) {
        String theImport = "";

        String activityName = ProjectFileBean.getActivityName(AndroidManifestInjector.getLauncherActivity(projectDataManager.a));
        if (!activityName.equals("MainActivity")) {
            theImport = "import " + packageName + "." + activityName + ";" + EOL;
        }

        return theImport;
    }

    private String getBillingResponseCode(ConstVarComponent component) {
        HashMap<String, ArrayList<String>> param = component.param;
        if (param == null || !param.containsKey("OnResultBillingResponse")) {
            return "";
        }

        ArrayList<String> arrayList = param.get("OnResultBillingResponse");
        return "if (!" + arrayList.get(0) + ".handleActivityResult(_requestCode, _resultCode, _data))";
    }

    /**
     * @return Generated Java code of the current View (not Widget)
     */
    public String generateCode() {
        boolean isDialogFragment = projectFileBean.fileName.contains("_dialog_fragment");
        boolean isBottomDialogFragment = projectFileBean.fileName.contains("_bottomdialog_fragment");
        boolean isFragment = projectFileBean.fileName.contains("_fragment");

        extraVariables();
        handleAppCompat();
        addFieldsDeclaration();
        addDrawerComponentInitializer();
        initializeEventsCodeGenerator();
        addMoreBlockCodes();
        addAdapterCode();
        addRequestCodeConstants();
        addImportsForBlocks();
        addLocalLibraryImports();

        StringBuilder sb = new StringBuilder(8192);
        sb.append("package ").append(packageName).append(";").append(EOL)
                .append(EOL);
        if (projectFileBean.getActivityName().equals("MainActivity")) {
            sb.append(getLauncherActivity(packageName));
        }

        if (buildConfig.isFirebaseEnabled) addImport("com.google.firebase.FirebaseApp");

        if (buildConfig.isAdMobEnabled) {
            addImport("com.google.android.gms.ads.MobileAds");

            if (buildConfig.isDebugBuild)
                addImport("com.google.android.gms.ads.RequestConfiguration");
        }

        if (buildConfig.g) {
            addImport("androidx.fragment.app.Fragment");
            addImport("androidx.fragment.app.FragmentManager");
            addImport("androidx.fragment.app.DialogFragment");
            if (isBottomDialogFragment) {
                addImport("com.google.android.material.bottomsheet.BottomSheetDialogFragment");
            }
            addImport("com.google.android.material.color.MaterialColors");
        } else {
            addImport("android.app.Fragment");
            addImport("android.app.FragmentManager");
            addImport("android.app.DialogFragment");
        }
        if (permissionManager.hasNewPermission() || buildConfig.a(projectFileBean.getActivityName()).a()) {
            if (buildConfig.g) {
                addImport("androidx.core.content.ContextCompat");
                addImport("androidx.core.app.ActivityCompat");
            }
            addImport("android.Manifest");
            addImport("android.content.pm.PackageManager");
        }

        removeExtraImports();
        Collections.sort(imports);
        for (String anImport : imports) {
            sb.append("import ").append(anImport).append(";").append(EOL);
        }

        String importsAddedByImportBlocks = LogicHandler.imports(eventManager.generateActivityLifecycleEventCode());
        if (!importsAddedByImportBlocks.isEmpty()) {
            sb.append(importsAddedByImportBlocks).append(EOL);
        }
        sb.append(EOL);

        sb.append("public class ").append(projectFileBean.getActivityName()).append(" extends ");
        if (buildConfig.g) {
            if (isBottomDialogFragment) {
                sb.append("BottomSheetDialogFragment");
            } else if (isDialogFragment) {
                sb.append("DialogFragment");
            } else if (isFragment) {
                sb.append("Fragment");
            } else {
                sb.append("AppCompatActivity");
            }
        } else {
            if (isBottomDialogFragment) {
                sb.append("/* Enable AppCompat to use it */");
            } else if (isDialogFragment) {
                sb.append("DialogFragment");
            } else if (isFragment) {
                sb.append("Fragment");
            } else {
                sb.append("Activity");
            }
        }
        sb.append(" {").append(EOL);

        boolean activityHasFields = false;

        for (String constant : filePickerRequestCodes) {
            if (!constant.isEmpty()) {
                activityHasFields = true;
                sb.append(EOL);
                sb.append(constant);
            }
        }

        if (!fieldsWithStaticInitializers.isEmpty()) {
            if (activityHasFields) sb.append(EOL);
            activityHasFields = true;

            for (String componentFieldDeclaration : fieldsWithStaticInitializers) {
                if (!componentFieldDeclaration.isEmpty()) {
                    sb.append(EOL);
                    sb.append(componentFieldDeclaration);
                }
            }
        }

        if (!fields.isEmpty()) {
            if (activityHasFields) sb.append(EOL);
            activityHasFields = true;

            for (String field : fields) {
                if (!field.isEmpty()) {
                    sb.append(EOL);
                    sb.append(field);
                }
            }
        }

        if (!lists.isEmpty()) {
            if (activityHasFields) sb.append(EOL);
            activityHasFields = true;

            for (String value : lists) {
                if (!value.isEmpty()) {
                    sb.append(EOL);
                    sb.append(value);
                }
            }
        }

        if (!views.isEmpty()) {
            if (activityHasFields) sb.append(EOL);
            activityHasFields = true;

            for (String viewDeclaration : views) {
                if (!viewDeclaration.isEmpty()) {
                    sb.append(EOL);
                    sb.append(viewDeclaration);
                }
            }
        }

        if (!components.isEmpty()) {
            if (activityHasFields) sb.append(EOL);
            activityHasFields = true;

            for (String componentFieldDeclaration : components) {
                if (!componentFieldDeclaration.isEmpty()) {
                    sb.append(EOL);
                    sb.append(componentFieldDeclaration);
                }
            }
        }

        if (activityHasFields) sb.append(EOL);

        sb.append(EOL);
        String bindingName = ViewBindingBuilder.generateFileNameForLayout(projectFileBean.fileName);
        if (isFragment) {
            if (buildConfig.g) {
                sb.append("@NonNull").append(EOL);
                sb.append("@Override").append(EOL);
                sb.append("public View onCreateView(@NonNull LayoutInflater _inflater, " +
                        "@Nullable ViewGroup _container, @Nullable Bundle _savedInstanceState) {").append(EOL);
            } else {
                sb.append("@Override").append(EOL);
                sb.append("public View onCreateView(LayoutInflater _inflater, ViewGroup _container, " +
                        "Bundle _savedInstanceState) {").append(EOL);
            }
            if (isViewBindingEnabled) {
                sb.append("binding = ").append(bindingName).append(".inflate(_inflater, _container, false);").append(EOL);
                sb.append("initialize(_savedInstanceState, binding.getRoot());");
            } else {
                sb.append("View _view = _inflater.inflate(R.layout.").append(projectFileBean.fileName).append(", _container, false);").append(EOL);
                sb.append("initialize(_savedInstanceState, _view);");
            }
        } else {
            sb.append("@Override").append(EOL);
            sb.append("protected void onCreate(Bundle _savedInstanceState) {").append(EOL);
            sb.append("super.onCreate(_savedInstanceState);").append(EOL);

            if (isViewBindingEnabled) {
                sb.append("binding = ").append(bindingName).append(".inflate(getLayoutInflater());").append(EOL);
                sb.append("setContentView(binding.getRoot());").append(EOL);
            } else {
                sb.append("setContentView(R.layout.").append(projectFileBean.fileName).append(");").append(EOL);
            }
            sb.append("initialize(_savedInstanceState);");
        }
        sb.append(EOL);
        if (buildConfig.isFirebaseEnabled) {
            if (isFragment) {
                sb.append("FirebaseApp.initializeApp(getContext());");
            } else {
                sb.append("FirebaseApp.initializeApp(this);");
            }
            sb.append(EOL);
        }

        if (buildConfig.isAdMobEnabled && !isFragment) {
            if (!buildConfig.isFirebaseEnabled) {
                sb.append(EOL);
            }
            sb.append("MobileAds.initialize(this);");
            sb.append(EOL);
            if (fieldsWithStaticInitializers.contains(Lx.getComponentFieldCode("InterstitialAd"))) {
                sb.append("_ad_unit_id = \"").append(buildConfig.isDebugBuild ? "ca-app-pub-3940256099942544/1033173712" : buildConfig.interstitialAdUnitId).append("\";");
            }
            if (fieldsWithStaticInitializers.contains(Lx.getComponentFieldCode("RewardedVideoAd"))) {
                sb.append("_reward_ad_unit_id = \"").append(buildConfig.isDebugBuild ? "ca-app-pub-3940256099942544/5224354917" : buildConfig.rewardAdUnitId).append("\";");
            }

            if (buildConfig.isDebugBuild) {
                StringBuilder testDevicesListCode = new StringBuilder("List<String> testDeviceIds = Arrays.asList(");
                ArrayList<String> testDevices = buildConfig.t;
                for (int j = 0, testDevicesSize = testDevices.size(); j < testDevicesSize; j++) {
                    String testDeviceId = testDevices.get(j);

                    testDevicesListCode.append("\"").append(testDeviceId).append("\"");
                    if (j != testDevicesSize - 1) {
                        testDevicesListCode.append(", ");
                    }
                }
                testDevicesListCode.append(");").append(EOL);

                sb.append(EOL);
                sb.append(testDevicesListCode);
                sb.append("MobileAds.setRequestConfiguration(new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build());");
            }

            sb.append(EOL);
        }

        if (!isFragment) {
            // Adds initializeLogic() call too, don't worry
            sb.append(permissionManager.writePermission(buildConfig.g, buildConfig.a(projectFileBean.getActivityName()).c));
        } else {
            sb.append("initializeLogic();").append(EOL);
            if (isViewBindingEnabled) {
                sb.append("return binding.getRoot();").append(EOL);
            } else {
                sb.append("return _view;").append(EOL);
            }
        }
        sb.append("}").append(EOL);

        if (permissionManager.hasPermission && !isFragment) {
            sb.append(EOL);
            sb.append("@Override").append(EOL);
            sb.append("public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {").append(EOL);
            sb.append("super.onRequestPermissionsResult(requestCode, permissions, grantResults);").append(EOL);
            sb.append("if (requestCode == 1000) {").append(EOL);
            sb.append("initializeLogic();").append(EOL);
            sb.append("}").append(EOL);
            sb.append("}").append(EOL);
        }
        sb.append(EOL);

        if (isFragment) {
            sb.append("private void initialize(Bundle _savedInstanceState, View _view) {");
        } else {
            sb.append("private void initialize(Bundle _savedInstanceState) {");
        }
        if (!TextUtils.isEmpty(initializeLogic())) {
            sb.append(EOL);
            sb.append(initializeLogic());
        }

        for (String value : initializeMethodCode) {
            if (!value.isEmpty()) {
                sb.append(EOL);
                sb.append(value);
            }
        }

        for (String componentInitializer : componentInitializers) {
            if (!componentInitializer.isEmpty()) {
                sb.append(EOL);
                sb.append(componentInitializer);
            }
        }

        String hxG = eventManager.generateViewEvents();
        if (!hxG.isEmpty()) {
            sb.append(EOL);
            sb.append(EOL);
            sb.append(hxG);
        }

        String hxC = eventManager.generateComponentEvents();
        if (!hxC.isEmpty()) {
            sb.append(EOL);
            sb.append(EOL);
            sb.append(hxC);
        }

        String hxD = eventManager.generateDrawerEvents();
        if (!hxD.isEmpty()) {
            sb.append(EOL);
            sb.append(EOL);
            sb.append(hxD);
        }

        String hxF = eventManager.generateAuthEvents();
        if (!hxF.isEmpty()) {
            sb.append(EOL);
            sb.append(EOL);
            sb.append(hxF);
        }

        sb.append(EOL);
        sb.append("}").append(EOL);
        sb.append(EOL);
        sb.append("private void initializeLogic() {").append(EOL);
        if (!onCreateEventCode.isEmpty()) {
            sb.append(onCreateEventCode).append(EOL);
        }
        sb.append("}").append(EOL);

        String agusComponentsOnActivityResultCode = getBillingResponseCode(buildConfig.x);
        String onActivityResultLogic = activityResult();
        String onActivityResultSwitchLogic = eventManager.getOnActivityResultSwitchCases();
        if (!agusComponentsOnActivityResultCode.isEmpty() || !onActivityResultLogic.isEmpty() || !onActivityResultSwitchLogic.isEmpty()) {
            sb.append(EOL);
            sb.append("@Override").append(EOL);
            if (isFragment) {
                sb.append("public");
            } else {
                sb.append("protected");
            }
            sb.append(" void onActivityResult(int _requestCode, int _resultCode, Intent _data) {").append(EOL);
            sb.append(agusComponentsOnActivityResultCode);
            sb.append("super.onActivityResult(_requestCode, _resultCode, _data);").append(EOL);
            sb.append(onActivityResultLogic).append(EOL);
            sb.append("switch (_requestCode) {").append(EOL);
            sb.append(onActivityResultSwitchLogic).append(EOL);
            sb.append("default:").append(EOL);
            sb.append("break;").append(EOL);
            sb.append("}").append(EOL);
            sb.append("}").append(EOL);
        }

        if (projectFileBean.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_DRAWER) && !isViewBindingEnabled) {
            eventManager.addLifecycleEvent("onBackPressed", "DrawerLayout", "_drawer");
        }

        ArrayList<ViewBean> beans = projectDataManager.d(projectFileBean.getXmlName());
        for (ViewBean next : beans) {
            if (next.type == ViewBean.VIEW_TYPE_WIDGET_MAPVIEW) {
                eventManager.addLifecycleEvent("onStart", "MapView", next.id);
                eventManager.addLifecycleEvent("onResume", "MapView", next.id);
                eventManager.addLifecycleEvent("onPause", "MapView", next.id);
                eventManager.addLifecycleEvent("onStop", "MapView", next.id);
                eventManager.addLifecycleEvent("onDestroy", "MapView", next.id);
            }
            if (next.type == ViewBean.VIEW_TYPE_WIDGET_ADVIEW) {
                eventManager.addLifecycleEvent("onResume", "AdView", next.id);
                eventManager.addLifecycleEvent("onPause", "AdView", next.id);
                eventManager.addLifecycleEvent("onDestroy", "AdView", next.id);
            }
        }
        if (!eventManager.k.isEmpty()) {
            sb.append(EOL);
            sb.append(eventManager.k).append(EOL);
        }
        if (!eventManager.l.isEmpty()) {
            sb.append(EOL);
            sb.append(eventManager.l);
            sb.append(EOL);
        }

        String base = LogicHandler.base(eventManager.generateActivityLifecycleEventCode());
        if (!base.isEmpty()) {
            sb.append(EOL);
            sb.append(base);
        }

        for (String moreBlocksCode : moreBlocks) {
            sb.append(EOL);
            sb.append(moreBlocksCode).append(EOL);
        }

        sb.append(EOL);
        for (int j = 0, qSize = adapterClasses.size(); j < qSize; j++) {
            String adapterCode = adapterClasses.get(j);

            if (base.contains("public CharSequence onTabLayoutNewTabAdded(int _position) {")
                    || !adapterCode.contains("return onTabLayoutNewTabAdded(pos);")) {
                sb.append(adapterCode);
            } else {
                sb.append(adapterCode.replace("return onTabLayoutNewTabAdded(pos);",
                        "// Use the Activity Event (onTabLayoutNewTabAdded) in order to use this method" + EOL +
                                "return \"page \" + String.valueOf(pos);"));
            }
            if (j != qSize - 1) {
                sb.append(EOL);
            }
        }
        if (buildConfig.g) {
            String rootView;
            if (isViewBindingEnabled) {
                rootView = "binding.getRoot()";
            } else if (isFragment || isDialogFragment || isBottomDialogFragment) {
                rootView = "getView()";
            } else {
                rootView = "getWindow().getDecorView().getRootView()";
            }
            sb.append(String.format("""
                
                private int getMaterialColor(int resourceId) {
                    return MaterialColors.getColor(%s, resourceId);
                }
                """, rootView));
        }
        if (!isFragment && !settings.getValue(ProjectSettings.SETTING_DISABLE_OLD_METHODS, BuildSettings.SETTING_GENERIC_VALUE_FALSE)
                .equals(BuildSettings.SETTING_GENERIC_VALUE_TRUE)) {
            sb.append(getDeprecatedMethodsCode());
        }
        sb.append("}").append(EOL);
        String code = sb.toString();

        if (isFragment) {
            code = code.replaceAll("getApplicationContext\\(\\)", "getContext().getApplicationContext()")
                    .replaceAll("getBaseContext\\(\\)", "getActivity().getBaseContext()")
                    .replaceAll("\\(ClipboardManager\\) getSystemService", "(ClipboardManager) getContext().getSystemService")
                    .replaceAll("\\(Vibrator\\) getSystemService", "(Vibrator) getContext().getSystemService")
                    .replaceAll("\\(SensorManager\\) getSystemService", "(SensorManager) getContext().getSystemService")
                    .replaceAll("Typeface.createFromAsset\\(getAssets\\(\\)", "Typeface.createFromAsset(getContext().getAssets()")
                    .replaceAll("= getAssets\\(\\).open", "= getContext().getAssets().open")
                    .replaceAll("getSharedPreferences", "getContext().getSharedPreferences")
                    .replaceAll("AlertDialog.Builder\\(this\\);", "AlertDialog.Builder(getActivity());")
                    .replaceAll("SpeechRecognizer.createSpeechRecognizer\\(this\\);", "SpeechRecognizer.createSpeechRecognizer(getContext());")
                    .replaceAll("new RequestNetwork\\(this\\);", "new RequestNetwork((Activity) getContext());")
                    .replaceAll("new BluetoothConnect\\(this\\);", "new BluetoothConnect((Activity) getContext());")
                    .replaceAll("MobileAds.getRewardedVideoAdInstance\\(this\\);", "MobileAds.getRewardedVideoAdInstance(getContext());")
                    .replaceAll("runOnUiThread\\(new", "getActivity().runOnUiThread(new")
                    .replaceAll(".setLayoutManager\\(new LinearLayoutManager\\(this", ".setLayoutManager(new LinearLayoutManager(getContext()")
                    .replaceAll("getLayoutInflater\\(\\)", "getActivity().getLayoutInflater()")
                    .replaceAll("getSupportFragmentManager\\(\\)", "getActivity().getSupportFragmentManager()");
        } else if (buildConfig.g) {
            code = code.replaceAll("getFragmentManager", "getSupportFragmentManager");
        }

        return CommandBlock.CB(Lx.j(code, false));
    }

    private String getListDeclarationAndAddImports(int listType, String listName) {
        String typeName = mq.b(listType);
        addImports(mq.getImportsByTypeName(typeName, null));
        return Lx.a(typeName, listName, Lx.AccessModifier.PRIVATE);
    }

    private String getComponentDeclarationAndAddImports(ComponentBean componentBean) {
        String typeName = mq.a(componentBean.type);
        addImports(mq.getImportsByTypeName(typeName, null));
        return Lx.a(typeName, componentBean.componentId, Lx.AccessModifier.PRIVATE, componentBean.param1, componentBean.param2, componentBean.param3);
    }

    private String getDrawerViewDeclarationAndAddImports(ViewBean viewBean) {
        String viewType = WIDGET_NAME_PATTERN.matcher(viewBean.convert).replaceAll("");
        if (viewType.isEmpty()) {
            viewType = viewBean.getClassInfo().a();
        }
        addImports(mq.getImportsByTypeName(viewType, null));
        return Lx.a(viewType, "_drawer_" + viewBean.id, Lx.AccessModifier.PRIVATE);
    }

    /**
     * @return Definition line for a Variable
     */
    private String getVariableDeclarationAndAddImports(int variableType, String name) {
        String variableTypeName = mq.c(variableType);
        addImports(mq.getImportsByTypeName(variableTypeName, null));
        return Lx.a(variableTypeName, name, Lx.AccessModifier.PRIVATE);
    }

    private String getViewDeclarationAndAddImports(ViewBean viewBean) {
        String viewType = WIDGET_NAME_PATTERN.matcher(viewBean.convert).replaceAll("");
        if (viewType.isEmpty()) {
            viewType = viewBean.getClassInfo().a();
        }
        addImports(mq.getImportsByTypeName(viewType, viewBean.convert));
        return Lx.a(viewType, viewBean.id, Lx.AccessModifier.PRIVATE);
    }

    private String getDeprecatedMethodsCode() {
        return EOL +
                "@Deprecated" + EOL +
                "public void showMessage(String _s) {" + EOL +
                "Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();" + EOL +
                "}" + EOL +
                EOL +
                "@Deprecated" + EOL +
                "public int getLocationX(View _v) {" + EOL +
                "int _location[] = new int[2];" + EOL +
                "_v.getLocationInWindow(_location);" + EOL +
                "return _location[0];" + EOL +
                "}" + EOL +
                EOL +
                "@Deprecated" + EOL +
                "public int getLocationY(View _v) {" + EOL +
                "int _location[] = new int[2];" + EOL +
                "_v.getLocationInWindow(_location);" + EOL +
                "return _location[1];" + EOL +
                "}" + EOL +
                EOL +
                "@Deprecated" + EOL +
                "public int getRandom(int _min, int _max) {" + EOL +
                "Random random = new Random();" + EOL +
                "return random.nextInt(_max - _min + 1) + _min;" + EOL +
                "}" + EOL +
                EOL +
                "@Deprecated" + EOL +
                "public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {" + EOL +
                "ArrayList<Double> _result = new ArrayList<Double>();" + EOL +
                "SparseBooleanArray _arr = _list.getCheckedItemPositions();" + EOL +
                "for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {" + EOL +
                "if (_arr.valueAt(_iIdx))" + EOL +
                "_result.add((double)_arr.keyAt(_iIdx));" + EOL +
                "}" + EOL +
                "return _result;" + EOL +
                "}" + EOL +
                EOL +
                "@Deprecated" + EOL +
                "public float getDip(int _input) {" + EOL +
                "return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());" + EOL +
                "}" + EOL +
                EOL +
                "@Deprecated" + EOL +
                "public int getDisplayWidthPixels() {" + EOL +
                "return getResources().getDisplayMetrics().widthPixels;" + EOL +
                "}" + EOL +
                EOL +
                "@Deprecated" + EOL +
                "public int getDisplayHeightPixels() {" + EOL +
                "return getResources().getDisplayMetrics().heightPixels;" + EOL +
                "}" + EOL;
    }

    private void addImport(String classToImport) {
        if (!imports.contains(classToImport)) {
            imports.add(classToImport);
        }
    }

    private void addImports(ArrayList<String> imports) {
        if (imports != null) {
            for (String value : imports) {
                addImport(value);
            }
        }
    }

    /**
     * @see Lx#getComponentInitializerCode(String, String, String...)
     */
    private String getComponentBeanInitializer(ComponentBean componentBean) {
        return Lx.getComponentInitializerCode(mq.a(componentBean.type), componentBean.componentId, componentBean.param1, componentBean.param2, componentBean.param3);
    }

    private void handleAppCompat() {
        if (buildConfig.g) {
            addImport("androidx.appcompat.app.AppCompatActivity");
            addImport("androidx.annotation.*");
        } else {
            addImport("android.app.Activity");
        }
        if (isViewBindingEnabled) {
            fields.add("private " + ViewBindingBuilder.generateFileNameForLayout(projectFileBean.fileName) + " binding;");
        }

        if (buildConfig.g) {
            if (projectFileBean.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_TOOLBAR) && !projectFileBean.fileName.contains("_fragment")) {
                addImport("androidx.appcompat.widget.Toolbar");
                addImport("androidx.coordinatorlayout.widget.CoordinatorLayout");
                addImport("com.google.android.material.appbar.AppBarLayout");

                if (isViewBindingEnabled) {
                    initializeMethodCode.add(
                            "setSupportActionBar(binding.Toolbar);" + EOL +
                                    "getSupportActionBar().setDisplayHomeAsUpEnabled(true);" + EOL +
                                    "getSupportActionBar().setHomeButtonEnabled(true);" + EOL +
                                    "binding.Toolbar.setNavigationOnClickListener(new View.OnClickListener() {" + EOL +
                                    "@Override" + EOL +
                                    "public void onClick(View _v) {" + EOL +
                                    "onBackPressed();" + EOL +
                                    "}" + EOL +
                                    "});"
                    );
                } else {
                    fields.add("private Toolbar _toolbar;");
                    fields.add("private AppBarLayout _app_bar;");
                    fields.add("private CoordinatorLayout _coordinator;");

                    initializeMethodCode.add(
                            "_app_bar = findViewById(R.id._app_bar);" + EOL +
                                    "_coordinator = findViewById(R.id._coordinator);" + EOL +
                                    "_toolbar = findViewById(R.id._toolbar);" + EOL +
                                    "setSupportActionBar(_toolbar);" + EOL +
                                    "getSupportActionBar().setDisplayHomeAsUpEnabled(true);" + EOL +
                                    "getSupportActionBar().setHomeButtonEnabled(true);" + EOL +
                                    "_toolbar.setNavigationOnClickListener(new View.OnClickListener() {" + EOL +
                                    "@Override" + EOL +
                                    "public void onClick(View _v) {" + EOL +
                                    "onBackPressed();" + EOL +
                                    "}" + EOL +
                                    "});"
                    );
                }
            }
            if (projectFileBean.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_FAB)) {
                addImport("com.google.android.material.floatingactionbutton.FloatingActionButton");

                if (!isViewBindingEnabled) {
                    fields.add("private FloatingActionButton _fab;");
                    initializeMethodCode.add("_fab = " +
                            (projectFileBean.fileName.contains("_fragment") ? "_view." : "") +
                            "findViewById(R.id._fab);");
                }
            }
            if (projectFileBean.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_DRAWER) && !projectFileBean.fileName.contains("_fragment")) {
                addImport("androidx.core.view.GravityCompat");
                addImport("androidx.drawerlayout.widget.DrawerLayout");
                addImport("androidx.appcompat.app.ActionBarDrawerToggle");


                if (isViewBindingEnabled) {
                    initializeMethodCode.add(
                            "ActionBarDrawerToggle _toggle = new ActionBarDrawerToggle(" +
                                    projectFileBean.getActivityName() + ".this, binding.Drawer, " +

                                    (projectFileBean.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_TOOLBAR) ?
                                            "binding.Toolbar, " : "") +

                                    "R.string.app_name, R.string.app_name);" + EOL +
                                    "binding.Drawer.addDrawerListener(_toggle);" + EOL +
                                    "_toggle.syncState();" + EOL
                    );
                } else {
                    fields.add("private DrawerLayout _drawer;");
                    initializeMethodCode.add("_drawer = findViewById(R.id._drawer);" + EOL +
                            "ActionBarDrawerToggle _toggle = new ActionBarDrawerToggle(" +
                            projectFileBean.getActivityName() + ".this, _drawer, " +
                            (projectFileBean.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_TOOLBAR) ?
                                    "_toolbar, " : "") +
                            "R.string.app_name, R.string.app_name);" + EOL +
                            "_drawer.addDrawerListener(_toggle);" + EOL +
                            "_toggle.syncState();" + EOL + EOL +
                            "LinearLayout _nav_view = findViewById(R.id._nav_view);" + EOL
                    );
                }
                addImports(mq.getImportsByTypeName("LinearLayout", null));
            }
        }
        addImport("android.app.*");
        addImport("android.os.*");
        addImport("android.view.*");
        addImport("android.view.View.*");
        addImport("android.widget.*");
        addImport("android.content.*");
        addImport("android.content.res.*");
        addImport("android.graphics.*");
        addImport("android.graphics.drawable.*");
        addImport("android.media.*");
        addImport("android.net.*");
        addImport("android.text.*");
        addImport("android.text.style.*");
        addImport("android.util.*");
        addImport("android.webkit.*");
        addImport("android.animation.*");
        addImport("android.view.animation.*");
        addImport("java.io.*");
        addImport("java.util.*");
        addImport("java.util.regex.*");
        addImport("java.text.*");
        addImport("org.json.*");
        onCreateEventCode = new Fx(projectFileBean.getActivityName(), buildConfig, projectDataManager.a(projectFileBean.getJavaName(), "onCreate_initializeLogic"), isViewBindingEnabled).a();
    }

    private String getDrawerViewInitializer(ViewBean viewBean) {
        String replaceAll = WIDGET_NAME_PATTERN.matcher(viewBean.convert).replaceAll("");
        if (replaceAll.isEmpty()) {
            replaceAll = viewBean.getClassInfo().a();
        }
        return Lx.getDrawerViewInitializer(replaceAll, viewBean.id, "_nav_view");
    }

    private void addAdapterCode() {
        for (ViewBean viewBean : projectDataManager.f(projectFileBean.getXmlName())) {
            String xmlName = ProjectFileBean.getXmlName(viewBean.customView);
            projectFileBean.getJavaName();
            String eventName = viewBean.id + "_onBindCustomView";
            String adapterLogic = new Fx(projectFileBean.getActivityName(), buildConfig, projectDataManager.a(projectFileBean.getJavaName(), eventName), isViewBindingEnabled).a();
            String adapterCode;
            if (viewBean.type == ViewBeans.VIEW_TYPE_LAYOUT_VIEWPAGER) {
                adapterCode = Lx.pagerAdapter(ox, viewBean.id, viewBean.customView, projectDataManager.d(xmlName), adapterLogic, isViewBindingEnabled);
            } else if (viewBean.type == ViewBeans.VIEW_TYPE_WIDGET_RECYCLERVIEW) {
                adapterCode = Lx.recyclerViewAdapter(ox, viewBean.id, viewBean.customView, projectDataManager.d(xmlName), adapterLogic, isViewBindingEnabled);
                addImport("androidx.recyclerview.widget.LinearLayoutManager");
                addImport("androidx.recyclerview.widget.RecyclerView");
            } else {
                adapterCode = Lx.getListAdapterCode(ox, viewBean.id, viewBean.customView, projectDataManager.d(xmlName), adapterLogic, isViewBindingEnabled);
            }
            adapterClasses.add(adapterCode);
        }
    }

    private String getViewInitializer(ViewBean viewBean) {
        String replaceAll = WIDGET_NAME_PATTERN.matcher(viewBean.convert).replaceAll("");
        if (replaceAll.isEmpty()) {
            replaceAll = viewBean.getClassInfo().a();
        }
        if (projectFileBean.fileName.contains("_fragment")) {
            return Lx.getViewInitializer(replaceAll, viewBean.id, true, isViewBindingEnabled);
        }
        return Lx.getViewInitializer(replaceAll, viewBean.id, false, isViewBindingEnabled);
    }

    private void addMoreBlockCodes() {
        String javaName = projectFileBean.getJavaName();
        ArrayList<Pair<String, String>> pairs = projectDataManager.i(javaName);
        for (int index = 0, pairsSize = pairs.size(); index < pairsSize; index++) {
            Pair<String, String> next = pairs.get(index);
            String name = next.first + "_moreBlock";
            String code = Lx.getMoreBlockCode(next.first, next.second, new Fx(projectFileBean.getActivityName(), buildConfig, projectDataManager.a(javaName, name), isViewBindingEnabled).a());
            if (index < (pairsSize - 1)) {
                moreBlocks.add(code);
            } else {
                // Removes unnecessary newline at end of More Block code
                moreBlocks.add(code.substring(0, code.length() - 2));
            }
        }
    }

    private void initializeEventsCodeGenerator() {
        eventManager = new Hx(buildConfig, projectFileBean, projectDataManager);
        addImports(eventManager.getImports());
    }

    /**
     * Adds imports for blocks used in the currently generated Activity.
     */
    private void addImportsForBlocks() {
        for (Map.Entry<String, ArrayList<BlockBean>> entry : projectDataManager.b(projectFileBean.getJavaName()).entrySet()) {
            for (BlockBean blockBean : entry.getValue()) {
                switch (blockBean.opCode) {
                    case "toStringWithDecimal":
                    case "toStringFormat":
                        addImport("java.text.DecimalFormat");
                        break;

                    case "strToListMap":
                    case "strToListStr":
                    case "strToMap":
                    case "GsonStringToListString":
                    case "GsonStringToListNumber":
                        addImport("com.google.gson.Gson");
                        addImport("com.google.gson.reflect.TypeToken");
                        break;

                    case "mapToStr":
                    case "listMapToStr":
                    case "GsonListTojsonString":
                        addImport("com.google.gson.Gson");
                        break;

                    case "setTypeface":
                        addImport("android.graphics.Typeface");
                        break;

                    case "copyToClipboard":
                        addImport("android.content.ClipData");
                        addImport("android.content.ClipboardManager");
                        break;

                    case "fileutilGetLastSegmentPath":
                        addImport("android.net.Uri");
                        break;

                    case "setImageUrl":
                        addImport("com.bumptech.glide.Glide");
                        break;

                    case "interstitialAdLoad":
                    case "rewardedVideoAdLoad":
                        addImport("com.google.android.gms.ads.AdRequest");
                        addImport("com.google.android.gms.ads.LoadAdError");
                        break;
                    default:
                        var block = getExtraBlockByName(blockBean.opCode);
                        if (block != null && block.containsKey("imports")) {
                            var imports = block.get("imports").toString().split("\n");
                            for (String importCode : imports) {
                                addImport(importCode);
                            }
                        }
                        break;
                }
            }
        }
    }
    
    private Map<String, Object> getExtraBlockByName(String name) {
        for (Map<String, Object> block : extraBlocks) {
            if (block.containsKey("name") && block.get("name").toString().equals(name)) {
                return block;
            }
        }
        return null;
    }

    /**
     * Handles the Activity's Drawer Views and Components
     */
    private void addDrawerComponentInitializer() {
        ArrayList<ViewBean> viewBeans = projectDataManager.d(projectFileBean.getXmlName());
        if (!isViewBindingEnabled) {
            for (ViewBean viewBean : viewBeans) {
                if (!viewBean.convert.equals("include")) {
                    Set<String> toNotAdd = ox.readAttributesToReplace(viewBean);
                    if (!toNotAdd.contains("android:id")) {
                        initializeMethodCode.add(getViewInitializer(viewBean));
                    }
                }
            }
            if (projectFileBean.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_DRAWER)) {
                ArrayList<ViewBean> drawerBeans = projectDataManager.d(projectFileBean.getDrawerXmlName());
                for (ViewBean viewBean : drawerBeans) {
                    if (!viewBean.convert.equals("include")) {
                        Set<String> toNotAdd = ox.readAttributesToReplace(viewBean);
                        if (!toNotAdd.contains("android:id")) {
                            initializeMethodCode.add(getDrawerViewInitializer(viewBean));
                        }
                    }
                }
            }
        }
        ArrayList<ComponentBean> componentBeans = projectDataManager.e(projectFileBean.getJavaName());
        for (ComponentBean componentBean : componentBeans) {
            componentInitializers.add(getComponentBeanInitializer(componentBean));
        }
    }

    /**
     * Handles the file's request code constants.
     */
    private void addRequestCodeConstants() {
        int startValue = 100;
        for (ComponentBean next : projectDataManager.e(projectFileBean.getJavaName())) {
            switch (next.type) {
                case ComponentBean.COMPONENT_TYPE_CAMERA:
                case ComponentBean.COMPONENT_TYPE_FILE_PICKER:
                case 31:
                    int incrementedValue = startValue + 1;
                    filePickerRequestCodes.add(Lx.getRequestCodeConstant(next.componentId, incrementedValue));
                    startValue = incrementedValue;
                    break;
            }
        }
    }

    private void addFieldsDeclaration() {
        String javaName = projectFileBean.getJavaName();
        for (Pair<Integer, String> next : projectDataManager.k(javaName)) {
            int variableId = next.first;
            String variableValue = next.second;
            if (variableId == 9) {
                addImport(variableValue);
            } else if (variableId == 6) {
                fields.add(variableValue + ";");
            } else {
                fields.add(getVariableDeclarationAndAddImports(variableId, variableValue));
            }
        }
        for (Pair<Integer, String> next2 : projectDataManager.j(javaName)) {
            lists.add(getListDeclarationAndAddImports(next2.first, next2.second));
        }
        if (!isViewBindingEnabled) {
            for (ViewBean viewBean : projectDataManager.d(projectFileBean.getXmlName())) {
                if (!viewBean.convert.equals("include")) {
                    Set<String> toNotAdd = ox.readAttributesToReplace(viewBean);
                    if (!toNotAdd.contains("android:id")) {
                        views.add(getViewDeclarationAndAddImports(viewBean));
                    }
                }
            }

            if (projectFileBean.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_DRAWER)) {
                for (ViewBean viewBean : projectDataManager.d(projectFileBean.getDrawerXmlName())) {
                    if (!viewBean.convert.equals("include")) {
                        Set<String> toNotAdd = ox.readAttributesToReplace(viewBean);
                        if (!toNotAdd.contains("android:id")) {
                            views.add(getDrawerViewDeclarationAndAddImports(viewBean));
                        }
                    }
                }
            }
        }
        ArrayList<ComponentBean> componentBeans = projectDataManager.e(javaName);
        for (ComponentBean bean : componentBeans) {
            components.add(getComponentDeclarationAndAddImports(bean));
        }

        boolean hasTimer = false;
        boolean hasFirebaseDB = false;
        boolean hasFirebaseStorage = false;
        boolean hasInterstitialAd = false;
        boolean hasRewardedVideoAd = false;
        for (ComponentBean bean : componentBeans) {
            switch (bean.type) {
                case ComponentBean.COMPONENT_TYPE_TIMERTASK:
                    hasTimer = true;
                    break;

                case ComponentBean.COMPONENT_TYPE_FIREBASE:
                    hasFirebaseDB = true;
                    break;

                case ComponentBean.COMPONENT_TYPE_FIREBASE_STORAGE:
                    hasFirebaseStorage = true;
                    break;

                case ComponentBean.COMPONENT_TYPE_INTERSTITIAL_AD:
                    hasInterstitialAd = true;
                    break;

                case ComponentBean.COMPONENT_TYPE_REWARDED_VIDEO_AD:
                    hasRewardedVideoAd = true;
                    break;
            }
        }
        if (hasTimer) {
            fieldsWithStaticInitializers.add(Lx.getComponentFieldCode("Timer"));
        }
        if (hasFirebaseDB) {
            fieldsWithStaticInitializers.add(Lx.getComponentFieldCode("FirebaseDB"));
        }
        if (hasFirebaseStorage) {
            fieldsWithStaticInitializers.add(Lx.getComponentFieldCode("FirebaseStorage"));
        }
        if (hasInterstitialAd) {
            fieldsWithStaticInitializers.add(Lx.getComponentFieldCode("InterstitialAd"));
        }
        if (hasRewardedVideoAd) {
            fieldsWithStaticInitializers.add(Lx.getComponentFieldCode("RewardedVideoAd"));
        }
    }

    /**
     * Adds Local libraries' imports
     */
    private void addLocalLibraryImports() {
        for (String value : mll.getImportLocalLibrary()) {
            addImport(value);
        }
    }
}

package com.besome.sketch.design;

import static pro.sketchware.utility.SketchwareUtil.getDip;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.besome.sketch.adapters.JavaFileAdapter;
import com.besome.sketch.beans.ProjectFileBean;
import com.besome.sketch.common.SrcViewerActivity;
import com.besome.sketch.editor.manage.ManageCollectionActivity;
import com.besome.sketch.editor.manage.ViewSelectorActivity;
import com.besome.sketch.editor.manage.font.ManageFontActivity;
import com.besome.sketch.editor.manage.image.ManageImageActivity;
import com.besome.sketch.editor.manage.library.ManageLibraryActivity;
import com.besome.sketch.editor.manage.sound.ManageSoundActivity;
import com.besome.sketch.editor.manage.view.ManageViewActivity;
import com.besome.sketch.lib.base.BaseAppCompatActivity;
import com.besome.sketch.lib.ui.CustomViewPager;
import com.besome.sketch.tools.CompileLogActivity;
import com.besome.sketch.design.hierarchy.LayoutHierarchySheet;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.topjohnwu.superuser.Shell;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import a.a.a.DB;
import a.a.a.GB;
import a.a.a.Ox;
import a.a.a.ProjectBuilder;
import a.a.a.ViewEditorFragment;
import a.a.a.aB;
import a.a.a.bB;
import a.a.a.bC;
import a.a.a.br;
import a.a.a.cC;
import a.a.a.eC;
import a.a.a.jC;
import a.a.a.kC;
import a.a.a.lC;
import a.a.a.mB;
import a.a.a.rs;
import a.a.a.wq;
import a.a.a.yB;
import a.a.a.yq;
import a.a.a.zy;
import dev.chrisbanes.insetter.Insetter;
import io.github.rosemoe.sora.langs.java.JavaLanguage;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.component.Magnifier;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import io.github.rosemoe.sora.widget.schemes.SchemeDarcula;
import mod.agus.jcoderz.editor.manage.permission.ManagePermissionActivity;
import mod.agus.jcoderz.editor.manage.resource.ManageResourceActivity;
import mod.hey.studios.activity.managers.assets.ManageAssetsActivity;
import mod.hey.studios.activity.managers.java.ManageJavaActivity;
import mod.hey.studios.build.BuildSettingsDialog;
import mod.hey.studios.compiler.kotlin.KotlinCompilerBridge;
import mod.hey.studios.project.custom_blocks.CustomBlocksDialog;
import mod.hey.studios.project.proguard.ManageProguardActivity;
import mod.hey.studios.project.proguard.ProguardHandler;
import mod.hey.studios.project.stringfog.ManageStringFogFragment;
import mod.hey.studios.project.stringfog.StringfogHandler;
import mod.hey.studios.util.Helper;
import mod.hey.studios.util.SystemLogPrinter;
import mod.hilal.saif.activities.android_manifest.AndroidManifestInjection;
import mod.hilal.saif.activities.tools.ConfigActivity;
import mod.jbk.build.BuildProgressReceiver;
import mod.jbk.build.BuiltInLibraries;
import mod.jbk.code.CodeEditorColorSchemes;
import mod.jbk.code.CodeEditorLanguages;
import mod.jbk.diagnostic.CompileErrorSaver;
import mod.jbk.diagnostic.MissingFileException;
import mod.jbk.util.LogUtil;
import mod.khaled.logcat.LogReaderActivity;
import pro.sketchware.R;
import pro.sketchware.activities.appcompat.ManageAppCompatActivity;
import pro.sketchware.activities.editor.command.ManageXMLCommandActivity;
import pro.sketchware.activities.editor.view.ViewCodeEditorActivity;
import pro.sketchware.databinding.ProgressMsgBoxBinding;
import pro.sketchware.utility.FileUtil;
import pro.sketchware.utility.SketchwareUtil;
import pro.sketchware.utility.ThemeUtils;
import pro.sketchware.utility.apk.ApkSignatures;
import pro.sketchware.activities.editor.view.CodeViewerActivity;

public class DesignActivity extends BaseAppCompatActivity implements View.OnClickListener {
    public static String sc_id;
    private ImageView xmlLayoutOrientation;
    private boolean B;
    private int currentTabNumber;
    private CustomViewPager viewPager;
    private CoordinatorLayout coordinatorLayout;
    private DrawerLayout drawer;
    private yq q;
    private DB r;
    private DB t;
    private Menu bottomMenu;
    private MenuItem directXmlEditorMenu;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ProjectFileBean projectFile;
    private TextView fileName;
    private String currentJavaFileName;
    private final ActivityResultLauncher<Intent> openImageManager = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            refresh();
        }
    });
    public final ActivityResultLauncher<Intent> changeOpenFile = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            assert result.getData() != null;
            projectFile = result.getData().getParcelableExtra("project_file");
            refresh();
        }
    });
    private ViewEditorFragment viewTabAdapter;
    private final ActivityResultLauncher<Intent> openLibraryManager = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            refresh();
            if (viewTabAdapter != null) {
                viewTabAdapter.n();
            }
        }
    });
    private final ActivityResultLauncher<Intent> openViewManager = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            refresh();
        }
    });
    private final ActivityResultLauncher<Intent> openCollectionManager = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            if (viewTabAdapter != null) {
                viewTabAdapter.j();
            }
        }
    });
    private final ActivityResultLauncher<Intent> openResourcesManager = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            if (viewTabAdapter != null) {
                viewTabAdapter.i();
            }
        }
    });
    private final ActivityResultLauncher<Intent> openViewCodeEditor = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == RESULT_OK) {
            if (viewTabAdapter != null) {
                viewTabAdapter.i();
            }
        }
    });
    private rs eventTabAdapter;
    private br componentTabAdapter;
    private BuildTask currentBuildTask;

    /**
     * Saves the app's version information to the currently opened Sketchware project file.
     */
    private void saveVersionCodeInformationToProject() {
        HashMap<String, Object> projectMetadata = lC.b(sc_id);
        if (projectMetadata != null) {
            projectMetadata.put("sketchware_ver", GB.d(getApplicationContext()));
            lC.b(sc_id, projectMetadata);
        }
    }

    private void loadProject(boolean haveSavedState) {
        projectFile = getDefaultProjectFile();
        jC.a(sc_id, haveSavedState);
        jC.b(sc_id, haveSavedState);
        kC var2 = jC.d(sc_id, haveSavedState);
        jC.c(sc_id, haveSavedState);
        cC.c(sc_id);
        bC.d(sc_id);
        if (!haveSavedState) {
            var2.f();
            var2.g();
            var2.e();
        }
    }

    private ProjectFileBean getDefaultProjectFile() {
        return jC.b(sc_id).b(ProjectFileBean.DEFAULT_XML_NAME);
    }

    private void refreshFileSelector() {
        if (projectFile == null) {
            projectFile = getDefaultProjectFile();
        }

        String javaFileName = projectFile.getJavaName();
        String xmlFileName = projectFile.getXmlName();

        if (!javaFileName.isEmpty()) {
            currentJavaFileName = javaFileName;
        }

        if (viewPager.getCurrentItem() == 0) {
            if (!ProjectFileBean.DEFAULT_XML_NAME.equals(xmlFileName) && jC.b(sc_id).b(xmlFileName) == null) {
                projectFile = getDefaultProjectFile();
                xmlFileName = ProjectFileBean.DEFAULT_XML_NAME;
            }
            fileName.setText(xmlFileName);
        } else {
            if (!ProjectFileBean.DEFAULT_JAVA_NAME.equals(currentJavaFileName) && jC.b(sc_id).a(currentJavaFileName) == null) {
                projectFile = getDefaultProjectFile();
                currentJavaFileName = ProjectFileBean.DEFAULT_JAVA_NAME;
            }
            fileName.setText(currentJavaFileName);
        }
    }

    private void refreshViewTabAdapter() {
        if (viewTabAdapter != null && projectFile != null) {
            int orientation = projectFile.orientation;
            if (orientation == ProjectFileBean.ORIENTATION_PORTRAIT) {
                xmlLayoutOrientation.setImageResource(R.drawable.ic_screen_portrait_grey600_24dp);
            } else if (orientation == ProjectFileBean.ORIENTATION_LANDSCAPE) {
                xmlLayoutOrientation.setImageResource(R.drawable.ic_screen_landscape_grey600_24dp);
            } else {
                xmlLayoutOrientation.setImageResource(R.drawable.ic_screen_rotation_grey600_24dp);
            }
            viewTabAdapter.a(projectFile);
        }
    }

    private void refreshEventTabAdapter() {
        if (eventTabAdapter != null && projectFile != null) {
            eventTabAdapter.setCurrentActivity(projectFile);
            eventTabAdapter.refreshEvents();
        }
    }

    private void refreshComponentTabAdapter() {
        if (componentTabAdapter != null && projectFile != null) {
            componentTabAdapter.setProjectFile(projectFile);
            componentTabAdapter.refreshData();
        }
    }

    private void refresh() {
        refreshFileSelector();
        if (viewPager.getCurrentItem() == 0) {
            refreshViewTabAdapter();
        } else {
            refreshEventTabAdapter();
            refreshComponentTabAdapter();
        }
    }

    public void setTouchEventEnabled(boolean touchEventEnabled) {
        if (touchEventEnabled) {
            viewPager.enableTouchEvent();
        } else {
            viewPager.disableTouchEvent();
        }
    }

    /**
     * Shows a Snackbar indicating that a problem occurred while compiling. The user can click on "SHOW" to get to {@link CompileLogActivity}.
     *
     * @param error The error, to be later displayed as text in {@link CompileLogActivity}
     */
    private void indicateCompileErrorOccurred(String error) {
        new CompileErrorSaver(sc_id).writeLogsToFile(error);
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Show compile log", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(Helper.getResString(R.string.common_word_show), v -> {
            if (!mB.a()) {
                snackbar.dismiss();
                Intent intent = new Intent(getApplicationContext(), CompileLogActivity.class);
                intent.putExtra("error", error);
                intent.putExtra("sc_id", sc_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });
        snackbar.show();
    }

    @Override
    public void finish() {
        jC.a();
        cC.a();
        bC.a();
        setResult(RESULT_CANCELED, getIntent());
        super.finish();
    }

    private void checkForUnsavedProjectData() {
        if (jC.c(sc_id).g() || jC.b(sc_id).g() || jC.d(sc_id).q() || jC.a(sc_id).d() || jC.a(sc_id).c()) {
            askIfToRestoreOldUnsavedProjectData();
        }
    }

    /**
     * Opens the debug APK to install.
     */
    private void installBuiltApk() {
        if (!ConfigActivity.isSettingEnabled(ConfigActivity.SETTING_ROOT_AUTO_INSTALL_PROJECTS)) {
            requestPackageInstallerInstall();
        } else {
            File apkUri = new File(q.finalToInstallApkPath);
            long length = apkUri.length();
            Shell.getShell(shell -> {
                if (shell.isRoot()) {
                    List<String> stdout = new LinkedList<>();
                    List<String> stderr = new LinkedList<>();

                    Shell.cmd("cat " + apkUri + " | pm install -S " + length).to(stdout, stderr).submit(result -> {
                        if (result.isSuccess()) {
                            SketchwareUtil.toast("Package installed successfully!");
                            if (ConfigActivity.isSettingEnabled(ConfigActivity.SETTING_ROOT_AUTO_OPEN_AFTER_INSTALLING)) {
                                Intent launcher = getPackageManager().getLaunchIntentForPackage(q.packageName);
                                if (launcher != null) {
                                    startActivity(launcher);
                                } else {
                                    SketchwareUtil.toastError("Couldn't launch project, either not installed or not with launcher activity.");
                                }
                            }
                        } else {
                            String sharedErrorMessage = "Failed to install package, result code: " + result.getCode() + ". ";
                            SketchwareUtil.toastError(sharedErrorMessage + "Logs are available in /Internal storage/.sketchware/debug.txt", Toast.LENGTH_LONG);
                            LogUtil.e("DesignActivity", sharedErrorMessage + "stdout: " + stdout + ", stderr: " + stderr);
                        }
                    });
                } else {
                    SketchwareUtil.toastError("No root access granted. Continuing using default package install prompt.");
                    requestPackageInstallerInstall();
                }
            });
        }
    }

    private void requestPackageInstallerInstall() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 24) {
            Uri apkUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", new File(q.finalToInstallApkPath));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(q.finalToInstallApkPath)), "application/vnd.android.package-archive");
        }

        startActivity(intent);
    }

    public void updateViewPropertiesMenuState() {
        viewTabAdapter.a(true);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else if (viewTabAdapter.g()) {
            viewTabAdapter.a(false);
        } else {
            if (currentTabNumber > 0) {
                currentTabNumber--;
                viewPager.setCurrentItem(currentTabNumber);
            } else if (t.c("P12I2")) {
                k();
                saveChangesAndCloseProject();
            } else {
                showSaveBeforeQuittingDialog();
            }
        }
    }

    private void saveChangesAndCloseProject() {
        k();
        SaveChangesProjectCloser saveChangesProjectCloser = new SaveChangesProjectCloser(this);
        saveChangesProjectCloser.execute();
    }

    private void saveProject() {
        k();
        ProjectSaver projectSaver = new ProjectSaver(this);
        projectSaver.execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.design);
        if (!isStoragePermissionGranted()) {
            finish();
        }

        if (savedInstanceState == null) {
            sc_id = getIntent().getStringExtra("sc_id");
        } else {
            sc_id = savedInstanceState.getString("sc_id");
        }

        r = new DB(getApplicationContext(), "P1");
        t = new DB(getApplicationContext(), "P12");
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setSubtitle(sc_id);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        drawer = findViewById(R.id.drawer_layout);
        Insetter.builder()
                .margin(WindowInsetsCompat.Type.navigationBars())
                .applyToView(findViewById(R.id.container));
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        coordinatorLayout = findViewById(R.id.layout_coordinator);
        fileName = findViewById(R.id.file_name);
        findViewById(R.id.file_name_container).setOnClickListener(this);
        BottomAppBar bottomAppBar = findViewById(R.id.bottom_app_bar);
        bottomAppBar.setOverflowIcon(ContextCompat.getDrawable(this,  R.drawable.sorting_options));
        bottomMenu = bottomAppBar.getMenu();
        bottomMenu.add(Menu.NONE, 1, Menu.NONE, "Build Settings");
        bottomMenu.add(Menu.NONE, 2, Menu.NONE, "Clean temporary files").setVisible(false);
        bottomMenu.add(Menu.NONE, 3, Menu.NONE, "Show last compile error");
        bottomMenu.add(Menu.NONE, 5, Menu.NONE, "Show source code");
        bottomMenu.add(Menu.NONE, 4, Menu.NONE, "Install last built APK").setVisible(false);
        bottomMenu.add(Menu.NONE, 6, Menu.NONE, "Show Apk signatures").setVisible(false);
        directXmlEditorMenu = bottomMenu.add(Menu.NONE, 7, Menu.NONE, "Direct XML editor");

        bottomAppBar.setOnMenuItemClickListener(
                item -> {
                    var itemId = item.getItemId();
                    switch (itemId) {
                        case 1 -> new BuildSettingsDialog(this, sc_id).show();
                        case 2 -> new Thread(
                                () -> {
                                    FileUtil.deleteFile(q.projectMyscPath);
                                    updateBottomMenu();
                                    runOnUiThread(
                                            () ->
                                                    SketchwareUtil.toast(
                                                            "Done cleaning temporary files!"));
                                })
                                .start();
                        case 3 -> new CompileErrorSaver(sc_id).showLastErrors(this);
                        case 4 -> {
                            if (FileUtil.isExistFile(q.finalToInstallApkPath)) {
                                installBuiltApk();
                            } else {
                                SketchwareUtil.toast("APK doesn't exist anymore");
                            }
                        }
                        case 5 -> showCurrentActivitySrcCode();
                        case 6 -> {
                            ApkSignatures apkSignatures =
                                    new ApkSignatures(this, q.finalToInstallApkPath);
                            apkSignatures.showSignaturesDialog();
                        }
                        case 7 -> toViewCodeEditor();
                        default -> {
                            if (itemId == R.id.menu_run) {
                                BuildTask buildTask = new BuildTask(this);
                                currentBuildTask = buildTask;
                                buildTask.execute();
                            } else if (itemId == R.id.menu_cancel) {
                                if (currentBuildTask != null) {
                                    currentBuildTask.cancelBuild();
                                }
                            } else {
                                return false;
                            }
                        }
                    }

                    return true;
                });
        xmlLayoutOrientation = findViewById(R.id.img_orientation);
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int state) {
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (currentTabNumber == 1) {
                    if (eventTabAdapter != null) {
                        eventTabAdapter.c();
                    }
                } else if (currentTabNumber == 2 && componentTabAdapter != null) {
                    componentTabAdapter.unselectAll();
                }
                if (position == 0) {
                    directXmlEditorMenu.setVisible(true);
                    if (viewTabAdapter != null) {
                        viewTabAdapter.c(true);
                        xmlLayoutOrientation.setImageResource(R.drawable.ic_mtrl_screen);
                    }
                } else if (position == 1) {
                    directXmlEditorMenu.setVisible(false);
                    if (viewTabAdapter != null) {
                        xmlLayoutOrientation.setImageResource(R.drawable.ic_mtrl_code);
                        viewTabAdapter.c(false);
                        if (eventTabAdapter != null) {
                            eventTabAdapter.refreshEvents();
                        }
                    }
                } else {
                    directXmlEditorMenu.setVisible(false);
                    if (viewTabAdapter != null) {
                        xmlLayoutOrientation.setImageResource(R.drawable.ic_mtrl_code);
                        viewTabAdapter.c(false);
                        if (componentTabAdapter != null) {
                            componentTabAdapter.refreshData();
                        }
                    }
                }
                refresh();
                currentTabNumber = position;
            }
        });
        viewPager.getAdapter().notifyDataSetChanged();
        ((TabLayout) findViewById(R.id.tab_layout)).setupWithViewPager(viewPager);

        IntentFilter filter = new IntentFilter(BuildTask.ACTION_CANCEL_BUILD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(buildCancelReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(buildCancelReceiver, filter);
        }
    }

    private boolean isDebugApkExists() {
        if (q != null) {
            return FileUtil.isExistFile(q.finalToInstallApkPath);
        }
        return false;
    }

    private void updateBottomMenu() {
        if (bottomMenu != null) {
            handler.post(
                    () -> {
                        bottomMenu
                                .findItem(2)
                                .setVisible(q != null && FileUtil.isExistFile(q.projectMyscPath));
                        var isDebugApkExists = isDebugApkExists();
                        bottomMenu.findItem(4).setVisible(isDebugApkExists);
                        bottomMenu.findItem(6).setVisible(isDebugApkExists);
                    });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(buildCancelReceiver);
    }

    private final BroadcastReceiver buildCancelReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BuildTask.ACTION_CANCEL_BUILD.equals(intent.getAction())) {
                if (currentBuildTask != null) {
                    currentBuildTask.cancelBuild();
                }
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.design_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.design_actionbar_titleopen_drawer) {
            if (!drawer.isDrawerOpen(GravityCompat.END)) {
                drawer.openDrawer(GravityCompat.END);
            }
        } else if (itemId == R.id.design_option_menu_title_save_project) {
            saveProject();
        } else if (itemId == R.id.menu_view_hierarchy) {
            toLayoutHierarchySheet();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        k();

        HashMap<String, Object> projectInfo = lC.b(sc_id);
        getSupportActionBar().setTitle(yB.c(projectInfo, "my_ws_name"));
        q = new yq(getApplicationContext(), wq.d(sc_id), projectInfo);

        try {
            ProjectLoader projectLoader = new ProjectLoader(this, savedInstanceState);
            projectLoader.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            SystemLogPrinter.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isStoragePermissionGranted()) {
            finish();
        }

        long freeMegabytes = GB.c();
        if (freeMegabytes < 100L && freeMegabytes > 0L) {
            warnAboutInsufficientStorageSpace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("sc_id", sc_id);
        super.onSaveInstanceState(outState);
        if (!isStoragePermissionGranted()) {
            finish();
        }

        if (!B) {
            UnsavedChangesSaver unsavedChangesSaver = new UnsavedChangesSaver(this);
            unsavedChangesSaver.execute();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.file_name_container) {
            if (viewPager.getCurrentItem() == 0) {
                showAvailableViews();
            } else {
                showAvailableJavaFiles();
            }
        }
    }

    /**
     * Show a dialog asking about saving the project before quitting.
     */
    private void showSaveBeforeQuittingDialog() {
        aB dialog = new aB(this);
        dialog.b(Helper.getResString(R.string.design_quit_title_exit_projet));
        dialog.a(R.drawable.ic_mtrl_exit);
        dialog.a(Helper.getResString(R.string.design_quit_message_confirm_save));
        dialog.b(Helper.getResString(R.string.design_quit_button_save_and_exit), v -> {
            if (!mB.a()) {
                dialog.dismiss();
                try {
                    saveChangesAndCloseProject();
                } catch (Exception e) {
                    e.printStackTrace();
                    h();
                }
            }
        });
        dialog.a(Helper.getResString(R.string.common_word_exit), v -> {
            if (!mB.a()) {
                dialog.dismiss();
                try {
                    k();
                    DiscardChangesProjectCloser discardChangesProjectCloser = new DiscardChangesProjectCloser(this);
                    discardChangesProjectCloser.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                    h();
                }
            }
        });
        dialog.configureDefaultButton(Helper.getResString(R.string.common_word_cancel),
                Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    /**
     * Show a dialog warning the user about low free space.
     */
    private void warnAboutInsufficientStorageSpace() {
        aB dialog = new aB(this);
        dialog.b(Helper.getResString(R.string.common_word_warning));
        dialog.a(R.drawable.break_warning_96_red);
        dialog.a(Helper.getResString(R.string.common_message_insufficient_storage_space));
        dialog.b(Helper.getResString(R.string.common_word_ok),
                Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    private void askIfToRestoreOldUnsavedProjectData() {
        B = true;
        aB dialog = new aB(this);
        dialog.a(R.drawable.ic_mtrl_history);
        dialog.b(Helper.getResString(R.string.design_restore_data_title));
        dialog.a(Helper.getResString(R.string.design_restore_data_message_confirm));
        dialog.b(Helper.getResString(R.string.common_word_restore), v -> {
            if (!mB.a()) {
                boolean g = jC.c(sc_id).g();
                boolean g2 = jC.b(sc_id).g();
                boolean q = jC.d(sc_id).q();
                boolean d = jC.a(sc_id).d();
                boolean c = jC.a(sc_id).c();
                if (g) {
                    jC.c(sc_id).h();
                }
                if (g2) {
                    jC.b(sc_id).h();
                }
                if (q) {
                    jC.d(sc_id).r();
                }
                if (d) {
                    jC.a(sc_id).h();
                }
                if (c) {
                    jC.a(sc_id).f();
                }
                if (g) {
                    jC.b(sc_id).a(jC.c(sc_id));
                    jC.a(sc_id).a(jC.c(sc_id).d());
                }
                if (g2 || g) {
                    jC.a(sc_id).a(jC.b(sc_id));
                }
                if (q) {
                    jC.a(sc_id).b(jC.d(sc_id));
                    jC.a(sc_id).c(jC.d(sc_id));
                    jC.a(sc_id).a(jC.d(sc_id));
                }
                refresh();
                B = false;
                dialog.dismiss();
            }
        });
        dialog.a(Helper.getResString(R.string.common_word_no), v -> {
            B = false;
            dialog.dismiss();
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showCurrentActivitySrcCode() {
        if (projectFile == null) return; 
        k();
        new Thread(() -> {
            final var filename = fileName.getText().toString();
            final var code = new yq(getApplicationContext(), sc_id).getFileSrc(filename, jC.b(sc_id), jC.a(sc_id), jC.c(sc_id));
            runOnUiThread(() -> {
                if (isFinishing()) return;
                h();
                if (code.isEmpty()) {
                    SketchwareUtil.toast("Failed to generate source.");
                    return;
                }
                final var scheme = filename.endsWith(".xml")
                  ? CodeViewerActivity.SCHEME_XML
                  : CodeViewerActivity.SCHEME_JAVA;
                launchActivity(CodeViewerActivity.class, null, new Pair<>("code", code), new Pair<>("sc_id", sc_id), new Pair<>("scheme", scheme));
            });
        }).start();
    }

    private void showAvailableJavaFiles() {
        var dialog = new aB(this);
        dialog.b(getTranslatedString(R.string.design_file_selector_title_java));
        dialog.a(R.drawable.ic_mtrl_java);
        View customView = a.a.a.wB.a(this, R.layout.file_selector_popup_select_java);
        RecyclerView recyclerView = customView.findViewById(R.id.file_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        var adapter = new JavaFileAdapter(sc_id);
        adapter.setOnItemClickListener(projectFileBean -> {
            projectFile = projectFileBean;
            refreshFileSelector();
            refreshEventTabAdapter();
            refreshComponentTabAdapter();
            dialog.dismiss();
        });
        recyclerView.setAdapter(adapter);
        dialog.a(customView);
        dialog.show();
    }

    private void showAvailableViews() {
        Intent intent = new Intent(getApplicationContext(), ViewSelectorActivity.class);
        intent.putExtra("sc_id", sc_id);
        intent.putExtra("current_xml", projectFile.getXmlName());
        intent.putExtra("is_custom_view", projectFile.fileType == 1 || projectFile.fileType == 2);
        changeOpenFile.launch(intent);
    }

    /**
     * Opens {@link ViewCodeEditorActivity}.
     */
    void toViewCodeEditor() {
        if (projectFile == null) return;
        k();
        new Thread(() -> {
            final String filename = fileName.getText().toString();
            // var yq = new yq(getApplicationContext(), sc_id);
            var xmlGenerator = new Ox(q.N, projectFile);
            var projectDataManager = jC.a(sc_id);
            var viewBeans = projectDataManager.d(filename);
            var viewFab = projectDataManager.h(filename);
            xmlGenerator.setExcludeAppCompat(true);
            xmlGenerator.a(eC.a(viewBeans), viewFab);
            final String content = xmlGenerator.b();
            runOnUiThread(() -> {
                if (isFinishing()) return;
                h();
                launchActivity(ViewCodeEditorActivity.class, openViewCodeEditor, new Pair<>("title", filename), new Pair<>("content", content));
            });
        }).start();
    }

    /**
     * Opens {@link LogReaderActivity}.
     */
    void toLogReader() {
        Intent intent = new Intent(getApplicationContext(), LogReaderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("sc_id", sc_id);
        startActivity(intent);
    }

    /**
     * Opens {@link ManageCollectionActivity}.
     */
    void toCollectionManager() {
        launchActivity(ManageCollectionActivity.class, openCollectionManager);
    }

    /**
     * Opens {@link AndroidManifestInjection}.
     */
    void toAndroidManifestManager() {
        if (projectFile == null) return;
        launchActivity(AndroidManifestInjection.class, null,
                new Pair<>("file_name", currentJavaFileName));
    }

    /**
     * Opens {@link ManageAppCompatActivity}.
     */
    void toAppCompatInjectionManager() {
        if (projectFile == null) return;
        launchActivity(ManageAppCompatActivity.class, null,
                new Pair<>("file_name", projectFile.getXmlName()));
    }

    /**
     * Opens {@link ManageAssetsActivity}.
     */
    void toAssetManager() {
        launchActivity(ManageAssetsActivity.class, null);
    }

    /**
     * Shows a {@link CustomBlocksDialog}.
     */
    void toCustomBlocksViewer() {
        new CustomBlocksDialog().show(this, sc_id);
    }

    /**
     * Opens {@link ManageJavaActivity}.
     */
    void toJavaManager() {
        launchActivity(ManageJavaActivity.class, null,
                new Pair<>("pkgName", q.packageName));
    }

    /**
     * Opens {@link ManagePermissionActivity}.
     */
    void toPermissionManager() {
        launchActivity(ManagePermissionActivity.class, null);
    }

    /**
     * Opens {@link ManageProguardActivity}.
     */
    void toProguardManager() {
        launchActivity(ManageProguardActivity.class, null);
    }

    /**
     * Opens {@link ManageResourceActivity}.
     */
    void toResourceManager() {
        launchActivity(ManageResourceActivity.class, openResourcesManager);
    }

    /**
     * Opens {@link ManageStringFogFragment}.
     */
    void toStringFogManager() {
        var fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag("stringFogFragment") == null) {
            var bottomSheet = new ManageStringFogFragment();
            bottomSheet.show(fragmentManager, "stringFogFragment");
        }
    }

    /**
     * Opens {@link ManageFontActivity}.
     */
    void toFontManager() {
        launchActivity(ManageFontActivity.class, null);
    }

    /**
     * Opens {@link ManageImageActivity}.
     */
    void toImageManager() {
        launchActivity(ManageImageActivity.class, openImageManager);
    }

    /**
     * Opens {@link ManageLibraryActivity}.
     */
    void toLibraryManager() {
        launchActivity(ManageLibraryActivity.class, openLibraryManager);
    }

    /**
     * Opens {@link ManageViewActivity}.
     */
    void toViewManager() {
        launchActivity(ManageViewActivity.class, openViewManager);
    }

    /**
     * Opens {@link ManageSoundActivity}.
     */
    void toSoundManager() {
        launchActivity(ManageSoundActivity.class, null);
    }

    /**
     * Opens {@link SrcViewerActivity}.
     */
    void toSourceCodeViewer() {
        launchActivity(SrcViewerActivity.class, null, new Pair<>("current", fileName.getText().toString()));
    }

    /**
     * Opens {@link ManageXMLCommandActivity}.
     */
    void toXMLCommandManager() {
        launchActivity(ManageXMLCommandActivity.class, null);
    }
    
    /**
     * Opens {@link LayoutHierarchySheet}.
     */
    void toLayoutHierarchySheet() {
        var projectDataManager = jC.a(sc_id);
        var fileName = projectFileSelector.getFileName();
        var viewBeans = projectDataManager.d(fileName);
        var hierarchySheet = new LayoutHierarchySheet(this, viewBeans);
        hierarchySheet.show();
    }

    public void initializeProjectActivityViews() {
        viewTabAdapter.n();
    }
    
    @SafeVarargs
    private void launchActivity(Class<? extends Activity> toLaunch, ActivityResultLauncher<Intent> optionalLauncher, Pair<String, String>... extras) {
        Intent intent = new Intent(getApplicationContext(), toLaunch);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("sc_id", sc_id);
        for (Pair<String, String> extra : extras) {
            intent.putExtra(extra.first, extra.second);
        }

        if (optionalLauncher == null) {
            startActivity(intent);
        } else {
            optionalLauncher.launch(intent);
        }
    }

    private abstract static class BaseTask {
        protected final WeakReference<DesignActivity> activityRef;

        protected BaseTask(DesignActivity activity) {
            this.activityRef = new WeakReference<>(activity);
        }

        protected DesignActivity getActivity() {
            return activityRef.get();
        }
    }

    private static class BuildTask extends BaseTask implements BuildProgressReceiver {
        public static final String ACTION_CANCEL_BUILD = "com.besome.sketch.design.ACTION_CANCEL_BUILD";
        private final ExecutorService executorService = Executors.newSingleThreadExecutor();
        private volatile boolean canceled;
        private volatile boolean isBuildFinished;
        private final NotificationManager notificationManager;
        private final int notificationId = 1;
        private boolean isShowingNotification = false;
        private static final String CHANNEL_ID = "build_notification_channel";
        private MenuItem runMenu;
        private MenuItem cancelMenu;

        private LinearLayout progressContainer;
        private TextView progressText;
        private LinearProgressIndicator progressBar;

        public BuildTask(DesignActivity activity) {
            super(activity);
            notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            runMenu = activity.bottomMenu.findItem(R.id.menu_run);
            cancelMenu = activity.bottomMenu.findItem(R.id.menu_cancel);
            progressContainer = activity.findViewById(R.id.progress_container);
            progressText = activity.findViewById(R.id.progress_text);
            progressBar = activity.findViewById(R.id.progress);
        }

        public void execute() {
            onPreExecute();
            executorService.execute(this::doInBackground);
        }

        private void onPreExecute() {
            DesignActivity activity = getActivity();
            if (activity == null) return;

            activity.runOnUiThread(() -> {
                updateRunMenu(true);
                activity.r.a("P1I10", true);
                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                maybeShowNotification();
            });
        }

        private void doInBackground() {
            DesignActivity activity = getActivity();
            if (activity == null) return;

            try {
                var q = activity.q;
                var sc_id = DesignActivity.sc_id;
                onProgress("Deleting temporary files...", 1);
                FileUtil.deleteFile(q.projectMyscPath);

                q.c(activity.getApplicationContext());
                q.a();
                q.a(activity.getApplicationContext(), wq.e("600"));
                if (yB.a(lC.b(sc_id), "custom_icon")) {
                    q.aa(wq.e() + File.separator + sc_id + File.separator + "mipmaps");
                    if (yB.a(lC.b(sc_id), "isIconAdaptive", false)) {
                        q.cf("""
                                <?xml version="1.0" encoding="utf-8"?>
                                <adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android" >
                                <background android:drawable="@mipmap/ic_launcher_background"/>
                                <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
                                <monochrome android:drawable="@mipmap/ic_launcher_monochrome"/>
                                </adaptive-icon>""");
                    } else {
                        q.a(wq.e() + File.separator + sc_id + File.separator + "icon.png");
                    }
                }

                onProgress("Generating source code...", 2);
                kC kC = jC.d(sc_id);
                kC.b(q.resDirectoryPath + File.separator + "drawable-xhdpi");
                kC = jC.d(sc_id);
                kC.c(q.resDirectoryPath + File.separator + "raw");
                kC = jC.d(sc_id);
                kC.a(q.assetsPath + File.separator + "fonts");

                ProjectBuilder builder = new ProjectBuilder(this, activity.getApplicationContext(), q);

                var fileManager = jC.b(sc_id);
                var dataManager = jC.a(sc_id);
                var libraryManager = jC.c(sc_id);
                q.a(libraryManager, fileManager, dataManager, false);
                builder.buildBuiltInLibraryInformation();
                q.b(fileManager, dataManager, libraryManager, builder.getBuiltInLibraryManager());
                q.f();
                q.e();

                builder.maybeExtractAapt2();
                if (canceled) {
                    return;
                }

                onProgress("Extracting built-in libraries...", 3);
                BuiltInLibraries.extractCompileAssets(this);
                if (canceled) {
                    return;
                }

                onProgress("AAPT2 is running...", 8);
                builder.compileResources();
                if (canceled) {
                    return;
                }

                onProgress("Generating view binding...", 11);
                builder.generateViewBinding();
                if (canceled) {
                    return;
                }

                KotlinCompilerBridge.compileKotlinCodeIfPossible(this, builder);
                if (canceled) {
                    return;
                }

                onProgress("Java is compiling...", 13);
                builder.compileJavaCode();
                if (canceled) {
                    return;
                }

                StringfogHandler stringfogHandler = new StringfogHandler(sc_id);
                stringfogHandler.start(this, builder);
                if (canceled) {
                    return;
                }

                ProguardHandler proguardHandler = new ProguardHandler(sc_id);
                proguardHandler.start(this, builder);
                if (canceled) {
                    return;
                }

                onProgress(builder.getDxRunningText(), 17);
                builder.createDexFilesFromClasses();
                if (canceled) {
                    return;
                }

                onProgress("Merging DEX files...", 18);
                builder.getDexFilesReady();
                if (canceled) {
                    return;
                }

                onProgress("Building APK...", 19);
                builder.buildApk();
                if (canceled) {
                    return;
                }

                onProgress("Signing APK...", 20);
                builder.signDebugApk();
                if (canceled) {
                    return;
                }

                activity.installBuiltApk();
                isBuildFinished = true;
            } catch (MissingFileException e) {
                isBuildFinished = true;
                activity.runOnUiThread(() -> {
                    boolean isMissingDirectory = e.isMissingDirectory();

                    aB dialog = new aB(activity);
                    if (isMissingDirectory) {
                        dialog.b("Missing directory detected");
                        dialog.a("A directory important for building is missing. " +
                                "Sketchware Pro can try creating " + e.getMissingFile().getAbsolutePath() +
                                " if you'd like to.");
                        dialog.configureDefaultButton("Create", v -> {
                            dialog.dismiss();
                            if (!e.getMissingFile().mkdirs()) {
                                SketchwareUtil.toastError("Failed to create directory / directories!");
                            }
                        });
                    } else {
                        dialog.b("Missing file detected");
                        dialog.a("A file needed for building is missing. " +
                                "Put the correct file back to " + e.getMissingFile().getAbsolutePath() +
                                " and try building again.");
                    }
                    dialog.b("Dismiss", Helper.getDialogDismissListener(dialog));
                    dialog.show();
                });
            } catch (zy zy) {
                isBuildFinished = true;
                activity.indicateCompileErrorOccurred(zy.getMessage());
            } catch (Throwable tr) {
                isBuildFinished = true;
                LogUtil.e("DesignActivity$BuildTask", "Failed to build project", tr);
                activity.indicateCompileErrorOccurred(Log.getStackTraceString(tr));
            } finally {
                activity.runOnUiThread(this::onPostExecute);
            }
        }

        public void onProgress(String progress, int step) {
            int totalSteps = 20;

            DesignActivity activity = getActivity();
            if (activity == null) return;

            activity.runOnUiThread(() -> {
                progressBar.setIndeterminate(step == -1);
                if (!canceled) {
                    updateNotification(progress + " (" + step + " / " + totalSteps + ")");
                }
                progressText.setText(progress);
                var progressInt = (step * 100) / totalSteps;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                   progressBar.setProgress(progressInt, true);
                } else {
                   progressBar.setProgress(progressInt);
                }
                Log.d("DesignActivity$BuildTask", step + " / " + totalSteps);
            });
        }

        private void onPostExecute() {
            DesignActivity activity = getActivity();
            if (activity == null) return;

            activity.runOnUiThread(() -> {
                if (!activity.isDestroyed()) {
                    if (isShowingNotification) {
                        notificationManager.cancel(notificationId);
                        isShowingNotification = false;
                    }
                    updateRunMenu(false);
                    activity.updateBottomMenu();
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            });
        }

        public void cancelBuild() {
            canceled = true;
            onProgress("Canceling build...", -1);
            if (isShowingNotification) {
                notificationManager.cancel(notificationId);
                isShowingNotification = false;
            }
            DesignActivity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(() -> {
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                });
            }
        }

        private void maybeShowNotification() {
            DesignActivity activity = getActivity();
            if (activity == null) return;

            if (!isShowingNotification) {
                createNotificationChannelIfNeeded();

                NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_mtrl_code)
                        .setContentTitle("Building project")
                        .setContentText("Starting build...")
                        .setOngoing(true)
                        .setProgress(0, 0, true)
                        .addAction(R.drawable.ic_cancel_white_96dp, "Cancel build", getCancelPendingIntent());

                notificationManager.notify(notificationId, builder.build());
                isShowingNotification = true;
            }
        }

        private void updateNotification(String progress) {
            DesignActivity activity = getActivity();
            if (activity == null) return;

            NotificationCompat.Builder builder = new NotificationCompat.Builder(activity, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_mtrl_code)
                    .setContentTitle("Building project")
                    .setContentText(progress)
                    .setOngoing(true)
                    .setProgress(0, 0, true)
                    .addAction(R.drawable.ic_cancel_white_96dp, "Cancel Build", getCancelPendingIntent());

            notificationManager.notify(notificationId, builder.build());
        }

        private PendingIntent getCancelPendingIntent() {
            DesignActivity activity = getActivity();
            if (activity == null) return null;

            Intent cancelIntent = new Intent(BuildTask.ACTION_CANCEL_BUILD);
            return PendingIntent.getBroadcast(activity, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        private void createNotificationChannelIfNeeded() {
            DesignActivity activity = getActivity();
            if (activity == null) return;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = "Build Notifications";
                String description = "Notifications for build progress";
                int importance = NotificationManager.IMPORTANCE_LOW;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                notificationManager.createNotificationChannel(channel);
            }
        }

        private void updateRunMenu(boolean isRunning) {
            runMenu.setVisible(!isRunning);
            cancelMenu.setVisible(isRunning);
            progressContainer.setVisibility(isRunning ? View.VISIBLE : View.GONE);
        }
    }

    private static class ProjectLoader extends BaseTask {
        private final Bundle savedInstanceState;
        private final ExecutorService executorService = Executors.newSingleThreadExecutor();

        public ProjectLoader(DesignActivity activity, Bundle savedInstanceState) {
            super(activity);
            this.savedInstanceState = savedInstanceState;
        }

        public void execute() {
            getActivity().k();
            executorService.execute(this::doInBackground);
        }

        private void doInBackground() {
            DesignActivity activity = getActivity();
            if (activity != null) {
                activity.loadProject(savedInstanceState != null);
                activity.runOnUiThread(() -> {
                    activity.updateBottomMenu();
                    activity.refresh();
                    activity.h();
                    if (savedInstanceState == null) {
                        activity.checkForUnsavedProjectData();
                    }
                });
            }
        }
    }

    private static class DiscardChangesProjectCloser extends BaseTask {
        private final ExecutorService executorService = Executors.newSingleThreadExecutor();

        public DiscardChangesProjectCloser(DesignActivity activity) {
            super(activity);
        }

        public void execute() {
            getActivity().k();
            executorService.execute(this::doInBackground);
        }

        private void doInBackground() {
            DesignActivity activity = getActivity();
            if (activity != null) {
                var sc_id = DesignActivity.sc_id;
                jC.d(sc_id).v();
                jC.d(sc_id).w();
                jC.d(sc_id).u();
                activity.runOnUiThread(() -> {
                    activity.h();
                    activity.finish();
                });
            }
        }
    }

    private static class ProjectSaver extends BaseTask {
        private final ExecutorService executorService = Executors.newSingleThreadExecutor();

        public ProjectSaver(DesignActivity activity) {
            super(activity);
        }

        public void execute() {
            getActivity().k();
            executorService.execute(this::doInBackground);
        }

        private void doInBackground() {
            DesignActivity activity = getActivity();
            if (activity != null) {
                var sc_id = DesignActivity.sc_id;
                jC.d(sc_id).a();
                jC.b(sc_id).m();
                jC.a(sc_id).j();
                jC.d(sc_id).x();
                jC.c(sc_id).l();
                activity.runOnUiThread(() -> {
                    bB.a(activity.getApplicationContext(), Helper.getResString(R.string.common_message_complete_save), bB.TOAST_NORMAL).show();
                    activity.saveVersionCodeInformationToProject();
                    activity.h();
                    jC.d(sc_id).f();
                    jC.d(sc_id).g();
                    jC.d(sc_id).e();
                });
            }
        }
    }

    private static class SaveChangesProjectCloser extends BaseTask {
        private final ExecutorService executorService = Executors.newSingleThreadExecutor();

        public SaveChangesProjectCloser(DesignActivity activity) {
            super(activity);
        }

        public void execute() {
            getActivity().k();
            executorService.execute(this::doInBackground);
        }

        private void doInBackground() {
            DesignActivity activity = getActivity();
            if (activity != null) {
                var sc_id = DesignActivity.sc_id;
                jC.d(sc_id).a();
                jC.b(sc_id).m();
                jC.a(sc_id).j();
                jC.d(sc_id).x();
                jC.c(sc_id).l();
                jC.d(sc_id).h();
                activity.runOnUiThread(() -> {
                    bB.a(activity.getApplicationContext(), Helper.getResString(R.string.common_message_complete_save), bB.TOAST_NORMAL).show();
                    activity.saveVersionCodeInformationToProject();
                    activity.h();
                    activity.finish();
                });
            }
        }
    }

    private static class UnsavedChangesSaver extends BaseTask {
        private final ExecutorService executorService = Executors.newSingleThreadExecutor();

        public UnsavedChangesSaver(DesignActivity activity) {
            super(activity);
        }

        public void execute() {
            executorService.execute(this::doInBackground);
        }

        private void doInBackground() {
            DesignActivity activity = getActivity();
            if (activity != null) {
                jC.a(sc_id).k();
            }
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final String[] labels;

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
            labels = new String[]{
                    Helper.getResString(R.string.design_tab_title_view),
                    Helper.getResString(R.string.design_tab_title_event),
                    Helper.getResString(R.string.design_tab_title_component)};
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return labels[position];
        }

        @Override
        @NonNull
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            if (position == 0) {
                viewTabAdapter = (ViewEditorFragment) fragment;
            } else if (position == 1) {
                eventTabAdapter = (rs) fragment;
            } else {
                componentTabAdapter = (br) fragment;
            }

            return fragment;
        }

        @Override
        @NonNull
        public Fragment getItem(int position) {
            if (position == 0) {
                return new ViewEditorFragment();
            } else {
                return position == 1 ? new rs() : new br();
            }
        }
    }
}

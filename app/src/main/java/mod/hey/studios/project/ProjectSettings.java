package mod.hey.studios.project;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;

import mod.hey.studios.util.Helper;
import mod.jbk.util.LogUtil;
import pro.sketchware.utility.FileUtil;

public class ProjectSettings {

    /**
     * Setting for the final app's {@code minSdkVersion}
     *
     * @see ApplicationInfo#minSdkVersion
     */
    public static final String SETTING_MINIMUM_SDK_VERSION = "min_sdk";
    
    /**
     * Setting for the final app's {@code targetSdkVersion}
     *
     * @see ApplicationInfo#targetSdkVersion
     */
    public static final String SETTING_TARGET_SDK_VERSION = "target_sdk";
    
    /**
     * Setting for the final app's {@link Application} class
     *
     * @see Application
     */
    public static final String SETTING_APPLICATION_CLASS = "app_class";
    
    /**
     * Setting to enable view binding in the project
     */
    public static final String SETTING_ENABLE_VIEWBINDING = "enable_viewbinding";
    
    /**
     * Setting to disable showing deprecated methods included in every generated class, e.g. showMessage(String)
     */
    public static final String SETTING_DISABLE_OLD_METHODS = "disable_old_methods";
    
    /**
     * Setting to make the app's main theme material 3.
     */
    public static final String SETTING_ENABLE_MATERIAL3 = "enable_material3_theme";
    
    /** 
     * Setting to enable DynamicColors.
     */
    public static final String SETTING_ENABLE_DYNAMIC_COLORS = "dynamic_colors";
    
    
    /**
     * Setting to use new xml command
     */
     
    public static final String SETTING_NEW_XML_COMMAND = "xml_command";
    
    public static final String SETTING_GENERIC_VALUE_TRUE = "true";
    public static final String SETTING_GENERIC_VALUE_FALSE = "false";
    private static final String TAG = "ProjectSettings";
    private final String path;
    public String sc_id;
    private HashMap<String, String> hashmap;

    public ProjectSettings(String scId) {
        sc_id = scId;

        path = getPath();

        if (FileUtil.isExistFile(path)) {
            try {
                hashmap = new Gson().fromJson(FileUtil.readFile(path).trim(), Helper.TYPE_STRING_MAP);
            } catch (Exception e) {
                Log.e("ProjectSettings", "Failed to read project settings for project " + sc_id + "!", e);
                hashmap = new HashMap<>();
                save();
            }
        } else {
            hashmap = new HashMap<>();
        }
    }

    /**
     * @return The configured minimum SDK version. Returns 21 if none or an invalid value was set.
     * @see #SETTING_MINIMUM_SDK_VERSION
     */
    public int getMinSdkVersion() {
        return Integer.parseInt(getValue(SETTING_MINIMUM_SDK_VERSION, "21"));
    }
    
    /**
     * @return The configured material3 choose. Returns false if none.
     * @see #SETTING_ENABLE_MATERIAL3
     */
    public boolean isMaterial3Enable() {
        return Boolean.parseBoolean(getValue(SETTING_ENABLE_MATERIAL3, "false"));
    }
    
    /**
     * @return The configured dynamic colors theme value. Returns false if none.
     * @see #SETTING_ENABLE_DYNAMIC_COLORS
     */
    public boolean isDynamicColorsEnable() {
        return Boolean.parseBoolean(getValue(SETTING_ENABLE_DYNAMIC_COLORS, "false"));
    }

    public String getPath() {
        return new File(Environment.getExternalStorageDirectory(), ".sketchware/data/" + sc_id + "/project_config").getAbsolutePath();
    }

    public String getValue(String key, String defaultValue) {
        if (hashmap.containsKey(key)) {
            if (!hashmap.get(key).isEmpty()) {
                return hashmap.get(key);
            } else {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    public void setValues(View... views) {
        for (View v : views) {
            if (v.getTag() != null) {
                String key = (String) v.getTag();  // v.getTag(0);
                //String value = (String) v.getTag(1);
                String value;

                if (v instanceof EditText editText) {
                    value = editText.getText().toString();
                } else if (v instanceof Checkable checkable) {
                    value = checkable.isChecked() ? "true" : "false";
                } else if (v instanceof RadioGroup radioGroup) {
                    value = getCheckedRbValue(radioGroup);
                } else {
                    continue;
                }

                hashmap.put(key, value);
            }
        }
        save();
    }

    public void setValue(String key, String value) {
        hashmap.put(key, value);
        save();
    }

    private String getCheckedRbValue(RadioGroup rg) {
        for (int i = 0; i < rg.getChildCount(); i++) {
            RadioButton rb = (RadioButton) rg.getChildAt(i);

            if (rb.isChecked()) {
                return rb.getText().toString();
            }
        }

        return "";
    }

    private void save() {
        FileUtil.writeFile(path, new Gson().toJson(hashmap));
    }
}

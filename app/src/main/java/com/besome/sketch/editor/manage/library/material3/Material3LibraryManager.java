package com.besome.sketch.editor.manage.library.material3;

import android.os.Environment;
import com.google.gson.Gson;

import java.io.File;

import a.a.a.jC;
import pro.sketchware.utility.FileUtil;

public class Material3LibraryManager {

    private final String sc_id;
    private final File configFile;
    private ConfigData configData;
    private final boolean isAppCompatEnabled;

    public Material3LibraryManager(String sc_id) {
        this.sc_id = sc_id;
        isAppCompatEnabled = jC.c(sc_id).c().isEnabled();
        this.configFile = getConfigPath();
        this.configData = loadOrCreateConfigData();
    }

    private File getConfigPath() {
        return new File(Environment.getExternalStorageDirectory(),
                ".sketchware" + File.separator + "data" + File.separator + sc_id + File.separator + "material_3.json");
    }

    private ConfigData loadOrCreateConfigData() {
        if (configFile.exists()) {
            String jsonData = FileUtil.readFileIfExist(configFile.getAbsolutePath());
            if (!jsonData.isEmpty()) {
                try {
                    return new Gson().fromJson(jsonData, ConfigData.class);
                } catch (Exception ignored) {
                }
            }
        }
        ConfigData defaultConfig = new ConfigData(false, false);
        saveConfigData(defaultConfig);
        return defaultConfig;
    }

    private void saveConfigData(ConfigData configData) {
        FileUtil.writeFile(configFile.getAbsolutePath(), new Gson().toJson(configData));
    }

    public ConfigData getConfigData() {
        return configData;
    }

    public void setConfigData(ConfigData configData) {
        this.configData = configData;
        saveConfigData(configData);
    }

    public boolean isMaterial3Enabled() {
        return isAppCompatEnabled && configData.isMaterial3Enabled();
    }

    public boolean isDynamicColorsEnabled() {
        return isAppCompatEnabled && configData.isMaterial3Enabled() && configData.isDynamicColorsEnabled();
    }

    public record ConfigData(boolean isMaterial3Enabled, boolean isDynamicColorsEnabled) {
    }
}
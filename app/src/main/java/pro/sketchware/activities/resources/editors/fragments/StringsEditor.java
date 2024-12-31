package pro.sketchware.activities.resources.editors.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import a.a.a.aB;
import mod.hey.studios.code.SrcCodeEditor;
import mod.hey.studios.code.SrcCodeEditorLegacy;
import mod.hey.studios.util.Helper;
import mod.hilal.saif.activities.tools.ConfigActivity;
import pro.sketchware.R;
import pro.sketchware.activities.resources.editors.ResourcesEditorActivity;
import pro.sketchware.databinding.ResourcesEditorFragmentBinding;
import pro.sketchware.databinding.ViewStringEditorAddBinding;
import pro.sketchware.utility.FileUtil;
import pro.sketchware.utility.SketchwareUtil;
import pro.sketchware.utility.XmlUtil;
import pro.sketchware.activities.resources.editors.adapters.StringsAdapter;

public class StringsEditor extends Fragment {

    private final ArrayList<HashMap<String, Object>> listmap = new ArrayList<>();
    private ResourcesEditorFragmentBinding binding;
    public StringsAdapter adapter;
    private boolean isComingFromSrcCodeEditor = true;
    public boolean isInitialized = false;
    public String filePath;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ResourcesEditorFragmentBinding.inflate(inflater, container, false);
        initialize();
        updateStringsList();
        return binding.getRoot();
    }

    private void initialize() {

        filePath = ((ResourcesEditorActivity) requireActivity()).stringsFilePath;
        isInitialized = true;

    }

    public void updateStringsList() {
        if (isComingFromSrcCodeEditor) {
            convertXmlToListMap(FileUtil.readFileIfExist(filePath), listmap);
            adapter = new StringsAdapter(((ResourcesEditorActivity) requireActivity()), listmap);
            binding.recyclerView.setAdapter(adapter);
            updateNoContentLayout();
        }
        isComingFromSrcCodeEditor = false;
    }

    public void updateNoContentLayout() {
        if (listmap.isEmpty()) {
            binding.noContentLayout.setVisibility(View.VISIBLE);
            binding.noContentTitle.setText(String.format(Helper.getResString(R.string.resource_manager_no_list_title), "Strings"));
            binding.noContentBody.setText(String.format(Helper.getResString(R.string.resource_manager_no_list_body), "string"));
        } else {
            binding.noContentLayout.setVisibility(View.GONE);
        }
    }

    public boolean checkForUnsavedChanges() {
        if (!FileUtil.isExistFile(filePath) && listmap.isEmpty()) {
            return false;
        }
        ArrayList<HashMap<String, Object>> cache = new ArrayList<>();
        convertXmlToListMap(FileUtil.readFileIfExist(filePath), cache);
        String cacheString = new Gson().toJson(cache);
        String cacheListmap = new Gson().toJson(listmap);
        return !cacheListmap.equals(cacheString) && !listmap.isEmpty();
    }

    public void handleOnOptionsItemSelected(@IdRes int id) {
        if (id == R.id.action_get_default) {
            convertXmlToListMap(FileUtil.readFileIfExist(getDefaultStringPath(Objects.requireNonNull(filePath))), listmap);
            adapter.notifyDataSetChanged();
        } else if (id == R.id.action_open_editor) {
            isComingFromSrcCodeEditor = true;
            XmlUtil.saveXml(filePath, convertListMapToXml(listmap));
            Intent intent = new Intent();
            intent.setClass(requireActivity(), ConfigActivity.isLegacyCeEnabled() ? SrcCodeEditorLegacy.class : SrcCodeEditor.class);
            intent.putExtra("title", "strings.xml");
            intent.putExtra("content", filePath);
            requireActivity().startActivity(intent);
        }
    }

    public static void convertXmlToListMap(final String xmlString, final ArrayList<HashMap<String, Object>> listmap) {
        try {
            listmap.clear();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream input = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
            Document doc = builder.parse(new InputSource(input));
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("string");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    HashMap<String, Object> map = getStringHashMap((Element) node);
                    listmap.add(map);
                }
            }
        } catch (Exception ignored) {}
    }

    private static HashMap<String, Object> getStringHashMap(Element node) {
        HashMap<String, Object> map = new HashMap<>();
        String key = node.getAttribute("name");
        String value = node.getTextContent();
        map.put("key", key);
        map.put("text", value);
        return map;
    }

    public static boolean isXmlStringsContains(ArrayList<HashMap<String, Object>> listMap, String value) {
        for (Map<String, Object> map : listMap) {
            if (map.containsKey("key") && value.equals(map.get("key"))) {
                return true;
            }
        }
        return false;
    }

    public static String convertListMapToXml(final ArrayList<HashMap<String, Object>> listmap) {
        StringBuilder xmlString = new StringBuilder();
        xmlString.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<resources>\n");
        for (HashMap<String, Object> map : listmap) {
            String key = (String) map.get("key");
            Object textObj = map.get("text");
            String text = textObj instanceof String ? (String) textObj : textObj.toString();
            String escapedText = escapeXml(text);
            xmlString.append("    <string name=\"").append(key).append("\"");
            if (map.containsKey("translatable")) {
                String translatable = (String) map.get("translatable");
                xmlString.append(" translatable=\"").append(translatable).append("\"");
            }
            xmlString.append(">").append(escapedText).append("</string>\n");
        }
        xmlString.append("</resources>");
        return xmlString.toString();
    }

    public static String escapeXml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;")
                .replace("\n", "&#10;")
                .replace("\r", "&#13;");
    }

    public void addStringDialog() {
        aB dialog = new aB(requireActivity());
        ViewStringEditorAddBinding binding = ViewStringEditorAddBinding.inflate(getLayoutInflater());
        dialog.b("Create new string");
        dialog.b("Create", v1 -> {
            String key = Objects.requireNonNull(binding.stringKeyInput.getText()).toString();
            String value = Objects.requireNonNull(binding.stringValueInput.getText()).toString();

            if (key.isEmpty() || value.isEmpty()) {
                SketchwareUtil.toast("Please fill in all fields", Toast.LENGTH_SHORT);
                return;
            }

            if (isXmlStringsContains(listmap, key)) {
                binding.stringKeyInputLayout.setError("\"" + key + "\" is already exist");
                return;
            }
            addString(key, value);
        });
        dialog.a(getString(R.string.cancel), v1 -> dialog.dismiss());
        dialog.a(binding.getRoot());
        dialog.show();
    }

    public void addString(final String key, final String text) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("key", key);
        map.put("text", text);
        if (listmap.isEmpty()) {
            listmap.add(map);
            adapter.notifyItemInserted(0);
            return;
        }
        for (int i = 0; i < listmap.size(); i++) {
            if (Objects.equals(listmap.get(i).get("key"), key)) {
                listmap.set(i, map);
                adapter.notifyItemChanged(i);
                return;
            }
        }
        listmap.add(map);
        adapter.notifyItemInserted(listmap.size() - 1);
        updateNoContentLayout();
    }

    public boolean checkDefaultString(final String path) {
        File file = new File(path);
        String parentFolder = Objects.requireNonNull(file.getParentFile()).getName();
        return parentFolder.equals("values");
    }

    public String getDefaultStringPath(final String path) {
        return path.replaceFirst("/values-[a-z]{2}", "/values");
    }

    public void saveStringsFile() {
        if (isInitialized) {
            XmlUtil.saveXml(filePath, StringsEditor.convertListMapToXml(listmap));
        }
    }
}

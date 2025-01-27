package pro.sketchware.activities.resources.editors.utils;

import static com.besome.sketch.design.DesignActivity.sc_id;
import static mod.hey.studios.util.ProjectFile.getDefaultColor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.besome.sketch.editor.manage.library.material3.Material3LibraryManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import a.a.a.lC;
import a.a.a.wq;
import a.a.a.yB;
import pro.sketchware.R;
import pro.sketchware.SketchApplication;
import pro.sketchware.activities.resources.editors.models.ColorModel;
import pro.sketchware.utility.FileUtil;
import pro.sketchware.utility.PropertiesUtil;
import pro.sketchware.utility.ThemeUtils;
import pro.sketchware.utility.XmlUtil;

@SuppressLint("DiscouragedApi")
public class ColorsEditorManager {

    public String contentPath;
    public boolean isDataLoadingFailed;
    public boolean isDefaultVariant = true;
    public boolean isNightVariant;
    public final String defaultHexColor = "#00000000";

    public HashMap<String, String> defaultColors;
    public HashMap<Integer, String> notesMap = new HashMap<>();

    private final Material3LibraryManager material3LibraryManager;

    public ColorsEditorManager() {
        material3LibraryManager = new Material3LibraryManager(sc_id);
    }

    public String getColorValue(Context context, String colorValue, int referencingLimit) {
        if (colorValue == null || referencingLimit <= 0) {
            return defaultHexColor;
        }

        if (colorValue.startsWith("#")) {
            return colorValue;
        }
        if (colorValue.startsWith("?attr/") || colorValue.startsWith("?")) {
            String attrName = colorValue.startsWith("?attr/") ? colorValue.substring(6) : colorValue.substring(1);
            return getColorValueFromAttrs(context, attrName, referencingLimit - 1);
        }
        if (colorValue.startsWith("@color/")) {
            return getColorValueFromXml(context, colorValue.substring(7), referencingLimit - 1);
        } else if (colorValue.startsWith("@android:color/")) {
            return getColorValueFromSystem(colorValue, context);
        }
        return defaultHexColor;
    }

    public String getColorValueFromAttrs(Context context, String attrName, int referencingLimit) {
        try {
            int attrId = context.getResources().getIdentifier(attrName, "attr", context.getPackageName());

            if (attrId != 0 && referencingLimit > 0) {
                Context themedContext;
                if (isNightVariant) {
                    if (material3LibraryManager.isDynamicColorsEnabled()) {
                        themedContext = new ContextThemeWrapper(context, R.style.ThemeOverlay_SketchwarePro_ViewEditor_Material3_Dark);
                    } else {
                        themedContext = new ContextThemeWrapper(context, R.style.ThemeOverlay_SketchwarePro_ViewEditor_Material3_NON_DYNAMIC_Dark);
                    }
                } else {
                    if (material3LibraryManager.isDynamicColorsEnabled()) {
                        themedContext = new ContextThemeWrapper(context, R.style.ThemeOverlay_SketchwarePro_ViewEditor_Material3_Light);
                    } else {
                        themedContext = new ContextThemeWrapper(context, R.style.ThemeOverlay_SketchwarePro_ViewEditor_Material3_NON_DYNAMIC_Light);
                    }
                }
                return String.format("#%06X", (0xFFFFFF & ThemeUtils.getColor(new View(themedContext), attrId)));
            }
        } catch (Exception ignored) {
        }
        return defaultHexColor;
    }

    private String getColorValueFromSystem(String colorValue, Context context) {
        String colorName = colorValue.substring(15);
        int colorId = context.getResources().getIdentifier(colorName, "color", "android");
        try {
            int colorInt = ContextCompat.getColor(context, colorId);
            return String.format("#%06X", (0xFFFFFF & colorInt));
        } catch (Exception e) {
            return defaultHexColor;
        }
    }

    private String getColorValueFromXml(Context context, String colorName, int referencingLimit) {
        String filePath = wq.b(sc_id) + "/files/resource/values/colors.xml";

        ArrayList<ColorModel> colorList = new ArrayList<>();

        parseColorsXML(colorList, FileUtil.readFileIfExist(filePath));
        for (ColorModel colorModel : colorList) {
            if (colorModel.getColorName().equals(colorName)) {
                return getColorValue(context, colorModel.getColorValue(), referencingLimit);
            }
        }
        return defaultHexColor;
    }

    public void parseColorsXML(ArrayList<ColorModel> colorList, String colorXml) {
        isDataLoadingFailed = false;
        ArrayList<String> foundPrimaryColors = new ArrayList<>();
        ArrayList<ColorModel> colorOrderList = new ArrayList<>();
        ArrayList<ColorModel> otherColors = new ArrayList<>();
        boolean hasChanges = false; // Flag to track changes

        try {
            colorList.clear();
            notesMap.clear();
            // Parse the XML using DocumentBuilder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(colorXml)));
            document.getDocumentElement().normalize();

            NodeList childNodes = document.getDocumentElement().getChildNodes();

            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);

                if (node.getNodeType() == Node.COMMENT_NODE) {
                    // Save comments in notesMap
                    notesMap.put(colorList.size(), node.getNodeValue().trim());
                } else if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("color")) {
                    Element element = (Element) node;
                    String colorName = element.getAttribute("name");
                    String colorValue = element.getTextContent().trim();

                    if (PropertiesUtil.isHexColor(getColorValue(SketchApplication.getContext(), colorValue, 4))) {
                        ColorModel colorModel = new ColorModel(colorName, colorValue);
                        colorList.add(colorModel);

                        if (defaultColors != null && defaultColors.containsKey(colorName)) {
                            foundPrimaryColors.add(colorName);
                            colorOrderList.add(colorModel);
                        } else {
                            otherColors.add(colorModel);
                        }
                    }
                }
            }

            if (isDefaultVariant && defaultColors != null && sc_id != null) {
                HashMap<String, Object> metadata = lC.b(sc_id);
                Set<String> missingKeys = new HashSet<>(defaultColors.keySet());
                foundPrimaryColors.forEach(missingKeys::remove);

                if (!missingKeys.isEmpty()) {
                    for (String missingColor : missingKeys) {
                        String colorHex = String.format("#%06X", yB.a(metadata, defaultColors.get(missingColor), getDefaultColor(defaultColors.get(missingColor))) & 0xffffff);
                        ColorModel missingColorModel = new ColorModel(missingColor, colorHex);
                        colorOrderList.add(missingColorModel);
                        hasChanges = true;
                    }
                }
            }

            // Reorder colors
            ArrayList<ColorModel> previousColorList = new ArrayList<>(colorList); // Save the original list for comparison
            colorList.clear();
            colorList.addAll(colorOrderList);
            colorList.addAll(otherColors);

            if (!previousColorList.equals(colorList)) {
                hasChanges = true;
            }

            // Save the updated XML if changes are detected
            if (hasChanges) {
                XmlUtil.saveXml(wq.b(sc_id) + "/files/resource/values/colors.xml", convertListToXml(colorList, notesMap));
            }

        } catch (Exception e) {
            isDataLoadingFailed = !colorXml.trim().isEmpty();
        }
    }

    public String convertListToXml(ArrayList<ColorModel> colorList, HashMap<Integer, String> notesMap) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<resources>\n");

        for (int i = 0; i < colorList.size(); i++) {
            if (notesMap.containsKey(i)) {
                xmlBuilder.append("    <!--").append(notesMap.get(i)).append("-->\n");
            }

            ColorModel colorModel = colorList.get(i);
            xmlBuilder.append("    <color name=\"").append(colorModel.getColorName()).append("\">")
                    .append(colorModel.getColorValue()).append("</color>\n");
        }

        xmlBuilder.append("</resources>");
        return xmlBuilder.toString();
    }

}

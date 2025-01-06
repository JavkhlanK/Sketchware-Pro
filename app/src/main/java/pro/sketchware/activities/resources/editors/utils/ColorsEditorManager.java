package pro.sketchware.activities.resources.editors.utils;

import static com.besome.sketch.design.DesignActivity.sc_id;
import static mod.hey.studios.util.ProjectFile.getDefaultColor;

import android.content.Context;
import android.util.Xml;

import androidx.core.content.ContextCompat;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import a.a.a.lC;
import a.a.a.wq;
import a.a.a.yB;
import pro.sketchware.SketchApplication;
import pro.sketchware.activities.resources.editors.models.ColorModel;
import pro.sketchware.utility.FileUtil;
import pro.sketchware.utility.PropertiesUtil;
import pro.sketchware.utility.XmlUtil;

public class ColorsEditorManager {

    public String contentPath;
    public boolean isDataLoadingFailed;
    public boolean isDefaultVariant = true;

    public HashMap<String, String> defaultColors;

    public String getColorValue(Context context, String colorValue, int referencingLimit) {
        if (colorValue == null || referencingLimit <= 0) {
            return null;
        }

        if (colorValue.startsWith("#")) {
            return colorValue;
        }
        if (colorValue.startsWith("?attr/")) {
            return getColorValueFromXml(context, colorValue.substring(6), referencingLimit - 1);
        }
        if (colorValue.startsWith("@color/")) {
            return getColorValueFromXml(context, colorValue.substring(7), referencingLimit - 1);

        } else if (colorValue.startsWith("@android:color/")) {
            return getColorValueFromSystem(colorValue, context);
        }
        return "#ffffff";
    }

    private String getColorValueFromSystem(String colorValue, Context context) {
        String colorName = colorValue.substring(15);
        int colorId = context.getResources().getIdentifier(colorName, "color", "android");
        try {
            int colorInt = ContextCompat.getColor(context, colorId);
            return String.format("#%06X", (0xFFFFFF & colorInt));
        } catch (Exception e) {
            return "#ffffff";
        }
    }

    private String getColorValueFromXml(Context context, String colorName, int referencingLimit) {
        try {
            String clrXml = FileUtil.readFileIfExist(contentPath);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(clrXml));
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && "color".equals(parser.getName())) {
                    String nameAttribute = parser.getAttributeValue(null, "name");
                    if (colorName.equals(nameAttribute)) {
                        String colorValue = parser.nextText().trim();
                        if (colorValue.startsWith("@")) {
                            return getColorValue(context, colorValue, referencingLimit - 1);
                        } else {
                            return colorValue;
                        }
                    }
                }
                eventType = parser.next();
            }

        } catch (Exception ignored) {}
        return null;
    }

    public void parseColorsXML(ArrayList<ColorModel> colorList, String colorXml) {
        isDataLoadingFailed = false;
        ArrayList<String> foundedPrimaryColors = new ArrayList<>();
        try {
            colorList.clear();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(colorXml));

            int eventType = parser.getEventType();
            String colorName = null;
            String colorValue = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if ("color".equals(tagName)) {
                            colorName = parser.getAttributeValue(null, "name");
                        }
                        break;
                    case XmlPullParser.TEXT:
                        colorValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if ("color".equals(tagName)) {
                            if ((colorName != null) && PropertiesUtil.isHexColor(getColorValue(SketchApplication.getContext(), colorValue, 4))) {
                                if (defaultColors != null && defaultColors.containsKey(colorName)) {
                                    foundedPrimaryColors.add(colorName);
                                }
                                colorList.add(new ColorModel(colorName, colorValue));
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
            if (isDefaultVariant && defaultColors != null && sc_id != null) {
                HashMap<String, Object> metadata = lC.b(sc_id);
                Set<String> missingKeys = new HashSet<>(defaultColors.keySet());
                foundedPrimaryColors.forEach(missingKeys::remove);
                if (!missingKeys.isEmpty()) {
                    ArrayList<String> keys = new ArrayList<>(defaultColors.keySet());
                    for (int i = 0; i < keys.size(); i++) {
                        String color = keys.get(i);
                        if (missingKeys.contains(color)) {
                            String colorHex = String.format("#%06X", yB.a(metadata, defaultColors.get(color), getDefaultColor(defaultColors.get(color))) & 0xffffff);
                            colorList.add(i, new ColorModel(color, colorHex));
                        }
                    }

                    XmlUtil.saveXml(wq.b(sc_id) + "/files/resource/values/colors.xml", convertListToXml(colorList));
                }
            }
        } catch (Exception ignored) {
            isDataLoadingFailed = !colorXml.trim().isEmpty();
        }
    }

    public String convertListToXml(ArrayList<ColorModel> colorList) {
        try {
            XmlSerializer xmlSerializer = Xml.newSerializer();
            StringWriter stringWriter = new StringWriter();

            xmlSerializer.setOutput(stringWriter);
            xmlSerializer.startTag(null, "resources");
            xmlSerializer.text("\n");

            for (ColorModel colorModel : colorList) {
                xmlSerializer.startTag(null, "color");
                xmlSerializer.attribute(null, "name", colorModel.getColorName());
                xmlSerializer.text(colorModel.getColorValue());
                xmlSerializer.endTag(null, "color");
                xmlSerializer.text("\n");
            }

            xmlSerializer.endTag(null, "resources");
            xmlSerializer.text("\n");
            xmlSerializer.endDocument();

            return stringWriter.toString();

        } catch (Exception ignored) {
        }
        return null;
    }

}

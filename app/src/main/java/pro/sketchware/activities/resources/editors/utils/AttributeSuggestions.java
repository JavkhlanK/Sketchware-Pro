package pro.sketchware.activities.resources.editors.utils;

import static com.besome.sketch.design.DesignActivity.sc_id;

import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import a.a.a.wq;

import pro.sketchware.activities.resources.editors.models.ColorModel;
import pro.sketchware.utility.FileUtil;

public class AttributeSuggestions {

    public final HashMap<String, SuggestionType> ATTRIBUTE_TYPES = new HashMap<>();
    public final ArrayList<String> ATTRIBUTE_SUGGESTIONS = new ArrayList<>();
    private final HashMap<SuggestionType, List<String>> SUGGESTIONS = new HashMap<>();

    public enum SuggestionType {
        BOOLEAN, DIMENSION, COLOR, TEXT, NUMBER, NUMBER_0_1, DRAWABLE, FONT
    }

    private final View view;

    public AttributeSuggestions(View view) {
        this.view = view;

        ATTRIBUTE_SUGGESTIONS.add("android:text");
        ATTRIBUTE_SUGGESTIONS.add("android:hint");
        ATTRIBUTE_SUGGESTIONS.add("android:showAsAction");

        ATTRIBUTE_SUGGESTIONS.add("android:textAllCaps");

        ATTRIBUTE_SUGGESTIONS.add("android:alpha");

        ATTRIBUTE_SUGGESTIONS.add("android:textColor");
        ATTRIBUTE_SUGGESTIONS.add("android:statusBarColor");
        ATTRIBUTE_SUGGESTIONS.add("android:navigationBarColor");

        ATTRIBUTE_SUGGESTIONS.add("android:textSize");
        ATTRIBUTE_SUGGESTIONS.add("android:lineSpacingExtra");
        ATTRIBUTE_SUGGESTIONS.add("android:elevation");
        ATTRIBUTE_SUGGESTIONS.add("android:letterSpacing");

        ATTRIBUTE_SUGGESTIONS.add("android:fontFamily");

        ATTRIBUTE_SUGGESTIONS.add("android:background");
        ATTRIBUTE_SUGGESTIONS.add("android:windowBackground");

        // Adding broader keys for general types
        ATTRIBUTE_TYPES.put("text", SuggestionType.TEXT);
        ATTRIBUTE_TYPES.put("hint", SuggestionType.TEXT);

        ATTRIBUTE_TYPES.put("indeterminateOnly", SuggestionType.BOOLEAN);
        ATTRIBUTE_TYPES.put("enabled", SuggestionType.BOOLEAN);
        ATTRIBUTE_TYPES.put("checked", SuggestionType.BOOLEAN);
        ATTRIBUTE_TYPES.put("focusable", SuggestionType.BOOLEAN);
        ATTRIBUTE_TYPES.put("visibility", SuggestionType.BOOLEAN);

        ATTRIBUTE_TYPES.put("alpha", SuggestionType.NUMBER_0_1);
        ATTRIBUTE_TYPES.put("scale", SuggestionType.NUMBER_0_1);
        ATTRIBUTE_TYPES.put("translation", SuggestionType.NUMBER_0_1);

        ATTRIBUTE_TYPES.put("lines", SuggestionType.NUMBER);

        ATTRIBUTE_TYPES.put("fontFamily", SuggestionType.FONT);

        ATTRIBUTE_TYPES.put("size", SuggestionType.DIMENSION);
        ATTRIBUTE_TYPES.put("height", SuggestionType.DIMENSION);
        ATTRIBUTE_TYPES.put("width", SuggestionType.DIMENSION);
        ATTRIBUTE_TYPES.put("padding", SuggestionType.DIMENSION);
        ATTRIBUTE_TYPES.put("margin", SuggestionType.DIMENSION);

        ATTRIBUTE_TYPES.put("color", SuggestionType.COLOR);
        ATTRIBUTE_TYPES.put("drawable", SuggestionType.DRAWABLE);
        ATTRIBUTE_TYPES.put("background", SuggestionType.DRAWABLE);

        // Initializing the suggestions for each type
        SUGGESTIONS.put(SuggestionType.TEXT, generateTextsSuggestions());
        SUGGESTIONS.put(SuggestionType.BOOLEAN, Arrays.asList("true", "false"));
        SUGGESTIONS.put(SuggestionType.DIMENSION, generateDimensionSuggestions());
        SUGGESTIONS.put(SuggestionType.COLOR, generateColorsSuggestions());
        SUGGESTIONS.put(SuggestionType.NUMBER, generateNumberSuggestions(1, 10));
        SUGGESTIONS.put(SuggestionType.NUMBER_0_1, generateNumberSuggestions(0.1F, 1));
        SUGGESTIONS.put(SuggestionType.DRAWABLE, generateDrawableSuggestions());
        SUGGESTIONS.put(SuggestionType.FONT, Arrays.asList("sans-serif", "serif", "monospace"));
    }

    public List<String> getSuggestions(String attr) {
        for (String currentAttr : ATTRIBUTE_TYPES.keySet()) {
            if (attr.contains(currentAttr.toLowerCase())) {
                return SUGGESTIONS.get(ATTRIBUTE_TYPES.get(currentAttr));
            }
        }
        return new ArrayList<>();
    }

    private List<String> generateDimensionSuggestions() {
        List<String> suggestions = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            suggestions.add(i + "dp");
        }
        return suggestions;
    }

    private List<String> generateNumberSuggestions(float step, float max) {
        List<String> suggestions = new ArrayList<>();
        for (float i = step; i <= max; i+= step) {
            if (i == Math.floor(i)) {
                suggestions.add(String.format(Locale.US, "%.0f", i));
            } else {
                suggestions.add(String.format(Locale.US, "%.1f", i));
            }
        }
        return suggestions;
    }

    private List<String> generateTextsSuggestions() {
        if (sc_id == null) {
            return new ArrayList<>();
        }
        String filePath = wq.b(sc_id) + "/files/resource/values/strings.xml";

        ArrayList<HashMap<String, Object>> StringsListMap = new ArrayList<>();

        new StringsEditorManager().convertXmlStringsToListMap(FileUtil.readFileIfExist(filePath), StringsListMap);

        return StringsListMap.stream()
                .map(stringMap -> "@string/" + stringMap.get("key"))
                .collect(Collectors.toList());
    }

    private List<String> generateColorsSuggestions() {
        if (sc_id == null) {
            return new ArrayList<>();
        }
        String filePath = wq.b(sc_id) + "/files/resource/values/colors.xml";

        ArrayList<ColorModel> colorList = new ArrayList<>();

        new ColorsEditorManager().parseColorsXML(colorList, FileUtil.readFileIfExist(filePath));

        return colorList.stream().map(colorModel -> "@color/" + colorModel.getColorName()).collect(Collectors.toList());

    }

    private List<String> generateDrawableSuggestions() {
        if (sc_id == null) {
            return new ArrayList<>();
        }
        String path = wq.b(sc_id) + "/files/resource/drawable/";
        return FileUtil.listFiles(path, ".xml")
                .stream()
                .map(file -> "@drawable/" + file.substring(path.length()))
                .collect(Collectors.toList());
    }
}

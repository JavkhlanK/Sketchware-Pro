 package pro.sketchware.activities.resources.editors.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import a.a.a.lC;
import a.a.a.wq;
import a.a.a.yB;
import pro.sketchware.activities.resources.editors.ResourcesEditorActivity;
import pro.sketchware.utility.XmlUtil;

public class StringsEditorManager {

    public boolean isDefaultVariant = true;
    public boolean isDataLoadingFailed;
    public boolean hasAppNameKey;
    public String sc_id;

    public HashMap<Integer, String> notesMap = new HashMap<>();

    public void convertXmlStringsToListMap(final String xmlString, final ArrayList<HashMap<String, Object>> listMap) {
        isDataLoadingFailed = false;
        hasAppNameKey = false;
        try {
            listMap.clear();
            notesMap.clear(); // Clear notes map at the beginning
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            ByteArrayInputStream input = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
            Document doc = builder.parse(new InputSource(input));
            doc.getDocumentElement().normalize();
            NodeList childNodes = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node node = childNodes.item(i);
                if (node.getNodeType() == Node.COMMENT_NODE) {
                    // Save comments in notesMap
                    notesMap.put(listMap.size(), node.getNodeValue().trim());
                } else if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("string")) {
                    addToListMap(listMap, (Element) node);
                }
            }
            if (isDefaultVariant && !hasAppNameKey) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("key", "app_name");
                map.put("text", yB.c(lC.b(sc_id), "my_app_name"));
                listMap.add(0, map);
                XmlUtil.saveXml(wq.b(sc_id) + "/files/resource/values/strings.xml", convertListMapToXmlStrings(listMap, notesMap));
            }
        } catch (Exception ignored) {
            isDataLoadingFailed = !xmlString.trim().isEmpty();
        }
    }

    private void addToListMap(ArrayList<HashMap<String, Object>> list, Element node) {
        HashMap<String, Object> map = new HashMap<>();
        String key = node.getAttribute("name");
        String value = node.getTextContent().replace("\\", "");
        map.put("key", key);
        map.put("text", value);
        if (key.equals("app_name")) {
            hasAppNameKey = true;
            list.add(0, map);
        } else {
            list.add(map);
        }
    }

    public boolean isXmlStringsExist(ArrayList<HashMap<String, Object>> listMap, String value) {
        for (Map<String, Object> map : listMap) {
            if (map.containsKey("key") && value.equals(map.get("key"))) {
                return true;
            }
        }
        return false;
    }

    public String convertListMapToXmlStrings(final ArrayList<HashMap<String, Object>> listMap, HashMap<Integer, String> notesMap) {
        StringBuilder xmlString = new StringBuilder();
        xmlString.append("<resources>\n");
        for (int i = 0; i < listMap.size(); i++) {
            if (notesMap.containsKey(i)) {
                xmlString.append("    <!-- ").append(notesMap.get(i)).append(" -->\n");
            }
            HashMap<String, Object> map = listMap.get(i);
            String key = (String) map.get("key");
            Object textObj = map.get("text");
            assert textObj != null;
            String text = textObj instanceof String ? (String) textObj : textObj.toString();
            String escapedText = ResourcesEditorActivity.escapeXml(text);
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

}

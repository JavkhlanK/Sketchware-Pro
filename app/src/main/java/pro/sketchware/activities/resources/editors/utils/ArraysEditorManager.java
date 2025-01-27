package pro.sketchware.activities.resources.editors.utils;

import androidx.annotation.NonNull;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import pro.sketchware.activities.resources.editors.ResourcesEditorActivity;
import pro.sketchware.activities.resources.editors.fragments.ArraysEditor.ARRAYS_TYPES;
import pro.sketchware.activities.resources.editors.models.ArrayModel;

public class ArraysEditorManager {

    public boolean isDataLoadingFailed;
    public LinkedHashMap<Integer, String> notesMap = new LinkedHashMap<>();

    public String convertArraysToXML(ArrayList<ArrayModel> arraysList, HashMap<Integer, String> notesMap) {
        StringBuilder xml = new StringBuilder();
        xml.append("<resources>\n");

        int arrayIndex = 0;
        for (ArrayModel array : arraysList) {
            if (notesMap.containsKey(arrayIndex)) {
                xml.append("    <!--").append(notesMap.get(arrayIndex)).append("-->\n");
            }

            String arrayTag = getArrayTag(array.getArrayType());
            xml.append("    <").append(arrayTag).append(" name=\"").append(array.getArrayName()).append("\">\n");

            int itemIndex = 0;
            for (Map.Entry<String, String> entry : array.getAttributes().entrySet()) {
                String itemName = entry.getKey().isEmpty() ? "item" + itemIndex : entry.getKey();
                xml.append("        <item name=\"").append(itemName).append("\">")
                        .append(ResourcesEditorActivity.escapeXml(entry.getValue())).append("</item>\n");
                itemIndex++;
            }

            xml.append("    </").append(arrayTag).append(">\n");
            arrayIndex++;
        }

        xml.append("</resources>");
        return xml.toString();
    }

    private String getArrayTag(ARRAYS_TYPES arraysType) {
        return switch (arraysType) {
            case STRING -> "string-array";
            case INTEGER -> "integer-array";
            case OBJECT -> "array";
        };
    }

    public ArrayList<ArrayModel> parseArraysFile(String content) {
        isDataLoadingFailed = false;
        ArrayList<ArrayModel> arrays = new ArrayList<>();
        notesMap.clear();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(content));
            Document document = builder.parse(inputSource);
            document.getDocumentElement().normalize();

            NodeList allNodes = document.getDocumentElement().getChildNodes();
            for (int i = 0; i < allNodes.getLength(); i++) {
                Node node = allNodes.item(i);

                if (node.getNodeType() == Node.COMMENT_NODE) {
                    Comment comment = (Comment) node;
                    String commentText = comment.getTextContent().trim();
                    notesMap.put(arrays.size(), commentText);
                } else if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().endsWith("array")) {
                    Element element = (Element) node;
                    String arrayName = element.getAttribute("name");
                    ARRAYS_TYPES arrayType = getArrayType(node.getNodeName());

                    LinkedHashMap<String, String> attributes = attributes(element);

                    arrays.add(new ArrayModel(arrayName, arrayType, attributes));
                }
            }

        } catch (Exception ignored) {
            isDataLoadingFailed = !content.trim().isEmpty();
        }

        return arrays;
    }

    private static @NonNull LinkedHashMap<String, String> attributes(Element element) {
        LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
        NodeList childNodes = element.getChildNodes();
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node childNode = childNodes.item(j);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                Element childElement = (Element) childNode;
                String attrName = childElement.getAttribute("name");
                String attrValue = childElement.getTextContent().trim();
                if (attrName.isEmpty()) {
                    attrName = "item" + (attributes.keySet().size() + 1);
                }
                attributes.put(attrName, attrValue);
            }
        }
        return attributes;
    }

    private ARRAYS_TYPES getArrayType(String nodeName) {
        return switch (nodeName) {
            case "string-array" -> ARRAYS_TYPES.STRING;
            case "integer-array" -> ARRAYS_TYPES.INTEGER;
            default -> ARRAYS_TYPES.OBJECT;
        };
    }
}
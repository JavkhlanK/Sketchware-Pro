package pro.sketchware.activities.resources.editors.models;

import java.util.LinkedHashMap;

public class StyleModel {

    private String styleName;
    private String parent;
    private LinkedHashMap<String, String> attributes;

    public StyleModel(String styleName, String parent, LinkedHashMap<String, String> attributes) {
        this.styleName = styleName;
        this.parent = parent;
        this.attributes = attributes;
    }

    public StyleModel(String styleName, String parent) {
        this.styleName = styleName;
        this.parent = parent;
        this.attributes = new LinkedHashMap<>();
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public String getParent() {
        return parent == null ? "" : parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public LinkedHashMap<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(LinkedHashMap<String, String> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(String attrName, String attrValue) {
        if (attributes == null) {
            attributes = new LinkedHashMap<>();
        }
        attributes.put(attrName, attrValue);
    }

    public String getAttribute(String attrName) {
        if (attributes != null) {
            return attributes.get(attrName);
        }
        return null;
    }
}

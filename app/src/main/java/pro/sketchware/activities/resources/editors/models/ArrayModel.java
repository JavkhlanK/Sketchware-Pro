package pro.sketchware.activities.resources.editors.models;

import java.util.LinkedHashMap;

import pro.sketchware.activities.resources.editors.fragments.ArraysEditor.ARRAYS_TYPES;

public class ArrayModel {

    private String arrayName;
    private ARRAYS_TYPES arrayType;
    private LinkedHashMap<String, String> attributes;

    public ArrayModel(String arrayName, ARRAYS_TYPES arrayType, LinkedHashMap<String, String> attributes) {
        this.arrayName = arrayName;
        this.arrayType = arrayType;
        this.attributes = attributes;
    }

    public ArrayModel(String arrayName, ARRAYS_TYPES arrayType) {
        this.arrayName = arrayName;
        this.arrayType = arrayType;
        this.attributes = new LinkedHashMap<>();
    }

    public String getArrayName() {
        return arrayName;
    }

    public void setArrayName(String arrayName) {
        this.arrayName = arrayName;
    }

    public ARRAYS_TYPES getArrayType() {
        return arrayType;
    }

    public void setArrayType(ARRAYS_TYPES arrayType) {
        this.arrayType = arrayType;
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
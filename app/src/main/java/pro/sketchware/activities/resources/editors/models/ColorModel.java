package pro.sketchware.activities.resources.editors.models;

public class ColorModel {
    private String colorName;
    private String colorValue;

    public ColorModel(String colorName, String colorValue) {
        this.colorName = colorName;
        this.colorValue = colorValue;
    }

    public String getColorName() {
        return colorName;
    }

    public String getColorValue() {
        return colorValue;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }

    public void setColorValue(String colorValue) {
        this.colorValue = colorValue;
    }
}

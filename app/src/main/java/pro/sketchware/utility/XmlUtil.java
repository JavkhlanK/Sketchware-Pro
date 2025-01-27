package pro.sketchware.utility;

public class XmlUtil {
    public static String replaceXml(final String text) {
        return text.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "")
                .replace("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>", "")
                .replace("\r", "")
                .replace("\n", "")
                .replace(" ", "")
                .replace("\t", "");
    }

    public static void saveXml(String path, String xml) {
        FileUtil.writeFile(path, xml);
    }

}

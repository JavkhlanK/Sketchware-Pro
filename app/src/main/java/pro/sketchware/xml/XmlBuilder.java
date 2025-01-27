package pro.sketchware.xml;

import java.util.ArrayList;

import a.a.a.Jx;

public class XmlBuilder {

    public String a;
    public int b;
    public String c;
    public boolean d;
    public ArrayList<AttributeBuilder> e;
    public ArrayList<XmlBuilder> f;
    public String g;

    public XmlBuilder(String str) {
        this(str, false);
    }

    public XmlBuilder(String str, boolean z) {
        d = z;
        a = str;
        b = 0;
        e = new ArrayList<>();
        f = new ArrayList<>();
    }

    private String addZeroIndent() {
        return addIndent(0);
    }

    private String addIndent(int indentSize) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < b + indentSize; i++) {
            str.append("\t");
        }
        return str.toString();
    }

    public void addNamespaceDeclaration(int position, String namespace, String attr, String value) {
        e.add(position, new AttributeBuilder(namespace, attr, value));
    }

    public void a(XmlBuilder xmlBuilder) {
        xmlBuilder.b(b + 1);
        f.add(xmlBuilder);
    }

    public void a(String str) {
        c = str;
    }

    public void addAttribute(String namespace, String attr, String value) {
        e.add(new AttributeBuilder(namespace, attr, value));
    }

    public void addAttributeValue(String value) {
        e.add(new AttributeBuilder(value));
    }

    public String toCode() {
        StringBuilder resultCode = new StringBuilder();

        resultCode.append(addZeroIndent());
        resultCode.append("<");
        resultCode.append(a);

        for (AttributeBuilder attr : e) {
            if (e.size() <= 1 || d) {
                resultCode.append(" ");
            } else {
                resultCode.append("\r\n");
                resultCode.append(addIndent(1));
                g = "\r\n" + addIndent(1);
            }
            resultCode.append(attr.toCode());
        }

        if (f.size() <= 0) {
            if (c == null || c.length() <= 0) {
                resultCode.append(" />");
            } else {
                resultCode.append(">");
                resultCode.append(c);
                resultCode.append("</");
                resultCode.append(a);
                resultCode.append(">");
            }
        } else {
            resultCode.append(">");
            resultCode.append("\r\n");
            for (XmlBuilder xmlBuilder : f) {
                resultCode.append(xmlBuilder.toCode());
            }
            resultCode.append(addZeroIndent());
            resultCode.append("</");
            resultCode.append(a);
            resultCode.append(">");
        }

        resultCode.append("\r\n");
        return resultCode.toString();
    }

    public String c() {
        return Jx.WIDGET_NAME_PATTERN.matcher(a).replaceAll("");
    }

    private void b(int indentSize) {
        b = indentSize;
        if (f != null) {
            for (XmlBuilder nx : f) {
                nx.b(indentSize + 1);
            }
        }
    }

    class AttributeBuilder {

        private final String value;
        private String namespace;
        private String attr;

        private AttributeBuilder(String namespace, String attr, String value) {
            this.namespace = namespace;
            this.attr = attr;
            this.value = value;
        }

        private AttributeBuilder(String value) {
            this.value = value;
        }

        private String toCode() {
            if (namespace != null && !namespace.isEmpty()) {
                return namespace + ":" + attr + "=" + "\"" + value + "\"";
            } else if (attr == null || attr.length() <= 0) {
                return value.replaceAll("\n", g);
            } else {
                return attr + "=" + "\"" + value + "\"";
            }
        }
    }
}
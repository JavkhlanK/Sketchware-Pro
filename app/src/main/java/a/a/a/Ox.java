package a.a.a;

import static com.besome.sketch.editor.property.PropertyAttributesItem.RELATIVE_IDS;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.besome.sketch.beans.ImageBean;
import com.besome.sketch.beans.LayoutBean;
import com.besome.sketch.beans.ProjectFileBean;
import com.besome.sketch.beans.TextBean;
import com.besome.sketch.beans.ViewBean;

import dev.aldi.sayuti.editor.injection.AppCompatInjection;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import mod.agus.jcoderz.beans.ViewBeans;
import mod.jbk.util.LogUtil;

import pro.sketchware.managers.inject.InjectRootLayoutManager;
import pro.sketchware.utility.InjectAttributeHandler;
import pro.sketchware.xml.XmlBuilder;

@SuppressLint("RtlHardcoded")
public class Ox {

    private final jq buildConfig;
    private final InjectRootLayoutManager rootManager;
    private final AppCompatInjection aci;
    private final ProjectFileBean projectFile;
    private ViewBean fab;
    private ArrayList<ViewBean> views;
    private XmlBuilder rootLayout = null;
    private XmlBuilder collapsingToolbarLayout = null;
    private boolean excludeAppCompat;

    public Ox(jq jq, ProjectFileBean projectFileBean) {
        buildConfig = jq;
        projectFile = projectFileBean;
        rootManager = new InjectRootLayoutManager(jq.sc_id);
        aci = new AppCompatInjection(jq, projectFileBean);
    }

    /**
     * @return The parameter String escaped properly for XML strings
     */
    private String escapeXML(String str) {
        CharBuffer buffer = CharBuffer.wrap(str);
        StringBuilder result = new StringBuilder(str.length());
        while (buffer.hasRemaining()) {
            char got = buffer.get();
            switch (got) {
                case '?' -> result.append("\\?");
                case '@' -> result.append("\\@");
                case '\"' -> result.append("&quot;");
                case '&' -> result.append("&amp;");
                case '<' -> result.append("&lt;");
                case '>' -> result.append("&gt;");
                case '\n' -> result.append("\\n");
                default -> result.append(got);
            }
        }
        return result.toString();
    }

    public void setExcludeAppCompat(boolean exclude) {
        excludeAppCompat = exclude;
    }

    private void writeRootLayout() {
        var root = rootManager.getLayoutByName(projectFile.getXmlName());
        XmlBuilder nx = new XmlBuilder(root.getClassName());
        var rootAttributes = root.getAttributes();
        if (!rootAttributes.containsKey("android:layout_width")) {
            nx.addAttribute("android", "layout_width", "match_parent");
        }
        if (!rootAttributes.containsKey("android:layout_height")) {
            nx.addAttribute("android", "layout_height", "match_parent");
        }
        for (Map.Entry<String, String> entry : rootAttributes.entrySet()) {
            nx.addAttribute(null, entry.getKey(), entry.getValue());
        }
        for (ViewBean viewBean : views) {
            String parent = viewBean.parent;
            if (parent == null || parent.isEmpty() || parent.equals("root")) {
                writeWidget(nx, viewBean);
            }
        }
        if (!excludeAppCompat && buildConfig.g) {
            if (projectFile.fileType == ProjectFileBean.PROJECT_FILE_TYPE_ACTIVITY) {
                if (projectFile.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_TOOLBAR)) {
                    if (!root.getAttributes().containsKey("app:layout_behavior")) {
                        nx.addAttribute("app", "layout_behavior", "@string/appbar_scrolling_view_behavior");
                    }
                }
                if (projectFile.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_TOOLBAR)
                        || projectFile.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_FAB)) {
                    XmlBuilder coordinatorLayoutTag = new XmlBuilder("androidx.coordinatorlayout.widget.CoordinatorLayout");
                    coordinatorLayoutTag.addAttribute("android", "id", "@+id/_coordinator");
                    aci.inject(coordinatorLayoutTag, "CoordinatorLayout");
                    rootLayout = coordinatorLayoutTag;
                }
                if (projectFile.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_TOOLBAR)) {
                    XmlBuilder toolbarTag = new XmlBuilder("androidx.appcompat.widget.Toolbar");
                    toolbarTag.addAttribute("android", "id", "@+id/_toolbar");
                    aci.inject(toolbarTag, "Toolbar");
                    XmlBuilder appBarLayoutTag = new XmlBuilder("com.google.android.material.appbar.AppBarLayout");
                    appBarLayoutTag.addAttribute("android", "id", "@+id/_app_bar");
                    aci.inject(appBarLayoutTag, "AppBarLayout");
                    if (collapsingToolbarLayout != null) {
                        collapsingToolbarLayout.a(toolbarTag);
                        appBarLayoutTag.a(collapsingToolbarLayout);
                    } else {
                        appBarLayoutTag.a(toolbarTag);
                    }
                    rootLayout.a(appBarLayoutTag);
                    rootLayout.a(nx);
                } else {
                    if (rootLayout == null) {
                        rootLayout = nx;
                    } else {
                        rootLayout.a(nx);
                    }
                }
                if (projectFile.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_FAB)) {
                    writeFabView(rootLayout, fab);
                }
                if (fab.type == ViewBeans.VIEW_TYPE_LAYOUT_BOTTOMNAVIGATIONVIEW) {
                    writeWidget(rootLayout, fab);
                }
                if (projectFile.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_DRAWER)) {
                    XmlBuilder drawerLayoutTag = new XmlBuilder("androidx.drawerlayout.widget.DrawerLayout");
                    drawerLayoutTag.addAttribute("android", "id", "@+id/_drawer");
                    aci.inject(drawerLayoutTag, "DrawerLayout");
                    drawerLayoutTag.a(rootLayout);
                    XmlBuilder linearLayoutTag = new XmlBuilder("LinearLayout");
                    linearLayoutTag.addAttribute("android", "id", "@+id/_nav_view");
                    aci.inject(linearLayoutTag, "NavigationDrawer");
                    XmlBuilder includeTag = new XmlBuilder("include", true);
                    includeTag.addAttribute("", "layout", "@layout/_drawer_" + projectFile.fileName);
                    includeTag.addAttribute("android", "id", "@+id/drawer");
                    linearLayoutTag.a(includeTag);
                    drawerLayoutTag.a(linearLayoutTag);
                    rootLayout = drawerLayoutTag;
                }
            } else {
                rootLayout = nx;
            }
        } else {
            rootLayout = nx;
        }
        rootLayout.addNamespaceDeclaration(0, "xmlns", "tools", "http://schemas.android.com/tools");
        rootLayout.addNamespaceDeclaration(0, "xmlns", "app", "http://schemas.android.com/apk/res-auto");
        rootLayout.addNamespaceDeclaration(0, "xmlns", "android", "http://schemas.android.com/apk/res/android");
    }

    public static String formatColor(int color) {
        int alpha = (color >> 24) & 0xff;

        if (alpha != 0xff) {
            return String.format("#%06X", color);
        } else {
            return String.format("#%08X", color);
        }
    }

    private void writeBackgroundResource(XmlBuilder nx, ViewBean viewBean) {
        var injectHandler = new InjectAttributeHandler(viewBean);
        Set<String> toNotAdd = readAttributesToReplace(viewBean);
        String backgroundResource = viewBean.layout.backgroundResource;
        if (backgroundResource == null || "NONE".equalsIgnoreCase(backgroundResource)) {
            int backgroundColor = viewBean.layout.backgroundColor;
            String backgroundResColor = viewBean.layout.backgroundResColor;

            if (backgroundColor != 0xffffff) {
                if (backgroundColor != 0) {
                    int color = backgroundColor & 0xffffff;
                    if (nx.c().equals("BottomAppBar")) {
                        if (!toNotAdd.contains("app:backgroundTint") && !injectHandler.contains("backgroundTint") && (backgroundResColor != null)) {
                            nx.addAttribute("app", "backgroundTint", backgroundResColor);
                        } else if (!toNotAdd.contains("app:backgroundTint") && !injectHandler.contains("backgroundTint")) {
                            nx.addAttribute("app", "backgroundTint", formatColor(color));
                        }
                    } else if (nx.c().equals("CardView") || nx.c().equals("MaterialCardView")) {
                        if (!toNotAdd.contains("app:cardBackgroundColor") && !injectHandler.contains("cardBackgroundColor") && backgroundResColor != null) {
                            nx.addAttribute("app", "cardBackgroundColor", backgroundResColor);
                        } else if (!toNotAdd.contains("app:cardBackgroundColor") && !injectHandler.contains("cardBackgroundColor")) {
                            nx.addAttribute("app", "cardBackgroundColor", formatColor(color));
                        }
                    } else if (nx.c().equals("CollapsingToolbarLayout")) {
                        if (!toNotAdd.contains("app:contentScrim") && !injectHandler.contains("contentScrim") && backgroundResColor != null) {
                            nx.addAttribute("app", "contentScrim", backgroundResColor);
                        } else if (!toNotAdd.contains("app:contentScrim") && !injectHandler.contains("contentScrim")) {
                            nx.addAttribute("app", "contentScrim", formatColor(color));
                        }
                    } else {
                        if (!hasAttr("background", viewBean) && !toNotAdd.contains("android:background") && !injectHandler.contains("background") && backgroundResColor != null) {
                            nx.addAttribute("android", "background", backgroundResColor);
                        } else if (!hasAttr("background", viewBean) && !toNotAdd.contains("android:background") && !injectHandler.contains("background")) {
                            nx.addAttribute("android", "background", formatColor(color));
                        }
                    }
                } else if (nx.c().equals("BottomAppBar")) {
                    if (!toNotAdd.contains("android:backgroundTint") && !injectHandler.contains("backgroundTint")) {
                        nx.addAttribute("android", "backgroundTint", "@android:color/transparent");
                    }
                } else if (nx.c().equals("CollapsingToolbarLayout")) {
                    if (!toNotAdd.contains("app:contentScrim") && !injectHandler.contains("contentScrim")) {
                        nx.addAttribute("app", "contentScrim", "?attr/colorPrimary");
                    }
                } else if (nx.c().equals("CardView") || nx.c().equals("MaterialCardView")) {
                    if (!toNotAdd.contains("app:cardBackgroundColor") && !injectHandler.contains("cardBackgroundColor")) {
                        nx.addAttribute("app", "cardBackgroundColor", "@android:color/transparent");
                    }
                } else {
                    if (!toNotAdd.contains("android:background") && !injectHandler.contains("background")) {
                        nx.addAttribute("android", "background", "@android:color/transparent");
                    }
                }
            }
        } else {
            if (!hasAttr("background", viewBean) && !toNotAdd.contains("android:background") && !injectHandler.contains("background")) {
                boolean isNinePatchBackground = backgroundResource.endsWith(".9");
                nx.addAttribute("android", "background", "@drawable/" +
                        (isNinePatchBackground ? backgroundResource.replaceAll("\\.9", "") :
                                backgroundResource));
            }
        }
    }

    public void a(ArrayList<ViewBean> arrayList) {
        a(arrayList, null);
    }

    public void a(ArrayList<ViewBean> arrayList, ViewBean viewBean) {
        fab = viewBean;
        views = arrayList;
        writeRootLayout();
    }

    public String b() {
        return rootLayout.toCode();
    }

    private void writeWidget(XmlBuilder nx, ViewBean viewBean) {
        viewBean.getClassInfo().a();
        String convert = viewBean.convert;
        var injectHandler = new InjectAttributeHandler(viewBean);
        Set<String> toNotAdd = readAttributesToReplace(viewBean);

        XmlBuilder widgetTag = convert.isEmpty() ? new XmlBuilder(viewBean.getClassInfo().a()) :
                new XmlBuilder(convert.replaceAll(" ", ""));
        if (convert.equals("include")) {
            if (!toNotAdd.contains("layout") && !injectHandler.contains("layout")) {
                widgetTag.addAttribute("", "layout", "@layout/" + viewBean.id);
            }
        } else {
            if (!toNotAdd.contains("android:id")) {
                widgetTag.addAttribute("android", "id", "@+id/" + viewBean.id);
            }
            int type = viewBean.type;
            if (projectFile.fileType == ProjectFileBean.PROJECT_FILE_TYPE_CUSTOM_VIEW) {
                switch (type) {
                    case ViewBean.VIEW_TYPE_WIDGET_TEXTVIEW:
                    case ViewBean.VIEW_TYPE_WIDGET_EDITTEXT:
                    case ViewBean.VIEW_TYPE_WIDGET_IMAGEVIEW:
                    case ViewBean.VIEW_TYPE_WIDGET_PROGRESSBAR:
                    case ViewBean.VIEW_TYPE_WIDGET_CHECKBOX:
                    case ViewBean.VIEW_TYPE_WIDGET_SWITCH:
                    case ViewBean.VIEW_TYPE_WIDGET_SEEKBAR:
                    case ViewBean.VIEW_TYPE_WIDGET_CALENDARVIEW:
                    case ViewBeans.VIEW_TYPE_WIDGET_RADIOBUTTON:
                    case ViewBeans.VIEW_TYPE_WIDGET_SEARCHVIEW:
                    case ViewBeans.VIEW_TYPE_WIDGET_AUTOCOMPLETETEXTVIEW:
                    case ViewBeans.VIEW_TYPE_WIDGET_MULTIAUTOCOMPLETETEXTVIEW:
                    case ViewBeans.VIEW_TYPE_LAYOUT_BOTTOMNAVIGATIONVIEW:
                        if (!hasAttr("focusable", viewBean) && !toNotAdd.contains("android:focusable") && !injectHandler.contains("focusable")) {
                            widgetTag.addAttribute("android", "focusable", "false");
                        }
                        break;

                    default:
                        break;
                }
            }

            if (!toNotAdd.contains("android:layout_width") && !injectHandler.contains("layout_width")) {
                int width = viewBean.layout.width;
                if (width == ViewGroup.LayoutParams.MATCH_PARENT) {
                    widgetTag.addAttribute("android", "layout_width", "match_parent");
                } else if (width == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    widgetTag.addAttribute("android", "layout_width", "wrap_content");
                } else {
                    widgetTag.addAttribute("android", "layout_width", width + "dp");
                }
            }

            if (!toNotAdd.contains("android:layout_height") && !injectHandler.contains("layout_height")) {
                int height = viewBean.layout.height;
                if (height == ViewGroup.LayoutParams.MATCH_PARENT) {
                    widgetTag.addAttribute("android", "layout_height", "match_parent");
                } else if (height == ViewGroup.LayoutParams.WRAP_CONTENT) {
                    widgetTag.addAttribute("android", "layout_height", "wrap_content");
                } else {
                    widgetTag.addAttribute("android", "layout_height", height + "dp");
                }
            }

            writeLayoutMargin(widgetTag, viewBean);
            if (widgetTag.c().equals("CardView")) {
                writeCardViewPadding(widgetTag, viewBean);
            } else {
                writeViewPadding(widgetTag, viewBean);
            }
            writeBackgroundResource(widgetTag, viewBean);
            if (viewBean.getClassInfo().a("ViewGroup")) {
                writeViewGravity(widgetTag, viewBean);
            }
        }
        if (viewBean.getClassInfo().a("LinearLayout") &&
                !widgetTag.c().matches("(BottomAppBar|NavigationView|Coordinator|Floating|Collaps|include)\\w*")) {
            if (!toNotAdd.contains("android:orientation") && !injectHandler.contains("orientation")) {
                int orientation = viewBean.layout.orientation;
                if (orientation == LinearLayout.HORIZONTAL) {
                    widgetTag.addAttribute("android", "orientation", "horizontal");
                } else if (orientation == LinearLayout.VERTICAL) {
                    widgetTag.addAttribute("android", "orientation", "vertical");
                }
            }

            if (!toNotAdd.contains("android:weightSum") && !injectHandler.contains("weightSum")) {
                int weightSum = viewBean.layout.weightSum;
                if (weightSum > 0) {
                    widgetTag.addAttribute("android", "weightSum", String.valueOf(weightSum));
                }
            }
        }
        if (viewBean.getClassInfo().a("TextView")) {
            writeViewGravity(widgetTag, viewBean);
            writeTextAttributes(widgetTag, viewBean);
        }
        if (viewBean.getClassInfo().a("ImageView")) {
            writeImgSrcAttr(widgetTag, viewBean);
            if (!widgetTag.toCode().contains(".")) {
                writeImageScaleType(widgetTag, viewBean);
            }
        }
        if (viewBean.getClassInfo().b("SeekBar")) {
            writeViewGravity(widgetTag, viewBean);
        }
        if (viewBean.getClassInfo().b("ProgressBar")) {
            writeViewGravity(widgetTag, viewBean);
        }
        if (viewBean.getClassInfo().b("WaveSideBar")) {
            int textSize = viewBean.text.textSize;
            if (textSize > 0 && !toNotAdd.contains("app:sidebar_text_size")) {
                widgetTag.addAttribute("app", "sidebar_text_size", textSize + "sp");
            }

            int textColor = viewBean.text.textColor;
            String resTextColor = viewBean.text.resTextColor;
            if (textColor != 0 && !toNotAdd.contains("app:sidebar_text_color") && resTextColor != null) {
                if (resTextColor.startsWith("?")) {
                    widgetTag.addAttribute("app", "sidebar_text_color", resTextColor);
                } else {
                    widgetTag.addAttribute("app", "sidebar_text_color", "@color/" + resTextColor);
                }
            } else if (textColor != 0 && !toNotAdd.contains("app:sidebar_text_color")) {
                widgetTag.addAttribute("app", "sidebar_text_color", formatColor(textColor & 0xffffff));
            }
        }
        k(widgetTag, viewBean);
        int parentViewType = viewBean.parentType;
        if (!viewBean.convert.equals("include")) {
            if (parentViewType == ViewBean.VIEW_TYPE_LAYOUT_LINEAR) {
                writeLayoutGravity(widgetTag, viewBean);
                int weight = viewBean.layout.weight;
                if (weight > 0 && !toNotAdd.contains("android:layout_weight") && !injectHandler.contains("layout_weight")) {
                    widgetTag.addAttribute("android", "layout_weight", String.valueOf(weight));
                }
            } else if (parentViewType == ViewBean.VIEW_TYPE_LAYOUT_HSCROLLVIEW || parentViewType == ViewBean.VIEW_TYPE_LAYOUT_VSCROLLVIEW) {
                writeLayoutGravity(widgetTag, viewBean);
            }
        }
        if (viewBean.getClassInfo().a("ViewGroup")) {
            for (ViewBean bean : views) {
                if (bean.parent != null && bean.parent.equals(viewBean.id)) {
                    writeWidget(widgetTag, bean);
                }
            }
        }
        if (!viewBean.inject.isEmpty()) {
            widgetTag.addAttributeValue(viewBean.inject.replaceAll(" ", ""));
        }
        
        if (!viewBean.parentAttributes.isEmpty()) {
            viewBean.parentAttributes.forEach((key, value) -> {
                String[] parts = key.split(":");
                widgetTag.addAttribute(parts[0], parts[1], RELATIVE_IDS.contains(key) ? "@id/" + value : value);
            });
        }

        if (widgetTag.c().equals("CollapsingToolbarLayout")
                && buildConfig.g
                && projectFile.fileType == ProjectFileBean.PROJECT_FILE_TYPE_ACTIVITY) {
            if (projectFile.hasActivityOption(ProjectFileBean.OPTION_ACTIVITY_TOOLBAR)) {
                if (collapsingToolbarLayout == null) {
                    collapsingToolbarLayout = widgetTag;
                    return;
                }
            }
        }
        nx.a(widgetTag);
    }

    private void writeFabView(XmlBuilder nx, ViewBean viewBean) {
        var injectHandler = new InjectAttributeHandler(viewBean);
        Set<String> toNotAdd = readAttributesToReplace(viewBean);
        XmlBuilder floatingActionButtonTag = new XmlBuilder("com.google.android.material.floatingactionbutton.FloatingActionButton");
        if (!toNotAdd.contains("android:id")) {
            floatingActionButtonTag.addAttribute("android", "id", "@+id/" + viewBean.id);
        }
        if (!toNotAdd.contains("android:layout_width") && !injectHandler.contains("layout_width")) {
            floatingActionButtonTag.addAttribute("android", "layout_width", "wrap_content");
        }
        if (!toNotAdd.contains("android:layout_height") && !injectHandler.contains("layout_height")) {
            floatingActionButtonTag.addAttribute("android", "layout_height", "wrap_content");
        }
        writeLayoutMargin(floatingActionButtonTag, viewBean);
        writeLayoutGravity(floatingActionButtonTag, viewBean);

        String resName = viewBean.image.resName;
        if (resName != null && !resName.isEmpty() && !resName.equals("NONE") &&
                !toNotAdd.contains("app:srcCompat") && !injectHandler.contains("srcCompat")) {
            floatingActionButtonTag.addAttribute("app", "srcCompat", "@drawable/" + resName.toLowerCase());
        }
        if (viewBean.id.equals("_fab")) {
            aci.inject(floatingActionButtonTag, "FloatingActionButton");
        }
        k(floatingActionButtonTag, viewBean);
        nx.a(floatingActionButtonTag);
    }

    private void writeViewGravity(XmlBuilder nx, ViewBean viewBean) {
        var injectHandler = new InjectAttributeHandler(viewBean);
        Set<String> toNotAdd = readAttributesToReplace(viewBean);
        if (!toNotAdd.contains("android:gravity") && !injectHandler.contains("gravity")) {
            int gravity = viewBean.layout.gravity;
            if (gravity != Gravity.NO_GRAVITY) {
                String attrValue = "";
                int verticalGravity = gravity & Gravity.FILL_VERTICAL;
                int horizontalGravity = gravity & Gravity.FILL_HORIZONTAL;
                if (horizontalGravity == Gravity.CENTER_HORIZONTAL) {
                    attrValue = "center_horizontal";
                } else {
                    if ((horizontalGravity & Gravity.LEFT) == Gravity.LEFT) {
                        attrValue = "left";
                    }
                    if ((horizontalGravity & Gravity.RIGHT) == Gravity.RIGHT) {
                        if (!attrValue.isEmpty()) {
                            attrValue += "|";
                        }
                        attrValue += "right";
                    }
                }
                if (verticalGravity == Gravity.CENTER_VERTICAL) {
                    if (!attrValue.isEmpty()) {
                        attrValue += "|";
                    }
                    attrValue += "center_vertical";
                } else {
                    if ((verticalGravity & Gravity.TOP) == Gravity.TOP) {
                        if (!attrValue.isEmpty()) {
                            attrValue += "|";
                        }
                        attrValue += "top";
                    }
                    if ((verticalGravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
                        if (!attrValue.isEmpty()) {
                            attrValue += "|";
                        }
                        attrValue += "bottom";
                    }
                }
                nx.addAttribute("android", "gravity", attrValue);
            }
        }
    }

    private void writeImgSrcAttr(XmlBuilder nx, ViewBean viewBean) {
        var injectHandler = new InjectAttributeHandler(viewBean);
        Set<String> toNotAdd = readAttributesToReplace(viewBean);
        String resName = viewBean.image.resName;
        if (!resName.isEmpty() && !"NONE".equals(resName)) {
            String value = "@drawable/" + resName.toLowerCase();
            if (nx.c().equals("FloatingActionButton")) {
                if (!toNotAdd.contains("app:srcCompat") && !injectHandler.contains("srcCompat")) {
                    nx.addAttribute("app", "srcCompat", value);
                }
            } else {
                if (!toNotAdd.contains("android:src") && !injectHandler.contains("src")) {
                    nx.addAttribute("android", "src", value);
                }
            }
        }
    }

    /**
     * @see ImageView.ScaleType
     */
    private void writeImageScaleType(XmlBuilder nx, ViewBean viewBean) {
        var injectHandler = new InjectAttributeHandler(viewBean);
        Set<String> toNotAdd = readAttributesToReplace(viewBean);
        if (!toNotAdd.contains("android:scaleType") && !injectHandler.contains("scaleType")) {
            if (viewBean.image.scaleType.equals(ImageBean.SCALE_TYPE_CENTER)) {
                nx.addAttribute("android", "scaleType", "center");
            } else if (viewBean.image.scaleType.equals(ImageBean.SCALE_TYPE_FIT_XY)) {
                nx.addAttribute("android", "scaleType", "fitXY");
            } else if (viewBean.image.scaleType.equals(ImageBean.SCALE_TYPE_FIT_START)) {
                nx.addAttribute("android", "scaleType", "fitStart");
            } else if (viewBean.image.scaleType.equals(ImageBean.SCALE_TYPE_FIT_END)) {
                nx.addAttribute("android", "scaleType", "fitEnd");
            } else if (viewBean.image.scaleType.equals(ImageBean.SCALE_TYPE_FIT_CENTER)) {
                nx.addAttribute("android", "scaleType", "fitCenter");
            } else if (viewBean.image.scaleType.equals(ImageBean.SCALE_TYPE_CENTER_CROP)) {
                nx.addAttribute("android", "scaleType", "centerCrop");
            } else if (viewBean.image.scaleType.equals(ImageBean.SCALE_TYPE_CENTER_INSIDE)) {
                nx.addAttribute("android", "scaleType", "centerInside");
            }
        }
    }

    /**
     * @see Gravity
     */
    private void writeLayoutGravity(XmlBuilder nx, ViewBean viewBean) {
        var injectHandler = new InjectAttributeHandler(viewBean);
        Set<String> toNotAdd = readAttributesToReplace(viewBean);
        if (!toNotAdd.contains("android:layout_gravity") && !injectHandler.contains("layout_gravity")) {
            int gravity = viewBean.layout.layoutGravity;
            if (gravity != Gravity.NO_GRAVITY) {
                String attrValue = "";
                int verticalGravity = gravity & Gravity.FILL_VERTICAL;
                int horizontalGravity = gravity & Gravity.FILL_HORIZONTAL;
                if (horizontalGravity == Gravity.CENTER_HORIZONTAL) {
                    attrValue = "center_horizontal";
                } else {
                    if ((horizontalGravity & Gravity.LEFT) == Gravity.LEFT) {
                        attrValue = "left";
                    }
                    if ((horizontalGravity & Gravity.RIGHT) == Gravity.RIGHT) {
                        if (!attrValue.isEmpty()) {
                            attrValue += "|";
                        }
                        attrValue += "right";
                    }
                }
                if (verticalGravity == Gravity.CENTER_VERTICAL) {
                    if (!attrValue.isEmpty()) {
                        attrValue += "|";
                    }
                    attrValue += "center_vertical";
                } else {
                    if ((verticalGravity & Gravity.TOP) == Gravity.TOP) {
                        if (!attrValue.isEmpty()) {
                            attrValue += "|";
                        }
                        attrValue += "top";
                    }
                    if ((verticalGravity & Gravity.BOTTOM) == Gravity.BOTTOM) {
                        if (!attrValue.isEmpty()) {
                            attrValue += "|";
                        }
                        attrValue += "bottom";
                    }
                }
                nx.addAttribute("android", "layout_gravity", attrValue);
            }
        }
    }

    /**
     * @see ViewGroup.MarginLayoutParams
     */
    private void writeLayoutMargin(XmlBuilder nx, ViewBean viewBean) {
        var injectHandler = new InjectAttributeHandler(viewBean);
        Set<String> toNotAdd = readAttributesToReplace(viewBean);
        LayoutBean layoutBean = viewBean.layout;
        int marginLeft = layoutBean.marginLeft;
        int marginTop = layoutBean.marginTop;
        int marginRight = layoutBean.marginRight;
        int marginBottom = layoutBean.marginBottom;

        if (marginLeft == marginRight && marginTop == marginBottom
                && marginLeft == marginTop && marginLeft > 0) {
            if (!toNotAdd.contains("android:layout_margin") && !injectHandler.contains("layout_margin")) {
                nx.addAttribute("android", "layout_margin", marginLeft + "dp");
            }
            return;
        }

        if (marginLeft > 0 && !toNotAdd.contains("android:layout_marginLeft") && !injectHandler.contains("layout_marginLeft")) {
            nx.addAttribute("android", "layout_marginLeft", marginLeft + "dp");
        }
        if (viewBean.layout.marginTop > 0 && !toNotAdd.contains("android:layout_marginTop") && !injectHandler.contains("layout_marginTop")) {
            nx.addAttribute("android", "layout_marginTop", viewBean.layout.marginTop + "dp");
        }
        if (marginRight > 0 && !toNotAdd.contains("android:layout_marginRight") && !injectHandler.contains("layout_marginRight")) {
            nx.addAttribute("android", "layout_marginRight", marginRight + "dp");
        }
        if (marginBottom > 0 && !toNotAdd.contains("android:layout_marginBottom") && !injectHandler.contains("layout_marginBottom")) {
            nx.addAttribute("android", "layout_marginBottom", marginBottom + "dp");
        }
    }

    /**
     * @see View#getPaddingLeft()
     * @see View#getPaddingTop()
     * @see View#getPaddingRight()
     * @see View#getPaddingBottom()
     */
    private void writeCardViewPadding(XmlBuilder nx, ViewBean viewBean) {
        var injectHandler = new InjectAttributeHandler(viewBean);
        Set<String> toNotAdd = readAttributesToReplace(viewBean);
        LayoutBean layoutBean = viewBean.layout;
        int paddingLeft = layoutBean.paddingLeft;
        int paddingTop = layoutBean.paddingTop;
        int paddingRight = layoutBean.paddingRight;
        int paddingBottom = layoutBean.paddingBottom;

        if (paddingLeft == paddingRight && paddingTop == paddingBottom
                && paddingLeft == paddingTop && paddingLeft > 0) {
            if (!toNotAdd.contains("app:contentPadding") && !injectHandler.contains("contentPadding")) {
                nx.addAttribute("app", "contentPadding", paddingLeft + "dp");
            }
            return;
        }

        if (paddingLeft > 0 && !toNotAdd.contains("app:contentPaddingLeft") && !injectHandler.contains("contentPaddingLeft")) {
            nx.addAttribute("app", "contentPaddingLeft", paddingLeft + "dp");
        }
        if (paddingTop > 0 && !toNotAdd.contains("app:contentPaddingTop") && !injectHandler.contains("contentPaddingTop")) {
            nx.addAttribute("app", "contentPaddingTop", paddingTop + "dp");
        }
        if (paddingRight > 0 && !toNotAdd.contains("app:contentPaddingRight") && !injectHandler.contains("contentPaddingRight")) {
            nx.addAttribute("app", "contentPaddingRight", paddingRight + "dp");
        }
        if (paddingBottom > 0 && !toNotAdd.contains("app:contentPaddingBottom") && !injectHandler.contains("contentPaddingBottom")) {
            nx.addAttribute("app", "contentPaddingBottom", paddingBottom + "dp");
        }
    }

    /**
     * @see View#getPaddingLeft()
     * @see View#getPaddingTop()
     * @see View#getPaddingRight()
     * @see View#getPaddingBottom()
     */
    private void writeViewPadding(XmlBuilder nx, ViewBean viewBean) {
        var injectHandler = new InjectAttributeHandler(viewBean);
        Set<String> toNotAdd = readAttributesToReplace(viewBean);
        LayoutBean layoutBean = viewBean.layout;
        int paddingLeft = layoutBean.paddingLeft;
        int paddingTop = layoutBean.paddingTop;
        int paddingRight = layoutBean.paddingRight;
        int paddingBottom = layoutBean.paddingBottom;

        if (paddingLeft == paddingRight && paddingTop == paddingBottom
                && paddingLeft == paddingTop && paddingLeft > 0) {
            if (!toNotAdd.contains("android:padding") && !injectHandler.contains("padding")) {
                nx.addAttribute("android", "padding", paddingLeft + "dp");
            }
            return;
        }

        if (paddingLeft > 0 && !toNotAdd.contains("android:paddingLeft") && !injectHandler.contains("paddingLeft")) {
            nx.addAttribute("android", "paddingLeft", paddingLeft + "dp");
        }
        if (paddingTop > 0 && !toNotAdd.contains("android:paddingTop") && !injectHandler.contains("paddingTop")) {
            nx.addAttribute("android", "paddingTop", paddingTop + "dp");
        }
        if (paddingRight > 0 && !toNotAdd.contains("android:paddingRight") && !injectHandler.contains("paddingRight")) {
            nx.addAttribute("android", "paddingRight", paddingRight + "dp");
        }
        if (paddingBottom > 0 && !toNotAdd.contains("android:paddingBottom") && !injectHandler.contains("paddingBottom")) {
            nx.addAttribute("android", "paddingBottom", paddingBottom + "dp");
        }
    }

    private void writeTextAttributes(XmlBuilder nx, ViewBean viewBean) {
        var injectHandler = new InjectAttributeHandler(viewBean);
        Set<String> toNotAdd = readAttributesToReplace(viewBean);
        String text = viewBean.text.text;
        if (text != null && !text.isEmpty() && !toNotAdd.contains("android:text") && !injectHandler.contains("text")) {
            if (text.startsWith("@")) {
                nx.addAttribute("android", "text", text);
            } else {
                nx.addAttribute("android", "text", escapeXML(text));
            }
        }

        int textSize = viewBean.text.textSize;
        if (textSize > 0 && !toNotAdd.contains("android:textSize") && !injectHandler.contains("textSize")) {
            nx.addAttribute("android", "textSize", textSize + "sp");
        }
        if (!toNotAdd.contains("android:textStyle") && !injectHandler.contains("textStyle")) {
            int textType = viewBean.text.textType;
            if (textType == TextBean.TEXT_TYPE_BOLD) {
                nx.addAttribute("android", "textStyle", "bold");
            } else if (textType == TextBean.TEXT_TYPE_ITALIC) {
                nx.addAttribute("android", "textStyle", "italic");
            } else if (textType == TextBean.TEXT_TYPE_BOLDITALIC) {
                nx.addAttribute("android", "textStyle", "bold|italic");
            }
        }
        if (viewBean.text.textColor != 0) {
            if (!hasAttr("textColor", viewBean) && !toNotAdd.contains("android:textColor") && !injectHandler.contains("textColor") && viewBean.text.resTextColor != null) {
                if (viewBean.text.resTextColor.startsWith("?")) {
                    nx.addAttribute("android", "textColor", viewBean.text.resTextColor);
                } else {
                    nx.addAttribute("android", "textColor", "@color/" + viewBean.text.resTextColor);
                }
            } else if (!hasAttr("textColor", viewBean) && !toNotAdd.contains("android:textColor") && !injectHandler.contains("textColor")) {
                nx.addAttribute("android", "textColor", formatColor(viewBean.text.textColor & 0xffffff));
            }
        }
        switch (viewBean.type) {
            case ViewBean.VIEW_TYPE_WIDGET_EDITTEXT:
            case ViewBeans.VIEW_TYPE_WIDGET_AUTOCOMPLETETEXTVIEW:
            case ViewBeans.VIEW_TYPE_WIDGET_MULTIAUTOCOMPLETETEXTVIEW:
                String hint = viewBean.text.hint;
                if (hint != null && !hint.isEmpty() && !toNotAdd.contains("android:hint") && !injectHandler.contains("hint")) {
                    if (hint.startsWith("@")) {
                        nx.addAttribute("android", "hint", hint);
                    } else {
                        nx.addAttribute("android", "hint", escapeXML(hint));
                    }
                }
                if (viewBean.text.hintColor != 0) {
                    if (!hasAttr("textColorHint", viewBean) && !toNotAdd.contains("android:textColorHint") && (viewBean.text.resHintColor != null)) {
                        if (viewBean.text.resHintColor.startsWith("?")) {
                            nx.addAttribute("android", "textColorHint", viewBean.text.resHintColor);
                        } else {
                            nx.addAttribute("android", "textColorHint", "@color/" + viewBean.text.resHintColor);
                        }
                    } else if (!hasAttr("textColorHint", viewBean) && !toNotAdd.contains("android:textColorHint")) {
                        nx.addAttribute("android", "textColorHint", formatColor(viewBean.text.hintColor & 0xffffff));
                    }
                }
                if (viewBean.text.singleLine != 0 && !toNotAdd.contains("android:singleLine") && !injectHandler.contains("singleLine")) {
                    nx.addAttribute("android", "singleLine", "true");
                }

                int line = viewBean.text.line;
                if (line > 0 && !toNotAdd.contains("android:lines") && !injectHandler.contains("lines")) {
                    nx.addAttribute("android", "lines", String.valueOf(line));
                }

                int inputType = viewBean.text.inputType;
                if (inputType != TextBean.INPUT_TYPE_TEXT && !toNotAdd.contains("android:inputType") && !injectHandler.contains("inputType")) {
                    nx.addAttribute("android", "inputType", sq.a("property_input_type", inputType));
                }

                int imeOption = viewBean.text.imeOption;
                if (imeOption != TextBean.IME_OPTION_NORMAL && !toNotAdd.contains("android:imeOptions") && !injectHandler.contains("imeOptions")) {
                    if (imeOption == TextBean.IME_OPTION_NONE) {
                        nx.addAttribute("android", "imeOptions", "actionNone");
                    } else if (imeOption == TextBean.IME_OPTION_GO) {
                        nx.addAttribute("android", "imeOptions", "actionGo");
                    } else if (imeOption == TextBean.IME_OPTION_SEARCH) {
                        nx.addAttribute("android", "imeOptions", "actionSearch");
                    } else if (imeOption == TextBean.IME_OPTION_SEND) {
                        nx.addAttribute("android", "imeOptions", "actionSend");
                    } else if (imeOption == TextBean.IME_OPTION_NEXT) {
                        nx.addAttribute("android", "imeOptions", "actionNext");
                    } else if (imeOption == TextBean.IME_OPTION_DONE) {
                        nx.addAttribute("android", "imeOptions", "actionDone");
                    }
                }
                break;

            case ViewBean.VIEW_TYPE_WIDGET_TEXTVIEW:
                if (viewBean.text.singleLine != 0 && !toNotAdd.contains("android:singleLine") && !injectHandler.contains("singleLine")) {
                    nx.addAttribute("android", "singleLine", "true");
                }
                line = viewBean.text.line;
                if (line > 0 && !toNotAdd.contains("android:lines") && !injectHandler.contains("lines")) {
                    nx.addAttribute("android", "lines", String.valueOf(line));
                }
                break;
        }
    }

    private void k(XmlBuilder nx, ViewBean viewBean) {
        var injectHandler = new InjectAttributeHandler(viewBean);
        Set<String> toNotAdd = readAttributesToReplace(viewBean);
        if (viewBean.enabled == 0 && !toNotAdd.contains("android:enabled") && !injectHandler.contains("enabled")) {
            nx.addAttribute("android", "enabled", "false");
        }
        if (viewBean.clickable == 0 && !toNotAdd.contains("android:clickable") && !injectHandler.contains("clickable")) {
            nx.addAttribute("android", "clickable", "false");
        }
        int rotate = viewBean.image.rotate;
        if (rotate != 0 && !toNotAdd.contains("android:rotation") && !injectHandler.contains("rotation")) {
            nx.addAttribute("android", "rotation", String.valueOf(rotate));
        }
        float alpha = viewBean.alpha;
        if (1.0f != alpha && !toNotAdd.contains("android:alpha") && !injectHandler.contains("alpha")) {
            nx.addAttribute("android", "alpha", String.valueOf(alpha));
        }
        if (0.0f != viewBean.translationX && !toNotAdd.contains("android:translationX") && !injectHandler.contains("translationX")) {
            nx.addAttribute("android", "translationX", viewBean.translationX + "dp");
        }
        if (0.0f != viewBean.translationY && !toNotAdd.contains("android:translationY") && !injectHandler.contains("translationY")) {
            nx.addAttribute("android", "translationY", viewBean.translationY + "dp");
        }
        float scaleX = viewBean.scaleX;
        if (1.0f != scaleX && !toNotAdd.contains("android:scaleX") && !injectHandler.contains("scaleX")) {
            nx.addAttribute("android", "scaleX", String.valueOf(scaleX));
        }
        float scaleY = viewBean.scaleY;
        if (1.0f != scaleY && !toNotAdd.contains("android:scaleY") && !injectHandler.contains("scaleY")) {
            nx.addAttribute("android", "scaleY", String.valueOf(scaleY));
        }

        switch (viewBean.type) {
            case ViewBean.VIEW_TYPE_WIDGET_CHECKBOX:
            case ViewBean.VIEW_TYPE_WIDGET_SWITCH:
            case ViewBeans.VIEW_TYPE_WIDGET_RADIOBUTTON:
                if (viewBean.checked == 1 && !toNotAdd.contains("android:checked") && !injectHandler.contains("checked")) {
                    nx.addAttribute("android", "checked", "true");
                }
                break;

            case ViewBean.VIEW_TYPE_WIDGET_SEEKBAR:
                int progress = viewBean.progress;
                if (progress > ViewBean.DEFAULT_PROGRESS && !toNotAdd.contains("android:progress") && !injectHandler.contains("progress")) {
                    nx.addAttribute("android", "progress", String.valueOf(progress));
                }

                int max = viewBean.max;
                if (max != ViewBean.DEFAULT_MAX && !toNotAdd.contains("android:max") && !injectHandler.contains("max")) {
                    nx.addAttribute("android", "max", String.valueOf(max));
                }
                break;

            case ViewBean.VIEW_TYPE_WIDGET_CALENDARVIEW:
                int firstDayOfWeek = viewBean.firstDayOfWeek;
                if (firstDayOfWeek != 1 && !toNotAdd.contains("android:firstDayOfWeek") && !injectHandler.contains("firstDayOfWeek")) {
                    nx.addAttribute("android", "firstDayOfWeek", String.valueOf(firstDayOfWeek));
                }
                break;

            case ViewBean.VIEW_TYPE_WIDGET_SPINNER:
                int spinnerMode = viewBean.spinnerMode;
                if (!toNotAdd.contains("android:spinnerMode") && !injectHandler.contains("spinnerMode")) {
                    if (spinnerMode == ViewBean.SPINNER_MODE_DROPDOWN) {
                        nx.addAttribute("android", "spinnerMode", "dropdown");
                    } else if (spinnerMode == ViewBean.SPINNER_MODE_DIALOG) {
                        nx.addAttribute("android", "spinnerMode", "dialog");
                    }
                }
                break;

            case ViewBean.VIEW_TYPE_WIDGET_LISTVIEW:
                int dividerHeight = viewBean.dividerHeight;
                if (dividerHeight != 1 && !toNotAdd.contains("android:dividerHeight") && !injectHandler.contains("dividerHeight")) {
                    nx.addAttribute("android", "dividerHeight", dividerHeight + "dp");
                }
                if (dividerHeight == 0 && !toNotAdd.contains("android:divider") && !injectHandler.contains("divider")) {
                    nx.addAttribute("android", "divider", "@null");
                }

                if (!toNotAdd.contains("android:choiceMode") && !injectHandler.contains("choiceMode")) {
                    var value = switch (viewBean.choiceMode) {
                        case ViewBean.CHOICE_MODE_NONE -> "none";
                        case ViewBean.CHOICE_MODE_SINGLE -> "singleChoice";
                        case ViewBean.CHOICE_MODE_MULTI -> "multipleChoice";
                        default -> "";
                    };
                    if (!value.isEmpty()) {
                        nx.addAttribute("android", "choiceMode", value);
                    }
                }
                break;

            case ViewBean.VIEW_TYPE_WIDGET_ADVIEW:
                String adSize = viewBean.adSize;
                if (!toNotAdd.contains("app:adSize") && !injectHandler.contains("adSize")) {
                    if (adSize == null || adSize.isEmpty()) {
                        nx.addAttribute("app", "adSize", "SMART_BANNER");
                    } else {
                        nx.addAttribute("app", "adSize", adSize);
                    }
                }

                if (!toNotAdd.contains("app:adUnitId") && !injectHandler.contains("adUnitId")) {
                    if (buildConfig.isDebugBuild) {
                        nx.addAttribute("app", "adUnitId", "ca-app-pub-3940256099942544/6300978111");
                    } else {
                        nx.addAttribute("app", "adUnitId", buildConfig.bannerAdUnitId);
                    }
                }
                break;

            case ViewBean.VIEW_TYPE_WIDGET_PROGRESSBAR:
                progress = viewBean.progress;
                if (progress > ViewBean.DEFAULT_PROGRESS && !toNotAdd.contains("android:progress") && !injectHandler.contains("progress")) {
                    nx.addAttribute("android", "progress", String.valueOf(progress));
                }

                max = viewBean.max;
                if (max != ViewBean.DEFAULT_MAX && !toNotAdd.contains("android:max") && !injectHandler.contains("max")) {
                    nx.addAttribute("android", "max", String.valueOf(max));
                }

                String indeterminate = viewBean.indeterminate;
                if (indeterminate != null && !indeterminate.isEmpty() && !toNotAdd.contains("android:indeterminate") && !injectHandler.contains("indeterminate")) {
                    nx.addAttribute("android", "indeterminate", indeterminate);
                }
                String progressStyle = viewBean.progressStyle;
                if (progressStyle != null && !progressStyle.isEmpty() && !toNotAdd.contains("style") && !injectHandler.contains("style")) {
                    nx.addAttribute(null, "style", progressStyle);
                }
                break;
        }
    }

    /**
     * check whether the attribute (attrName) is injected to the ViewBean or not.
     */
    private boolean hasAttr(String attrName, ViewBean bean) {
        final String inject = bean.inject;
        if (inject == null || inject.isEmpty()) return false;
        return Pattern.compile("(android|app) *?: *?" + attrName).matcher(inject).find();
    }

    public Set<String> readAttributesToReplace(ViewBean viewBean) {
        Set<String> toReplace = new HashSet<>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader("<tag xmlns:android=\"http://schemas.android.com/apk/res/android\" " +
                    "xmlns:app=\"http://schemas.android.com/apk/res-auto\" " +
                    "xmlns:tools=\"http://schemas.android.com/tools\"" +
                    viewBean.inject + "></tag>"));

            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    for (int i = 0; i < parser.getAttributeCount(); i++) {
                        if ("http://schemas.android.com/tools".equals(parser.getAttributeNamespace(i)) &&
                                "replace".equals(parser.getAttributeName(i))) {
                            toReplace.addAll(Arrays.asList(parser.getAttributeValue(i).split("\\s*,\\s*")));
                        }
                    }
                }

                eventType = parser.next();
            }
        } catch (XmlPullParserException | IOException | RuntimeException e) {
            LogUtil.e("a.a.a.Ox", "Failed to parse inject property of View " + viewBean.id, e);
        }

        return toReplace;
    }
}

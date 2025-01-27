package a.a.a;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.besome.sketch.beans.ColorBean;
import com.besome.sketch.editor.manage.library.material3.Material3LibraryManager;
import com.besome.sketch.editor.view.ColorGroupItem;

import pro.sketchware.R;
import pro.sketchware.activities.resources.editors.utils.ColorsEditorManager;
import pro.sketchware.databinding.ColorPickerBinding;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pro.sketchware.databinding.ItemAttrBinding;
import pro.sketchware.utility.FileUtil;
import mod.hey.studios.util.Helper;
import pro.sketchware.utility.SketchwareUtil;
import pro.sketchware.utility.ThemeUtils;

public class Zx extends PopupWindow {

    private final ArrayList<ColorBean> colorList = new ArrayList<>();
    private final ArrayList<ColorBean[]> colorGroups = new ArrayList<>();
    private ArrayList<Attribute> attributes;
    private final ArrayList<HashMap<String, Object>> color_res_list = new ArrayList<>();
    private final ColorPickerBinding binding;
    private b colorPickerCallback;
    private materialColorAttr materialColorAttr;
    private XB colorValidator;
    private int k;
    private int l;
    private int m = -1;
    private DB colorPref;
    private Activity activity;
    private static String sc_id;
    private boolean hasMaterialColors;
    private final ColorsAdapter colorsAdapter = new ColorsAdapter();
    private Material3LibraryManager material3LibraryManager;

    public Zx(Activity activity, int var3, boolean isTransparentColor, boolean isNoneColor) {
        super(activity);
        binding = ColorPickerBinding.inflate(activity.getLayoutInflater());
        initialize(activity, var3, isTransparentColor, isNoneColor);
    }

    public Zx(Activity activity, int var3, boolean isTransparentColor, boolean isNoneColor, String scId) {
        super(activity);
        binding = ColorPickerBinding.inflate(activity.getLayoutInflater());
        sc_id = scId;
        material3LibraryManager = new Material3LibraryManager(scId);
        hasMaterialColors = true;
        initialize(activity, var3, isTransparentColor, isNoneColor);
    }

    private void deleteAllSavedColors() {
        aB dialog = new aB(activity);
        dialog.a(R.drawable.delete_96);
        dialog.b(xB.b().a(activity, R.string.picker_color_title_delete_all_custom_color));
        dialog.a(xB.b().a(activity, R.string.picker_color_message_delete_all_custom_color));
        dialog.b(xB.b().a(activity, R.string.common_word_delete), v -> {
            colorPref.a();
            colorGroups.set(0, getSavedColorBeans());
            notifyChanges();
            dialog.dismiss();
        });
        dialog.a(xB.b().a(activity, R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    public void a(b callback) {
        colorPickerCallback = callback;
    }

    public void materialColorAttr(materialColorAttr callback) {
        materialColorAttr = callback;
    }

    public void initialize(Activity activity, int var3, boolean isTransparentColor, boolean isNoneColor) {
        this.activity = activity;
        colorPref = new DB(activity, "P24");
        initializeColorData(isTransparentColor, isNoneColor);

        for (int groupIndex = 0; groupIndex < colorGroups.size(); ++groupIndex) {
            ColorBean[] colorBeans = colorGroups.get(groupIndex);

            for (int colorIndex = 0; colorIndex < colorBeans.length; ++colorIndex) {
                if (colorBeans[colorIndex].colorCode == var3) {
                    k = groupIndex;
                    l = groupIndex;
                    m = colorIndex;
                    break;
                }
            }
        }
        super.setBackgroundDrawable(null);
        super.setAnimationStyle(android.R.style.Animation_Dialog);
        super.setFocusable(true);
        super.setOutsideTouchable(true);
        super.setContentView(binding.getRoot());
        int[] widthAndHeight = GB.c(activity);
        super.setWidth(widthAndHeight[0]);
        super.setHeight(widthAndHeight[1]);
        binding.colorList.setHasFixedSize(true);
        binding.colorList.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
        binding.colorList.setAdapter(colorsAdapter);
        binding.colorList.setItemAnimator(new DefaultItemAnimator());

        binding.tiCustomColor.setHint(xB.b().a(activity, R.string.picker_color_hint_enter_hex_color_code));

        colorValidator = new XB(activity, binding.tiCustomColor, binding.tvCustomColor);
        binding.etCustomColor.setPrivateImeOptions("defaultInputmode=english;");
        binding.tvAddColor.setText(xB.b().a(activity, R.string.common_word_add).toUpperCase());
        binding.tvAddColor.setOnClickListener(view -> {
            if (colorValidator.b()) {
                String formattedColor = String.format("#%8s", binding.etCustomColor.getText().toString()).replaceAll(" ", "F");
                savePickedColor(formattedColor.toUpperCase());
                notifyChanges();
            }
        });
        binding.colorList.getAdapter().notifyItemChanged(m);
        binding.layoutColorTitle.removeAllViews();

        for (int j = 0; j < colorList.size(); ++j) {
            ColorGroupItem colorGroupItem = new ColorGroupItem(activity);
            ColorBean colorBean = colorList.get(j);
            int finalJ = j;
            colorGroupItem.b.setOnClickListener(v -> {
                l = finalJ;
                if (finalJ == 0 && colorGroups.get(finalJ).length == 0) {
                    bB.b(activity, xB.b().a(activity, R.string.picker_color_custom_color_not_found), 1).show();
                    return;
                }
                if (finalJ == 1 && colorGroups.get(finalJ).length == 0) {
                    bB.b(activity, xB.b().a(activity, R.string.picker_color_xml_is_empty), 1).show();
                    return;
                }
                if (finalJ == 2 && !material3LibraryManager.isMaterial3Enabled()) {
                    SketchwareUtil.toastError("Please enable Material3 in the Library Manager first");
                    return;
                }
                if (hasMaterialColors) {
                    if (binding.colorList.getAdapter() instanceof AttrAdapter && finalJ != 2) {
                        binding.colorList.setAdapter(colorsAdapter);
                    } else if (finalJ == 2) {
                        binding.colorList.setAdapter(new AttrAdapter(attributes));
                    } else {
                        colorsAdapter.notifyDataSetChanged();
                    }
                } else {
                    colorsAdapter.notifyDataSetChanged();
                }
            });
            colorGroupItem.b.setText(colorBean.colorName);
            colorGroupItem.b.setTextColor(colorBean.displayNameColor);
            colorGroupItem.b.setBackgroundColor(colorBean.colorCode);
            binding.layoutColorTitle.addView(colorGroupItem);
            if (j == k) {
                colorGroupItem.c.setImageResource(colorBean.icon);
                colorGroupItem.c.setVisibility(View.VISIBLE);
            } else {
                colorGroupItem.c.setVisibility(View.GONE);
            }

            colorGroupItem.b.setOnLongClickListener(v -> {
                if (finalJ == 0) deleteAllSavedColors();
                return false;
            });
        }

        Animation animation = binding.getRoot().getAnimation();
        if (animation != null) {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    smoothScrollToCurrentItem();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }
        initializeAttrsList();

    }

    private void showColorRemoveDialog(String color) {
        aB dialog = new aB(activity);
        dialog.a(R.drawable.delete_96);
        dialog.b(xB.b().a(activity, R.string.picker_color_title_delete_custom_color));
        dialog.a(xB.b().a(activity, R.string.picker_color_message_delete_custom_color));
        dialog.b(xB.b().a(activity, R.string.common_word_delete), v -> {
            removeSavedColor(color);
            notifyChanges();
            dialog.dismiss();
        });
        dialog.a(xB.b().a(activity, R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    private void initializeColorData(boolean isColorTransparent, boolean isColorNone) {
        colorList.add(new ColorBean("#FFF6F6F6", "CUSTOM", "#212121", R.drawable.checked_grey_32));
        if (sc_id != null)
            colorList.add(new ColorBean("#FFF6F6F6", "colors.xml", "#212121", R.drawable.checked_grey_32));
        if (hasMaterialColors)
            colorList.add(new ColorBean("#FFF6F6F6", "Material 3 Colors", "#212121", R.drawable.checked_grey_32));
        colorList.add(sq.p[0]);
        colorList.add(sq.q[0]);
        colorList.add(sq.r[0]);
        colorList.add(sq.s[0]);
        colorList.add(sq.t[0]);
        colorList.add(sq.u[0]);
        colorList.add(sq.v[0]);
        colorList.add(sq.w[0]);
        colorList.add(sq.x[0]);
        colorList.add(sq.y[0]);
        colorList.add(sq.z[0]);
        colorList.add(sq.A[0]);
        colorList.add(sq.B[0]);
        colorList.add(sq.C[0]);
        colorList.add(sq.D[0]);
        colorList.add(sq.E[0]);
        colorList.add(sq.F[0]);
        colorList.add(sq.G[0]);
        colorList.add(sq.H[0]);
        colorList.add(sq.I[0]);
        colorList.add(sq.J[0]);
        colorGroups.add(getSavedColorBeans());
        if (sc_id != null)
            colorGroups.add(geColorResBeans());
        if (hasMaterialColors)
            colorGroups.add(sq.p);
        colorGroups.add(sq.p);
        colorGroups.add(sq.q);
        colorGroups.add(sq.r);
        colorGroups.add(sq.s);
        colorGroups.add(sq.t);
        colorGroups.add(sq.u);
        colorGroups.add(sq.v);
        colorGroups.add(sq.w);
        colorGroups.add(sq.x);
        colorGroups.add(sq.y);
        colorGroups.add(sq.z);
        colorGroups.add(sq.A);
        colorGroups.add(sq.B);
        colorGroups.add(sq.C);
        colorGroups.add(sq.D);
        colorGroups.add(sq.E);
        colorGroups.add(sq.F);
        colorGroups.add(sq.G);
        colorGroups.add(sq.H);
        colorGroups.add(sq.I);
        colorGroups.add(sq.J);
        if (isColorTransparent) {
            colorList.add(sq.K[0]);
            colorGroups.add(sq.K);
        }
        if (isColorNone) {
            colorList.add(sq.L[0]);
            colorGroups.add(sq.L);
        }
    }

    private void removeSavedColor(String color) {
        String savedColors = colorPref.f("P24I1");
        if (savedColors.contains(color)) {
            String colorToRemove = color + ",";
            String colorToSave = savedColors.replaceAll(colorToRemove, "");
            colorPref.a("P24I1", ((Object) colorToSave));
            colorGroups.set(0, getSavedColorBeans());
            notifyChanges();
        }
    }

    private ColorBean[] getSavedColorBeans() {
        String savedColors = colorPref.f("P24I1");
        ColorBean[] colorBeansResult;
        if (!savedColors.isEmpty()) {
            String[] colorStrings = savedColors.split(",");
            ColorBean[] colorBeans = new ColorBean[colorStrings.length];
            int index = 0;
            while (index < colorStrings.length) {
                try {
                    int parsedColor = Color.parseColor(colorStrings[index]);
                    int red = Color.red(parsedColor);
                    int green = Color.green(parsedColor);
                    int blue = Color.blue(parsedColor);

                    int count = 0;
                    if (red > 240) count++;
                    if (green > 240) count++;
                    if (blue > 240) count++;


                    colorBeans[index] = new ColorBean(colorStrings[index], "CUSTOM",
                            count >= 2 ? "#212121" : "#ffffff",
                            R.drawable.checked_white_32);

                } catch (Exception e) {
                    colorPref.a();
                    colorBeans = new ColorBean[0];
                    break;
                }
                index++;
            }
            colorBeansResult = colorBeans;
        } else {
            colorBeansResult = new ColorBean[0];
        }

        return colorBeansResult;
    }


    private ColorBean[] geColorResBeans() {
        ColorBean[] colorBeansResult;
        String clrsPath = FileUtil.getExternalStorageDir().concat("/.sketchware/data/").concat(sc_id.concat("/files/resource/values/colors.xml"));
        parseColorsXML(FileUtil.readFileIfExist(clrsPath));

        if (!color_res_list.isEmpty()) {
            ColorBean[] colorBeans = new ColorBean[color_res_list.size()];
            int index = 0;
            while (index < color_res_list.size()) {
                try {
                    int parsedColor = Color.parseColor((color_res_list.get(index).get("colorValue")).toString());
                    int red = Color.red(parsedColor);
                    int green = Color.green(parsedColor);
                    int blue = Color.blue(parsedColor);

                    int count = 0;
                    if (red > 240) count++;
                    if (green > 240) count++;
                    if (blue > 240) count++;


                    colorBeans[index] = new ColorBean((color_res_list.get(index).get("colorValue")).toString(), (color_res_list.get(index).get("colorName")).toString(),
                            count >= 2 ? "#212121" : "#ffffff",
                            R.drawable.checked_white_32);

                } catch (Exception e) {
                    colorBeans = new ColorBean[0];
                    break;
                }
                index++;
            }
            colorBeansResult = colorBeans;
        } else {
            colorBeansResult = new ColorBean[0];
        }

        return colorBeansResult;
    }

    private void parseColorsXML(String colorXml) {
        ColorsEditorManager colorsEditorManager = new ColorsEditorManager();
        try {
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
                        if (tagName.equals("color")) {
                            colorName = parser.getAttributeValue(null, "name");
                        }
                        break;
                    case XmlPullParser.TEXT:
                        colorValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (tagName.equals("color")) {
                            if (colorName != null && isValidHexColor(colorsEditorManager.getColorValue(activity.getApplicationContext(), colorValue, 4))) {
                                HashMap<String, Object> colors = new HashMap<>();
                                colors.put("colorName", colorName);
                                colors.put("colorValue", String.format("#%8s", colorsEditorManager.getColorValue(activity.getApplicationContext(), colorValue, 4).replaceFirst("#", "")).replaceAll(" ", "F"));
                                color_res_list.add(colors);
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
        } catch (Exception ignored) {
        }
    }

     public static boolean isValidHexColor(String colorStr) {
        if (colorStr == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("^#([a-fA-F0-9]*)");
        Matcher matcher = pattern.matcher(colorStr);
        return matcher.matches();
    }

    private void smoothScrollToCurrentItem() {
        if (k < binding.layoutColorTitle.getChildCount()) {
            View childView = binding.layoutColorTitle.getChildAt(k);
            binding.layoutHsvColor.smoothScrollTo((int) childView.getX(), 0);
            binding.colorList.scrollToPosition(m);
        }
    }

    private void savePickedColor(String color) {
        String savedColors = colorPref.f("P24I1");
        if (savedColors.contains(color)) {
            bB.b(activity, xB.b().a(activity, R.string.picker_color_already_exist), 0).show();
        } else {
            String colorsToSave = color + "," + savedColors;
            colorPref.a("P24I1", (Object) (colorsToSave));
            colorGroups.set(0, getSavedColorBeans());
            notifyChanges();
            k = 0;
            smoothScrollToCurrentItem();
        }
    }

    private void notifyChanges() {
        l = 0;
        k = 0;
        m = 0;
        binding.colorList.getAdapter().notifyDataSetChanged();
    }

    public interface b {
        void a(int var1);

        void a(String var1, int var2);
    }

    public interface materialColorAttr {
        void selectedMaterialColorAttr(String attr, int attrId);
    }

    public interface materialLightColorAttr {
        void selectedMaterialLightColorAttr(String attr, int attrId);
    }

    private class ColorsAdapter extends RecyclerView.Adapter<ColorsAdapter.ColorViewHolder> {

        public ColorsAdapter() {
        }

        public int getItemCount() {
            return colorGroups.get(l).length;
        }

        public void onBindViewHolder(ColorViewHolder holder, int position) {
            ColorBean colorBean = ((ColorBean[]) colorGroups.get(l))[position];

            holder.tvColorCode.setText(colorBean.getColorCode(l == 0));
            if (position == 0) {
                holder.tvColorName.setText(((ColorBean[]) colorGroups.get(l))[0].colorName);
            } else {
                holder.tvColorName.setText("");
            }
            if (l == 1 && sc_id != null) {
                holder.tvColorName.setText(((ColorBean[]) colorGroups.get(l))[position].colorName);
            }

            holder.tvColorCode.setTextColor(((ColorBean[]) colorGroups.get(l))[position].displayNameColor);
            holder.tvColorName.setTextColor(((ColorBean[]) colorGroups.get(l))[position].displayNameColor);
            holder.layoutColorItem.setBackgroundColor(((ColorBean[]) colorGroups.get(l))[position].colorCode);
            if (position == m && l == k) {
                holder.imgSelector.setImageResource(((ColorBean[]) colorGroups.get(l))[position].icon);
                holder.imgSelector.setVisibility(View.VISIBLE);
            } else {
                holder.imgSelector.setVisibility(View.GONE);
            }

        }

        @NonNull
        public ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ColorViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.color_picker_item, parent, false));
        }

        private class ColorViewHolder extends RecyclerView.ViewHolder {

            public View layoutColorItem;
            public TextView tvColorCode;
            public TextView tvColorName;
            public ImageView imgSelector;

            public ColorViewHolder(View itemView) {
                super(itemView);
                layoutColorItem = itemView.findViewById(R.id.layout_color_item);
                tvColorCode = itemView.findViewById(R.id.tv_color_code);
                tvColorName = itemView.findViewById(R.id.tv_color_name);
                imgSelector = itemView.findViewById(R.id.img_selector);
                itemView.setOnClickListener(v -> {
                    if (colorPickerCallback != null) {
                        if (tvColorCode.getText().toString().equals("TRANSPARENT")) {
                            colorPickerCallback.a(0);
                        } else if (tvColorCode.getText().toString().equals("NONE")) {
                            colorPickerCallback.a(0xffffff);
                        } else if (l == 1 && sc_id != null) {
                            colorPickerCallback.a((String) tvColorName.getText(), Color.parseColor(tvColorCode.getText().toString()));
                        } else {
                            colorPickerCallback.a(Color.parseColor(tvColorCode.getText().toString()));
                        }
                    }
                    dismiss();
                });
                itemView.setOnLongClickListener(v -> {
                    if (l == 0) showColorRemoveDialog(tvColorCode.getText().toString());
                    return false;
                });
            }
        }
    }

    public void initializeAttrsList() {
        if (!hasMaterialColors) return;
        attributes = new ArrayList<>();

        attributes.add(new Attribute("colorSurface", R.attr.colorSurface));
        attributes.add(new Attribute("colorOnSurface", R.attr.colorOnSurface));
        attributes.add(new Attribute("colorPrimary", R.attr.colorPrimary));
        attributes.add(new Attribute("colorOnPrimary", R.attr.colorOnPrimary));
        attributes.add(new Attribute("colorPrimaryContainer", R.attr.colorPrimaryContainer));
        attributes.add(new Attribute("colorOnPrimaryContainer", R.attr.colorOnPrimaryContainer));
        attributes.add(new Attribute("colorSecondary", R.attr.colorSecondary));
        attributes.add(new Attribute("colorOnSecondary", R.attr.colorOnSecondary));
        attributes.add(new Attribute("colorSecondaryContainer", R.attr.colorSecondaryContainer));
        attributes.add(new Attribute("colorOnSecondaryContainer", R.attr.colorOnSecondaryContainer));
        attributes.add(new Attribute("colorTertiary", R.attr.colorTertiary));
        attributes.add(new Attribute("colorOnTertiary", R.attr.colorOnTertiary));
        attributes.add(new Attribute("colorTertiaryContainer", R.attr.colorTertiaryContainer));
        attributes.add(new Attribute("colorOnTertiaryContainer", R.attr.colorOnTertiaryContainer));
        attributes.add(new Attribute("colorSurfaceVariant", R.attr.colorSurfaceVariant));
        attributes.add(new Attribute("colorOnSurfaceVariant", R.attr.colorOnSurfaceVariant));
        attributes.add(new Attribute("colorSurfaceInverse", R.attr.colorSurfaceInverse));
        attributes.add(new Attribute("colorOnSurfaceInverse", R.attr.colorOnSurfaceInverse));
        attributes.add(new Attribute("colorError", R.attr.colorError));
        attributes.add(new Attribute("colorOnError", R.attr.colorOnError));
        attributes.add(new Attribute("colorErrorContainer", R.attr.colorErrorContainer));
        attributes.add(new Attribute("colorOnErrorContainer", R.attr.colorOnErrorContainer));
    }

    public class AttrAdapter extends RecyclerView.Adapter<AttrAdapter.AttrViewHolder> {

        private final ArrayList<Attribute> attributeList;
        private final Context themedDarkContext;
        private final Context themedLightContext;

        public AttrAdapter(ArrayList<Attribute> attributeList) {
            this.attributeList = attributeList;
            if (material3LibraryManager.isDynamicColorsEnabled()) {
                themedDarkContext = new ContextThemeWrapper(activity, R.style.ThemeOverlay_SketchwarePro_ViewEditor_Material3_Dark);
                themedLightContext = new ContextThemeWrapper(activity, R.style.ThemeOverlay_SketchwarePro_ViewEditor_Material3_Light);
            } else {
                themedDarkContext = new ContextThemeWrapper(activity, R.style.ThemeOverlay_SketchwarePro_ViewEditor_Material3_NON_DYNAMIC_Dark);
                themedLightContext = new ContextThemeWrapper(activity, R.style.ThemeOverlay_SketchwarePro_ViewEditor_Material3_NON_DYNAMIC_Light);
            }
        }

        @NonNull
        @Override
        public AttrViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemAttrBinding binding = ItemAttrBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new AttrViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull AttrViewHolder holder, int position) {
            Attribute attribute = attributeList.get(position);
            holder.binding.tvAttrName.setText(attribute.attrName());

            int darkColor = ThemeUtils.getColor(new View(themedDarkContext), attribute.attr());
            int lightColor = ThemeUtils.getColor(new View(themedLightContext), attribute.attr());

            holder.binding.darkContainer.setBackgroundColor(darkColor);
            holder.binding.lightContainer.setBackgroundColor(lightColor);
            holder.binding.darkTtl.setTextColor(lightColor);
            holder.binding.lightTtl.setTextColor(darkColor);

            holder.binding.getRoot().setOnClickListener(view -> {
                if (materialColorAttr != null) {
                    materialColorAttr.selectedMaterialColorAttr(attribute.attrName(), attribute.attr());
                }
                dismiss();
            });
        }

        @Override
        public int getItemCount() {
            return attributeList.size();
        }

        public static class AttrViewHolder extends RecyclerView.ViewHolder {
            private final ItemAttrBinding binding;

            public AttrViewHolder(@NonNull ItemAttrBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }

    public record Attribute(String attrName, int attr) {
    }

}

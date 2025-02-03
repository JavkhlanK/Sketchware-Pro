package com.besome.sketch.editor.view;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.besome.sketch.beans.ProjectFileBean;
import com.besome.sketch.beans.ViewBean;
import com.besome.sketch.ctrls.ViewIdSpinnerItem;
import com.besome.sketch.editor.property.ViewPropertyItems;
import com.besome.sketch.lib.ui.CustomHorizontalScrollView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;

import java.util.ArrayList;

import a.a.a.Iw;
import a.a.a.Jw;
import a.a.a.Kw;
import a.a.a.Lw;
import a.a.a.NB;
import a.a.a.Op;
import a.a.a.Qs;
import a.a.a.Rp;
import a.a.a.aB;
import a.a.a.bB;
import a.a.a.jC;
import a.a.a.mB;
import a.a.a.wB;
import mod.hey.studios.project.ProjectSettings;
import mod.hey.studios.util.Helper;
import pro.sketchware.R;

public class ViewProperty extends LinearLayout implements Kw {

    private final ArrayList<ViewBean> projectActivityViews = new ArrayList<>();
    private String sc_id;
    private ProjectFileBean projectFile;
    private Spinner spnWidget;
    private ViewIdsAdapter idsAdapter;
    private Jw propertyTargetChangeListener = null;
    private LinearLayout layoutPropertySeeAll;
    private ViewPropertyItems viewPropertyItems;
    private SeeAllPropertiesFloatingItem seeAll;
    private View propertyLayout;
    private ViewEvents viewEvent;
    private Iw propertyListener = null;
    private Lw propertyValueChangedListener;
    private LinearLayout layoutPropertyGroup;
    private int selectedGroupId;
    private ImageView imgSave;
    private ObjectAnimator showAllShower;
    private ObjectAnimator showAllHider;
    private boolean showAllVisible = true;

    public ViewProperty(Context context) {
        super(context);
        initialize(context);
    }

    public ViewProperty(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initialize(context);
    }

    @Override
    public void a(String str, Object obj) {
    }

    public void setOnEventClickListener(Qs onEventClickListener) {
        viewEvent.setOnEventClickListener(onEventClickListener);
    }

    public void setOnPropertyListener(Iw onPropertyListener) {
        propertyListener = onPropertyListener;
    }

    public void setOnPropertyTargetChangeListener(Jw onPropertyTargetChangeListener) {
        propertyTargetChangeListener = onPropertyTargetChangeListener;
    }

    public void setOnPropertyValueChangedListener(Lw onPropertyValueChangedListener) {
        propertyValueChangedListener = onPropertyValueChangedListener;
        viewPropertyItems.setOnPropertyValueChangedListener(viewBean -> {
            if (propertyValueChangedListener != null) {
                propertyValueChangedListener.a(viewBean);
            }
        });
    }

    private void cancelSeeAllAnimations() {
        if (showAllShower.isRunning()) {
            showAllShower.cancel();
        }
        if (showAllHider.isRunning()) {
            showAllHider.cancel();
        }
    }

    private void initializeSeeAllAnimations() {
        if (showAllShower == null) {
            showAllShower = ObjectAnimator.ofFloat(layoutPropertySeeAll, View.TRANSLATION_Y, 0.0f);
            showAllShower.setDuration(400L);
            showAllShower.setInterpolator(new DecelerateInterpolator());
        }
        if (showAllHider == null) {
            showAllHider = ObjectAnimator.ofFloat(layoutPropertySeeAll, View.TRANSLATION_Y, wB.a(getContext(), 84.0f));
            showAllHider.setDuration(200L);
            showAllHider.setInterpolator(new DecelerateInterpolator());
        }
    }

    public void d() {
        if (viewPropertyItems != null) {
            viewPropertyItems.save();
        }
    }

    public void e() {
        for (int i = 0; i < layoutPropertyGroup.getChildCount(); i++) {
            GroupItem item = (GroupItem) layoutPropertyGroup.getChildAt(i);

            if (selectedGroupId == (Integer) item.getTag()) {
                item.setSelected(true);
                item.title.setTextColor(getResources().getColor(R.color.view_property_tab_active_text));
                item.animate().scaleX(0.9f).scaleY(0.9f).start();
            } else {
                item.setSelected(false);
                item.title.setTextColor(getResources().getColor(R.color.view_property_tab_deactive_text));
                item.animate().scaleX(0.8f).scaleY(0.8f).start();
            }
        }
        if (idsAdapter.getSelectedItemPosition() < projectActivityViews.size()) {
            ViewBean viewBean = projectActivityViews.get(idsAdapter.getSelectedItemPosition());
            if (selectedGroupId == 0) {
                propertyLayout.setVisibility(VISIBLE);
                layoutPropertySeeAll.setVisibility(VISIBLE);
                viewPropertyItems.a(sc_id, viewBean);
                a(viewBean);
                viewEvent.setVisibility(GONE);
            } else if (selectedGroupId == 1) {
                propertyLayout.setVisibility(VISIBLE);
                viewPropertyItems.e(viewBean);
                layoutPropertySeeAll.setVisibility(GONE);
            } else if (selectedGroupId == 2) {
                propertyLayout.setVisibility(GONE);
                viewEvent.setVisibility(VISIBLE);
                viewEvent.setData(sc_id, projectFile, viewBean);
            }
        }
    }

    private void showSaveToCollectionDialog() {
        aB dialog = new aB((Activity) getContext());
        dialog.b(Helper.getResString(R.string.view_widget_favorites_save_title));
        dialog.a(R.drawable.ic_bookmark_red_48dp);
        View view = wB.a(getContext(), R.layout.property_popup_save_to_favorite);
        ((TextView) view.findViewById(R.id.tv_favorites_guide)).setText(Helper.getResString(R.string.view_widget_favorites_save_guide_new));
        EditText editText = view.findViewById(R.id.ed_input);
        editText.setPrivateImeOptions("defaultInputmode=english;");
        editText.setLines(1);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        NB validator = new NB(getContext(), view.findViewById(R.id.ti_input), Rp.h().g());
        dialog.a(view);
        dialog.b(Helper.getResString(R.string.common_word_save), v -> {
            if (!mB.a() && validator.b()) {
                String widgetName = editText.getText().toString();
                ArrayList<ViewBean> viewBeans = jC.a(sc_id).b(projectFile.getXmlName(), projectActivityViews.get(idsAdapter.getSelectedItemPosition()));
                for (ViewBean viewBean : viewBeans) {
                    String backgroundResource = viewBean.layout.backgroundResource;
                    String resName = viewBean.image.resName;
                    if (backgroundResource != null && !backgroundResource.equals("NONE") && jC.d(sc_id).l(backgroundResource) && !Op.g().b(backgroundResource)) {
                        try {
                            Op.g().a(sc_id, jC.d(sc_id).g(backgroundResource));
                        } catch (Exception e) {
                            e.printStackTrace();
                            bB.b(getContext(), e.getMessage(), bB.TOAST_NORMAL).show();
                        }
                    }
                    if (resName != null && !resName.equals("default_image") && !resName.equals("NONE") && jC.d(sc_id).l(resName) && !Op.g().b(resName)) {
                        try {
                            Op.g().a(sc_id, jC.d(sc_id).g(resName));
                        } catch (Exception e) {
                            bB.b(getContext(), e.getMessage(), bB.TOAST_NORMAL).show();
                        }
                    }
                }
                Rp.h().a(widgetName, viewBeans, true);
                if (propertyListener != null) {
                    propertyListener.a();
                }
                bB.a(getContext(), Helper.getResString(R.string.common_message_complete_save), bB.TOAST_NORMAL).show();
                dialog.dismiss();
            }
        });
        dialog.a(Helper.getResString(R.string.common_word_cancel), Helper.getDialogDismissListener(dialog));
        dialog.show();
    }

    private void initialize(Context context) {
        wB.a(context, this, R.layout.view_property);
        layoutPropertyGroup = findViewById(R.id.layout_property_group);
        CustomHorizontalScrollView hcvProperty = findViewById(R.id.hcv_property);
        propertyLayout = findViewById(R.id.property_layout);
        LinearLayout propertyContents = findViewById(R.id.property_contents);
        layoutPropertySeeAll = findViewById(R.id.layout_property_see_all);
        viewEvent = findViewById(R.id.view_event);
        hcvProperty.setHorizontalScrollBarEnabled(false);
        hcvProperty.setOnScrollChangedListener((scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (Math.abs(scrollX - oldScrollY) <= 5) {
                return;
            }
            int maxScrollX = hcvProperty.getChildAt(0).getWidth() - hcvProperty.getWidth();
            if (scrollX > 100 && scrollX < maxScrollX) {
                if (scrollX > oldScrollX) {
                    if (showAllVisible) {
                        showAllVisible = false;
                        cancelSeeAllAnimations();
                        showAllHider.start();
                    }
                } else {
                    if (!showAllVisible) {
                        showAllVisible = true;
                        cancelSeeAllAnimations();
                        showAllShower.start();
                    }
                }
            } else if (scrollX >= maxScrollX) {
                if (showAllVisible) {
                    showAllVisible = false;
                    cancelSeeAllAnimations();
                    showAllHider.start();
                }
            } else {
                if (!showAllVisible) {
                    showAllVisible = true;
                    cancelSeeAllAnimations();
                    showAllShower.start();
                }
            }
        });
        imgSave = findViewById(R.id.img_save);
        imgSave.setOnClickListener(v -> {
            if (!mB.a()) {
                showSaveToCollectionDialog();
            }
        });
        spnWidget = findViewById(R.id.spn_widget);
        idsAdapter = new ViewIdsAdapter(context, projectActivityViews);
        spnWidget.setAdapter(idsAdapter);
        spnWidget.setSelection(0);
        spnWidget.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idsAdapter.setPosition(position);
                selectView(projectActivityViews.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        initializeGroups();
        initializeSeeAllAnimations();
        viewPropertyItems = new ViewPropertyItems(getContext());
        viewPropertyItems.setOrientation(HORIZONTAL);
        propertyContents.addView(viewPropertyItems);
    }

    public void selectView(ViewBean viewBean) {
        if (propertyTargetChangeListener != null) {
            propertyTargetChangeListener.a(viewBean.id);
        }
        if ("_fab".equals(viewBean.id)) {
            imgSave.setVisibility(GONE);
        } else {
            imgSave.setVisibility(VISIBLE);
        }
        viewPropertyItems.setProjectFileBean(projectFile);
        e();
    }

    public boolean isProjectActivityViewsListEmpty() {
        return projectActivityViews.isEmpty();
    }

    public void a(String sc_id, ProjectFileBean projectFileBean) {
        this.sc_id = sc_id;
        projectFile = projectFileBean;
        viewPropertyItems.setProjectSettings(new ProjectSettings(sc_id));
    }

    public void a(String str) {
        for (int i = 0; i < projectActivityViews.size(); i++) {
            if (projectActivityViews.get(i).id.equals(str)) {
                spnWidget.setSelection(i);
                return;
            }
        }
    }

    public void addActivityViews(ArrayList<ViewBean> activityViews, ViewBean fab) {
        projectActivityViews.clear();
        projectActivityViews.addAll(activityViews);
        if (fab != null) {
            projectActivityViews.add(0, fab);
        }
        idsAdapter.notifyDataSetChanged();
    }

    private void initializeGroups() {
        addGroup(0, R.string.property_group_basic);
        addGroup(1, R.string.property_group_recent);
        addGroup(2, R.string.property_group_event);
    }

    private void addGroup(int id, int labelResId) {
        GroupItem group = new GroupItem(getContext());
        group.configure(id, labelResId);
        group.setTag(id);
        layoutPropertyGroup.addView(group);
    }

    private void a(ViewBean viewBean) {
        if (seeAll == null) {
            seeAll = new SeeAllPropertiesFloatingItem(getContext());
            seeAll.configure(R.drawable.color_more_96, R.string.common_word_see_all);
            seeAll.setView(viewBean);
            layoutPropertySeeAll.addView(seeAll);
            return;
        }
        seeAll.setView(viewBean);
    }

    private static class ViewIdsAdapter extends BaseAdapter {

        private final Context context;
        private final ArrayList<ViewBean> views;
        private int selectedItemPosition;

        public ViewIdsAdapter(Context context, ArrayList<ViewBean> arrayList) {
            this.context = context;
            views = arrayList;
        }

        private void setPosition(int position) {
            selectedItemPosition = position;
        }

        @Override
        public int getCount() {
            if (views == null) {
                return 0;
            } else {
                return views.size();
            }
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getSpinnerItem(position, convertView, selectedItemPosition == position, true);
        }

        @Override
        public ViewBean getItem(int position) {
            return views.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getSpinnerItem(position, convertView, false, false);
        }

        private int getSelectedItemPosition() {
            return selectedItemPosition;
        }

        private ViewIdSpinnerItem getSpinnerItem(int position, View convertView, boolean isSelected, boolean isDropDownView) {
            ViewIdSpinnerItem item;
            if (convertView != null) {
                item = (ViewIdSpinnerItem) convertView;
            } else {
                item = new ViewIdSpinnerItem(context);
                item.setTextSize(R.dimen.text_size_body_small);
            }
            item.setDropDown(isDropDownView);

            ViewBean view = views.get(position);
            item.a(ViewBean.getViewTypeResId(view.type), view.id, isSelected);
            return item;
        }
    }

    private class GroupItem extends LinearLayout implements View.OnClickListener {

        private TextView title;

        public GroupItem(Context context) {
            super(context);
            initialize(context);
        }

        private void initialize(Context context) {
            wB.a(context, this, R.layout.property_group_item);
            title = findViewById(R.id.tv_title);
        }

        @Override
        public void onClick(View view) {
            selectedGroupId = (Integer) view.getTag();
            e();
        }

        private void configure(int id, int labelResId) {
            setTag(id);
            title.setText(Helper.getResString(labelResId));
            setOnClickListener(this);
        }
    }

    private class SeeAllPropertiesFloatingItem extends LinearLayout implements View.OnClickListener {

        private final MaterialCardView propertyMenuItem;
        private final ImageView icon;
        private final TextView title;
        private ViewBean viewBean;

        public SeeAllPropertiesFloatingItem(Context context) {
            super(context);

            wB.a(context, this, R.layout.property_grid_item);
            propertyMenuItem = findViewById(R.id.property_menu_item);
            icon = findViewById(R.id.img_icon);
            title = findViewById(R.id.tv_title);
        }

        @Override
        public void onClick(View v) {
            if (propertyListener != null) {
                propertyListener.a(projectFile.getXmlName(), viewBean);
            }
        }

        private void configure(int imageResId, int propertyNameResId) {
            propertyMenuItem.setVisibility(VISIBLE);
            propertyMenuItem.setCardBackgroundColor(MaterialColors.getColor(this, R.attr.colorPrimaryContainer));
            icon.setImageResource(imageResId);
            icon.setColorFilter(MaterialColors.getColor(this, R.attr.colorOnPrimaryContainer), PorterDuff.Mode.SRC_ATOP);
            title.setText(Helper.getResString(propertyNameResId));
            title.setTextColor(MaterialColors.getColor(this, R.attr.colorOnPrimaryContainer));
            setOnClickListener(this);
        }

        private void setView(ViewBean viewBean) {
            this.viewBean = viewBean;
        }
    }
}

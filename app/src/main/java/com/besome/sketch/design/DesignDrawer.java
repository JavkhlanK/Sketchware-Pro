package com.besome.sketch.design;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;
import pro.sketchware.R;
import pro.sketchware.databinding.DesignDrawerBinding;
import pro.sketchware.databinding.DesignDrawerItemBinding;

import a.a.a.mB;
import a.a.a.wB;
import dev.chrisbanes.insetter.Insetter;
import dev.chrisbanes.insetter.Side;
import mod.hey.studios.util.Helper;

public class DesignDrawer extends LinearLayout implements View.OnClickListener {
    private DesignDrawerBinding binding;

    public DesignDrawer(Context context) {
        this(context, null);
    }

    public DesignDrawer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private DrawerItem addDrawerItem(int tag, int iconResId, int titleResId, int descriptionResId) {
        DrawerItem drawerItem = new DrawerItem(getContext());
        drawerItem.setContent(iconResId, Helper.getResString(drawerItem, titleResId), Helper.getResString(drawerItem, descriptionResId));
        drawerItem.setOnClickListener(tag, this);
        return drawerItem;
    }

    private void addLayoutMenu(int tag, int iconResId, int titleResId, int descriptionResId) {
        View drawerItem = addDrawerItem(tag, iconResId, titleResId, descriptionResId);
        binding.layoutMenus.addView(drawerItem);
    }

    private void applyDrawerLayoutInsets() {
        var layoutDirection = getResources().getConfiguration().getLayoutDirection();
        Insetter.builder()
                .padding(WindowInsetsCompat.Type.navigationBars(),
                        Side.create(layoutDirection == LAYOUT_DIRECTION_RTL, false,
                                layoutDirection == LAYOUT_DIRECTION_LTR, false))
                .applyToView(binding.layoutDrawer);
    }

    private void applyTitleInsets() {
        Insetter.builder()
                .margin(WindowInsetsCompat.Type.statusBars())
                .applyToView(binding.tvTitleConfiguration);
    }

    private void applyBottomMenuInsets() {
        Insetter.builder()
                .margin(WindowInsetsCompat.Type.navigationBars(), Side.BOTTOM)
                .applyToView(binding.layoutBottomMenus);
    }

    private void initialize(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = DesignDrawerBinding.inflate(inflater, this, true);

        applyDrawerLayoutInsets();
        applyTitleInsets();
        applyBottomMenuInsets();

        binding.tvTitleConfiguration.setText(Helper.getResString(binding.tvTitleConfiguration, R.string.design_drawer_menu_title));
        binding.tvTitleGlobal.setText(Helper.getResString(binding.tvTitleGlobal, R.string.design_drawer_menu_bottom_title));

        binding.layoutBottomMenus.addView(
            addDrawerItem(1, R.drawable.ic_mtrl_bookmark, R.string.design_drawer_menu_title_collection,
                R.string.design_drawer_menu_description_collection));

        addLayoutMenu(3, R.drawable.ic_mtrl_category, R.string.design_drawer_menu_title_library,
            R.string.design_drawer_menu_description_library);

        addLayoutMenu(4, R.drawable.ic_mtrl_devices, R.string.design_drawer_menu_title_view,
            R.string.design_drawer_menu_description_view);

        addLayoutMenu(5, R.drawable.ic_mtrl_image, R.string.design_drawer_menu_title_image,
            R.string.design_drawer_menu_description_image);

        addLayoutMenu(6, R.drawable.ic_mtrl_music, R.string.design_drawer_menu_title_sound,
            R.string.design_drawer_menu_description_sound);

        addLayoutMenu(7, R.drawable.ic_mtrl_font, R.string.design_drawer_menu_title_font,
            R.string.design_drawer_menu_description_font);

        addLayoutMenu(8, R.drawable.ic_mtrl_java, R.string.text_title_menu_java,
            R.string.text_subtitle_menu_java);

        addLayoutMenu(9, R.drawable.ic_mtrl_folder, R.string.text_title_menu_resource,
            R.string.text_subtitle_menu_resource);

        addLayoutMenu(23, R.drawable.ic_mtrl_folder_code, R.string.text_title_menu_resource_editor,
                R.string.text_subtitle_menu_resource_editor);

        addLayoutMenu(10, R.drawable.ic_mtrl_file_present, R.string.text_title_menu_assets,
            R.string.text_subtitle_menu_assets);

        addLayoutMenu(11, R.drawable.ic_mtrl_shield_check, R.string.text_title_menu_permission,
            R.string.text_subtitle_menu_permission);

        addLayoutMenu(12, R.drawable.ic_mtrl_inject, R.string.design_drawer_menu_injection,
            R.string.design_drawer_menu_injection_subtitle);

        addLayoutMenu(13, R.drawable.ic_mtrl_deployed_code, R.string.design_drawer_menu_androidmanifest,
            R.string.design_drawer_menu_androidmanifest_subtitle);

        addLayoutMenu(20, R.drawable.ic_mtrl_block, R.string.design_drawer_menu_customblocks,
            R.string.design_drawer_menu_customblocks_subtitle);

        addLayoutMenu(17, R.drawable.ic_mtrl_shield_lock, R.string.design_drawer_menu_proguard,
            R.string.design_drawer_menu_proguard_subtitle);

        addLayoutMenu(18, R.drawable.ic_mtrl_regular_expression, R.string.design_drawer_menu_stringfog,
            R.string.design_drawer_menu_stringfog_subtitle);

        addLayoutMenu(16, R.drawable.ic_mtrl_frame_source, R.string.design_drawer_menu_title_source_code,
            R.string.design_drawer_menu_description_source_code);

        addLayoutMenu(14, R.drawable.ic_mtrl_code, R.string.design_drawer_menu_title_xml_command,
            R.string.design_drawer_menu_description_xml_command);

        addLayoutMenu(22, R.drawable.ic_mtrl_article, R.string.design_drawer_menu_title_logcat_reader,
            R.string.design_drawer_menu_subtitle_logcat_reader);
    }

    @Override
    public void onClick(View v) {
        if (!mB.a()) {
            if (getContext() instanceof DesignActivity activity) {
                switch ((Integer) v.getTag()) {
                    case 1:
                        activity.toCollectionManager();
                        return;

                    case 3:
                        activity.toLibraryManager();
                        return;

                    case 4:
                        activity.toViewManager();
                        return;

                    case 5:
                        activity.toImageManager();
                        return;

                    case 6:
                        activity.toSoundManager();
                        return;

                    case 7:
                        activity.toFontManager();
                        return;

                    case 8:
                        activity.toJavaManager();
                        return;

                    case 9:
                        activity.toResourceManager();
                        return;

                    case 10:
                        activity.toAssetManager();
                        return;

                    case 11:
                        activity.toPermissionManager();
                        return;

                    case 12:
                        activity.toAppCompatInjectionManager();
                        return;

                    case 13:
                        activity.toAndroidManifestManager();
                        return;

                    case 14:
                        activity.toXMLCommandManager();
                        return;

                    case 16:
                        activity.toSourceCodeViewer();
                        return;

                    case 17:
                        activity.toProguardManager();
                        return;

                    case 18:
                        activity.toStringFogManager();
                        return;

                    case 20:
                        activity.toCustomBlocksViewer();
                        return;

                    case 22:
                        activity.toLogReader();
                        return;
                    case 23:
                        activity.toResourceEditor();
                        return;
                    case 2:
                    default:
                }
            }
        }
    }

    private static class DrawerItem extends LinearLayout {
        private final DesignDrawerItemBinding binding;

        public DrawerItem(Context context) {
            this(context, null);
        }

        public DrawerItem(Context context, AttributeSet attrs) {
            super(context, attrs);
            LayoutInflater inflater = LayoutInflater.from(context);
            binding = DesignDrawerItemBinding.inflate(inflater, this, true);
        }

        public void setContent(int iconResId, String rootTitleText, String subTitleText) {
            binding.imgIcon.setImageResource(iconResId);
            binding.tvRootTitle.setText(rootTitleText);
            binding.tvSubTitle.setText(subTitleText);
        }

        public void setOnClickListener(int tag, OnClickListener listener) {
            binding.getRoot().setTag(tag);
            binding.getRoot().setOnClickListener(listener);
        }
    }
}
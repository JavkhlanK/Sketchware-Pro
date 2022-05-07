package com.besome.sketch.editor.property;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.sketchware.remod.R;

import a.a.a.Kw;
import a.a.a.mB;
import a.a.a.wB;
import a.a.a.xB;

public class PropertySwitchSingleLineItem extends LinearLayout implements View.OnClickListener {

    public String key = "";
    public boolean value = false;
    public TextView tvName;
    public Switch switchValue;
    public ImageView imgLeftIcon;
    public int icon;
    public View propertyItem;
    public View propertyMenuItem;
    public Kw valueChangeListener;

    public PropertySwitchSingleLineItem(Context context, boolean z) {
        super(context);
        initialize(context, z);
    }

    private void initialize(Context context, boolean z) {
        wB.a(context, this, R.layout.property_switch_item_singleline);
        tvName = findViewById(R.id.tv_name);
        switchValue = findViewById(R.id.switch_value);
        imgLeftIcon = findViewById(R.id.img_left_icon);
        propertyItem = findViewById(R.id.property_item);
        propertyMenuItem = findViewById(R.id.property_menu_item);
        if (z) {
            setOnClickListener(this);
            setSoundEffectsEnabled(true);
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        mB.a(this);
        this.key = key;
        int identifier = getResources().getIdentifier(key, "string", getContext().getPackageName());
        if (identifier > 0) {
            tvName.setText(xB.b().a(getResources(), identifier));
            switch (this.key) {
                case "property_checked":
                    icon = R.drawable.ok_48;
                    break;

                case "property_single_line":
                    icon = R.drawable.horizontal_line_48;
                    break;

                case "property_enabled":
                    icon = R.drawable.light_on_48;
                    break;

                case "property_clickable":
                    icon = R.drawable.natural_user_interface2_48;
            }
            if (propertyMenuItem.getVisibility() == VISIBLE) {
                ((ImageView) findViewById(R.id.img_icon)).setImageResource(icon);
                ((TextView) findViewById(R.id.tv_title)).setText(xB.b().a(getContext(), identifier));
                return;
            }
            imgLeftIcon.setImageResource(icon);
        }
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
        switchValue.setChecked(value);
    }

    public void onClick(View view) {
        setValue(!value);
        if (valueChangeListener != null) {
            valueChangeListener.a(key, value);
        }
    }

    public void setOnPropertyValueChangeListener(Kw onPropertyValueChangeListener) {
        valueChangeListener = onPropertyValueChangeListener;
    }

    public void setOrientationItem(int orientationItem) {
        if (orientationItem == 0) {
            propertyItem.setVisibility(GONE);
            propertyMenuItem.setVisibility(VISIBLE);
        } else {
            propertyItem.setVisibility(VISIBLE);
            propertyMenuItem.setVisibility(GONE);
        }
    }
}

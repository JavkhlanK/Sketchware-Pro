package com.besome.sketch.editor.manage.font;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.besome.sketch.beans.ProjectResourceBean;
import com.besome.sketch.lib.base.BaseDialogActivity;
import com.besome.sketch.lib.ui.EasyDeleteEditText;
import com.sketchware.remod.R;

import java.util.ArrayList;

import a.a.a.HB;
import a.a.a.Np;
import a.a.a.WB;
import a.a.a.bB;
import a.a.a.mB;
import a.a.a.uq;
import a.a.a.xB;
import a.a.a.yy;

public class AddFontActivity extends BaseDialogActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_FONT_PICKER = 229;

    private TextView fontPreview;
    private CheckBox addOrAddedToCollection;
    private Uri fontUri = null;
    private boolean validFontPicked;
    private String sc_id;
    private EditText fontName;
    private WB fontNameValidator;
    private ImageView selectFile;

    private void addToCollectionIfNeeded() {
        if (a(fontNameValidator)) {
            String fontName = this.fontName.getText().toString();
            String pickedFontFilePath = HB.a(this, fontUri);
            if (pickedFontFilePath != null) {
                ProjectResourceBean resourceBean = new ProjectResourceBean(ProjectResourceBean.PROJECT_RES_TYPE_FILE, fontName, pickedFontFilePath);
                resourceBean.savedPos = 1;
                resourceBean.isNew = true;
                if (addOrAddedToCollection.isChecked()) {
                    try {
                        Np.g().a(sc_id, resourceBean);
                    } catch (Exception e) {
                        // Well, (parts of) the bytecode's lying, yy can be thrown.
                        //noinspection ConstantConditions
                        if (e instanceof yy) {
                            switch (e.getMessage()) {
                                case "duplicate_name":
                                    bB.b(this, xB.b().a(this, R.string.collection_duplicated_name), Toast.LENGTH_LONG).show();
                                    break;

                                case "file_no_exist":
                                    bB.b(this, xB.b().a(this, R.string.collection_no_exist_file), Toast.LENGTH_LONG).show();
                                    break;

                                case "fail_to_copy":
                                    bB.b(this, xB.b().a(this, R.string.collection_failed_to_copy), Toast.LENGTH_LONG).show();
                                    break;

                                default:
                            }

                            return;
                        } else {
                            throw e;
                        }
                    }
                }

                Intent intent = new Intent();
                intent.putExtra("resource_bean", resourceBean);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri intentData = data.getData();
        if (requestCode == REQUEST_CODE_FONT_PICKER && resultCode == RESULT_OK && intentData != null && selectFile != null) {
            fontUri = intentData;
            try {
                String pickedFilePath = HB.a(this, fontUri);
                if (pickedFilePath != null) {
                    pickedFilePath.substring(pickedFilePath.lastIndexOf("."));
                    validFontPicked = true;
                    fontPreview.setTypeface(Typeface.createFromFile(pickedFilePath));
                    if (fontName.getText() == null || fontName.getText().length() <= 0) {
                        int lastIndexOf = pickedFilePath.lastIndexOf("/");
                        int lastIndexOf2 = pickedFilePath.lastIndexOf(".");
                        if (lastIndexOf2 <= 0) {
                            lastIndexOf2 = pickedFilePath.length();
                        }
                        fontName.setText(pickedFilePath.substring(lastIndexOf + 1, lastIndexOf2));
                    }
                    fontPreview.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                validFontPicked = false;
                fontPreview.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.common_dialog_cancel_button) {
            finish();
        } else if (id == R.id.common_dialog_ok_button) {
            addToCollectionIfNeeded();
        } else if (id == R.id.select_file) {
            if (!mB.a()) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(Intent.createChooser(intent, xB.b().a(this, R.string.common_word_choose)), REQUEST_CODE_FONT_PICKER);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        e(xB.b().a(this, R.string.design_manager_font_title_add_font));
        d(xB.b().a(this, R.string.common_word_save));
        b(xB.b().a(this, R.string.common_word_cancel));
        setContentView(R.layout.manage_font_add);

        Intent intent = getIntent();
        sc_id = intent.getStringExtra("sc_id");
        addOrAddedToCollection = findViewById(R.id.chk_collection);
        TextView addOrAddedToCollectionLabel = findViewById(R.id.tv_collection);
        EasyDeleteEditText edFontName = findViewById(R.id.ed_input);
        selectFile = findViewById(R.id.select_file);
        fontPreview = findViewById(R.id.font_preview);
        fontName = edFontName.getEditText();
        edFontName.setHint(xB.b().a(this, R.string.design_manager_font_hint_enter_font_name));
        fontNameValidator = new WB(this, edFontName.getTextInputLayout(), uq.b, intent.getStringArrayListExtra("font_names"));
        fontName.setPrivateImeOptions("defaultInputmode=english;");
        fontPreview.setText(xB.b().a(this, R.string.design_manager_font_description_look_like_this));
        addOrAddedToCollectionLabel.setText(xB.b().a(this, R.string.design_manager_title_add_to_collection));
        selectFile.setOnClickListener(this);
        r.setOnClickListener(this);
        s.setOnClickListener(this);
        if (intent.getIntExtra("request_code", -1) == 272) {
            e(xB.b().a(this, R.string.design_manager_font_title_edit_font));
            fontNameValidator = new WB(this, edFontName.getTextInputLayout(), uq.b, new ArrayList<>());
            fontName.setText(((ProjectResourceBean) intent.getParcelableExtra("resource_bean")).resName);
            fontName.setEnabled(false);
            addOrAddedToCollection.setEnabled(false);
        }
    }

    private boolean a(WB wb) {
        if (!wb.b()) {
            return false;
        }
        if (validFontPicked && fontUri != null) {
            return true;
        }
        selectFile.startAnimation(AnimationUtils.loadAnimation(this, R.anim.ani_1));
        return false;
    }
}

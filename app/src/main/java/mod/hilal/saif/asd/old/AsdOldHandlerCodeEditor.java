package mod.hilal.saif.asd.old;

import android.view.View;

import com.besome.sketch.editor.LogicEditorActivity;

import a.a.a.Ss;
import mod.SketchwareUtil;
import mod.hey.studios.lib.code_editor.CodeEditorEditText;

public class AsdOldHandlerCodeEditor implements View.OnClickListener {

    private final CodeEditorEditText codeEditorEditText;
    private final AsdOldDialog dialog;
    private final boolean isNumber;
    private final LogicEditorActivity logicEditorActivity;
    private final Ss ss;

    public AsdOldHandlerCodeEditor(LogicEditorActivity activity, boolean isNumber, Ss ss, AsdOldDialog asdOldDialog, CodeEditorEditText codeEditorEditText) {
        this.logicEditorActivity = activity;
        this.isNumber = isNumber;
        this.ss = ss;
        dialog = asdOldDialog;
        this.codeEditorEditText = codeEditorEditText;
    }

    @Override
    public void onClick(View v) {
        String editable = codeEditorEditText.getText().toString();
        if (isNumber) {
            String parsedDouble = "";
            try {
                double parseDouble = Double.parseDouble(editable);
                if (!Double.isNaN(parseDouble) && !Double.isInfinite(parseDouble)) {
                    parsedDouble = editable;
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            editable = parsedDouble;
        } else if (editable.length() > 0 && editable.charAt(0) == '@') {
            editable = " " + editable;
        }
        logicEditorActivity.a(ss, (Object) editable);
        SketchwareUtil.hideKeyboard(v);
        dialog.dismiss();
    }
}

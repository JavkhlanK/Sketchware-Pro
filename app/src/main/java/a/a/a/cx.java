package a.a.a;

import android.view.View;
import com.besome.sketch.editor.property.PropertyInputItem;

public class cx implements View.OnClickListener {

    public final aB a;
    public final PropertyInputItem b;

    public cx(PropertyInputItem propertyInputItem, aB aBVar) {
        this.b = propertyInputItem;
        this.a = aBVar;
    }

    public void onClick(View view) {
        this.a.dismiss();
    }
}
package pro.sketchware.activities.resources.editors.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import pro.sketchware.activities.resources.editors.ResourcesEditorActivity;

public class EditorsAdapter extends FragmentStateAdapter {

    private final ResourcesEditorActivity activity;

    public EditorsAdapter(@NonNull ResourcesEditorActivity activity) {
        super(activity);
        this.activity = activity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return switch (position) {
            case 0 -> activity.stringsEditor;
            case 1 -> activity.colorsEditor;
            case 2 -> activity.stylesEditor;
            case 3 -> activity.themesEditor;
            case 4 -> activity.arraysEditor;
            default -> throw new IllegalArgumentException("Invalid position");
        };
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}

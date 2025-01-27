package pro.sketchware.activities.resources.editors.adapters;

import static com.besome.sketch.design.DesignActivity.sc_id;
import static com.besome.sketch.editor.LogicEditorActivity.getAllJavaFileNames;
import static com.besome.sketch.editor.LogicEditorActivity.getAllXmlFileNames;

import static pro.sketchware.utility.UI.animateLayoutChanges;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.besome.sketch.beans.BlockBean;
import com.besome.sketch.beans.ViewBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import a.a.a.aB;
import a.a.a.eC;
import a.a.a.jC;
import mod.hey.studios.util.Helper;
import pro.sketchware.R;
import pro.sketchware.activities.resources.editors.ResourcesEditorActivity;
import pro.sketchware.databinding.PalletCustomviewBinding;
import pro.sketchware.databinding.ViewStringEditorAddBinding;
import pro.sketchware.utility.SketchwareUtil;

public class StringsAdapter extends RecyclerView.Adapter<StringsAdapter.ViewHolder> {

    private final ArrayList<HashMap<String, Object>> originalData;
    private ArrayList<HashMap<String, Object>> filteredData;
    private final ResourcesEditorActivity activity;
    private final HashMap<Integer, String> notesMap;

    public StringsAdapter(ResourcesEditorActivity activity, ArrayList<HashMap<String, Object>> data, HashMap<Integer, String> notesMap) {
        this.originalData = new ArrayList<>(data);
        this.filteredData = data;
        this.activity = activity;
        this.notesMap = notesMap;
    }

    @NonNull
    @Override
    public StringsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PalletCustomviewBinding itemBinding = PalletCustomviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new StringsAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull StringsAdapter.ViewHolder holder, int position) {
        HashMap<String, Object> item = filteredData.get(position);
        String key = (String) item.get("key");
        String text = (String) item.get("text");
        holder.binding.title.setHint(key);
        holder.binding.sub.setText(text);

        if (notesMap.containsKey(position)) {
            holder.binding.tvTitle.setText(notesMap.get(position));
            holder.binding.tvTitle.setVisibility(View.VISIBLE);
        } else {
            holder.binding.tvTitle.setVisibility(View.GONE);
        }

        holder.binding.backgroundCard.setOnClickListener(v -> {
            int adapterPosition = holder.getAbsoluteAdapterPosition();
            HashMap<String, Object> currentItem = filteredData.get(adapterPosition);

            aB dialog = new aB(activity);
            ViewStringEditorAddBinding dialogBinding = ViewStringEditorAddBinding.inflate(activity.getLayoutInflater());

            dialogBinding.stringKeyInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    animateLayoutChanges(dialogBinding.getRoot());
                    dialogBinding.importantNote.setVisibility(s.toString().equals("app_name") ? View.VISIBLE : View.GONE);
                }
            });
            dialogBinding.stringKeyInput.setText((String) currentItem.get("key"));
            dialogBinding.stringValueInput.setText((String) currentItem.get("text"));
            dialogBinding.stringHeaderInput.setText(notesMap.getOrDefault(adapterPosition, ""));

            if ("app_name".equals(currentItem.get("key"))) {
                dialogBinding.stringKeyInput.setEnabled(false);
            }

            dialog.b("Edit string");
            dialog.b("Save", v1 -> {
                String keyInput = Objects.requireNonNull(dialogBinding.stringKeyInput.getText()).toString();
                String valueInput = Objects.requireNonNull(dialogBinding.stringValueInput.getText()).toString();
                if (keyInput.isEmpty() || valueInput.isEmpty()) {
                    SketchwareUtil.toast("Please fill in all fields", Toast.LENGTH_SHORT);
                    return;
                }
                currentItem.put("key", keyInput);
                currentItem.put("text", valueInput);
                String note = Objects.requireNonNull(dialogBinding.stringHeaderInput.getText()).toString().trim();
                if (note.isEmpty()) {
                    notesMap.remove(adapterPosition);
                } else {
                    notesMap.put(adapterPosition, note);
                }
                notifyItemChanged(adapterPosition);
                activity.stringsEditor.hasUnsavedChanges = true;
            });

            String keyInput = Objects.requireNonNull(dialogBinding.stringKeyInput.getText()).toString();
            if (!keyInput.equals("app_name")) {
                dialog.configureDefaultButton(Helper.getResString(R.string.common_word_delete), v1 -> {
                    if (isXmlStringUsed(key)) {
                        SketchwareUtil.toastError(Helper.getResString(R.string.logic_editor_title_remove_xml_string_error));
                    } else {
                        filteredData.remove(adapterPosition);
                        notifyItemRemoved(adapterPosition);
                        activity.stringsEditor.updateNoContentLayout();
                        activity.stringsEditor.hasUnsavedChanges = true;
                    }
                });
            }
            dialog.a(Helper.getResString(R.string.cancel), v1 -> dialog.dismiss());
            dialog.a(dialogBinding.getRoot());
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return filteredData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        PalletCustomviewBinding binding;

        public ViewHolder(PalletCustomviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void filter(String query) {
        if (query == null || query.isEmpty()) {
            filteredData = new ArrayList<>(originalData);
        } else {
            filteredData = new ArrayList<>();
            for (HashMap<String, Object> item : originalData) {
                String key = (String) item.get("key");
                String text = (String) item.get("text");
                if ((key != null && key.toLowerCase().contains(query)) || (text != null && text.toLowerCase().contains(query))) {
                    filteredData.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public boolean isXmlStringUsed(String key) {
        if ("app_name".equals(key) || sc_id == null) {
            return false;
        }

        String projectScId = sc_id;
        eC projectDataManager = jC.a(projectScId);

        return isStringUsedInJavaFiles(projectScId, projectDataManager, key) || isStringUsedInXmlFiles(projectScId, projectDataManager, key);
    }

    private boolean isStringUsedInJavaFiles(String projectScId, eC projectDataManager, String key) {
        for (String javaFileName : getAllJavaFileNames(projectScId)) {
            for (Map.Entry<String, ArrayList<BlockBean>> entry : projectDataManager.b(javaFileName).entrySet()) {
                for (BlockBean block : entry.getValue()) {
                    if ((block.opCode.equals("getResStr") && block.spec.equals(key)) || (block.opCode.equals("getResString") && block.parameters.get(0).equals("R.string." + key))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isStringUsedInXmlFiles(String projectScId, eC projectDataManager, String key) {
        for (String xmlFileName : getAllXmlFileNames(projectScId)) {
            for (ViewBean view : projectDataManager.d(xmlFileName)) {
                if (view.text.text.equals("@string/" + key) || view.text.hint.equals("@string/" + key)) {
                    return true;
                }
            }
        }
        return false;
    }
}

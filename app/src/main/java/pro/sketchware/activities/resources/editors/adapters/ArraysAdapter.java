package pro.sketchware.activities.resources.editors.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import pro.sketchware.activities.resources.editors.fragments.ArraysEditor;
import pro.sketchware.databinding.PalletCustomviewBinding;
import pro.sketchware.activities.resources.editors.models.ArrayModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ArraysAdapter extends RecyclerView.Adapter<ArraysAdapter.ArrayViewHolder> {

    private final List<ArrayModel> arraysList;
    private final List<ArrayModel> originalList;
    private final HashMap<Integer, String> notesMap;
    private final Fragment fragment;

    public ArraysAdapter(ArrayList<ArrayModel> arraysList, Fragment fragment, HashMap<Integer, String> notesMap) {
        this.arraysList = arraysList;
        this.originalList = new ArrayList<>(arraysList);
        this.notesMap = notesMap;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public ArrayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PalletCustomviewBinding binding = PalletCustomviewBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ArrayViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArrayViewHolder holder, int position) {
        ArrayModel array = arraysList.get(position);
        holder.bind(array);
    }

    @Override
    public int getItemCount() {
        return arraysList.size();
    }

    public void filter(String newText) {
        arraysList.clear();

        for (ArrayModel array : originalList) {
            if (array.getArrayName().toLowerCase().contains(newText)) {
                arraysList.add(array);
            }
        }

        notifyDataSetChanged();
    }

    public class ArrayViewHolder extends RecyclerView.ViewHolder {

        private final PalletCustomviewBinding binding;

        public ArrayViewHolder(PalletCustomviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ArrayModel array) {
            binding.title.setHint(array.getArrayName());
            binding.sub.setText(array.getArrayType().name());
            if (notesMap.containsKey(getAbsoluteAdapterPosition())) {
                binding.tvTitle.setText(notesMap.get(getAbsoluteAdapterPosition()));
                binding.tvTitle.setVisibility(View.VISIBLE);
            } else {
                binding.tvTitle.setVisibility(View.GONE);
            }

            binding.backgroundCard.setOnClickListener(view -> {
                if (fragment instanceof ArraysEditor arraysEditor) {
                    arraysEditor.showArrayAttributesDialog(getAbsoluteAdapterPosition());
                }
            });

            binding.backgroundCard.setOnLongClickListener(view -> {
                if (fragment instanceof ArraysEditor arraysEditor) {
                    arraysEditor.showEditArrayDialog(getAbsoluteAdapterPosition());
                }
                return true;
            });
        }
    }
}
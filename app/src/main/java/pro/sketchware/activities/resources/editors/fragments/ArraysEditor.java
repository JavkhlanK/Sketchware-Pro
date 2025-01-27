package pro.sketchware.activities.resources.editors.fragments;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.besome.sketch.editor.property.PropertyInputItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import a.a.a.aB;
import mod.hey.studios.util.Helper;

import pro.sketchware.R;
import pro.sketchware.activities.resources.editors.ResourcesEditorActivity;
import pro.sketchware.databinding.ArraysEditorAddAttrBinding;
import pro.sketchware.databinding.ArraysEditorAddBinding;
import pro.sketchware.databinding.PropertyPopupParentAttrBinding;
import pro.sketchware.databinding.ResourcesEditorFragmentBinding;
import pro.sketchware.activities.resources.editors.models.ArrayModel;
import pro.sketchware.activities.resources.editors.adapters.ArraysAdapter;
import pro.sketchware.activities.resources.editors.utils.ArraysEditorManager;
import pro.sketchware.utility.FileUtil;
import pro.sketchware.utility.SketchwareUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class ArraysEditor extends Fragment {

    private ResourcesEditorFragmentBinding binding;

    public ArraysAdapter adapter;
    private PropertyInputItem.AttributesAdapter attributesAdapter;

    private final ArrayList<ArrayModel> arraysList = new ArrayList<>();
    private HashMap<Integer, String> notesMap = new HashMap<>();

    public final ArraysEditorManager arraysEditorManager;

    public boolean hasUnsavedChanges;
    private String filePath;
    private final ResourcesEditorActivity activity;

    public ArraysEditor(ResourcesEditorActivity activity) {
        this.activity = activity;
        arraysEditorManager = new ArraysEditorManager();
    }

    public enum ARRAYS_TYPES {
        STRING, INTEGER, OBJECT
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ResourcesEditorFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void updateArraysList(String filePath, int updateMode, boolean hasUnsavedChangesStatus) {
        hasUnsavedChanges = hasUnsavedChangesStatus;
        this.filePath = filePath;
        boolean isSkippingMode = updateMode == 1;
        boolean isMergeAndReplace = updateMode == 2;

        ArrayList<ArrayModel> defaultArrays = arraysEditorManager.parseArraysFile(FileUtil.readFileIfExist(filePath));

        if (isSkippingMode) {
            HashSet<String> existingArrayNames = new HashSet<>();
            for (ArrayModel existingArray : arraysList) {
                existingArrayNames.add(existingArray.getArrayName());
            }

            for (ArrayModel array : defaultArrays) {
                if (!existingArrayNames.contains(array.getArrayName())) {
                    arraysList.add(array);
                }
            }
        } else {
            if (isMergeAndReplace) {
                HashSet<String> newArrayNames = new HashSet<>();
                for (ArrayModel array : defaultArrays) {
                    newArrayNames.add(array.getArrayName());
                }

                arraysList.removeIf(existingArray -> newArrayNames.contains(existingArray.getArrayName()));
            } else {
                arraysList.clear();
            }
            arraysList.addAll(defaultArrays);
        }

        activity.runOnUiThread(() -> {
            notesMap = new HashMap<>(arraysEditorManager.notesMap);
            adapter = new ArraysAdapter(arraysList, this, notesMap);
            binding.recyclerView.setAdapter(adapter);
            activity.checkForInvalidResources();
            updateNoContentLayout();
        });
    }

    private void updateNoContentLayout() {
        if (arraysList.isEmpty()) {
            binding.noContentLayout.setVisibility(View.VISIBLE);
            binding.noContentTitle.setText(String.format(Helper.getResString(R.string.resource_manager_no_list_title), "Arrays"));
            binding.noContentBody.setText(String.format(Helper.getResString(R.string.resource_manager_no_list_body), "Arrays"));
        } else {
            binding.noContentLayout.setVisibility(View.GONE);
        }
    }

    public void showAddArrayDialog() {
        aB dialog = new aB(requireActivity());
        ArraysEditorAddBinding binding = ArraysEditorAddBinding.inflate(getLayoutInflater());
        dialog.b("Create new array");

        binding.arrayType.setOnClickListener(view -> {
            String[] arrayTypes = {"STRING", "INTEGER", "OBJECT"};
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Select Array Type")
                    .setSingleChoiceItems(arrayTypes, -1, (dialogInterface, which) -> {
                        binding.arrayType.setText(arrayTypes[which]);
                        dialogInterface.dismiss();
                    })
                    .show();
        });

        dialog.b("Create", v1 -> {
            String arrayName = Objects.requireNonNull(binding.arrayName.getText()).toString();
            String arrayTypeString = Objects.requireNonNull(binding.arrayType.getText()).toString();
            String header = Objects.requireNonNull(binding.arrayHeaderInput.getText()).toString();

            if (arrayName.isEmpty()) {
                SketchwareUtil.toastError("Array name Input is Empty");
                return;
            }

            if (arrayTypeString.isEmpty()) {
                SketchwareUtil.toastError("Array type not selected");
                return;
            }

            ARRAYS_TYPES arrayType = ARRAYS_TYPES.valueOf(arrayTypeString.toUpperCase());

            ArrayModel array = new ArrayModel(arrayName, arrayType);
            arraysList.add(array);
            int notifyPosition = arraysList.size() - 1;
            if (!header.isEmpty()) {
                notesMap.put(notifyPosition, header);
            }
            adapter.notifyItemInserted(notifyPosition);
            updateNoContentLayout();
            hasUnsavedChanges = true;
        });

        dialog.a(getString(R.string.cancel), Helper.getDialogDismissListener(dialog));
        dialog.a(binding.getRoot());
        dialog.show();
    }
    public void showEditArrayDialog(int position) {
        ArrayModel array = arraysList.get(position);
        aB dialog = new aB(requireActivity());
        ArraysEditorAddBinding binding = ArraysEditorAddBinding.inflate(getLayoutInflater());

        binding.arrayName.setText(array.getArrayName());
        binding.arrayType.setText(array.getArrayType().name());
        if (notesMap.containsKey(position)) {
            binding.arrayHeaderInput.setText(notesMap.get(position));
        }
        binding.arrayType.setOnClickListener(view -> {
            String[] arrayTypes = {"STRING", "INTEGER", "OBJECT"};
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Select Array Type")
                    .setSingleChoiceItems(arrayTypes, -1, (dialogInterface, which) -> {
                        binding.arrayType.setText(arrayTypes[which]);
                        dialogInterface.dismiss();
                    })
                    .show();
        });

        dialog.b("Edit array");
        dialog.b("Edit", v1 -> {
            String arrayName = Objects.requireNonNull(binding.arrayName.getText()).toString();
            String arrayType = Objects.requireNonNull(binding.arrayType.getText()).toString();
            String header = Objects.requireNonNull(binding.arrayHeaderInput.getText()).toString();

            if (arrayName.isEmpty()) {
                SketchwareUtil.toastError("Array name Input is Empty");
                return;
            }

            array.setArrayName(arrayName);
            array.setArrayType(ARRAYS_TYPES.valueOf(arrayType.toUpperCase()));
            if (header.isEmpty()) {
                notesMap.remove(position);
            } else {
                notesMap.put(position, header);
            }
            adapter.notifyItemChanged(position);
            hasUnsavedChanges = true;
        });
        dialog.setDismissOnDefaultButtonClick(false);
        dialog.configureDefaultButton(Helper.getResString(R.string.common_word_delete), view -> new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Warning")
                .setMessage("Are you sure you want to delete " + array.getArrayName() + "?")
                .setPositiveButton(R.string.common_word_yes, (d, w) -> {
                    arraysList.remove(position);
                    notesMap.remove(position);
                    adapter.notifyItemRemoved(position);
                    dialog.dismiss();
                    updateNoContentLayout();
                    hasUnsavedChanges = true;
                })
                .setNegativeButton("Cancel", null)
                .create()
                .show());
        dialog.a(getString(R.string.cancel), Helper.getDialogDismissListener(dialog));
        dialog.a(binding.getRoot());
        dialog.show();
    }

    public void showArrayAttributesDialog(int position) {
        ArrayModel array = arraysList.get(position);
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        var binding = PropertyPopupParentAttrBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());
        dialog.show();

        binding.title.setText(array.getArrayName() + " attributes");

        attributesAdapter = new PropertyInputItem.AttributesAdapter();
        attributesAdapter.setOnItemClickListener(
                new PropertyInputItem.AttributesAdapter.ItemClickListener() {
                    @Override
                    public void onItemClick(LinkedHashMap<String, String> attributes, String attr) {
                        showAttributeDialog(array, attr);
                    }

                    @Override
                    public void onItemLongClick(LinkedHashMap<String, String> attributes, String attr) {
                        new MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Warning")
                                .setMessage("Are you sure you want to delete " + attr + "?")
                                .setPositiveButton(R.string.common_word_yes, (d, w) -> {
                                    attributes.remove(attr);
                                    array.setAttributes(attributes);
                                    attributesAdapter.submitList(new ArrayList<>(attributes.keySet()));
                                    hasUnsavedChanges = true;
                                })
                                .setNegativeButton("Cancel", null)
                                .create()
                                .show();
                    }
                });
        binding.recyclerView.setAdapter(attributesAdapter);
        var dividerItemDecoration =
                new DividerItemDecoration(
                        binding.recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        binding.recyclerView.addItemDecoration(dividerItemDecoration);
        var attributes = array.getAttributes();
        attributesAdapter.setAttributes(attributes);
        List<String> keys = new ArrayList<>(attributes.keySet());
        attributesAdapter.submitList(keys);

        binding.add.setOnClickListener(
                v -> showAttributeDialog(array, ""));
    }

    private void showAttributeDialog(ArrayModel array, String attr) {
        boolean isEditing = !attr.isEmpty();

        aB dialog = new aB(requireActivity());
        ArraysEditorAddAttrBinding binding = ArraysEditorAddAttrBinding.inflate(getLayoutInflater());

        if (array.getArrayType() == ARRAYS_TYPES.INTEGER) {
            binding.itemValue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED);
        } else {
            binding.itemValue.setInputType(InputType.TYPE_CLASS_TEXT);
        }

        if (isEditing) {
            binding.itemName.setText(attr);
            binding.itemValue.setText(array.getAttribute(attr));
        } else {
            binding.itemName.setText("item" + (array.getAttributes().keySet().size() + 1));
            binding.itemValue.requestFocus();
        }

        dialog.b(isEditing ? "Edit item" : "Create new item");

        dialog.b(Helper.getResString(R.string.common_word_save), v1 -> {
            String attribute = Objects.requireNonNull(binding.itemName.getText()).toString();
            String value = Objects.requireNonNull(binding.itemValue.getText()).toString();

            if (attribute.isEmpty() || value.isEmpty()) {
                SketchwareUtil.toastError("Please fill in all fields");
                return;
            }

            if (!attribute.equals(attr)) array.getAttributes().remove(attr);

            array.addAttribute(attribute, value);
            attributesAdapter.submitList(new ArrayList<>(array.getAttributes().keySet()));
            attributesAdapter.notifyDataSetChanged();
            hasUnsavedChanges = true;
        });

        dialog.a(getString(R.string.cancel), Helper.getDialogDismissListener(dialog));
        dialog.a(binding.getRoot());
        dialog.show();
    }
    public void saveArraysFile() {
        if (hasUnsavedChanges && FileUtil.isExistFile(filePath) || !arraysList.isEmpty()) {
            FileUtil.writeFile(filePath, arraysEditorManager.convertArraysToXML(arraysList, notesMap));
            hasUnsavedChanges = false;
        }
    }

}
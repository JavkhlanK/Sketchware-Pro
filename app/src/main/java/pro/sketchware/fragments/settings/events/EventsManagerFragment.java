package pro.sketchware.fragments.settings.events;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import a.a.a.qA;
import mod.hey.studios.util.Helper;
import pro.sketchware.R;
import pro.sketchware.databinding.DialogAddNewListenerBinding;
import pro.sketchware.databinding.FragmentEventsManagerBinding;
import pro.sketchware.databinding.LayoutEventItemBinding;
import pro.sketchware.fragments.settings.events.details.EventsManagerDetailsFragment;
import pro.sketchware.utility.FileUtil;
import pro.sketchware.utility.SketchwareUtil;
import pro.sketchware.utility.UI;

public class EventsManagerFragment extends qA {

    private FragmentEventsManagerBinding binding;
    private ArrayList<HashMap<String, Object>> listMap = new ArrayList<>();

    public static String getNumOfEvents(String name) {
        int eventAmount = 0;
        if (FileUtil.isExistFile(EventsManagerConstants.EVENTS_FILE.getAbsolutePath())) {
            ArrayList<HashMap<String, Object>> events = new Gson()
                    .fromJson(FileUtil.readFile(EventsManagerConstants.EVENTS_FILE.getAbsolutePath()), Helper.TYPE_MAP_LIST);
            for (HashMap<String, Object> event : events) {
                if (event.get("listener").toString().equals(name)) {
                    eventAmount++;
                }
            }
        }
        return "Events: " + eventAmount;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEventsManagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        configureToolbar(binding.toolbar);
        binding.toolbar.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_import_events) {
                showImportEventsDialog();
                return true;
            } else if (itemId == R.id.action_export_events) {
                exportAllEvents();
                return true;
            }
            return false;
        });
        binding.activityEvents.setOnClickListener(v -> openFragment(new EventsManagerDetailsFragment()));
        binding.activityEventsDescription.setText(getNumOfEvents(""));
        binding.fabNewListener.setOnClickListener(v -> showAddNewListenerDialog());
        refreshList();
    }

    private void showAddNewListenerDialog() {
        showListenerDialog(null, -1);
    }

    private void showEditListenerDialog(int position) {
        showListenerDialog(listMap.get(position), position);
    }

    private void showListenerDialog(@Nullable HashMap<String, Object> existingListener, int position) {
        var listenerBinding = DialogAddNewListenerBinding.inflate(LayoutInflater.from(requireContext()));
        String commandBlock_2 = "\nBpWI8U4flOpx8Ke66QTlZYBA_NEusQ7BN-D0wvZs7ArsRfi0.EP3Php97kjdMCs*";
        String commandBlock_1 = "*-JX4UA2y_f1OckjjvxWI.bQwRei-sLEsBmds7ArsRfi0xSFEP3Php97kjdMCs5ed"
                              + "\n>[\"public class\"]"
                              + "\n>1"
                              + "\n>0"
                              + "\n>0"
                              + "\n>add\n";
        
        if (existingListener != null) {
            
            String existingName = existingListener.get("name").toString();
            listenerBinding.listenerName.setText(existingName);
            
            listenerBinding.listenerCustomImport.setText(existingListener.get("imports").toString());
            
            String existingCode = existingListener.get("code").toString();
            int startOfVariables_i = commandBlock_1.length() + existingCode.indexOf(commandBlock_1);
            int endOfVariables_i = existingCode.indexOf(commandBlock_2);
            int startOfCode_i = 2 + commandBlock_2.length() + endOfVariables_i;
            
            String finalVariables = existingCode.contains(commandBlock_1) && existingCode.contains(commandBlock_2)
            ? existingCode.substring(startOfVariables_i, endOfVariables_i)
            : "";
            
            String finalCode = existingCode.contains(commandBlock_2) 
            ? existingCode.substring(startOfCode_i)
            : existingCode;
            
            listenerBinding.listenerDeclareVariables.setText(finalVariables);
            listenerBinding.listenerCode.setText(finalCode);
            
            
            if ("true".equals(existingListener.get("s"))) {
                listenerBinding.listenerIsIndependentClassOrMethod.setChecked(true);
                listenerBinding.listenerCode.setText(finalCode.replaceFirst("//" + existingName + "\n", ""));
            }
        }

        var dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(existingListener == null ? "New Listener" : "Edit Listener")
                .setMessage("Type info of the listener")
                .setView(listenerBinding.getRoot())
                .setPositiveButton("Save", (di, i) -> {
                    
                    String listenerName = listenerBinding.listenerName.getText().toString();
                    String listenerVariables = listenerBinding.listenerDeclareVariables.getText().toString();
                    
                    String listenerCode = listenerBinding.listenerCode.getText().toString();
                    String listenerVarAndCode = "".equals(listenerVariables.replace("\\s", ""))
                                              ? listenerCode
                                              : "xaxaxa/xaxaxa" + commandBlock_1 + listenerVariables + commandBlock_2 + "xaxaxa/xaxaxa\n" + listenerCode;
                    
                
                    if (!listenerName.isEmpty()) {
                        HashMap<String, Object> hashMap = existingListener != null ? existingListener : new HashMap<>();
                        hashMap.put("name", listenerName);
                        hashMap.put("code", listenerBinding.listenerIsIndependentClassOrMethod.isChecked()
                                ? "//" + listenerName + "\n" + listenerVarAndCode.replace("xaxaxa", "")
                                : listenerVarAndCode.replace("xaxaxa", ""));
                        hashMap.put("s", listenerBinding.listenerIsIndependentClassOrMethod.isChecked() ? "true" : "false");
                        hashMap.put("imports", listenerBinding.listenerCustomImport.getText().toString());
                        if (position >= 0) {
                            listMap.set(position, hashMap);
                        } else {
                            listMap.add(hashMap);
                        }
                        addListenerItem();
                        di.dismiss();
                    } else {
                        SketchwareUtil.toastError("Invalid name!");
                    }
                })
                .setNegativeButton("Cancel", (di, i) -> di.dismiss()).create();
        dialog.show();
    }
    public void refreshList() {
        listMap.clear();
        if (FileUtil.isExistFile(EventsManagerConstants.LISTENERS_FILE.getAbsolutePath())) {
            listMap = new Gson().fromJson(FileUtil.readFile(EventsManagerConstants.LISTENERS_FILE.getAbsolutePath()), Helper.TYPE_MAP_LIST);
            binding.listenersRecyclerView.setAdapter(new ListenersAdapter(listMap, requireContext()));
            binding.listenersRecyclerView.getAdapter().notifyDataSetChanged();
        }
        Collections.reverse(listMap);
    }

    private void showImportEventsDialog() {
        DialogProperties dialogProperties = new DialogProperties();
        dialogProperties.selection_mode = 0;
        dialogProperties.selection_type = 0;
        File file = new File(FileUtil.getExternalStorageDir());
        dialogProperties.root = file;
        dialogProperties.error_dir = file;
        dialogProperties.offset = file;
        dialogProperties.extensions = null;
        FilePickerDialog filePickerDialog = new FilePickerDialog(requireContext(), dialogProperties, R.style.RoundedCornersDialog);
        filePickerDialog.setTitle("Select a .txt file");
        filePickerDialog.setDialogSelectionListener(selections -> {
            if (FileUtil.readFile(selections[0]).isEmpty()) {
                SketchwareUtil.toastError("The selected file is empty!");
            } else if (FileUtil.readFile(selections[0]).equals("[]")) {
                SketchwareUtil.toastError("The selected file is empty!");
            } else {
                try {
                    String[] split = FileUtil.readFile(selections[0]).split("\n");
                    importEvents(new Gson().fromJson(split[0], Helper.TYPE_MAP_LIST),
                            new Gson().fromJson(split[1], Helper.TYPE_MAP_LIST));
                } catch (Exception e) {
                    SketchwareUtil.toastError("Invalid file");
                }
            }
        });
        filePickerDialog.show();
    }

    private void importEvents(ArrayList<HashMap<String, Object>> data, ArrayList<HashMap<String, Object>> data2) {
        ArrayList<HashMap<String, Object>> events = new ArrayList<>();
        if (FileUtil.isExistFile(EventsManagerConstants.EVENTS_FILE.getAbsolutePath())) {
            events = new Gson().fromJson(FileUtil.readFile(EventsManagerConstants.EVENTS_FILE.getAbsolutePath()), Helper.TYPE_MAP_LIST);
        }
        events.addAll(data2);
        FileUtil.writeFile(EventsManagerConstants.EVENTS_FILE.getAbsolutePath(), new Gson().toJson(events));
        listMap.addAll(data);
        FileUtil.writeFile(EventsManagerConstants.LISTENERS_FILE.getAbsolutePath(), new Gson().toJson(listMap));
        refreshList();
        SketchwareUtil.toast("Successfully imported events");
    }

    private void exportListener(int p) {
        String concat = FileUtil.getExternalStorageDir().concat("/.sketchware/data/system/export/events/");
        ArrayList<HashMap<String, Object>> ex = new ArrayList<>();
        ex.add(listMap.get(p));
        ArrayList<HashMap<String, Object>> ex2 = new ArrayList<>();
        if (FileUtil.isExistFile(EventsManagerConstants.EVENTS_FILE.getAbsolutePath())) {
            ArrayList<HashMap<String, Object>> events = new Gson()
                    .fromJson(FileUtil.readFile(EventsManagerConstants.EVENTS_FILE.getAbsolutePath()), Helper.TYPE_MAP_LIST);
            for (int i = 0; i < events.size(); i++) {
                if (events.get(i).get("listener").toString().equals(listMap.get(p).get("name"))) {
                    ex2.add(events.get(i));
                }
            }
        }
        FileUtil.writeFile(concat + ex.get(0).get("name").toString() + ".txt", new Gson().toJson(ex) + "\n" + new Gson().toJson(ex2));
        SketchwareUtil.toast("Successfully exported event to:\n" +
                "/Internal storage/.sketchware/data/system/export/events", Toast.LENGTH_LONG);
    }

    private void exportAllEvents() {
        ArrayList<HashMap<String, Object>> events = new ArrayList<>();
        if (FileUtil.isExistFile(EventsManagerConstants.EVENTS_FILE.getAbsolutePath())) {
            events = new Gson().fromJson(FileUtil.readFile(EventsManagerConstants.EVENTS_FILE.getAbsolutePath()), Helper.TYPE_MAP_LIST);
        }
        FileUtil.writeFile(new File(EventsManagerConstants.EVENT_EXPORT_LOCATION, "All_Events.txt").getAbsolutePath(),
                new Gson().toJson(listMap) + "\n" + new Gson().toJson(events));
        SketchwareUtil.toast("Successfully exported events to:\n" +
                "/Internal storage/.sketchware/data/system/export/events", Toast.LENGTH_LONG);
    }

    private void addListenerItem() {
        FileUtil.writeFile(EventsManagerConstants.LISTENERS_FILE.getAbsolutePath(), new Gson().toJson(listMap));
        refreshList();
    }

    private void deleteItem(int position) {
        listMap.remove(position);
        FileUtil.writeFile(EventsManagerConstants.LISTENERS_FILE.getAbsolutePath(), new Gson().toJson(listMap));
        refreshList();
    }

    private void deleteRelatedEvents(String name) {
        ArrayList<HashMap<String, Object>> events = new ArrayList<>();
        if (FileUtil.isExistFile(EventsManagerConstants.EVENTS_FILE.getAbsolutePath())) {
            events = new Gson()
                    .fromJson(FileUtil.readFile(EventsManagerConstants.EVENTS_FILE.getAbsolutePath()), Helper.TYPE_MAP_LIST);
            for (int i = events.size() - 1; i > -1; i--) {
                if (events.get(i).get("listener").toString().equals(name)) {
                    events.remove(i);
                }
            }
        }
        FileUtil.writeFile(EventsManagerConstants.EVENTS_FILE.getAbsolutePath(), new Gson().toJson(events));
    }

    public class ListenersAdapter extends RecyclerView.Adapter<ListenersAdapter.ViewHolder> {

        private final ArrayList<HashMap<String, Object>> dataArray;
        private final Context context;

        public ListenersAdapter(ArrayList<HashMap<String, Object>> arrayList, Context context) {
            dataArray = arrayList;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutEventItemBinding binding = LayoutEventItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            HashMap<String, Object> item = dataArray.get(position);
            String name = (String) item.get("name");
            holder.itemView.setBackgroundResource(UI.getShapedBackgroundForList(dataArray, position));

            holder.binding.eventIcon.setImageResource(R.drawable.event_on_response_48dp);
            ((LinearLayout) holder.binding.eventIcon.getParent()).setGravity(Gravity.CENTER);

            holder.binding.eventTitle.setText(name);
            holder.binding.eventSubtitle.setText(getNumOfEvents(name));

            holder.itemView.setOnClickListener(v -> openFragment(new EventsManagerDetailsFragment(name)));

            holder.itemView.setOnLongClickListener(v -> {
                new MaterialAlertDialogBuilder(context)
                        .setTitle(name)
                        .setItems(new String[]{"Edit", "Export", "Delete"}, (dialog, which) -> {
                            switch (which) {
                                case 0:
                                    showEditListenerDialog(position);
                                    break;
                                case 1:
                                    exportListener(position);
                                    break;
                                case 2:
                                    new MaterialAlertDialogBuilder(context)
                                            .setTitle("Delete listener")
                                            .setMessage("Are you sure you want to delete this item?")
                                            .setPositiveButton("Yes", (di, i) -> {
                                                deleteRelatedEvents(name);
                                                deleteItem(position);
                                                di.dismiss();
                                            })
                                            .setNegativeButton("No", (di, i) -> di.dismiss())
                                            .show();
                                    break;
                            }
                        }).show();
                return true;
            });
        }

        @Override
        public int getItemCount() {
            return dataArray.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private final LayoutEventItemBinding binding;

            public ViewHolder(@NonNull LayoutEventItemBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}

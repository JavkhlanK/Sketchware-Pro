package com.besome.sketch.design.hierarchy;

import static pro.sketchware.SketchApplication.getContext;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import com.besome.sketch.beans.ViewBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import pro.sketchware.databinding.LayoutHierarchyItemBinding;

public class LayoutHierarchyAdapter extends ListAdapter<ViewBean, LayoutHierarchyAdapter.LayoutHierarchyAdapterViewHolder> {

    private LayoutHierarchyItemListener layoutHierarchyItemListener;
    private int selectedItemPosition;
    private final ArrayList<ViewBean> viewBeansList;
    private final Map<Integer, Boolean> expandedStateMap;

    public LayoutHierarchyAdapter(ArrayList<ViewBean> viewBeansList) {
        super(new LayoutHierarchyAdapterDiffCallback());
        this.viewBeansList = viewBeansList;
        this.expandedStateMap = new HashMap<>();
    }

    public void setLayoutHierarchyItemListener(LayoutHierarchyItemListener layoutHierarchyItemListener) {
        this.layoutHierarchyItemListener = layoutHierarchyItemListener;
    }

    public void setSelectedItemPosition(int selectedItemPosition) {
        this.selectedItemPosition = selectedItemPosition;
    }

    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    @NonNull
    @Override
    public LayoutHierarchyAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var binding = LayoutHierarchyItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new LayoutHierarchyAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LayoutHierarchyAdapterViewHolder holder, int position) {
        var bean = getItem(position);
        var imgRes = ViewBean.getViewTypeResId(bean.type);
        boolean isViewGroup = bean.getClassInfo().a("ViewGroup");

        boolean isExpanded = Boolean.TRUE.equals(expandedStateMap.getOrDefault(position, false));
        holder.binding.recyclerView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.binding.expandToggle.setRotation(isExpanded ? 180f : 0f);

        holder.binding.getRoot().setOnClickListener(v -> handleItemClick(holder, bean, position, isViewGroup));

        holder.binding.expandToggle.setVisibility(isViewGroup ? View.VISIBLE : View.GONE);
        holder.binding.viewIcon.setImageResource(imgRes);
        holder.binding.viewName.setText(bean.id);
    }

    private void handleItemClick(LayoutHierarchyAdapterViewHolder holder, ViewBean bean, int position, boolean isViewGroup) {
        if (isViewGroup) {
            toggleExpandCollapse(holder, bean, position);
        }
        setSelectedItemPosition(getViewBeanPosition(bean));
        if (layoutHierarchyItemListener != null) {
            layoutHierarchyItemListener.onClick(bean);
        }
    }

    private void toggleExpandCollapse(LayoutHierarchyAdapterViewHolder holder, ViewBean bean, int position) {
        ArrayList<ViewBean> childViews = getChildViews(bean.id);

        LayoutHierarchyAdapter adapter = new LayoutHierarchyAdapter(viewBeansList);
        boolean currentlyExpanded = Boolean.TRUE.equals(expandedStateMap.getOrDefault(position, false));
        if (currentlyExpanded) {
            holder.binding.recyclerView.setVisibility(View.GONE);
            ObjectAnimator.ofFloat(holder.binding.expandToggle, "rotation", 180f, 0f).start();
        } else {
            adapter.submitList(childViews);
            adapter.setLayoutHierarchyItemListener(layoutHierarchyItemListener);
            holder.binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            holder.binding.recyclerView.setAdapter(adapter);
            holder.binding.recyclerView.setVisibility(View.VISIBLE);
            ObjectAnimator.ofFloat(holder.binding.expandToggle, "rotation", 0f, 180f).start();
        }
        expandedStateMap.put(position, !currentlyExpanded);
    }

    private ArrayList<ViewBean> getChildViews(String parentId) {
        ArrayList<ViewBean> childViews = new ArrayList<>();
        for (ViewBean viewBean : viewBeansList) {
            if (viewBean.parent != null && viewBean.parent.equals(parentId)) {
                childViews.add(viewBean);
            }
        }
        return childViews;
    }

    private int getViewBeanPosition(ViewBean viewBean) {
        for (int i = 0, viewBeansListSize = viewBeansList.size(); i < viewBeansListSize; i++) {
            ViewBean bean = viewBeansList.get(i);
            if (bean.isEqual(viewBean)) return i;
        }
        return -1;
    }

    public static class LayoutHierarchyAdapterViewHolder extends RecyclerView.ViewHolder {
        public LayoutHierarchyItemBinding binding;

        public LayoutHierarchyAdapterViewHolder(LayoutHierarchyItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public static class LayoutHierarchyAdapterDiffCallback extends DiffUtil.ItemCallback<ViewBean> {
        @Override
        public boolean areItemsTheSame(@NonNull ViewBean oldItem, @NonNull ViewBean newItem) {
            return oldItem.isEqual(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull ViewBean oldItem, @NonNull ViewBean newItem) {
            return oldItem.isEqual(newItem);
        }
    }
}
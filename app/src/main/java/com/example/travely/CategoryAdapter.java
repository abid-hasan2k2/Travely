package com.example.travely;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<CategoryItem> categories;
    private List<CategoryItem> filteredCategories; // Store filtered list separately
    private OnCategoryClickListener listener;
    private Context context;

    public CategoryAdapter(Context context, List<CategoryItem> categories, OnCategoryClickListener listener) {
        this.context = context;
        this.categories = categories;
        this.filteredCategories = new ArrayList<>(categories); // Initialize filtered list with all categories
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryItem item = filteredCategories.get(position); // Use filtered list
        holder.categoryName.setText(item.getName());
        holder.categoryImage.setImageResource(item.getImageResource());
        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(item.getName()));
    }

    @Override
    public int getItemCount() {
        return filteredCategories.size(); // Return size of filtered list
    }

    public void filterList(List<CategoryItem> filteredList) {
        filteredCategories = filteredList;
        notifyDataSetChanged();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView categoryImage;
        TextView categoryName;

        CategoryViewHolder(View itemView) {
            super(itemView);
            categoryImage = itemView.findViewById(R.id.categoryImage);
            categoryName = itemView.findViewById(R.id.categoryName);
        }
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }
}

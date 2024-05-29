package com.sydney.recipemanagaer.ui.view.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

public class IngredientAdapter extends ArrayAdapter<String> {
    private List<String> items;
    private List<String> suggestions;
    private List<String> allItems;

    public IngredientAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
        this.items = items;
        this.allItems = new ArrayList<>(items); // full list of items
        this.suggestions = new ArrayList<>();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    suggestions.clear();
                    for (String item : allItems) {
                        if (item.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            suggestions.add(item);
                        }
                    }
                    if (suggestions.isEmpty()) {
                        suggestions.add("Add new: " + constraint.toString());
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                    return filterResults;
                }
                return new FilterResults();
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values instanceof List<?>) { // Check if results.values is indeed a List
                    List<?> rawList = (List<?>) results.values;
                    List<String> filterList = new ArrayList<>();
                    for (Object object : rawList) {
                        if (object instanceof String) { // Further checks to ensure it's List<String>
                            filterList.add((String) object);
                        }
                    }

                    if (!filterList.isEmpty()) {
                        clear();
                        for (String item : filterList) {
                            add(item);
                        }
                        notifyDataSetChanged();
                    }
                }
            }
        };
    }
}



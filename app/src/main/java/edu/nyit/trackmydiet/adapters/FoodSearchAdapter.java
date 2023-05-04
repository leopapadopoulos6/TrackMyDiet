package edu.nyit.trackmydiet.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.nyit.trackmydiet.interfaces.SelectFoodListener;
import edu.nyit.trackmydiet.models.FoodItem;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import edu.nyit.trackmydiet.R;

public class FoodSearchAdapter extends RecyclerView.Adapter<FoodSearchAdapter.FoodSearchViewHolder> {

    private ArrayList<FoodItem> FoodItems;
    SelectFoodListener selectFoodListener;


    public FoodSearchAdapter(ArrayList<FoodItem> FoodItems, SelectFoodListener selectFoodListener) {
        this.FoodItems = FoodItems;
        this.selectFoodListener = selectFoodListener;
    }


    @NonNull
    @Override
    public FoodSearchViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_row, viewGroup, false);
        return new FoodSearchAdapter.FoodSearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FoodSearchViewHolder holder, int position) {
        holder.foodNameTextView.setText(FoodItems.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFoodListener.onFoodSelected(FoodItems.get(position));
                //Intent intent = new Intent(getContext(), SearchResultsActivity.class);
                //startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return FoodItems.size();
    }

    public ArrayList<FoodItem> getFoodItems() {
        return FoodItems;
    }

    public class FoodSearchViewHolder extends RecyclerView.ViewHolder {

        private TextView foodNameTextView;

        public FoodSearchViewHolder(View view) {
            super(view);
            foodNameTextView = view.findViewById(R.id.food_name);
        }
    }
}
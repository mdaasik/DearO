package com.carworkz.dearo.addjobcard.createjobcard.inventory;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.carworkz.dearo.R;
import com.carworkz.dearo.domain.entities.Inventory;
import com.carworkz.dearo.helpers.DoubleClickListener;

import java.util.List;

/**
 * Created by Farhan on 16/8/17.
 */
class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private static final String CONDITION_NA = "NA";
    private static final String CONDITION_YES = "YES";
    private static final String CONDITION_NO = "NO";

    private List<Inventory> inventoryList;
    private Context context;
    private Boolean isViewOnly;

    InventoryAdapter(List<Inventory> inventoryList, Context context, Boolean isViewOnly) {
        this.inventoryList = inventoryList;
        this.context = context;
        this.isViewOnly = isViewOnly;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_inventory_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Inventory inventory = inventoryList.get(position);
        holder.itemsParent.setTag(inventory);
        holder.itemNameView.setText(inventory.getText());
        if (inventory.getStatus().equals(CONDITION_YES)) {
            holder.setupYes(position);
        } else {
            holder.setupNoOrNa(position, inventory.getStatus());
        }
    }

    List<Inventory> getSelectedInventoryList() {
        return inventoryList;
    }

    @Override
    public int getItemCount() {
        return inventoryList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout itemsParent;
        TextView itemNameView;
        ImageView itemSelectionImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemsParent = itemView.findViewById(R.id.ll_inventory_item_parent);
            itemNameView = itemView.findViewById(R.id.tv_inventory_item);
            itemSelectionImageView = itemView.findViewById(R.id.iv_inventory_item);
            if (!isViewOnly) {
                itemsParent.setOnClickListener(new DoubleClickListener() {
                    @Override
                    public void onSingleClick(View v) {
                        int pos = getAdapterPosition();
                        if (!inventoryList.get(pos).getStatus().equals(CONDITION_NA)) {
                            setupNoOrNa(pos, CONDITION_NA);
                        } else {
                            setupNoOrNa(pos, CONDITION_NO);
                        }

                    }

                    @Override
                    public void onDoubleClick(View v) {
                        int pos = getAdapterPosition();
                        Inventory inventory = inventoryList.get(pos);
                        if (!inventory.getStatus().equals(CONDITION_YES)) {
                            setupYes(pos);
                        }
                    }
                });
            }
        }

        private void setupNoOrNa(int pos, String status) {
            Inventory inventory = inventoryList.get(pos);
            if (status.equals(CONDITION_NA)) {
                inventory.setStatus(CONDITION_NA);
                itemSelectionImageView.setVisibility(View.INVISIBLE);
                itemNameView.setTextColor(Color.BLACK);
                itemsParent.setBackground(ContextCompat.getDrawable(context, R.drawable.inventory_item_border));
            } else {
                itemSelectionImageView.setVisibility(View.VISIBLE);
                itemSelectionImageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_clear_black_24dp));
                inventory.setStatus(CONDITION_NO);
                itemNameView.setTextColor(Color.WHITE);
                itemsParent.setBackground(null);
                itemsParent.setBackgroundColor(ContextCompat.getColor(context, R.color.persion_red));
            }
            inventoryList.set(pos, inventory);
        }

        private void setupYes(int pos) {
            Inventory inventory = inventoryList.get(pos);
            inventory.setStatus(CONDITION_YES);
            inventoryList.set(pos, inventory);
            itemSelectionImageView.setVisibility(View.VISIBLE);
            itemSelectionImageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_check_circle_black_24dp));
            itemNameView.setTextColor(Color.WHITE);
            itemsParent.setBackground(null);
            itemsParent.setBackgroundColor(ContextCompat.getColor(context, R.color.forest_green));
        }

    }
}

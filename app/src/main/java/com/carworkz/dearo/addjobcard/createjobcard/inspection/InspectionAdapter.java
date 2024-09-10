package com.carworkz.dearo.addjobcard.createjobcard.inspection;

import static com.carworkz.dearo.R.*;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.carworkz.dearo.R;
import com.carworkz.dearo.addjobcard.createjobcard.inspection.pojo.InspectionSubItemPOJO;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

/**
 * Created by Farhan on 23/8/17.
 */

public class InspectionAdapter extends ExpandableRecyclerViewAdapter<InspectionAdapter.InspectionItemViewHolder, InspectionAdapter.InspectionSubItemViewHolder> {


    public static final String POOR = "poor";
    public static final String AVERAGE = "average";
    public static final String GOOD = "good";


    private static final String TAG = "Inspection adapter";

    private Map<String, String> selectedInspection = new HashMap<>();
    private Context context;

    public InspectionAdapter(Context context, List<? extends ExpandableGroup> groups) {
        super(groups);
        this.context = context;
    }

    @Override
    public InspectionItemViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_inspection_item_title, parent, false);
        return new InspectionItemViewHolder(view);
    }

    @Override
    public InspectionSubItemViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_inspection_subitem, parent, false);
        return new InspectionSubItemViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(InspectionSubItemViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        Timber.d(TAG, "onBindChildViewHolder: sub item init " + group.getTitle());
        final InspectionSubItemPOJO inspectionSubItemPOJO = (InspectionSubItemPOJO) group.getItems().get(childIndex);
        holder.subItem.setText(inspectionSubItemPOJO.getText());
        holder.subItem.setTag(inspectionSubItemPOJO);

        String id = inspectionSubItemPOJO.getId();
        if (selectedInspection.containsKey(id)) {
            for (Map.Entry<String, String> entry : selectedInspection.entrySet()
                    ) {
                Timber.d(TAG, "onBindChildViewHolder: key" + entry.getKey() + " value " + entry.getValue());

                if (Objects.equals(entry.getKey(), id) && Objects.equals(entry.getValue(), POOR)) {
                    Timber.d(TAG, "key : " + entry.getKey() + "inside poor");
                    holder.poorBox.setChecked(true);
                    holder.avgBox.setChecked(false);
                    holder.goodBox.setChecked(false);
                    break;
                }

                if (Objects.equals(entry.getKey(), id) && Objects.equals(entry.getValue(), AVERAGE)) {
                    Timber.d(TAG, "key : " + entry.getKey() + "inside Average");
                    holder.poorBox.setChecked(false);
                    holder.avgBox.setChecked(true);
                    holder.goodBox.setChecked(false);
                    break;
                }
                if (Objects.equals(entry.getKey(), id) && Objects.equals(entry.getValue(), GOOD)) {
                    Timber.d(TAG, "key : " + entry.getKey() + "inside good");
                    holder.poorBox.setChecked(false);
                    holder.avgBox.setChecked(false);
                    holder.goodBox.setChecked(true);
                    break;
                }
            }

        } else {
            holder.poorBox.setChecked(false);
            holder.avgBox.setChecked(false);
            holder.goodBox.setChecked(false);
        }
    }

    @Override
    public void onBindGroupViewHolder(InspectionItemViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setTitle(group.getTitle());
    }

    @Override
    public boolean toggleGroup(int flatPos) {
        notifyItemChanged(flatPos);
        return super.toggleGroup(flatPos);
    }

    public Map<String, String> getSelectedInspection() {
        return selectedInspection;
    }

    public static class InspectionItemViewHolder extends GroupViewHolder {
        TextView titleView;
        ImageView imageView;
        Context context;

        InspectionItemViewHolder(View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.tv_title_inspection_item);
            imageView = itemView.findViewById(R.id.iv_inspection_item);
            context = itemView.getContext();
        }

        public void setTitle(String title) {
            this.titleView.setText(title);
        }

        @Override
        public void expand() {
            imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_up_black_24dp));
            super.expand();
        }

        @Override
        public void collapse() {
            imageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_keyboard_arrow_down_black_24dp));
            super.collapse();
        }
    }



    public class InspectionSubItemViewHolder extends ChildViewHolder implements View.OnClickListener {
        TextView subItem;
        private CheckBox poorBox, avgBox, goodBox;

        InspectionSubItemViewHolder(View itemView) {
            super(itemView);
            subItem = itemView.findViewById(R.id.tv_title_inspection_subitem);
            poorBox = itemView.findViewById(R.id.cb_poor);
            avgBox = itemView.findViewById(R.id.cb_average);
            goodBox = itemView.findViewById(R.id.cb_good);

            poorBox.setOnClickListener(this);
            avgBox.setOnClickListener(this);
            goodBox.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            InspectionSubItemPOJO subItemPOJO = (InspectionSubItemPOJO) subItem.getTag();
            switch (view.getId()) {
                case R.id.cb_poor:
                    if (poorBox.isChecked()) {
                        selectedInspection.put(subItemPOJO.getId(), POOR);

                        //add to adapterList
                    } else {
                        selectedInspection.remove(subItemPOJO.getId());
                        //remove from adapterList
                    }
                    avgBox.setChecked(false);
                    goodBox.setChecked(false);
                    break;
                case R.id.cb_average:
                    if (avgBox.isChecked()) {

                        selectedInspection.put(subItemPOJO.getId(), AVERAGE);
                        //add to adapterList
                    } else {

                        selectedInspection.remove(subItemPOJO.getId());
                        //remove from adapterList
                    }
                    poorBox.setChecked(false);
                    goodBox.setChecked(false);
                    break;
                case R.id.cb_good:
                    if (goodBox.isChecked()) {
                        //add to adapterList
                        selectedInspection.put(subItemPOJO.getId(), GOOD);
                    } else {
                        selectedInspection.remove(subItemPOJO.getId());
                        //remove from adapterList
                    }
                    poorBox.setChecked(false);
                    avgBox.setChecked(false);
                    break;
            }
        }
    }
}

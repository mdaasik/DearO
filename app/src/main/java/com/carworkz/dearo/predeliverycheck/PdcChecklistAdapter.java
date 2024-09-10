package com.carworkz.dearo.predeliverycheck;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.carworkz.dearo.R;
import com.carworkz.dearo.customviews.toggle.interfaces.OnToggledListener;
import com.carworkz.dearo.customviews.toggle.model.ToggleableView;
import com.carworkz.dearo.customviews.toggle.widget.LabeledSwitch;
import com.carworkz.dearo.domain.entities.ChecklistItem;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

import java.util.List;

import timber.log.Timber;


public class PdcChecklistAdapter extends ExpandableRecyclerViewAdapter<PdcChecklistAdapter.InspectionItemViewHolder, PdcChecklistAdapter.InspectionSubItemViewHolder> {

    private static final String TAG = "PdcChecklistAdapter";
    private Context context;
    private CallBackListener listener;
    private boolean isPdcCompleted=false;
//    private List<ChecklistItemPOJO> checklist;
    public PdcChecklistAdapter(Context context, List< ? extends ExpandableGroup> groups, CallBackListener listener,boolean isPdcCompleted) {
        super(groups);
        this.context = context;
        this.listener = listener;
        this.isPdcCompleted=isPdcCompleted;
//        this.checklist= groups;
    }

    @Override
    public InspectionItemViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_inspection_item_title, parent, false);
        return new InspectionItemViewHolder(view);
    }

    @Override
    public InspectionSubItemViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_pdc_subitem, parent, false);
        return new InspectionSubItemViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(InspectionSubItemViewHolder holder, int flatPosition, ExpandableGroup group, int childIndex) {
        Timber.d(TAG, "onBindChildViewHolder: sub item init " + group.getTitle());
        final ChecklistItem checklistItem = (ChecklistItem) group.getItems().get(childIndex);
        holder.pdcTitle.setTag(checklistItem);

        holder.pdcTitle.setText(checklistItem.getName());
        holder.pdcChecked.setChecked(checklistItem.getChecked());
        holder.pdcDefectToggle.setEnabled(checklistItem.getChecked());
        holder.pdcRemark.setEnabled(checklistItem.getChecked());
        holder.pdcDefectToggle.setOn(checklistItem.getDefect());
        holder.pdcRemark.setText(checklistItem.getComments());
        //check if checkbox checked or not

        //make UI read only
        if(isPdcCompleted)
        {
            holder.pdcChecked.setEnabled(false);
            holder.pdcDefectToggle.setEnabled(false);
            holder.pdcRemark.setEnabled(false);
        }
    }

    @Override
    public void onBindGroupViewHolder(InspectionItemViewHolder holder, int flatPosition, ExpandableGroup group) {
        holder.setTitle(group.getTitle());
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

    @Override
    public boolean toggleGroup(int flatPos) {
        notifyItemChanged(flatPos);
        return super.toggleGroup(flatPos);
    }

    public List<ChecklistItemPOJO> getChecklist() {
        return (List<ChecklistItemPOJO>) getGroups();
    }

    public class InspectionSubItemViewHolder extends ChildViewHolder implements View.OnClickListener, OnToggledListener {
        TextView pdcTitle;
        private CheckBox pdcChecked;
        private LabeledSwitch pdcDefectToggle;
        private EditText pdcRemark;

        InspectionSubItemViewHolder(View itemView) {
            super(itemView);
            pdcTitle = itemView.findViewById(R.id.pdcTitle);
            pdcChecked = itemView.findViewById(R.id.pdcChecked);
            pdcDefectToggle = itemView.findViewById(R.id.pdcDefectToggle);
            pdcRemark = itemView.findViewById(R.id.pdcRemark);
            pdcChecked.setOnClickListener(this);
            pdcDefectToggle.setOnToggledListener(this);
            pdcDefectToggle.setEnabled(false);
            pdcRemark.setEnabled(false);
            pdcRemark.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    ChecklistItem checklistItem = (ChecklistItem) pdcTitle.getTag();
                    checklistItem.setComments(s.toString());
                    listener.dataChanged(true);
                }
            });
        }

        @Override
        public void onClick(View view) {
            ChecklistItem checklistItem = (ChecklistItem) pdcTitle.getTag();
            switch (view.getId()) {
                case R.id.pdcChecked:
                    //data changed
                    checklistItem.setChecked(pdcChecked.isChecked());
                    pdcDefectToggle.setEnabled(pdcChecked.isChecked());
                    pdcRemark.setEnabled(pdcChecked.isChecked());
                    listener.dataChanged(true);
                    break;
            }
        }

        @Override
        public void onSwitched(ToggleableView toggleableView, boolean isOn) {
            ChecklistItem checklistItem = (ChecklistItem) pdcTitle.getTag();
            checklistItem.setDefect(isOn);
            listener.dataChanged(true);
        }
    }

    public interface CallBackListener
    {
        void dataChanged( Boolean isChanged);
    }
}

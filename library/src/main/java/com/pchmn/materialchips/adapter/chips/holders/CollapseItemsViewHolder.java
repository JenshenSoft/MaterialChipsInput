package com.pchmn.materialchips.adapter.chips.holders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pchmn.materialchips.R;
import com.pchmn.materialchips.adapter.chips.items.CollapseButtonItem;
import com.pchmn.materialchips.model.ChipInterface;

import java.util.List;

public class CollapseItemsViewHolder extends RecyclerView.ViewHolder {

    final TextView collapseChipsView;
    private final Context context;

    public CollapseItemsViewHolder(Context context, View view) {
        super(view);
        this.context = context;
        collapseChipsView = (TextView) itemView.findViewById(R.id.text);
    }

    public void bind(CollapseButtonItem item, View.OnClickListener onClickListener) {
        collapseChipsView.setOnClickListener(onClickListener);
        List<ChipInterface> items = item.getItems();
        if (item.isCollapsed() && items != null && !items.isEmpty()) {
            int size = items.size();
            collapseChipsView.setText(context.getString(R.string.show_more, size));
        } else {
            collapseChipsView.setText(context.getString(R.string.show_less));
        }
    }
}
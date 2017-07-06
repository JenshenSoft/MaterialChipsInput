package com.pchmn.materialchips.adapter.chips;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pchmn.materialchips.R;
import com.pchmn.materialchips.adapter.chips.items.CollapseButtonItem;

public class CollapseItemsViewHolder extends RecyclerView.ViewHolder {

    final TextView collapseChipsView;
    private final Context context;

    CollapseItemsViewHolder(Context context, View view) {
        super(view);
        this.context = context;
        collapseChipsView = (TextView) itemView.findViewById(R.id.text);
    }

    void bind(CollapseButtonItem item, View.OnClickListener onClickListener) {
        collapseChipsView.setOnClickListener(onClickListener);
        if (item.isCollapsed()) {
            collapseChipsView.setText(context.getString(R.string.show_more, item.getItems().size()));
        } else {
            collapseChipsView.setText(context.getString(R.string.show_less));
        }
    }
}
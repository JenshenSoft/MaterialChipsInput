package com.pchmn.materialchips.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.pchmn.materialchips.R;
import com.pchmn.materialchips.model.ChipInterface;

import java.util.List;

public class CollapseItemsViewHolder extends RecyclerView.ViewHolder {

    final TextView collapseChipsView;
    private final Context context;

    CollapseItemsViewHolder(Context context, View view) {
        super(view);
        this.context = context;
        collapseChipsView = (TextView) itemView.findViewById(R.id.text);
    }

    void bind(boolean collapse, List<ChipInterface> chipInterfaces, View.OnClickListener onClickListener) {
        collapseChipsView.setOnClickListener(onClickListener);
        if (collapse) {
            collapseChipsView.setText(context.getString(R.string.show_more, chipInterfaces.size() - 1));
        } else {
            collapseChipsView.setText(context.getString(R.string.show_less));
        }
    }
}
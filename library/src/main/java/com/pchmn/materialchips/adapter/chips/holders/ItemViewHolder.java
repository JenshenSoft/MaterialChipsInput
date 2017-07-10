package com.pchmn.materialchips.adapter.chips.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.pchmn.materialchips.ChipView;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    public final ChipView chipView;

    public ItemViewHolder(View view) {
        super(view);
        chipView = (ChipView) view;
    }
}
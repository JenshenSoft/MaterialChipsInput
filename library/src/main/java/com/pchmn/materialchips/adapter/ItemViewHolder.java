package com.pchmn.materialchips.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.pchmn.materialchips.ChipView;

public class ItemViewHolder extends RecyclerView.ViewHolder {

    final ChipView chipView;

    public ItemViewHolder(View view) {
        super(view);
        chipView = (ChipView) view;
    }
}